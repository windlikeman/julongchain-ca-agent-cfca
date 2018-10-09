package org.bica.julongchain.cfca.ra.command.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.security.PrivateKey;
import java.text.SimpleDateFormat;
import java.util.Collections;

import org.apache.commons.io.FileUtils;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.bouncycastle.util.io.pem.PemWriter;

/**
 * @author zhangchong
 * @create 2018/5/17
 * @Description Pem文件工具类, 用于读取证书和密钥写入 pem 格式文件
 * @CodeReviewer
 * @since v3.0.0
 */
public class PemUtils {

    public static void storePrivateKey(String resource, PrivateKey privateKey) throws Exception {
        try (FileOutputStream bOut = new FileOutputStream(resource); PemWriter pWrt = new PemWriter(new OutputStreamWriter(bOut));) {
            pWrt.writeObject(new PemObject("EC PRIVATE KEY", Collections.EMPTY_LIST, privateKey.getEncoded()));
        }
    }

    public static void storeCert(String certFile, byte[] data) throws IOException {
        try (FileOutputStream bOut = FileUtils.openOutputStream(new File(certFile)); PemWriter pWrt = new PemWriter(new OutputStreamWriter(bOut))) {
            PemObject pemObj = new PemObject("CERTIFICATE", Collections.EMPTY_LIST, data);
            pWrt.writeObject(pemObj);
        }
    }

    public static void storeCaChain(String chainFile, byte[] data) throws IOException {
        FileUtils.writeByteArrayToFile(new File(chainFile), data);
    }

    public static Certificate loadCert(String fileName) throws IOException {
        if (StringUtils.isBlank(fileName)) {
            throw new IOException();
        }
        final PemObject certObject = loadFile(fileName);
        final byte[] certDecoded = certObject.getContent();

        return Certificate.getInstance(certDecoded);
    }

    public static Certificate loadCert(byte[] content) throws IOException {
        if (null == content) {
            throw new IOException();
        }

        return Certificate.getInstance(content);
    }

    public static PrivateKey loadPrivateKey(String fileName) throws IOException {
        if (StringUtils.isBlank(fileName)) {
            throw new IOException("loadPrivateKey fileName is blank");
        }
        final PemObject certObject = loadFile(fileName);
        final byte[] content = certObject.getContent();
        
        final PrivateKeyInfo info = PrivateKeyInfo.getInstance(content);
        return BouncyCastleProvider.getPrivateKey(info);
    }

    private static PemObject loadFile(String fileName) throws IOException {
        if (StringUtils.isBlank(fileName)) {
            throw new IOException("loadFile fileName is blank");
        }
        File file = new File(fileName);
        if (!file.exists()) {
            throw new IOException("loadFile fileName[" + fileName + "] not exist");
        }
        try (final FileReader fileReader = new FileReader(fileName); PemReader p = new PemReader(fileReader)) {
            return p.readPemObject();
        }
    }

    public static byte[] loadFileContent(String fileName) throws IOException {
        if (StringUtils.isBlank(fileName)) {
            throw new IOException("loadFileContent fileName is blank");
        }
        final PemObject certObject = loadFile(fileName);
        return certObject.getContent();
    }

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
