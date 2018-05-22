package com.cfca.ra.ca.repository;

import com.cfca.ra.RAServerException;
import com.cfca.ra.utils.MyFileUtils;
import com.cfca.ra.utils.PemUtils;
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

/**
 * @author zhangchong
 * @create 2018/5/16
 * @Description
 * @CodeReviewer
 * @since v3.0.0
 */
public enum CertStore implements ICAStore{
    CFCA("CFCA") {
        private String getHomeDir() {
            String homeDir = String.join(File.separator, System.getProperty("user.dir"), caName);
            return MyFileUtils.getAbsFilePath(homeDir);
        }

        @Override
        public void storeCert(String username, String b64cert) throws RAServerException {
            if (StringUtils.isBlank(username)) {
                throw new RAServerException(RAServerException.REASON_CODE_CA_STORE_CERT_INVALID_ARGS, "ca[" + caName + "] fail to store cert to file, username is empty");
            }

            if (StringUtils.isBlank(b64cert)) {
                throw new RAServerException(RAServerException.REASON_CODE_CA_STORE_CERT_INVALID_ARGS, "ca[" + caName + "] fail to store cert to file, b64cert is empty");
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

        private String buildCertFile(String homeDir, String enrollmentId) {
            final String certDir = String.join(File.separator, homeDir, "certs");
            final String certFile = String.format("%s-cert.pem", enrollmentId);
            return String.join(File.separator, certDir, certFile);
        }

        @Override
        public Certificate loadCert(String enrollmentId) throws RAServerException {
            try {
                final String homeDir = getHomeDir();
                if (StringUtils.isBlank(enrollmentId)){
                    enrollmentId = "admin";
                }
                final String certFile = buildCertFile(homeDir, enrollmentId);
                return PemUtils.loadCert(certFile);
            } catch (IOException e) {
                throw new RAServerException(RAServerException.REASON_CODE_CA_LOAD_CERT, e);
            }
        }
    };
    protected static final Logger logger = LoggerFactory.getLogger(CertStore.class);
    protected final String caName;

    CertStore(String caName) {
        this.caName = caName;
    }
}
