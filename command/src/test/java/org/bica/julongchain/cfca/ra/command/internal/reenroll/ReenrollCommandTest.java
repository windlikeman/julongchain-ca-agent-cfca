package org.bica.julongchain.cfca.ra.command.internal.reenroll;

import org.bica.julongchain.cfca.ra.command.CommandException;
import org.bica.julongchain.cfca.ra.command.config.ConfigBean;
import org.bica.julongchain.cfca.ra.command.config.CsrConfig;
import org.bica.julongchain.cfca.ra.command.internal.CsrResult;
import org.bica.julongchain.cfca.ra.command.utils.ConfigUtils;
import org.bica.julongchain.cfca.ra.command.utils.CsrUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.FileUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.security.Security;

/**
 * @author zhangchong
 * @create 2018/5/11
 * @Description 测试 reenroll 命令
 * @CodeReviewer helonglong
 * @since v3.0.0
 */
public class ReenrollCommandTest {

    private ReenrollCommand reenrollCommand;
    private BouncyCastleProvider provider;

    @Before
    public void setUp() throws Exception {
        reenrollCommand = new ReenrollCommand();
        this.provider = new BouncyCastleProvider();
        Security.addProvider(provider);
    }

    @After
    public void tearDown() throws Exception {
    }

    private ConfigBean loadConfigFile() throws CommandException {
        try {
            return ConfigUtils.load("ca-client/config/ca-client-config.yaml");
        } catch (Exception e) {
            throw new CommandException("the reenrollment command failed to initiallize with config file", e);
        }
    }

    @Test
    public void testReenroll() throws Exception {
        final ConfigBean configBean = loadConfigFile();
        CsrConfig csrConfig = configBean.getCsr();
        String caName = configBean.getCaname();

        final String algo = csrConfig.getKey().getAlgo();
        final String names = csrConfig.getNames();
        final CsrResult result = CsrUtils.genCSR(algo, names);
        final String keyDir = "ca-client\\config\\msp\\tmp\\keystore";
        final String keyFile = "key.pem";

        CsrUtils.storePrivateKey(result, keyDir, keyFile);
        final String csr = result.getCsr();
        System.out.println("CSR=" + csr);
        final String username = "admin";
        final String password = "1234";
        final ReenrollmentRequest.Builder builder = new ReenrollmentRequest.Builder(csr, username, password, null,
                csrConfig, caName);
        final ReenrollmentRequest reenrollmentRequest = builder.build();
        final Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        final String jsonFile = "TestData/reenroll.json";
        final String request = gson.toJson(reenrollmentRequest);
        FileUtils.writeStringToFile(new File(jsonFile), request);

        final String keyFilePath = String.join(File.separator, keyDir, keyFile);
        String[] args = new String[]{"reenroll", "-h", "localhost", "-p", "8089", "-a", jsonFile, "-key", keyFilePath};
        ReenrollCommand reenrollCommand = new ReenrollCommand();
        reenrollCommand.prepare(args);
        reenrollCommand.execute();
    }
}