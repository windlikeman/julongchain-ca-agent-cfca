package com.cfca.ra.repository;

import java.io.File;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.bouncycastle.util.encoders.Base64;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.cfca.ra.RAServerException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class EnrollIdStoreTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    enum EnrollIdStore1 implements IEnrollIdStore {
        INSTANCE("CFCA") {
            Map<String, String> enrollIdStore = new HashMap<>();

            @Override
            public void updateEnrollIdStore(String enrollmentID, String id) throws RAServerException {
                enrollIdStore.put("=sas1ss", "1212121");
                enrollIdStore.put("=sas1=$%#ss", "12121212");
                Gson gson = new GsonBuilder().enableComplexMapKeySerialization().disableHtmlEscaping().create();
                final String s = gson.toJson(enrollIdStore);
                System.out.println(s);
                // {"=sas1=$%#ss":"12121212","=sas1ss":"1212121"}
                Assert.assertTrue("{\"=sas1=$%#ss\":\"12121212\",\"=sas1ss\":\"1212121\"}".equals(s));

                Type type = new TypeToken<Map<String, String>>() {
                }.getType();
                Map<String, String> map = gson.fromJson(s, type);
                Iterator<Map.Entry<String, String>> entries = map.entrySet().iterator();

                while (entries.hasNext()) {
                    Map.Entry<String, String> entry = entries.next();
                    System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
                }
            }

            @Override
            public String getEnrollmentId(String id) throws RAServerException {
                return null;
            }
        };

        protected final String name;

        EnrollIdStore1(String cfca) {
            this.name = cfca;
        }
    }

    @Test
    public void testGson() throws Exception {
        final EnrollIdStore1 instance = EnrollIdStore1.INSTANCE;
        instance.updateEnrollIdStore("", "");
    }

    @Test
    public void testE() throws Exception {
        final EnrollIdStore cfca = EnrollIdStore.CFCA;
        cfca.updateEnrollIdStore("zc", "1212");
    }

    @Test
    public void testStore() throws Exception {
        String b64Cert = "MIICzjCCAnGgAwIBAgIFEDIVAjAwDAYIKoEcz1UBg3UFADBcMQswCQYDVQQGEwJDTjEwMC4GA1UECgwnQ2hpbmEgRmluYW5jaWFsIENlcnRpZmljYXRpb24gQXV0aG9yaXR5MRswGQYDVQQDDBJDRkNBIFRFU1QgU00yIE9DQTEwHhcNMTgwNTI1MDQ0NjQ1WhcNMTgwOTAzMDc0MzIzWjBxMQswCQYDVQQGEwJDTjEXMBUGA1UECgwOQ0ZDQSBURVNUIE9DQTExETAPBgNVBAsMCExvY2FsIFJBMRUwEwYDVQQLDAxJbmRpdmlkdWFsLTExHzAdBgNVBAMMFjA1MUBhYWFhYUBaSDA5MzU4MDI4QDcwWTATBgcqhkjOPQIBBggqgRzPVQGCLQNCAASUFmdyrhjvzx8o8Dm4KZ49h9cNHfBJuZKb9h7XwEcow5mzDvPMRIS0g2WPXaQwFxFDGsTjevQpuED7OEgaAMk/o4IBBzCCAQMwHwYDVR0jBBgwFoAUa/4Y2o9COqa4bbMuiIM6NKLBMOEwSAYDVR0gBEEwPzA9BghggRyG7yoBATAxMC8GCCsGAQUFBwIBFiNodHRwOi8vd3d3LmNmY2EuY29tLmNuL3VzL3VzLTE0Lmh0bTA4BgNVHR8EMTAvMC2gK6AphidodHRwOi8vdWNybC5jZmNhLmNvbS5jbi9TTTIvY3JsMjMyOS5jcmwwEQYDKlYBBAoMCGV4dFZhbHVlMAsGA1UdDwQEAwID6DAdBgNVHQ4EFgQUWqPiSQl+O/ZSaSztkVwAJDs7MvEwHQYDVR0lBBYwFAYIKwYBBQUHAwIGCCsGAQUFBwMEMAwGCCqBHM9VAYN1BQADSQAwRgIhAMu8d24OOFUeTy6FelY6Fa7txt6enTTermMNOBNlWyf4AiEApmydTgVx5vbbNENmHJskyBnGd8Q/zPQmdAPBA87GKsI=";
        final byte[] decode = Base64.decode(b64Cert);
        FileUtils.writeByteArrayToFile(new File("TestData/test.cer"), decode);
    }
}