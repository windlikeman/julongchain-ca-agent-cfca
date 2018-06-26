package com.cfca.ra;

import org.apache.commons.lang.StringUtils;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.PublicKey;
import java.security.Signature;

/**
 * @author zhangchong
 * @create 2018/6/4
 * @Description 用户个体实例
 * @CodeReviewer helonglong
 * @since v3.0.0
 */
public class Identity {

    private static final Logger logger = LoggerFactory.getLogger(Identity.class);

    private final RAServer server;
    private final String caName;
    private final String auth;

    private static final int AUTH_ELEMENT_NUM = 2;

    public Identity(RAServer server, String caName, String auth) {
        this.server = server;
        this.caName = caName;
        this.auth = auth;
    }

    private class AuthPair {
        private final String enrollmentId;
        private final String b64Sig;

        private AuthPair(String enrollmentId, String b64Sig) {
            this.b64Sig = b64Sig;
            this.enrollmentId = enrollmentId;
        }

        private String getB64Sig() {
            return b64Sig;
        }

        private String getEnrollmentId() {
            return enrollmentId;
        }
    }

    public void verify(byte[] body) throws RAServerException {
        final AuthPair authPair = getAuthPairFromAuth(auth);
        final String enrollmentId = authPair.getEnrollmentId();
        PublicKey publicKey = server.getKey(caName, enrollmentId);
        if (publicKey == null) {
            throw new RAServerException(RAServerException.REASON_CODE_IDENTITY_VERIFY_TOKEN, "This user not enroll first. Please execute enroll command first.");
        }
        final String b64Sig = authPair.getB64Sig();
        logger.info("verify>>>>>> enrollmentId  : {}", enrollmentId);
        logger.info("verify>>>>>> publicKey     : {}", publicKey);
        logger.info("verify>>>>>> b64Sig        : {}", b64Sig);
        logger.info("verify>>>>>> body          : {}", Hex.toHexString(body));
        verify(b64Sig, publicKey, body);
    }

    private void verify(String b64Sig, PublicKey publicKey, byte[] body) throws RAServerException {
        try {
            Signature signature = Signature.getInstance("SM3withSM2", "BC");
            signature.initVerify(publicKey);
            signature.update(body);

            final byte[] sign = Base64.decode(b64Sig);
            final boolean verify = signature.verify(sign);
            if (!verify) {
                throw new RAServerException(RAServerException.REASON_CODE_IDENTITY_VERIFY_TOKEN, "identity verify failed");
            }
        } catch (Exception e) {
            throw new RAServerException(RAServerException.REASON_CODE_IDENTITY_VERIFY_TOKEN, e);
        }
    }

    private AuthPair getAuthPairFromAuth(String auth) throws RAServerException {
        final String[] split = auth.split("\\.");
        if (split.length != AUTH_ELEMENT_NUM) {
            throw new RAServerException(RAServerException.REASON_CODE_IDENTITY_VERIFY_TOKEN, "failed to get enrollmentId from auth, expected:<enrollmentId.sig>,but invalid auth:" + auth);
        }
        final String b64EnrollmentId = split[0];
        if (StringUtils.isBlank(b64EnrollmentId)) {
            throw new RAServerException(RAServerException.REASON_CODE_IDENTITY_VERIFY_TOKEN, "failed to get enrollmentId from auth, expected:<enrollmentId.sig>,but b64EnrollmentId is blank, invalid auth:" + auth);
        }

        final String b64Sig = split[1];
        if (StringUtils.isBlank(b64Sig)) {
            throw new RAServerException(RAServerException.REASON_CODE_IDENTITY_VERIFY_TOKEN, "failed to get b64Sig from auth, expected:<enrollmentId.sig>,but b64Sig is blank, invalid auth:" + auth);
        }
        return new AuthPair(new String(Base64.decode(b64EnrollmentId)), b64Sig);
    }
}
