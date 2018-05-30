package com.cfca.ra.command.internal.gettcert;

import com.cfca.ra.command.CommandException;
import com.cfca.ra.command.internal.BaseClientCommand;
import com.cfca.ra.command.internal.Identity;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhangchong
 * @create 2018/5/13
 * @Description 获取交易证书命令
 * @CodeReviewer
 * @since v3.0.0
 */
public class GettCertCommand extends BaseClientCommand {

    private static final Logger logger = LoggerFactory.getLogger(GettCertCommand.class);

    public GettCertCommand() {
        this.name = COMMAND_NAME_GETTCERT;
    }

    @Override
    public JsonObject execute() throws CommandException {
        logger.info("Entered getTCert");
        Identity id = client.loadMyIdentity();

        GettCertRequest request = clientCfg.getGettCertRequest();
        final GettCertResponse resp = id.gettcert(request);
        if (resp == null) {
            throw new CommandException(CommandException.REASON_CODE_GETTCERT_COMMAND_COMMS_FAILED, "gettcert command failed to execute, but I do not know why");
        }
        return new JsonObject();
    }

    @Override
    public void prepare(String[] args) throws CommandException {
        super.prepare(args);

        final GettCertRequest gettCertRequest = new Gson().fromJson(content, GettCertRequest.class);
        clientCfg.setGettCertRequest(gettCertRequest);
    }

    @Override
    public void checkArgs(String[] args) throws CommandException {
        if (args.length != COMMAND_LINE_ARGS_NUM) {
            logger.error("Usage: " + getUsage());
            throw new CommandException(CommandException.REASON_CODE_GETTCERT_COMMAND_ARGS_INVALID, "args is invalid =>" + getUsage());
        }
    }

    @Override
    public String getUsage() {
        return "ca-client tcert -h host -p port -a <json>";
    }


}
