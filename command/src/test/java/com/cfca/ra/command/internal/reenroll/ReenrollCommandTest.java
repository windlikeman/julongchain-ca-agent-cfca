package com.cfca.ra.command.internal.reenroll;

import com.cfca.ra.command.CommandException;
import com.cfca.ra.command.config.ConfigBean;
import com.cfca.ra.command.config.CsrConfig;
import com.cfca.ra.command.internal.enroll.EnrollCommand;
import com.cfca.ra.command.internal.enroll.EnrollmentRequest;
import com.cfca.ra.command.utils.ConfigUtils;
import com.google.gson.Gson;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ReenrollCommandTest {

    private ReenrollCommand reenrollCommand;

    @Before
    public void setUp() throws Exception {
        reenrollCommand = new ReenrollCommand();
    }

    @After
    public void tearDown() throws Exception {
    }

    private ConfigBean loadConfigFile() throws CommandException {
        try {
            return ConfigUtils.load("ca-client/config/ca-client-config.yaml");
        } catch (Exception e) {
            throw new CommandException(CommandException.REASON_CODE_ENROLL_COMMAND_LOAD_CONFIG_FAILED, e);
        }
    }

    @Test
    public void testReenroll() throws Exception{
        final ConfigBean configBean = loadConfigFile();
        String profile = configBean.getEnrollment().getProfile();
        CsrConfig csrConfig = configBean.getCsr();
        String caName = configBean.getCaname();
        //"test", "dGVzdDoxMjM0"// "test2":"dGVzdDI6MTIzNA=="
        final ReenrollmentRequest.Builder builder = new ReenrollmentRequest.Builder("admin","1234", profile, csrConfig, caName);
        final ReenrollmentRequest reenrollmentRequest = builder.build();
        String[] args = new String[]{"enroll", "-h", "localhost", "-p", "8089", "-a", new Gson().toJson(reenrollmentRequest)};
        reenrollCommand.prepare(args);
        reenrollCommand.execute();
    }
}