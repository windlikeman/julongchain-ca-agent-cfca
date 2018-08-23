package org.bica.julongchain.cfca.ra.command.internal.enroll;

import java.util.Arrays;

import org.bica.julongchain.cfca.ra.command.CommandException;
import org.bica.julongchain.cfca.ra.command.config.ConfigBean;
import org.bica.julongchain.cfca.ra.command.config.CsrConfig;
import org.bica.julongchain.cfca.ra.command.internal.BaseClientCommand;
import org.bica.julongchain.cfca.ra.command.internal.Identity;
import org.bica.julongchain.cfca.ra.command.internal.ServerInfo;
import org.bica.julongchain.cfca.ra.command.utils.RandomUtils;
import org.bica.julongchain.cfca.ra.command.utils.StringUtils;
import org.bouncycastle.util.encoders.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

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
        if (!StringUtils.isBlank(content) && !EMPTY_JSON_STRING.equalsIgnoreCase(content)) {
            processContent();
        }
    }

    private void processContent() {
        final Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        final EnrollmentRequest overrideEnrollmentRequest = gson.fromJson(content, EnrollmentRequest.class);
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
        clientCfg.setSequenceNo(configBean.getSequenceNo());
    }

    @Override
    public void checkArgs(String[] args) throws CommandException {
        if (args.length != COMMAND_LINE_ARGS_NUM) {
            for (int i = 0; i < args.length; i++) {
                logger.error("args[{}]={}", i, args[i]);
            }
            logger.error("Usage : " + getUsage());
            throw new CommandException("fail to build enroll command ,because args is invalid : args[" + args.length + "]=" + Arrays.toString(args));
        }
    }

    @Override
    public JsonObject execute() throws CommandException {
        logger.info("EnrollCommand@execute : start");
        String url = clientCfg.getUrl();
        String seqNo = clientCfg.getSequenceNo();
        if (StringUtils.isEmpty(seqNo)) {
            seqNo = generateSequence();
            clientCfg.setSequenceNo(seqNo);
            replaceConfigSequenceNo(seqNo);
        }
        final EnrollmentResponse resp = enrollWithConfig(url);

        if (resp == null) {
            throw new CommandException("failed to execute, but I do not know why");
        }

        Identity id = resp.getIdentity();

        id.store();

        ServerInfo serverInfo = resp.getServerInfo();
        storeCAChain(clientCfg, serverInfo);

        final String enrollmentId = serverInfo.getEnrollmentId();
        replaceConfigCommonName(enrollmentId);
        final String username = clientCfg.getEnrollmentRequest().getUsername();
        enrollIdStore.updateEnrollIdStore(enrollmentId, username);
        replaceConfigUserNameAndPassword(clientCfg);
        logger.info("EnrollCommand@execute : updateEnrollIdStore {}=>{}", enrollmentId, username);
        return buildResult(id);
    }


    private String generateSequence() throws CommandException {
        return RandomUtils.createRandomString(10);
    }

    private JsonObject buildResult(Identity identity) {
        final byte[] cert = identity.getEcert().getCert();
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("cert", Base64.toBase64String(cert));
        return jsonObject;
    }

    private EnrollmentRequest buildEnrollmentRequestfromConfig(ConfigBean configBean) throws CommandException {
        clientCfg.setCaName(configBean.getCaname());
        clientCfg.setAdmin(configBean.getAdmin());
        clientCfg.setAdminpwd(configBean.getAdminpwd());

        final CsrConfig csr = configBean.getCsr();
        clientCfg.setCsrConfig(csr);
        String caName = configBean.getCaname();
        if (null == configBean.getEnrollment()) {
            throw new CommandException();
        }
        final String profile = configBean.getEnrollment().getProfile();
        return new EnrollmentRequest.Builder(null, configBean.getAdmin(), configBean.getAdminpwd(), profile, csr, caName).build();
    }

    /**
     * @param rawurl 已经解析好的url
     * @return EnrollmentResponse
     * @throws CommandException 失败则返回
     */
    private EnrollmentResponse enrollWithConfig(String rawurl) throws CommandException {
        if (StringUtils.isEmpty(rawurl)) {
            throw new CommandException("rawurl is empty");
        }

        ParsedUrl purl = parseRawurl(rawurl);

        final String host = purl.getHost();

        final String scheme = purl.getScheme();
        if (StringUtils.isEmpty(host)) {
            String expecting = String.format("%s://<host>", scheme);
            String message = String.format("The URL of the server is missing the host; found '%s' but expecting '%s'", rawurl, expecting);
            throw new CommandException(message);
        }

        final String username = purl.getUsername();
        final String password = purl.getPassword();

        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            throw new CommandException("missing the enrollment ID and secret");
        }
        final EnrollmentRequest enrollmentRequest = clientCfg.getEnrollmentRequest();
        if (null == enrollmentRequest) {
            throw new CommandException("clientCfg missing enrollmentRequest");
        }

        final CsrConfig csrConfig = enrollmentRequest.getCsrConfig();
        final String caName = enrollmentRequest.getCaName();
        if (StringUtils.isEmpty(caName)) {
            throw new CommandException("clientCfg missing caName");
        }

        String profile = enrollmentRequest.getProfile();
//        if (StringUtils.isEmpty(profile)) {
//            throw new CommandException("enrollmentRequest missing profile");
//        }
        final String request = enrollmentRequest.getRequest();
        if (StringUtils.isEmpty(request)) {
            throw new CommandException("enrollmentRequest missing request");
        }
        logger.info("enroll@execute : username={}", username);
        final EnrollmentRequest.Builder reqBuilder = new EnrollmentRequest.Builder(request, username, password, profile, csrConfig, caName);
        return client.enroll(reqBuilder.build());
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

        // remaining => localhost:7054
        try {
            String host = rawurl.substring(startIndex);
            final String username = clientCfg.getAdmin();
            final String password = clientCfg.getAdminpwd();
            return new ParsedUrl(scheme, host, username, password);
        } catch (Exception e) {
            throw new CommandException("invalid rawurl :" + rawurl, e);
        }
    }

    @Override
    public String getUsage() {
        return "ca-client enroll -h serverAddr -p serverPort -a <json string>";

    }
}
