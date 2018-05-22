package com.cfca.ra.command.internal;

import com.cfca.ra.command.CommandException;
import com.google.gson.Gson;
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
    public void execute() throws CommandException {
        logger.info("Entered getTCert");
        Identity id = client.loadMyIdentity();

        GettCertRequest request = clientCfg.getGettCertRequest();
        final GettCertResponse resp = id.gettcert(request);
        if (resp == null) {
            throw new CommandException(CommandException.REASON_CODE_GETTCERT_COMMAND_COMMS_FAILED, "gettcert command failed to execute, but I do not know why");
        }
    }

    @Override
    public void prepare(String[] args) throws CommandException {
        super.prepare(args);

        final GettCertRequest gettCertRequest = new Gson().fromJson(content, GettCertRequest.class);
        clientCfg.setGettCertRequest(gettCertRequest);
    }

    @Override
    public void checkArgs(String[] args) throws CommandException {
        if (args.length != 7) {
            logger.error("Usage: " + getUseage());
            throw new CommandException(CommandException.REASON_CODE_GETTCERT_COMMAND_ARGS_INVALID, "args is invalid =>" + getUseage());
        }
    }

    @Override
    public String getUseage() {
        return "ca-client tcert -h host -p port -a <json>";
    }


}
