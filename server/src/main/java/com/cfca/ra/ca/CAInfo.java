package com.cfca.ra.ca;

import java.util.Objects;

/**
 * @author zhangchong
 * @create 2018/5/16
 * @Description
 * @CodeReviewer
 * @since v3.0.0
 */
public class CAInfo {
    /**
     * Certificate Authority name
     */
    private final String name;
    /**
     * PEM-encoded CA key file
     */
    private final String keyfile;
    /**
     * PEM-encoded CA certificate file
     */
    private final String certfile;

    /**
     * PEM-encoded CA chain file
     */
    private final String chainfile;

    private final String homeDir;

    public CAInfo(Builder builder) {
        this.name = builder.name;
        this.keyfile = builder.keyfile;
        this.certfile = builder.certfile;
        this.chainfile = builder.chainfile;
        this.homeDir = builder.homeDir;
    }

    public String getName() {
        return name;
    }

    public String getKeyfile() {
        return keyfile;
    }

    public String getCertfile() {
        return certfile;
    }

    public String getChainfile() {
        return chainfile;
    }

    public String getHomeDir() {
        return homeDir;
    }

    public static class Builder {
        private final String name;
        private final String homeDir;
        private String keyfile = "ca-key.pem";
        private String certfile = "ca-cert.pem";
        private String chainfile = "ca-chain.pem";

        public Builder(String name, String homeDir) {
            this.name = name;
            this.homeDir = homeDir;
        }

        public Builder keyfile(String v) {
            this.keyfile = v;
            return this;
        }

        public Builder certfile(String v) {
            this.certfile = v;
            return this;
        }

        public Builder chainfile(String v) {
            this.chainfile = v;
            return this;
        }

        public CAInfo build() {
            return new CAInfo(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CAInfo caInfo = (CAInfo) o;
        return Objects.equals(name, caInfo.name) &&
                Objects.equals(keyfile, caInfo.keyfile) &&
                Objects.equals(certfile, caInfo.certfile) &&
                Objects.equals(chainfile, caInfo.chainfile) &&
                Objects.equals(homeDir, caInfo.homeDir);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, keyfile, certfile, chainfile, homeDir);
    }

    @Override
    public String toString() {
        return "CAInfo{" +
                "name='" + name + '\'' +
                ", keyfile='" + keyfile + '\'' +
                ", certfile='" + certfile + '\'' +
                ", chainfile='" + chainfile + '\'' +
                ", homeDir='" + homeDir + '\'' +
                '}';
    }
}
