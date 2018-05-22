package com.cfca.ra.ca;

import com.cfca.ra.RAServer;
import com.cfca.ra.RAServerException;
import com.cfca.ra.beans.*;
import com.cfca.ra.ca.register.IUser;
import com.cfca.ra.ca.repository.CertStore;
import com.cfca.ra.ca.repository.EnrollIdStore;
import com.cfca.ra.ca.repository.UserStore;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.util.encoders.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author zhangchong
 * @create 2018/5/16
 * @Description
 * @CodeReviewer
 * @since v3.0.0
 */
public class CA {
    private static final Logger logger = LoggerFactory.getLogger(CA.class);

    /**
     * The CA's configuration
     */
    private final CAConfig config;

    /**
     * The file path of the config file
     */
    private final String configFilePath;

    /**
     * The database handle used to certStore certificates and optionally
     * the user registry information, unless LDAP it enabled for the
     * user registry function.
     */
    private final CertStore certStore;

    /**
     * The server hosting this CA
     */
    private final RAServer server;

    /**
     * The user registry
     */
    private final IUserRegistry registry;

    private final UserStore userStore;
    private final EnrollIdStore enrollIdStore;

    private CA(Builder builder){
        this.config = builder.config;
        this.configFilePath = builder.configFilePath;
        this.certStore = builder.certStore;
        this.server = builder.server;
        this.registry = builder.registry;
        this.userStore = builder.userStore;
        this.enrollIdStore =builder.enrollIdStore;
    }

    public String getHomeDir() {
        final CAInfo ca = config.getCA();
        String homeDir = "";
        if (ca != null) {
            homeDir = ca.getHomeDir();
        }
        return homeDir;
    }

    private String getName() {
        final CAInfo ca = config.getCA();
        if (ca != null) {
            return ca.getName();
        } else {
            return "";
        }
    }

    public CAConfig getConfig() {
        return config;
    }

    public String getConfigFilePath() {
        return configFilePath;
    }

    public CertStore getCertStore() {
        return certStore;
    }

    public RAServer getServer() {
        return server;
    }

    public IUserRegistry getRegistry() {
        return registry;
    }

    public void checkIdRegistered(String id) throws RAServerException {
        if (userStore == null) {
            throw new RAServerException(RAServerException.REASON_CODE_CA_CHECK_ID_REGISTERED, "this userStore is null");
        }

        if (StringUtils.isEmpty(id)) {
            throw new RAServerException(RAServerException.REASON_CODE_CA_CHECK_ID_REGISTERED, "this user id is null");
        }

        if (userStore.containsUser(id)) {
            throw new RAServerException(RAServerException.REASON_CODE_CA_USER_ALREADY_REGISTERED, "this user[" + id + "] not exist already registered in CA[" + getName() + "]");
        }
    }

    public String getUserSecret(String user) throws RAServerException {
        if (userStore == null) {
            throw new RAServerException(RAServerException.REASON_CODE_CA_NOT_READY, "CA[" + getName() + "] user store is null");
        }
        if (!userStore.containsUser(user)) {
            throw new RAServerException(RAServerException.REASON_CODE_CA_USER_NOT_EXIST, "this user[" + user + "] not exist in CA[" + getName() + "]");
        }
        return userStore.getUser(user);
    }

    public void attributeIsTrue(String id, String s) throws RAServerException {
    }

    public void fillGettcertInfo(GettcertResponseNet resp) {
    }


    public String getEnrollmentId(String id) throws RAServerException {
        return enrollIdStore.getEnrollmentId(id);
    }

    public void storeCert(String username, String b64cert) throws RAServerException {
        certStore.storeCert(username, b64cert);
    }

    public Certificate loadCert(String enrollmentId) throws RAServerException {
        return certStore.loadCert(enrollmentId);
    }

    public void updateEnrollIdStore(String enrollmentId, String id) throws RAServerException {
        enrollIdStore.updateEnrollIdStore(enrollmentId, id);
    }

    public void updateUserStore(IUser user, String secret) throws RAServerException {
        userStore.updateUserStore(user, secret);
    }

    public static class Builder {
        private final CAConfig config;
        private final RAServer server;
        private final IUserRegistry registry;

        private String configFilePath = "";
        private CertStore certStore = CertStore.CFCA;
        private UserStore userStore = UserStore.CFCA;
        private EnrollIdStore enrollIdStore = EnrollIdStore.CFCA;

        public Builder(RAServer server, CAConfig config, IUserRegistry registry) {
            this.server = server;
            this.config = config;
            this.registry = registry;
        }

        public Builder enrollIdStore(EnrollIdStore v) {
            this.enrollIdStore = v;
            return this;
        }

        public Builder userStore(UserStore v) {
            this.userStore = v;
            return this;
        }

        public Builder certStore(CertStore v) {
            this.certStore = v;
            return this;
        }

        public Builder configFilePath(String v) {
            this.configFilePath = v;
            return this;
        }

        public CA build()  {
            return new CA(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CA ca = (CA) o;
        return Objects.equals(config, ca.config) &&
                Objects.equals(configFilePath, ca.configFilePath) &&
                Objects.equals(certStore, ca.certStore) &&
                Objects.equals(server, ca.server) &&
                Objects.equals(registry, ca.registry) &&
                userStore == ca.userStore &&
                enrollIdStore == ca.enrollIdStore;
    }

    @Override
    public int hashCode() {
        return Objects.hash(config, configFilePath, certStore, server, registry, userStore, enrollIdStore);
    }

    public void fillCAInfo(EnrollmentResponseNet enrollmentResponseNet) {
        final List<ServerResponseMessage> messages = new ArrayList<>();
        ServerResponseMessage e1 = new ServerResponseMessage(ServerResponseMessage.RESPONSE_MESSAGE_CODE_CANAME, getName());
        ServerResponseMessage e2 = new ServerResponseMessage(ServerResponseMessage.RESPONSE_MESSAGE_CODE_VERSION, config.getVersion());
        messages.add(e1);
        messages.add(e2);

        byte[] caChain = getCAChain();
        if (caChain != null) {
            final String chain = Base64.toBase64String(caChain);
            ServerResponseMessage e3 = new ServerResponseMessage(ServerResponseMessage.RESPONSE_MESSAGE_CODE_CACHAIN, chain);
            messages.add(e3);
        }
        enrollmentResponseNet.setMessages(messages);
    }

    //FIXME
    private byte[] getCAChain() {
        return null;
    }

    public void fillCAInfo(GetCAInfoResponseNet resp) {
        final String caName = getName();
        byte[] caChain = getCAChain();
        String chain = "";
        if (null != caChain) {
            chain = Base64.toBase64String(caChain);
        }
        GetCAInfoResponseResult result = new GetCAInfoResponseResult(caName, chain);
        resp.setResult(result);

        List<ServerResponseMessage> messages = new ArrayList<>();
        ServerResponseMessage e1 = new ServerResponseMessage(ServerResponseMessage.RESPONSE_MESSAGE_CODE_VERSION, config.getVersion());
        messages.add(e1);
        resp.setMessages(messages);
    }
}
