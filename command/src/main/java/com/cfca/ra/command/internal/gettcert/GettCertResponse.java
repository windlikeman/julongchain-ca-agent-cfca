package com.cfca.ra.command.internal.gettcert;

import org.bouncycastle.asn1.x509.Certificate;

import java.util.List;

/**
 * @author zhangchong
 * @create 2018/5/22
 * @Description 处理 Enrollment 命令与服务器之间的网络交互, 发送和接受网络消息
 * @CodeReviewer
 * @since v3.0.0
 */
public class GettCertResponse {
    private final List<Certificate> certificates;

    public GettCertResponse(List<Certificate> certificates) {
        this.certificates = certificates;
    }

    public List<Certificate> getCertificates() {
        return certificates;
    }

    @Override
    public String toString() {
        return "GettCertResponse{" +
                "certificates=" + certificates +
                '}';
    }
}
