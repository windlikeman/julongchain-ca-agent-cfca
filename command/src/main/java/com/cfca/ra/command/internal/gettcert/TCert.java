package com.cfca.ra.command.internal.gettcert;

import java.util.List;

/**
 * @author zhangchong
 * @create 2018/5/22
 * @Description GettCert命令服务器返回给客户端的 tcert 对象中的交易证书详细信息
 * @CodeReviewer
 * @since v3.0.0
 */
public class TCert {
    /**
     * 基于 B64 编码的交易证书
     */
    private final String cert;

    /**
     * 一组解密密钥,假设加密已启用
     */
    private final List<TKey> keys;

    TCert(String cert, List<TKey> keys) {
        this.cert = cert;
        this.keys = keys;
    }

    @Override
    public String toString() {
        return "TCert{" + "cert='" + cert + '\'' + ", keys=" + keys + '}';
    }
}
