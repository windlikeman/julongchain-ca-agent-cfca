package org.bica.julongchain.cfca.ra;

import org.apache.commons.lang.StringUtils;
import org.bica.julongchain.cfca.ra.ca.CA;
import org.bica.julongchain.cfca.ra.ca.CAConfig;
import org.bica.julongchain.cfca.ra.ca.CAInfo;
import org.bica.julongchain.cfca.ra.enroll.EnrollmentResponseNet;
import org.bica.julongchain.cfca.ra.getcainfo.GetCAInfoResponseNet;
import org.bica.julongchain.cfca.ra.register.IUser;
import org.bica.julongchain.cfca.ra.repository.MongodbCertStore;
import org.bica.julongchain.cfca.ra.repository.MongodbRegistryStore;
import org.bica.julongchain.cfca.ra.utils.CertUtils;
import org.bica.julongchain.cfca.ra.utils.MyFileUtils;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.openssl.PEMException;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

/**
 * @author zhangchong
 * @create 2018/5/16
 * @Description 服务器的信息
 * @CodeReviewer helonglong
 * @since v3.0.0
 */
@Configuration
public class RAServer {

    private static final Logger logger = LoggerFactory.getLogger(RAServer.class);
    private final MongodbCertStore mongodbCertStore;
    private final MongodbRegistryStore mongodbRegistryStore;

    /**
     * 服务器的工作目录
     */
    private String serverHomeDir;

    private final CAInfo caInfo;

    /**
     * RAServer's default CA
     */
    private CA defaultCA;

    /**
     * A map of CAs stored by CA name as key
     */
    private Map<String, CA> caMap = new HashMap<>();

    @Autowired
    public RAServer(final CAInfo caInfo, final MongodbCertStore mongodbCertStore ,
                    final MongodbRegistryStore mongodbRegistryStore)
            throws
            RAServerException {
        this.caInfo = caInfo;
        this.mongodbCertStore = mongodbCertStore;
        this.mongodbRegistryStore = mongodbRegistryStore;
        initialize();
    }

    public void initialize() throws RAServerException {
        initServerHomeDir();

        defaultCA = initCA(null);
        addCA(defaultCA);
    }

    public synchronized void initServerHomeDir() {
        if (StringUtils.isEmpty(serverHomeDir)) {
            this.serverHomeDir = System.getProperty("user.dir");
        }
        this.serverHomeDir = MyFileUtils.getAbsFilePath(serverHomeDir);
        logger.info("Initializing server in directory {}", serverHomeDir);
        final File file = new File(serverHomeDir);
        if (file.exists()){
            logger.warn("home directory already exist");
            return;
        }
        final boolean mkdirs = file.mkdirs();
        if (!mkdirs) {
            logger.warn("failed to init server with create home directory");
        }
    }

    public String getServerHomeDir() {
        return serverHomeDir;
    }

    public String getUserSecret(final String caName, final String user) throws RAServerException {
        return getCA(caName).getUserSecret(user);
    }

    private void addCA(final CA adding) throws RAServerException {
        if (Objects.isNull(adding.getConfig())) {
            throw new RAServerException( "the adding one get config is null");
        }
        final CAInfo addingOne = adding.getConfig().getCA();
        if (Objects.isNull(addingOne)) {
            throw new RAServerException( "the adding one get config cainfo is null");
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
                throw new RAServerException( message);
            }
        }
        caMap.put(caName, adding);
    }

    private CA initCA(final CAConfig caConfig) throws RAServerException {
        CAConfig local = caConfig;
        if (local == null) {
            final String name = caInfo.getName();
            logger.warn("RAServer@initCA : CaName={}", name);
            String homeDir = String.join(File.separator, serverHomeDir, name);
            homeDir = MyFileUtils.getAbsFilePath(homeDir);
            final File file = new File(homeDir);
            if (file.isDirectory()) {
                if (!file.exists()) {
                    final boolean mkdirs = file.mkdirs();
                    if (!mkdirs) {
                        logger.warn("failed to init CA with create CA home directory :{}", homeDir);
                        throw new RAServerException("RA Server failed to create CA due to fail to create home dir");
                    }
                }
            } else {
                logger.warn("RAServer@initCA : failed to init CA with create CA home directory due to homeDir not dir" +
                                " type:{}",
                        homeDir);
            }

            local = new CAConfig.Builder(caInfo, homeDir).build();
        }

        CA.Builder defaultCABuilder = new CA.Builder(this, local, mongodbRegistryStore)
                .certStore(mongodbCertStore);
        CA defaultCA = defaultCABuilder.build();

        logger.info("RAServer@initCA: Init default CA with home={} and config={}", defaultCA.getHomeDir(), defaultCA
                .getConfig());

        logger.info("RAServer@initCA : chainfile={}", caInfo.getChainfile());
        defaultCA.checkAndGetCAChain();

        return defaultCA;
    }

    public CA getCA(final String caName) throws RAServerException {
        if (StringUtils.isEmpty(caName)) {
            throw new RAServerException("RA Server failed to get CA with empty ca name");
        }
        // Lookup the CA from the server
        CA ca = caMap.getOrDefault(caName.toUpperCase(), null);
        if (ca == null) {
            String message = String.format("CA '%s' does not exist", caName);
            throw new RAServerException( message);
        }
        return ca;
    }

    public void storeCert(final String caName, final String enrollmentID, final String b64cert, String serialNo) throws RAServerException {
        final CA ca = getCA(caName);
        ca.storeCert(enrollmentID, b64cert, serialNo);
    }

    public void revokeCert(String caName, String serialNo) throws RAServerException {
        final CA ca = getCA(caName);
        ca.revokeCert(serialNo);
    }

    public PublicKey getPublicKey(final String caName, final String enrollmentId) throws RAServerException {
        Certificate certificate = null;
        try {
            final CA ca = getCA(caName);
            certificate = ca.loadCert(enrollmentId);
            final SubjectPublicKeyInfo subjectPublicKeyInfo = certificate.getSubjectPublicKeyInfo();
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
            return converter.getPublicKey(subjectPublicKeyInfo);
        } catch (PEMException e) {
            logger.error("RAServer@getPublicKey : failed to get public key from certificate => " +
                    "caName={}, enrollmentId={}, certificate={} ",caName, enrollmentId,
                    CertUtils.dumpCert(new StringBuilder(), certificate));
            throw new RAServerException("RA Server failed to register service due to invalid token", e);
        }
    }

    public void checkIdRegistered(final String caname, final String id) throws RAServerException {
        final CA ca = getCA(caname);
        ca.checkIdRegistered(id);
    }

    public void fillCAInfo(String caname, GetCAInfoResponseNet resp) throws RAServerException {
        final CA ca = getCA(caname);
        ca.fillCAInfo(resp);
    }

    public void fillCAInfo(String caname, EnrollmentResponseNet resp, String enrollmentID) throws RAServerException {
        final CA ca = getCA(caname);
        ca.fillCAInfo(resp, enrollmentID);
    }

    public IUser getUser(String caname, String enrollmentID, String[] attrs) throws RAServerException {
        final CA ca = getCA(caname);
        IUser caller = ca.getRegistry().getUser(enrollmentID, null);
        return caller;
    }

    public void attributeIsTrue(String caName, String id, String attr) throws RAServerException {
        final CA ca = getCA(caName);
        ca.attributeIsTrue(id, attr);
    }

}
