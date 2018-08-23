package org.bica.julongchain.cfca.ra.command.utils;

import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.util.encoders.Base64;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.text.SimpleDateFormat;

import static org.junit.Assert.*;

/**
 * @author zhangchong
 * @Create 2018/7/15 11:50
 * @CodeReviewer
 * @Description
 * @since
 */
public class PemUtilsTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void loadCert() throws Exception{
        final Certificate certificate = PemUtils.loadCert("ca-client/config/msp/signcerts/cert.pem");
        final StringBuilder builder = new StringBuilder();
        PemUtils.dumpCert(builder,certificate);
        System.out.println(builder.toString());
    }
}