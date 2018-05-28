package com.cfca.ra.repository;

import com.cfca.ra.RAServerException;
import com.cfca.ra.utils.MyFileUtils;
import com.cfca.ra.utils.PemUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.EmptyFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.lang.StringUtils;
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
import java.util.Collection;

/**
 * @author zhangchong
 * @create 2018/5/16
 * @Description CA 中的证书数据库管理类
 * @CodeReviewer
 * @since v3.0.0
 */
public enum CertCertStore implements ICACertStore {
    CFCA("CFCA") {
        private String getHomeDir() {
            String homeDir = String.join(File.separator, System.getProperty("user.dir"), caName);
            return MyFileUtils.getAbsFilePath(homeDir);
        }

        @Override
        public void storeCert(String enrollmentID, String b64cert) throws RAServerException {
            if (StringUtils.isBlank(enrollmentID)) {
                throw new RAServerException(RAServerException.REASON_CODE_CA_CERT_STORE_CERT_INVALID_ARGS, "ca[" + caName + "] fail to store cert to file, username is empty");
            }

            if (StringUtils.isBlank(b64cert)) {
                throw new RAServerException(RAServerException.REASON_CODE_CA_CERT_STORE_CERT_INVALID_ARGS, "ca[" + caName + "] fail to store cert to file, b64cert is empty");
            }

            byte[] decode = null;
            try {
                final String homeDir = getHomeDir();
                final String certB64File = buildCertB64File(homeDir, enrollmentID);
                FileUtils.writeStringToFile(new File(certB64File), b64cert);
                logger.info("storeCert<<<<<< store cert b64 file to :" + certB64File);
                decode = Base64.decode(b64cert);

                final String certFile = buildCertFile(homeDir, enrollmentID);
                logger.info("storeCert<<<<<< store cert pem file to :" + certFile);
                PemUtils.storeCert(certFile, decode);
            } catch (DecoderException e) {
                final String msg = "storeCert>>>>>> Failure cert:\n" + Hex.toHexString(decode) + "\nfail to decode b64";
                throw new RAServerException(RAServerException.REASON_CODE_CA_CERT_STORE_CERT_B64_DECODE, "fail to store cert to file due to b64 decode :" + msg, e);
            } catch (IOException e) {
                final String cert = dumpCertASN1(decode);
                throw new RAServerException(RAServerException.REASON_CODE_CA_CERT_STORE_CERT_WITH_PEM, "fail to store cert to file due to process pem file , cert:\n" + cert, e);
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

        private String buildCertFile(String homeDir, String enrollmentId) {
            final String certDir = String.join(File.separator, homeDir, "certs");
            final String certFile = String.format("%s-cert.pem", enrollmentId);
            return String.join(File.separator, certDir, certFile);
        }

        private String buildCertB64File(String homeDir, String enrollmentId) {
            final String certDir = String.join(File.separator, homeDir, "certs");
            final String certFile = String.format("%s-b64cert.cer", enrollmentId);
            return String.join(File.separator, certDir, certFile);
        }

        @Override
        public Certificate loadCert(String enrollmentId) throws RAServerException {
            try {
                final String homeDir = getHomeDir();
                if (StringUtils.isBlank(enrollmentId)) {
                    throw new RAServerException(RAServerException.REASON_CODE_CA_CERT_STORE_LOAD_CERT, "enrollmentId is blank");
                }
                final String certFile = buildCertFile(homeDir, enrollmentId);
                return PemUtils.loadCert(certFile);
            } catch (IOException e) {
                throw new RAServerException(RAServerException.REASON_CODE_CA_CERT_STORE_LOAD_CERT, e);
            }
        }

        @Override
        public String getCertFilePath(String serial) throws RAServerException {
            if (StringUtils.isBlank(serial)) {
                throw new RAServerException(RAServerException.REASON_CODE_CA_CERT_STORE_GET_CERT_FILE_PATH, "serial is blank");
            }
            final String homeDir = getHomeDir();
            final String certDir = String.join(File.separator, homeDir, "certs");
            final Collection<File> listFiles = FileUtils.listFiles(new File(certDir), new String[]{"pem"}, false);
            Certificate certificate;
            String serialNumber;
            for (File file : listFiles) {
                logger.info(file.getName());
                try {
                    certificate = PemUtils.loadCert(file);
                    serialNumber = getStringSerialNumber(certificate);
                    if (serialNumber.equalsIgnoreCase(serial)) {
                        final String absolutePath = file.getAbsolutePath();
                        logger.info("found cert file at: {}", absolutePath);
                        return absolutePath;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        public boolean containsCert(String enrollmentId) throws RAServerException {
            if (StringUtils.isBlank(enrollmentId)) {
                throw new RAServerException(RAServerException.REASON_CODE_CA_CERT_STORE_CONTAINS_CERT, "enrollmentId is blank");
            }
            final String homeDir = getHomeDir();
            final String certDir = String.join(File.separator, homeDir, "certs");
            final String certFile = String.format("%s-b64cert.cer", enrollmentId);
            Collection listFiles = FileUtils.listFiles(new File(certDir),
                    FileFilterUtils.andFileFilter(EmptyFileFilter.NOT_EMPTY, new NameFileFilter(certFile, IOCase.SENSITIVE)),
                    DirectoryFileFilter.INSTANCE);

            return listFiles.size() > 0;
        }

        @Override
        public String loadB64CertString(String enrollmentId) throws RAServerException {
            try {
                final String homeDir = getHomeDir();
                final String certDir = String.join(File.separator, homeDir, "certs");
                final String certFile = String.format("%s-b64cert.cer", enrollmentId);
                final String certFilePath = String.join(File.separator, certDir, certFile);
                return FileUtils.readFileToString(new File(certFilePath));
            } catch (Exception e) {
                throw new RAServerException(RAServerException.REASON_CODE_CA_CERT_STORE_LOAD_B64_CERT_STRING, e);
            }
        }

        private String getStringSerialNumber(Certificate certificate) {
            byte[] snData = certificate.getSerialNumber().getPositiveValue().toByteArray();
            if (snData != null) {
                int length = snData.length;
                StringBuffer buf = new StringBuffer();
                for (int i = 0; i != length; i++) {
                    int v = snData[i] & 0xff;
                    buf.append(DIGITS.charAt(v >>> 4));
                    buf.append(DIGITS.charAt(v & 0xf));
                }
                return buf.toString();
            } else {
                return "";
            }
        }
    };
    protected static final Logger logger = LoggerFactory.getLogger(CertCertStore.class);
    protected final String caName;
    protected final String DIGITS = "0123456789ABCDEF";

    CertCertStore(String caName) {
        this.caName = caName;
    }
}
