package com.cfca.ra.gettcert;

import java.util.List;

/**
 * @author zhangchong
 * @create 2018/5/22
 * @Description 处理 GettCert 命令,供服务器内部使用
 * @CodeReviewer helonglong
 * @since v3.0.0
 */
public class GettCertResponse {

    private long id;
    private long ts;
    private byte[] key;
    private List<TCert> tCerts;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTs() {
        return ts;
    }

    public void setTs(long ts) {
        this.ts = ts;
    }

    public byte[] getKey() {
        return key;
    }

    public void setKey(byte[] key) {
        this.key = key;
    }

    public List<TCert> gettCerts() {
        return tCerts;
    }

    public void settCerts(List<TCert> tCerts) {
        this.tCerts = tCerts;
    }

    @Override
    public String toString() {
        return "GettCertResponse{" +
                "certificates=" + tCerts +
                '}';
    }
}
