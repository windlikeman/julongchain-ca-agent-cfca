package com.cfca.ra.command.internal.register;

import com.cfca.ra.command.CommandException;
import com.cfca.ra.command.config.ConfigBean;
import com.cfca.ra.command.internal.BaseClientCommand;
import com.cfca.ra.command.internal.Identity;
import com.cfca.ra.command.utils.ConfigUtils;
import com.cfca.ra.command.utils.MyFileUtils;
import com.cfca.ra.command.utils.MyStringUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.apache.commons.io.FileUtils;
import org.bouncycastle.util.encoders.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhangchong
 * @create 2018/5/11
 * @Description 注册用户命令
 * @CodeReviewer
 * @since v3.0.0
 */
public final class RegisterCommand extends BaseClientCommand {

    private static final Logger logger = LoggerFactory.getLogger(RegisterCommand.class);

    private final ConcurrentHashMap<String, String> registered;

    private final String registerFile = "registers.dat";

    public RegisterCommand() {
        this.name = COMMAND_NAME_REGISTER;
        registered = new ConcurrentHashMap<String, String>();
    }

    /**
     * ca-client register -h host -p port -a <json string>
     *
     * @param args
     *            命令行参数
     * @throws CommandException
     *             失败则返回
     */
    @Override
    public void prepare(String[] args) throws CommandException {
        super.prepare(args);

        processConfigFile();
        if (!MyStringUtils.isBlank(content) && !EMPTY_JSON_STRING.equalsIgnoreCase(content)) {
            processContent();
        }
    }

    private void processContent() {
        final Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        final RegistrationRequest registrationRequest = gson.fromJson(content, RegistrationRequest.class);
        logger.info(registrationRequest.toString());
        clientCfg.setRegistrationRequest(registrationRequest);
    }

    private void processConfigFile() throws CommandException {
        ConfigBean configBean = loadConfigFile();
        clientCfg.setCaName(configBean.getCaname());
        clientCfg.setAdmin(configBean.getAdmin());
        clientCfg.setAdminpwd(configBean.getAdminpwd());
        clientCfg.setEnrollmentId(configBean.getCsr().getCn());
    }

    @Override
    public void checkArgs(String[] args) throws CommandException {
        if (args.length != COMMAND_LINE_ARGS_NUM) {
            logger.error("Usage : " + getUsage());
            throw new CommandException(CommandException.REASON_CODE_REGISTER_COMMAND_ARGS_INVALID,
                    "fail to build enroll command ,because args is invalid : args=" + Arrays.toString(args));
        }
    }

    @Override
    public JsonObject execute() throws CommandException {
        logger.info("RegisterCommand Running");

        RegistrationRequest registrationRequest = clientCfg.getRegistrationRequest();

        Identity id = client.loadMyIdentity();
        RegistrationResponse resp = id.register(registrationRequest);

        final String secret = resp.getSecret();
        logger.info("execute<<<<<<Password: {}", secret);
        if (MyStringUtils.isEmpty(secret)) {
            throw new CommandException(CommandException.REASON_CODE_REGISTER_COMMAND_RESPONSE_NOT_SUCCESS);
        }

        final String name = registrationRequest.getName();
        logger.info("RegisterCommand<<<<<<new registered user : {}", name);
        registered.put(name, secret);
        updateRegisterFile();
        mkNewUserDir(name);
        return buildResult(secret);
    }

    private JsonObject buildResult(String s) {
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("credentials", s);
        return jsonObject;
    }

    private void mkNewUserDir(String name) {
        final String mspDir = clientCfg.getMspDir();
        String userDir = String.join(File.separator, mspDir, name);
        userDir = MyFileUtils.makeFileAbs(userDir);
        boolean mkdirs = new File(userDir).mkdirs();
        if (!mkdirs) {
            logger.warn("RegisterCommand<<<<<<failed to create new user directory");
        }

    }

    @Override
    public String getUsage() {
        return "ca-client register -h host -p port -a <json string>";
    }

    private ConcurrentHashMap<String, String> loadRegisterFile() throws CommandException {
        ConcurrentHashMap<String, String> enrollIdStore = new ConcurrentHashMap<>(30);
        final String homeDir = clientCfg.getMspDir();
        File file = new File(String.join(File.separator, homeDir, registerFile));
        if (!file.exists()) {
            enrollIdStore.put("admin", "admin");
            return enrollIdStore;
        }
        try {
            final String s = FileUtils.readFileToString(file);
            logger.info("RegisterCommand<<<<<<loadRegisterFile file content:\n" + s);
            final Map map = new Gson().fromJson(s, Map.class);
            Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, String> entry = it.next();
                enrollIdStore.put(entry.getKey(), entry.getValue());
            }
            return enrollIdStore;
        } catch (Exception e) {
            throw new CommandException(CommandException.REASON_CODE_REGISTER_COMMAND_LOAD_REGISTER_FILE, e);
        }
    }

    private void updateRegisterFile() throws CommandException {
        try {
            final String homeDir = clientCfg.getMspDir();
            File file = new File(String.join(File.separator, homeDir, registerFile));
            Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
            final String s = gson.toJson(registered);
            logger.info("RegisterCommand<<<<<<updateRegisterFile file content:{}", s);
            FileUtils.writeStringToFile(file, s);
        } catch (IOException e) {
            throw new CommandException(CommandException.REASON_CODE_REGISTER_COMMAND_UPDATE_REGISTER_FILE, e);
        }
    }
}
