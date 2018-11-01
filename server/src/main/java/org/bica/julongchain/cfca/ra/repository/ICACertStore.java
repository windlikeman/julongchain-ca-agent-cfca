package org.bica.julongchain.cfca.ra.repository;

import org.bica.julongchain.cfca.ra.RAServerException;
import org.bouncycastle.asn1.x509.Certificate;

/**
 * @author zhangchong
 * @create 2018/5/22
 * @Description CA 证书管理的接口类,用于对接数据库
 * @CodeReviewer helonglong
 * @since v3.0.0
 */
public interface ICACertStore {
    void storeCert(String caName, String enrollmentId, String b64cert, String serialNo) throws RAServerException;

    Certificate loadCert(String caName, String enrollmentId) throws RAServerException;

    String getCertFilePath(String caName, String serial) throws RAServerException;

    void revokeCert(String caName, String serialNo) throws RAServerException;
}
