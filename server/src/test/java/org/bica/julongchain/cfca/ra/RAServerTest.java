package org.bica.julongchain.cfca.ra;

import java.security.Key;
import java.security.Security;

import org.bica.julongchain.cfca.ra.ca.CAInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.crypto.spec.SecretKeySpec;

public class RAServerTest {

    private RAServer raServer;

    private final static byte[] KEY_BYTES = {0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38,
            0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38};

    @Before
    public void setUp() throws Exception {
        raServer = new RAServer(new CAInfo(), null, null);
        Security.addProvider(new BouncyCastleProvider());

    }

    @After
    public void tearDown() throws Exception {
        Security.removeProvider("BC");
    }

    Key genRootKey() {
        return new SecretKeySpec(KEY_BYTES, "AES256");
    }

    @Test
    public void testGenRootKey() throws Exception {
        String content = "TEST1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        System.out.println("Original content:");
        System.out.println(content);
        final Key key = genRootKey();
        System.out.println("key = " + key);
    }

}