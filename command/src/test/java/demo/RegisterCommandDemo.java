package demo;

import com.cfca.ra.command.internal.register.RegisterCommand;
import com.cfca.ra.command.internal.register.RegistrationRequest;
import com.cfca.ra.command.internal.register.UserAttrs;
import com.google.gson.Gson;

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
        builder.name("test9").caName("CFCA").affiliation("org.department.c").attributes(v).maxEnrollments(2).secret("1234").type("user");
        final RegistrationRequest registrationRequest = builder.build();
        final RegisterCommand registerCommand = new RegisterCommand();
        String[] args = new String[]{"register", "-h", "localhost", "-p", "8089", "-a", new Gson().toJson(registrationRequest)};
        registerCommand.prepare(args);
        registerCommand.execute();
    }
}