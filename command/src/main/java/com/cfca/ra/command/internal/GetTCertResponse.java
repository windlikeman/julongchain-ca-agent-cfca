package com.cfca.ra.command.internal;

import org.bouncycastle.asn1.x509.Certificate;

import java.util.List;

/**
 * @author zhangchong
 * @create 2018/5/22
 * @Description
 * @CodeReviewer
 * @since v3.0.0
 */
class GetTCertResponse {
    private final List<Certificate> certificates;

    GetTCertResponse(List<Certificate> certificates) {
        this.certificates = certificates;
    }

    public List<Certificate> getCertificates() {
        return certificates;
    }

    @Override
    public String toString() {
        return "GetTCertResponse{" +
                "certificates=" + certificates +
                '}';
    }
}
