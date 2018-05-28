package com.cfca.ra.command.internal.register;

import com.google.gson.Gson;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

public class RegisterCommandTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void parseArgs() {
        final RegistrationRequest.Builder builder = new RegistrationRequest.Builder();
        final ArrayList<UserAttrs> v = new ArrayList<>();
        v.add(new UserAttrs("name", "zc"));
        builder.caName("CFCA").affiliation("a.b.c").attributes(v).maxEnrollments(2).secret("1234").type("user");
        final RegistrationRequest registrationRequest = builder.build();
        System.out.println(new Gson().toJson(registrationRequest));
        String s = "{\"type\":\"user\",\"secret\":\"1234\",\"maxEnrollments\":2,\"affiliation\":\"a.b.c\",\"attributes\":[{\"name\":\"name\",\"value\":\"zc\"}],\"caName\":\"CFCA\"}";
    }

    @Test
    public void testRegister() throws Exception {
        final RegistrationRequest.Builder builder = new RegistrationRequest.Builder();
        final ArrayList<UserAttrs> v = new ArrayList<>();
        v.add(new UserAttrs("hf.Revoker", "true"));
        v.add(new UserAttrs("hf.Registrar.Roles", "client,user,peer,validator,auditor"));
        builder.name("test5").caName("CFCA").affiliation("org.department.c").attributes(v).maxEnrollments(2).secret("1234").type("user");
        final RegistrationRequest registrationRequest = builder.build();
        final RegisterCommand registerCommand = new RegisterCommand();
        String[] args = new String[]{"register", "-h", "localhost", "-p", "8089", "-a", new Gson().toJson(registrationRequest)};
        registerCommand.prepare(args);
        registerCommand.execute();
    }
}