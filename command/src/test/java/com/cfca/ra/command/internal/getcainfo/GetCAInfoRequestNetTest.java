package com.cfca.ra.command.internal.getcainfo;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class GetCAInfoRequestNetTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testString() throws Exception {
        GetCAInfoRequestNet a = new GetCAInfoRequestNet("CFCA");
        /**
         * {"caname":"CFCA"}
         */
        System.out.println(new Gson().toJson(a));

    }

    @Test
    public void testExecute() throws Exception {
        final GetCAInfoCommand getCAInfoCommand = new GetCAInfoCommand();
        getCAInfoCommand.prepare(new String[]{"cainfo", "-h", "localhost", "-p", "8089", "-a", "{\"caName\":\"CFCA\"}"});
        final JsonObject result = getCAInfoCommand.execute();
    }
}