package org.bica.julongchain.cfca.ra.repository;

import org.apache.commons.lang.StringUtils;
import org.bica.julongchain.cfca.ra.RAServerException;
import org.bica.julongchain.cfca.ra.client.RAClientUtil;
import org.bica.julongchain.cfca.ra.po.EnrollCertPo;
import org.bica.julongchain.cfca.ra.utils.CertUtils;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.util.encoders.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;

/**
 * @author zhangchong
 * @Create 2018/7/13 17:04
 * @CodeReviewer
 * @Description
 * @since
 */
@Component
public class MongodbCertStore implements ICACertStore {
    private static final Logger logger = LoggerFactory.getLogger(MongodbCertStore.class);
    private final EnrollCertRepository enrollCertRepository;

    @Autowired
    public MongodbCertStore(final EnrollCertRepository enrollCertRepository) {
        this.enrollCertRepository = enrollCertRepository;
    }


    @Override
    public void storeCert(final String caName, final String enrollmentId, final String b64cert, String serialNo) throws RAServerException {
        final long startTime = System.currentTimeMillis();
        try {
            if (StringUtils.isBlank(enrollmentId)) {
                throw new RAServerException("ca[" + caName + "] fail to store cert to file, username is empty");
            }

            if (StringUtils.isBlank(b64cert)) {
                throw new RAServerException("ca[" + caName + "] fail to store cert to file, b64cert is empty");
            }
            String id = String.format("%s-%s", caName, enrollmentId);
            int certStatus = 1;
            final EnrollCertPo insert = enrollCertRepository.insert(new EnrollCertPo(id, enrollmentId, caName, serialNo,
                    b64cert, certStatus));
            logger.info("MongodbCertStore@storeCert >>>>>> insert one successful : " + insert);
        } finally {
            final long runTime = System.currentTimeMillis() - startTime;
            if (runTime > RAClientUtil.warningTime) {
                logger.warn("MongodbCertStore@storeCert : runTime={}, caName={}, enrollmentId={}",
                        runTime, caName, enrollmentId);
            }
        }
    }

    @Override
    public void revokeCert(String caName, String serialNo) throws RAServerException {
        final long startTime = System.currentTimeMillis();
        try {
            if (StringUtils.isBlank(serialNo)) {
                throw new RAServerException("ca[" + caName + "] fail to revoke cert , serialNo is empty");
            }

            enrollCertRepository.deleteByCaNameAndSerialNo(caName, serialNo);
            logger.info("MongodbCertStore@storeCert : delete one successful ca={}, serialNo={}", caName, serialNo);
        } finally {
            final long runTime = System.currentTimeMillis() - startTime;
            if (runTime > RAClientUtil.warningTime) {
                logger.warn("MongodbCertStore@storeCert : runTime={}, caName={}, serialNo={}",
                        runTime, caName, serialNo);
            }
        }
    }

    @Override
    public Certificate loadCert(String caName, String enrollmentId) throws RAServerException {
        final long startTime = System.currentTimeMillis();
        try {
            if (StringUtils.isBlank(enrollmentId)) {
                throw new RAServerException("ca[" + caName + "] fail to loadCert, enrollmentId is empty");
            }

            if (StringUtils.isBlank(caName)) {
                throw new RAServerException("ca[" + caName + "] fail to loadCert, caName is empty");
            }
            String id = String.format("%s-%s", caName, enrollmentId);

            final EnrollCertPo enrollCertPo = enrollCertRepository.findById(id);
            final Certificate instance;
            try {
                if (Objects.isNull(enrollCertPo)) {
                    throw new RAServerException("not to find cert[" + enrollmentId + "] " +
                            "in ca[" + caName + "]");
                }
                instance = CertUtils.loadCert(Base64.decode(enrollCertPo.getB64Cert()));
                logger.info("MongodbCertStore@loadCert : cert=\n" + dumpCert(instance));
                return instance;
            } catch (IOException e) {
                throw new RAServerException("fail to load cert[" + enrollmentId + "] in ca[" + caName + "]", e);
            }
        } finally {
            final long runTime = System.currentTimeMillis() - startTime;
            if (runTime > RAClientUtil.warningTime) {
                logger.warn("MongodbCertStore@loadCert : runTime={}, caName={}, enrollmentId={}",
                        runTime, caName, enrollmentId);
            }
        }

    }

    private String dumpCert(Certificate instance) {
        final StringBuilder builder = new StringBuilder();
        builder.append("Subject=").append(instance.getSubject()).append("\n");
        builder.append("Issuer=").append(instance.getIssuer()).append("\n");
        builder.append("SerialNumber=").append(instance.getSerialNumber()).append("\n");
        builder.append("Version=").append(instance.getVersion()).append("\n");
        builder.append("EndDate=").append(instance.getEndDate()).append("\n");
        builder.append("StartDate=").append(instance.getStartDate()).append("\n");
        builder.append("Signature=").append(instance.getSignature()).append("\n");
        builder.append("SignatureAlgorithm=").append(instance.getSignatureAlgorithm()).append("\n");
        return builder.toString();
    }

    @Override
    public String getCertFilePath(String caName, String serial) throws RAServerException {
        return null;
    }

}
