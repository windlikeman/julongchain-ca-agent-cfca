package org.bica.julongchain.cfca.ra.command.internal.heartbeat;

import com.google.gson.JsonObject;
import org.bica.julongchain.cfca.ra.command.CommandException;
import org.bica.julongchain.cfca.ra.command.internal.BaseClientCommand;
import org.bica.julongchain.cfca.ra.command.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * @author zhangchong
 * @create 2018/5/11
 * @Description 注册用户命令
 * @CodeReviewer
 * @since v3.0.0
 */
public final class HeartBeatCommand extends BaseClientCommand {

    private static final Logger logger = LoggerFactory.getLogger(HeartBeatCommand.class);

    public HeartBeatCommand() {
        this.name = COMMAND_NAME_HEARTBEAT;
    }

    /**
     * ca-client heartbeat -h host -p port
     *
     * @param args 命令行参数
     * @throws CommandException 失败则返回
     */
    @Override
    public void prepare(String[] args) throws CommandException {
        super.prepare(args);
    }

    @Override
    public void checkArgs(String[] args) throws CommandException {
        if (args.length != COMMAND_LINE_ARGS_NUM - 2) {
            logger.error("Usage : {}", getUsage());
            throw new CommandException("fail to build HeartBeat command " +
                    ",because args[" + args.length + "] is invalid : args=" + Arrays.toString(args));
        }
    }

    @Override
    protected void parseArgs(String[] args) throws CommandException {
        for (int i = 0, size = args.length; i < size; i++) {
            switch (args[i]) {
                case "-h":
                    if (i + 1 < size) {
                        host = args[i + 1];
                    }
                    break;
                case "-p":
                    if (i + 1 < size) {
                        port = args[i + 1];
                    }
                    break;
                default:
                    break;
            }
        }
        if (StringUtils.isEmpty(host)) {
            String expecting = "-h host -p port";
            String message = String.format("The args of the command[" + name + "] is missing the host; found '%s' but expecting '%s'", Arrays.toString(args),
                    expecting);
            throw new CommandException(message);
        }
        if (StringUtils.isEmpty(port)) {
            String expecting = "-h host -p port -a<json string>";
            String message = String.format("The args of the command[" + name + "] is missing the host; found '%s' but expecting '%s'", Arrays.toString(args),
                    expecting);
            throw new CommandException(message);
        }
        clientCfg.setUrl("http://" + host + ":" + port);
    }

    @Override
    public JsonObject execute() throws CommandException {
        logger.info("HeartBeatCommand@execute : Running");

        final HeartBeatResponseVo heartbeat = client.heartbeat();
        return buildResult(heartbeat);
    }

    private JsonObject buildResult(HeartBeatResponseVo s) {
        final JsonObject jsonObject = new JsonObject();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(s.getDate());
        String status = s.getStatus();
        jsonObject.addProperty("heartbeatDate", dateString);
        jsonObject.addProperty("status", status);
        return jsonObject;
    }

    @Override
    public String getUsage() {
        return "ca-client heartbeat -h host -p port ";
    }
}
