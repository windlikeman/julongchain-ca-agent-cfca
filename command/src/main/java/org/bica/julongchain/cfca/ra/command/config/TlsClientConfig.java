package org.bica.julongchain.cfca.ra.command.config;

/**
 * @author zhangchong
 * @create 2018/5/15
 * @Description 配置了 Tls 的客户端配置信息
 * @CodeReviewer
 * @since v3.0.0
 */
public class TlsClientConfig {
    /**
     * PEM-encoded certificate file for when client authentication
     */
    private String certfile;

    /**
     * PEM-encoded key file for when client authentication
     */
    private String keyfile;

    public String getCertfile() {
        return certfile;
    }

    public String getKeyfile() {
        return keyfile;
    }

    public void setCertfile(String certfile) {
        this.certfile = certfile;
    }

    public void setKeyfile(String keyfile) {
        this.keyfile = keyfile;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("TlsClientConfig [certfile=");
        builder.append(certfile);
        builder.append(", keyfile=");
        builder.append(keyfile);
        builder.append("]");
        return builder.toString();
    }

   
    
}
