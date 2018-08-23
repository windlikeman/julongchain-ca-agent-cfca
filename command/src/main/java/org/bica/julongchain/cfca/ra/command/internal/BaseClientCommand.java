package org.bica.julongchain.cfca.ra.command.internal;


import org.bica.julongchain.cfca.ra.command.CommandException;
import org.bica.julongchain.cfca.ra.command.IClientCommand;
import org.bica.julongchain.cfca.ra.command.config.ConfigBean;
import org.bica.julongchain.cfca.ra.command.config.CsrConfig;
import org.bica.julongchain.cfca.ra.command.internal.enroll.EnrollIdStore;
import org.bica.julongchain.cfca.ra.command.internal.enroll.IEnrollIdStore;
import org.bica.julongchain.cfca.ra.command.utils.ConfigUtils;
import org.bica.julongchain.cfca.ra.command.utils.FileUtils;
import org.bica.julongchain.cfca.ra.command.utils.StringUtils;
import org.bica.julongchain.cfca.ra.command.utils.PemUtils;
import org.bouncycastle.util.encoders.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * @author zhangchong
 * @create 2018/5/10
 * @Description RA命令接口类
 * @CodeReviewer
 * @since v3.0.0
 */
public abstract class BaseClientCommand implements IClientCommand {
    private static final Logger logger = LoggerFactory.getLogger(BaseClientCommand.class);

    protected static final int COMMAND_LINE_ARGS_NUM = 7;

    public static final String COMMAND_NAME_ENROLL = "enroll";
    public static final String COMMAND_NAME_REENROLL = "reenroll";
    public static final String COMMAND_NAME_GETCAINFO = "cainfo";
    public static final String COMMAND_NAME_REVOKE = "revoke";
    public static final String COMMAND_NAME_REGISTER = "register";
    public static final String COMMAND_NAME_HEARTBEAT = "heartbeat";

    private static final String CA_CLIENT_CONFIG_FILENAME = "ca-client-config.yaml";

    protected static final String EMPTY_JSON_STRING = "{}";
    private static final String USER_ADMIN = "admin";
    public static final String PEM_SUFFIX = ".pem";
    public static final String P7B_SUFFIX = ".p7b";


    protected final IEnrollIdStore enrollIdStore = EnrollIdStore.CFCA;
    /**
     * 具体命令的名字
     */
    protected String name;

    /**
     * 客户端的配置
     */
    protected ClientConfig clientCfg = ClientConfig.INSTANCE;

    /**
     * 命令的配置文件路径
     */
    protected String cfgFileName;

    /**
     * 命令的主目录
     */
    private String homeDirectory;

    /**
     * 命令的真正实现客户端
     */
    protected Client client;

    protected String host;
    protected String port;
    protected String content;

    /**
     * 描述用法
     *
     * @return 用法描述
     */
    public abstract String getUsage();

    /**
     * 检查命令行参数有效性
     *
     * @param args 命令行参数
     * @throws CommandException 遇到错误返回异常
     */
    public abstract void checkArgs(String[] args) throws CommandException;

    protected ConfigBean loadConfigFile() throws CommandException {
        try {
            return ConfigUtils.load(this.cfgFileName);
        } catch (Exception e) {
            throw new CommandException("failed to load config file:" + this.cfgFileName, e);
        }
    }

    protected void parseArgs(String[] args) throws CommandException {
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
                        String filename = args[i + 1];
                        try {
                            content = FileUtils.readFileToString(new File(filename), "UTF-8");
                        } catch (IOException e) {
                            throw new CommandException("fail to read file[" + filename + "] to string", e);
                        }
                    }
                    break;
                default:
                    break;
            }
        }
        if (StringUtils.isEmpty(host)) {
            String expecting = "-h host -p port";
            String message = String.format("The args of the command[" + name + "] is missing the host; found '%s' but expecting '%s'", Arrays.toString(args),
                    expecting);
            throw new CommandException(message);
        }
        if (StringUtils.isEmpty(port)) {
            String expecting = "-h host -p port -a<json string>";
            String message = String.format("The args of the command[" + name + "] is missing the host; found '%s' but expecting '%s'", Arrays.toString(args),
                    expecting);
            throw new CommandException(message);
        }
        if (StringUtils.isEmpty(content)) {
            String expecting = "-h host -p port -a<json string>";
            String message = String.format("The args of the command[" + name + "] is missing the content; found '%s' but expecting '%s'",
                    Arrays.toString(args), expecting);
            throw new CommandException(message);
        }
        clientCfg.setUrl("http://" + host + ":" + port);
    }

    @Override
    public void prepare(String[] args) throws CommandException {
        checkArgs(args);
        parseArgs(args);
        initializeConfig();
        initializeClient();
    }

    /**
     * 初始化配置文件,如果没有,则在运行目录下创建一个默认的配置文件
     *
     * @throws CommandException
     */
    private void initializeConfig() throws CommandException {
        validateAndReturnAbsConfigFile();
        if (shouldCreateDefaultConfig(this.name)) {
            try {
                if (!FileUtils.fileExists(this.cfgFileName)) {
                    this.cfgFileName = createDefaultConfigFile(CA_CLIENT_CONFIG_FILENAME);
                    logger.info("BaseClientCommand@initializeConfig : Created a default configuration file at {}",
                            this.cfgFileName);
                }
            } catch (Exception e) {
                logger.error("BaseClientCommand@initializeConfig : failed to check config file", e);
                throw new CommandException("failed to check config file ", e);
            }
        }
        String mspDir = clientCfg.getMspDir();
        if (StringUtils.isEmpty(mspDir) || ClientConfig.DEFAULT_CONFIG_MSPDIR_VAL.equalsIgnoreCase(mspDir)) {
            clientCfg.setMspDir("msp");
        }
        mspDir = FileUtils.makeFileAbs(clientCfg.getMspDir(), homeDirectory);
        clientCfg.setMspDir(mspDir);
    }

    private void initializeClient() throws CommandException {
        client = new Client(clientCfg, homeDirectory);
        if (requiresEnrollment()) {
            final ConfigBean configBean = loadConfigFile();
            final String enrollmentId = configBean.getCsr().getCn();
            final String userName = enrollIdStore.getUserName(enrollmentId);
            logger.info("BaseClientCommand@initializeClient : userName[{}]", userName);
            checkForEnrollment(userName);
        }
    }

    /**
     * 在执行 enroll 命令的时候,如果配置文件不存在, 创建一个默认的配置文件.
     * Enroll 应该是第一个执行命令
     *
     * @param cmdName
     * @return
     * @throws CommandException
     */
    private boolean shouldCreateDefaultConfig(String cmdName) throws CommandException {
        logger.info("ClientCommand@shouldCreateDefaultConfig : only enroll command need to create default config, " +
                "current command is {}", cmdName);
        if (StringUtils.isEmpty(cmdName)) {
            throw new CommandException("fail to create defaultConfig in 'shouldCreateDefaultConfig',because cmdName is empty");
        }
        return COMMAND_NAME_ENROLL.equals(cmdName);
    }

    private String createDefaultConfigFile(String fname) throws CommandException {
        try {
            String configFileName = getDefaultConfigFile(fname);
            FileUtils.createNewFile(configFileName);
            FileUtils.writeStringToFile(new File(configFileName), ConfigUtils.DEFAULT_CFG_TEMPLATE);
            return configFileName;
        } catch (Exception e) {
            logger.error("ClientCommand@createDefaultConfigFile : create default configFile at[" + this.cfgFileName + "] failed", e);
            throw new CommandException("create default configFile at[" + this.cfgFileName + "] failed", e);
        }
    }

    private void checkForEnrollment(String userName) throws CommandException {
        logger.info("ClientCommand@checkForEnrollment : enter userName[{}]", userName);
        client.checkEnrollment(userName);
    }

    private boolean requiresEnrollment() {
        return !name.equals(COMMAND_NAME_ENROLL) && !name.equals(COMMAND_NAME_GETCAINFO);
    }

    private void validateAndReturnAbsConfigFile() throws CommandException {
        boolean configFileSet = false;
        boolean homeDirSet = false;

        String defaultConfig = getDefaultConfigFile(CA_CLIENT_CONFIG_FILENAME);

        if (StringUtils.isEmpty(this.cfgFileName)) {
            this.cfgFileName = defaultConfig;
        } else {
            configFileSet = true;
        }

        if (StringUtils.isEmpty(this.homeDirectory)) {
            this.homeDirectory = FileUtils.getDir(defaultConfig);
        } else {
            homeDirSet = true;
        }

        this.homeDirectory = FileUtils.makeFileAbs(this.homeDirectory).trim();
        // 如果末尾是'/' 去掉它
        this.homeDirectory = StringUtils.trimRight(this.homeDirectory, File.separator);

        if (configFileSet && homeDirSet) {
            logger.info("validateAndReturnAbsConfigFile<<<<<<Using both --config and --home CLI flags; --config will take precedence");
        }

        if (configFileSet) {
            this.cfgFileName = FileUtils.makeFileAbs(this.cfgFileName);
            this.homeDirectory = FileUtils.getDir(this.cfgFileName);
            return;
        }

        this.cfgFileName = String.join(File.separator, this.homeDirectory, CA_CLIENT_CONFIG_FILENAME);
    }

    private String getDefaultConfigFile(String fname) {
        String home = System.getProperty("user.dir");

        StringBuilder builder = new StringBuilder(1024);
        return builder.append(home).append(File.separatorChar).append("ca-client").append(File.separatorChar).append("config").append(File.separatorChar)
                .append(fname).toString();
    }

    /**
     * @param clientCfg  客户端配置
     * @param serverInfo 返回的 CA 服务器信息
     * @throws CommandException 遇到错误返回异常
     */
    protected void storeCAChain(ClientConfig clientCfg, ServerInfo serverInfo) throws CommandException {
        final String mspDir = clientCfg.getMspDir();
        String fname = requireCAChainFileName(clientCfg);
        final String username = clientCfg.getAdmin();
        logger.info("BaseClientCommand@storeCAChain : username={}", username);
        String rootCACertsDir;
        if (!username.equals(USER_ADMIN)) {
            rootCACertsDir = String.join(File.separator, mspDir, username, "cacerts");
        } else {
            rootCACertsDir = String.join(File.separator, mspDir, "cacerts");
        }

        final String chainFile = String.join(File.separator, rootCACertsDir, fname);
        byte[] chain = serverInfo.getCaChain();
        logger.info("BaseClientCommand@storeCAChain : " + Hex.toHexString(chain));
        try {
            PemUtils.storeCaChain(chainFile, chain);
        } catch (IOException e) {
            logger.error("BaseClientCommand@storeCAChain : failed", e);
            throw new CommandException("ClientCommand@storeCAChain : failed", e);
        }
    }

    private String requireCAChainFileName(ClientConfig clientCfg) {
        String fileName = host + "-" + port;
        final String caName = clientCfg.getCaName();
        if (!StringUtils.isEmpty(caName)) {
            fileName = String.format("%s-%s", fileName, caName);
        }

        fileName = fileName.replace(":", "-");
        fileName = fileName.replace(".", "-");
        fileName = fileName + P7B_SUFFIX;
        return fileName;
    }

    protected void replaceConfigUserNameAndPassword(ClientConfig clientCfg) throws CommandException{
        ConfigBean configBean = loadConfigFile();
        logger.info("ClientCommand@replaceConfigUserNameAndPassword configBean : {}", configBean);
        configBean.setAdmin(clientCfg.getAdmin());
        configBean.setAdminpwd(clientCfg.getAdminpwd());
        updateConfigFile(configBean);
    }

    protected void replaceConfigCommonName(String enrollmentId) throws CommandException {
        ConfigBean configBean = loadConfigFile();
        logger.info("ClientCommand@replaceConfigCommonName configBean : {}", configBean);
        final CsrConfig csr = configBean.getCsr();
        csr.setCn(enrollmentId);
        configBean.setCsr(csr);
        updateConfigFile(configBean);
    }

    protected void replaceConfigSequenceNo(String seqNo) throws CommandException {
        ConfigBean configBean = loadConfigFile();
        logger.info("ClientCommand@replaceConfigSequenceNo configBean : {}", configBean);
        configBean.setSequenceNo(seqNo);
        updateConfigFile(configBean);
    }

    private void updateConfigFile(ConfigBean configBean) throws CommandException {
        try {
            logger.info("ClientCommand@updateConfigFile configBean : {}", configBean);
            ConfigUtils.update(this.cfgFileName, configBean);
        } catch (Exception e) {
            logger.error("ClientCommand@updateConfigFile : failed to update config file:" + this.cfgFileName, e);
            throw new CommandException("failed to update config file:" + this.cfgFileName, e);
        }
    }

}
