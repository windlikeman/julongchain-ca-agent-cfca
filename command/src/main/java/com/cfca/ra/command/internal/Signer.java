package com.cfca.ra.command.internal;

import org.bouncycastle.util.encoders.Hex;

import java.security.PrivateKey;

/**
 * @author zhangchong
 * @create 2018/5/11
 * @Description
 * @CodeReviewer
 * @since v3.0.0
 */
class Signer {
    /**
     * 生成的密钥对的私钥
     */
    private final PrivateKey key;
    /**
     * b64 解码后的x509证书字节
     */
    private final byte[] cert;
    private final Client client;

    Signer(PrivateKey key, byte[] cert, Client client) {
        this.key = key;
        this.cert = cert;
        this.client = client;
    }

    public PrivateKey getKey() {
        return key;
    }

    public byte[] getCert() {
        return cert;
    }

    public Client getClient() {
        return client;
    }

    @Override
    public String toString() {
        return "Signer{" +
                "key=" + key + "\n" +
                "cert=" + Hex.toHexString(cert) + "\n" +
                ", client=" + client +
                '}';
    }
}
