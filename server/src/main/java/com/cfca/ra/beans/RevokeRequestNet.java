package com.cfca.ra.beans;

/**
 * @author zhangchong
 * @create 2018/5/14
 * @Description
 * @CodeReviewer
 * @since v3.0.0
 */
public class RevokeRequestNet {
    /**
     * The enrollment ID of the identity whose certificates are to be revoked,
     * including both enrollment certificates and transaction certificates.
     * All future enrollment attempts for this identity will be rejected.
     * If this field is specified, the *serial* and *aki* fields are ignored.
     */
    private final String id;
    /**
     * The Authority Key Identifier of the certificate which is to be revoked.
     * The *serial* field must also be specified.
     */
    private final String aki;
    /**
     * The serial number of the certificate which is to be revoked.
     * The *aki* (Authority Key Identifier) field must also be specified.
     */
    private final String serial;
    /**
     * The reason for revocation.
     * The default value is 0 (ocsp.Unspecified).
     */
    private final String reason;
    /**
     * Name of the CA to direct traffic to within server.
     */
    private final String caname;

    public RevokeRequestNet(String id, String aki, String serial, String reason, String caname) {
        this.id = id;
        this.aki = aki;
        this.serial = serial;
        this.reason = reason;
        this.caname = caname;
    }

    public String getId() {
        return id;
    }

    public String getAki() {
        return aki;
    }

    public String getSerial() {
        return serial;
    }

    public String getReason() {
        return reason;
    }

    public String getCaname() {
        return caname;
    }
}
