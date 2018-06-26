package com.cfca.ra;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.KeySpec;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;

import com.cfca.ra.ca.CAInfo;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author zhangchong
 * @create 2018/5/25
 * @Description 加密测试类
 * @CodeReviewer
 * @since
 */
public class EncryptTest {

    private RAServer raServer;

    @Before
    public void setUp() throws Exception {
        raServer = new RAServer(new CAInfo());
        Security.addProvider(new BouncyCastleProvider());
    }

    @After
    public void tearDown() throws Exception {
        Security.removeProvider("BC");
    }

    private String createHMACKey() {
        SecureRandom random = new SecureRandom();
        byte[] key = new byte[49];
        random.nextBytes(key);
        return Base64.toBase64String(key);
    }

    @Test
    public void testCreateHMACKey() throws Exception {
        for (int i = 0; i < 10; i++) {
            System.out.println(i + " = " + createHMACKey());
        }
    }

    private static byte[] generateSalt() throws NoSuchAlgorithmException {
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[32];
        random.nextBytes(salt);
        return salt;
    }

    @Test
    public void testAES() throws Exception {
        try {
            String keyString = "Olmy9iqs1LwWblwe";
            String input = "teststring";
            byte[] inputBytes = input.getBytes();
            String xiv = "1234567891234567";
            byte[] iv = xiv.getBytes("UTF-8");
            int length;
            // Set up
            AESEngine engine = new AESEngine();
            CBCBlockCipher blockCipher = new CBCBlockCipher(engine);
            PaddedBufferedBlockCipher cipher = new PaddedBufferedBlockCipher(blockCipher);
            KeyParameter keyParam = new KeyParameter(keyString.getBytes());
            ParametersWithIV keyParamWithIV = new ParametersWithIV(keyParam, iv, 0, 16);

            // Encrypt
            cipher.init(true, keyParamWithIV);
            byte[] outputBytes = new byte[cipher.getOutputSize(inputBytes.length)];
            length = cipher.processBytes(inputBytes, 0, inputBytes.length, outputBytes, 0);
            cipher.doFinal(outputBytes, length);
            String encryptedInput = new String(Base64.encode(outputBytes));
            System.out.println("Encrypted String:" + encryptedInput);

            // Decrypt
            cipher.init(false, keyParamWithIV);
            byte[] out2 = Base64.decode(encryptedInput);
            byte[] comparisonBytes = new byte[cipher.getOutputSize(out2.length)];
            length = cipher.processBytes(out2, 0, out2.length, comparisonBytes, 0);
            cipher.doFinal(comparisonBytes, length); // Do the final block
            String s2 = new String(comparisonBytes);
            System.out.println("Decrypted String:" + s2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testPBEKDF() throws Exception {
        SecretKeyFactory factorybc = SecretKeyFactory.getInstance("PBEWITHHMACSHA256", "BC");
        KeySpec keyspecbc = new PBEKeySpec("password".toCharArray(), generateSalt(), 1000, 128);
        Key keybc = factorybc.generateSecret(keyspecbc);
        System.out.println(keybc.getClass().getName());
        System.out.println(Arrays.toString(keybc.getEncoded()));
        System.out.println(keybc.getAlgorithm());
    }

    @Test
    public void testAESEncrypt() throws Exception {
        String content = "TEST1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        System.out.println("Original content:");
        System.out.println(content);
        final byte[] encode = Base64.encode(content.getBytes("UTF-8"));
        final Key key = raServer.genRootKey();
        System.out.println("key = " + key);
        Cipher in = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
        byte[] iv = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        // ========================ENCRYPT===========================
        in.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
        byte[] enc = in.doFinal(encode);
        System.out.println("Encrypted Content:");
        System.out.println(new String(Hex.encode(enc)));

        // ========================DECRYPT===========================
        Cipher out = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
        out.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
        byte[] dec = out.doFinal(enc);
        final byte[] decode = Base64.decode(dec);
        System.out.println("Decrypted Content:");
        final String x = new String(decode);
        System.out.println(x);
        Assert.assertTrue(x.equals(content));
    }
}
