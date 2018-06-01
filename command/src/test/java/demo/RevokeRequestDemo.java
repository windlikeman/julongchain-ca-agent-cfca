package demo;

import com.cfca.ra.command.internal.revoke.RevokeCommand;
import com.cfca.ra.command.internal.revoke.RevokeRequest;
import com.google.gson.Gson;

public class RevokeRequestDemo {


    public static void main(String[] args) throws Exception {
        final RevokeRequestDemo revokeRequestDemo = new RevokeRequestDemo();
        revokeRequestDemo.testRevoke();
    }

    private void testRevoke() throws Exception{
        String id = "admin";
        String aki = "ssss";
        String serial = "1032162646";
        String reason = "expire";
        String caname = "CFCA";

        final RevokeRequest revokeRequest = new RevokeRequest(id, aki, serial, reason, caname);
        String[] args = new String[]{"revoke", "-h", "localhost", "-p", "8089", "-a", new Gson().toJson(revokeRequest)};
        RevokeCommand revokeCommand = new RevokeCommand();
        revokeCommand.prepare(args);
        revokeCommand.execute();
    }
}