package org.bica.julongchain.cfca.ra.command.internal.register;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Random;

/**
 * @author zhangchong
 * @create 2018/5/11
 * @Description 测试 用户注册 命令
 * @CodeReviewer helonglong
 * @since v3.0.0
 */
public class RegisterCommandTest {

    private RegisterCommand registerCommand;

    @Before
    public void setUp() throws Exception {
        registerCommand = new RegisterCommand();
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


        String[] args = new String[]{"register", "-h", "localhost", "-p", "8089", "-a", jsonFile};
        registerCommand.prepare(args);
        registerCommand.execute();
    }

    private static char ch[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G',
            'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b',
            'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
            'x', 'y', 'z', '0', '1' };

    private String createRandomString(int length) {
        if (length > 0) {
            int index = 0;
            char[] temp = new char[length];
            Random random = new SecureRandom();
            int num = random.nextInt();
            for (int i = 0; i < length % 5; i++) {
                temp[index++] = ch[num & 63];
                num >>= 6;
            }
            for (int i = 0; i < length / 5; i++) {
                num = random.nextInt();
                for (int j = 0; j < 5; j++) {
                    temp[index++] = ch[num & 63];
                    num >>= 6;
                }
            }
            return new String(temp, 0, length);
        }
        else if (length == 0) {
            return "";
        }
        else {
            throw new IllegalArgumentException();
        }
    }
    @Test
    public void checkLengthValid() throws Exception{
        String s = createRandomString(64);

        try {
            registerCommand.checkNameValid(s);
        }catch (Exception e){
            e.printStackTrace();
            Assert.assertTrue(e == null);
        }
    }
}