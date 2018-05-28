package com.cfca.ra.command.internal.enroll;

import com.cfca.ra.command.CommandException;
import com.cfca.ra.command.config.ConfigBean;
import com.cfca.ra.command.config.CsrConfig;
import com.cfca.ra.command.utils.ConfigUtils;
import com.google.gson.Gson;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class EnrollCommandTest {

    private EnrollCommand enrollCommand;

    @Before
    public void setUp() throws Exception {
        enrollCommand = new EnrollCommand();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testParseRawurlOK() throws Exception {
        String s1 = "http://<enrollmentID>:<secret>@ip:port";
        final ParsedUrl parsedUrl = enrollCommand.parseRawurl(s1);
        System.out.println(parsedUrl);
    }

    @Test
    public void testBuildEnrollment() throws Exception {
        String s = "{}";
        final EnrollmentRequest enrollmentRequest = new Gson().fromJson(s, EnrollmentRequest.class);
        System.out.println(enrollmentRequest.toString());
        Assert.assertTrue(enrollmentRequest.isNull());
    }

    private ConfigBean loadConfigFile() throws CommandException {
        try {
            return ConfigUtils.load("ca-client/config/ca-client-config.yaml");
        } catch (Exception e) {
            throw new CommandException(CommandException.REASON_CODE_ENROLL_COMMAND_LOAD_CONFIG_FAILED, e);
        }
    }

    @Test
    public void testEnrollment() throws Exception {
        final ConfigBean configBean = loadConfigFile();
        String profile = configBean.getEnrollment().getProfile();
        CsrConfig csrConfig = configBean.getCsr();
        String caName = configBean.getCaname();
        //"test", "dGVzdDoxMjM0"// "test2":"dGVzdDI6MTIzNA=="
        final EnrollmentRequest.Builder builder = new EnrollmentRequest.Builder("test2","dGVzdDI6MTIzNA==", profile, csrConfig, caName);
        final EnrollmentRequest enrollmentRequest = builder.build();
        final EnrollCommand enrollCommand = new EnrollCommand();
        String[] args = new String[]{"enroll", "-h", "localhost", "-p", "8089", "-a", new Gson().toJson(enrollmentRequest)};
        enrollCommand.prepare(args);
        enrollCommand.execute();
    }
}