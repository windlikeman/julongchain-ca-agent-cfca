package com.cfca.ra.command.internal.register;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
/**
 * @author zhangchong
 * @create 2018/5/11
 * @Description 测试 用户注册 命令
 * @CodeReviewer helonglong
 * @since v3.0.0
 */
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
    }

    @Test
    public void testRegister() throws Exception {
        final RegistrationRequest.Builder builder = new RegistrationRequest.Builder();
        final ArrayList<UserAttrs> v = new ArrayList<>();
        v.add(new UserAttrs("hf.Revoker", "true"));
        v.add(new UserAttrs("hf.Registrar.Roles", "client,user,peer,validator,auditor"));
        builder.name("test4").caName("CFCA").affiliation("org.department.c").attributes(v).maxEnrollments(2).secret("1234").type("user");
        final RegistrationRequest registrationRequest = builder.build();

        final Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        final String request = gson.toJson(registrationRequest);

        final String jsonFile = "TestData/register.json";
        FileUtils.writeStringToFile(new File(jsonFile), request);

        final RegisterCommand registerCommand = new RegisterCommand();
        String[] args = new String[]{"register", "-h", "localhost", "-p", "8089", "-a", jsonFile};
        registerCommand.prepare(args);
        registerCommand.execute();
    }
}