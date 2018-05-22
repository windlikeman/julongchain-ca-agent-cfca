package com.cfca.ra.command.internal;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

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
}