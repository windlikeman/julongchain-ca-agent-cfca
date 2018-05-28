package com.cfca.ra.ca;

import java.util.ArrayList;
import java.util.Objects;

/**
 * @author zhangchong
 * @create 2018/5/16
 * @Description CA 配置信息,包含与证书颁发机构相关的信息, 包括CA的名称, 对于区块链网络的所有成员而言, 该名称应该是唯一的.
 * 它还包括发放登记证书(ECerts)和交易证书(TCerts)时使用的密钥和证书文件.
 * @CodeReviewer
 * @since v3.0.0
 */
public class CAConfig {
    private final String version;
    private final CAInfo CA;
    private final CAConfigRegistry registry;
    private final String homeDir;

    private CAConfig(Builder builder) {
        this.version = builder.version;
        this.CA = builder.CA;
        this.registry = builder.registry;
        this.homeDir = builder.homeDir;
    }

    public String getVersion() {
        return version;
    }

    public CAInfo getCA() {
        return CA;
    }

    public CAConfigRegistry getRegistry() {
        return registry;
    }

    public String getHomeDir() {
        return homeDir;
    }

    @Override
    public String toString() {
        return "CAConfig{" +
                "version='" + version + '\'' +
                ", CA=" + CA +
                ", registry=" + registry +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CAConfig caConfig = (CAConfig) o;
        return Objects.equals(version, caConfig.version) &&
                Objects.equals(CA, caConfig.CA) &&
                Objects.equals(registry, caConfig.registry);
    }

    @Override
    public int hashCode() {

        return Objects.hash(version, CA, registry);
    }

    public static class Builder {
        private final CAInfo CA;
        private String version = "0";
        private CAConfigRegistry registry = new CAConfigRegistry(-1, new ArrayList<CAConfigIdentity>());
        public final String homeDir;

        public Builder(CAInfo ca, String homeDir) {
            this.CA = ca;
            this.homeDir = homeDir;
        }

        public Builder registry(CAConfigRegistry v) {
            this.registry = v;
            return this;
        }

        public Builder version(String v) {
            this.version = v;
            return this;
        }

        public CAConfig build() {
            return new CAConfig(this);
        }
    }
}
