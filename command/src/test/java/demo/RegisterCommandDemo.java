package demo;

import org.bica.julongchain.cfca.ra.command.internal.register.RegisterCommand;
import org.bica.julongchain.cfca.ra.command.internal.register.RegistrationRequest;
import org.bica.julongchain.cfca.ra.command.internal.register.UserAttrs;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.ArrayList;

public class RegisterCommandDemo {


    public static void main(String[] args) throws Exception {
        final RegisterCommandDemo registerCommandDemo = new RegisterCommandDemo();
        registerCommandDemo.testRegister();
    }

    private void testRegister() throws Exception {
        final RegistrationRequest.Builder builder = new RegistrationRequest.Builder();
        final ArrayList<UserAttrs> v = new ArrayList<>();
        v.add(new UserAttrs("hf.Revoker", "true"));
        v.add(new UserAttrs("hf.Registrar.Roles", "client,user,peer,validator,auditor"));
        builder.name("zhangchong").caName("OCA").affiliation("org.department.c").attributes(v).maxEnrollments(2)
                .secret("abcde").type("user");
        final RegistrationRequest registrationRequest = builder.build();

        final String jsonFile = "TestData/register.json";
        final Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        final String request = gson.toJson(registrationRequest);
        System.out.println("request=" + request);
        FileUtils.writeStringToFile(new File(jsonFile), request);

        final RegisterCommand registerCommand = new RegisterCommand();
        String[] args = new String[]{"register", "-h", "localhost", "-p", "8089", "-a", jsonFile};
        registerCommand.prepare(args);
        registerCommand.execute();
    }
}