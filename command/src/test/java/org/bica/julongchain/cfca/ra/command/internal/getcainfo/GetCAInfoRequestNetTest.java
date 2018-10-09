package org.bica.julongchain.cfca.ra.command.internal.getcainfo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

/**
 * @author zhangchong
 * @create 2018/5/11
 * @Description 测试getcainfo命令
 * @CodeReviewer helonglong
 * @since v3.0.0
 */
public class GetCAInfoRequestNetTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testString() throws Exception {
        GetCAInfoRequestNet a = new GetCAInfoRequestNet("CFCA");
        /**
         * {"caname":"CFCA"}
         */
        System.out.println(new Gson().toJson(a));

    }

    @Test
    public void testExecute() throws Exception {
        final GetCAInfoRequest caInfoRequest = new GetCAInfoRequest("CFCA");
        final Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        final String jsonFile = "TestData/cainfo.json";
        final String request = gson.toJson(caInfoRequest);
        FileUtils.writeStringToFile(new File(jsonFile), request);

        final GetCAInfoCommand getCAInfoCommand = new GetCAInfoCommand();
        getCAInfoCommand.prepare(new String[]{"cainfo", "-h", "localhost", "-p", "8089", "-a", jsonFile});
        final JsonObject result = getCAInfoCommand.execute();
        System.out.println(result);
    }
}