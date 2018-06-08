package com.cfca.ra;


import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhangchong
 * @create 2018/5/11
 * @Description 命令异常类
 * @CodeReviewer
 * @since v3.0.0
 */
public class RAServerException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 8383137633372345955L;

    public static final int REASON_CODE_SERVER_EXCEPTION = 0x00;

    /**
     * ENROLL 命令读取 Auth 信息失败
     */
    public static final int REASON_CODE_ENROLL_SERVICE_READ_AUTH = 0x5003;
    /**
     * ENROLL 命令构建 RATK 请求失败
     */
    public static final int REASON_CODE_ENROLL_SERVICE_BUILD_RATK_REQUEST = 0x5004;
    /**
     * ENROLL 命令调用 RATK 请求失败
     */
    public static final int REASON_CODE_ENROLL_SERVICE_RATK_PROCESS = 0x5005;
    /**
     * ENROLL 服务检查鉴权信息失败
     */
    public static final int REASON_CODE_ENROLL_SERVICE_CHECK_AUTH = 0x5006;
    /**
     * ENROLL 服务消息ID重复,防止重放攻击
     */
    public static final int REASON_CODE_ENROLL_SERVICE_MESSAGE_DUPLICATE = 0x5007;

    /**
     * REGISTER 命令失败:该用户已经注册过
     */
    public static final int REASON_CODE_REGISTER_SERVICE_ALREADY_REGISTERED = 0x6001;
    /**
     * REGISTER 命令失败:无效的token
     */
    public static final int REASON_CODE_REGISTER_SERVICE_INVALID_TOKEN = 0x6002;
    /**
     * REGISTER 命令失败:验证token失败
     */
    public static final int REASON_CODE_REGISTER_SERVICE_VERIFY_TOKEN = 0x6003;
    /**
     * REGISTER 命令失败:没有在证书库中找到该用户的公钥
     */
    public static final int REASON_CODE_IDENTITY_VERIFY_TOKEN = 0x6004;
    /**
     * REGISTER 命令失败:LOAD注册用户数据文件失败
     */
    public static final int REASON_CODE_REGISTER_SERVICE_LOAD_REGISTER_STORE = 0x6005;
    /**
     * REGISTER 命令失败:update注册用户数据文件失败
     */
    public static final int REASON_CODE_REGISTER_SERVICE_UPDATE_REGISTER_STORE = 0x6006;
    /**
     * REGISTER 命令失败:插入新注册用户信息失败
     */
    public static final int REASON_CODE_REGISTER_SERVICE_INSERT_USER = 0x6007;

    /**
     * REENROLL 命令失败:验证token失败
     */
    public static final int REASON_CODE_REENROLL_SERVICE_VERIFY_TOKEN = 0x7001;
    /**
     * REENROLL 命令失败:REENROLL之前必须ENROLL
     */
    public static final int REASON_CODE_REENROLL_SERVICE_NOT_ENROLL = 0x7002;
    /**
     * REENROLL 命令失败:无效的token
     */
    public static final int REASON_CODE_REENROLL_SERVICE_INVALID_TOKEN = 0x7003;
    /**
     * REENROLL 命令失败:获取CA失败,ca 名称是空
     */
    public static final int REASON_CODE_REENROLL_SERVICE_GET_CA_NAME_EMPTY = 0x7004;
    /**
     * REENROLL 命令失败:消息重复
     */
    public static final int REASON_CODE_REENROLL_SERVICE_MESSAGE_DUPLICATE = 0x7005;

    /**
     * REVOKE 命令失败: REVOKE 之前必须ENROLL
     */
    public static final int REASON_CODE_REVOKE_SERVICE_NOT_ENROLL = 0x8001;
    /**
     * REVOKE 命令失败: 无效的token
     */
    public static final int REASON_CODE_REVOKE_SERVICE_INVALID_TOKEN = 0x8002;
    /**
     * REVOKE 命令失败: 验证token失败
     */
    public static final int REASON_CODE_REVOKE_SERVICE_VERIFY_TOKEN = 0x8003;
    /**
     * REVOKE 命令失败: 调用RATK失败
     */
    public static final int REASON_CODE_REVOKE_SERVICE_RATK_PROCESS = 0x8004;
    /**
     * REVOKE 命令失败: 删除已经吊销的证书失败,不用做验签
     */
    public static final int REASON_CODE_REVOKE_SERVICE_REMOVE_CERT = 0x8005;
    /**
     * REVOKE 命令失败: 无效的请求参数
     */
    public static final int REASON_CODE_REVOKE_SERVICE_INVALID_REQUEST = 0x8006;
    /**
     * REVOKE 命令失败: 无效的请求参数
     */
    public static final int REASON_CODE_REVOKE_SERVICE_MESSAGE_DUPLICATE = 0x8007;

    /**
     * GETTCERT 命令失败: 在 geitcert 前必须先enroll
     */
    public static final int REASON_CODE_GETTCERT_SERVICE_NOT_ENROLL = 0x9001;
    /**
     * GETTCERT 命令失败: 无效的 token
     */
    public static final int REASON_CODE_GETTCERT_SERVICE_INVALID_TOKEN = 0x9002;
    /**
     * GETTCERT 命令失败: 校验 token 失败
     */
    public static final int REASON_CODE_GETTCERT_SERVICE_VERIFY_TOKEN = 0x9003;
    /**
     * GETTCERT 命令失败: 生成 csr 失败
     */
    public static final int REASON_CODE_GETTCERT_SERVICE_GEN_CSR = 0x9004;
    /**
     * GETTCERT 命令失败: 初始化keytree 派生密钥失败
     */
    public static final int REASON_CODE_GETTCERT_SERVICE_DERIVE_CHILD_KEY = 0x9005;
    /**
     * GETTCERT 命令失败: 加载 TCERT MGR 失败
     */
    public static final int REASON_CODE_RA_SERVER_LOAD_TCERT_MGR = 0x9006;
    /**
     * GETTCERT 命令失败: 处理批处理请求失败
     */
    public static final int REASON_CODE_GETTCERT_SERVICE_GET_BATCH = 0x9007;
    /**
     * GETTCERT 命令失败: 生成派生密钥失败
     */
    public static final int REASON_CODE_GETTCERT_SERVICE_GEN_KDF_KEY = 0x9008;
    /**
     * GETTCERT 命令失败: 使用CBC方式+PKCS7填充加密失败
     */
    public static final int REASON_CODE_GETTCERT_SERVICE_CBC_PKCS7_ENCRYPT = 0x9009;
    /**
     * GETTCERT 命令失败: 从身份证书派生公钥失败
     */
    public static final int REASON_CODE_GETTCERT_SERVICE_GENERATE_PUBLIC_KEY = 0x900a;
    /**
     * GETTCERT 命令失败: 使用CBC方式+PKCS7填充解密失败
     */
    public static final int REASON_CODE_GETTCERT_SERVICE_CBC_PKCS7_DECRYPT = 0x900b;
    /**
     * GETTCERT 命令失败: 使用RATK 下载证书失败
     */
    public static final int REASON_CODE_GETTCERT_SERVICE_RATK_PROCESS = 0x900c;
    /**
     * GETTCERT 命令失败: 消息重复
     */
    public static final int REASON_CODE_GETTCERT_SERVICE_MESSAGE_DUPLICATE = 0x900d;

    /**
     * CERTUTILS 操作失败: 获取证书的使用者名称失败
     */
    public static final int REASON_CODE_CERTUTILS_GET_SUBJECT_NAME = 0xa001;

    /**
     * RA Server 没有初始化
     */
    public static final int REASON_CODE_CA_NOT_READY = 0xb001;
    /**
     * RA Server 该用户不存在数据库中
     */
    public static final int REASON_CODE_CA_USER_NOT_EXIST = 0xb002;
    /**
     * RA Server:没有在证书库中找到公钥,因为token无效
     */
    public static final int REASON_CODE_RA_SERVER_GET_KEY_EXCEPTION = 0xb003;
    /**
     * RA Server:找不到指定名称的CA
     */
    public static final int REASON_CODE_RA_SERVER_GET_CA_NOT_FOUND = 0xb004;
    /**
     * RA Server:添加CA失败
     */
    public static final int REASON_CODE_RA_SERVER_ADD_CA_EXCEPTION = 0xb005;
    /**
     * RA Server:查找CA失败,CA名称为空
     */
    public static final int REASON_CODE_RA_SERVER_GET_CA_NAME_EMPTY = 0xb006;
    /**
     * RA Server:初始化CA失败,创建CA home dir 失败
     */
    public static final int REASON_CODE_RA_SERVER_CREATE_CA_HOME_DIR = 0xb007;

    /**
     * CA:因为B64解码失败导致存储证书失败
     */
    public static final int REASON_CODE_CA_CERT_STORE_CERT_B64_DECODE = 0xc001;
    /**
     * CA:因为pem文件格式操作失败导致存储证书失败
     */
    public static final int REASON_CODE_CA_CERT_STORE_CERT_WITH_PEM = 0xc002;
    /**
     * CA:无效的参数导致存储证书失败
     */
    public static final int REASON_CODE_CA_CERT_STORE_CERT_INVALID_ARGS = 0xc003;
    /**
     * CA:该用户已经注册
     */
    public static final int REASON_CODE_CA_USER_ALREADY_REGISTERED = 0xc004;
    /**
     * CA:加载公钥文件失败
     */
    public static final int REASON_CODE_CA_CERT_STORE_LOAD_CERT = 0xc005;
    /**
     * CA:公钥 Store 获取文件路径失败
     */
    public static final int REASON_CODE_CA_CERT_STORE_GET_CERT_FILE_PATH = 0xc006;
    /**
     * CA:检查 ID 是否被注册时失败
     */
    public static final int REASON_CODE_CA_CHECK_ID_REGISTERED = 0xc007;
    /**
     * CA:没有找到该CA的证书链文件
     */
    public static final int REASON_CODE_CA_NOT_FOUND_CACHAIN_FILE = 0xc008;
    /**
     * CA:读取该CA的证书链文件失败
     */
    public static final int REASON_CODE_CA_READ_CACHAIN_FILE = 0xc009;
    /**
     * CA:从证书库中寻找对应用户的公钥失败
     */
    public static final int REASON_CODE_CA_CERT_STORE_CONTAINS_CERT = 0xc00a;
    /**
     * CA:从证书库中寻找对应用户的证书并转为B64字符串读取出来
     */
    public static final int REASON_CODE_CA_CERT_STORE_LOAD_B64_CERT_STRING = 0xc00b;

    /**
     * ENROLLIDSTORE:更新ENROLLID失败
     */
    public static final int REASON_CODE_ENROLLIDSTORE_UPDATE_ENROLLID_FILE = 0xe001;
    /**
     * ENROLLIDSTORE:加载ENROLLID失败
     */
    public static final int REASON_CODE_ENROLLIDSTORE_LOAD_ENROLLID_FILE = 0xe002;

    /**
     * MESSAGE_STORE:加载 MESSAGE STORE 失败
     */
    public static final int REASON_CODE_MESSAGE_STORE_LOAD = 0xf001;

    private String message;

    private final int reasonCode;
    private Throwable cause;
    private Map<Integer, String> messageCatalog = new HashMap<Integer, String>(10) {
        {
            put(REASON_CODE_SERVER_EXCEPTION, "server exception");
            put(REASON_CODE_ENROLL_SERVICE_READ_AUTH, "enrollment service failed to get auth");
            put(REASON_CODE_ENROLL_SERVICE_BUILD_RATK_REQUEST, "enrollment service failed to build ratk request");
            put(REASON_CODE_ENROLL_SERVICE_RATK_PROCESS, "enrollment service failed to process ratk request");
            put(REASON_CODE_ENROLL_SERVICE_CHECK_AUTH, "enrollment service failed to check auth");
            put(REASON_CODE_ENROLL_SERVICE_MESSAGE_DUPLICATE, "enrollment service failed due to duplicate message ID");

            put(REASON_CODE_REGISTER_SERVICE_ALREADY_REGISTERED, "register service failed to register by already registered");
            put(REASON_CODE_REGISTER_SERVICE_INVALID_TOKEN, "register service failed with invalid token");
            put(REASON_CODE_REGISTER_SERVICE_VERIFY_TOKEN, "register service failed with verify token");
            put(REASON_CODE_IDENTITY_VERIFY_TOKEN, "register service failed with must enroll first");
            put(REASON_CODE_REGISTER_SERVICE_LOAD_REGISTER_STORE, "register service failed to load register store");
            put(REASON_CODE_REGISTER_SERVICE_UPDATE_REGISTER_STORE, "register service failed to update register store");
            put(REASON_CODE_REGISTER_SERVICE_INSERT_USER, "register service failed to insert user into register store");

            put(REASON_CODE_REENROLL_SERVICE_VERIFY_TOKEN, "reenroll service failed to verify token");
            put(REASON_CODE_REENROLL_SERVICE_NOT_ENROLL, "reenroll service failed with must enroll first");
            put(REASON_CODE_REENROLL_SERVICE_INVALID_TOKEN, "reenroll service failed due to invalid token");
            put(REASON_CODE_REENROLL_SERVICE_GET_CA_NAME_EMPTY, "reenroll service failed due to ca name is empty");
            put(REASON_CODE_REENROLL_SERVICE_MESSAGE_DUPLICATE, "reenroll service failed due to duplicate message");

            put(REASON_CODE_REVOKE_SERVICE_NOT_ENROLL, "revoke service failed with must enroll first");
            put(REASON_CODE_REVOKE_SERVICE_INVALID_TOKEN, "revoke service failed due to invalid token");
            put(REASON_CODE_REVOKE_SERVICE_VERIFY_TOKEN, "revoke service failed due with verify token");
            put(REASON_CODE_REVOKE_SERVICE_RATK_PROCESS, "revoke service failed to process ratk request");
            put(REASON_CODE_REVOKE_SERVICE_REMOVE_CERT, "revoke service failed to remove cert been revoked");
            put(REASON_CODE_REVOKE_SERVICE_INVALID_REQUEST, "revoke service failed due to invalid request");
            put(REASON_CODE_REVOKE_SERVICE_MESSAGE_DUPLICATE, "revoke service failed due to duplicate message");

            put(REASON_CODE_GETTCERT_SERVICE_NOT_ENROLL, "gettcert service failed with must enroll first");
            put(REASON_CODE_GETTCERT_SERVICE_INVALID_TOKEN, "gettcert service failed due to invalid token");
            put(REASON_CODE_GETTCERT_SERVICE_VERIFY_TOKEN, "gettcert service failed due to verify token");
            put(REASON_CODE_GETTCERT_SERVICE_GEN_CSR, "gettcert service failed due to generate csr");
            put(REASON_CODE_RA_SERVER_LOAD_TCERT_MGR, "gettcert service failed due to load tcert manger");
            put(REASON_CODE_GETTCERT_SERVICE_DERIVE_CHILD_KEY, "gettcert service failed due to derive child key");
            put(REASON_CODE_GETTCERT_SERVICE_GET_BATCH, "gettcert service failed due to get batch");
            put(REASON_CODE_GETTCERT_SERVICE_GEN_KDF_KEY, "gettcert service failed due to generate kdf key");
            put(REASON_CODE_GETTCERT_SERVICE_CBC_PKCS7_ENCRYPT, "gettcert service failed due to cbc pkcs7 encrypt");
            put(REASON_CODE_GETTCERT_SERVICE_CBC_PKCS7_DECRYPT, "gettcert service failed due to cbc pkcs7 decrypt");
            put(REASON_CODE_GETTCERT_SERVICE_GENERATE_PUBLIC_KEY, "gettcert service failed due to generate public key derivative from ecert");
            put(REASON_CODE_GETTCERT_SERVICE_RATK_PROCESS, "gettcert service failed due to process ratk request");
            put(REASON_CODE_GETTCERT_SERVICE_MESSAGE_DUPLICATE, "gettcert service failed due to duplicate message");

            put(REASON_CODE_RA_SERVER_GET_KEY_EXCEPTION, "RA Server failed to register service due to invalid token");
            put(REASON_CODE_RA_SERVER_GET_CA_NOT_FOUND, "RA Server not found CA by ca name");
            put(REASON_CODE_RA_SERVER_ADD_CA_EXCEPTION, "RA Server failed to add CA");
            put(REASON_CODE_RA_SERVER_GET_CA_NAME_EMPTY, "RA Server failed to get CA with empty ca name");
            put(REASON_CODE_RA_SERVER_CREATE_CA_HOME_DIR, "RA Server failed to create CA due to fail to create home dir");

            put(REASON_CODE_CA_NOT_READY, "CA is not ready");
            put(REASON_CODE_CA_USER_NOT_EXIST, " this user not exist in this ca store");
            put(REASON_CODE_CA_USER_ALREADY_REGISTERED, "this user already registered in this ca");
            put(REASON_CODE_CA_CERT_STORE_CERT_B64_DECODE, "ca fail to store cert due to decode b64 string");
            put(REASON_CODE_CA_CERT_STORE_CERT_WITH_PEM, "ca fail to store cert due to pem file format");
            put(REASON_CODE_CA_CERT_STORE_CERT_INVALID_ARGS, "ca fail to store cert due to invalid args");
            put(REASON_CODE_CA_CERT_STORE_GET_CERT_FILE_PATH, "ca fail to get cert file path from cert store");
            put(REASON_CODE_CA_CERT_STORE_LOAD_CERT, "ca fail to load cert due to pem operation");
            put(REASON_CODE_CA_CERT_STORE_CONTAINS_CERT, "ca fail to check whether contains this enrollmentId cert");
            put(REASON_CODE_CA_CERT_STORE_LOAD_B64_CERT_STRING, "ca fail to load the b64 string of the cert");
            put(REASON_CODE_CA_CHECK_ID_REGISTERED, "ca fail to check id registered");
            put(REASON_CODE_CA_NOT_FOUND_CACHAIN_FILE, "ca fail to read chain file due to not found");
            put(REASON_CODE_CA_READ_CACHAIN_FILE, "ca fail to read chain file");

            put(REASON_CODE_ENROLLIDSTORE_UPDATE_ENROLLID_FILE, "enrollid store fail to update enroll id dat file");
            put(REASON_CODE_ENROLLIDSTORE_LOAD_ENROLLID_FILE, "enrollid store fail to load update enroll id dat file");
            put(REASON_CODE_MESSAGE_STORE_LOAD, "message store fail to load");

            put(REASON_CODE_CERTUTILS_GET_SUBJECT_NAME, "cert utils fail to get subject name of cert");
        }
    };

    public RAServerException(final int reasonCode) {
        super();
        this.reasonCode = reasonCode;
    }

    public RAServerException(final Throwable cause) {
        super();
        this.reasonCode = REASON_CODE_SERVER_EXCEPTION;
        this.cause = cause;
        this.message = cause.getMessage();
    }

    public RAServerException(final int reason, final Throwable cause) {
        super();
        this.reasonCode = reason;
        this.cause = cause;
        this.message = cause.getMessage();
    }

    public RAServerException(final int reason, final String message, final Throwable cause) {
        super();
        this.reasonCode = reason;
        this.message = message;
        this.cause = cause;
    }

    public RAServerException(final int reason, final String message) {
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
        if (!StringUtils.isBlank(this.message)) {
            return this.message;
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
