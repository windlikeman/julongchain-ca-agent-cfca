package org.bica.julongchain.cfca.ra.command.internal.register;

import java.util.Objects;

/**
 * @author zhangchong
 * @create 2018/5/11
 * @Description 用户信息其中的用户属性
 * @CodeReviewer
 * @since v3.0.0
 */
public class UserAttrs {
    private final String name;
    private final String value;
    private final boolean eCert;

    public UserAttrs(String name, String value) {
        this(name, value, false);
    }

    public UserAttrs(String name, String value, boolean eCert) {
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
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserAttrs attribute = (UserAttrs) o;
        return eCert == attribute.eCert &&
                Objects.equals(name, attribute.name) &&
                Objects.equals(value, attribute.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value, eCert);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("UserAttrs [name=");
        builder.append(name);
        builder.append(", value=");
        builder.append(value);
        builder.append(", eCert=");
        builder.append(eCert);
        builder.append("]");
        return builder.toString();
    }

   
}
