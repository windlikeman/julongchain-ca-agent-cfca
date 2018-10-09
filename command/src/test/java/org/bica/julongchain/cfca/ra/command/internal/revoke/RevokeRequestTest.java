package org.bica.julongchain.cfca.ra.command.internal.revoke;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;

import java.io.File;

/**
 * @author zhangchong
 * @create 2018/5/11
 * @Description 测试 吊销证书 命令
 * @CodeReviewer helonglong
 * @since v3.0.0
 */
public class RevokeRequestTest {

    private RevokeCommand revokeCommand;

    @Before
    public void setUp() throws Exception {
        revokeCommand = new RevokeCommand();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testToString() throws Exception{
        String id = "admin";
        String aki = "sss";
        String serial = "1032162421";
        String reason = "expire";
        String caname = "CFCA";

        final RevokeRequest revokeRequest = new RevokeRequest(id, aki, serial, reason, caname);
        System.out.println(new Gson().toJson(revokeRequest));
        String s = "{\"id\":\"admin\",\"aki\":\"sss\",\"serial\":\"122222\",\"reason\":\"expire\",\"caname\":\"CFCA\"}";
    }

    @Test
    public void testRevoke() throws Exception{
        String id = "admin";
        String aki = "sss";
        String serial = "1032162646";
        String reason = "expire";
        String caname = "CFCA";

        final RevokeRequest revokeRequest = new RevokeRequest(id, aki, serial, reason, caname);
        final String jsonFile = "TestData/revoke.json";
        final Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        final String request = gson.toJson(revokeRequest);
        FileUtils.writeStringToFile(new File(jsonFile), request);

        String[] args = new String[]{"revoke", "-h", "localhost", "-p", "8089", "-a", jsonFile};
        RevokeCommand revokeCommand = new RevokeCommand();
        revokeCommand.prepare(args);
        final JsonObject result = revokeCommand.execute();
        System.out.println(result);
    }
}