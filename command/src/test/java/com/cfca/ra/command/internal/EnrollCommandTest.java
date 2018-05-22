package com.cfca.ra.command.internal;

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
}