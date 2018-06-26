package demo;

import com.cfca.ra.command.CommandException;
import com.cfca.ra.command.config.ConfigBean;
import com.cfca.ra.command.config.CsrConfig;
import com.cfca.ra.command.internal.CsrResult;
import com.cfca.ra.command.internal.reenroll.ReenrollCommand;
import com.cfca.ra.command.internal.reenroll.ReenrollmentRequest;
import com.cfca.ra.command.utils.ConfigUtils;
import com.cfca.ra.command.utils.CsrUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.FileUtils;

import java.io.File;

public class ReenrollCommandDemo {

    private ConfigBean loadConfigFile() throws CommandException {
        try {
            return ConfigUtils.load("ca-client/config/ca-client-config.yaml");
        } catch (Exception e) {
            throw new CommandException(CommandException.REASON_CODE_ENROLL_COMMAND_LOAD_CONFIG_FAILED, e);
        }
    }

    public static void main(String[] args) throws Exception {
        final ReenrollCommandDemo reenrollCommandDemo = new ReenrollCommandDemo();
        reenrollCommandDemo.testReenroll();
    }

    private void testReenroll() throws Exception {
        final ConfigBean configBean = loadConfigFile();
        String profile = configBean.getEnrollment().getProfile();
        CsrConfig csrConfig = configBean.getCsr();
        String caName = configBean.getCaname();
        //"test", "dGVzdDoxMjM0"// "test2":"dGVzdDI6MTIzNA=="//"zc": "emM6MTIzNA=="

        final String algo = csrConfig.getKey().getAlgo();
        final String names = csrConfig.getNames();
        final CsrResult result = CsrUtils.genCSR(algo, names);
        final String keyDir = "D:\\R15\\P1552\\dev\\blockchain\\command\\ca-client\\config\\msp\\tmp\\keystore";
        final String keyFile = "key.pem";

        CsrUtils.storePrivateKey(result, keyDir, keyFile);
        final String csr = result.getCsr();
        System.out.println("CSR=" + csr);
        final String username = "admin";
        final String password = "1234";
        final ReenrollmentRequest.Builder builder = new ReenrollmentRequest.Builder(csr, username, password, profile, csrConfig, caName);
        final ReenrollmentRequest reenrollmentRequest = builder.build();
        final Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        final String jsonFile = "TestData/reenroll.json";
        final String request = gson.toJson(reenrollmentRequest);
        System.out.println("request=" + request);
        FileUtils.writeStringToFile(new File(jsonFile), request);

        final String keyFilePath = String.join(File.separator, keyDir, keyFile);
        String[] args = new String[]{"reenroll", "-h", "localhost", "-p", "8089", "-a", jsonFile, "-key", keyFilePath};
        ReenrollCommand reenrollCommand = new ReenrollCommand();
        reenrollCommand.prepare(args);
        reenrollCommand.execute();
    }

}