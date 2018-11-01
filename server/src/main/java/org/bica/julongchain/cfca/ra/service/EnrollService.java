package org.bica.julongchain.cfca.ra.service;

import cfca.ra.common.vo.response.CertServiceResponseVO;
import cfca.ra.toolkit.RAClient;
import org.apache.commons.lang.StringUtils;
import org.bica.julongchain.cfca.ra.RAServer;
import org.bica.julongchain.cfca.ra.RAServerException;
import org.bica.julongchain.cfca.ra.beans.AuthInfo;
import org.bica.julongchain.cfca.ra.beans.ServerResponseError;
import org.bica.julongchain.cfca.ra.client.IRAClient;
import org.bica.julongchain.cfca.ra.client.RAClientImpl;
import org.bica.julongchain.cfca.ra.enroll.EnrollmentRequest;
import org.bica.julongchain.cfca.ra.enroll.EnrollmentRequestNet;
import org.bica.julongchain.cfca.ra.enroll.EnrollmentResponseNet;
import org.bica.julongchain.cfca.ra.utils.CertUtils;
import org.bouncycastle.util.encoders.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangchong
 * @create 2018/5/18
 * @Description 签发证书的服务
 * @CodeReviewer helonglong
 * @since v3.0.0
 */
@Service
public class EnrollService {
    private static final Logger logger = LoggerFactory.getLogger(EnrollService.class);
    private static final int AUTH_ELEMENT_NUM = 2;
    private final RAServer server;

    private final IRAClient raClient;

    @Autowired
    public EnrollService(final RAServer server) {
        this.server = server;
        this.raClient = new RAClientImpl();
        final String serverHomeDir = server.getServerHomeDir();
        logger.info("EnrollService@init : serverHomeDir=" + serverHomeDir);
    }

    public EnrollmentResponseNet enroll(EnrollmentRequest request, String auth) {
        try {
            EnrollmentRequestNet data = request.getEnrollmentRequestNet();

            final String caName = data.getCaname();
            AuthInfo authInfo = getUserNameFromAuth(auth);
            checkAuth(caName, authInfo);

            //uniqueID=admin-nonce
            String uniqueID = String.format("%s-%s", authInfo.getUser(), authInfo.getSeqNo());
            logger.info("EnrollService@enroll : uniqueID={}", uniqueID);
            final CertServiceResponseVO enrollResponseFromRA = raClient.enroll(data, uniqueID);

            final String resultCode = enrollResponseFromRA.getResultCode();
            final String resultMessage = enrollResponseFromRA.getResultMessage();
            logger.info(resultCode + " , " + resultMessage);

            EnrollmentResponseNet response;
            switch (resultCode) {
                case RAClient.SUCCESS:
                    logger.info(enrollResponseFromRA.toString());
                    String b64cert = enrollResponseFromRA.getSignatureCert();
                    response = new EnrollmentResponseNet(true, b64cert, null, null);
                    String enrollmentID = CertUtils.getSubjectName(b64cert);
                    server.fillCAInfo(caName, response, enrollmentID);
                    final String serialNo = enrollResponseFromRA.getSerialNo();
                    server.storeCert(caName, enrollmentID, b64cert, serialNo);
                    break;
                default:
                    List<ServerResponseError> errors = new ArrayList<>();
                    ServerResponseError error = new ServerResponseError(resultMessage);
                    errors.add(error);
                    response = new EnrollmentResponseNet(false, null, errors, null);
                    break;
            }
            return response;
        } catch (RAServerException e) {
            logger.error("EnrollService@enroll >>>>>> Failure : " + e.getMessage(), e);
            return buildErrorServerResponse(e);
        } catch (Exception e) {
            logger.error("EnrollService@enroll >>>>>> Failure : " + e.getMessage(), e);
            return buildErrorServerResponse(e);
        }
    }

    private EnrollmentResponseNet buildErrorServerResponse(Exception e) {
        List<ServerResponseError> errors = new ArrayList<>();
        ServerResponseError elem = new ServerResponseError(e.getMessage());
        errors.add(elem);
        return new EnrollmentResponseNet(false, null, errors, null);
    }

    private EnrollmentResponseNet buildErrorServerResponse(RAServerException e) {
        List<ServerResponseError> errors = new ArrayList<>();
        ServerResponseError elem = new ServerResponseError(e.getMessage());
        errors.add(elem);
        return new EnrollmentResponseNet(false, null, errors, null);
    }

    private void checkAuth(String caName, AuthInfo authInfo) throws RAServerException {
        if (StringUtils.isEmpty(caName)) {
            logger.info("EnrollService@checkAuth<<<<<<Finished:result is: false=>caName is empty");
            throw new RAServerException("caName is empty");
        }

        if (authInfo == null || StringUtils.isEmpty(authInfo.getUser())) {
            logger.info("EnrollService@checkAuth<<<<<<Finished:result is: false=>userName is empty");
            throw new RAServerException("userName in auth info is empty");
        }

        final String userName = authInfo.getUser();
        String expected = server.getUserSecret(caName, userName);
        String auth = authInfo.getSecret();
        final boolean result = expected.equals(auth);
        logger.info("EnrollService@checkAuth<<<<<<Finished:result is: " + result);
        if (!result) {
            logger.error("EnrollService@checkAuth<<<<<<failed :expected={},actual={}", expected, auth);
            throw new RAServerException("auth check failed");
        }
    }

    private String getAuth(String auth) {
        final String prefix = "Basic ";
        String remaining = "";
        if (!StringUtils.isEmpty(auth) && auth.startsWith(prefix)) {
            final int basic = auth.indexOf(prefix);
            remaining = auth.substring(basic + prefix.length());
        }

        return remaining;
    }

    private AuthInfo getUserNameFromAuth(String auth) throws RAServerException {
        String remaining = getAuth(auth);
        if (StringUtils.isEmpty(remaining)) {
            logger.error("getUserNameFromAuth<<<<<<remaining is empty");
            throw new RAServerException("enrollment service failed to get auth");
        }

        final String[] authAndSeq = remaining.split(":", AUTH_ELEMENT_NUM);
        if (AUTH_ELEMENT_NUM != authAndSeq.length) {
            logger.error("authAndSeq expected[auth:seqNo], but now split length=" + authAndSeq.length);
            throw new RAServerException("getUserNameFromAuth:enrollment service failed to get UserName From Auth ");
        }
        remaining = authAndSeq[0];
        final String seqNo = authAndSeq[1];
        final byte[] decode = Base64.decode(remaining);
        String userInfo;
        try {
            userInfo = new String(decode, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            logger.error("getUserNameFromAuth<<<<<<new String UTF-8 error:" + e.getMessage(), e);
            throw new RAServerException("enrollment service failed to check auth : fail to new String", e);
        }

        if (StringUtils.isEmpty(userInfo)) {
            logger.info("getUserNameFromAuth<<<<<<userInfo is empty");
            throw new RAServerException("enrollment service failed to get auth");
        }
        final String[] usernameAndPwd = userInfo.split(":", AUTH_ELEMENT_NUM);
        if (AUTH_ELEMENT_NUM != usernameAndPwd.length) {
            logger.error("usernameAndPwd expected[username:password], but now split length=" +
                    usernameAndPwd.length);
            throw new RAServerException("enrollment service failed to check auth:invalid auth");
        }
        final String username = usernameAndPwd[0];
        return new AuthInfo(username, seqNo, remaining);
    }
}
