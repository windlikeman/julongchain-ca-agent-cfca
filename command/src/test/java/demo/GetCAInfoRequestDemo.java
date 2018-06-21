package demo;

import com.cfca.ra.command.internal.getcainfo.GetCAInfoCommand;
import com.cfca.ra.command.internal.getcainfo.GetCAInfoRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.apache.commons.io.FileUtils;

import java.io.File;

public class GetCAInfoRequestDemo {

    public static void main(String[] args) throws Exception {

        final GetCAInfoRequest caInfoRequest = new GetCAInfoRequest("CFCA");
        final Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        final String jsonFile = "TestData/cainfo.json";
        final String request = gson.toJson(caInfoRequest);
        System.out.println("request=" + request);
        FileUtils.writeStringToFile(new File(jsonFile), request);

        final GetCAInfoCommand getCAInfoCommand = new GetCAInfoCommand();
        getCAInfoCommand.prepare(new String[]{"cainfo", "-h", "localhost", "-p", "8089", "-a", jsonFile});
        final JsonObject result = getCAInfoCommand.execute();
        System.out.println(result);
    }
}