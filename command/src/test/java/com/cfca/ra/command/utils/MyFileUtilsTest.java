package com.cfca.ra.command.utils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MyFileUtilsTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void makeFileAbs() {
        String name = MyFileUtils.makeFileAbs("F:\\zc\\Block Chain Cipher Innovation Alliance\\code\\1.txt", "F:\\zc\\Block Chain Cipher Innovation Alliance\\code");
        System.out.println(name);
    }
}
