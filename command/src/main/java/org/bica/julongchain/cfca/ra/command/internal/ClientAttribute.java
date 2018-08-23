package org.bica.julongchain.cfca.ra.command.internal;

/**
 * @author zhangchong
 * @create 2018/5/15
 * @Description ClientAttribute 用于标识客户端属性,以键值对表示
 * @CodeReviewer
 * @since v3.0.0
 */
public class ClientAttribute {
    private final String name;
    private final String val;

    public ClientAttribute(String name, String val) {
        this.name = name;
        this.val = val;
    }

    public String getName() {
        return name;
    }

    public String getVal() {
        return val;
    }

    @Override
    public String toString() {
        return "ClientAttribute{" + "name='" + name + '\'' + ", val='" + val + '\'' + '}';
    }
}
