package com.cfca.ra.ca.repository;

import com.cfca.ra.RAServerException;
import org.bouncycastle.asn1.x509.Certificate;

/**
 * @author zhangchong
 * @create 2018/5/22
 * @Description
 * @CodeReviewer
 * @since
 */
public interface ICAStore {
    void storeCert(String username, String b64cert) throws RAServerException;

    Certificate loadCert(String enrollmentId) throws RAServerException;
}
