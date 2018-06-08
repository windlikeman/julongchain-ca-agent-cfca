package demo;

import com.cfca.ra.command.CommandException;
import com.cfca.ra.command.config.ConfigBean;
import com.cfca.ra.command.config.CsrConfig;
import com.cfca.ra.command.internal.CsrResult;
import com.cfca.ra.command.internal.enroll.EnrollCommand;
import com.cfca.ra.command.internal.enroll.EnrollmentRequest;
import com.cfca.ra.command.utils.ConfigUtils;
import com.cfca.ra.command.utils.CsrUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class EnrollCommandDemo {

    private EnrollCommandDemo() {

    }

    private static ConfigBean loadConfigFile() throws CommandException {
        try {
            return ConfigUtils.load("ca-client/config/ca-client-config.yaml");
        } catch (Exception e) {
            throw new CommandException(CommandException.REASON_CODE_ENROLL_COMMAND_LOAD_CONFIG_FAILED, e);
        }
    }


    public static void main(String[] args) throws CommandException {
        final ConfigBean configBean = loadConfigFile();
        String profile = configBean.getEnrollment().getProfile();
        CsrConfig csrConfig = configBean.getCsr();
        String caName = configBean.getCaname();

        final String username = "test9";
        final String password = "dGVzdDk6MTIzNA==";

        final String algo = csrConfig.getKey().getAlgo();
        final String names = csrConfig.getNames();
        final CsrResult result = CsrUtils.genCSR(algo, names);
        CsrUtils.storeMyPrivateKey(result, username);

        final EnrollmentRequest.Builder builder = new EnrollmentRequest.Builder(result.getCsr(), username, password, profile, csrConfig, caName);
        final EnrollmentRequest enrollmentRequest = builder.build();
        final EnrollCommand enrollCommand = new EnrollCommand();
        String[] args1 = new String[]{"enroll", "-h", "localhost", "-p", "8089", "-a", new Gson().toJson(enrollmentRequest)};
        enrollCommand.prepare(args1);
        final JsonObject response = enrollCommand.execute();
        System.out.println(response);
    }

}