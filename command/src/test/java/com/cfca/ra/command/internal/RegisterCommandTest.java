package com.cfca.ra.command.internal;

import com.google.gson.Gson;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

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
}