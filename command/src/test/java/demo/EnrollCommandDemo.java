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
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

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

    public static void main(String[] args) throws Exception {
        final ConfigBean configBean = loadConfigFile();
        String profile = configBean.getEnrollment().getProfile();
        CsrConfig csrConfig = configBean.getCsr();
        String caName = configBean.getCaname();
        final String username = "admin";
        final String password = "1234";

        final String algo = csrConfig.getKey().getAlgo();
        final String names = csrConfig.getNames();
        final CsrResult result = CsrUtils.genCSR(algo, names);
        final String csr = result.getCsr();
        System.out.println("Csr=" + csr);
        CsrUtils.storeMyPrivateKey(result, username);

        final EnrollmentRequest.Builder builder = new EnrollmentRequest.Builder(csr, username, password, profile, csrConfig, caName);
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