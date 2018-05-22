package com.cfca.ra.command.internal;


import com.cfca.ra.command.ClientConfig;
import com.cfca.ra.command.CommandException;
import com.cfca.ra.command.IClientCommand;
import com.cfca.ra.command.utils.*;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.cert.X509Certificate;
import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * @author zhangchong
 * @create 2018/5/10
 * @Description RA命令接口类
 * @CodeReviewer
 * @since v3.0.0
 */
public abstract class BaseClientCommand implements IClientCommand {
    private static final Logger logger = LoggerFactory.getLogger(BaseClientCommand.class);

    public static final String COMMAND_NAME_ENROLL = "enroll";
    public static final String COMMAND_NAME_REENROLL = "reenroll";
    public static final String COMMAND_NAME_GETCAINFO = "cainfo";
    public static final String COMMAND_NAME_REVOKE = "revoke";
    public static final String COMMAND_NAME_GETTCERT = "tcert";
    public static final String COMMAND_NAME_REGISTER = "register";

    private static final String CA_CLIENT_CONFIG_FILENAME = "ca-client-config.yaml";
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
    protected String homeDirectory;

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
    public abstract String getUseage();

    public abstract void checkArgs(String[] args) throws CommandException;

    public void parseArgs(String[] args) throws CommandException {
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
                        content = args[i + 1];
                    }
                    break;
                default:
                    break;
            }
        }
        if(StringUtils.isEmpty(host)){
            String expecting = "-h host -p port";
            String message = String.format("The args of the command["+name+"] is missing the host; found '%s' but expecting '%s'", Arrays.toString(args), expecting);
            throw new CommandException(CommandException.REASON_CODE_BASE_COMMAND_ARGS_MISSING_HOST, message);
        }
        if(StringUtils.isEmpty(port)){
            String expecting = "-h host -p port -a<json string>";
            String message = String.format("The args of the command["+name+"] is missing the host; found '%s' but expecting '%s'", Arrays.toString(args), expecting);
            throw new CommandException(CommandException.REASON_CODE_BASE_COMMAND_ARGS_MISSING_PORT, message);
        }
        if(StringUtils.isEmpty(content)){
            String expecting = "-h host -p port -a<json string>";
            String message = String.format("The args of the command["+name+"] is missing the content; found '%s' but expecting '%s'", Arrays.toString(args), expecting);
            throw new CommandException(CommandException.REASON_CODE_BASE_COMMAND_ARGS_MISSING_CONTENT, message);
        }
        clientCfg.setUrl("http://"+host+":"+port);
    }

    @Override
    public void prepare(String[] args) throws CommandException {
        checkArgs(args);
        parseArgs(args);
        initializeConfig();
        initializeClient();
    }

    private void initializeConfig() throws CommandException {
        validateAndReturnAbsConfigFile();
        if (shouldCreateDefaultConfig(this.name)) {
            // 在执行 enroll 命令的时候,如果配置文件不存在, 创建一个默认的配置文件.
            // Enroll 应该是第一个执行命令
            try {
                if (!MyFileUtils.fileExists(this.cfgFileName)) {
                    this.cfgFileName = createDefaultConfigFile(CA_CLIENT_CONFIG_FILENAME);
                    logger.info("Created a default configuration file at {}", this.cfgFileName);
                }
            } catch (Exception e) {
                throw new CommandException(CommandException.REASON_CODE_CLIENT_EXCEPTION, "failed to check config file ", e);
            }
        }
    }

    private void initializeClient() throws CommandException {
        client = new Client(clientCfg, homeDirectory);
        if (requiresEnrollment()) {
            checkForEnrollment();
        }
    }

    private boolean shouldCreateDefaultConfig(String cmdName) throws CommandException {
        if (StringUtils.isEmpty(cmdName)) {
            throw new CommandException(CommandException.REASON_CODE_CLIENT_EXCEPTION, "fail to create defaultConfig in 'shouldCreateDefaultConfig',because cmdName is empty");
        }
        return COMMAND_NAME_ENROLL.equals(cmdName);
    }

    private String createDefaultConfigFile(String fname) throws CommandException {
        try {
            String configFileName = getDefaultConfigFile(fname);
            MyFileUtils.createNewFile(configFileName);
            FileUtils.writeStringToFile(new File(configFileName), ConfigUtils.DEFAULT_CFG_TEMPLATE);
            return configFileName;
        } catch (Exception e) {
            throw new CommandException(CommandException.REASON_CODE_CLIENT_EXCEPTION, "create default configFile at[" + this.cfgFileName + "] failed", e);
        }
    }

    private void checkForEnrollment() throws CommandException {
        logger.info("Checking for enrollment");
        client.checkEnrollment();
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
            this.homeDirectory = MyFileUtils.getDir(defaultConfig);
        } else {
            homeDirSet = true;
        }

        this.homeDirectory = MyFileUtils.makeFileAbs(this.homeDirectory).trim();
        //如果末尾是'/' 去掉它
        this.homeDirectory = StringUtils.trimRight(this.homeDirectory, File.separator);

        if (configFileSet && homeDirSet) {
            logger.info("Using both --config and --home CLI flags; --config will take precedence");
        }

        if (configFileSet) {
            this.cfgFileName = MyFileUtils.makeFileAbs(this.cfgFileName);
            this.homeDirectory = MyFileUtils.getDir(this.cfgFileName);
            return;
        }

        this.cfgFileName = String.join(File.separator, this.homeDirectory, CA_CLIENT_CONFIG_FILENAME);
    }

    private String getDefaultConfigFile(String fname) {
        String home = System.getProperty("user.dir");

        StringBuilder builder = new StringBuilder(1024);
        return builder.append(home).append(File.separatorChar)
                .append("ca-client").append(File.separatorChar)
                .append("config").append(File.separatorChar)
                .append(fname).toString();
    }


    /**
     * @param clientCfg
     * @param serverInfo
     * @throws CommandException
     */
    void storeCAChain(ClientConfig clientCfg, ServerInfo serverInfo) throws CommandException {
//        final String mspDir = clientCfg.getMspDir();
//        final String url = clientCfg.getUrl();
//        String fname = requireCAChainFileName(url);
//        if (StringUtils.isEmpty(clientCfg.getCaName())) {
//            fname = String.format("%s-%s", fname, clientCfg.getCaName());
//        }
//        fname = fname.replace(":", "-");
//        fname = fname.replace(".", "-");
//        fname = fname + ".pem";
//        String tlsfname = String.format("tls-%s", fname);
//
//        String rootCACertsDir = String.join(File.separator, mspDir, "cacerts");
//        String intCACertsDir = String.join(File.separator, mspDir, "intermediatecerts");
//        String tlsRootCACertsDir = String.join(File.separator, mspDir, "tlscacerts");
//        String tlsIntCACertsDir = String.join(File.separator, mspDir, "tlsintermediatecerts");
//
//        byte[] chain = serverInfo.getCaChain();
//        // 解析根证书链将字节解析成 X509证书数组
//        List<X509Certificate> certs = CertUtils.buildCertsFromP7b(chain);
//        // 将根证书存入 "cacerts" msp 文件夹,约定第一个位置的证书就是根证书
//        if (certs != null && !certs.isEmpty()) {
//            X509Certificate cert = certs.get(0);
//            if (!CertUtils.isCA(cert)) {
//                throw new CommandException(CommandException.REASON_CODE_CLIENT_EXCEPTION, "A certificate in the CA chain is not a CA certificate");
//            }
//        }
//        PemUtils.storeCert(rootCACertsDir, fname, cert);
    }

    private String requireCAChainFileName(String url) {
        logger.info("requireCAChainFileName>>>>>>Running: url=" + url);
        return "";
    }

}
