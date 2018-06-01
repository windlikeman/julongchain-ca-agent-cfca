package demo;

import com.cfca.ra.command.CommandException;
import com.cfca.ra.command.config.ConfigBean;
import com.cfca.ra.command.config.CsrConfig;
import com.cfca.ra.command.internal.CsrResult;
import com.cfca.ra.command.internal.enroll.EnrollCommand;
import com.cfca.ra.command.internal.enroll.EnrollmentRequest;
import com.cfca.ra.command.utils.ConfigUtils;
import com.cfca.ra.command.utils.PemUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.PrivateKey;

import static demo.CsrUtils.storeMyPrivateKey;

public class EnrollCommandDemo {

    private EnrollCommandDemo(){

    }

    private ConfigBean loadConfigFile() throws CommandException {
        try {
            return ConfigUtils.load("ca-client/config/ca-client-config.yaml");
        } catch (Exception e) {
            throw new CommandException(CommandException.REASON_CODE_ENROLL_COMMAND_LOAD_CONFIG_FAILED, e);
        }
    }


    public static void main(String[] args) throws CommandException{
        final EnrollCommandDemo enrollCommandDemo = new EnrollCommandDemo();
        enrollCommandDemo.work();
    }

    private void work() throws CommandException {
        final ConfigBean configBean = loadConfigFile();
        String profile = configBean.getEnrollment().getProfile();
        CsrConfig csrConfig = configBean.getCsr();
        String caName = configBean.getCaname();

        final String algo = csrConfig.getKey().getAlgo();
        final String names = csrConfig.getNames();
        final CsrResult result = CsrUtils.genCSR(algo, names);
        storeMyPrivateKey(result);

        final EnrollmentRequest.Builder builder = new EnrollmentRequest.Builder(result.getCsr(), "admin", "1234", profile, csrConfig, caName);
        final EnrollmentRequest enrollmentRequest = builder.build();
        final EnrollCommand enrollCommand = new EnrollCommand();
        String[] args = new String[]{"enroll", "-h", "localhost", "-p", "8089", "-a", new Gson().toJson(enrollmentRequest)};
        enrollCommand.prepare(args);
        final JsonObject response = enrollCommand.execute();
        System.out.println(response);
    }

}