package com.cfca.ra.command.internal.revoke;

import com.cfca.ra.command.CommandException;
import com.cfca.ra.command.config.ConfigBean;
import com.cfca.ra.command.config.CsrConfig;
import com.cfca.ra.command.internal.reenroll.ReenrollmentRequest;
import com.cfca.ra.command.utils.ConfigUtils;
import com.google.gson.Gson;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class RevokeRequestTest {

    private RevokeCommand revokeCommand;

    @Before
    public void setUp() throws Exception {
        revokeCommand = new RevokeCommand();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testToString() throws Exception{
        String id = "admin";
        String aki = "sss";
        String serial = "1032162421";
        String reason = "expire";
        String caname = "CFCA";

        final RevokeRequest revokeRequest = new RevokeRequest(id, aki, serial, reason, caname);
        System.out.println(new Gson().toJson(revokeRequest));
        String s = "{\"id\":\"admin\",\"aki\":\"sss\",\"serial\":\"122222\",\"reason\":\"expire\",\"caname\":\"CFCA\"}";
    }

    @Test
    public void testRevoke() throws Exception{
        String id = "admin";
        String aki = "sss";
        String serial = "1032162421";
        String reason = "expire";
        String caname = "CFCA";

        final RevokeRequest revokeRequest = new RevokeRequest(id, aki, serial, reason, caname);
        String[] args = new String[]{"revoke", "-h", "localhost", "-p", "8089", "-a", new Gson().toJson(revokeRequest)};
        revokeCommand.prepare(args);
        revokeCommand.execute();

    }
}