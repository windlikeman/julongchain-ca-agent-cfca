package demo;

import com.google.gson.JsonObject;
import org.bica.julongchain.cfca.ra.command.internal.heartbeat.HeartBeatCommand;

/**
 * @Author zhangchong
 * @Description EnrollCommandDemo
 * @create 2018/6/11
 * @CodeReviewer zhangqingan
 * @since v3.0.0.1
 */
public class HeartBeatCommandDemo {

    public static void main(String[] args) throws Exception {
        final HeartBeatCommand command = new HeartBeatCommand();
        String[] args1 = new String[]{"heartbeat", "-h", "localhost", "-p", "8089"};
        command.prepare(args1);
        final JsonObject response = command.execute();
        System.out.println(response);
    }

}