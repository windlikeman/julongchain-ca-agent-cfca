package org.bica.julongchain.cfca.ra.po;

import org.springframework.data.mongodb.core.mapping.Document;
/**
 * @author zhangchong
 * @create 2018/5/18
 * @Description 签发证书的服务
 * @CodeReviewer helonglong
 * @since v3.0.0
 */
@Document(collection = "t_enrollcert")
public class EnrollCertPo {

    /**
     * 数据库中的id
     */
    public final String id;
    /**
     * 身份ID
     */
    public final String enrollmentId;
    /**
     * ca 名称
     */
    private final String caName;

    /**
     * 证书序列号
     */
    private final String serialNo;
    /**
     * 经过B64编码的证书数据
     */
    private final String b64Cert;
    /**
     * 0-吊销 1-可用
     */
    private final int certStatus;

    public EnrollCertPo(String id, String enrollmentId, String caName, String serialNo, String b64Cert, int certStatus) {
        super();
        this.id = id;
        this.enrollmentId = enrollmentId;
        this.caName = caName;
        this.b64Cert = b64Cert;
        this.certStatus = certStatus;
        this.serialNo = serialNo;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public String getId() {
        return id;
    }

    public String getEnrollmentId() {
        return enrollmentId;
    }

    public String getCaName() {
        return caName;
    }

    public String getB64Cert() {
        return b64Cert;
    }

    public int getCertStatus() {
        return certStatus;
    }

    @Override
    public String toString() {
        return "EnrollCertPo{" +
                "id='" + id + '\'' +
                ", enrollmentId='" + enrollmentId + '\'' +
                ", caName='" + caName + '\'' +
                ", serialNo='" + serialNo + '\'' +
                ", certStatus=" + certStatus +
                '}';
    }
}
