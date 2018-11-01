package org.bica.julongchain.cfca.ra.ca;

import java.security.Key;
import java.security.Security;
import java.security.spec.KeySpec;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TcertKeyTreeTest {

    @Before
    public void setUp() throws Exception {
        Security.addProvider(new BouncyCastleProvider());
    }

    @After
    public void tearDown() throws Exception {
        Security.removeProvider("BC");
    }

    private final static byte[] keybytes = { 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38 };

    @Test
    public void getKey() throws Exception {
        String childName = "zc";
        Key parentKey = new SecretKeySpec(keybytes, "AES256");
        KeySpec keyspecbc = new PBEKeySpec(childName.toCharArray(), parentKey.getEncoded(), 1000, 128);

        SecretKeyFactory factorybc = SecretKeyFactory.getInstance("PBEWITHHMACSHA256", "BC");
        Key keybc = factorybc.generateSecret(keyspecbc);

        System.out.println(keybc.getAlgorithm());
        System.out.println(keybc.getFormat());
        System.out.println(Hex.toHexString(keybc.getEncoded()));
    }
}