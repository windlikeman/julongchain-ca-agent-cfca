package com.cfca.ra.ca;

import java.util.Objects;

/**
 * @author zhangchong
 * @create 2018/5/24
 * @Description 属性, 名称和值的键值对
 * @CodeReviewer helonglong
 * @since v3.0.0
 */
public class Attribute {

    private final String name;
    private final String value;
    private final boolean eCert;

    public Attribute(String name, String value, boolean eCert) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()){
            return false;
        }
        Attribute attribute = (Attribute) o;
        return eCert == attribute.eCert &&
                Objects.equals(name, attribute.name) &&
                Objects.equals(value, attribute.value);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name, value, eCert);
    }
}
