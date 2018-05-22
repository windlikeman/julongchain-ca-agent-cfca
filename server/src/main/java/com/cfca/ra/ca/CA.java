package com.cfca.ra.ca;

import com.cfca.ra.RAServer;
import com.cfca.ra.RAServerException;
import com.cfca.ra.beans.*;
import com.cfca.ra.utils.PemUtils;
import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.util.ASN1Dump;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.DecoderException;
import org.bouncycastle.util.encoders.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

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
     * The database handle used to store certificates and optionally
     * the user registry information, unless LDAP it enabled for the
     * user registry function.
     */
    private final CAStore store;

    /**
     * The server hosting this CA
     */
    private final RAServer server;

    /**
     * The user registry
     */
    private final IUserRegistry registry;

    private ConcurrentMap<String, String> userStore;
    private ConcurrentMap<String, String> enrollIdStore;

    private CA(Builder builder) throws RAServerException {
        this.config = builder.config;
        this.configFilePath = builder.configFilePath;
        this.store = builder.store;
        this.server = builder.server;
        this.registry = builder.registry;
        this.userStore = new ConcurrentHashMap<>();
        this.userStore.put("admin", "YWRtaW46MTIzNA==");
        this.enrollIdStore = loadEnrollIdFile();
        updateEnrollIdFile();
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

    public CAStore getStore() {
        return store;
    }

    public RAServer getServer() {
        return server;
    }

    public IUserRegistry getRegistry() {
        return registry;
    }

    public void storeCert(String username, String b64cert) throws RAServerException {
        if (StringUtils.isBlank(username)) {
            throw new RAServerException(RAServerException.REASON_CODE_CA_STORE_CERT_INVALID_ARGS, "ca[" + getName() + "] fail to store cert to file, username is empty");
        }

        if (StringUtils.isBlank(b64cert)) {
            throw new RAServerException(RAServerException.REASON_CODE_CA_STORE_CERT_INVALID_ARGS, "ca[" + getName() + "] fail to store cert to file, b64cert is empty");
        }

        byte[] decode = null;
        try {
            decode = Base64.decode(b64cert);
            final String homeDir = getHomeDir();
            final String certFile = buildCertFile(homeDir, username);
            if (logger.isInfoEnabled()) {
                logger.info("storeCert<<<<<< store cert to :" + certFile);
            }
            PemUtils.storeCert(certFile, decode);
        } catch (DecoderException e) {
            final String msg = "storeCert>>>>>> Failure cert:\n" + Hex.toHexString(decode) + "\nfail to decode b64";
            throw new RAServerException(RAServerException.REASON_CODE_CA_STORE_CERT_B64_DECODE, "fail to store cert to file due to b64 decode :" + msg, e);
        } catch (IOException e) {
            final String cert = dumpCertASN1(decode);
            throw new RAServerException(RAServerException.REASON_CODE_CA_STORE_CERT_WITH_PEM, "fail to store cert to file due to process pem file , cert:\n" + cert, e);
        }
    }

    private String dumpCertASN1(byte[] decode) {
        String result = "none";
        if (decode == null) {
            return result;
        }
        try {
            final ASN1Primitive asn1Primitive = ASN1Primitive.fromByteArray(decode);
            result = ASN1Dump.dumpAsString(asn1Primitive, true);
        } catch (IOException e) {
            result = Hex.toHexString(decode) + "\nfail to decode asn1 due to:" + e.getMessage();
        }
        return result;
    }

    public void updateEnrollIdStore(String enrollmentID, String id) throws RAServerException {
        enrollIdStore.put(id, enrollmentID);
        updateEnrollIdFile();
    }

    private ConcurrentHashMap<String, String> loadEnrollIdFile() throws RAServerException {
        final ConcurrentHashMap<String, String> enrollIdStore = new ConcurrentHashMap<>();
        final String homeDir = getHomeDir();
        File file = new File(String.join(File.separator, homeDir, "enroll-id.dat"));
        if (!file.exists()) {
            enrollIdStore.put("admin", "admin");
            return enrollIdStore;
        }
        try {
            final String s = FileUtils.readFileToString(file);
            if (logger.isInfoEnabled()) {
                logger.info("loadEnrollIdFile<<<<<< s:" + s);
            }
            final Map map = new Gson().fromJson(s, Map.class);
            Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
            Map.Entry<String, String> entry;
            while (it.hasNext()) {
                entry = it.next();
                enrollIdStore.put(entry.getKey(), entry.getValue());
            }
            return enrollIdStore;
        } catch (Exception e) {
            throw new RAServerException(RAServerException.REASON_CODE_CA_LOAD_ENROLLID_FILE, e);
        }
    }

    private void updateEnrollIdFile() throws RAServerException {
        try {
            final String homeDir = getHomeDir();
            File file = new File(String.join(File.separator, homeDir, "enroll-id.dat"));
            final String s = new Gson().toJson(enrollIdStore);
            if (logger.isInfoEnabled()) {
                logger.info("updateEnrollIdFile<<<<<<" + s);
            }
            FileUtils.writeStringToFile(file, s);
        } catch (IOException e) {
            throw new RAServerException(RAServerException.REASON_CODE_CA_UPDATE_ENROLLID_FILE, e);
        }
    }

    public String getEnrollmentId(String id) {
        String enrollmentId = "admin";
        if (enrollIdStore.containsKey(id)) {
            enrollmentId = enrollIdStore.get(id);
        }
        return enrollmentId;
    }

    public void checkIdRegistered(String id) throws RAServerException {
        if (userStore == null) {
            throw new RAServerException(RAServerException.REASON_CODE_CA_CHECK_ID_REGISTERED, "this userStore is null");
        }

        if (StringUtils.isEmpty(id)) {
            throw new RAServerException(RAServerException.REASON_CODE_CA_CHECK_ID_REGISTERED, "this user id is null");
        }

        if (userStore.containsKey(id)) {
            throw new RAServerException(RAServerException.REASON_CODE_CA_USER_ALREADY_REGISTERED, "this user[" + id + "] not exist already registered in CA[" + getName() + "]");
        }
    }

    public String getUserSecret(String user) throws RAServerException {
        if (userStore == null) {
            throw new RAServerException(RAServerException.REASON_CODE_CA_NOT_READY, "CA[" + getName() + "] user store is null");
        }
        if (!userStore.containsKey(user)) {
            throw new RAServerException(RAServerException.REASON_CODE_CA_USER_NOT_EXIST, "this user[" + user + "] not exist in CA[" + getName() + "]");
        }
        return userStore.get(user);
    }

    private String buildCertFile(String homeDir, String enrollmentId) {
        final String certDir = String.join(File.separator, homeDir, "certs");
        final String certFile = String.format("%s-cert.pem", enrollmentId);
        return String.join(File.separator, certDir, certFile);
    }

    public Certificate loadCert(String homeDir, String enrollmentId) throws RAServerException {
        try {
            return PemUtils.loadCert(buildCertFile(homeDir, enrollmentId));
        } catch (IOException e) {
            throw new RAServerException(RAServerException.REASON_CODE_CA_LOAD_CERT, e);
        }
    }

    public void attributeIsTrue(String id, String s) throws RAServerException {
    }

    public void fillGettcertInfo(GettcertResponseNet resp) {
    }

    public static class Builder {
        private final CAConfig config;
        private final RAServer server;
        private final IUserRegistry registry;

        private String configFilePath = "";
        private CAStore store = new CAStore();

        public Builder(RAServer server, CAConfig config, IUserRegistry registry) {
            this.server = server;
            this.config = config;
            this.registry = registry;
        }

        public Builder configFilePath(String v) {
            this.configFilePath = v;
            return this;
        }

        public Builder store(CAStore v) {
            this.store = v;
            return this;
        }

        public CA build() throws RAServerException {
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
        final CA ca = (CA) o;
        return Objects.equals(config, ca.config) &&
                Objects.equals(configFilePath, ca.configFilePath) &&
                Objects.equals(store, ca.store) &&
                Objects.equals(server, ca.server) &&
                Objects.equals(registry, ca.registry);
    }

    @Override
    public int hashCode() {
        return Objects.hash(config, configFilePath, store, server, registry);
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
