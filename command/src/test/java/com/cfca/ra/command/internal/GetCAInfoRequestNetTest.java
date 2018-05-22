package com.cfca.ra.command.internal;

import com.google.gson.Gson;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class GetCAInfoRequestNetTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testString() throws Exception{
        GetCAInfoRequestNet a = new GetCAInfoRequestNet("CFCA");
        /**
         * {"caname":"CFCA"}
         */
        System.out.println(new Gson().toJson(a));

    }
}