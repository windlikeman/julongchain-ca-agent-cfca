package org.bica.julongchain.cfca.ra.command.internal.enroll;


import org.bica.julongchain.cfca.ra.command.config.CsrConfig;

import java.util.Objects;

/**
 * @author zhangchong
 * @create 2018/5/11
 * @Description 用于调用服务器Restful接口用的参数
 * @CodeReviewer
 * @since v3.0.0
 */
public class EnrollmentRequest {

    /**
     * The label used in HSM operations
     */
    private final String label;

    private final String username;
    private final String password;

    /**
     * Profile is the name of the signing profile to use in issuing the
     * certificate
     */
    private final String profile;

    /**
     * CSR is Certificate Signing Request info
     */
    private final CsrConfig csrConfig;

    /**
     * CAName is the name of the CA to connect to
     */
    private final String caName;

    /**
     * 包含基于PKCS＃10的 CSR(证书签名请求) 的编码字符串。
     */
    private final String request;

    private final static EnrollmentRequest NULL = new Builder(null, null, null, null, null, null).label(null).build();

    private EnrollmentRequest(Builder builder) {
        this.label = builder.label;
        this.username = builder.username;
        this.password = builder.password;
        this.profile = builder.profile;
        this.csrConfig = builder.csrConfig;
        this.caName = builder.caName;
        this.request = builder.request;
    }

    public String getLabel() {
        return label;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getProfile() {
        return profile;
    }

    public CsrConfig getCsrConfig() {
        return csrConfig;
    }

    public String getCaName() {
        return caName;
    }

    public String getRequest() {
        return request;
    }

    public boolean isNull() {
        return this.equals(NULL);
    }




    @Override
    public String toString() {
        StringBuilder builder2 = new StringBuilder();
        builder2.append("EnrollmentRequest [label=");
        builder2.append(label);
        builder2.append(", username=");
        builder2.append(username);
        builder2.append(", password=");
        builder2.append(password);
        builder2.append(", profile=");
        builder2.append(profile);
        builder2.append(", csrConfig=");
        builder2.append(csrConfig);
        builder2.append(", caName=");
        builder2.append(caName);
        builder2.append(", request=");
        builder2.append(request);
        builder2.append("]");
        return builder2.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EnrollmentRequest that = (EnrollmentRequest) o;
        return Objects.equals(label, that.label) &&
                Objects.equals(username, that.username) &&
                Objects.equals(password, that.password) &&
                Objects.equals(profile, that.profile) &&
                Objects.equals(csrConfig, that.csrConfig) &&
                Objects.equals(caName, that.caName);
    }

    @Override
    public int hashCode() {

        return Objects.hash(label, username, password, profile, csrConfig, caName);
    }

    public static class Builder {
        private final String password;
        private final String username;
        private final String profile;
        private final CsrConfig csrConfig;
        private final String caName;
        private final String request;
        /**
         * Optional:The label used in HSM operations
         */
        private String label = "";


        public Builder(String request, String username, String password, String profile, CsrConfig csrConfig, String caName) {
            this.request = request;
            this.username = username;
            this.password = password;
            this.profile = profile;
            this.csrConfig = csrConfig;
            this.caName = caName;
        }

        public Builder label(String v) {
            this.label = v;
            return this;
        }

        public EnrollmentRequest build() {
            return new EnrollmentRequest(this);
        }
    }
}
