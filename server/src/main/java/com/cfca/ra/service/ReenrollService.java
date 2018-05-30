package com.cfca.ra.service;

import cfca.ra.common.vo.response.CertServiceResponseVO;
import cfca.ra.toolkit.RAClient;
import com.cfca.ra.RAServer;
import com.cfca.ra.RAServerException;
import com.cfca.ra.beans.ServerResponseError;
import com.cfca.ra.client.IRAClient;
import com.cfca.ra.client.RAClientImpl;
import com.cfca.ra.enroll.EnrollmentResponseNet;
import com.cfca.ra.reenroll.ReenrollmentRequest;
import com.cfca.ra.reenroll.ReenrollmentRequestNet;
import com.cfca.ra.repository.IMessageStore;
import com.cfca.ra.repository.MessageStore;
import com.cfca.ra.utils.CertUtils;
import org.bouncycastle.util.encoders.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.PublicKey;
import java.security.Signature;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangchong
 * @create 2018/5/18
 * @Description 重新签发证书的服务
 * @CodeReviewer
 * @since v3.0.0
 */
@Service
public class ReenrollService {
    private static final Logger logger = LoggerFactory.getLogger(ReenrollService.class);
    private final RAServer server;

    private final IRAClient raClient;

    private final IMessageStore messageStore;

    @Autowired
    public ReenrollService(RAServer server) {
        this.server = server;
        this.raClient = new RAClientImpl();
        this.messageStore = MessageStore.REENROLL_DEFAULT;
        MessageStore.REENROLL_DEFAULT.setServerHomeDir(server.getServerHomeDir());
    }

    private void verifyTokenByEnrollmentId(String caName, String enrollmentId, String auth) throws RAServerException {
        PublicKey publicKey = server.getKey(caName, enrollmentId);
        if (publicKey == null) {
            throw new RAServerException(RAServerException.REASON_CODE_REENROLL_SERVICE_NOT_ENROLL, "not found the pubkey file, this user may not enroll first. Please execute enroll command first.");
        }

        verify(auth, publicKey);
    }

    private void verify(String auth, PublicKey publicKey) throws RAServerException {
        try {
            final String[] split = auth.split("\\.");
            if (split.length != 2) {
                throw new RAServerException(RAServerException.REASON_CODE_REENROLL_SERVICE_INVALID_TOKEN, "expected:<b64EnrollmentId.b64sig>,but invalid auth:" + auth);
            }
            final String b64EnrollmentId = split[0];
            final String b64Sig = split[1];
            final byte[] enrollmentId = Base64.decode(b64EnrollmentId);
            if (logger.isInfoEnabled()) {
                logger.info("verify>>>>>>publicKey    : " + publicKey);
                logger.info("verify>>>>>>enrollmentId : " + new String(enrollmentId));
            }
            Signature signature = Signature.getInstance("SM3withSM2", "BC");
            signature.initVerify(publicKey);
            signature.update(enrollmentId);

            final byte[] sign = Base64.decode(b64Sig);
            final boolean verify = signature.verify(sign);
            if (!verify) {
                throw new RAServerException(RAServerException.REASON_CODE_REENROLL_SERVICE_VERIFY_TOKEN);
            }
        } catch (Exception e) {
            throw new RAServerException(RAServerException.REASON_CODE_REENROLL_SERVICE_VERIFY_TOKEN, e);
        }
    }

    public EnrollmentResponseNet reenroll(ReenrollmentRequest request, String auth) {
        try {
            final ReenrollmentRequestNet data = request.getReenrollmentRequestNet();
            final int messageId = data.hashCode();
            if (messageStore.containsMessage(messageId)) {
                throw new RAServerException(RAServerException.REASON_CODE_REENROLL_SERVICE_MESSAGE_DUPLICATE, "messageId[" + messageId + "] is duplicate");
            }
            final String caname = data.getCaname();
            String enrollmentId = getEnrollmentIdFromAuth(auth);
            verifyTokenByEnrollmentId(caname, enrollmentId, auth);

            final CertServiceResponseVO reenrollResponseFromRA = raClient.reenroll(data);

            final String resultCode = reenrollResponseFromRA.getResultCode();
            final String resultMessage = reenrollResponseFromRA.getResultMessage();
            logger.info(resultCode);
            logger.info(resultMessage);
            EnrollmentResponseNet response;
            switch (resultCode) {
                case RAClient.SUCCESS:
                    logger.info(reenrollResponseFromRA.toString());
                    String b64cert = reenrollResponseFromRA.getSignatureCert();
                    response = new EnrollmentResponseNet(true, b64cert, null, null);

                    enrollmentId = CertUtils.getSubjectName(b64cert);

                    server.fillCAInfo(caname, response, enrollmentId);
                    //FIXME:enrollmentID
                    server.storeCert(caname, enrollmentId, b64cert);
                    break;
                default:
                    List<ServerResponseError> errors = new ArrayList<>();
                    ServerResponseError error = new ServerResponseError(1002, resultMessage);
                    errors.add(error);
                    response = new EnrollmentResponseNet(false, null, errors, null);
                    break;
            }
            updateMessage(messageId, request);
            return response;
        } catch (RAServerException e) {
            logger.error("reenroll >>>>>> Failure : " + e.getMessage(), e);
            return buildErrorServerResponse(e);
        }
    }

    private String getEnrollmentIdFromAuth(String auth) throws RAServerException {
        final String[] split = auth.split("\\.");
        if (split.length != 2) {
            throw new RAServerException(RAServerException.REASON_CODE_REENROLL_SERVICE_INVALID_TOKEN, "expected:<enrollmentId.sig>,but invalid auth:" + auth);
        }
        final String b64EnrollmentId = split[0];

        return new String(Base64.decode(b64EnrollmentId));
    }

    private void updateMessage(int messageId, ReenrollmentRequest request) throws RAServerException {
        messageStore.updateMessage(messageId, request);
    }

    private EnrollmentResponseNet buildErrorServerResponse(RAServerException e) {
        List<ServerResponseError> errors = new ArrayList<>();
        ServerResponseError elem = new ServerResponseError(e.getReasonCode(), e.getMessage());
        errors.add(elem);
        return new EnrollmentResponseNet(false, null, errors, null);
    }
}
