package com.cfca.ra.command.config;

/**
 * @author zhangchong
 * @create 2018/5/15
 * @Description
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
        return "TlsClientConfig{" +
                "certfile='" + certfile + '\'' +
                ", keyfile='" + keyfile + '\'' +
                '}';
    }
}
