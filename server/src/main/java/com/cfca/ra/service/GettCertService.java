package com.cfca.ra.service;

import com.cfca.ra.RAServer;
import com.cfca.ra.RAServerException;
import com.cfca.ra.beans.ServerResponseError;
import com.cfca.ra.ca.Attribute;
import com.cfca.ra.ca.TcertKeyTree;
import com.cfca.ra.ca.TcertManager;
import com.cfca.ra.client.RAClientImpl;
import com.cfca.ra.enroll.EnrollmentRequestNet;
import com.cfca.ra.gettcert.GettCertRequest;
import com.cfca.ra.gettcert.GettCertRequestNet;
import com.cfca.ra.gettcert.GettCertResponse;
import com.cfca.ra.gettcert.GettCertResponseNet;
import com.cfca.ra.register.IUser;
import com.cfca.ra.repository.IMessageStore;
import com.cfca.ra.repository.MessageStore;
import org.apache.commons.lang.StringUtils;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveGenParameterSpec;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;
import org.bouncycastle.util.encoders.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.*;
import java.security.spec.AlgorithmParameterSpec;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangchong
 * @create 2018/5/22
 * @Description 获取交易证书的服务
 * @CodeReviewer
 * @since v3.0.0
 */
public class GettCertService {
    private static final Logger logger = LoggerFactory.getLogger(GettCertService.class);

    private final RAServer server;
    private final RAClientImpl raClient;
    private final IMessageStore messageStore;
    private Certificate eCert;

    GettCertService(RAServer raServer) {
        this.server = raServer;
        this.raClient = new RAClientImpl();
        this.messageStore = MessageStore.GETTCERT_DEFAULT;
        MessageStore.GETTCERT_DEFAULT.setServerHomeDir(this.server.getServerHomeDir());
    }

    private String getEnrollmentIdFromAuth(String auth) throws RAServerException {
        final String[] split = auth.split("\\.");
        if (split.length != 2) {
            throw new RAServerException(RAServerException.REASON_CODE_GETTCERT_SERVICE_INVALID_TOKEN, "expected:<enrollmentId.sig>,but invalid auth:" + auth);
        }
        final String b64EnrollmentId = split[0];

        return new String(Base64.decode(b64EnrollmentId));
    }

    public GettCertResponseNet gettcert(GettCertRequestNet data, String auth, BouncyCastleProvider provider) {
        try {
            logger.info("gettcert Entered");
            final int messageId = data.hashCode();
            if (messageStore.containsMessage(messageId)){
                throw new RAServerException(RAServerException.REASON_CODE_GETTCERT_SERVICE_MESSAGE_DUPLICATE,"messageId[" + messageId + "] is duplicate");
            }

            final String caname = data.getCaname();
            String enrollmentID = getEnrollmentIdFromAuth(auth);
            eCert = server.getEnrollmentCert(caname, enrollmentID);
            verifyToken(caname, enrollmentID, auth);
            GettCertResponseNet resp = new GettCertResponseNet(true, null);

            IUser caller = server.getUser(caname,enrollmentID, null);

            List<Attribute> attrs = caller.getAttributes(data.getAttrNames());
            List<String> affiliationPath = caller.getAffiliationPath();

            final TcertKeyTree tcertKeyTree = server.getTcertKeyTree(caname);
            Key prekey = tcertKeyTree.getKey(affiliationPath);
            String prekeyStr = new String(prekey.getEncoded());
            final GettCertRequest tcertReq = new GettCertRequest.Builder(attrs, true, caname, data.getCount(), prekeyStr).build();
            final TcertManager tcertMgr = server.getTcertMgr(caname);
            final GettCertResponse tcertResponse = tcertMgr.getBatch(tcertReq, eCert, provider);
            server.fillGettcertInfo(caname, resp, tcertResponse);

            updateMessageId(messageId, tcertReq);
            return resp;
        } catch (RAServerException e) {
            logger.error("gettcert >>>>>>Failure : " + e.getMessage());
            return buildGettcertErrorServerResponse(e);
        }
    }

    private void updateMessageId(int messageId, GettCertRequest data) throws RAServerException {
        messageStore.updateMessage(messageId, data);
    }

    private EnrollmentRequestNet buildEnrollmentRequestNet(GettCertRequestNet data, BouncyCastleProvider provider) throws RAServerException {
        final AlgorithmParameterSpec sm2p256v1 = new ECNamedCurveGenParameterSpec("sm2p256v1");
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("EC", provider);
            generator.initialize(sm2p256v1);
            KeyPair keypair = generator.generateKeyPair();
            logger.info("buildEnrollmentRequestNet>>>>>>getSM2CsrResult@publicKey : " + keypair.getPublic());
            logger.info("buildEnrollmentRequestNet>>>>>>getSM2CsrResult@privateKey : " + keypair.getPrivate());
            String distinctName = generateDistinctName();
            String csr = genSM2CSR(distinctName, keypair);
            final String profile = "H09358028";
            final String label = "";
            final String caname = data.getCaname();
            return new EnrollmentRequestNet(csr, profile, label, caname);
        } catch (Exception e) {
            throw new RAServerException(RAServerException.REASON_CODE_GETTCERT_SERVICE_GEN_CSR, e);
        }
    }


    private String genSM2CSR(String distictName, KeyPair keypair) throws RAServerException {

        try {
            if (StringUtils.isEmpty(distictName)) {
                throw new RAServerException(RAServerException.REASON_CODE_GETTCERT_SERVICE_GEN_CSR, "distictName is empty");
            }

            PKCS10CertificationRequestBuilder pkcs10Builder = new JcaPKCS10CertificationRequestBuilder(
                    new X500Name(distictName),
                    keypair.getPublic());

            ContentSigner contentSigner = new JcaContentSignerBuilder("SM3WITHSM2").setProvider("BC").build(keypair.getPrivate());
            PKCS10CertificationRequest csr = pkcs10Builder.build(contentSigner);
            final byte[] base64Encoded = Base64.encode(csr.getEncoded());
            return new String(base64Encoded);
        } catch (RAServerException e) {
            throw e;
        } catch (Exception e) {
            throw new RAServerException(RAServerException.REASON_CODE_GETTCERT_SERVICE_GEN_CSR, e);
        }
    }

    private void verifyToken(String caName, String id, String token) throws RAServerException {
        logger.info("verifyToken Entered>>>>>>id : " + id + ",caName=" + caName + ",token=" + token);
        String enrollmentId = getEnrollmentIdFromAuth(token);
        PublicKey publicKey = server.getKey(caName, enrollmentId);
        if (publicKey == null) {
            throw new RAServerException(RAServerException.REASON_CODE_GETTCERT_SERVICE_NOT_ENROLL, "This user not enroll first. Please execute enroll command first.");
        }

        verify(token, publicKey);
    }

    private void verify(String auth, PublicKey publicKey) throws RAServerException {
        try {
            final String[] split = auth.split("\\.");
            if (split.length != 2) {
                throw new RAServerException(RAServerException.REASON_CODE_GETTCERT_SERVICE_INVALID_TOKEN, "expected:<b64EnrollmentId.sig>,but invalid auth:" + auth);
            }
            final String b64EnrollmentId = split[0];
            final String b64Sig = split[1];
            final byte[] enrollmentId = Base64.decode(b64EnrollmentId);
            if (logger.isInfoEnabled()) {
                logger.info("verify>>>>>>enrollmentId : " + new String(enrollmentId));
                logger.info("verify>>>>>>publicKey    : " + publicKey);
            }
            Signature signature = Signature.getInstance("SM3withSM2", "BC");
            signature.initVerify(publicKey);
            signature.update(enrollmentId);

            final byte[] sign = Base64.decode(b64Sig);
            final boolean verify = signature.verify(sign);
            if (!verify) {
                throw new RAServerException(RAServerException.REASON_CODE_GETTCERT_SERVICE_VERIFY_TOKEN);
            }
        } catch (Exception e) {
            throw new RAServerException(RAServerException.REASON_CODE_GETTCERT_SERVICE_VERIFY_TOKEN, e);
        }
    }

    private GettCertResponseNet buildGettcertErrorServerResponse(RAServerException e) {
        List<ServerResponseError> errors = new ArrayList<>();
        ServerResponseError elem = new ServerResponseError(e.getReasonCode(), e.getMessage());
        errors.add(elem);
        return new GettCertResponseNet(false, errors);
    }

    private String generateDistinctName() {
        return "CN=051@testName@Z1234567890@53,OU=Individual-3,OU=Local RA,O=CFCA TEST CA,C=CN";
    }
}
