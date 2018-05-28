package com.cfca.ra;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.security.Key;
import java.security.Security;

public class RAServerTest {

    private RAServer raServer;

    @Before
    public void setUp() throws Exception {
        raServer = new RAServer();
        Security.addProvider(new BouncyCastleProvider());
    }

    @After
    public void tearDown() throws Exception {
        Security.removeProvider("BC");
    }

    @Test
    public void testGenRootKey() throws Exception {
        String content = "TEST1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        System.out.println("Original content:");
        System.out.println(content);
        final Key key = raServer.genRootKey();
        System.out.println("key = " + key);
    }

}