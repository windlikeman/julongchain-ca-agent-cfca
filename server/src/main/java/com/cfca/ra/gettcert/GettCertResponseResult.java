package com.cfca.ra.gettcert;

import java.util.List;

/**
 * @author zhangchong
 * @create 2018/5/22
 * @Description GettCert命令服务器返回给客户端的 tcert 对象
 * @CodeReviewer
 * @since v3.0.0
 */
public class GettCertResponseResult {
    /**
     * Transaction batch identifier
     */
    private final long id;

    /**
     * Time stamp
     */
    private final long ts;
    /**
     * Base 64 encoded key
     */
    private final String key;

    /**
     * An array of transaction certificates
     */
    private final List<TCert> tcerts;

    public GettCertResponseResult(long id, long ts, String key, List<TCert> tcerts) {
        this.id = id;
        this.ts = ts;
        this.key = key;
        this.tcerts = tcerts;
    }
}