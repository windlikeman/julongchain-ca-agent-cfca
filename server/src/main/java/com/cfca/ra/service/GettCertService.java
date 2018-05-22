package com.cfca.ra.service;

import com.cfca.ra.RAServer;
import com.cfca.ra.RAServerException;
import com.cfca.ra.beans.GettCertRequestNet;
import com.cfca.ra.beans.GettCertResponseNet;
import com.cfca.ra.beans.ServerResponseError;
import com.cfca.ra.ca.CA;
import org.bouncycastle.util.encoders.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.PublicKey;
import java.security.Signature;
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

    GettCertService(RAServer raServer) {
        server = raServer;
    }

    public GettCertResponseNet gettcert(GettCertRequestNet data, String token) {
        try {
            logger.info("gettcert Entered");
            String id = "admin";
            verifyToken(data.getCaname(), id, token);
            GettCertResponseNet resp = new GettCertResponseNet(true, null);
            final CA ca = server.getCA(data.getCaname());
            ca.fillGettcertInfo(resp);
            return resp;
        }catch (RAServerException e){
            logger.error("gettcert >>>>>>Failure : " + e.getMessage());
            return buildGettcertErrorServerResponse(e);
        }
    }

    private void verifyToken(String caName, String id, String token) throws RAServerException{
        logger.info("verifyToken Entered>>>>>>id : " + id + ",caName=" + caName + ",token=" + token);
        String enrollmentId = server.getEnrollmentId(caName, id);
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
                throw new RAServerException(RAServerException.REASON_CODE_GETTCERT_SERVICE_INVALID_TOKEN, "expected:<cert.sig>,but invalid auth:" + auth);
            }
            final String b64Cert = split[0];
            final String b64Sig = split[1];
            final byte[] cert = Base64.decode(b64Cert);
            if (logger.isInfoEnabled()) {
                logger.info("createToken>>>>>>publicKey : " + publicKey);
            }
            Signature signature = Signature.getInstance("SM3withSM2", "BC");
            signature.initVerify(publicKey);
            signature.update(cert);

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
}
