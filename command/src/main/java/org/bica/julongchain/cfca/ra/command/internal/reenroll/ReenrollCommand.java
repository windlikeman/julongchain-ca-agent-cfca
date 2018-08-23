package org.bica.julongchain.cfca.ra.command.internal.reenroll;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.apache.commons.io.FileUtils;
import org.bica.julongchain.cfca.ra.command.CommandException;
import org.bica.julongchain.cfca.ra.command.config.ConfigBean;
import org.bica.julongchain.cfca.ra.command.config.CsrConfig;
import org.bica.julongchain.cfca.ra.command.internal.BaseClientCommand;
import org.bica.julongchain.cfca.ra.command.internal.ClientConfig;
import org.bica.julongchain.cfca.ra.command.internal.Identity;
import org.bica.julongchain.cfca.ra.command.internal.ServerInfo;
import org.bica.julongchain.cfca.ra.command.internal.enroll.EnrollmentRequest;
import org.bica.julongchain.cfca.ra.command.internal.enroll.EnrollmentResponse;
import org.bica.julongchain.cfca.ra.command.utils.CsrUtils;
import org.bica.julongchain.cfca.ra.command.utils.RandomUtils;
import org.bica.julongchain.cfca.ra.command.utils.StringUtils;
import org.bica.julongchain.cfca.ra.command.utils.PemUtils;
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
        if (!StringUtils.isBlank(content) && !EMPTY_JSON_STRING.equalsIgnoreCase(content)) {
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
                            throw new CommandException("fail to read file[" + filename + "] to string", e);
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
        if (StringUtils.isEmpty(host)) {
            String message = String.format("The args of the command[" + name + "] is missing the host; found '%s' but expecting '%s'", Arrays.toString(args),
                    expecting);
            throw new CommandException(message);
        }
        if (StringUtils.isEmpty(port)) {

            String message = String.format("The args of the command[" + name + "] is missing the host; found '%s' but expecting '%s'", Arrays.toString(args),
                    expecting);
            throw new CommandException(message);
        }
        if (StringUtils.isEmpty(content)) {
            String message = String.format("The args of the command[" + name + "] is missing the content; found '%s' but expecting '%s'",
                    Arrays.toString(args), expecting);
            throw new CommandException(message);
        }
        if (StringUtils.isEmpty(tmpKeyFile)) {
            String message = String.format("The args of the command[" + name + "] is missing the tmpKeyFile; found '%s' but expecting '%s'",
                    Arrays.toString(args), expecting);
            throw new CommandException(message);
        }


        clientCfg.setUrl("http://" + host + ":" + port);
    }

    private void processContent() {
        final Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        final EnrollmentRequest enrollmentRequest = gson.fromJson(content, EnrollmentRequest.class);
        logger.info(enrollmentRequest.toString());
        clientCfg.setAdmin(enrollmentRequest.getUsername());
        clientCfg.setAdminpwd(enrollmentRequest.getPassword());
        clientCfg.setEnrollmentRequest(enrollmentRequest);
    }

    private void processConfigFile() throws CommandException {
        ConfigBean configBean = loadConfigFile();
        clientCfg.setCaName(configBean.getCaname());
        clientCfg.setAdmin(configBean.getAdmin());
        clientCfg.setAdminpwd(configBean.getAdminpwd());
        clientCfg.setSequenceNo(configBean.getSequenceNo());
        final CsrConfig csr = configBean.getCsr();
        clientCfg.setCsrConfig(csr);
        String caName = configBean.getCaname();
        if (null == configBean.getEnrollment()) {
            throw new CommandException("the reenroll command fail to initiallize with config file missing enrollment");
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
            throw new CommandException("fail to build reenroll command ,because args is invalid : args["+args.length+"]=" + Arrays.toString(args));
        }
    }

    @Override
    public JsonObject execute() throws CommandException {
        logger.info("ReenrollCommand@execute : Entered reenroll");

        Identity id = client.loadMyIdentity();

        ReenrollmentRequest request = buildReenrollmentRequest(clientCfg);
        final EnrollmentResponse resp = id.reenroll(request);
        if (resp == null) {
            throw new CommandException("reenroll command failed to execute, but I do not know why");
        }

        final Identity identity = resp.getIdentity();
        ServerInfo serverInfo = resp.getServerInfo();
        storeCAChain(clientCfg, serverInfo);
        final String enrollmentId = serverInfo.getEnrollmentId();
        replaceConfigCommonName(enrollmentId);
        enrollIdStore.updateEnrollIdStore(enrollmentId, clientCfg.getEnrollmentRequest().getUsername());
        replaceConfigUserNameAndPassword(clientCfg);
        try {
            final PrivateKey privateKey = PemUtils.loadPrivateKey(tmpKeyFile);
            //FIXME 存储失败还要回滚
            CsrUtils.storePrivateKey(privateKey, id.getKeyFile());
            identity.store();
        } catch (IOException e) {
            logger.error("reenroll command failed to store PrivateKey", e);
            throw new CommandException("reenroll command failed to store PrivateKey", e);
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
