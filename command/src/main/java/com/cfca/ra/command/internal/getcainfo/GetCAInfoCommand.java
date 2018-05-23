package com.cfca.ra.command.internal.getcainfo;

import com.cfca.ra.command.CommandException;
import com.cfca.ra.command.config.ConfigBean;
import com.cfca.ra.command.internal.BaseClientCommand;
import com.cfca.ra.command.internal.ServerInfo;
import com.cfca.ra.command.utils.ConfigUtils;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhangchong
 * @create 2018/5/13
 * @Description GetCAInfo 命令对象,处理 GetCAInfo 操作
 * @CodeReviewer
 * @since v3.0.0
 */
public class GetCAInfoCommand extends BaseClientCommand {

    private static final Logger logger = LoggerFactory.getLogger(GetCAInfoCommand.class);
    private String mspDir;

    public GetCAInfoCommand() {
        this.name = COMMAND_NAME_GETCAINFO;
    }

    @Override
    public void prepare(String[] args) throws CommandException {
        super.prepare(args);

        ConfigBean configBean = loadConfigFile();
        clientCfg.setMspDir(configBean.getMspdir());
        final GetCAInfoRequest getCAInfoRequest = new Gson().fromJson(content, GetCAInfoRequest.class);
        logger.error("prepare<<<<<<" + getCAInfoRequest.toString());
        clientCfg.setGetCAInfoRequest(getCAInfoRequest);
        clientCfg.setCaName(getCAInfoRequest.getCaName());

        mspDir = clientCfg.getMspDir();
        if (mspDir.isEmpty()) {
            throw new CommandException(CommandException.REASON_CODE_GETCAINFO_COMMAND_INIT_MISSING_MSPDIR, "fail to prepare getcainfo command ,because mspDir is empty");
        }
    }

    @Override
    public void checkArgs(String[] args) throws CommandException {
        if (args.length != COMMAND_LINE_ARGS_NUM) {
            logger.error("Useage : " + getUsage());
            throw new CommandException(CommandException.REASON_CODE_GETCAINFO_COMMAND_ARGS_INVALID, "getcainfo command args is invalid =>" + getUsage());
        }
    }

    @Override
    public void execute() throws CommandException {
        logger.info("Entered getcainfo <<<<<< clientCfg:"+clientCfg.toString());
        GetCAInfoRequest req = clientCfg.getGetCAInfoRequest();

        ServerInfo si = client.getCAInfo(req);
        storeCAChain(client.getClientCfg(), si);
    }

    @Override
    public String getUsage() {
        /**
         * "ca-client cainfo -u http://serverAddr:serverPort -M <MSP-directory>"
         */
        return "ca-client cainfo -h host -p port -a <json>";
    }

    private ConfigBean loadConfigFile() throws CommandException {
        try {
            return ConfigUtils.load(this.cfgFileName);
        } catch (Exception e) {
            throw new CommandException(CommandException.REASON_CODE_GETCAINFO_COMMAND_LOAD_CONFIG_FILE, "failed to load config file:" + this.cfgFileName, e);
        }
    }
}
