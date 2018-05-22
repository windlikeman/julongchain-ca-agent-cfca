package com.cfca.ra.api;

import com.cfca.ra.command.internal.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;

/**
 * @author zhangchong
 * @create 2018/5/11
 * @Description 命令行功能的主运行类
 * @CodeReviewer
 * @since v3.0.0
 */
public class Client {
    private static final Logger logger = LoggerFactory.getLogger(Client.class);

    public static void main(String[] args) throws Exception {
        final Client client = new Client();
        client.init();
        int err = client.runMain(args);
        if (err != 0) {
            System.exit(1);
        }
    }

    private HashMap<String, BaseClientCommand> commandHashMap;

    private void init() {
        commandHashMap = new HashMap<String, BaseClientCommand>(10);
        final EnrollCommand enrollCommand = new EnrollCommand();
        final ReenrollCommand reenrollCommand = new ReenrollCommand();
        final GetCAInfoCommand getCAInfoCommand = new GetCAInfoCommand();
        final RegisterCommand registerCommand = new RegisterCommand();
        final RevokeCommand revokeCommand = new RevokeCommand();
        final GetTCertCommand getTCertCommand = new GetTCertCommand();

        commandHashMap.put(BaseClientCommand.COMMAND_NAME_ENROLL, enrollCommand);
        commandHashMap.put(BaseClientCommand.COMMAND_NAME_REENROLL, reenrollCommand);
        commandHashMap.put(BaseClientCommand.COMMAND_NAME_GETCAINFO, getCAInfoCommand);
        commandHashMap.put(BaseClientCommand.COMMAND_NAME_REGISTER, registerCommand);
        commandHashMap.put(BaseClientCommand.COMMAND_NAME_REVOKE, revokeCommand);
        commandHashMap.put(BaseClientCommand.COMMAND_NAME_GETTCERT, getTCertCommand);
    }


    private int runMain(String[] args) {
        logger.info("runMain>>>>>>Running:" + Arrays.toString(args));
        String cmdName = "";

        if (args.length > 0) {
            cmdName = args[0];
        }
        int err = 0;
        try {
            if (commandHashMap.containsKey(cmdName)) {
                final BaseClientCommand command = commandHashMap.get(cmdName);
                command.prepare(args);
                command.execute();
            }
            return err;

        } catch (Exception e) {
            logger.error("runMain<<<<<<Failure", e);
            err = -1;
        }

        return err;
    }
}
