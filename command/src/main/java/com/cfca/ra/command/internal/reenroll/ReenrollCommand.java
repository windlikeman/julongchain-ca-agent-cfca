package com.cfca.ra.command.internal.reenroll;

import com.cfca.ra.command.CommandException;
import com.cfca.ra.command.config.ConfigBean;
import com.cfca.ra.command.config.CsrConfig;
import com.cfca.ra.command.internal.BaseClientCommand;
import com.cfca.ra.command.internal.ClientConfig;
import com.cfca.ra.command.internal.Identity;
import com.cfca.ra.command.internal.ServerInfo;
import com.cfca.ra.command.internal.enroll.EnrollmentRequest;
import com.cfca.ra.command.internal.enroll.EnrollmentResponse;
import com.cfca.ra.command.utils.CsrUtils;
import com.cfca.ra.command.utils.MyStringUtils;
import com.cfca.ra.command.utils.PemUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.apache.commons.io.FileUtils;
import org.bouncycastle.util.encoders.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.security.PrivateKey;
import java.util.Arrays;

/**
 * @author zhangchong
 * @create 2018/5/11
 * @Description 签发证书命令
 * @CodeReviewer
 * @since v3.0.0
 */
public class ReenrollCommand extends BaseClientCommand {
    private static final Logger logger = LoggerFactory.getLogger(ReenrollCommand.class);
    private String tmpKeyFile;

    public ReenrollCommand() {
        this.name = COMMAND_NAME_REENROLL;
    }

    /**
     * ca-client reenroll -u http://serverAddr:serverPort
     *
     * @param args 命令行参数
     * @throws CommandException 失败则返回
     */
    @Override
    public void prepare(String[] args) throws CommandException {
        super.prepare(args);

        processConfigFile();
        if (!MyStringUtils.isBlank(content) && !EMPTY_JSON_STRING.equalsIgnoreCase(content)) {
            processContent();
        }
    }

    @Override
    protected void parseArgs(String[] args) throws CommandException {
        String filename;
        final String expecting = "-h host -p port -a <json string>  -key <newkeyfile>";
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
                case "-a":
                    if (i + 1 < size) {
                        filename = args[i + 1];
                        try {
                            content = FileUtils.readFileToString(new File(filename), "UTF-8");
                        } catch (IOException e) {
                            throw new CommandException(CommandException.REASON_CODE_BASE_COMMAND_ARGS_MISSING_CONTENT, "fail to read file[" + filename + "] to string", e);
                        }
                    }
                    break;
                case "-key":
                    if (i + 1 < size) {
                        tmpKeyFile = args[i + 1];
                    }
                    break;
                default:
                    break;
            }
        }
        if (MyStringUtils.isEmpty(host)) {
            String message = String.format("The args of the command[" + name + "] is missing the host; found '%s' but expecting '%s'", Arrays.toString(args),
                    expecting);
            throw new CommandException(CommandException.REASON_CODE_BASE_COMMAND_ARGS_MISSING_HOST, message);
        }
        if (MyStringUtils.isEmpty(port)) {

            String message = String.format("The args of the command[" + name + "] is missing the host; found '%s' but expecting '%s'", Arrays.toString(args),
                    expecting);
            throw new CommandException(CommandException.REASON_CODE_BASE_COMMAND_ARGS_MISSING_PORT, message);
        }
        if (MyStringUtils.isEmpty(content)) {
            String message = String.format("The args of the command[" + name + "] is missing the content; found '%s' but expecting '%s'",
                    Arrays.toString(args), expecting);
            throw new CommandException(CommandException.REASON_CODE_BASE_COMMAND_ARGS_MISSING_CONTENT, message);
        }
        if (MyStringUtils.isEmpty(tmpKeyFile)) {
            String message = String.format("The args of the command[" + name + "] is missing the tmpKeyFile; found '%s' but expecting '%s'",
                    Arrays.toString(args), expecting);
            throw new CommandException(CommandException.REASON_CODE_BASE_COMMAND_ARGS_MISSING_CONTENT, message);
        }


        clientCfg.setUrl("http://" + host + ":" + port);
    }

    private void processContent() {
        final Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        final EnrollmentRequest enrollmentRequest = gson.fromJson(content, EnrollmentRequest.class);
        logger.info(enrollmentRequest.toString());
        clientCfg.setEnrollmentRequest(enrollmentRequest);
    }

    private void processConfigFile() throws CommandException {
        ConfigBean configBean = loadConfigFile();
        clientCfg.setCaName(configBean.getCaname());
        clientCfg.setAdmin(configBean.getAdmin());
        clientCfg.setAdminpwd(configBean.getAdminpwd());
        final CsrConfig csr = configBean.getCsr();
        clientCfg.setCsrConfig(csr);
        String caName = configBean.getCaname();
        if (null == configBean.getEnrollment()) {
            throw new CommandException(CommandException.REASON_CODE_REENROLL_COMMAND_CONFIG_MISSING_ENROLLMENT);
        }
        final String profile = configBean.getEnrollment().getProfile();
        clientCfg.setEnrollmentRequest(new EnrollmentRequest.Builder(null, null, null, profile, csr, caName).build());
        clientCfg.setEnrollmentId(csr.getCn());
    }

    @Override
    public void checkArgs(String[] args) throws CommandException {
        if (args.length != COMMAND_LINE_ARGS_NUM + 2) {
            for (int i = 0; i < args.length; i++) {
                logger.error("args[{}]={}", i, args[i]);
            }
            logger.error("Usage : " + getUsage());
            throw new CommandException(CommandException.REASON_CODE_REENROLL_COMMAND_ARGS_INVALID,
                    "fail to build reenroll command ,because args is invalid : args["+args.length+"]=" + Arrays.toString(args));
        }
    }

    @Override
    public JsonObject execute() throws CommandException {
        logger.info("Entered reenroll");
        Identity id = client.loadMyIdentity();

        ReenrollmentRequest request = buildReenrollmentRequest(clientCfg);
        final EnrollmentResponse resp = id.reenroll(request);
        if (resp == null) {
            throw new CommandException(CommandException.REASON_CODE_REENROLL_COMMAND_COMMS_FAILED, "reenroll command failed to execute, but I do not know why");
        }

        final Identity identity = resp.getIdentity();
        identity.store();

        ServerInfo serverInfo = resp.getServerInfo();
        storeCAChain(clientCfg, serverInfo);
        final String enrollmentId = serverInfo.getEnrollmentId();
        replaceConfigCommonName(enrollmentId);
        enrollIdStore.updateEnrollIdStore(enrollmentId, clientCfg.getEnrollmentRequest().getUsername());

        try {
            final PrivateKey privateKey = PemUtils.loadPrivateKey(tmpKeyFile);
            CsrUtils.storePrivateKey(privateKey, id.getKeyFile());
        } catch (IOException e) {
            throw new CommandException(CommandException.REASON_CODE_REENROLL_COMMAND_COMMS_FAILED, "reenroll command failed to store PrivateKey due to " + e.getMessage(), e);
        }

        return buildResult(identity);
    }

    private JsonObject buildResult(Identity identity) {
        final byte[] cert = identity.getEcert().getCert();
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("cert", Base64.toBase64String(cert));
        return jsonObject;
    }

    private ReenrollmentRequest buildReenrollmentRequest(ClientConfig clientCfg) {
        final String username = clientCfg.getAdmin();
        final String password = clientCfg.getAdminpwd();
        final EnrollmentRequest req = clientCfg.getEnrollmentRequest();
        final String profile = req.getProfile();
        final CsrConfig csrConfig = req.getCsrConfig();
        final String caName = req.getCaName();
        final String request = req.getRequest();
        return new ReenrollmentRequest.Builder(request, username, password, profile, csrConfig, caName).build();
    }

    @Override
    public String getUsage() {
        return "ca-client reenroll -h serverAddr -p serverPort -a <json string> -key <newkeyfile>";

    }
}
