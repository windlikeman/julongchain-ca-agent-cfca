package com.cfca.ra.command.internal.gettcert;

import java.util.List;

/**
 * @author zhangchong
 * @create 2018/5/22
 * @Description 处理 GettCert 命令,供客户端内部使用
 * @CodeReviewer
 * @since v3.0.0
 */
public class GettCertResponse {

    long id;
    long ts;
    byte[] key;

    List<TCert> tCerts;

    public GettCertResponse(List<TCert> certificates) {
        this.tCerts = certificates;
    }

    public List<TCert> getCertificates() {
        return tCerts;
    }

    @Override
    public String toString() {
        return "GettCertResponse{" +
                "certificates=" + tCerts +
                '}';
    }
}
