package com.cfca.ra.command.internal.enroll;

import com.cfca.ra.command.CommandException;
import com.cfca.ra.command.config.ConfigBean;
import com.cfca.ra.command.config.CsrConfig;
import com.cfca.ra.command.internal.BaseClientCommand;
import com.cfca.ra.command.internal.Identity;
import com.cfca.ra.command.internal.ServerInfo;
import com.cfca.ra.command.utils.ConfigUtils;
import com.cfca.ra.command.utils.MyStringUtils;
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
public final class EnrollCommand extends BaseClientCommand {

    private static final Logger logger = LoggerFactory.getLogger(EnrollCommand.class);

    private static final String HTTP_PREFIX = "http://";
    private static final String HTTPS_PREFIX = "https://";

    public EnrollCommand() {
        this.name = COMMAND_NAME_ENROLL;
    }

    /**
     * ca-client enroll -u http://serverAddr:serverPort
     *
     * @param args 命令行参数
     * @throws CommandException 失败则返回
     */
    @Override
    public void prepare(String[] args) throws CommandException {
        super.prepare(args);

        processConfigFile();
        if (!MyStringUtils.isBlank(content) && !"{}".equalsIgnoreCase(content)) {
            processContent();
        }
    }

    private void processContent() {
        final EnrollmentRequest overrideEnrollmentRequest = new Gson().fromJson(content, EnrollmentRequest.class);
        clientCfg.setCaName(overrideEnrollmentRequest.getCaName());
        clientCfg.setAdmin(overrideEnrollmentRequest.getUsername());
        clientCfg.setAdminpwd(overrideEnrollmentRequest.getPassword());
        clientCfg.setCsrConfig(overrideEnrollmentRequest.getCsrConfig());
        clientCfg.setEnrollmentRequest(overrideEnrollmentRequest);
    }

    private void processConfigFile() throws CommandException {
        ConfigBean configBean = loadConfigFile();
        clientCfg.setCaName(configBean.getCaname());
        clientCfg.setAdmin(configBean.getAdmin());
        clientCfg.setAdminpwd(configBean.getAdminpwd());
        clientCfg.setCsrConfig(configBean.getCsr());
        clientCfg.setEnrollmentRequest(buildEnrollmentRequestfromConfig(configBean));
    }

    @Override
    public void checkArgs(String[] args) throws CommandException {
        if (args.length != COMMAND_LINE_ARGS_NUM) {
            logger.error("ca-client enroll -h serverAddr -p serverPort -a <json string>");
            throw new CommandException(CommandException.REASON_CODE_ENROLL_COMMAND_ARGS_INVALID, "fail to build enroll command ,because args is invalid : args=" + Arrays.toString(args));
        }
    }

    @Override
    public void execute() throws CommandException {
        logger.info("Entered enroll");
        String url = clientCfg.getUrl();
        final EnrollmentResponse resp = enrollWithConfig(url);

        if (resp == null) {
            throw new CommandException(CommandException.REASON_CODE_ENROLL_COMMAND_COMMS_FAILED, "failed to execute, but I do not know why");
        }


        Identity id = resp.getIdentity();

        id.store();

        ServerInfo serverInfo = resp.getServerInfo();
        storeCAChain(clientCfg, serverInfo);

        final String enrollmentId = serverInfo.getEnrollmentId();
        replaceConfigCommonName(enrollmentId);
        enrollIdStore.updateEnrollIdStore(enrollmentId, clientCfg.getEnrollmentRequest().getUsername());
    }

    private EnrollmentRequest buildEnrollmentRequestfromConfig(ConfigBean configBean) throws CommandException {
        clientCfg.setCaName(configBean.getCaname());
        clientCfg.setAdmin(configBean.getAdmin());
        clientCfg.setAdminpwd(configBean.getAdminpwd());

        final CsrConfig csr = configBean.getCsr();
        clientCfg.setCsrConfig(csr);
        String caName = configBean.getCaname();
        if (null == configBean.getEnrollment()) {
            throw new CommandException(CommandException.REASON_CODE_CONFIG_MISSING_ENROLLMENT);
        }
        final String profile = configBean.getEnrollment().getProfile();
        return new EnrollmentRequest.Builder(configBean.getAdmin(), configBean.getAdminpwd(), profile, csr, caName).build();
    }

    /**
     * @param rawurl 已经解析好的url
     * @return EnrollmentResponse
     * @throws CommandException 失败则返回
     */
    private EnrollmentResponse enrollWithConfig(String rawurl) throws CommandException {
        if (MyStringUtils.isEmpty(rawurl)) {
            throw new CommandException(CommandException.REASON_CODE_ENROLL_COMMAND_ARGS_INVALID, "rawurl is empty");
        }

        ParsedUrl purl = parseRawurl(rawurl);

        final String host = purl.getHost();

        final String scheme = purl.getScheme();
        if (MyStringUtils.isEmpty(host)) {
            String expecting = String.format(
                    "%s://<host>", scheme);
            String message = String.format("The URL of the server is missing the host; found '%s' but expecting '%s'", rawurl, expecting);
            throw new CommandException(CommandException.REASON_CODE_ENROLL_COMMAND_ARGS_INVALID, message);
        }

        final String username = purl.getUsername();
        final String password = purl.getPassword();

        if (MyStringUtils.isEmpty(username) || MyStringUtils.isEmpty(password)) {
            throw new CommandException(CommandException.REASON_CODE_ENROLL_COMMAND_ARGS_INVALID, "missing the enrollment ID and secret");
        }

        final CsrConfig csrConfig = clientCfg.getCsrConfig();
        final String caName = clientCfg.getCaName();
        if (MyStringUtils.isEmpty(caName)) {
            throw new CommandException(CommandException.REASON_CODE_CONFIG_MISSING_CA_NAME, "clientCfg missing caName");
        }
        String profile = clientCfg.getEnrollmentRequest().getProfile();
        if (MyStringUtils.isEmpty(profile)) {
            throw new CommandException(CommandException.REASON_CODE_CONFIG_MISSING_PROFILE, "enrollmentRequest missing profile");
        }
        final EnrollmentRequest enrollmentRequest = new EnrollmentRequest.Builder(username, password, profile, csrConfig, caName).build();

        return client.enroll(enrollmentRequest);

    }

    /**
     * @param rawurl %s://<enrollmentID>:<secret>@<host>
     * @return 解析后的username password host
     * @throws CommandException 遇到解析异常
     */
    ParsedUrl parseRawurl(String rawurl) throws CommandException {

        int startIndex = 0;
        String scheme = "http";
        if (rawurl.startsWith(HTTP_PREFIX)) {
            startIndex = rawurl.indexOf(HTTP_PREFIX) + HTTP_PREFIX.length();
        } else if (rawurl.startsWith(HTTPS_PREFIX)) {
            startIndex = rawurl.indexOf(HTTPS_PREFIX) + HTTPS_PREFIX.length();
            scheme = "https";
        }

        //remaining => localhost:7054
        try {
            String host = rawurl.substring(startIndex);
            final String username = clientCfg.getAdmin();
            final String password = clientCfg.getAdminpwd();
            return new ParsedUrl(scheme, host, username, password);
        } catch (Exception e) {
            throw new CommandException(CommandException.REASON_CODE_ENROLL_COMMAND_ARGS_INVALID, "invalid rawurl :" + rawurl);
        }
    }

    @Override
    public String getUsage() {
        return "ca-client enroll -h serverAddr -p serverPort -a <json string>";

    }
}
