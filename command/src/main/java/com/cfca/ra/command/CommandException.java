package com.cfca.ra.command;

import com.cfca.ra.command.utils.MyStringUtils;

import java.util.HashMap;

/**
 * @author zhangchong
 * @create 2018/5/11
 * @Description 命令异常类
 * @CodeReviewer
 * @since v3.0.0
 */
public class CommandException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 8383137635212345955L;

    public static final int REASON_CODE_CLIENT_EXCEPTION = 0x00;

    /**
     * 配置文件没有 CA 名称
     */
    public static final int REASON_CODE_CONFIG_MISSING_CA_NAME = 0x1006;

    /**
     * 配置文件没有指定 eroll 命令的 profile
     */
    public static final int REASON_CODE_CONFIG_MISSING_PROFILE = 0x1007;

    /**
     * 配置文件没有指定 eroll 命令
     */
    public static final int REASON_CODE_CONFIG_MISSING_ENROLLMENT = 0x1008;

    /**
     * 命令行参数缺少 host
     */
    public static final int REASON_CODE_BASE_COMMAND_ARGS_MISSING_HOST = 0x2001;
    /**
     * 命令行参数缺少 port
     */
    public static final int REASON_CODE_BASE_COMMAND_ARGS_MISSING_PORT = 0x2002;
    /**
     * 命令行参数缺少 content
     */
    public static final int REASON_CODE_BASE_COMMAND_ARGS_MISSING_CONTENT = 0x2003;
    /**
     * 存储CA CHAIN 文件失败
     */
    public static final int REASON_CODE_BASE_COMMAND_STORE_CA_CHAIN = 0x2004;

    /**
     * ENROLL 命令的参数无效
     */
    public static final int REASON_CODE_ENROLL_COMMAND_ARGS_INVALID = 0x5001;
    /**
     * ENROLL 命令网络通信失败
     */
    public static final int REASON_CODE_ENROLL_COMMAND_COMMS_FAILED = 0x5002;
    /**
     * ENROLL 命令加载配置文件失败
     */
    public static final int REASON_CODE_ENROLL_COMMAND_LOAD_CONFIG_FAILED = 0x5003;

    /**
     * GETCAINFO 命令网络通信失败
     */
    public static final int REASON_CODE_GETCAINFO_COMMAND_COMMS_FAILED = 0x6001;
    /**
     * GETCAINFO 命令初始化失败
     */
    public static final int REASON_CODE_GETCAINFO_COMMAND_INIT_MISSING_MSPDIR = 0x6002;
    /**
     * GETCAINFO 命令解析命令行参数失败
     */
    public static final int REASON_CODE_GETCAINFO_COMMAND_ARGS_INVALID = 0x6003;
    /**
     * GETCAINFO 命令加载配置文件失败
     */
    public static final int REASON_CODE_GETCAINFO_COMMAND_LOAD_CONFIG_FILE = 0x6004;

    /**
     * REENROLL 命令加载配置文件失败
     */
    public static final int REASON_CODE_REENROLL_COMMAND_LOAD_CONFIG_FAILED = 0x7002;
    /**
     * REENROLL 命令网络通信失败
     */
    public static final int REASON_CODE_REENROLL_COMMAND_COMMS_FAILED = 0x7003;
    /**
     * REENROLL 命令解析命令行参数失败
     */
    public static final int REASON_CODE_REENROLL_COMMAND_ARGS_INVALID = 0x7004;
    /**
     * REENROLL 命令解析配置文件缺失ENROLLMENT
     */
    public static final int REASON_CODE_REENROLL_COMMAND_CONFIG_MISSING_ENROLLMENT = 0x7005;

    /**
     * REGISTER 命令解析命令行参数失败
     */
    public static final int REASON_CODE_REGISTER_COMMAND_ARGS_INVALID = 0x8001;
    /**
     * REGISTER 命令网络通信失败
     */
    public static final int REASON_CODE_REGISTER_COMMAND_COMMS_FAILED = 0x8002;
    /**
     * REGISTER 命令服务器返回失败的回复
     */
    public static final int REASON_CODE_REGISTER_COMMAND_RESPONSE_NOT_SUCCESS = 0x8003;
    /**
     * REGISTER 命令加载注册数据库文件失败
     */
    public static final int REASON_CODE_REGISTER_COMMAND_LOAD_REGISTER_FILE = 0x8005;
    /**
     * REGISTER 命令更新注册数据库文件失败
     */
    public static final int REASON_CODE_REGISTER_COMMAND_UPDATE_REGISTER_FILE = 0x8006;

    /**
     * REVOKE 命令解析命令行参数失败
     */
    public static final int REASON_CODE_REVOKE_COMMAND_ARGS_INVALID = 0x9001;
    /**
     * REVOKE 命令网络通信失败
     */
    public static final int REASON_CODE_REVOKE_COMMAND_COMMS_FAILED = 0x9002;

    /**
     * GETTCERT 命令网络返回失败
     */
    public static final int REASON_CODE_GETTCERT_COMMAND_COMMS_FAILED = 0xa001;
    /**
     * GETTCERT 命令解析命令行参数失败
     */
    public static final int REASON_CODE_GETTCERT_COMMAND_ARGS_INVALID = 0xa002;

    /**
     * 内部客户端load identity失败
     */
    public static final int REASON_CODE_INTERNAL_CLIENT_LOAD_IDENTITY_EXCEPTION = 0xb002;
    /**
     * 内部客户端创建 cert file 文件路径失败
     */
    public static final int REASON_CODE_INTERNAL_CLIENT_BUILD_CERTFILE_PATH = 0xb003;
    /**
     * 内部客户端创建 private key file 文件路径失败
     */
    public static final int REASON_CODE_INTERNAL_CLIENT_BUILD_PRIVATEKEY_FILE_PATH = 0xb004;
    /**
     * 内部客户端存储 private key file 文件失败
     */
    public static final int REASON_CODE_INTERNAL_CLIENT_STORE_PRIVATEKEY_FAILED = 0xb005;
    /**
     * 内部客户端存储 identity 失败
     */
    public static final int REASON_CODE_INTERNAL_CLIENT_STORE_IDENTITY_FAILED = 0xb006;
    /**
     * 内部客户端生成 SM2 CSR 失败
     */
    public static final int REASON_CODE_INTERNAL_CLIENT_GEN_SM2_CSR_FAILED = 0xb007;
    /**
     * 内部客户端执行 enroll 失败,csr配置问题
     */
    public static final int REASON_CODE_INTERNAL_CLIENT_ENROLL_CSRCONFIG_EXCEPTION = 0xb008;
    /**
     * 内部客户端存储 serverinfo 失败
     */
    public static final int REASON_CODE_INTERNAL_CLIENT_STORE_LOCAL_SERVERINFO_EXCEPTION = 0xb009;
    /**
     * 内部客户端创建 identity 失败
     */
    public static final int REASON_CODE_INTERNAL_CLIENT_BUILD_IDENTITY_FAILED = 0xb00a;
    /**
     * 内部客户端创建 reenrollment 网络请求失败
     */
    public static final int REASON_CODE_INTERNAL_CLIENT_REENROLLMENT_BUILD_NET_REQUEST_FAILED = 0xb00b;
    /**
     * 内部客户端创建鉴权信息失败
     */
    public static final int REASON_CODE_INTERNAL_CLIENT_BUILD_BASICAUTH_EXCEPTION = 0xb00c;
    /**
     * 内部客户端创建CSR失败
     */
    public static final int REASON_CODE_INTERNAL_CLIENT_GENCSR_FAILED = 0xb00d;
    /**
     * 内部客户端创建失败
     */
    public static final int REASON_CODE_INTERNAL_CLIENT_INIT_FAILED = 0xb00e;
    /**
     * 内部客户端检查是否签发过证书失败
     */
    public static final int REASON_CODE_INTERNAL_CLIENT_CHECK_ENROLLMENT_EXCEPTION = 0xb00f;
    /**
     * 内部客户端 reenroll 失败
     */
    public static final int REASON_CODE_INTERNAL_CLIENT_REENROLL_EXCEPTION = 0xb010;
    /**
     * 内部客户端 register 后服务器回复空的密码
     */
    public static final int REASON_CODE_INTERNAL_CLIENT_REGISTER_RESPONSE_EMPTY_PASSWORD_FROM_SERVER = 0xb012;

    /**
     * 文件操作失败,检查文件是否存在失败
     */
    public static final int REASON_CODE_FILE_EXISTS = 0xc001;
    /**
     * 文件操作失败,创建新文件失败
     */
    public static final int REASON_CODE_FILE_CREATE_NEW_FILE = 0xc002;
    /**
     * 文件操作失败,获取文件目录失败
     */
    public static final int REASON_CODE_FILE_GET_DIR_PATH = 0xc003;
    /**
     * IDENTITY 创建签名鉴权失败
     */
    public static final int REASON_CODE_IDENTITY_CREATE_TOKEN = 0xd001;

    private String message;

    private int reasonCode;
    private Throwable cause;
    private HashMap<Integer, String> messageCatalog = new HashMap<Integer, String>(10) {
        {
            put(REASON_CODE_CLIENT_EXCEPTION, "client exception");

            put(REASON_CODE_BASE_COMMAND_ARGS_MISSING_HOST, "the base command fail to parse CLI parameters due to missing host");
            put(REASON_CODE_BASE_COMMAND_ARGS_MISSING_PORT, "the base command fail to parse CLI parameters due to missing port");
            put(REASON_CODE_BASE_COMMAND_ARGS_MISSING_CONTENT, "the base command fail to parse CLI parameters due to missing content");
            put(REASON_CODE_BASE_COMMAND_STORE_CA_CHAIN, "the base command fail to store ca chain file");

            put(REASON_CODE_ENROLL_COMMAND_ARGS_INVALID, "the enrollment command fail to parse CLI parameters");
            put(REASON_CODE_ENROLL_COMMAND_COMMS_FAILED, "the enrollment command failed to communicate with server");
            put(REASON_CODE_ENROLL_COMMAND_LOAD_CONFIG_FAILED, "the enrollment command failed to initiallize with config file");

            put(REASON_CODE_INTERNAL_CLIENT_LOAD_IDENTITY_EXCEPTION, "the internal client fail to load identity");
            put(REASON_CODE_INTERNAL_CLIENT_BUILD_CERTFILE_PATH, "the internal client fail to build cert file path");
            put(REASON_CODE_INTERNAL_CLIENT_BUILD_PRIVATEKEY_FILE_PATH, "the internal client fail to build private key file path");
            put(REASON_CODE_INTERNAL_CLIENT_STORE_PRIVATEKEY_FAILED, "the internal client fail to store private key file");
            put(REASON_CODE_INTERNAL_CLIENT_STORE_IDENTITY_FAILED, "the internal client fail to store identity");
            put(REASON_CODE_INTERNAL_CLIENT_GEN_SM2_CSR_FAILED, "the internal client fail to generate sm2 csr");
            put(REASON_CODE_INTERNAL_CLIENT_ENROLL_CSRCONFIG_EXCEPTION, "the internal client fail to enroll due to csr config");
            put(REASON_CODE_INTERNAL_CLIENT_STORE_LOCAL_SERVERINFO_EXCEPTION, "the internal client fail to store local serverinfo");
            put(REASON_CODE_INTERNAL_CLIENT_BUILD_IDENTITY_FAILED, "the internal client fail to build identity");
            put(REASON_CODE_INTERNAL_CLIENT_REENROLLMENT_BUILD_NET_REQUEST_FAILED, "the internal client fail to build reenrollment net request");
            put(REASON_CODE_INTERNAL_CLIENT_BUILD_BASICAUTH_EXCEPTION, "the internal client fail to build basic auth message");
            put(REASON_CODE_INTERNAL_CLIENT_GENCSR_FAILED, "the internal client fail to build generate csr");
            put(REASON_CODE_INTERNAL_CLIENT_INIT_FAILED, "the internal client fail to initialize");
            put(REASON_CODE_INTERNAL_CLIENT_CHECK_ENROLLMENT_EXCEPTION, "the internal client fail to check enrollment");

            put(REASON_CODE_GETCAINFO_COMMAND_COMMS_FAILED, "the getcainfo command fail to communicate with server");
            put(REASON_CODE_GETCAINFO_COMMAND_INIT_MISSING_MSPDIR, "the getcainfo command fail to init by missing mspdir");
            put(REASON_CODE_GETCAINFO_COMMAND_ARGS_INVALID, "the getcainfo command fail to parse CLI parameters");
            put(REASON_CODE_GETCAINFO_COMMAND_LOAD_CONFIG_FILE, "the getcainfo command fail to load config file");

            put(REASON_CODE_REENROLL_COMMAND_LOAD_CONFIG_FAILED, "the reenroll command fail to initiallize with config file");
            put(REASON_CODE_REENROLL_COMMAND_COMMS_FAILED, "the reenroll command fail to communicate with server");
            put(REASON_CODE_REENROLL_COMMAND_ARGS_INVALID, "the reenroll command fail to parse CLI parameters");
            put(REASON_CODE_REENROLL_COMMAND_CONFIG_MISSING_ENROLLMENT, "the reenroll command fail to initiallize with config file missing enrollment");

            put(REASON_CODE_REGISTER_COMMAND_ARGS_INVALID, "the register command fail to parse CLI parameters");
            put(REASON_CODE_REGISTER_COMMAND_COMMS_FAILED, "the register command fail to communicate with server");
            put(REASON_CODE_REGISTER_COMMAND_RESPONSE_NOT_SUCCESS, "the register command fail to get successful response from server");
            put(REASON_CODE_REGISTER_COMMAND_LOAD_REGISTER_FILE, "the register command fail to load register data file");
            put(REASON_CODE_REGISTER_COMMAND_UPDATE_REGISTER_FILE, "the register command fail to update register data file");

            put(REASON_CODE_REVOKE_COMMAND_ARGS_INVALID, "the revoke command fail to parse CLI parameters");
            put(REASON_CODE_REVOKE_COMMAND_COMMS_FAILED, "the revoke command fail to communicate with server");

            put(REASON_CODE_GETTCERT_COMMAND_COMMS_FAILED, "the gettcert command fail to communicate with server");
            put(REASON_CODE_GETTCERT_COMMAND_ARGS_INVALID, "the gettcert command fail to parse CLI parameters");

            put(REASON_CODE_CONFIG_MISSING_CA_NAME, "the config file missing ca name");
            put(REASON_CODE_CONFIG_MISSING_PROFILE, "the config file missing the profile of erollment");
            put(REASON_CODE_CONFIG_MISSING_ENROLLMENT, "the config file missing erollment");

            put(REASON_CODE_FILE_EXISTS, "fail to check file exists");
            put(REASON_CODE_FILE_CREATE_NEW_FILE, "fail to create new file");
            put(REASON_CODE_FILE_GET_DIR_PATH, "fail to get file dir");

            put(REASON_CODE_IDENTITY_CREATE_TOKEN, "fail to create token");

        }
    };


    public CommandException(int reasonCode) {
        super();
        this.reasonCode = reasonCode;
    }

    public CommandException(Throwable cause) {
        super();
        this.reasonCode = REASON_CODE_CLIENT_EXCEPTION;
        this.cause = cause;
    }

    public CommandException(int reason, Throwable cause) {
        super();
        this.reasonCode = reason;
        this.cause = cause;
    }

    public CommandException(int reason, String message, Throwable cause) {
        super();
        this.reasonCode = reason;
        this.message = message;
        this.cause = cause;
    }

    public CommandException(int reason, String message) {
        super();
        this.reasonCode = reason;
        this.message = message;
    }


    public int getReasonCode() {
        return reasonCode;
    }

    @Override
    public Throwable getCause() {
        return cause;
    }


    @Override
    public String getMessage() {
        if (!MyStringUtils.isBlank(message)) {
            return message;
        }
        return messageCatalog.getOrDefault(reasonCode, "Unknown error message");
    }

    @Override
    public String toString() {
        String result = getMessage() + " (" + reasonCode + ")";
        if (cause != null) {
            result = result + " - " + cause.toString();
        }
        return result;
    }
}
