package com.cfca.ra.beans;

/**
 * @author zhangchong
 * @create 2018/5/22
 * @Description  GettCert命令服务器返回给客户端交易证书所用的对称加密密钥,用以解密
 * @CodeReviewer
 * @since v3.0.0
 */
class TKey {
    /**
     * Attribute name
     */
    private final String name;
    /**
     * Base 64 encoded symmetric key
     */
    private final String value;

    TKey(String name, String value) {
        this.name = name;
        this.value = value;
    }
}
