package org.bica.julongchain.cfca.ra.utils;

import org.bica.julongchain.cfca.ra.RAServerException;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.PrivateKey;
import java.text.SimpleDateFormat;
import java.util.Objects;

/**
 * @author zhangchong
 * @create 2018/5/28
 * @Description 证书工具类, 用于提取证书相关信息
 * @CodeReviewer helonglong
 * @since v3.0.0
 */
public class CertUtils {
    private static final Logger logger = LoggerFactory.getLogger(CertUtils.class);

    public static String getSubjectName(String b64cert) throws RAServerException {
        try {
            final ASN1Primitive asn1Primitive = ASN1Primitive.fromByteArray(Base64.decode(b64cert));
            final Certificate instance = Certificate.getInstance(asn1Primitive);
            final String s = instance.getSubject().toString();
            logger.info("getSubjectName>>>>>>" + s);
            return s;
        } catch (Exception e) {
            throw new RAServerException("cert utils fail to get subject name of cert", e);
        }
    }

    public static Certificate loadCert(final byte[] certDecoded) throws IOException {
        if (Objects.isNull(certDecoded) || 0 == certDecoded.length) {
            throw new IOException("failed to loadCert due to certDecoded bytes is empty");
        }
        final ASN1Primitive asn1Primitive = ASN1Primitive.fromByteArray(certDecoded);
        return Certificate.getInstance(asn1Primitive);
    }

    public static PrivateKey loadPrivateKey(final byte[] content) throws IOException {
        if (Objects.isNull(content) || 0 == content.length) {
            throw new IOException("failed to loadPrivateKey due to content bytes is empty");
        }
        final ASN1Primitive asn1Primitive = ASN1Primitive.fromByteArray(content);
        final PrivateKeyInfo info = PrivateKeyInfo.getInstance(asn1Primitive);
        return BouncyCastleProvider.getPrivateKey(info);
    }

    /**
     * 打印 Cert 信息,但是不打印 Cert Encoding 细节
     *
     * @param builder
     * @param cert
     * @return 打印用的 StringBuilder
     */
    public static StringBuilder dumpCert(final StringBuilder builder, Certificate cert) {

        if (cert != null) {
            final String separator = "\n";
            try {
                builder.append(separator);
                builder.append("\tSN: ").append(cert.getSerialNumber()).append(separator);
                builder.append("\tIssuer: ").append(cert.getIssuer()).append(separator);
                builder.append("\tSubject: ").append(cert.getSubject()).append(separator);
                builder.append("\tValidate: ").append(buildCertValidate(cert)).append(separator);
                builder.append("\tAlgorithm: ").append(cert.getSignatureAlgorithm().getAlgorithm()).append(separator);
                builder.append("\tcertBase64: ").append(new String(Base64.encode(cert.getEncoded()))).append(separator);

            } catch (Exception e) {
                builder.append("dumpCert<<<Failure : ").append(e.getMessage());
            }
        } else {
            builder.append("\tnone content");
        }
        return builder;
    }

    /**
     * validate: yyyy-MM-dd HH:mm:ss -- yyyy-MM-dd HH:mm:ss
     *
     * @param cert
     * @return
     */
    public static final String buildCertValidate(Certificate cert) {
        String validate = null;
        if (cert != null) {
            final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            validate = dateFormat.format(cert.getStartDate().getDate()) + " -- " + dateFormat.format(cert.getEndDate().getDate());
        }
        return validate;
    }
}
