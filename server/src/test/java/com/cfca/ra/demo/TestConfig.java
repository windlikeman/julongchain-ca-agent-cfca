package com.cfca.ra.demo;

import cfca.ra.toolkit.RAClient;
import cfca.ra.toolkit.exception.RATKException;

public class TestConfig {
    // 连接超时时间 毫秒
    public static final int connectTimeout = 3000;
    // 读取超时时间 毫秒
    public static final int readTimeout = 30000;
    // URL（http、https方式）
    public static final String url = "http://192.168.123.177:8084/raWeb/CSHttpServlet";
    // 服务器ip（socket、ssl socket方式）
    public static final String ip = "localhost";
    // 服务器端口（socket、ssl socket方式）
    public static final int port = 9140;

    // 通信证书配置
    public static final String keyStorePath = "D:/apache-tomcat-5.5.36/yanzhengT.jks";
    public static final String keyStorePassword = "Abcd1234";
    // 信任证书链配置
    public static final String trustStorePath = "D:/apache-tomcat-5.5.36/yanzhengT.jks";
    public static final String trustStorePassword = "Abcd1234";

    public static RAClient getRAClient() throws RATKException {
        return getRAClient(1);
    }

    // 客户端与RA之间为短链接
    // 该方法仅作为demo示例，使用时直接创建RAClient对象即可
    // 连接参数不变时，多次调用可使用同一RAClient对象，无需重新创建
    public static RAClient getRAClient(int type) throws RATKException {
        RAClient client = null;
        switch (type) {
        case 1:
            // 初始化为http连接方式，指定url
            client = new RAClient(url, connectTimeout, readTimeout);
            break;
        case 2:
            // 初始化为https连接方式，指定url，另需配置ssl的证书及信任证书链
            client = new RAClient(url, connectTimeout, readTimeout);
            client.initSSL(keyStorePath, keyStorePassword, trustStorePath, trustStorePassword);
            // 如需指定ssl协议、算法、证书库类型，使用如下方式
            // client.initSSL(keyStorePath, keyStorePassword, trustStorePath,
            // trustStorePassword, "SSL", "IbmX509", "IbmX509", "JKS", "JKS");
            break;
        case 3:
            // 初始化为socket 连接方式，指定ip和端口
            client = new RAClient(ip, port, connectTimeout, readTimeout);
            break;
        case 4:
            // 初始化为ssl socket 连接方式，指定ip和端口，另需配置ssl的证书及信任证书链
            client = new RAClient(ip, port, connectTimeout, readTimeout);
            client.initSSL(keyStorePath, keyStorePassword, trustStorePath, trustStorePassword);
            // 如需指定ssl协议、算法、证书库类型，使用如下方式
            // client.initSSL(keyStorePath, keyStorePassword, trustStorePath,
            // trustStorePassword, "SSL", "IbmX509", "IbmX509", "JKS", "JKS");
            break;
        default:
            break;
        }
        return client;
    }
}
