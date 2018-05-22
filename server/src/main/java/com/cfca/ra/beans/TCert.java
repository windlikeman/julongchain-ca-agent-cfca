package com.cfca.ra.beans;

import java.util.List;

/**
 * @author zhangchong
 * @create 2018/5/22
 * @Description
 * @CodeReviewer
 * @since v3.0.0
 */
class TCert {
    /**
     * Based 64 encoded transaction certificate
     */
    private final String cert;

    /**
     * An array of decryption keys, assuming encryption was enabled.
     */
    private final List<TKey> keys;

    TCert(String cert, List<TKey> keys) {
        this.cert = cert;
        this.keys = keys;
    }
}
