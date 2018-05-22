package com.cfca.ra.utils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.util.ASN1Dump;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.bouncycastle.util.io.pem.PemWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Collections;

/**
 * @author zhangchong
 * @create 2018/5/17
 * @Description Pem文件工具类, 用于读取证书和密钥写入 pem 格式文件
 * @CodeReviewer
 * @since v3.0.0
 */
public class PemUtils {
    private static final Logger logger = LoggerFactory.getLogger(PemUtils.class);

    public static void storeCert(String certFile, byte[] data) throws IOException {
        try (FileOutputStream bOut = FileUtils.openOutputStream(new File(certFile));
             PemWriter pWrt = new PemWriter(new OutputStreamWriter(bOut))) {
            PemObject pemObj = new PemObject("CERTIFICATE", Collections.EMPTY_LIST, data);
            pWrt.writeObject(pemObj);
        }
    }

    public static Certificate loadCert(File file) throws IOException {
        if (!file.exists()) {
            throw new IOException("file [" + file.getAbsolutePath() + "] is not exist");
        }
        return loadCert(file.getAbsolutePath());
    }

    public static Certificate loadCert(String fileName) throws IOException {
        if (StringUtils.isBlank(fileName)) {
            throw new IOException("file name is blank");
        }
        final PemObject certObject = loadFile(fileName);
        final byte[] certDecoded = certObject.getContent();
        final ASN1Primitive asn1Primitive = ASN1Primitive.fromByteArray(certDecoded);
        logger.info("loadCert>>>>>>" + ASN1Dump.dumpAsString(asn1Primitive, true));
        return Certificate.getInstance(asn1Primitive);
    }

    private static PemObject loadFile(String fileName) throws IOException {
        if (StringUtils.isBlank(fileName)) {
            throw new IOException("pem loadFile fileName is blank");
        }
        File file = new File(fileName);
        if (!file.exists()) {
            throw new IOException("pem loadFile fileName[" + fileName + "] not exist");
        }
        try (final FileReader fileReader = new FileReader(fileName);
             PemReader p = new PemReader(fileReader)) {
            return p.readPemObject();
        }
    }
}
