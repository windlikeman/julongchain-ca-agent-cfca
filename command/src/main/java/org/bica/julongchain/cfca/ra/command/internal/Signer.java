package org.bica.julongchain.cfca.ra.command.internal;

import java.security.PrivateKey;
import java.util.Arrays;

/**
 * @author zhangchong
 * @create 2018/5/11
 * @Description 由签名私钥和对应公钥证书,以及内部的客户端实现组成
 * @CodeReviewer
 * @since v3.0.0
 */
public class Signer {
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
        StringBuilder builder = new StringBuilder();
        builder.append("Signer [key=");
        builder.append(key);
        builder.append(", cert=");
        builder.append(Arrays.toString(cert));
        builder.append(", client=");
        builder.append(client);
        builder.append("]");
        return builder.toString();
    }
    
}
