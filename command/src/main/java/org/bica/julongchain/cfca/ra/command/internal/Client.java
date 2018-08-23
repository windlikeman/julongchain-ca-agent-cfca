package org.bica.julongchain.cfca.ra.command.internal;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Security;
import java.util.List;
import java.util.Objects;

import org.bica.julongchain.cfca.ra.command.CommandException;
import org.bica.julongchain.cfca.ra.command.config.CsrConfig;
import org.bica.julongchain.cfca.ra.command.internal.enroll.EnrollIdStore;
import org.bica.julongchain.cfca.ra.command.internal.enroll.EnrollmentComms;
import org.bica.julongchain.cfca.ra.command.internal.enroll.EnrollmentRequest;
import org.bica.julongchain.cfca.ra.command.internal.enroll.EnrollmentRequestNet;
import org.bica.julongchain.cfca.ra.command.internal.enroll.EnrollmentResponse;
import org.bica.julongchain.cfca.ra.command.internal.enroll.EnrollmentResponseNet;
import org.bica.julongchain.cfca.ra.command.internal.enroll.IEnrollIdStore;
import org.bica.julongchain.cfca.ra.command.internal.getcainfo.GetCAInfoComms;
import org.bica.julongchain.cfca.ra.command.internal.getcainfo.GetCAInfoRequest;
import org.bica.julongchain.cfca.ra.command.internal.getcainfo.GetCAInfoRequestNet;
import org.bica.julongchain.cfca.ra.command.internal.getcainfo.GetCAInfoResponseNet;
import org.bica.julongchain.cfca.ra.command.internal.getcainfo.GetCAInfoResponseResult;
import org.bica.julongchain.cfca.ra.command.internal.heartbeat.HeartBeatComms;
import org.bica.julongchain.cfca.ra.command.internal.heartbeat.HeartBeatResponseNet;
import org.bica.julongchain.cfca.ra.command.internal.heartbeat.HeartBeatResponseVo;
import org.bica.julongchain.cfca.ra.command.internal.reenroll.ReenrollmentComms;
import org.bica.julongchain.cfca.ra.command.internal.reenroll.ReenrollmentRequest;
import org.bica.julongchain.cfca.ra.command.internal.reenroll.ReenrollmentRequestNet;
import org.bica.julongchain.cfca.ra.command.internal.register.RegisterComms;
import org.bica.julongchain.cfca.ra.command.internal.register.RegistrationRequest;
import org.bica.julongchain.cfca.ra.command.internal.register.RegistrationRequestNet;
import org.bica.julongchain.cfca.ra.command.internal.register.RegistrationResponse;
import org.bica.julongchain.cfca.ra.command.internal.register.RegistrationResponseNet;
import org.bica.julongchain.cfca.ra.command.internal.revoke.RevokeComms;
import org.bica.julongchain.cfca.ra.command.internal.revoke.RevokeRequest;
import org.bica.julongchain.cfca.ra.command.internal.revoke.RevokeRequestNet;
import org.bica.julongchain.cfca.ra.command.internal.revoke.RevokeResponse;
import org.bica.julongchain.cfca.ra.command.internal.revoke.RevokeResponseNet;
import org.bica.julongchain.cfca.ra.command.utils.FileUtils;
import org.bica.julongchain.cfca.ra.command.utils.PemUtils;
import org.bica.julongchain.cfca.ra.command.utils.StringUtils;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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

    private final HeartBeatComms heartBeatComms;

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
        this.heartBeatComms = new HeartBeatComms(clientCfg);

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
            throw new CommandException("enrollmentRequest missing csrConfig or missing key info");
        }
        final String password = enrollmentRequest.getPassword();
        final String username = enrollmentRequest.getUsername();
        final String sequenceNo = clientCfg.getSequenceNo();
        logger.info("Client@enroll : username={},sequenceNo={}", username, sequenceNo);
        String basicAuth = buildBasicAuth(username, password, sequenceNo);
        EnrollmentRequestNet enrollmentRequestNet = buildEnrollmentRequestNet(enrollmentRequest,
                enrollmentRequest.getRequest());
        final EnrollmentResponseNet responseNet = enrollmentComms.request(enrollmentRequestNet, basicAuth);
        PrivateKey privateKey = getPrivateKey(keyFile);
        logger.info("Client@enroll : get private Key from keyFile=[{}]", keyFile);
        logger.info("Client@enroll : get private Key=[{}]", privateKey);
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
            throw new CommandException(e);
        }
    }

    private byte[] marshal(Object request) throws CommandException {
        try {
            final Gson gson = new GsonBuilder().disableHtmlEscaping().create();
            final String s = gson.toJson(request);
            logger.info("Client@marshal<<<<<<json    : " + s);
            final byte[] encode = Base64.encode(s.getBytes("UTF-8"));
            logger.info("Client@marshal<<<<<<encode  : " + Hex.toHexString(encode));
            return encode;
        } catch (Exception e) {
            throw new CommandException("marshal failed", e);
        }
    }

    public HeartBeatResponseVo heartbeat() throws CommandException {
        logger.info("Client@heartbeat : Running");
        final HeartBeatResponseNet heartBeatResponseNet = heartBeatComms.request();
        return new HeartBeatResponseVo(heartBeatResponseNet.getDate(), heartBeatResponseNet.getStatus());
    }

    public EnrollmentResponse reenroll(ReenrollmentRequest reenrollmentRequest, String username, Identity identity) throws CommandException {
        initializeIfNeeded(reenrollmentRequest.getUsername());
        final CsrConfig csrConfig = reenrollmentRequest.getCsrConfig();
        if (csrConfig == null || csrConfig.getKey() == null) {
            throw new CommandException("Client@reenrollmentRequest missing csrConfig or missing key info");
        }

        ReenrollmentRequestNet reenrollmentRequestNet = buildReenrollmentRequestNet(reenrollmentRequest, reenrollmentRequest.getRequest());
        final byte[] encode = marshal(reenrollmentRequestNet);
        String token = identity.addTokenAuthHdr(encode, true);
        final EnrollmentResponseNet responseNet = reenrollmentComms.request(reenrollmentRequestNet, token);

        final PrivateKey privateKey = getPrivateKey(keyFile);
        logger.info("Client@reenroll  <<<<<< get private Key at {}", keyFile);
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
        if (!StringUtils.isEmpty(cachain)) {
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
            throw new CommandException("Client@caChain getBytes unsupport encoding :UTF-8", e);
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
            if (StringUtils.isEmpty(certB64Encoded)) {
                throw new CommandException("Client@failed to build identity by empty certB64Encoded");
            }

            // 这里certB64Encoded只是证书公钥
            final byte[] certDecode = Base64.decode(certB64Encoded);
            return buildIdentity(username, certDecode, key);
        } catch (Exception e) {
            throw new CommandException(e);
        }

    }

    private EnrollmentRequestNet buildEnrollmentRequestNet(EnrollmentRequest enrollmentRequest, String p10) throws CommandException {
        final CsrConfig csrConfig = enrollmentRequest.getCsrConfig();
        if (csrConfig == null) {
            throw new CommandException("Client@enrollmentRequest missing csrConfig ");
        }

        final String username = enrollmentRequest.getUsername();
        if (StringUtils.isEmpty(username)) {
            throw new CommandException("Client@enrollmentRequest missing username");
        }

        final String caName = enrollmentRequest.getCaName();
        if (StringUtils.isEmpty(caName)) {
            throw new CommandException("Client@enrollmentRequest missing CA Name");
        }
        return new EnrollmentRequestNet.Builder(p10, null, caName).build();
    }

    private ReenrollmentRequestNet buildReenrollmentRequestNet(ReenrollmentRequest reenrollmentRequest, String p10) throws CommandException {
        final CsrConfig csrConfig = reenrollmentRequest.getCsrConfig();
        if (csrConfig == null) {
            throw new CommandException("Client@reenrollmentRequest missing csrConfig ");
        }

        final String username = reenrollmentRequest.getUsername();
        if (StringUtils.isEmpty(username)) {
            throw new CommandException("Client@reenrollmentRequest missing username");
        }

        final String caName = reenrollmentRequest.getCaName();
        if (StringUtils.isEmpty(caName)) {
            throw new CommandException("Client@reenrollmentRequest missing CA Name");
        }
        return new ReenrollmentRequestNet.Builder(p10, null, caName).build();
    }

    private String buildBasicAuth(String username, String password, String sequenceNo) throws CommandException {
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            throw new CommandException("Client@username or password is empty");
        }
        try {
            String userInfo = username + ":" + password;
            String auth = Base64.toBase64String(userInfo.getBytes("UTF-8"));
            auth = String.format("%s:%s", auth, sequenceNo);
            return "Basic " + auth;
        } catch (UnsupportedEncodingException e) {
            throw new CommandException(e);
        }
    }

    public void storeMyIdentity(byte[] cert) throws CommandException {
        try {
            initializeIfNeeded(null);
            PemUtils.storeCert(certFile, cert);
            logger.info("Client@storeMyIdentity  <<<<<< certFile =>[{}] ", certFile);

            if (logger.isInfoEnabled()) {
                final Certificate certificate = PemUtils.loadCert(certFile);
                final SubjectPublicKeyInfo subjectPublicKeyInfo = certificate.getSubjectPublicKeyInfo();
                JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
                PublicKey publicKey = converter.getPublicKey(subjectPublicKeyInfo);
                logger.info("Client@storeMyIdentity  <<<<<< publicKey :{}", publicKey);
            }

            Certificate c = PemUtils.loadCert(cert);
            clientCfg.setEnrollmentId(c.getSubject().toString());

            logger.info("Client@storeMyIdentity  <<<<<< enrollmentId is {}", clientCfg.getEnrollmentId());
        } catch (IOException e) {
            throw new CommandException(e);
        }
    }

    private String buildCertFile(String certDir) throws CommandException {
        if (StringUtils.isBlank(certDir)) {
            throw new CommandException("certDir is blank");
        }
        return String.join(File.separator, certDir, "cert.pem");
    }

    private String buildPrivateKeyFile(String keyDir) throws CommandException {
        if (StringUtils.isBlank(keyDir)) {
            throw new CommandException("keyDir is blank");
        }
        return String.join(File.separator, keyDir, "key.pem");
    }

    private void initializeIfNeeded(String userName) throws CommandException {
        logger.info("Client@initializeIfNeeded<<<<<<Initializing client with initialized[{}],userName[{}]", initialized, userName);
        if (!initialized) {
            try {
                logger.info("initializeIfNeeded<<<<<<Initializing client with config[{}]", clientCfg);
                String mspDir = clientCfg.getMspDir();
                if (StringUtils.isEmpty(mspDir) || ClientConfig.DEFAULT_CONFIG_MSPDIR_VAL.equalsIgnoreCase(mspDir)) {
                    clientCfg.setMspDir("msp");
                }
                mspDir = FileUtils.makeFileAbs(clientCfg.getMspDir(), homedir);
                clientCfg.setMspDir(mspDir);
                // 密钥目录和文件

                this.keyDir = createDirIfNeed(userName, mspDir, "keystore");
                this.keyFile = buildPrivateKeyFile(keyDir);
                logger.info("initializeIfNeeded<<<<<<use keyFile at {}", keyFile);

                // 证书目录和文件
                this.certDir = createDirIfNeed(userName, mspDir, "signcerts");
                this.certFile = buildCertFile(certDir);
                logger.info("initializeIfNeeded<<<<<<use certFile at {}", certFile);

                // CA 证书目录
                this.caCertsDir = createDirIfNeed(userName, mspDir, "cacerts");

                // Successfully initialized the client
                initialized = true;

                logger.info("Client@initializeIfNeeded<<<<<<initialized={}", initialized);
            } catch (Exception e) {
                logger.error("initializeIfNeeded<<<<<<failed", e);
                throw new CommandException("failed to init client", e);
            }
        }
    }

    private final String createDirIfNeed(String userName, String mspDir, String dirType) throws Exception {
        String dir = null;
        if (StringUtils.isBlank(userName) || ADMIN.equalsIgnoreCase(userName)) {
            dir = String.join(File.separator, mspDir, dirType);
        } else {
            dir = String.join(File.separator, mspDir, userName, dirType);
        }

        final File destDir = new File(dir);
        if (destDir.exists()) {
            if (!destDir.isDirectory()) {
                throw new Exception("Client@destDir is file, cannot mkdirs " + destDir.getAbsolutePath());
            }

        } else {
            boolean mkdirs = destDir.mkdirs();
            if (!mkdirs) {
                logger.warn("Client@initializeIfNeeded<<<<<<failed to create {} directory", dirType);
            }
        }
        return dir;
    }

    public void checkEnrollment(String userName) throws CommandException {
        logger.info("Client@checkEnrollment enter:userName[{}]", userName);
        initializeIfNeeded(userName);
        boolean keyFileExists = FileUtils.fileExists(keyFile);
        boolean certFileExists = FileUtils.fileExists(certFile);
        if (!keyFileExists || !certFileExists) {
            throw new CommandException("Client@Enrollment information does not exist. Please execute enroll command first.");
        }
    }

    public Identity loadMyIdentity() throws CommandException {
        try {
            logger.info("Client@loadMyIdentity enter");
            String enrollmentId = clientCfg.getEnrollmentId();
            String userName = getUserName(enrollmentId);
            logger.info("Client@loadMyIdentity : userName[{}]", userName);
            initializeIfNeeded(userName);

            final PrivateKey privateKey = PemUtils.loadPrivateKey(keyFile);
            final byte[] certDecoded = PemUtils.loadFileContent(certFile);
            logger.info("loadMyIdentity<<<<<<enrollmentId[{}]=>userName[{}]", enrollmentId, userName);
            logger.info("loadMyIdentity<<<<<<keyFile            =>[{}]", keyFile);
            logger.info("loadMyIdentity<<<<<<certFile           =>[{}]", certFile);
            if (StringUtils.isBlank(enrollmentId)) {
                Certificate c = PemUtils.loadCert(certFile);
                clientCfg.setEnrollmentId(c.getSubject().toString());
            }
            enrollmentId = clientCfg.getEnrollmentId();

            return buildIdentity(enrollmentId, certDecoded, privateKey);
        } catch (IOException e) {
            throw new CommandException(e);
        }
    }

    private String getUserName(String enrollmentId) throws CommandException {
        return enrollIdStore.getUserName(enrollmentId);
    }

    /**
     * @param name 用户名
     * @param cert 通过b64解码得到的证书的原始字节
     * @return 用户标识
     */
    private Identity buildIdentity(String name, byte[] cert, PrivateKey privateKey) throws CommandException {
        logger.info("Client@buildIdentity<<<<<<name={}, PrivateKey=>[{}]", name, privateKey);

        final PublicKey publicKey = getPublicKey(cert);
        logger.info("Client@buildIdentity<<<<<<PublicKey   =>[{}]", publicKey);

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
            throw new CommandException(e);
        }
    }

    public RegistrationResponse register(RegistrationRequest registrationRequest, Identity identity) throws CommandException {
        final String userName = clientCfg.getAdmin();
        initializeIfNeeded(userName);
        RegistrationRequestNet registrationRequestNet = buildRegistrationRequestNet(registrationRequest);
        final byte[] encode = marshal(registrationRequestNet);
        String token = identity.addTokenAuthHdr(encode, false);
        final RegistrationResponseNet responseNet = registerComms.request(registrationRequestNet, token);
        return buildRegistrationResponse(responseNet);

    }

    private RegistrationResponse buildRegistrationResponse(RegistrationResponseNet responseNet) throws CommandException {
        final String redentials = responseNet.getResult().getRedentials();
        if (StringUtils.isEmpty(redentials)) {
            throw new CommandException();
        }
        return new RegistrationResponse(redentials);
    }

    public RevokeResponse revoke(RevokeRequest registrationRequest, Identity identity) throws CommandException {
        RevokeRequestNet registrationRequestNet = buildRevokeRequestNet(registrationRequest);
        final byte[] encode = marshal(registrationRequest);
        String token = identity.addTokenAuthHdr(encode, false);
        final RevokeResponseNet responseNet = revokeComms.request(registrationRequestNet, token);
        return buildRevokeResponse(responseNet);
    }

    private RevokeRequestNet buildRevokeRequestNet(RevokeRequest registrationRequest) {
        return new RevokeRequestNet(registrationRequest);
    }

    private RevokeResponse buildRevokeResponse(RevokeResponseNet responseNet) {
        final List<ServerResponseError> errors = responseNet.getErrors();
        if (!Objects.isNull(errors) && !errors.isEmpty()) {
            return new RevokeResponse(errors.get(0).getMessage());
        }
        return new RevokeResponse(responseNet.getResult());
    }

    private RegistrationRequestNet buildRegistrationRequestNet(RegistrationRequest registrationRequest) {
        return new RegistrationRequestNet(registrationRequest);
    }

    public String getKeyFile() {
        return keyFile;
    }

    @Override
    public String toString() {
        return "Client{" + "clientCfg=" + clientCfg + ", homedir='" + homedir + '\'' + '}';
    }
}
