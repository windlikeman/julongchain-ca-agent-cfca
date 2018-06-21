package com.cfca.ra.command.config;

import java.util.List;

/**
 * @author zhangchong
 * @create 2018/5/15
 * @Description Tls 的相关配置信息
 * @CodeReviewer
 * @since v3.0.0
 */
public class TlsConfig {
    private boolean enabled;
    /**
     * PEM-encoded list of trusted root certificate files
     */
    private List<String> certfiles;

    private TlsClientConfig client;

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setCertfiles(List<String> certfiles) {
        this.certfiles = certfiles;
    }

    public void setClient(TlsClientConfig client) {
        this.client = client;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public List<String> getCertfiles() {
        return certfiles;
    }

    public TlsClientConfig getClient() {
        return client;
    }

    @Override
    public String toString() {
        return "TlsConfig{" + "enabled=" + enabled + ", certfiles=" + certfiles + ", client=" + client + '}';
    }
}
