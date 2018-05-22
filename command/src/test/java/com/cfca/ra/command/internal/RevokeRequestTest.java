package com.cfca.ra.command.internal;

import com.google.gson.Gson;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class RevokeRequestTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testToString() {
        String id = "admin";
        String aki = "sss";
        String serial = "122222";
        String reason = "expire";
        String caname = "CFCA";

        final RevokeRequest revokeRequest = new RevokeRequest(id, aki, serial, reason, caname);
        System.out.println(new Gson().toJson(revokeRequest));
        String s = "{\"id\":\"admin\",\"aki\":\"sss\",\"serial\":\"122222\",\"reason\":\"expire\",\"caname\":\"CFCA\"}";
    }
}