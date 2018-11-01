package org.bica.julongchain.cfca.ra.client;

import java.util.Properties;

/**
 * @author qazhang
 * @Description RA客户端配置项
 * @CodeReviewer helonglong
 *
 */
final class RAClientBean {
    
    /**
     * 告警时间 毫秒
     */
    int warningTime;

    /**
     * 连接超时时间 毫秒
     */
    int connectTimeout;
    /**
     * 读取超时时间 毫秒
     */
    int readTimeout;

    /**
     * URL（http、https方式）
     */
    String url;

    /**
     * 通信证书配置(文件)
     */
    String keyStorePath;
    /**
     * 通信证书配置（口令）
     */
    String keyStorePassword;

    /**
     * 信任证书链配置(文件)
     */
    String trustStorePath;
    /**
     * 信任证书链配置（口令）
     */
    String trustStorePassword;

    RAClientBean(Properties properties) throws Exception {

        this.warningTime = Integer.valueOf(properties.getProperty("ratk.http.warningTime", "5000"));
        this.connectTimeout = Integer.valueOf(properties.getProperty("ratk.http.connect.timeout", "3000"));
        this.readTimeout = Integer.valueOf(properties.getProperty("ratk.http.read.timeout", "30000"));
        this.url = properties.getProperty("ratk.http.url");
        this.keyStorePath = properties.getProperty("ratk.https.keyStorePath");
        this.keyStorePassword = properties.getProperty("ratk.https.keyStorePassword");

        this.trustStorePath = properties.getProperty("ratk.https.trustStorePath");
        this.trustStorePassword = properties.getProperty("ratk.https.trustStorePassword");
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("RAClientBean [");       
        builder.append("warningTime=").append(warningTime);
        builder.append(", connectTimeout=").append(connectTimeout);
        builder.append(", readTimeout=").append(readTimeout);
        builder.append("\n url=").append(url);
        builder.append("\n keyStorePath=").append(keyStorePath);
        builder.append("\n keyStorePassword=").append("******");
        builder.append("\n trustStorePath=").append(trustStorePath);
        builder.append("\n trustStorePassword=").append("******");
        builder.append("]");
        return builder.toString();
    }
}
