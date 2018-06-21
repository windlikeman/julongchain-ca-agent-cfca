package com.cfca.ra.service;

import com.cfca.ra.Identity;
import com.cfca.ra.RAServer;
import com.cfca.ra.RAServerException;
import com.cfca.ra.beans.ServerResponseError;
import com.cfca.ra.ca.Attribute;
import com.cfca.ra.ca.CA;
import com.cfca.ra.ca.IUserRegistry;
import com.cfca.ra.register.*;
import com.cfca.ra.utils.AuthUtils;
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
 * @Description 注册服务
 * @CodeReviewer helonglong
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

    private RegistrationRequest buildRegistrationRequest(RegistrationRequestNet data) {
        return new RegistrationRequest(data);
    }

    public RegistrationResponseNet registerUser(final RegistrationRequestNet requestNet, final String auth) {
        try {
            final RegistrationRequest req = buildRegistrationRequest(requestNet);
            final byte[] body = AuthUtils.marshal(requestNet);
            return registerUser(req, auth, body);
        } catch (RAServerException e) {
            logger.error("registerUser >>>>>> Failure : " + e.getMessage(), e);
            return buildRegisterErrorResponse(e);
        } catch (UnsupportedEncodingException e) {
            logger.error("registerUser >>>>>> Failure : " + e.getMessage(), e);
            return buildRegisterErrorResponse(new RAServerException(RAServerException.REASON_CODE_REGISTER_SERVICE_UPDATE_REGISTER_STORE, e));
        }
    }

    private RegistrationResponseNet registerUser(RegistrationRequest req, String auth, byte[] body) throws RAServerException,UnsupportedEncodingException {

        final String caname = req.getCaName();
        final Identity i = new Identity(server, caname, auth);
        i.verify(body);

        final String id = req.getName();
        checkIdRegistered(caname, id);

        final CA ca = server.getCA(caname);
//            IUser user = ca.getRegistry().getUser(enrollmentId, null);
        //FIXME: 将待注册权限磨平或者继承到注册者一致的权限 :
//            normalizeRegistrationRequest(data, user);
        //FIXME: 是否允许注册
//            canRegister(user, data);
        String pass = req.getName() + ":" + req.getSecret();
        pass = Base64.toBase64String(pass.getBytes("UTF-8"));

        final UserInfo insert = new UserInfo(req, pass, 1);
        final String secret = registerUserID(req, ca, insert);
        final RegistrationResponseNet registrationResponseNet = buildRegistrationResponseNet(secret);
        return registrationResponseNet;
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
        return insert.getPass();
    }

    private void addAttributeToRequest(AttributeNames attributeName, String value, List<Attribute> attribute) {
        attribute.add(new Attribute(attributeName.getName(), value, true));
    }

    private int getMaxEnrollments(int maxEnrollments, int maxEnrollments1) {
        return (maxEnrollments > maxEnrollments1) ? maxEnrollments1 : maxEnrollments;
    }

//    private void verify(String auth, PublicKey publicKey) throws RAServerException {
//        try {
//            final String[] split = auth.split("\\.");
//            if (split.length != AUTH_ELEMENT_NUM) {
//                throw new RAServerException(RAServerException.REASON_CODE_REGISTER_SERVICE_INVALID_TOKEN, "expected:<b64EnrollmentId.b64sig>,but invalid auth:" + auth);
//            }
//            final String b64EnrollmentId = split[0];
//            final String b64Sig = split[1];
//            final byte[] enrollmentId = Base64.decode(b64EnrollmentId);
//            logger.info("verify>>>>>> enrollmentId : " + new String(enrollmentId));
//            logger.info("verify>>>>>> publicKey    : " + publicKey);
//            Signature signature = Signature.getInstance("SM3withSM2", "BC");
//            signature.initVerify(publicKey);
//            signature.update(enrollmentId);
//
//            final byte[] sign = Base64.decode(b64Sig);
//            final boolean verify = signature.verify(sign);
//            if (!verify) {
//                throw new RAServerException(RAServerException.REASON_CODE_REGISTER_SERVICE_VERIFY_TOKEN);
//            }
//        } catch (Exception e) {
//            throw new RAServerException(RAServerException.REASON_CODE_REGISTER_SERVICE_VERIFY_TOKEN, e);
//        }
//    }

}
