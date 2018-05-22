package com.cfca.ra.service;

import cfca.ra.common.vo.response.CertServiceResponseVO;
import cfca.ra.toolkit.RAClient;
import com.cfca.ra.RAServer;
import com.cfca.ra.RAServerException;
import com.cfca.ra.ServerRequestContext;
import com.cfca.ra.beans.AuthInfo;
import com.cfca.ra.beans.EnrollmentRequestNet;
import com.cfca.ra.beans.EnrollmentResponseNet;
import com.cfca.ra.beans.ServerResponseError;
import com.cfca.ra.ca.CA;
import com.cfca.ra.client.IRAClient;
import com.cfca.ra.client.RAClientImpl;
import org.apache.commons.lang3.StringUtils;
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
 * @CodeReviewer
 * @since v3.0.0
 */
@Service
public class EnrollService {
    private static final Logger logger = LoggerFactory.getLogger(EnrollService.class);
    private final RAServer server;

    private final IRAClient raClient;

    @Autowired
    public EnrollService(RAServer server) {
        this.server = server;
        this.raClient = new RAClientImpl();
    }

    public EnrollmentResponseNet enroll(EnrollmentRequestNet data, String auth) {
        try {
            final String caName = data.getCaname();
            final ServerRequestContext serverRequestContext = buildServerRequestContext(caName, auth);
            final CertServiceResponseVO enrollResponseFromRA = raClient.enroll(data, serverRequestContext);

            final String resultCode = enrollResponseFromRA.getResultCode();
            final String resultMessage = enrollResponseFromRA.getResultMessage();
            logger.info(resultCode);
            logger.info(resultMessage);
            EnrollmentResponseNet response;
            switch (resultCode) {
                case RAClient.SUCCESS:
                    logger.info(enrollResponseFromRA.toString());
                    final CA ca = getCA(caName, serverRequestContext);
                    String b64cert = enrollResponseFromRA.getSignatureCert();
                    response = new EnrollmentResponseNet(true, b64cert, null, null);
                    ca.fillCAInfo(response);
                    String name = serverRequestContext.getEnrollmentID();
                    server.storeCert(caName, name, b64cert);
                    ca.updateEnrollIdStore(name, name);
                    break;
                default:
                    List<ServerResponseError> errors = new ArrayList<>();
                    ServerResponseError error = new ServerResponseError(1002, resultMessage);
                    errors.add(error);
                    response = new EnrollmentResponseNet(false, null, errors, null);
                    break;
            }

            return response;
        } catch (RAServerException e) {
            logger.error("enroll >>>>>> Failure : " + e.getMessage(), e);
            return buildErrorServerResponse(e);
        }
    }

    private CA getCA(String caName, ServerRequestContext serverRequestContext) throws RAServerException {
        if (StringUtils.isEmpty(caName)) {
            throw new RAServerException(RAServerException.REASON_CODE_RA_SERVER_GET_CA_NAME_EMPTY, "ca name is empty");
        }
        return serverRequestContext.getServer().getCA(caName);
    }

    private EnrollmentResponseNet buildErrorServerResponse(RAServerException e) {
        List<ServerResponseError> errors = new ArrayList<>();
        ServerResponseError elem = new ServerResponseError(e.getReasonCode(), e.getMessage());
        errors.add(elem);
        return new EnrollmentResponseNet(false, null, errors, null);
    }

    private ServerRequestContext buildServerRequestContext(String caName, String auth) throws RAServerException {
        AuthInfo authInfo = getUserNameFromAuth(auth);
        checkAuth(caName, authInfo);

        ServerRequestContext.Builder builder = new ServerRequestContext.Builder();
        builder.enrollmentID(authInfo.getUser());
        builder.server(server);
        return builder.build();
    }

    private void checkAuth(String caName, AuthInfo authInfo) throws RAServerException {
        if (authInfo == null || StringUtils.isEmpty(authInfo.getUser())) {
            logger.info("checkAuth<<<<<<Finished:result is: false=>userName is empty");
            throw new RAServerException(RAServerException.REASON_CODE_ENROLL_SERVICE_CHECK_AUTH, "userName in auth info is empty");
        }

        final String userName = authInfo.getUser();
        String expected = server.getUserSecret(caName, userName);
        String auth = authInfo.getSecret();
        final boolean result = expected.equals(auth);
        logger.info("checkAuth<<<<<<Finished:result is: " + result);
        if (!result) {
            throw new RAServerException(RAServerException.REASON_CODE_ENROLL_SERVICE_CHECK_AUTH, "auth check failed");
        }
    }

    private String getAuth(String auth) {
        String remaining = "";
        if (!StringUtils.isEmpty(auth)) {
            final String prefix = "Basic ";
            final int basic = auth.indexOf(prefix);
            remaining = auth.substring(basic + prefix.length());
        }

        return remaining;
    }

    private AuthInfo getUserNameFromAuth(String auth) throws RAServerException {
        String remaining = getAuth(auth);
        if (StringUtils.isEmpty(remaining)) {
            logger.error("checkAuth<<<<<<remaining is empty");
            throw new RAServerException(RAServerException.REASON_CODE_ENROLL_SERVICE_READ_AUTH);
        }

        final byte[] decode = Base64.decode(remaining);
        String userInfo = "";
        try {
            userInfo = new String(decode, "UTF-8");
            logger.info("checkAuth<<<<<<decoded auth is: " + userInfo);
        } catch (UnsupportedEncodingException e) {
            logger.error("checkAuth<<<<<<new String UTF-8 error:" + e.getMessage(), e);
            throw new RAServerException(RAServerException.REASON_CODE_ENROLL_SERVICE_READ_AUTH, "failed to  check auth : fail to new String", e);
        }

        if (StringUtils.isEmpty(userInfo)) {
            logger.info("checkAuth<<<<<<userInfo is empty");
            throw new RAServerException(RAServerException.REASON_CODE_ENROLL_SERVICE_READ_AUTH);
        }
        final String[] usernameAndPwd = userInfo.split(":");
        if (2 != usernameAndPwd.length) {
            logger.error("usernameAndPwd expected[username:password], but now split length=" + usernameAndPwd.length);
            throw new RAServerException(RAServerException.REASON_CODE_ENROLL_SERVICE_READ_AUTH, "failed to check auth:invalid auth");
        }
        return new AuthInfo(usernameAndPwd[0], remaining);
    }
}
