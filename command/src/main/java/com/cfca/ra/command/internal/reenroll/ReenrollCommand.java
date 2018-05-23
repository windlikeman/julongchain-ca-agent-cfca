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
import com.cfca.ra.command.utils.ConfigUtils;
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
public class ReenrollCommand extends BaseClientCommand {
    private static final Logger logger = LoggerFactory.getLogger(ReenrollCommand.class);

    public ReenrollCommand() {
        this.name = COMMAND_NAME_REENROLL;
    }

    /**
     * ca-client reenroll -u http://serverAddr:serverPort
     *
     * @param args           命令行参数
     * @throws CommandException 失败则返回
     */
    @Override
    public void prepare(String[] args) throws CommandException {
        super.prepare(args);

        ConfigBean configBean = loadConfigFile();
        prepareClientConfig(configBean);
    }

    @Override
    public void checkArgs(String[] args) throws CommandException {
        if (args.length != COMMAND_LINE_ARGS_NUM) {
            logger.error("ca-client reenroll -h serverAddr -p serverPort -a <json string>");
            throw new CommandException(CommandException.REASON_CODE_REENROLL_COMMAND_ARGS_INVALID, "fail to build reenroll command ,because args is invalid : args=" + Arrays.toString(args));
        }
    }

    private ConfigBean loadConfigFile() throws CommandException {
        try {
            return ConfigUtils.load(this.cfgFileName);
        } catch (Exception e) {
            throw new CommandException(CommandException.REASON_CODE_REENROLL_COMMAND_LOAD_CONFIG_FAILED, "reenroll command failed to load config file:" + this.cfgFileName, e);
        }
    }

    @Override
    public void execute() throws CommandException {
        logger.info("Entered reenroll");
        Identity id = client.loadMyIdentity();

        ReenrollmentRequest request = buildReenrollmentRequest(clientCfg);
        final EnrollmentResponse resp = id.reenroll(request);
        if (resp == null) {
            throw new CommandException(CommandException.REASON_CODE_REENROLL_COMMAND_COMMS_FAILED, "reenroll command failed to execute, but I do not know why");
        }

        resp.getIdentity().store();

        ServerInfo serverInfo = resp.getServerInfo();
        storeCAChain(clientCfg, serverInfo);
    }

    private ReenrollmentRequest buildReenrollmentRequest(ClientConfig clientCfg) {
        final String username = clientCfg.getAdmin();
        final String password = clientCfg.getAdminpwd();
        final EnrollmentRequest req = clientCfg.getEnrollmentRequest();
        final String profile = req.getProfile();
        final CsrConfig csrConfig = req.getCsrConfig();
        final String caName = req.getCaName();
        return new ReenrollmentRequest.Builder(username, password, profile, csrConfig, caName).build();
    }

    private void prepareClientConfig(ConfigBean configBean) throws CommandException {
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
        clientCfg.setEnrollmentRequest(new EnrollmentRequest.Builder(null, null, profile, csr, caName).build());

    }

    @Override
    public String getUsage() {
        return "ca-client reenroll -h serverAddr -p serverPort -a <json string>";

    }
}
