package com.cfca.ra.command.internal.gettcert;

import com.cfca.ra.command.utils.PemUtils;
import org.bouncycastle.asn1.x509.Certificate;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangchong
 * @create 2018/5/11
 * @Description 测试gettcert命令 暂不支持
 * @CodeReviewer helonglong
 * @since v3.0.0
 * @deprecated
 */
public class GettCertCommandTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void execute() throws Exception{

        Certificate ecert = PemUtils.loadCert("Testdata/test2-cert.pem");
        int count = 2;
        boolean encryptAttrs = true;
        String preKey = "anotherprekey";
        //        batchReq.Attrs = Attrs;
        List<Attribute> attrs = new ArrayList<>(2);
        attrs.add(new Attribute("SSN", "123-456-789", false));
        attrs.add(new Attribute("Income", "USD", false));

        GettCertRequest.Builder builder = new GettCertRequest.Builder(attrs,encryptAttrs,"CFCA",count, preKey);
        final GettCertRequest req = builder.build();
//        final TcertManager mgr = new TcertManager(caKey, caCert, validityPeriod, maxAllowedBatchSize);
//        GettCertResponse resp = mgr.getBatch(req, ecert);
    }
}