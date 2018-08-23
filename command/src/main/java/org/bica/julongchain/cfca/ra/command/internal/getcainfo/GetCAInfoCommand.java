package org.bica.julongchain.cfca.ra.command.internal.getcainfo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.bica.julongchain.cfca.ra.command.CommandException;
import org.bica.julongchain.cfca.ra.command.config.ConfigBean;
import org.bica.julongchain.cfca.ra.command.internal.BaseClientCommand;
import org.bica.julongchain.cfca.ra.command.internal.ServerInfo;
import org.bouncycastle.util.encoders.Base64;
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
        clientCfg.setAdmin(configBean.getAdmin());
        clientCfg.setMspDir(configBean.getMspdir());

        final Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        final GetCAInfoRequest getCAInfoRequest = gson.fromJson(content, GetCAInfoRequest.class);
        logger.info("prepare<<<<<<" + getCAInfoRequest.toString());
        clientCfg.setGetCAInfoRequest(getCAInfoRequest);
        clientCfg.setCaName(getCAInfoRequest.getCaName());

        mspDir = clientCfg.getMspDir();
        if (mspDir.isEmpty()) {
            throw new CommandException("fail to prepare getcainfo command ,because mspDir is empty");
        }
    }

    @Override
    public void checkArgs(String[] args) throws CommandException {
        if (args.length != COMMAND_LINE_ARGS_NUM) {
            logger.error("Usage : " + getUsage());
            throw new CommandException("getcainfo command args is invalid =>" + getUsage());
        }
    }

    @Override
    public JsonObject execute() throws CommandException {
        logger.info("Entered getcainfo <<<<<< clientCfg:" + clientCfg.toString());
        GetCAInfoRequest req = clientCfg.getGetCAInfoRequest();

        ServerInfo si = client.getCAInfo(req);
        storeCAChain(client.getClientCfg(), si);

        return buildResult(si);
    }

    private JsonObject buildResult(ServerInfo si) {
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("caname", si.getCaName());
        jsonObject.addProperty("cachain", Base64.toBase64String(si.getCaChain()));
        return jsonObject;
    }

    @Override
    public String getUsage() {
        /**
         * "ca-client cainfo -u http://serverAddr:serverPort -M <MSP-directory>"
         */
        return "ca-client cainfo -h host -p port -a <json>";
    }

}
