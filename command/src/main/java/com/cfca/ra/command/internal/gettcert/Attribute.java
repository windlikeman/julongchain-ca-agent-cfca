package com.cfca.ra.command.internal.gettcert;

/**
 * @author zhangchong
 * @create 2018/5/24
 * @Description 属性, 名称和值的键值对
 * @CodeReviewer
 * @since
 */
class Attribute {

    private final String name;
    private final String value;
    private final boolean eCert;

    Attribute(String name, String value, boolean eCert) {
        this.name = name;
        this.value = value;
        this.eCert = eCert;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public boolean iseCert() {
        return eCert;
    }
}
