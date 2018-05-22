package com.cfca.ra.service;

import com.cfca.ra.RAServer;
import com.cfca.ra.RAServerException;
import com.cfca.ra.beans.RevokeRequestNet;
import com.cfca.ra.beans.RevokeResponseNet;
import com.cfca.ra.beans.ServerResponseError;
import com.cfca.ra.ca.CA;
import com.cfca.ra.ca.register.IUser;
import com.cfca.ra.client.IRAClient;
import com.cfca.ra.client.RAClientImpl;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.util.encoders.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.security.PublicKey;
import java.security.Signature;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangchong
 * @create 2018/5/18
 * @Description
 * @CodeReviewer
 * @since
 */
@Service
public class RevokeService {
    private static final Logger logger = LoggerFactory.getLogger(RAServiceImpl.class);
    private final RAServer server;
    private final IRAClient raClient;

    @Autowired
    public RevokeService(RAServer server) {
        this.server = server;
        this.raClient = new RAClientImpl();
    }

    public RevokeResponseNet revoke(RevokeRequestNet data, String auth) {
        try {
            logger.info("revoke >>>>>> data : " + data + ", auth=" + auth);
            final String caname = data.getCaname();
            final String id = data.getId();
            String enrollmentId = getEnrollmentIdFromToken(caname, id, auth);
            logger.info("revoke >>>>>> enrollmentId : " + enrollmentId);

            final CA ca = server.getCA(caname);
            ca.attributeIsTrue(id, "hf.Revoker");
            if (StringUtils.isEmpty(data.getSerial()) && StringUtils.isEmpty(data.getAki()) && StringUtils.isEmpty(data.getId())) {
                throw new RAServerException(RAServerException.REASON_CODE_REVOKE_SERVICE_INVALID_REQUEST, "Either Name or Serial and AKI are required for a revoke request");
            }

            if (!StringUtils.isEmpty(data.getId())) {
                final IUser user = ca.getRegistry().getUser(data.getId(), null);
                user.revoke();
            }

            final String result = raClient.revoke(data);
            removeCert(data.getSerial());
            return new RevokeResponseNet(true, result, null, null);
        } catch (RAServerException e) {
            logger.error("revoke >>>>>> Failure : " + e.getMessage(), e);
            return buildErrorResponse(e);
        }
    }

    private void removeCert(String serial) throws RAServerException {
        File file = findCertFile(serial);
        if (file != null && file.exists()) {
            final boolean delete = file.delete();
            if (!delete) {
                throw new RAServerException(RAServerException.REASON_CODE_REVOKE_SERVICE_REMOVE_CERT, "remove cert file[" + file.getAbsolutePath() + "] failed");
            }
        }

    }

    //FIXME
    private File findCertFile(String serial) {
        return null;
    }

    private RevokeResponseNet buildErrorResponse(RAServerException e) {
        List<ServerResponseError> errors = new ArrayList<>();
        ServerResponseError elem = new ServerResponseError(e.getReasonCode(), e.getMessage());
        errors.add(elem);
        return new RevokeResponseNet(false, null, errors, null);
    }

    private String getEnrollmentIdFromToken(String caName, String id, String auth) throws RAServerException {
        logger.info("getEnrollmentIdFromToken>>>>>>id : " + id + ",caName=" + caName + ",auth=" + auth);
        String enrollmentId = server.getEnrollmentId(caName, id);
        PublicKey publicKey = server.getKey(caName, enrollmentId);
        if (publicKey == null) {
            throw new RAServerException(RAServerException.REASON_CODE_REVOKE_SERVICE_NOT_ENROLL, "This user not enroll first. Please execute enroll command first.");
        }

        verify(auth, publicKey);
        return enrollmentId;
    }

    private void verify(String auth, PublicKey publicKey) throws RAServerException {
        try {
            final String[] split = auth.split("\\.");
            if (split.length != 2) {
                throw new RAServerException(RAServerException.REASON_CODE_REVOKE_SERVICE_INVALID_TOKEN, "expected:<cert.sig>,but invalid auth:" + auth);
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
                throw new RAServerException(RAServerException.REASON_CODE_REVOKE_SERVICE_VERIFY_TOKEN);
            }
        } catch (Exception e) {
            throw new RAServerException(RAServerException.REASON_CODE_REVOKE_SERVICE_VERIFY_TOKEN, e);
        }
    }
}
