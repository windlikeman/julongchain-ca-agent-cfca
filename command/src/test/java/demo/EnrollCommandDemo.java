package demo;

import java.io.File;

import org.apache.commons.io.FileUtils;

import org.bica.julongchain.cfca.ra.command.CommandException;
import org.bica.julongchain.cfca.ra.command.config.ConfigBean;
import org.bica.julongchain.cfca.ra.command.config.CsrConfig;
import org.bica.julongchain.cfca.ra.command.internal.CsrResult;
import org.bica.julongchain.cfca.ra.command.internal.enroll.EnrollCommand;
import org.bica.julongchain.cfca.ra.command.internal.enroll.EnrollmentRequest;
import org.bica.julongchain.cfca.ra.command.utils.ConfigUtils;
import org.bica.julongchain.cfca.ra.command.utils.CsrUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

/**
 * @Author zhangchong
 * @Description EnrollCommandDemo
 * @create 2018/6/11
 * @CodeReviewer zhangqingan
 * @since v3.0.0.1
 */
public class EnrollCommandDemo {

    private EnrollCommandDemo() {

    }

    private static ConfigBean loadConfigFile() throws CommandException {
        try {
            return ConfigUtils.load("ca-client/config/ca-client-config.yaml");
        } catch (Exception e) {
            throw new CommandException("the enrollment command failed to initiallize with config file", e);
        }
    }

    /**
     * final String username = "zhangqingan";
     * final String password = "emhhbmdxaW5nYW46MTIzNA==";
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        final ConfigBean configBean = loadConfigFile();
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

        final EnrollmentRequest.Builder builder = new EnrollmentRequest.Builder(csr, username, password,
                null, csrConfig, caName);
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