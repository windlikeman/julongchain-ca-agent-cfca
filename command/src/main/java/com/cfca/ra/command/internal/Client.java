package com.cfca.ra.command.internal;

import com.cfca.ra.command.CommandException;
import com.cfca.ra.command.config.CsrConfig;
import com.cfca.ra.command.internal.enroll.*;
import com.cfca.ra.command.internal.getcainfo.*;
import com.cfca.ra.command.internal.gettcert.*;
import com.cfca.ra.command.internal.reenroll.ReenrollmentComms;
import com.cfca.ra.command.internal.reenroll.ReenrollmentRequest;
import com.cfca.ra.command.internal.reenroll.ReenrollmentRequestNet;
import com.cfca.ra.command.internal.register.*;
import com.cfca.ra.command.internal.revoke.*;
import com.cfca.ra.command.utils.MyFileUtils;
import com.cfca.ra.command.utils.MyStringUtils;
import com.cfca.ra.command.utils.PemUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangchong
 * @create 2018/5/15
 * @Description 内部实现客户端
 * @CodeReviewer
 * @since v3.0.0
 */
public class Client {
    private static final Logger logger = LoggerFactory.getLogger(Client.class);
    private static final String ADMIN = "admin";

    /**
     * 客户端配置
     */
    private final ClientConfig clientCfg;
    /**
     * 客户端 home 目录
     */
    private final String homedir;

    private final EnrollmentComms enrollmentComms;
    private final ReenrollmentComms reenrollmentComms;

    private final GetCAInfoComms getCAInfoComms;

    private final RegisterComms registerComms;
    private final RevokeComms revokeComms;

    private final GettCertComms gettCertComms;


    private boolean initialized;

    private final IEnrollIdStore enrollIdStore;

    /**
     * 文件和目录
     */
    private String keyFile;
    private String certFile;
    private String caCertsDir;
    private String certDir;
    private String keyDir;

    private final Provider provider;

    public Client(ClientConfig clientCfg, String homedir) {
        this.clientCfg = clientCfg;
        this.homedir = homedir;
        this.enrollmentComms = new EnrollmentComms(clientCfg);
        this.getCAInfoComms = new GetCAInfoComms(clientCfg);
        this.reenrollmentComms = new ReenrollmentComms(clientCfg);
        this.registerComms = new RegisterComms(clientCfg);
        this.revokeComms = new RevokeComms(clientCfg);
        this.gettCertComms = new GettCertComms(clientCfg);
        this.enrollIdStore = EnrollIdStore.CFCA;
        this.provider = new BouncyCastleProvider();
        Security.addProvider(provider);
    }

    public ClientConfig getClientCfg() {
        return clientCfg;
    }

    public EnrollmentResponse enroll(EnrollmentRequest enrollmentRequest) throws CommandException {
        initializeIfNeeded(enrollmentRequest.getUsername());
        final CsrConfig csrConfig = enrollmentRequest.getCsrConfig();
        if (csrConfig == null || csrConfig.getKey() == null) {
            throw new CommandException(CommandException.REASON_CODE_INTERNAL_CLIENT_ENROLL_CSRCONFIG_EXCEPTION, "enrollmentRequest missing csrConfig or missing key info");
        }
        final String password = enrollmentRequest.getPassword();
        final String username = enrollmentRequest.getUsername();
        String basicAuth = buildBasicAuth(username, password);
//        final String algo = csrConfig.getKey().getAlgo();
//        final String names = csrConfig.getNames();
//        final CsrResult result = genCSR(algo, names);
//        storeMyPrivateKey(result);
        EnrollmentRequestNet enrollmentRequestNet = buildEnrollmentRequestNet(enrollmentRequest, enrollmentRequest.getRequest());

        final EnrollmentResponseNet responseNet = enrollmentComms.request(enrollmentRequestNet, basicAuth);

        PrivateKey privateKey = getPrivateKey(keyFile);
        logger.info("enroll  <<<<<< get private Key at {}", keyFile);
        logger.info("enroll  <<<<<< get private Key => [{}]", privateKey);
        return buildEnrollmentResponse(responseNet, username, privateKey);
    }

    /**
     * 用户必须先提供私钥,将私钥放置与命令行工具包指定的目录下,这个私钥将用于以后的身份签名,保证安全性
     *
     * @param keyFile
     * @return
     * @throws CommandException
     */
    private PrivateKey getPrivateKey(String keyFile) throws CommandException {
        try {
            return PemUtils.loadPrivateKey(keyFile);
        } catch (Exception e) {
            throw new CommandException(CommandException.REASON_CODE_INTERNAL_CLIENT_LOAD_PRIVATEKEY_FAILED, e);
        }
    }

    private byte[] marshal(Object request) throws CommandException {
        try {
            final Gson gson = new GsonBuilder().disableHtmlEscaping().create();
            final String s = gson.toJson(request);
            logger.info("marshal<<<<<<json    : " + s);
            final byte[] encode = Base64.encode(s.getBytes("UTF-8"));
            logger.info("marshal<<<<<<encode  : " + Hex.toHexString(encode));
            return encode;
        } catch (Exception e) {
            throw new CommandException(CommandException.REASON_CODE_INTERNAL_CLIENT_BUILD_BASICAUTH_EXCEPTION, "marshal failed", e);
        }
    }

    public EnrollmentResponse reenroll(ReenrollmentRequest reenrollmentRequest, String username, Identity identity) throws CommandException {
        initializeIfNeeded(reenrollmentRequest.getUsername());
        final CsrConfig csrConfig = reenrollmentRequest.getCsrConfig();
        if (csrConfig == null || csrConfig.getKey() == null) {
            throw new CommandException(CommandException.REASON_CODE_INTERNAL_CLIENT_REENROLL_EXCEPTION, "reenrollmentRequest missing csrConfig or missing key info");
        }

        ReenrollmentRequestNet reenrollmentRequestNet = buildReenrollmentRequestNet(reenrollmentRequest, reenrollmentRequest.getRequest());
        final byte[] encode = marshal(reenrollmentRequestNet);
        String token = identity.addTokenAuthHdr(encode);
        final EnrollmentResponseNet responseNet = reenrollmentComms.request(reenrollmentRequestNet, token);

        final PrivateKey privateKey = getPrivateKey(keyFile);
        logger.info("reenroll  <<<<<< get private Key at {}", keyFile);
        return buildEnrollmentResponse(responseNet, username, privateKey);
    }

    public ServerInfo getCAInfo(GetCAInfoRequest getCAInfoRequest) throws CommandException {
        initializeIfNeeded(null);
        GetCAInfoRequestNet getCAInfoRequestNet = buildGetCAInfoRequestNet(getCAInfoRequest);
        final GetCAInfoResponseNet responseNet = getCAInfoComms.request(getCAInfoRequestNet);
        return buildServerInfo(responseNet);
    }

    private ServerInfo buildServerInfo(GetCAInfoResponseNet responseNet) {
        ServerInfo.Builder builder = new ServerInfo.Builder();
        final GetCAInfoResponseResult result = responseNet.getResult();
        final String cachain = result.getCachain();
        if (!MyStringUtils.isEmpty(cachain)) {
            builder.caChain(Base64.decode(cachain));
        }
        builder.caName(result.getCaname());
        return builder.build();
    }

    private GetCAInfoRequestNet buildGetCAInfoRequestNet(GetCAInfoRequest req) {
        return new GetCAInfoRequestNet(req.getCaName());
    }

    private EnrollmentResponse buildEnrollmentResponse(EnrollmentResponseNet responseNet, String username, PrivateKey key) throws CommandException {
        final String certB64Encoded = responseNet.getResult();
        Identity identity = buildIdentity(username, certB64Encoded, key);
        ServerInfo serverInfo = net2LocalServerInfo(responseNet);
        return new EnrollmentResponse(identity, serverInfo);
    }

    private ServerInfo net2LocalServerInfo(EnrollmentResponseNet responseNet) throws CommandException {
        final List<ServerResponseMessage> messages = responseNet.getMessages();
        if (messages == null) {
            return null;
        }

        try {
            int code;
            String caName;
            String version;
            String caChain;
            String enrollmentId;
            ServerInfo.Builder serverInfo = new ServerInfo.Builder();
            for (ServerResponseMessage message : messages) {
                if (message == null) {
                    continue;
                }
                code = message.getCode();
                switch (code) {
                    case ServerResponseMessage.RESPONSE_MESSAGE_CODE_CANAME:
                        caName = message.getMessage();
                        serverInfo.caName(caName);
                        break;
                    case ServerResponseMessage.RESPONSE_MESSAGE_CODE_VERSION:
                        version = message.getMessage();
                        serverInfo.version(version);
                        break;
                    case ServerResponseMessage.RESPONSE_MESSAGE_CODE_CACHAIN:
                        caChain = message.getMessage();
                        serverInfo.caChain(caChain.getBytes("UTF-8"));
                        break;
                    case ServerResponseMessage.RESPONSE_MESSAGE_CODE_ENROLLMENTID:
                        enrollmentId = message.getMessage();
                        serverInfo.enrollmentId(enrollmentId);
                        break;
                    default:
                        break;
                }
            }
            return serverInfo.build();
        } catch (UnsupportedEncodingException e) {
            throw new CommandException(CommandException.REASON_CODE_INTERNAL_CLIENT_STORE_LOCAL_SERVERINFO_EXCEPTION, "caChain getBytes unsupport encoding :UTF-8", e);
        }
    }

    /**
     * String name, byte[] cert, PrivateKey key
     *
     * @param username
     * @param certB64Encoded
     * @param key
     * @return
     * @throws CommandException
     */
    private Identity buildIdentity(String username, String certB64Encoded, PrivateKey key) throws CommandException {
        try {
            if (MyStringUtils.isEmpty(certB64Encoded)) {
                throw new CommandException(CommandException.REASON_CODE_INTERNAL_CLIENT_BUILD_IDENTITY_FAILED, "failed to build identity by empty certB64Encoded");
            }

            //这里certB64Encoded只是证书公钥
            final byte[] certDecode = Base64.decode(certB64Encoded);
            return buildIdentity(username, certDecode, key);
        } catch (Exception e) {
            throw new CommandException(CommandException.REASON_CODE_INTERNAL_CLIENT_BUILD_IDENTITY_FAILED, e);
        }

    }

    private EnrollmentRequestNet buildEnrollmentRequestNet(EnrollmentRequest enrollmentRequest, String p10) throws CommandException {
        final CsrConfig csrConfig = enrollmentRequest.getCsrConfig();
        if (csrConfig == null) {
            throw new CommandException(CommandException.REASON_CODE_INTERNAL_CLIENT_REENROLLMENT_BUILD_NET_REQUEST_FAILED, "enrollmentRequest missing csrConfig ");
        }

        final String username = enrollmentRequest.getUsername();
        if (MyStringUtils.isEmpty(username)) {
            throw new CommandException(CommandException.REASON_CODE_INTERNAL_CLIENT_REENROLLMENT_BUILD_NET_REQUEST_FAILED, "enrollmentRequest missing username");
        }

        final String profile = enrollmentRequest.getProfile();
        if (MyStringUtils.isEmpty(profile)) {
            throw new CommandException(CommandException.REASON_CODE_INTERNAL_CLIENT_REENROLLMENT_BUILD_NET_REQUEST_FAILED, "enrollmentRequest missing profile");
        }
        final String caName = enrollmentRequest.getCaName();
        if (MyStringUtils.isEmpty(caName)) {
            throw new CommandException(CommandException.REASON_CODE_INTERNAL_CLIENT_REENROLLMENT_BUILD_NET_REQUEST_FAILED, "enrollmentRequest missing CA Name");
        }
        return new EnrollmentRequestNet.Builder(p10, profile, caName).build();
    }


    private ReenrollmentRequestNet buildReenrollmentRequestNet(ReenrollmentRequest reenrollmentRequest, String p10) throws CommandException {
        final CsrConfig csrConfig = reenrollmentRequest.getCsrConfig();
        if (csrConfig == null) {
            throw new CommandException(CommandException.REASON_CODE_INTERNAL_CLIENT_REENROLLMENT_BUILD_NET_REQUEST_FAILED, "reenrollmentRequest missing csrConfig ");
        }

        final String username = reenrollmentRequest.getUsername();
        if (MyStringUtils.isEmpty(username)) {
            throw new CommandException(CommandException.REASON_CODE_INTERNAL_CLIENT_REENROLLMENT_BUILD_NET_REQUEST_FAILED, "reenrollmentRequest missing username");
        }

        final String profile = reenrollmentRequest.getProfile();
        if (MyStringUtils.isEmpty(profile)) {
            throw new CommandException(CommandException.REASON_CODE_INTERNAL_CLIENT_REENROLLMENT_BUILD_NET_REQUEST_FAILED, "reenrollmentRequest missing profile");
        }
        final String caName = reenrollmentRequest.getCaName();
        if (MyStringUtils.isEmpty(caName)) {
            throw new CommandException(CommandException.REASON_CODE_INTERNAL_CLIENT_REENROLLMENT_BUILD_NET_REQUEST_FAILED, "reenrollmentRequest missing CA Name");
        }
        return new ReenrollmentRequestNet.Builder(p10, profile, caName).build();
    }

    private String buildBasicAuth(String username, String password) throws CommandException {
        if (MyStringUtils.isEmpty(username) || MyStringUtils.isEmpty(password)) {
            throw new CommandException(CommandException.REASON_CODE_INTERNAL_CLIENT_BUILD_BASICAUTH_EXCEPTION, "username or password is empty");
        }
        try {
            String userInfo = username + ":" + password;
            return "Basic " + Base64.toBase64String(userInfo.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new CommandException(CommandException.REASON_CODE_INTERNAL_CLIENT_BUILD_BASICAUTH_EXCEPTION, e);
        }
    }

    public void storeMyIdentity(byte[] cert) throws CommandException {
        try {
            initializeIfNeeded(null);
            PemUtils.storeCert(certFile, cert);
            logger.info("storeMyIdentity  <<<<<< certFile =>[{}] ", certFile);

            if (logger.isInfoEnabled()) {
                final Certificate certificate = PemUtils.loadCert(certFile);
                final SubjectPublicKeyInfo subjectPublicKeyInfo = certificate.getSubjectPublicKeyInfo();
                JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
                PublicKey publicKey = converter.getPublicKey(subjectPublicKeyInfo);
                logger.info("storeMyIdentity  <<<<<< publicKey :{}", publicKey);
            }

            Certificate c = PemUtils.loadCert(cert);
            clientCfg.setEnrollmentId(c.getSubject().toString());


            logger.info("storeMyIdentity  <<<<<< enrollmentId is {}", clientCfg.getEnrollmentId());
        } catch (IOException e) {
            throw new CommandException(CommandException.REASON_CODE_INTERNAL_CLIENT_STORE_IDENTITY_FAILED, e);
        }
    }

    private String buildCertFile(String certDir) throws CommandException {
        if (MyStringUtils.isBlank(certDir)) {
            throw new CommandException(CommandException.REASON_CODE_INTERNAL_CLIENT_BUILD_CERTFILE_PATH, "certDir is blank");
        }
        return String.join(File.separator, certDir, "cert.pem");
    }

    private String buildPrivateKeyFile(String keyDir) throws CommandException {
        if (MyStringUtils.isBlank(keyDir)) {
            throw new CommandException(CommandException.REASON_CODE_INTERNAL_CLIENT_BUILD_PRIVATEKEY_FILE_PATH, "keyDir is blank");
        }
        return String.join(File.separator, keyDir, "key.pem");
    }

    private void initializeIfNeeded(String userName) throws CommandException {
        logger.info("initializeIfNeeded<<<<<<Initializing client with initialized[{}],userName[{}]", initialized, userName);
        if (!initialized) {
            try {
                logger.info("initializeIfNeeded<<<<<<Initializing client with config[{}]", clientCfg);
                String mspDir = clientCfg.getMspDir();
                if (MyStringUtils.isEmpty(mspDir) || ClientConfig.DEFAULT_CONFIG_MSPDIR_VAL.equalsIgnoreCase(mspDir)) {
                    clientCfg.setMspDir("msp");
                }
                mspDir = MyFileUtils.makeFileAbs(clientCfg.getMspDir(), homedir);
                clientCfg.setMspDir(mspDir);
                // 密钥目录和文件
                if (MyStringUtils.isBlank(userName) || ADMIN.equalsIgnoreCase(userName)) {
                    this.keyDir = String.join(File.separator, mspDir, "keystore");
                } else {
                    this.keyDir = String.join(File.separator, mspDir, userName, "keystore");
                }
                boolean mkdirs = new File(keyDir).mkdirs();
                if (!mkdirs) {
                    logger.warn("initializeIfNeeded<<<<<<failed to create keystore directory");
                }
                this.keyFile = buildPrivateKeyFile(keyDir);
                logger.info("initializeIfNeeded<<<<<<use keyFile at {}", keyFile);
                // 证书目录和文件
                if (MyStringUtils.isBlank(userName) || ADMIN.equalsIgnoreCase(userName)) {
                    this.certDir = String.join(File.separator, mspDir, "signcerts");
                } else {
                    this.certDir = String.join(File.separator, mspDir, userName, "signcerts");
                }
                mkdirs = new File(certDir).mkdirs();
                if (!mkdirs) {
                    logger.warn("initializeIfNeeded<<<<<<failed to create keystore directory");
                }
                this.certFile = buildCertFile(certDir);
                logger.info("initializeIfNeeded<<<<<<use certFile at {}", certFile);

                // CA 证书目录
                this.caCertsDir = String.join(File.separator, mspDir, "cacerts");
                mkdirs = new File(caCertsDir).mkdirs();
                if (!mkdirs) {
                    logger.info("initializeIfNeeded<<<<<<failed to create keystore directory");
                }
                // Successfully initialized the client
                initialized = true;
            } catch (Exception e) {
                throw new CommandException(CommandException.REASON_CODE_INTERNAL_CLIENT_INIT_FAILED, "failed to init client", e);
            }
        }
    }

    public void checkEnrollment(String userName) throws CommandException {
        initializeIfNeeded(userName);
        boolean keyFileExists = MyFileUtils.fileExists(keyFile);
        boolean certFileExists = MyFileUtils.fileExists(certFile);
        if (!keyFileExists || !certFileExists) {
            throw new CommandException(CommandException.REASON_CODE_INTERNAL_CLIENT_CHECK_ENROLLMENT_EXCEPTION, "Enrollment information does not exist. Please execute enroll command first.");
        }
    }

    public Identity loadMyIdentity() throws CommandException {
        try {
            String enrollmentId = clientCfg.getEnrollmentId();
            String userName = getUserName(enrollmentId);
            initializeIfNeeded(userName);

            final PrivateKey privateKey = PemUtils.loadPrivateKey(keyFile);
            final byte[] certDecoded = PemUtils.loadFileContent(certFile);
            logger.info("loadMyIdentity<<<<<<enrollmentId[{}]=>userName[{}]", enrollmentId, userName);
            logger.info("loadMyIdentity<<<<<<keyFile            =>[{}]", keyFile);
            logger.info("loadMyIdentity<<<<<<certFile           =>[{}]", certFile);
            if (MyStringUtils.isBlank(enrollmentId)) {
                Certificate c = PemUtils.loadCert(certFile);
                clientCfg.setEnrollmentId(c.getSubject().toString());
            }
            enrollmentId = clientCfg.getEnrollmentId();

            return buildIdentity(enrollmentId, certDecoded, privateKey);
        } catch (IOException e) {
            throw new CommandException(CommandException.REASON_CODE_INTERNAL_CLIENT_LOAD_IDENTITY_EXCEPTION, e);
        }
    }

    private String getUserName(String enrollmentId) throws CommandException {
        return enrollIdStore.getUserName(enrollmentId);
    }

    /**
     * @param name       用户名
     * @param cert       通过b64解码得到的证书的原始字节
     * @param privateKey 私钥
     * @return 用户标识
     */
    private Identity buildIdentity(String name, byte[] cert, PrivateKey privateKey) throws CommandException {
        logger.info("buildIdentity<<<<<<PrivateKey  =>[{}]", privateKey);

        final PublicKey publicKey = getPublicKey(cert);
        logger.info("buildIdentity<<<<<<PublicKey   =>[{}]", publicKey);

        Signer ecert = new Signer(privateKey, cert, this);
        return new Identity(name, ecert, this);
    }

    private PublicKey getPublicKey(byte[] cert) throws CommandException {
        try {
            final Certificate certificate = PemUtils.loadCert(cert);
            final SubjectPublicKeyInfo subjectPublicKeyInfo = certificate.getSubjectPublicKeyInfo();
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
            return converter.getPublicKey(subjectPublicKeyInfo);
        } catch (Exception e) {
            throw new CommandException(CommandException.REASON_CODE_INTERNAL_CLIENT_LOAD_IDENTITY_EXCEPTION, e);
        }
    }

    public RegistrationResponse register(RegistrationRequest registrationRequest, Identity identity) throws CommandException {
        final String userName = clientCfg.getAdmin();
        initializeIfNeeded(userName);
        RegistrationRequestNet registrationRequestNet = buildRegistrationRequestNet(registrationRequest);
        final byte[] encode = marshal(registrationRequestNet);
        String token = identity.addTokenAuthHdr(encode);
        final RegistrationResponseNet responseNet = registerComms.request(registrationRequestNet, token);
        return buildRegistrationResponse(responseNet);

    }

    private RegistrationResponse buildRegistrationResponse(RegistrationResponseNet responseNet) throws CommandException {
        final String redentials = responseNet.getResult().getRedentials();
        if (MyStringUtils.isEmpty(redentials)) {
            throw new CommandException(CommandException.REASON_CODE_INTERNAL_CLIENT_REGISTER_RESPONSE_EMPTY_PASSWORD_FROM_SERVER);
        }
        return new RegistrationResponse(redentials);
    }

    public RevokeResponse revoke(RevokeRequest registrationRequest, Identity identity) throws CommandException {
        RevokeRequestNet registrationRequestNet = buildRevokeRequestNet(registrationRequest);
        final byte[] encode = marshal(registrationRequest);
        String token = identity.addTokenAuthHdr(encode);
        final RevokeResponseNet responseNet = revokeComms.request(registrationRequestNet, token);
        return buildRevokeResponse(responseNet);
    }

    public GettCertResponse gettcert(GettCertRequest request, Identity identity) throws CommandException {
        GettCertRequestNet gettCertRequestNet = buildGettCertRequestNet(request);
        final byte[] encode = marshal(gettCertRequestNet);
        final String token = identity.addTokenAuthHdr(encode);
        final GettcertResponseNet responseNet = gettCertComms.request(gettCertRequestNet, token);
        return buildGettCertResponse(responseNet);
    }

    private GettCertResponse buildGettCertResponse(GettcertResponseNet responseNet) {
        List<TCert> certs = new ArrayList<>();
        return new GettCertResponse(certs);
    }

    private GettCertRequestNet buildGettCertRequestNet(GettCertRequest request) {
        int count = 0;
        List<String> attrNames = new ArrayList<>();
        boolean encryptAttrs = false;
        int validityPeriod = 10;
        String caName = "CFCA";
        return new GettCertRequestNet(count, attrNames, encryptAttrs, validityPeriod, caName);
    }

    private RevokeRequestNet buildRevokeRequestNet(RevokeRequest registrationRequest) {
        return new RevokeRequestNet(registrationRequest);
    }

    private RevokeResponse buildRevokeResponse(RevokeResponseNet responseNet) {
        return new RevokeResponse(responseNet.getResult());
    }

    private RegistrationRequestNet buildRegistrationRequestNet(RegistrationRequest registrationRequest) {
        return new RegistrationRequestNet(registrationRequest);
    }


    @Override
    public String toString() {
        return "Client{" +
                "clientCfg=" + clientCfg +
                ", homedir='" + homedir + '\'' +
                '}';
    }


}
