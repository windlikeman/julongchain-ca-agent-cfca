package org.bica.julongchain.cfca.ra.command.internal.register;

import java.util.List;

/**
 * @author zhangchong
 * @create 2018/5/18
 * @Description 命令行内部使用注册接口参数
 * @CodeReviewer
 * @since v3.0.0
 */
public class RegistrationRequest {
    /**
     * Name用于唯一标示 identity, 是证书的使用者密钥标识符
     */
    private final String name;

    /**
     * 所要注册的类型 (e.g. "peer, app, user")
     */
    private final String type;
    /**
     * 密码是可选的,如果没有指定,就随机产生一个.不管是哪一种情况,密码都会在 RegistrationResponse 中返回
     */
    private final String secret;
    /**
     * MaxEnrollments 是指这个密码可以用来重复申请证书的最大重复次数.
     */
    private final int maxEnrollments;

    /**
     * 在 response 中返回 identity 的从属关系. 例如,"org1.department1"的隶属关系将该身份与 "org1" 中的
     * "department1" 相关联
     */
    private final String affiliation;

    /**
     * Attributes 与 identity 关联
     */
    private final List<UserAttrs> attributes;

    /**
     * CAName 是指要连接的 CA 的名字
     */
    private final String caName;

    private RegistrationRequest(Builder builder) {
        this.name = builder.name;
        this.type = builder.type;
        this.secret = builder.secret;
        this.maxEnrollments = builder.maxEnrollments;
        this.affiliation = builder.affiliation;
        this.attributes = builder.attributes;
        this.caName = builder.caName;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getSecret() {
        return secret;
    }

    public int getMaxEnrollments() {
        return maxEnrollments;
    }

    public String getAffiliation() {
        return affiliation;
    }

    public List<UserAttrs> getAttributes() {
        return attributes;
    }

    public String getCaName() {
        return caName;
    }

   
    

    @Override
    public String toString() {
        StringBuilder builder2 = new StringBuilder();
        builder2.append("RegistrationRequest [name=");
        builder2.append(name);
        builder2.append(", type=");
        builder2.append(type);
        builder2.append(", secret=");
        builder2.append(secret);
        builder2.append(", maxEnrollments=");
        builder2.append(maxEnrollments);
        builder2.append(", affiliation=");
        builder2.append(affiliation);
        builder2.append(", attributes=");
        builder2.append(attributes);
        builder2.append(", caName=");
        builder2.append(caName);
        builder2.append("]");
        return builder2.toString();
    }




    public static class Builder {
        private String name;
        private String type;
        private String secret;
        private int maxEnrollments = -1;
        private String affiliation;
        private List<UserAttrs> attributes;
        private String caName;

        public Builder name(String v) {
            this.name = v;
            return this;
        }

        public Builder type(String v) {
            this.type = v;
            return this;
        }

        public Builder secret(String v) {
            this.secret = v;
            return this;
        }

        public Builder maxEnrollments(int v) {
            this.maxEnrollments = v;
            return this;
        }

        public Builder affiliation(String v) {
            this.affiliation = v;
            return this;
        }

        public Builder attributes(List<UserAttrs> v) {
            this.attributes = v;
            return this;
        }

        public Builder caName(String v) {
            this.caName = v;
            return this;
        }

        public RegistrationRequest build() {
            return new RegistrationRequest(this);
        }
    }
}
