package com.cfca.ra.command.internal;

import com.cfca.ra.command.CommandException;
import com.cfca.ra.command.config.CsrConfig;
import com.cfca.ra.command.utils.MyFileUtils;
import com.cfca.ra.command.utils.PemUtils;
import com.cfca.ra.command.utils.MyStringUtils;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.util.ASN1Dump;
import org.bouncycastle.asn1.x500.X500Name;
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

    /**
     * 文件和目录
     */
    private String keyFile;
    private String certFile;
    private String caCertsDir;

    private static Provider provider;

    static {
        provider = new BouncyCastleProvider();
        Security.addProvider(provider);
    }

    private String certDir;
    private String keyDir;

    public Client(ClientConfig clientCfg, String homedir) {
        this.clientCfg = clientCfg;
        this.homedir = homedir;
        this.enrollmentComms = new EnrollmentComms(clientCfg);
        this.getCAInfoComms = new GetCAInfoComms(clientCfg);
        this.reenrollmentComms = new ReenrollmentComms(clientCfg);
        this.registerComms = new RegisterComms(clientCfg);
        this.revokeComms = new RevokeComms(clientCfg);
        this.gettCertComms= new GettCertComms(clientCfg);
    }

    public ClientConfig getClientCfg() {
        return clientCfg;
    }

    public EnrollmentResponse enroll(String username, EnrollmentRequest enrollmentRequest) throws CommandException {
        initializeIfNeeded();
        final CsrConfig csrConfig = enrollmentRequest.getCsrConfig();
        if (csrConfig == null || csrConfig.getKey() == null) {
            throw new CommandException(CommandException.REASON_CODE_INTERNAL_CLIENT_EXCEPTION, "enrollmentRequest missing csrConfig or missing key info");
        }
        final String algo = csrConfig.getKey().getAlgo();
        final String password = enrollmentRequest.getPassword();
        String basicAuth = buildBasicAuth(username, password);
        final CsrResult result = genCSR(algo, csrConfig.getNames());
        storeMyPrivateKey(result);
        EnrollmentRequestNet enrollmentRequestNet = buildReenrollmentRequestNet(enrollmentRequest, result.getCsr());

        final EnrollmentResponseNet responseNet = enrollmentComms.request(enrollmentRequestNet, basicAuth);

        return buildEnrollmentResponse(responseNet, username, result.getKeyPair().getPrivate());
    }

    public ServerInfo getCAInfo(GetCAInfoRequest getCAInfoRequest) throws CommandException {
        initializeIfNeeded();
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
                    default:
                        break;
                }
            }
            return serverInfo.build();
        } catch (UnsupportedEncodingException e) {
            throw new CommandException(CommandException.REASON_CODE_INTERNAL_CLIENT_EXCEPTION, "caChain getBytes unsupport encoding :UTF-8", e);
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
                throw new CommandException(CommandException.REASON_CODE_ENROLL_COMMAND_BUILD_IDENTITY_FAILED, "failed to build identity by empty certB64Encoded");
            }

            //这里certB64Encoded只是证书公钥
            final byte[] decode = Base64.decode(certB64Encoded);
            return buildIdentity(username, decode, key);
        } catch (Exception e) {
            throw new CommandException(CommandException.REASON_CODE_ENROLL_COMMAND_BUILD_IDENTITY_FAILED, e);
        }

    }

    private EnrollmentRequestNet buildReenrollmentRequestNet(EnrollmentRequest enrollmentRequest, String p10) throws CommandException {
        final CsrConfig csrConfig = enrollmentRequest.getCsrConfig();
        if (csrConfig == null) {
            throw new CommandException(CommandException.REASON_CODE_ENROLL_COMMAND_BUILD_NET_REQUEST_FAILED, "enrollmentRequest missing csrConfig ");
        }

        final String username = enrollmentRequest.getUsername();
        if (MyStringUtils.isEmpty(username)) {
            throw new CommandException(CommandException.REASON_CODE_ENROLL_COMMAND_BUILD_NET_REQUEST_FAILED, "enrollmentRequest missing username");
        }

        final String profile = enrollmentRequest.getProfile();
        if (MyStringUtils.isEmpty(profile)) {
            throw new CommandException(CommandException.REASON_CODE_ENROLL_COMMAND_BUILD_NET_REQUEST_FAILED, "enrollmentRequest missing profile");
        }
        final String caName = enrollmentRequest.getCaName();
        if (MyStringUtils.isEmpty(caName)) {
            throw new CommandException(CommandException.REASON_CODE_ENROLL_COMMAND_BUILD_NET_REQUEST_FAILED, "enrollmentRequest missing CA Name");
        }
        return new EnrollmentRequestNet.Builder(p10, profile, caName, csrConfig).build();
    }


    private ReenrollmentRequestNet buildReenrollmentRequestNet(ReenrollmentRequest enrollmentRequest, String p10) throws CommandException {
        final CsrConfig csrConfig = enrollmentRequest.getCsrConfig();
        if (csrConfig == null) {
            throw new CommandException(CommandException.REASON_CODE_REENROLL_COMMAND_BUILD_NET_REQUEST_FAILED, "reenrollmentRequest missing csrConfig ");
        }

        final String username = enrollmentRequest.getUsername();
        if (MyStringUtils.isEmpty(username)) {
            throw new CommandException(CommandException.REASON_CODE_REENROLL_COMMAND_BUILD_NET_REQUEST_FAILED, "reenrollmentRequest missing username");
        }

        final String profile = enrollmentRequest.getProfile();
        if (MyStringUtils.isEmpty(profile)) {
            throw new CommandException(CommandException.REASON_CODE_REENROLL_COMMAND_BUILD_NET_REQUEST_FAILED, "reenrollmentRequest missing profile");
        }
        final String caName = enrollmentRequest.getCaName();
        if (MyStringUtils.isEmpty(caName)) {
            throw new CommandException(CommandException.REASON_CODE_REENROLL_COMMAND_BUILD_NET_REQUEST_FAILED, "reenrollmentRequest missing CA Name");
        }
        return new ReenrollmentRequestNet.Builder(p10, profile, caName, csrConfig).build();
    }

    private String buildBasicAuth(String username, String password) throws CommandException {
        if (!MyStringUtils.isEmpty(username) && !MyStringUtils.isEmpty(password)) {
            try {
                String userInfo = username + ":" + password;
                return "Basic " + Base64.toBase64String(userInfo.getBytes("UTF-8"));
            } catch (UnsupportedEncodingException e) {
                throw new CommandException(CommandException.REASON_CODE_CLIENT_EXCEPTION, e);
            }
        }
        return "";
    }

    /**
     * @param keyAlg      签名算法名称
     * @param distictName 证书使用者 DN
     * @return CsrResult
     * @throws CommandException 失败时报错
     */
    public CsrResult genCSR(String keyAlg, String distictName) throws CommandException {
        if (MyStringUtils.isEmpty(keyAlg) || MyStringUtils.isEmpty(distictName)) {
            throw new CommandException(CommandException.REASON_CODE_ENROLL_COMMAND_GENCSR_FAILED, "keyAlg or distictName is empty");
        }

        CsrResult result;
        switch (keyAlg.toUpperCase()) {
            case "SM2":
                result = getSM2CsrResult(distictName);
                break;
            default:
                throw new CommandException(CommandException.REASON_CODE_ENROLL_COMMAND_GENCSR_FAILED, "Unsupport keyAlg type[" + keyAlg + "]");
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
            throw new CommandException(CommandException.REASON_CODE_ENROLL_COMMAND_GENCSR_FAILED, e);
        }
    }

    private String genSM2CSR(String distictName, KeyPair keypair) throws CommandException {

        try {
            if (MyStringUtils.isEmpty(distictName)) {
                throw new CommandException(CommandException.REASON_CODE_ENROLL_COMMAND_GENCSR_FAILED, "distictName is empty");
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
            throw new CommandException(CommandException.REASON_CODE_ENROLL_COMMAND_GENCSR_FAILED, e);
        }
    }

    public void storeMyIdentity(byte[] cert) throws CommandException {
        try {
            initializeIfNeeded();
            PemUtils.storeCert(certFile, cert);
            logger.info("Stored client certificate at {}", certFile);
        } catch (IOException e) {
            throw new CommandException(CommandException.REASON_CODE_ENROLL_COMMAND_STORE_IDENTITY_FAILED, e);
        }
    }

    private String buildCertFile() {
        return String.join(File.separator, certDir, "cert.pem");
    }

    private void storeMyPrivateKey(CsrResult result) throws CommandException {
        try {
            initializeIfNeeded();
            final PrivateKey privateKey = result.getKeyPair().getPrivate();
            PemUtils.storePrivateKey(keyFile, privateKey);
            logger.info("Stored client private key at {}", keyFile);
        } catch (Exception e) {
            throw new CommandException(CommandException.REASON_CODE_ENROLL_COMMAND_STORE_PRIVATEKEY_FAILED, e);
        }
    }

    private String buildPrivateKeyFile() {
        return String.join(File.separator, keyDir, "key.pem");
    }

    private void initializeIfNeeded() throws CommandException {
        if (!initialized) {
            try {
                logger.info("Initializing client with config: {}", clientCfg);
                String mspDir = clientCfg.getMspDir();
                if (MyStringUtils.isEmpty(mspDir) || "<<<MSPDIR>>>".equalsIgnoreCase(mspDir)) {
                    clientCfg.setMspDir("msp");
                }
                mspDir = MyFileUtils.makeFileAbs(clientCfg.getMspDir(), homedir);
                clientCfg.setMspDir(mspDir);
                // 密钥目录和文件
                this.keyDir = String.join(File.separator, mspDir, "keystore");
                boolean mkdirs = new File(keyDir).mkdirs();
                if (!mkdirs) {
                    logger.info("failed to create keystore directory");
                }
                this.keyFile = buildPrivateKeyFile();
                // 证书目录和文件
                this.certDir = String.join(File.separator, mspDir, "signcerts");
                mkdirs = new File(certDir).mkdirs();
                if (!mkdirs) {
                    logger.info("failed to create keystore directory");
                }
                this.certFile = buildCertFile();

                // CA 证书目录
                this.caCertsDir = String.join(File.separator, mspDir, "cacerts");
                mkdirs = new File(caCertsDir).mkdirs();
                if (!mkdirs) {
                    logger.info("failed to create keystore directory");
                }
                // Successfully initialized the client
                initialized = true;
            } catch (Exception e) {
                throw new CommandException(CommandException.REASON_CODE_ENROLL_COMMAND_INITIALIZE_CLIENT_FAILED, "failed to init client", e);
            }
        }
    }

    public void checkEnrollment() throws CommandException {
        initializeIfNeeded();
        boolean keyFileExists = MyFileUtils.fileExists(keyFile);
        boolean certFileExists = MyFileUtils.fileExists(certFile);
        if (!keyFileExists || !certFileExists) {
            throw new CommandException(CommandException.REASON_CODE_CLIENT_EXCEPTION, "Enrollment information does not exist. Please execute enroll command first.");
        }
    }

    public Identity loadMyIdentity() throws CommandException {
        try {
            final PrivateKey key = PemUtils.loadPrivateKey(keyFile);
            final byte[] certDecoded = PemUtils.loadFileContent(certFile);

            logger.info("loadMyIdentity<<<<<<cert:\n"+ ASN1Dump.dumpAsString(ASN1Primitive.fromByteArray(certDecoded)));
            logger.info("loadMyIdentity<<<<<<key:\n"+key);

            String name = clientCfg.getAdmin();
            return buildIdentity(name, certDecoded, key);
        } catch (IOException e) {
            throw new CommandException(CommandException.REASON_CODE_INTERNAL_CLIENT_LOAD_IDENTITY_EXCEPTION, e);
        }
    }

    private Identity buildIdentity(String name, byte[] cert, PrivateKey key) {
        try {
            logger.info("buildIdentity<<<<<<cert:\n"+ ASN1Dump.dumpAsString(ASN1Primitive.fromByteArray(cert)));
            logger.info("buildIdentity<<<<<<key:\n"+key);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Signer ecert = new Signer(key, cert, this);
        return new Identity(name, ecert, this);
    }

    public EnrollmentResponse reenroll(ReenrollmentRequest reenrollmentRequest, String token, String username) throws CommandException {
        initializeIfNeeded();
        final CsrConfig csrConfig = reenrollmentRequest.getCsrConfig();
        if (csrConfig == null || csrConfig.getKey() == null) {
            throw new CommandException(CommandException.REASON_CODE_INTERNAL_CLIENT_EXCEPTION, "reenrollmentRequest missing csrConfig or missing key info");
        }
        final String algo = csrConfig.getKey().getAlgo();
        final CsrResult result = genCSR(algo, csrConfig.getNames());

        storeMyPrivateKey(result);

        ReenrollmentRequestNet reenrollmentRequestNet = buildReenrollmentRequestNet(reenrollmentRequest, result.getCsr());
        final EnrollmentResponseNet responseNet = reenrollmentComms.request(reenrollmentRequestNet, token);

        return buildEnrollmentResponse(responseNet, username, result.getKeyPair().getPrivate());
    }

    public RegistrationResponse register(RegistrationRequest registrationRequest, String token) throws CommandException {
        RegistrationRequestNet registrationRequestNet = buildRegistrationRequestNet(registrationRequest);
        final RegistrationResponseNet responseNet = registerComms.request(registrationRequestNet, token);
        if (responseNet.isSuccess()) {
            return buildRegistrationResponse(responseNet);
        } else {
            throw new CommandException(CommandException.REASON_CODE_REGISTER_COMMAND_RESPONSE_NOT_SUCCESS);
        }

    }
    private RegistrationResponse buildRegistrationResponse(RegistrationResponseNet responseNet) throws CommandException {
        final String redentials = responseNet.getResult().getRedentials();
        if (MyStringUtils.isEmpty(redentials)) {
            throw new CommandException(CommandException.REASON_CODE_REGISTER_COMMAND_RESPONSE_EMPTY_PASSWORD);
        }
        return new RegistrationResponse(redentials);
    }

    public RevokeResponse revoke(RevokeRequest registrationRequest, String token) throws CommandException {
        RevokeRequestNet registrationRequestNet = buildRevokeRequestNet(registrationRequest);
        final RevokeResponseNet responseNet = revokeComms.request(registrationRequestNet, token);
        if (responseNet.isSuccess()) {
            return buildRevokeResponse(responseNet);
        } else {
            throw new CommandException(CommandException.REASON_CODE_REVOKE_COMMAND_RESPONSE_NOT_SUCCESS);
        }
    }

    public GetTCertResponse gettcert(GetTCertRequest request, String token) throws CommandException {
        GettcertRequestNet gettCertRequestNet = buildGettCertRequestNet(request);
        final GettcertResponseNet responseNet = gettCertComms.request(gettCertRequestNet, token);
        if (responseNet.isSuccess()) {
            return buildGettCertResponse(responseNet);
        } else {
            throw new CommandException(CommandException.REASON_CODE_REVOKE_COMMAND_RESPONSE_NOT_SUCCESS);
        }
    }

    private GetTCertResponse buildGettCertResponse(GettcertResponseNet responseNet) {
        return null;
    }

    private GettcertRequestNet buildGettCertRequestNet(GetTCertRequest request) {
        return null;
    }

    private RevokeRequestNet buildRevokeRequestNet(RevokeRequest registrationRequest) {
        return new RevokeRequestNet(registrationRequest);
    }

    private RevokeResponse buildRevokeResponse(RevokeResponseNet responseNet) throws CommandException{
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
