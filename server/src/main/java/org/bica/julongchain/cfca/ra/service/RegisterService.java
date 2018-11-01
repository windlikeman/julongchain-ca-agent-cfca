package org.bica.julongchain.cfca.ra.service;

import org.bica.julongchain.cfca.ra.Identity;
import org.bica.julongchain.cfca.ra.RAServer;
import org.bica.julongchain.cfca.ra.RAServerException;
import org.bica.julongchain.cfca.ra.beans.ServerResponseError;
import org.bica.julongchain.cfca.ra.ca.Attribute;
import org.bica.julongchain.cfca.ra.ca.CA;
import org.bica.julongchain.cfca.ra.ca.IUserRegistry;
import org.bica.julongchain.cfca.ra.register.IUser;
import org.bica.julongchain.cfca.ra.register.RegistrationRequest;
import org.bica.julongchain.cfca.ra.register.RegistrationRequestNet;
import org.bica.julongchain.cfca.ra.register.RegistrationResponseNet;
import org.bica.julongchain.cfca.ra.register.RegistrationResponseResult;
import org.bica.julongchain.cfca.ra.register.UserInfo;
import org.bica.julongchain.cfca.ra.repository.MongodbRegistryStore;
import org.bica.julongchain.cfca.ra.utils.AuthUtils;
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
            return buildRegisterErrorResponse(new RAServerException("register service failed to update register " +
                    "store", e));
        }
    }

    private RegistrationResponseNet registerUser(RegistrationRequest req, String auth, byte[] body)
            throws RAServerException,UnsupportedEncodingException {

        final String caname = req.getCaName();
        final Identity i = new Identity(server, caname, auth, false);
        i.verify(body);

        final String id = req.getName();
        checkIdRegistered(caname, id);

        final CA ca = server.getCA(caname);
//            IUser user = ca.getRegistry().getUser(enrollmentId, null);
//            normalizeRegistrationRequest(user, data);
//            canRegister(user, data);
        String pass = req.getName() + ":" + req.getSecret();
        pass = Base64.toBase64String(pass.getBytes("UTF-8"));

        final UserInfo insert = new UserInfo(req, pass, 1);
        final String secret = registerUserID(req, ca, insert);
        final RegistrationResponseNet registrationResponseNet = buildRegistrationResponseNet(secret);
        return registrationResponseNet;
    }

    /**
     * FIXME: 是否允许注册
     * @param user
     * @param data
     * @throws RAServerException
     */
    private void canRegister(IUser user, RegistrationRequestNet data) throws RAServerException {

    }

    /**
     *
     * FIXME: 将待注册权限磨平或者继承到注册者一致的权限 :
     * @param user
     * @param data
     */
    private void normalizeRegistrationRequest(IUser user, RegistrationRequestNet data) {
    }

    private void checkIdRegistered(String caname, String id) throws RAServerException {
        server.checkIdRegistered(caname, id);
    }

    private RegistrationResponseNet buildRegisterErrorResponse(RAServerException e) {
        List<ServerResponseError> errors = new ArrayList<>();
        ServerResponseError elem = new ServerResponseError(e.getMessage());
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

        final MongodbRegistryStore registry = ca.getRegistry();

        if (registry.containsUser(req.getName(), null)) {
            String message = String.format("Identity '%s' is already registered", req.getName());
            throw new RAServerException( message);
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



}
