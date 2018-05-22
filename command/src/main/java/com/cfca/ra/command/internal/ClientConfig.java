package com.cfca.ra.command.internal;

import com.cfca.ra.command.config.CsrConfig;


/**
 * @author zhangchong
 * @create 2018/5/11
 * @Description 客户端配置类, 通过构造器模式构建
 * @CodeReviewer
 * @since v3.0.0
 */
public enum ClientConfig {
    /**
     * 单例实现,被所有命令持有,只有一份
     */
    INSTANCE;
    /**
     * 连接ca地址: "http://localhost:7054" opt:"u" help:"URL of cfca-ca-server"
     */
    private String url;

    /**
     * Membership Service Provider目录
     */
    private String mspDir;

    /**
     * CA 名称
     */
    private String caName;

    /**
     *
     */
    private CsrConfig csrConfig;

    private String admin;

    private String adminpwd;

    /**
     *
     */
    private EnrollmentRequest enrollmentRequest;

    /**
     *
     */
    private RegistrationRequest registrationRequest;
    /**
     *
     */
    private GetCAInfoRequest getCAInfoRequest;

    /**
     *
     */
    private RevokeRequest revokeRequest;

    private GettCertRequest gettCertRequest;

    public GettCertRequest getGettCertRequest() {
        return gettCertRequest;
    }

    public void setGettCertRequest(GettCertRequest gettCertRequest) {
        this.gettCertRequest = gettCertRequest;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public String getAdminpwd() {
        return adminpwd;
    }

    public void setAdminpwd(String adminpwd) {
        this.adminpwd = adminpwd;
    }

    public EnrollmentRequest getEnrollmentRequest() {
        return enrollmentRequest;
    }

    public void setEnrollmentRequest(EnrollmentRequest enrollmentRequest) {
        this.enrollmentRequest = enrollmentRequest;
    }

    public String getUrl() {
        return url;
    }

    public String getMspDir() {
        return mspDir;
    }

    public String getCaName() {
        return caName;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setMspDir(String mspDir) {
        this.mspDir = mspDir;
    }

    public void setCaName(String caName) {
        this.caName = caName;
    }

    public CsrConfig getCsrConfig() {
        return csrConfig;
    }

    public void setCsrConfig(CsrConfig csrConfig) {
        this.csrConfig = csrConfig;
    }

    public RegistrationRequest getRegistrationRequest() {
        return registrationRequest;
    }

    public void setRegistrationRequest(RegistrationRequest registrationRequest) {
        this.registrationRequest = registrationRequest;
    }

    public GetCAInfoRequest getGetCAInfoRequest() {
        return getCAInfoRequest;
    }

    public void setGetCAInfoRequest(GetCAInfoRequest getCAInfoRequest) {
        this.getCAInfoRequest = getCAInfoRequest;
    }

    public RevokeRequest getRevokeRequest() {
        return revokeRequest;
    }

    public void setRevokeRequest(RevokeRequest revokeRequest) {
        this.revokeRequest = revokeRequest;
    }

    @Override
    public String toString() {
        return "ClientConfig{" +
                "url='" + url + '\'' +
                ", mspDir='" + mspDir + '\'' +
                ", caName='" + caName + '\'' +
                ", csrConfig=" + csrConfig +
                ", admin='" + admin + '\'' +
                ", adminpwd='" + adminpwd + '\'' +
                ", enrollmentRequest=" + enrollmentRequest +
                ", registrationRequest=" + registrationRequest +
                ", getCAInfoRequest=" + getCAInfoRequest +
                ", revokeRequest=" + revokeRequest +
                '}';
    }
}
