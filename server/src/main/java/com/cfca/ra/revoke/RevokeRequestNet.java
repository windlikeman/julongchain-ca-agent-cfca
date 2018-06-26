package com.cfca.ra.revoke;

import java.util.Objects;

/**
 * @author zhangchong
 * @create 2018/5/14
 * @Description 吊销接口网络请求参数,用于调用服务器Restful接口
 * @CodeReviewer helonglong
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RevokeRequestNet that = (RevokeRequestNet) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(aki, that.aki) &&
                Objects.equals(serial, that.serial) &&
                Objects.equals(reason, that.reason) &&
                Objects.equals(caname, that.caname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, aki, serial, reason, caname);
    }

    @Override
    public String toString() {
        return "RevokeRequestNet{" +
                "id='" + id + '\'' +
                ", aki='" + aki + '\'' +
                ", serial='" + serial + '\'' +
                ", reason='" + reason + '\'' +
                ", caname='" + caname + '\'' +
                '}';
    }
}
