package com.cfca.ra.command.internal;

import com.cfca.ra.command.CommandException;
import com.cfca.ra.command.internal.enroll.EnrollmentResponse;
import com.cfca.ra.command.internal.gettcert.GettCertRequest;
import com.cfca.ra.command.internal.gettcert.GettCertResponse;
import com.cfca.ra.command.internal.reenroll.ReenrollmentRequest;
import com.cfca.ra.command.internal.register.RegistrationRequest;
import com.cfca.ra.command.internal.register.RegistrationResponse;
import com.cfca.ra.command.internal.revoke.RevokeRequest;
import com.cfca.ra.command.internal.revoke.RevokeResponse;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.*;

/**
 * @author zhangchong
 * @create 2018/5/11
 * @Description 每一个用户实体, 标识每一个用户, 使用用户的名称, 签名私钥和公钥证书 以及内部客户端来标识
 * @CodeReviewer
 * @since v3.0.0
 */
public class Identity {

    private static final Logger logger = LoggerFactory.getLogger(Identity.class);
    private final String name;
    private Signer ecert;
    private final Client client;
    private final boolean needToVerify = true;

    Identity(String name, Signer ecert, Client client) {
        this.name = name;
        this.ecert = ecert;
        this.client = client;
    }

    public void setEcert(Signer ecert) {
        this.ecert = ecert;
    }

    public String getName() {
        return name;
    }

    public Signer getEcert() {
        return ecert;
    }

    public Client getClient() {
        return client;
    }

    public void store() throws CommandException {
        client.storeMyIdentity(ecert.getCert());
    }

    public EnrollmentResponse reenroll(ReenrollmentRequest request) throws CommandException {
        return client.reenroll(request, name, this);
    }

    /**
     * 根据ecert的cert和key 算出token
     *
     * @param body
     * @return
     */
    public String addTokenAuthHdr(byte[] body) throws CommandException {
        logger.info("addTokenAuthHdr<<<<<<Adding token-based authorization header");
        byte[] cert = ecert.getCert();
        PrivateKey key = ecert.getKey();
        return createToken(cert, key, body);
    }

    /**
     * @param cert       经过B64解码后的证书字节数组
     * @param privateKey 私钥
     * @param body
     * @return
     */
    private String createToken(byte[] cert, PrivateKey privateKey, byte[] body) throws CommandException {
        try {
            byte[] newCert = new byte[cert.length];
            System.arraycopy(cert, 0, newCert, 0, cert.length);

            final ASN1Primitive asn1Primitive = ASN1Primitive.fromByteArray(newCert);
            final Certificate certificate = Certificate.getInstance(asn1Primitive);
            final SubjectPublicKeyInfo subjectPublicKeyInfo = certificate.getSubjectPublicKeyInfo();
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
            PublicKey publicKey = converter.getPublicKey(subjectPublicKeyInfo);
            logger.info("createToken<<<<<<publicKey  : {}", publicKey);
            logger.info("createToken<<<<<<privateKey : {}", privateKey);

            final byte[] enrollmentIdBytes = name.getBytes();
            String b64EnrollmentId = Base64.toBase64String(enrollmentIdBytes);

            Signature signature = Signature.getInstance("SM3withSM2", "BC");
            signature.initSign(privateKey);
            signature.update(body);
            final byte[] sign = signature.sign();
            String b64Sig = new String(Base64.encode(sign));

            final String token = b64EnrollmentId + "." + b64Sig;
            logger.info("createToken<<<<<<token : {}", token);
            logger.info("createToken<<<<<<body  : {}", Hex.toHexString(body));

            if (needToVerify) {
                verifyAfterSign(body, publicKey, sign);
            }
            return token;
        } catch (Exception e) {
            throw new CommandException(CommandException.REASON_CODE_IDENTITY_CREATE_TOKEN, e);
        }
    }

    private void verifyAfterSign(byte[] body, PublicKey publicKey, byte[] sign) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException, CommandException {
        Signature verifySignature = Signature.getInstance("SM3withSM2", "BC");
        verifySignature.initVerify(publicKey);
        verifySignature.update(body);
        final boolean verify = verifySignature.verify(sign);
        if (!verify) {
            throw new CommandException(CommandException.REASON_CODE_IDENTITY_CREATE_TOKEN, "verify failed due to public and private keys do not match");
        }
    }

    public RegistrationResponse register(RegistrationRequest registrationRequest) throws CommandException {
        return client.register(registrationRequest, this);
    }

    public RevokeResponse revoke(RevokeRequest revokeRequest) throws CommandException {
        return client.revoke(revokeRequest, this);
    }

    public GettCertResponse gettcert(GettCertRequest gettCertRequest) throws CommandException {
        return client.gettcert(gettCertRequest, this);
    }

    @Override
    public String toString() {
        return "Identity{" +
                "name='" + name + '\'' +
                ", ecert=" + ecert +
                ", client=" + client +
                '}';
    }
}
