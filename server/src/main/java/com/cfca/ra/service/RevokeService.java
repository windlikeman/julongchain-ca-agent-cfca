package com.cfca.ra.service;

import com.cfca.ra.RAServer;
import com.cfca.ra.RAServerException;
import com.cfca.ra.beans.ServerResponseError;
import com.cfca.ra.client.IRAClient;
import com.cfca.ra.client.RAClientImpl;
import com.cfca.ra.register.IUser;
import com.cfca.ra.repository.IMessageStore;
import com.cfca.ra.repository.MessageStore;
import com.cfca.ra.revoke.RevokeRequest;
import com.cfca.ra.revoke.RevokeRequestNet;
import com.cfca.ra.revoke.RevokeResponseNet;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.lang.StringUtils;
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
 * @Description 吊销服务
 * @CodeReviewer
 * @since v3.0.0
 */
@Service
public class RevokeService {
    private static final Logger logger = LoggerFactory.getLogger(RAServiceImpl.class);
    private final RAServer server;
    private final IRAClient raClient;
    private final IMessageStore messageStore;

    @Autowired
    public RevokeService(RAServer server) {
        this.server = server;
        this.raClient = new RAClientImpl();
        this.messageStore = MessageStore.REVOKE_DEFAULT;
        MessageStore.REVOKE_DEFAULT.setServerHomeDir(server.getServerHomeDir());
    }

    public RevokeResponseNet revoke(RevokeRequest request, String auth) {
        try {
            logger.info("revoke >>>>>> request : " + request + ", auth=" + auth);
            final RevokeRequestNet data = request.getRevokeRequestNet();
            final int messageId = data.hashCode();
            if (messageStore.containsMessage(messageId)){
                throw new RAServerException(RAServerException.REASON_CODE_REVOKE_SERVICE_MESSAGE_DUPLICATE,"messageId[" + messageId + "] is duplicate");
            }

            final String caName = data.getCaname();
            final String id = data.getId();
            String enrollmentId = getEnrollmentIdFromToken(caName, id, auth);
            logger.info("revoke >>>>>> enrollmentId : " + enrollmentId);

            server.attributeIsTrue(caName, id, "hf.Revoker");
            if (StringUtils.isEmpty(data.getSerial()) && StringUtils.isEmpty(data.getAki()) && StringUtils.isEmpty(data.getId())) {
                throw new RAServerException(RAServerException.REASON_CODE_REVOKE_SERVICE_INVALID_REQUEST, "Either Name or Serial and AKI are required for a revoke request");
            }

            if (!StringUtils.isEmpty(data.getId())) {
                final IUser user = server.getUser(caName, data.getId(), null);
                if (user != null) {
                    user.revoke();
                }
            }

            final String result = raClient.revoke(data);
            removeCert(caName, data.getSerial());
            updateMessageId(messageId, request);
            return new RevokeResponseNet(true, result, null, null);
        } catch (RAServerException e) {
            logger.error("revoke >>>>>> Failure : " + e.getMessage(), e);
            return buildErrorResponse(e);
        }
    }

    private void updateMessageId(int messageId, RevokeRequest request) throws RAServerException {
        messageStore.updateMessage(messageId, request);
    }

    private void removeCert(String caName, String serial) throws RAServerException {
        File file = findCertFile(caName, serial);
        if (file != null && file.exists()) {
            final boolean delete = file.delete();
            if (!delete) {
                throw new RAServerException(RAServerException.REASON_CODE_REVOKE_SERVICE_REMOVE_CERT, "remove cert file[" + file.getAbsolutePath() + "] failed");
            }
        }
    }

    /**
     * 查找指定序列号的证书文件
     * @param caName
     * @param serial
     * @return
     * @throws RAServerException
     */
    private File findCertFile(String caName, String serial) throws RAServerException {
        final String certFilePath = server.findCertFile(caName, serial);
        if (StringUtils.isBlank(certFilePath)) {
            logger.warn("findCertFile<<<<<<Failure : ca[{}] not found cert file with serial[{}]", caName, serial);
            return null;
        }
        return new File(certFilePath);
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
