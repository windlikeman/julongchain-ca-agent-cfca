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
        //"test", "dGVzdDoxMjM0"// "test2":"dGVzdDI6MTIzNA=="

        final String algo = csrConfig.getKey().getAlgo();
        final String names = csrConfig.getNames();
        final CsrResult result = CsrUtils.genCSR(algo, names);

        final String username = "test9";
        final String password = "dGVzdDk6MTIzNA==";
        final ReenrollmentRequest.Builder builder = new ReenrollmentRequest.Builder(result.getCsr(), username, password, profile, csrConfig, caName);
        final ReenrollmentRequest reenrollmentRequest = builder.build();
        String[] args = new String[]{"reenroll", "-h", "localhost", "-p", "8089", "-a", new Gson().toJson(reenrollmentRequest)};
        ReenrollCommand reenrollCommand = new ReenrollCommand();
        reenrollCommand.prepare(args);
        reenrollCommand.execute();

        CsrUtils.storeMyPrivateKey(result, username);
    }

}