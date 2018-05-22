package com.cfca.ra.repository;

import com.cfca.ra.RAServerException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static org.junit.Assert.*;

public class EnrollIdStoreTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    enum EnrollIdStore1 implements IEnrollIdStore{
        INSTANCE("CFCA") {
            Map<String, String> enrollIdStore = new HashMap<>();

            @Override
            public void updateEnrollIdStore(String enrollmentID, String id) throws RAServerException {
                enrollIdStore.put("=sas1ss", "1212121");
                enrollIdStore.put("=sas1=$%#ss", "12121212");
                Gson gson = new GsonBuilder().enableComplexMapKeySerialization().disableHtmlEscaping().create();
                final String s = gson.toJson(enrollIdStore);
                System.out.println(s);
                //{"=sas1=$%#ss":"12121212","=sas1ss":"1212121"}
                Assert.assertTrue("{\"=sas1=$%#ss\":\"12121212\",\"=sas1ss\":\"1212121\"}".equals(s) );


                Type type = new TypeToken<Map<String, String>>() {}.getType();
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
    public void testE()throws Exception{
        final EnrollIdStore cfca = EnrollIdStore.CFCA;
        cfca.updateEnrollIdStore("zc", "1212");
    }
}