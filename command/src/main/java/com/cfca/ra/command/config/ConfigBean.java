package com.cfca.ra.command.config;

import com.cfca.ra.command.internal.ClientIdentity;

/**
 * @author zhangchong
 * @create 2018/5/14
 * @Description config javaBean
 * @CodeReviewer
 * @since v3.0.0
 */
public class ConfigBean {
    private String url;

    /**
     * Membership Service Provider (MSP) directory
     * This is useful when the client is used to enroll a peer or orderer, so
     * that the enrollment artifacts are stored in the format expected by MSP.
     */
    private String mspdir;

    /**
     * TLS section for secure socket connection
     */
    private TlsConfig tls;

    /**
     * Certificate Signing Request section for generating the CSR for an
     * enrollment certificate (ECert)
     */
    private CsrConfig csr;

    /**
     * Registration section used to register a new identity with fabric-ca server
     */
    private ClientIdentity id;

    /**
     * Enrollment section used to enroll an identity with fabric-ca server
     */
    private Enrollment enrollment;

    /**
     * Name of the CA to connect to within the fabric-ca server
     */
    private String caname;

    private String admin;

    private String adminpwd;

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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMspdir() {
        return mspdir;
    }

    public void setMspdir(String mspdir) {
        this.mspdir = mspdir;
    }

    public TlsConfig getTls() {
        return tls;
    }

    public void setTls(TlsConfig tls) {
        this.tls = tls;
    }

    public CsrConfig getCsr() {
        return csr;
    }

    public void setCsr(CsrConfig csr) {
        this.csr = csr;
    }

    public ClientIdentity getId() {
        return id;
    }

    public void setId(ClientIdentity id) {
        this.id = id;
    }

    public Enrollment getEnrollment() {
        return enrollment;
    }

    public void setEnrollment(Enrollment enrollment) {
        this.enrollment = enrollment;
    }

    public String getCaname() {
        return caname;
    }

    public void setCaname(String caname) {
        this.caname = caname;
    }

    @Override
    public String toString() {
        return "ConfigBean{" +
                "url='" + url + '\'' +
                ", mspdir='" + mspdir + '\'' +
                ", tls=" + tls +
                ", csr=" + csr +
                ", id=" + id +
                ", enrollment=" + enrollment +
                ", caname='" + caname + '\'' +
                '}';
    }
}
