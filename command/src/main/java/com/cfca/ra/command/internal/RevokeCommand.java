package com.cfca.ra.command.internal;

import com.cfca.ra.command.CommandException;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * @author zhangchong
 * @create 2018/5/11
 * @Description 签发证书命令
 * @CodeReviewer
 * @since v3.0.0
 */
public final class RevokeCommand extends BaseClientCommand {

    private static final Logger logger = LoggerFactory.getLogger(RevokeCommand.class);


    public RevokeCommand() {
        this.name = COMMAND_NAME_REVOKE;
    }

    /**
     * ca-client revoke -h host -p port -a <json string>
     *
     * @param args 命令行参数
     * @throws CommandException 失败则返回
     */
    @Override
    public void prepare(String[] args) throws CommandException {
        super.prepare(args);

        final RevokeRequest revokeRequest = new Gson().fromJson(content, RevokeRequest.class);
        clientCfg.setRevokeRequest(revokeRequest);
    }

    @Override
    public void checkArgs(String[] args) throws CommandException {
        if (args.length != COMMAND_LINE_ARGS_NUM) {
            logger.error("Usage : " + getUsage());
            throw new CommandException(CommandException.REASON_CODE_REVOKE_COMMAND_ARGS_INVALID, "fail to build revoke command ,because args is invalid : args=" + Arrays.toString(args));
        }
    }

    @Override
    public void execute() throws CommandException {
        logger.info("Entered revoke");
        Identity id = client.loadMyIdentity();

        RevokeRequest revokeRequest = clientCfg.getRevokeRequest();
        id.revoke(revokeRequest);
    }

    @Override
    public String getUsage() {
        return "ca-client revoke -h host -p port -a <json string>";
    }
}
