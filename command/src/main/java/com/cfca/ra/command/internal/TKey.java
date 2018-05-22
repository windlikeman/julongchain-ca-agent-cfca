package com.cfca.ra.command.internal;

/**
 * @author zhangchong
 * @create 2018/5/22
 * @Description
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
