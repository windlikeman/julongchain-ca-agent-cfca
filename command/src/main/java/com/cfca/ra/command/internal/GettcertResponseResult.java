package com.cfca.ra.command.internal;

import java.util.List;

/**
 * @author zhangchong
 * @create 2018/5/22
 * @Description
 * @CodeReviewer
 * @since
 */
public class GettcertResponseResult {
    /**
     * Transaction batch identifier
     */
    private final int id;

    /**
     * Time stamp
     */
    private final int ts;
    /**
     * Base 64 encoded key
     */
    private final String key;

    /**
     * An array of transaction certificates
     */
    private final List<TCert> tcerts;

    public GettcertResponseResult(int id, int ts, String key, List<TCert> tcerts) {
        this.id = id;
        this.ts = ts;
        this.key = key;
        this.tcerts = tcerts;
    }
}
