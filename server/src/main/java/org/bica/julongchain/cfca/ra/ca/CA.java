package org.bica.julongchain.cfca.ra.ca;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.bica.julongchain.cfca.ra.RAServer;
import org.bica.julongchain.cfca.ra.RAServerException;
import org.bica.julongchain.cfca.ra.beans.ServerResponseMessage;
import org.bica.julongchain.cfca.ra.enroll.EnrollmentResponseNet;
import org.bica.julongchain.cfca.ra.getcainfo.GetCAInfoResponseNet;
import org.bica.julongchain.cfca.ra.getcainfo.GetCAInfoResponseResult;
import org.bica.julongchain.cfca.ra.register.IUser;
import org.bica.julongchain.cfca.ra.repository.ICACertStore;
import org.bica.julongchain.cfca.ra.repository.MongodbRegistryStore;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.util.encoders.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author zhangchong
 * @create 2018/5/16
 * @Description CA 对象,用于支持多CA配置管理
 * @CodeReviewer helonglong
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
     * The database handle used to certStore certificates and optionally the
     * user registry information, unless LDAP it enabled for the user registry
     * function.
     */
    private final ICACertStore certStore;

    /**
     * The server hosting this CA
     */
    private final RAServer server;

    /**
     * The user registry store
     */
    private final MongodbRegistryStore registry;

    /**
     *  CA名字
     */
    private final String name;

    private CA(Builder builder) {
        this.config = builder.config;
        this.configFilePath = builder.configFilePath;
        this.certStore = builder.certStore;
        this.server = builder.server;
        this.registry = builder.registry;
        this.name = builder.name;
    }

    public CAConfig getConfig() {
        return config;
    }

    public String getConfigFilePath() {
        return configFilePath;
    }

    public RAServer getServer() {
        return server;
    }

    public MongodbRegistryStore getRegistry() {
        return registry;
    }

    public String getHomeDir() {
        String homeDir = "";
        if (config == null) {
            return homeDir;
        }
        return config.getHomeDir();
    }

    private String getName() {
        return this.name;
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
            throw new RAServerException( "this registryStore is null");
        }

        if (StringUtils.isEmpty(id)) {
            throw new RAServerException( "this user id is null");
        }

        if (registry.containsUser(id, null)) {
            throw new RAServerException( "this user[" + id + "] already registered in CA[" + getName()
                    + "]");
        }
    }

    public String getUserSecret(String user) throws RAServerException {
        if (registry == null) {
            throw new RAServerException("CA[" + getName() + "] user store is null");
        }
        if (!registry.containsUser(user, null)) {
            throw new RAServerException( "this user[" + user + "] not exist in CA[" + getName() + "]");
        }
        final IUser iu = registry.getUser(user, null);
        return iu.getPassWord();
    }

    public void attributeIsTrue(String id, String attr) throws RAServerException {
    }

    public void storeCert(String enrollmentID, String b64cert, String serialNo) throws RAServerException {
        certStore.storeCert(name, enrollmentID, b64cert, serialNo);
    }

    public void revokeCert(String serialNo) throws RAServerException {
        certStore.revokeCert(name, serialNo);
    }

    public Certificate loadCert(String enrollmentId) throws RAServerException {
        return certStore.loadCert(name, enrollmentId);
    }



    public static class Builder {
        private final CAConfig config;
        private final RAServer server;
        private final MongodbRegistryStore registry;
        private String name = "";

        private String configFilePath = "";
        private ICACertStore certStore = null;


        public Builder(RAServer server, CAConfig config, MongodbRegistryStore registry) {
            this.server = server;
            this.config = config;
            this.registry = registry;
            if (config != null) {
                final CAInfo ca = config.getCA();
                if (ca != null) {
                    this.name = ca.getName();
                }
            }
        }

        public Builder certStore(ICACertStore v) {
            this.certStore = v;
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
        return Objects.equals(config, ca.config) && Objects.equals(configFilePath, ca.configFilePath)
                && Objects.equals(certStore, ca.certStore)
                && Objects.equals(server, ca.server) && Objects.equals(registry, ca.registry);
    }

    @Override
    public int hashCode() {
        return Objects.hash(config, configFilePath, certStore, server, registry);
    }

    public void fillCAInfo(EnrollmentResponseNet enrollmentResponseNet, String enrollmentID) throws RAServerException {
        final List<ServerResponseMessage> messages = new ArrayList<>();

        final String chain = checkAndGetCAChain();

        messages.add(new ServerResponseMessage(ServerResponseMessage.RESPONSE_MESSAGE_CODE_CANAME, getName()));
        messages.add(new ServerResponseMessage(ServerResponseMessage.RESPONSE_MESSAGE_CODE_VERSION, config.getVersion()));
        messages.add(new ServerResponseMessage(ServerResponseMessage.RESPONSE_MESSAGE_CODE_ENROLLMENTID, enrollmentID));
        messages.add(new ServerResponseMessage(ServerResponseMessage.RESPONSE_MESSAGE_CODE_CACHAIN, chain));
        enrollmentResponseNet.setMessages(messages);
    }

    public void fillCAInfo(GetCAInfoResponseNet resp) throws RAServerException {
        final String caName = getName();

        String chain = checkAndGetCAChain();

        GetCAInfoResponseResult result = new GetCAInfoResponseResult(caName, chain);
        resp.setResult(result);

        List<ServerResponseMessage> messages = new ArrayList<>();
        ServerResponseMessage e1 = new ServerResponseMessage(ServerResponseMessage.RESPONSE_MESSAGE_CODE_VERSION, config.getVersion());
        messages.add(e1);
        resp.setMessages(messages);
    }

    public final String checkAndGetCAChain() throws RAServerException {
        final String homeDir = getHomeDir();
        final String caChain = getCaChain();
        final String caChainFilePath = String.join(File.separator, homeDir, caChain);
        File caChainFile = new File(caChainFilePath);
        logger.info("CA@checkAndGetCAChain : caChainFile={}",caChainFile.getAbsolutePath());
        if (!caChainFile.exists()) {
            throw new RAServerException("caChain file not found at "+ caChainFile.getAbsolutePath());
        }

        byte[] chainData = null;
        try {
            chainData = FileUtils.readFileToByteArray(caChainFile);
        } catch (IOException e) {
            throw new RAServerException( caChainFile.getAbsolutePath(), e);
        }

        byte[] data = null;
        try {
            org.bouncycastle.asn1.pkcs.ContentInfo contentInfo = org.bouncycastle.asn1.pkcs.ContentInfo.getInstance(chainData);
            org.bouncycastle.asn1.pkcs.SignedData signedData = org.bouncycastle.asn1.pkcs.SignedData.getInstance(contentInfo.getContent());
            data = signedData.getEncoded();
        } catch (Exception e) {
            throw new RAServerException( "invalid pkcs#7 chain: " + caChainFile.getAbsolutePath(), e);
        }

        return new String(Base64.encode(data));
    }

}
