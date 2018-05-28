package com.cfca.ra.ca;

import com.cfca.ra.RAServer;
import com.cfca.ra.RAServerException;
import com.cfca.ra.beans.*;
import com.cfca.ra.enroll.EnrollmentResponseNet;
import com.cfca.ra.getcainfo.GetCAInfoResponseNet;
import com.cfca.ra.getcainfo.GetCAInfoResponseResult;
import com.cfca.ra.gettcert.GettCertResponse;
import com.cfca.ra.gettcert.GettCertResponseNet;
import com.cfca.ra.gettcert.GettCertResponseResult;
import com.cfca.ra.register.IUser;
import com.cfca.ra.repository.CertCertStore;
import com.cfca.ra.repository.EnrollIdStore;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.util.encoders.Base64;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author zhangchong
 * @create 2018/5/16
 * @Description CA 对象,用于支持多CA配置管理
 * @CodeReviewer
 * @since v3.0.0
 */
public class CA {
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
    private final CertCertStore certStore;

    /**
     * The server hosting this CA
     */
    private final RAServer server;

    /**
     * The user registry
     */
    private final IUserRegistry registry;

    /**
     * private final UserStore userStore;
     */
    private final EnrollIdStore enrollIdStore;

    /**
     * The tcert manager for this CA
     */
    private final TcertManager tcertMgr;
    /**
     * The key tree
     */
    private final TcertKeyTree tcertKeyTree;

    private CA(Builder builder) {
        this.config = builder.config;
        this.configFilePath = builder.configFilePath;
        this.certStore = builder.certStore;
        this.server = builder.server;
        this.registry = builder.registry;
        this.enrollIdStore = builder.enrollIdStore;
        this.tcertMgr = builder.tcertMgr;
        this.tcertKeyTree = builder.tcertKeyTree;
    }

    public TcertManager getTcertMgr() {
        return tcertMgr;
    }

    public TcertKeyTree getTcertKeyTree() {
        return tcertKeyTree;
    }

    public CAConfig getConfig() {
        return config;
    }

    public String getConfigFilePath() {
        return configFilePath;
    }

    public CertCertStore getCertStore() {
        return certStore;
    }

    public RAServer getServer() {
        return server;
    }

    public IUserRegistry getRegistry() {
        return registry;
    }

    public String getHomeDir() {
        String homeDir = "";
        if (config == null) {
            return homeDir;
        }
        final CAInfo ca = config.getCA();
        if (ca != null) {
            homeDir = ca.getHomeDir();
        }
        return homeDir;
    }

    private String getName() {
        String name = "";
        if (config == null) {
            return name;
        }
        final CAInfo ca = config.getCA();
        if (ca != null) {
            name = ca.getName();
        }
        return name;
    }

    private String getCaChain() {
        String chainfile = "";
        if (config == null) {
            return chainfile;
        }
        final CAInfo ca = config.getCA();
        if (ca != null) {
            chainfile = ca.getChainfile();
        }
        return chainfile;
    }

    public void checkIdRegistered(String id) throws RAServerException {
        if (registry == null) {
            throw new RAServerException(RAServerException.REASON_CODE_CA_CHECK_ID_REGISTERED, "this registryStore is null");
        }

        if (StringUtils.isEmpty(id)) {
            throw new RAServerException(RAServerException.REASON_CODE_CA_CHECK_ID_REGISTERED, "this user id is null");
        }

        if (registry.containsUser(id, null)) {
            throw new RAServerException(RAServerException.REASON_CODE_CA_USER_ALREADY_REGISTERED, "this user[" + id + "] already registered in CA[" + getName() + "]");
        }
    }

    public String getUserSecret(String user) throws RAServerException {
        if (registry == null) {
            throw new RAServerException(RAServerException.REASON_CODE_CA_NOT_READY, "CA[" + getName() + "] user store is null");
        }
        if (!registry.containsUser(user, null)) {
            throw new RAServerException(RAServerException.REASON_CODE_CA_USER_NOT_EXIST, "this user[" + user + "] not exist in CA[" + getName() + "]");
        }
        final IUser iu = registry.getUser(user, null);
        return iu.getPassWord();
    }

    public void attributeIsTrue(String id, String attr) throws RAServerException {
    }

    public void fillGettcertInfo(GettCertResponseNet resp, GettCertResponse gettCertResponse) {
        long id = gettCertResponse.getId();
        long ts = gettCertResponse.getTs();
        String key = Base64.toBase64String(gettCertResponse.getKey());

        resp.setResult(new GettCertResponseResult(id, ts, key, gettCertResponse.gettCerts()));
    }


    public String getEnrollmentId(String id) throws RAServerException {
        return enrollIdStore.getEnrollmentId(id);
    }

    public void storeCert(String enrollmentID, String b64cert) throws RAServerException {
        certStore.storeCert(enrollmentID, b64cert);
    }

    public Certificate loadCert(String enrollmentId) throws RAServerException {
        return certStore.loadCert(enrollmentId);
    }

    public void updateEnrollIdStore(String enrollmentId, String id) throws RAServerException {
        enrollIdStore.updateEnrollIdStore(enrollmentId, id);
    }

    public String getCertFile(String serial) throws RAServerException {
        return certStore.getCertFilePath(serial);
    }

    public static class Builder {
        private final CAConfig config;
        private final RAServer server;
        private final IUserRegistry registry;

        private String configFilePath = "";
        private CertCertStore certStore = CertCertStore.CFCA;
        private EnrollIdStore enrollIdStore = EnrollIdStore.CFCA;

        private TcertManager tcertMgr;
        private TcertKeyTree tcertKeyTree;

        public Builder(RAServer server, CAConfig config, IUserRegistry registry) {
            this.server = server;
            this.config = config;
            this.registry = registry;
        }

        public Builder tcertMgr(TcertManager v) {
            this.tcertMgr = v;
            return this;
        }

        public Builder tcertKeyTree(TcertKeyTree v) {
            this.tcertKeyTree = v;
            return this;
        }

        public Builder certStore(CertCertStore v) {
            this.certStore = v;
            return this;
        }

        public Builder enrollIdStore(EnrollIdStore v) {
            this.enrollIdStore = v;
            return this;
        }

        public Builder configFilePath(String v) {
            this.configFilePath = v;
            return this;
        }

        public CA build() {
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
                enrollIdStore == ca.enrollIdStore;
    }

    @Override
    public int hashCode() {
        return Objects.hash(config, configFilePath, certStore, server, registry, enrollIdStore);
    }

    public void fillCAInfo(EnrollmentResponseNet enrollmentResponseNet) throws RAServerException {
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

    private byte[] getCAChain() throws RAServerException {
        final String homeDir = getHomeDir();
        final String caChain = getCaChain();
        final String caChainFilePath = String.join(File.separator, homeDir, caChain);
        File caChainFile = new File(caChainFilePath);
        if (!caChainFile.exists()) {
            throw new RAServerException(RAServerException.REASON_CODE_CA_NOT_FOUND_CACHAIN_FILE);
        }
        try {
            return FileUtils.readFileToByteArray(caChainFile);
        } catch (IOException e) {
            throw new RAServerException(RAServerException.REASON_CODE_CA_READ_CACHAIN_FILE, e);
        }
    }

    public void fillCAInfo(GetCAInfoResponseNet resp) throws RAServerException {
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
