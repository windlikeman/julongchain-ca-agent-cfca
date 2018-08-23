package org.bica.julongchain.cfca.ra.command.internal;

import org.bica.julongchain.cfca.ra.command.config.CsrConfig;
import org.bica.julongchain.cfca.ra.command.internal.enroll.EnrollmentRequest;
import org.bica.julongchain.cfca.ra.command.internal.getcainfo.GetCAInfoRequest;
import org.bica.julongchain.cfca.ra.command.internal.register.RegistrationRequest;
import org.bica.julongchain.cfca.ra.command.internal.revoke.RevokeRequest;

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
     * 配置文件 MSPDIR 的默认值
     */
    public static final String DEFAULT_CONFIG_MSPDIR_VAL = "<<<MSPDIR>>>";

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

    private String sequenceNo;

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

    /**
     * 身份ID
     */
    private String enrollmentId;

    public String getSequenceNo() {
        return sequenceNo;
    }

    public void setSequenceNo(String sequenceNo) {
        this.sequenceNo = sequenceNo;
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

    public void setEnrollmentId(String enrollmentId) {
        this.enrollmentId = enrollmentId;
    }

    public String getEnrollmentId() {
        return enrollmentId;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ClientConfig [url=");
        builder.append(url);
        builder.append(", mspDir=");
        builder.append(mspDir);
        builder.append(", caName=");
        builder.append(caName);
        builder.append(", csrConfig=");
        builder.append(csrConfig);
        builder.append(", admin=");
        builder.append(admin);
        builder.append(", adminpwd=");
        builder.append(adminpwd);
        builder.append(", enrollmentRequest=");
        builder.append(enrollmentRequest);
        builder.append(", registrationRequest=");
        builder.append(registrationRequest);
        builder.append(", getCAInfoRequest=");
        builder.append(getCAInfoRequest);
        builder.append(", revokeRequest=");
        builder.append(revokeRequest);
        builder.append(", sequenceNo=");
        builder.append(sequenceNo);
        builder.append(", enrollmentId=");
        builder.append(enrollmentId);
        builder.append("]");
        return builder.toString();
    }

}
