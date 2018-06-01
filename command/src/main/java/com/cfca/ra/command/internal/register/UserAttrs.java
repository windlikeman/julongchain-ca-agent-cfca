package com.cfca.ra.command.internal.register;
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

    public UserAttrs(String name, String value) {
        this.name = name;
        this.value = value;
    }

    String getName() {
        return name;
    }

    String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "UserAttrs{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
