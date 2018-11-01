package org.bica.julongchain.cfca.ra.utils;

import org.bouncycastle.asn1.x509.Certificate;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Base64;

import static org.junit.Assert.*;

/**
 * @author zhangchong
 * @Create 2018/7/15 11:44
 * @CodeReviewer
 * @Description
 * @since
 */
public class CertUtilsTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void dumpCert() throws Exception {
        String s =
                "MIIB6zCCAY+gAwIBAgIIEAAAAYZSeWUwDAYIKoEcz1UBg3UFADAsMQswCQYDVQQGEwJDTjEdMBsGA1UEAwwUVEVTVCBST09UIFNNMiBDQTIxNzAwHhcNMTgwNzE1MDMyNDQ2WhcNMjAwNzE1MDMyNDQ2WjB2MQswCQYDVQQGEwJDTjEXMBUGA1UECgwOQ0ZDQSBURVNUIE9DQTExETAPBgNVBAsMCExvY2FsIFJBMRUwEwYDVQQLDAxJbmRpdmlkdWFsLTExJDAiBgNVBAMMGzA1MUBhZG1pbkBaSDA5MzU4MDI4QDUxNjMzNjBZMBMGByqGSM49AgEGCCqBHM9VAYItA0IABBHCuF9uD/b/oNuYdW1j6fYrf0D8KeU8AgGJ6ZMXCcZpdC2OzeMIog3LhxOzRqdHZRt0T6BkMmvtLpPJ9lmX+j6jTzBNMB8GA1UdIwQYMBaAFACQCuvvo4oRDRbCWCXtDieQ7RgnMAsGA1UdDwQEAwIE8DAdBgNVHQ4EFgQUuUEeGxZ64tRhiZCk5E3EU2YLTdcwDAYIKoEcz1UBg3UFAANIADBFAiAQVo3PzvUoINEUwDAYe+H8dFucAxNnY9cSiowfGO3usgIhAOrxhKO1cgAOHzsPBhHDh6rSBPdpGiXenBWyyTCA67Qv";
        final byte[] decode = org.bouncycastle.util.encoders.Base64.decode(s);
        final StringBuilder builder = new StringBuilder();
        CertUtils.dumpCert(builder,Certificate.getInstance(decode));
        System.out.println(builder.toString());
    }
}