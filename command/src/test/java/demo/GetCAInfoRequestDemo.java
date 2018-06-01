package demo;

import com.cfca.ra.command.CommandException;
import com.cfca.ra.command.internal.getcainfo.GetCAInfoCommand;
import com.cfca.ra.command.internal.getcainfo.GetCAInfoRequestNet;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class GetCAInfoRequestDemo {

    public static void main(String[] args) throws CommandException {
        final GetCAInfoCommand getCAInfoCommand = new GetCAInfoCommand();
        getCAInfoCommand.prepare(new String[]{"cainfo", "-h", "localhost", "-p", "8089", "-a", "{\"caName\":\"CFCA\"}"});
        final JsonObject result = getCAInfoCommand.execute();
        System.out.println(result);
    }
}