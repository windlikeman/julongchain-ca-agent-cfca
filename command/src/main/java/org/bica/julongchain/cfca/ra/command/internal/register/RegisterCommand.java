package org.bica.julongchain.cfca.ra.command.internal.register;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Pattern;

import org.bica.julongchain.cfca.ra.command.CommandException;
import org.bica.julongchain.cfca.ra.command.config.ConfigBean;
import org.bica.julongchain.cfca.ra.command.internal.BaseClientCommand;
import org.bica.julongchain.cfca.ra.command.internal.Identity;
import org.bica.julongchain.cfca.ra.command.utils.FileUtils;
import org.bica.julongchain.cfca.ra.command.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

/**
 * @author zhangchong
 * @create 2018/5/11
 * @Description 注册用户命令
 * @CodeReviewer
 * @since v3.0.0
 */
public final class RegisterCommand extends BaseClientCommand {

    private static final Logger logger = LoggerFactory.getLogger(RegisterCommand.class);
    private static final int MAX_NAME_LENGTH = 64;

    public RegisterCommand() {
        this.name = COMMAND_NAME_REGISTER;
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
        if (!StringUtils.isBlank(content) && !EMPTY_JSON_STRING.equalsIgnoreCase(content)) {
            processContent();
        }
    }

    private void processContent() throws CommandException {
        final Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        final RegistrationRequest registrationRequest = gson.fromJson(content, RegistrationRequest.class);
        logger.info(registrationRequest.toString());
        checkNameValid(registrationRequest.getName());
        clientCfg.setRegistrationRequest(registrationRequest);
    }

    private final static String PATTERN = "^[0-9a-zA-Z]{3,64}$";

    void checkNameValid(String name) throws CommandException {
        if (StringUtils.isBlank(name)) {
            throw new CommandException("RegisterCommand@checkNameValid : name[" + name + "] is Blank");
        }

        if (name.length() > MAX_NAME_LENGTH) {
            throw new CommandException("RegisterCommand@checkNameValid : name[" + name + "] is More than 64 bytes of specified length");
        }

        boolean isMatch = Pattern.matches(PATTERN, name);
        if (!isMatch) {
            throw new CommandException("RegisterCommand@checkNameValid : name[" + name + "] is invalid length or contains" + " special characters");
        }
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
            logger.error("Usage : {}", getUsage());
            throw new CommandException("fail to build register command ,because args is invalid : args=" + Arrays
                    .toString(args));
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
        if (StringUtils.isEmpty(secret)) {
            throw new CommandException();
        }

        final String name = registrationRequest.getName();
        logger.info("RegisterCommand<<<<<<new registered user : {}", name);

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
        userDir = FileUtils.makeFileAbs(userDir);
        boolean mkdirs = new File(userDir).mkdirs();
        if (!mkdirs) {
            logger.warn("RegisterCommand<<<<<<failed to create new user directory");
        }

    }

    @Override
    public String getUsage() {
        return "ca-client register -h host -p port -a <json string>";
    }
}
