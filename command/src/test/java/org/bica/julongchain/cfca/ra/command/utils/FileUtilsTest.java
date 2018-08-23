package org.bica.julongchain.cfca.ra.command.utils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FileUtilsTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void makeFileAbs() {
        String name = FileUtils.makeFileAbs("F:\\zc\\Block Chain Cipher Innovation Alliance\\code\\1.txt",
                "F:\\zc\\Block Chain Cipher Innovation Alliance\\code");
        System.out.println(name);
    }
}
