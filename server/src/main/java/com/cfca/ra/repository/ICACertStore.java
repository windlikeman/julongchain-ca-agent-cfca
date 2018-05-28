package com.cfca.ra.repository;

import com.cfca.ra.RAServerException;
import org.bouncycastle.asn1.x509.Certificate;

/**
 * @author zhangchong
 * @create 2018/5/22
 * @Description CA 证书管理的接口类,用于对接数据库
 * @CodeReviewer
 * @since v3.0.0
 */
public interface ICACertStore {
    void storeCert(String username, String b64cert) throws RAServerException;

    Certificate loadCert(String enrollmentId) throws RAServerException;

    String getCertFilePath(String serial) throws RAServerException;

    boolean containsCert(String enrollmentID)throws RAServerException;

    String loadB64CertString(String enrollmentID)throws RAServerException;
}
