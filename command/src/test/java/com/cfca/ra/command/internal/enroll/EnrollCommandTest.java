package com.cfca.ra.command.internal.enroll;

import com.cfca.ra.command.CommandException;
import com.cfca.ra.command.config.ConfigBean;
import com.cfca.ra.command.config.CsrConfig;
import com.cfca.ra.command.internal.CsrResult;
import com.cfca.ra.command.utils.ConfigUtils;
import com.cfca.ra.command.utils.CsrUtils;
import com.cfca.ra.command.utils.MyStringUtils;
import com.cfca.ra.command.utils.PemUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.apache.commons.io.FileUtils;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveGenParameterSpec;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;
import org.bouncycastle.util.encoders.Base64;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.Security;
import java.security.spec.AlgorithmParameterSpec;
/**
 * @author zhangchong
 * @create 2018/5/11
 * @Description 测试enroll命令
 * @CodeReviewer helonglong
 * @since v3.0.0
 */
public class EnrollCommandTest {

    private EnrollCommand enrollCommand;
    private BouncyCastleProvider provider;

    @Before
    public void setUp() throws Exception {
        enrollCommand = new EnrollCommand();
        this.provider = new BouncyCastleProvider();
        Security.addProvider(provider);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testParseRawurlOK() throws Exception {
        String s1 = "http://<enrollmentID>:<secret>@ip:port";
        final ParsedUrl parsedUrl = enrollCommand.parseRawurl(s1);
        System.out.println(parsedUrl);
    }

    @Test
    public void testBuildEnrollment() {
        String s = "{}";
        final EnrollmentRequest enrollmentRequest = new Gson().fromJson(s, EnrollmentRequest.class);
        System.out.println(enrollmentRequest.toString());
        Assert.assertTrue(enrollmentRequest.isNull());
    }

    private ConfigBean loadConfigFile() throws CommandException {
        try {
            return ConfigUtils.load("ca-client/config/ca-client-config.yaml");
        } catch (Exception e) {
            throw new CommandException(CommandException.REASON_CODE_ENROLL_COMMAND_LOAD_CONFIG_FAILED, e);
        }
    }

    @Test
    public void testEnrollment() throws Exception {
        final ConfigBean configBean = loadConfigFile();
        String profile = configBean.getEnrollment().getProfile();
        CsrConfig csrConfig = configBean.getCsr();
        String caName = configBean.getCaname();

//        final String username = "admin";
//        final String password = "1234";
        final String username = "test4";
        final String password = "1234";
        final String algo = csrConfig.getKey().getAlgo();
        final String names = csrConfig.getNames();
        final CsrResult result = CsrUtils.genCSR(algo, names);
        CsrUtils.storeMyPrivateKey(result, username);

        final EnrollmentRequest.Builder builder = new EnrollmentRequest.Builder(result.getCsr(), username, password, profile, csrConfig, caName);
        final EnrollmentRequest enrollmentRequest = builder.build();
        final Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        final String jsonFile = "TestData/enroll.json";
        FileUtils.writeStringToFile(new File(jsonFile), gson.toJson(enrollmentRequest));

        final EnrollCommand enrollCommand = new EnrollCommand();
        String[] args1 = new String[]{"enroll", "-h", "localhost", "-p", "8089", "-a", jsonFile};
        enrollCommand.prepare(args1);
        final JsonObject response = enrollCommand.execute();
        System.out.println(response);
    }

}