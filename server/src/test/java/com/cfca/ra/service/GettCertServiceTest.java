package com.cfca.ra.service;

import com.cfca.ra.utils.HMAC;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.Security;

public class GettCertServiceTest {

    private BouncyCastleProvider provider;

    @Before
    public void setUp() throws Exception {
        provider = new BouncyCastleProvider();
        Security.addProvider(provider);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void gettcert() throws Exception {
        byte[] keybytes = {0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38,
                0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38};
        byte[] iv = {0x38, 0x37, 0x36, 0x35, 0x34, 0x33, 0x32, 0x31, 0x38,
                0x37, 0x36, 0x35, 0x34, 0x33, 0x32, 0x31};
        String content = "TEST1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        System.out.println("Original content:");
        System.out.println(content);
        try {
            Security.addProvider(new BouncyCastleProvider());
            Key key = new SecretKeySpec(keybytes, "AES");
            Cipher in = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
            in.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
            byte[] enc = in.doFinal(content.getBytes());
            System.out.println("Encrypted Content:");
            System.out.println(new String(Hex.encode(enc)));

            Cipher out = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
            out.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
            byte[] dec = out.doFinal(enc);
            System.out.println("Decrypted Content:");
            System.out.println(new String(dec));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    /**
     * 进行相关的摘要算法的处理展示
     *
     * @throws Exception
     **/
    @Test
    public void testHMAC() throws Exception {
        String str = "123000000000090989999999999999999999999999999999999999999999999999999999999999999999111" +
                "23000000000090989999999999999999999999999999999999999999999999999999999999999999999" +
                "23000000000090989999999999999999999999999999999999999999999999999999999999999999999" +
                "23000000000090989999999999999999999999999999999999999999999999999999999999999999999" +
                "23000000000090989999999999999999999999999999999999999999999999999999999999999999999" +
                "23000000000090989999999999999999999999999999999999999999999999999999999999999999999" +
                "23000000000090989999999999999999999999999999999999999999999999999999999999999999999" +
                "23000000000090989999999999999999999999999999999999999999999999999999999999999999999" +
                "23000000000090989999999999999999999999999999999999999999999999999999999999999999999" +
                "23000000000090989999999999999999999999999999999999999999999999999999999999999999999" +
                "23000000000090989999999999999999999999999999999999999999999999999999999999999999999" +
                "23000000000090989999999999999999999999999999999999999999999999999999999999999999999" +
                "23000000000090989999999999999999999999999999999999999999999999999999999999999999999" +
                "23000000000090989999999999999999999999999999999999999999999999999999999999999999999" +
                "23000000000090989999999999999999999999999999999999999999999999999999999999999999999" +
                "23000000000090989999999999999999999999999999999999999999999999999999999999999999999" +
                "230000000000909899999999999999999999999999999999999999999999999999999999999999999991";
        //初始化密钥
        byte[] key1 = HMAC.initHmacMD5Key();
        //获取摘要信息
        byte[] data1 = HMAC.encodeHmacMD5(str.getBytes(), key1);
        String datahex1 = new String(Hex.encode(data1));

        System.out.println("原文：" + str);
        System.out.println();

        System.out.println("Bouncycastle HmacMD5的密钥内容和长度:" + key1.toString() + "--" + key1.length);
        System.out.println("Bouncycastle HmacMD5算法摘要：" + data1.toString());
        System.out.println("Bouncycastle HmacMD5算法摘要HEX：" + datahex1.toString());
        System.out.println();

        //初始化密钥
        byte[] key2 = HMAC.initHmacSHA256Key();
        //获取摘要信息
        byte[] data2 = HMAC.encodeHmacSHA256(str.getBytes(), key2);
        String datahex2 = new String(Hex.encode(data2));
        System.out.println("Bouncycastle HmacSHA256的密钥:" + key2.toString());
        System.out.println("Bouncycastle HmacSHA256算法摘要：" + data2.toString());
        System.out.println("Bouncycastle HmacSHA256算法摘要HEX：" + datahex2);
        System.out.println();


        //初始化密钥
        byte[] key3 = HMAC.initHmacSHAKey();
        //获取摘要信息
        byte[] data3 = HMAC.encodeHmacSHA(str.getBytes(), key3);
        String datahex3 = new String(Hex.encode(data3));
        System.out.println("Bouncycastle HmacSHA1的密钥:" + key3.toString());
        System.out.println("Bouncycastle HmacSHA1算法摘要：" + data3.toString());
        System.out.println("Bouncycastle HmacSHA1算法摘要HEX：" + datahex3);
        System.out.println();


        //初始化密钥
        byte[] key4 = HMAC.initHmacSHA384Key();

        //获取摘要信息
        byte[] data4 = HMAC.encodeHmacSHA384(str.getBytes(), key4);
        String datahex4 = new String(Hex.encode(data4));
        System.out.println("Bouncycastle HmacSHA384的密钥:" + key4.toString());
        System.out.println("Bouncycastle HmacSHA384算法摘要：" + data4.toString());
        System.out.println("Bouncycastle HmacSHA384算法摘要HEX：" + datahex4);
        System.out.println();


        //初始化密钥
        byte[] key5 = HMAC.initHmacSHA512Key();
        //获取摘要信息
        byte[] data5 = HMAC.encodeHmacSHA512(str.getBytes(), key5);
        System.out.println("HmacSHA512的密钥:" + key5.toString());
        System.out.println("HmacSHA512算法摘要：" + data5.toString());
        System.out.println();

        System.out.println("================以下的算法支持是bouncycastle支持的算法，sun java6不支持=======================");
        //初始化密钥
        byte[] key6 = HMAC.initHmacMD2Key();
        //获取摘要信息
        byte[] data6 = HMAC.encodeHmacMD2(str.getBytes(), key6);
        String datahex6 = HMAC.encodeHmacMD2Hex(str.getBytes(), key6);
        System.out.println("Bouncycastle HmacMD2的密钥:" + key6.toString());
        System.out.println("Bouncycastle HmacMD2算法摘要：" + data6.toString());
        System.out.println("Bouncycastle HmacMD2Hex算法摘要：" + datahex6.toString());
        System.out.println();

        //初始化密钥
        byte[] key7 = HMAC.initHmacMD4Key();
        //获取摘要信息
        byte[] data7 = HMAC.encodeHmacMD4(str.getBytes(), key7);
        String datahex7 = HMAC.encodeHmacMD4Hex(str.getBytes(), key7);
        System.out.println("Bouncycastle HmacMD4的密钥:" + key7.toString());
        System.out.println("Bouncycastle HmacMD4算法摘要：" + data7.toString());
        System.out.println("Bouncycastle HmacMD4Hex算法摘要：" + datahex7.toString());
        System.out.println();

        //初始化密钥
        byte[] key8 = HMAC.initHmacSHA224Key();
        //获取摘要信息
        byte[] data8 = HMAC.encodeHmacSHA224(str.getBytes(), key8);
        String datahex8 = HMAC.encodeHmacSHA224Hex(str.getBytes(), key8);
        System.out.println("Bouncycastle HmacSHA224的密钥:" + key8.toString());
        System.out.println("Bouncycastle HmacSHA224算法摘要：" + data8.toString());
        System.out.println("Bouncycastle HmacSHA224算法摘要：" + datahex8.toString());
        System.out.println();
    }

    private static boolean isValidIdentifier(String identifier) {
        if (identifier.length() < 3 || identifier.charAt(1) != '.') {
            return false;
        }

        char first = identifier.charAt(0);
        if (first < '0' || first > '2') {
            return false;
        }

        return isValidBranchID(identifier, 2);
    }

    private static boolean isValidBranchID(String branchID, int start) {
        boolean periodAllowed = false;

        int pos = branchID.length();
        while (--pos >= start) {
            char ch = branchID.charAt(pos);

            // TODO Leading zeroes?
            if ('0' <= ch && ch <= '9') {
                periodAllowed = true;
                continue;
            }

            if (ch == '.') {
                if (!periodAllowed) {
                    return false;
                }

                periodAllowed = false;
                continue;
            }

            return false;
        }

        return periodAllowed;
    }

    @Test
    public void testStart() {
        System.out.println(isValidIdentifier("1.2.3.4"));
    }
}