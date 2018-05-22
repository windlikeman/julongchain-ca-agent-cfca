package com.cfca.ra.service;

import com.cfca.ra.RAServer;
import com.cfca.ra.RAServerException;
import com.cfca.ra.beans.*;
import com.cfca.ra.ca.CA;
import com.cfca.ra.ca.IUserRegistry;
import com.cfca.ra.register.DefaultUser;
import com.cfca.ra.register.IUser;
import com.cfca.ra.register.UserAttrs;
import com.cfca.ra.register.UserInfo;
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
 * @Description 注册服务
 * @CodeReviewer
 * @since v3.0.0
 */
@Service
public class RegisterService {
    private static final Logger logger = LoggerFactory.getLogger(RAServiceImpl.class);
    private final RAServer server;

    @Autowired
    public RegisterService(RAServer server) {
        this.server = server;
    }

    enum AttributeNames {
        /**
         * 角色
         */
        Roles("hf.Registrar.Roles"),

        /**
         * 吊销角色
         */
        Revoker("hf.Revoker"),
        /**
         * 注册属性
         */
        RegistrarAttr("hf.Registrar.Attributes"),

        /**
         * 注册
         */
        EnrollmentID("hf.EnrollmentID"),

        /**
         * 类型
         */
        Type("hf.Type"),

        /**
         * 从属关系
         */
        Affiliation("hf.Affiliation");

        private final String name;

        AttributeNames(String s) {
            this.name = s;
        }

        public String getName() {
            return name;
        }
    }

    public RegistrationResponseNet registerUser(RegistrationRequestNet data, String auth) {
        try {
            final String caname = data.getCaname();
            final String id = data.getId();
            checkIdRegistered(caname, id);
            String enrollmentId = getEnrollmentIdFromToken(caname, id, auth);
            logger.info("registerUser >>>>>> enrollmentId : " + enrollmentId);

            final CA ca = server.getCA(caname);
//            IUser user = ca.getRegistry().getUser(enrollmentId, null);
            //FIXME: 将待注册权限磨平或者继承到注册者一致的权限 :
//            normalizeRegistrationRequest(data, user);
            //FIXME: 是否允许注册
//            canRegister(user, data);
            RegistrationRequest req = new RegistrationRequest(data);
            final UserInfo insert = new UserInfo(req, 1);
            final String secret = registerUserID(req, ca, insert);
            final RegistrationResponseNet registrationResponseNet = buildRegistrationResponseNet(secret);
            updateCallerStore(caname, enrollmentId, id);
            updateUserStore(caname, new DefaultUser(insert), secret);
            return registrationResponseNet;
        } catch (RAServerException e) {
            logger.error("registerUser >>>>>> Failure : " + e.getMessage(), e);
            return buildRegisterErrorResponse(e);
        }
    }

    private void canRegister(IUser user, RegistrationRequestNet data) throws RAServerException {

    }

    /**
     * 将调整待注册权限与注册者一致的权限
     *
     * @param data
     * @param user
     */
    private void normalizeRegistrationRequest(RegistrationRequestNet data, IUser user) {
    }

    private void checkIdRegistered(String caname, String id) throws RAServerException {
        server.checkIdRegistered(caname, id);
    }


    private RegistrationResponseNet buildRegisterErrorResponse(RAServerException e) {
        List<ServerResponseError> errors = new ArrayList<>();
        ServerResponseError elem = new ServerResponseError(e.getReasonCode(), e.getMessage());
        errors.add(elem);
        return new RegistrationResponseNet(false, null, errors, null);
    }

    private RegistrationResponseNet buildRegistrationResponseNet(String secret) {
        return new RegistrationResponseNet(true, new RegistrationResponseResult(secret), null, null);
    }

    private String registerUserID(RegistrationRequest req, CA ca, UserInfo insert) throws RAServerException {
        final int maxEnrollments = getMaxEnrollments(req.getMaxEnrollments(), ca.getConfig().getRegistry().getMaxEnrollments());
        req.setMaxEnrollments(maxEnrollments);
        addAttributeToRequest(AttributeNames.EnrollmentID, req.getName(), req.getAttribute());
        addAttributeToRequest(AttributeNames.Type, req.getType(), req.getAttribute());
        addAttributeToRequest(AttributeNames.Affiliation, req.getAffiliation(), req.getAttribute());

        IUserRegistry registry = ca.getRegistry();

        final IUser user = registry.getUser(req.getName(), null);
        if (user != null) {
            String message = String.format("Identity '%s' is already registered", req.getName());
            throw new RAServerException(RAServerException.REASON_CODE_REGISTER_SERVICE_ALREADY_REGISTERED, message);
        }

        registry.insertUser(insert);
        return req.getSecret();
    }

    private void addAttributeToRequest(AttributeNames attributeName, String value, List<UserAttrs> attribute) {
        attribute.add(new UserAttrs(attributeName.getName(), value));
    }

    private int getMaxEnrollments(int maxEnrollments, int maxEnrollments1) {
        return (maxEnrollments > maxEnrollments1) ? maxEnrollments1 : maxEnrollments;
    }

    private String getEnrollmentIdFromToken(String caName, String id, String auth) throws RAServerException {
        logger.info("getEnrollmentIdFromToken>>>>>>id : " + id + ",caName=" + caName + ",auth=" + auth);

        String enrollmentId = server.getEnrollmentId(caName, id);

        PublicKey publicKey = server.getKey(caName, enrollmentId);
        if (publicKey == null) {
            throw new RAServerException(RAServerException.REASON_CODE_REGISTER_SERVICE_NOT_ENROLL, "This user not enroll first. Please execute enroll command first.");
        }

        verify(auth, publicKey);
        return enrollmentId;
    }

    private void verify(String auth, PublicKey publicKey) throws RAServerException {
        try {
            final String[] split = auth.split("\\.");
            if (split.length != 2) {
                throw new RAServerException(RAServerException.REASON_CODE_REGISTER_SERVICE_INVALID_TOKEN, "expected:<cert.sig>,but invalid auth:" + auth);
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
                throw new RAServerException(RAServerException.REASON_CODE_REGISTER_SERVICE_VERIFY_TOKEN);
            }
        } catch (Exception e) {
            throw new RAServerException(RAServerException.REASON_CODE_REGISTER_SERVICE_VERIFY_TOKEN, e);
        }
    }

    private void updateCallerStore(String caname, String callerID, String id) throws RAServerException {
        final CA ca = server.getCA(caname);
        ca.updateEnrollIdStore(callerID, id);
    }

    private void updateUserStore(String caname, IUser user, String secret) throws RAServerException {
        final CA ca = server.getCA(caname);
        ca.updateUserStore(user, secret);
    }
}
