package com.cfca.ra.ca.register;

import java.util.Objects;

/**
 * @author zhangchong
 * @create 2018/5/14
 * @Description
 * @CodeReviewer
 * @since v3.0.0
 */
public class UserAttrs {
    private final String name;
    private final String value;

    public UserAttrs(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserAttrs attrs = (UserAttrs) o;
        return Objects.equals(name, attrs.name) &&
                Objects.equals(value, attrs.value);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name, value);
    }
}
