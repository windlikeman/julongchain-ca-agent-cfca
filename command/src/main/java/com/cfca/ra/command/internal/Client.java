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
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveGenParameterSpec;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;
import org.bouncycastle.util.encoders.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.spec.AlgorithmParameterSpec;
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
    private static final String USER_ADMIN = "admin";
    public static final String ADMIN = "admin";

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
        final String algo = csrConfig.getKey().getAlgo();
        final String password = enrollmentRequest.getPassword();
        final String username = enrollmentRequest.getUsername();
        String basicAuth = buildBasicAuth(username, password);
        final String names = csrConfig.getNames();
        final CsrResult result = genCSR(algo, names);
        storeMyPrivateKey(result);
        EnrollmentRequestNet enrollmentRequestNet = buildEnrollmentRequestNet(enrollmentRequest, result.getCsr());

        final EnrollmentResponseNet responseNet = enrollmentComms.request(enrollmentRequestNet, basicAuth);

        return buildEnrollmentResponse(responseNet, username, result.getKeyPair().getPrivate());
    }

    public EnrollmentResponse reenroll(ReenrollmentRequest reenrollmentRequest, String token, String username) throws CommandException {
        initializeIfNeeded(reenrollmentRequest.getUsername());
        final CsrConfig csrConfig = reenrollmentRequest.getCsrConfig();
        if (csrConfig == null || csrConfig.getKey() == null) {
            throw new CommandException(CommandException.REASON_CODE_INTERNAL_CLIENT_REENROLL_EXCEPTION, "reenrollmentRequest missing csrConfig or missing key info");
        }
        final String algo = csrConfig.getKey().getAlgo();
        final String names = csrConfig.getNames();
        final CsrResult result = genCSR(algo, names);

        storeMyPrivateKey(result);

        ReenrollmentRequestNet reenrollmentRequestNet = buildEnrollmentRequestNet(reenrollmentRequest, result.getCsr());
        final EnrollmentResponseNet responseNet = reenrollmentComms.request(reenrollmentRequestNet, token);

        return buildEnrollmentResponse(responseNet, username, result.getKeyPair().getPrivate());
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


    private ReenrollmentRequestNet buildEnrollmentRequestNet(ReenrollmentRequest enrollmentRequest, String p10) throws CommandException {
        final CsrConfig csrConfig = enrollmentRequest.getCsrConfig();
        if (csrConfig == null) {
            throw new CommandException(CommandException.REASON_CODE_INTERNAL_CLIENT_REENROLLMENT_BUILD_NET_REQUEST_FAILED, "reenrollmentRequest missing csrConfig ");
        }

        final String username = enrollmentRequest.getUsername();
        if (MyStringUtils.isEmpty(username)) {
            throw new CommandException(CommandException.REASON_CODE_INTERNAL_CLIENT_REENROLLMENT_BUILD_NET_REQUEST_FAILED, "reenrollmentRequest missing username");
        }

        final String profile = enrollmentRequest.getProfile();
        if (MyStringUtils.isEmpty(profile)) {
            throw new CommandException(CommandException.REASON_CODE_INTERNAL_CLIENT_REENROLLMENT_BUILD_NET_REQUEST_FAILED, "reenrollmentRequest missing profile");
        }
        final String caName = enrollmentRequest.getCaName();
        if (MyStringUtils.isEmpty(caName)) {
            throw new CommandException(CommandException.REASON_CODE_INTERNAL_CLIENT_REENROLLMENT_BUILD_NET_REQUEST_FAILED, "reenrollmentRequest missing CA Name");
        }
        return new ReenrollmentRequestNet.Builder(p10, profile, caName, csrConfig).build();
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

    /**
     * @param keyAlg      签名算法名称
     * @param distictName 证书使用者 DN
     * @return CsrResult
     * @throws CommandException 失败时报错
     */
    public CsrResult genCSR(String keyAlg, String distictName) throws CommandException {
        if (MyStringUtils.isEmpty(keyAlg) || MyStringUtils.isEmpty(distictName)) {
            throw new CommandException(CommandException.REASON_CODE_INTERNAL_CLIENT_GENCSR_FAILED, "keyAlg or distictName is empty");
        }

        CsrResult result;
        switch (keyAlg.toUpperCase()) {
            case "SM2":
                result = getSM2CsrResult(distictName);
                break;
            default:
                throw new CommandException(CommandException.REASON_CODE_INTERNAL_CLIENT_GENCSR_FAILED, "Unsupport keyAlg type[" + keyAlg + "]");
        }

        return result;
    }

    private CsrResult getSM2CsrResult(String distictName) throws CommandException {
        final AlgorithmParameterSpec sm2p256v1 = new ECNamedCurveGenParameterSpec("sm2p256v1");
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("EC", provider);
            generator.initialize(sm2p256v1);
            KeyPair keypair = generator.generateKeyPair();
            logger.info("getSM2CsrResult>>>>>>publicKey : " + keypair.getPublic());
            logger.info("getSM2CsrResult>>>>>>privateKey : " + keypair.getPrivate());
            String csr = genSM2CSR(distictName, keypair);
            return new CsrResult(csr, keypair);
        } catch (Exception e) {
            throw new CommandException(CommandException.REASON_CODE_INTERNAL_CLIENT_GEN_SM2_CSR_FAILED, e);
        }
    }

    private String genSM2CSR(String distictName, KeyPair keypair) throws CommandException {

        try {
            if (MyStringUtils.isEmpty(distictName)) {
                throw new CommandException(CommandException.REASON_CODE_INTERNAL_CLIENT_GEN_SM2_CSR_FAILED, "distictName is empty");
            }

            PKCS10CertificationRequestBuilder pkcs10Builder = new JcaPKCS10CertificationRequestBuilder(
                    new X500Name(distictName),
                    keypair.getPublic());

            ContentSigner contentSigner = new JcaContentSignerBuilder("SM3WITHSM2").setProvider("BC").build(keypair.getPrivate());
            PKCS10CertificationRequest csr = pkcs10Builder.build(contentSigner);
            final byte[] base64Encoded = Base64.encode(csr.getEncoded());
            return new String(base64Encoded);
        } catch (CommandException e) {
            throw e;
        } catch (Exception e) {
            throw new CommandException(CommandException.REASON_CODE_INTERNAL_CLIENT_GEN_SM2_CSR_FAILED, e);
        }
    }

    public void storeMyIdentity(byte[] cert) throws CommandException {
        try {
            initializeIfNeeded(null);
            PemUtils.storeCert(certFile, cert);

            Certificate c = PemUtils.loadCert(cert);
            clientCfg.setEnrollmentId(c.getSubject().toString());

            logger.info("storeMyIdentity  <<<<<<store certificate at {} , enrollmentId is {}", certFile, clientCfg.getEnrollmentId());
        } catch (IOException e) {
            throw new CommandException(CommandException.REASON_CODE_INTERNAL_CLIENT_STORE_IDENTITY_FAILED, e);
        }
    }

    private void storeMyPrivateKey(CsrResult result) throws CommandException {
        try {
            initializeIfNeeded(null);
            final PrivateKey privateKey = result.getKeyPair().getPrivate();
            PemUtils.storePrivateKey(keyFile, privateKey);
            logger.info("storeMyPrivateKey<<<<<<store private key at {}", keyFile);
        } catch (Exception e) {
            throw new CommandException(CommandException.REASON_CODE_INTERNAL_CLIENT_STORE_PRIVATEKEY_FAILED, e);
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
        if (!initialized) {
            try {
                logger.info("initializeIfNeeded<<<<<<Initializing client with config: {}", clientCfg);
                String mspDir = clientCfg.getMspDir();
                if (MyStringUtils.isEmpty(mspDir) || ClientConfig.DEFAULT_CONFIG_MSPDIR_VAL.equalsIgnoreCase(mspDir)) {
                    clientCfg.setMspDir("msp");
                }
                mspDir = MyFileUtils.makeFileAbs(clientCfg.getMspDir(), homedir);
                clientCfg.setMspDir(mspDir);
                // 密钥目录和文件
                if (MyStringUtils.isBlank(userName) || USER_ADMIN.equalsIgnoreCase(userName)) {
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
            logger.info("loadMyIdentity<<<<<<enrollmentId[{}]=>userName[{}]", enrollmentId, userName);
            initializeIfNeeded(userName);

            final PrivateKey privateKey = PemUtils.loadPrivateKey(keyFile);
            final byte[] certDecoded = PemUtils.loadFileContent(certFile);

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
     *
     * @param name 用户名
     * @param cert 通过b64解码得到的证书的原始字节
     * @param key 私钥
     * @return 用户标识
     */
    private Identity buildIdentity(String name, byte[] cert, PrivateKey key) {
        logger.info("buildIdentity<<<<<<PrivateKey:\n" + key);

        Signer ecert = new Signer(key, cert, this);
        return new Identity(name, ecert, this);
    }

    public RegistrationResponse register(RegistrationRequest registrationRequest, String token) throws CommandException {
        final String userName = clientCfg.getAdmin();
        initializeIfNeeded(userName);
        RegistrationRequestNet registrationRequestNet = buildRegistrationRequestNet(registrationRequest);
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

    public RevokeResponse revoke(RevokeRequest registrationRequest, String token) throws CommandException {
        RevokeRequestNet registrationRequestNet = buildRevokeRequestNet(registrationRequest);
        final RevokeResponseNet responseNet = revokeComms.request(registrationRequestNet, token);
        return buildRevokeResponse(responseNet);
    }

    public GettCertResponse gettcert(GettCertRequest request, String token) throws CommandException {
        GettCertRequestNet gettCertRequestNet = buildGettCertRequestNet(request);
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
