package com.cfca.ra.command.internal.enroll;

import com.cfca.ra.command.CommandException;
import com.cfca.ra.command.config.ConfigBean;
import com.cfca.ra.command.config.CsrConfig;
import com.cfca.ra.command.internal.CsrResult;
import com.cfca.ra.command.utils.ConfigUtils;
import com.cfca.ra.command.utils.MyStringUtils;
import com.cfca.ra.command.utils.PemUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
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

public class EnrollCommandTest {

    private EnrollCommand enrollCommand;
    private BouncyCastleProvider provider;
    private String keyFile;

    private final static String MSPDIR = "D:\\R15\\P1552\\dev\\blockchain\\command\\ca-client\\config\\msp";

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
    public void testBuildEnrollment() throws Exception {
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

        final String algo = csrConfig.getKey().getAlgo();
        final String names = csrConfig.getNames();
        final CsrResult result = genCSR(algo, names);
        storeMyPrivateKey(result);

        final EnrollmentRequest.Builder builder = new EnrollmentRequest.Builder(result.getCsr(), "admin", "1234", profile, csrConfig, caName);
        final EnrollmentRequest enrollmentRequest = builder.build();
        final EnrollCommand enrollCommand = new EnrollCommand();
        String[] args = new String[]{"enroll", "-h", "localhost", "-p", "8089", "-a", new Gson().toJson(enrollmentRequest)};
        enrollCommand.prepare(args);
        final JsonObject response = enrollCommand.execute();
        System.out.println(response);
    }

    private void storeMyPrivateKey(CsrResult result) throws CommandException {
        try {
            initializeIfNeeded();
            final PrivateKey privateKey = result.getKeyPair().getPrivate();
            PemUtils.storePrivateKey(keyFile, privateKey);
            System.out.println("storeMyPrivateKey<<<<<<store private key at {"+keyFile+"}");
        } catch (Exception e) {
            throw new CommandException(CommandException.REASON_CODE_INTERNAL_CLIENT_STORE_PRIVATEKEY_FAILED, e);
        }
    }

    private void initializeIfNeeded() throws CommandException {
        try {
            // 密钥目录和文件
            String keyDir = String.join(File.separator, MSPDIR, "keystore");
            boolean mkdirs = new File(keyDir).mkdirs();
            if (!mkdirs) {
                System.out.println("initializeIfNeeded<<<<<<failed to create keystore directory");
            }
            keyFile = String.join(File.separator, keyDir, "key.pem");
            System.out.println("initializeIfNeeded<<<<<<use keyFile at "+ keyFile);
            // 证书目录和文件
        } catch (Exception e) {
            throw new CommandException(CommandException.REASON_CODE_INTERNAL_CLIENT_INIT_FAILED, "failed to init client", e);
        }
    }

    /**
     * @param keyAlg      签名算法名称
     * @param distictName 证书使用者 DN
     * @return CsrResult
     * @throws CommandException 失败时报错
     */
    private CsrResult genCSR(String keyAlg, String distictName) throws CommandException {
        if (MyStringUtils.isEmpty(keyAlg) || MyStringUtils.isEmpty(distictName)) {
            throw new CommandException(CommandException.REASON_CODE_INTERNAL_CLIENT_GENCSR_FAILED, "keyAlg or distictName is empty");
        }

        CsrResult result;
        switch (keyAlg.toUpperCase()) {
            case "SM2":
                result = getSM2CsrResult(distictName);
                break;
            default:
                throw new CommandException(CommandException.REASON_CODE_INTERNAL_CLIENT_GENCSR_FAILED, "Unsupport keyAlg type[" + keyAlg + "]");
        }

        return result;
    }

    private CsrResult getSM2CsrResult(String distictName) throws CommandException {
        final AlgorithmParameterSpec sm2p256v1 = new ECNamedCurveGenParameterSpec("sm2p256v1");
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("EC", provider);
            generator.initialize(sm2p256v1);
            KeyPair keypair = generator.generateKeyPair();
            System.out.println("getSM2CsrResult>>>>>>publicKey : " + keypair.getPublic());
            System.out.println("getSM2CsrResult>>>>>>privateKey : " + keypair.getPrivate());
            String csr = genSM2CSR(distictName, keypair);
            return new CsrResult(csr, keypair);
        } catch (Exception e) {
            throw new CommandException(CommandException.REASON_CODE_INTERNAL_CLIENT_GEN_SM2_CSR_FAILED, e);
        }
    }

    private String genSM2CSR(String distictName, KeyPair keypair) throws CommandException {

        try {
            if (MyStringUtils.isEmpty(distictName)) {
                throw new CommandException(CommandException.REASON_CODE_INTERNAL_CLIENT_GEN_SM2_CSR_FAILED, "distictName is empty");
            }

            PKCS10CertificationRequestBuilder pkcs10Builder = new JcaPKCS10CertificationRequestBuilder(
                    new X500Name(distictName),
                    keypair.getPublic());

            ContentSigner contentSigner = new JcaContentSignerBuilder("SM3WITHSM2").setProvider("BC").build(keypair.getPrivate());
            PKCS10CertificationRequest csr = pkcs10Builder.build(contentSigner);
            final byte[] base64Encoded = Base64.encode(csr.getEncoded());
            return new String(base64Encoded);
        } catch (CommandException e) {
            throw e;
        } catch (Exception e) {
            throw new CommandException(CommandException.REASON_CODE_INTERNAL_CLIENT_GEN_SM2_CSR_FAILED, e);
        }
    }
}