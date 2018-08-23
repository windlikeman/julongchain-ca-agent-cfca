package org.bica.julongchain.cfca.ra.command.internal;

import java.util.Arrays;

/**
 * @author zhangchong
 * @create 2018/5/11
 * @Description 其中包含了证书信息
 * @CodeReviewer
 * @since v3.0.0
 */
public class ServerInfo {
    private final String caName;
    /**
     * CAChain 就是 CA 证书链. 链表中的第一个元素就是CA根证书
     */
    private final byte[] caChain;

    /**
     * 服务器版本号
     */
    private final String version;

    private final String enrollmentId;

    private ServerInfo(Builder builder) {
        this.caName = builder.caName;
        this.caChain = builder.caChain;
        this.version = builder.version;
        this.enrollmentId = builder.enrollmentId;
    }

    public static class Builder {
        private String caName;
        /**
         * CAChain 就是 CA 证书链. 链表中的第一个元素就是CA根证书
         */
        private byte[] caChain;

        /**
         * 服务器版本号
         */
        private String version = "v3.0.0";
        private String enrollmentId;

        public Builder enrollmentId(String enrollmentId) {
            this.enrollmentId = enrollmentId;
            return this;
        }

        public Builder caName(String caName) {
            this.caName = caName;
            return this;
        }

        public Builder caChain(byte[] caChain) {
            this.caChain = caChain;
            return this;
        }

        public Builder version(String version) {
            this.version = version;
            return this;
        }

        public ServerInfo build() {
            return new ServerInfo(this);
        }
    }

    public String getCaName() {
        return caName;
    }

    public byte[] getCaChain() {
        return caChain;
    }

    public String getVersion() {
        return version;
    }

    public String getEnrollmentId() {
        return enrollmentId;
    }

    @Override
    public String toString() {
        StringBuilder builder2 = new StringBuilder();
        builder2.append("ServerInfo [caName=");
        builder2.append(caName);
        builder2.append(", caChain=");
        builder2.append(Arrays.toString(caChain));
        builder2.append(", version=");
        builder2.append(version);
        builder2.append(", enrollmentId=");
        builder2.append(enrollmentId);
        builder2.append("]");
        return builder2.toString();
    }

    
    
}
