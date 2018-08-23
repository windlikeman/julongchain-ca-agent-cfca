package org.bica.julongchain.cfca.ra.command.internal.reenroll;

import org.bica.julongchain.cfca.ra.command.config.CsrConfig;

/**
 * @author zhangchong
 * @create 2018/5/17
 * @Description 重新签发证书命令的内部使用的请求,用于适配网络Restful接口请求
 * @CodeReviewer
 * @since v3.0.0
 */
public class ReenrollmentRequest {
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

    private final String request;

    private ReenrollmentRequest(ReenrollmentRequest.Builder builder) {
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


    

    @Override
    public String toString() {
        StringBuilder builder2 = new StringBuilder();
        builder2.append("ReenrollmentRequest [label=");
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




    public static class Builder {
        private final String password;
        private final String username;
        private final String profile;
        private final CsrConfig csrConfig;
        private final String caName;
        /**
         * Optional:The label used in HSM operations
         */
        private String label = "";
        public final String request;

        public Builder(String request, String username, String password, String profile, CsrConfig csrConfig, String caName) {
            this.request = request;
            this.username = username;
            this.password = password;
            this.profile = profile;
            this.csrConfig = csrConfig;
            this.caName = caName;
        }

        public ReenrollmentRequest build() {
            return new ReenrollmentRequest(this);
        }
    }
}
