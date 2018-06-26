package com.cfca.ra.ca;

import java.io.FileInputStream;
import java.security.Key;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.cfca.ra.utils.PemUtils;

public class TcertManagerTest {

    private TcertManager tcertManager;

    @Before
    public void setUp() throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        PrivateKey caKey = PemUtils.loadPrivateKey("TestData/key.pem");
        Certificate caCert = PemUtils.loadCert("TestData/cert.pem");
        tcertManager = new TcertManager.Builder(caKey, caCert).builder();
    }

    @After
    public void tearDown() throws Exception {
        Security.removeProvider("BC");
    }

    @Test
    public void testCert() throws Exception {
        try (FileInputStream fileInputStream = new FileInputStream("TestData/test.cer")) {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            X509Certificate x509Certificate = (X509Certificate) certificateFactory.generateCertificate(fileInputStream);
            Certificate enrollmentCert = Certificate.getInstance(x509Certificate.getEncoded());
            tcertManager.buildPaddingEnrollmentId(enrollmentCert);
        } catch (Exception ignore) {

        }
    }

    @Test
    public void testBuildPaddingEnrollmentId() throws Exception {

        Certificate enrollmentCert = PemUtils.loadCert("TestData/cert-test.pem");
        final byte[] bytes = tcertManager.buildPaddingEnrollmentId(enrollmentCert);

        StringBuilder builder = new StringBuilder(100);
        for (int i = 0; i < bytes.length; i++) {
            if (i > 0) {
                builder.append(",");
            }
            builder.append("0x").append(Hex.toHexString(new byte[] { bytes[i] }));
        }
        final String s = builder.toString();
        Assert.assertTrue("0x43,0x3d,0x43,0x4e,0x2c,0x4f,0x3d,0x43,0x46,0x43,0x41,0x20,0x54,0x45,0x53,0x54,0x20,0x4f,0x43,0x41,0x31,0x2c,0x4f,0x55,0x3d,0x4c,0x6f,0x63,0x61,0x6c,0x20,0x52,0x41,0x2c,0x4f,0x55,0x3d,0x49,0x6e,0x64,0x69,0x76,0x69,0x64,0x75,0x61,0x6c,0x2d,0x31,0x2c,0x43,0x4e,0x3d,0x30,0x35,0x31,0x40,0x7a,0x63,0x38,0x40,0x5a,0x48,0x30,0x39,0x33,0x35,0x38,0x30,0x32,0x38,0x40,0x33,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff"
                .equals(s));

    }

    @Test
    public void test1() throws Exception {
        Certificate ecert = PemUtils.loadCert("TestData/cert-test.pem");
        byte[] kdfKey = tcertManager.generateKdfKey(ecert);
        Key extKey = tcertManager.generateExtKey(kdfKey);
        byte[] src = "hello".getBytes();
        final byte[] pkcs7Encrypt = tcertManager.cbcPKCS7Encrypt(extKey, src);

        final byte[] cbcPKCS7Decrypt = tcertManager.cbcPKCS7Decrypt(extKey, pkcs7Encrypt);
        final String x = new String(cbcPKCS7Decrypt);
        System.out.println(x);
        Assert.assertTrue(x.equals("hello"));
    }

    @Test
    public void test2() throws Exception {
        Key extKey = tcertManager.generateHKdfKey("12322322".getBytes());
        byte[] src = "hello".getBytes();
        final byte[] pkcs7Encrypt = tcertManager.cbcPKCS7Encrypt(extKey, src);

        final byte[] cbcPKCS7Decrypt = tcertManager.cbcPKCS7Decrypt(extKey, pkcs7Encrypt);
        final String x = new String(cbcPKCS7Decrypt);
        System.out.println(x);
        Assert.assertTrue(x.equals("hello"));
    }
}