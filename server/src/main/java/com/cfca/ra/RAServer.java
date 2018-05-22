package com.cfca.ra;

import com.cfca.ra.ca.CA;
import com.cfca.ra.ca.CAConfig;
import com.cfca.ra.ca.CAInfo;
import com.cfca.ra.ca.DefaultUserRegistry;
import com.cfca.ra.utils.MyFileUtils;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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
 * @CodeReviewer
 * @since v3.0.0
 */
@Configuration
public class RAServer {

    private static final Logger logger = LoggerFactory.getLogger(RAServer.class);

    @Value("${server.ca.expiry}")
    private String caExpiry;

    @Value("${server.ca.pathlenzero}")
    private String caPathlenzero;

    @Value("${server.ca.pathlen}")
    private String caPathlen;

    /**
     * The home directory for the server
     */
    @Value("${server.homeDir}")
    private String serverHomeDir;

    /**
     * RAServer's default CA
     */
    private CA defaultCA;

    /**
     * A map of CAs stored by CA name as key
     */
    private Map<String, CA> caMap = new HashMap<>();

    private ServerRequestContext serverRequestContext;

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
        logger.info("Home directory for default CA: {}", defaultCA.getHomeDir());
    }

    public ServerRequestContext getServerRequestContext() {
        return serverRequestContext;
    }

    public void setServerRequestContext(final ServerRequestContext serverRequestContext) {
        this.serverRequestContext = serverRequestContext;
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
            entry = (Map.Entry<String, CA>) iter.next();
            value = entry.getValue();
            existedOne = value.getConfig().getCA();
            if (existedOne != null && caName.equals(existedOne.getName())) {
                message = String.format("CA name '%s' is used in '%s' and '%s'",
                        caName, adding.getConfigFilePath(), value.getConfigFilePath());
                throw new RAServerException(RAServerException.REASON_CODE_RA_SERVER_ADD_CA_EXCEPTION, message);
            }
            //FIXME: compareDN(existedOne.getCertfile(), addingOne.getCertfile())
        }
        caMap.put(caName, adding);

    }

    private CA initCA(final CAConfig caConfig) throws RAServerException {
        CAConfig local = caConfig;
        if (local == null) {
            final String name = "CFCA";
            String homeDir = String.join(File.separator, System.getProperty("user.dir"), name);
            homeDir = MyFileUtils.getAbsFilePath(homeDir);
            final CAInfo caInfo = new CAInfo.Builder(name, homeDir).build();
            local = new CAConfig.Builder(caInfo).build();
        }
        DefaultUserRegistry registry = new DefaultUserRegistry(local.getCA().getHomeDir());
        CA.Builder defaultCABuilder = new CA.Builder(this, local, registry);
        CA defaultCA = defaultCABuilder.build();

        logger.info("Init default CA with home {} and config {}", defaultCA.getHomeDir(), defaultCA.getConfig());
        return defaultCA;
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

    public void storeCert(final String caName, final String username, final String b64cert) throws RAServerException {
        final CA ca = getCA(caName);
        ca.storeCert(username, b64cert);
    }

    public PublicKey getKey(final String caName, final String enrollmentId) throws RAServerException {
        try {
            final CA ca = getCA(caName);
            final Certificate certificate = ca.loadCert(ca.getHomeDir(), enrollmentId);
            final SubjectPublicKeyInfo subjectPublicKeyInfo = certificate.getSubjectPublicKeyInfo();
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
            return converter.getPublicKey(subjectPublicKeyInfo);
        } catch (Exception e) {
            throw new RAServerException(RAServerException.REASON_CODE_RA_SERVER_GET_KEY_EXCEPTION, e);
        }
    }

    public void updateEnrollIdStore(final String caName, final String enrollmentID, final String id) throws RAServerException {
        final CA ca = getCA(caName);
        ca.updateEnrollIdStore(enrollmentID, id);
    }

    public String getEnrollmentId(final String caName, final String id) throws RAServerException {
        final CA ca = getCA(caName);
        return ca.getEnrollmentId(id);
    }

    public void checkIdRegistered(final String caname, final String id) throws RAServerException {
        final CA ca = getCA(caname);
        ca.checkIdRegistered(id);
    }
}
