package demo;

import org.bica.julongchain.cfca.ra.command.internal.revoke.RevokeCommand;
import org.bica.julongchain.cfca.ra.command.internal.revoke.RevokeRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.apache.commons.io.FileUtils;

import java.io.File;

public class RevokeRequestDemo {

    public static void main(String[] args) throws Exception {
        final RevokeRequestDemo revokeRequestDemo = new RevokeRequestDemo();
        revokeRequestDemo.testRevoke();
    }

    private void testRevoke() throws Exception{
        String id = "admin";
        String aki = "ssss";
        String serial = "1033792875";
        String reason = "expire";
        String caname = "CFCA";

        final RevokeRequest revokeRequest = new RevokeRequest(id, aki, serial, reason, caname);

        final String jsonFile = "TestData/revoke.json";
        final Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        final String request = gson.toJson(revokeRequest);
        System.out.println("request=" + request);
        FileUtils.writeStringToFile(new File(jsonFile), request);

        String[] args = new String[]{"revoke", "-h", "localhost", "-p", "8089", "-a", jsonFile};
        RevokeCommand revokeCommand = new RevokeCommand();
        revokeCommand.prepare(args);
        final JsonObject result = revokeCommand.execute();
        System.out.println(result);
    }
}