package com.cfca.ra.command.internal;

import java.security.KeyPair;

/**
 * @author zhangchong
 * @create 2018/5/15
 * @Description 构建出来的CSR和密钥对
 * @CodeReviewer
 * @since v3.0.0
 */
public class CsrResult {
    private final String csr;

    private final KeyPair keyPair;

    public CsrResult(String csr, KeyPair keyPair) {
        this.csr = csr;
        this.keyPair = keyPair;
    }

    public KeyPair getKeyPair() {
        return keyPair;
    }

    public String getCsr() {
        return csr;
    }
}
