package com.cfca.ra;

import com.cfca.ra.ca.*;
import com.cfca.ra.enroll.EnrollmentResponseNet;
import com.cfca.ra.getcainfo.GetCAInfoResponseNet;
import com.cfca.ra.gettcert.GettCertResponse;
import com.cfca.ra.gettcert.GettCertResponseNet;
import com.cfca.ra.register.IUser;
import com.cfca.ra.repository.CertCertStore;
import com.cfca.ra.repository.EnrollIdStore;
import com.cfca.ra.utils.MyFileUtils;
import com.cfca.ra.utils.PemUtils;
import org.apache.commons.lang.StringUtils;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

/**
 * @author zhangchong
 * @create 2018/5/16
 * @Description 服务器的信息
 * @CodeReviewer
 * @since v3.0.0
 */
@Configuration
public class RAServer {

    private static final Logger logger = LoggerFactory.getLogger(RAServer.class);

    /**
     * 服务器的工作目录
     */
    @Value("${server.homeDir}")
    private String serverHomeDir;

    @Autowired
    private CAInfo caInfo;

    /**
     * RAServer's default CA
     */
    private CA defaultCA;

    /**
     * A map of CAs stored by CA name as key
     */
    private Map<String, CA> caMap = new HashMap<>();

    private final static byte[] keybytes = {0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38,
            0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38};

    public void initialize() throws RAServerException {
        if (StringUtils.isEmpty(serverHomeDir)) {
            this.serverHomeDir = System.getProperty("user.dir");
        }
        this.serverHomeDir = MyFileUtils.getAbsFilePath(serverHomeDir);
        logger.info("Initializing server in directory {}", serverHomeDir);
        final boolean mkdirs = new File(serverHomeDir).mkdirs();
        if (!mkdirs) {
            logger.warn("failed to init server with create home directory");
        }

        defaultCA = initCA(null);
        addCA(defaultCA);
    }

    public String getServerHomeDir() {
        return serverHomeDir;
    }

    public String getUserSecret(final String caName, final String user) throws RAServerException {
        return getCA(caName).getUserSecret(user);
    }

    private void addCA(final CA adding) throws RAServerException {
        if (Objects.isNull(adding.getConfig())) {
            throw new RAServerException(RAServerException.REASON_CODE_RA_SERVER_ADD_CA_EXCEPTION, "the adding one get config is null");
        }
        final CAInfo addingOne = adding.getConfig().getCA();
        if (Objects.isNull(addingOne)) {
            throw new RAServerException(RAServerException.REASON_CODE_RA_SERVER_ADD_CA_EXCEPTION, "the adding one get config cainfo is null");
        }
        final String caName = addingOne.getName();
        final Iterator<Map.Entry<String, CA>> iter = caMap.entrySet().iterator();
        Map.Entry<String, CA> entry;
        CA value;
        CAInfo existedOne;
        String message;
        while (iter.hasNext()) {
            entry = iter.next();
            value = entry.getValue();
            existedOne = value.getConfig().getCA();
            if (existedOne != null && caName.equals(existedOne.getName())) {
                message = String.format("CA name '%s' is used in '%s' and '%s'",
                        caName, adding.getConfigFilePath(), value.getConfigFilePath());
                throw new RAServerException(RAServerException.REASON_CODE_RA_SERVER_ADD_CA_EXCEPTION, message);
            }
        }
        caMap.put(caName, adding);
    }

    private CA initCA(final CAConfig caConfig) throws RAServerException {
        CAConfig local = caConfig;
        if (local == null) {
            final String name = caInfo.getName();
            String homeDir = String.join(File.separator, serverHomeDir, name);
            homeDir = MyFileUtils.getAbsFilePath(homeDir);
            final File file = new File(homeDir);
            if (file.isDirectory()) {
                if (!file.exists()) {
                    final boolean mkdirs = file.mkdirs();
                    if (!mkdirs) {
                        logger.warn("failed to init CA with create CA home directory :{}", homeDir);
                        throw new RAServerException(RAServerException.REASON_CODE_RA_SERVER_CREATE_CA_HOME_DIR);
                    }
                }
            } else {
                logger.warn("failed to init CA with create CA home directory due to homeDir not dir type:{}", homeDir);
            }

//            final CAInfo caInfo = new CAInfo.Builder(name, homeDir).build();
            local = new CAConfig.Builder(caInfo, homeDir).build();
        }
        DefaultUserRegistry registry = new DefaultUserRegistry();

        // Initialize TCert handling
        final String keyfile = String.join(File.separator, local.getHomeDir(), local.getCA().getKeyfile());
        final String certfile = String.join(File.separator, local.getHomeDir(), local.getCA().getCertfile());
        TcertManager tcertMgr = loadTcertMgr(keyfile, certfile);
        // FIXME: root 前置密钥 需要序列化到数据库或者本地文件
        Key rootKey = genRootKey();
        final TcertKeyTree tcertKeyTree = new TcertKeyTree(rootKey);

        CA.Builder defaultCABuilder = new CA.Builder(this, local, registry)
                .enrollIdStore(EnrollIdStore.CFCA)
                .certStore(CertCertStore.CFCA)
                .tcertKeyTree(tcertKeyTree)
                .tcertMgr(tcertMgr);
        CA defaultCA = defaultCABuilder.build();

        logger.info("Init default CA with home {} and config {}", defaultCA.getHomeDir(), defaultCA.getConfig());
        return defaultCA;
    }

    Key genRootKey() {
        return new SecretKeySpec(keybytes, "AES256");
    }

    private TcertManager loadTcertMgr(String keyfile, String certfile) throws RAServerException {
        try {
            PrivateKey caKey = PemUtils.loadPrivateKey(keyfile);
            Certificate caCert = PemUtils.loadCert(certfile);
            ContentSigner caCertSigner = new JcaContentSignerBuilder("SM3WITHSM2").setProvider("BC").build(caKey);
            return new TcertManager.Builder(caKey, caCert).caCertSigner(caCertSigner).builder();
        } catch (Exception e) {
            throw new RAServerException(RAServerException.REASON_CODE_RA_SERVER_LOAD_TCERT_MGR, e);
        }
    }

    public CA getCA(final String caName) throws RAServerException {
        if (StringUtils.isEmpty(caName)) {
            throw new RAServerException(RAServerException.REASON_CODE_RA_SERVER_GET_CA_NAME_EMPTY);
        }
        // Lookup the CA from the server
        CA ca = caMap.getOrDefault(caName.toUpperCase(), null);
        if (ca == null) {
            String message = String.format("CA '%s' does not exist", caName);
            throw new RAServerException(RAServerException.REASON_CODE_RA_SERVER_GET_CA_NOT_FOUND, message);
        }
        return ca;
    }

    public void storeCert(final String caName, final String enrollmentID, final String b64cert) throws RAServerException {
        final CA ca = getCA(caName);
        ca.storeCert(enrollmentID, b64cert);
    }

    public PublicKey getKey(final String caName, final String enrollmentId) throws RAServerException {
        try {
            final CA ca = getCA(caName);
            final Certificate certificate = ca.loadCert(enrollmentId);
            final SubjectPublicKeyInfo subjectPublicKeyInfo = certificate.getSubjectPublicKeyInfo();
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
            return converter.getPublicKey(subjectPublicKeyInfo);
        } catch (Exception e) {
            throw new RAServerException(RAServerException.REASON_CODE_RA_SERVER_GET_KEY_EXCEPTION, e);
        }
    }

    public String getEnrollmentId(final String caName, final String id) throws RAServerException {
        final CA ca = getCA(caName);
        return ca.getEnrollmentId(id);
    }

    public void checkIdRegistered(final String caname, final String id) throws RAServerException {
        final CA ca = getCA(caname);
        ca.checkIdRegistered(id);
    }

    public String findCertFile(String caName, String serial) throws RAServerException {
        final CA ca = getCA(caName);
        return ca.getCertFile(serial);
    }

    public void fillCAInfo(String caname, GetCAInfoResponseNet resp) throws RAServerException {
        final CA ca = getCA(caname);
        ca.fillCAInfo(resp);
    }

    public void fillCAInfo(String caname, EnrollmentResponseNet resp) throws RAServerException {
        final CA ca = getCA(caname);
        ca.fillCAInfo(resp);
    }

    public IUser getUser(String caname, String enrollmentID, String[] attrs) throws RAServerException {
        final CA ca = getCA(caname);
        IUser caller = ca.getRegistry().getUser(enrollmentID, null);
        return caller;
    }

    public TcertKeyTree getTcertKeyTree(String caname) throws RAServerException {
        final CA ca = getCA(caname);
        return ca.getTcertKeyTree();
    }

    public TcertManager getTcertMgr(String caname) throws RAServerException {
        final CA ca = getCA(caname);
        return ca.getTcertMgr();
    }

    public void fillGettcertInfo(String caname, GettCertResponseNet resp, GettCertResponse tcertResponse) throws RAServerException {
        final CA ca = getCA(caname);
        ca.fillGettcertInfo(resp, tcertResponse);
    }

    public Certificate getEnrollmentCert(String caname, String enrollmentID) throws RAServerException {
        final CA ca = getCA(caname);
        return ca.loadCert(enrollmentID);
    }

    public void attributeIsTrue(String caName, String id, String attr) throws RAServerException {
        final CA ca = getCA(caName);
        ca.attributeIsTrue(id, attr);
    }
}
