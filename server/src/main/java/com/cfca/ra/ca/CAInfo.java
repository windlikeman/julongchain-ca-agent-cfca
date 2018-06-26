package com.cfca.ra.ca;

import java.util.Objects;

/**
 * @author zhangchong
 * @create 2018/5/16
 * @Description CA 信息,它包括发放登记证书(ECerts)和交易证书(TCerts)时使用的密钥和证书文件
 * @CodeReviewer helonglong
 * @since v3.0.0
 */
public class CAInfo {
    /**
     * Certificate Authority name
     */
    private String name;
    /**
     * PEM-encoded CA key file
     */
    private String keyfile;
    /**
     * PEM-encoded CA certificate file
     */
    private String certfile;

    /**
     * PEM-encoded CA chain file
     */
    private String chainfile;

    public CAInfo() {
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setKeyfile(String keyfile) {
        this.keyfile = keyfile;
    }

    public void setCertfile(String certfile) {
        this.certfile = certfile;
    }

    public void setChainfile(String chainfile) {
        this.chainfile = chainfile;
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

//        public CAInfo build() {
//            return new CAInfo(this);
//        }
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
                Objects.equals(chainfile, caInfo.chainfile);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, keyfile, certfile, chainfile);
    }

    @Override
    public String toString() {
        return "CAInfo{" +
                "name='" + name + '\'' +
                ", keyfile='" + keyfile + '\'' +
                ", certfile='" + certfile + '\'' +
                ", chainfile='" + chainfile + '\'' +
                '}';
    }
}
