package com.cfca.ra.ca;

import java.util.List;
import java.util.Objects;

/**
 * @author zhangchong
 * @create 2018/5/16
 * @Description CA 配置注册属性
 * CAConfigRegistry 控制 fabric-ca-server 如何做两件事:
 *  1) 验证包含用户名和密码的注册请求(也被称为enrollment ID 和 secret).
 *  2) 一旦通过身份验证,就会检索 ca-server 可选择放入 TCerts 的身份属性名称和值,它将在区块链上进行交易. 这些属性对链码中的访问控制决策很有用.
 * @CodeReviewer helonglong
 * @since v3.0.0
 */
public class CAConfigRegistry {
    /**
     * 配置文件中标记该 CA 所能签发的最大证书数量
     * 一个密码可以重新用于注册的最大次数,default: -1, 意为没有限制
     *
     */
    private final int maxEnrollments;
    /**
     * 配置文件中表示在该 CA 注册的各个身份信息所包含的各个属性
     */
    private final List<CAConfigIdentity> identities;

    public CAConfigRegistry(int maxEnrollments, List<CAConfigIdentity> identities) {
        this.maxEnrollments = maxEnrollments;
        this.identities = identities;
    }

    public int getMaxEnrollments() {
        return maxEnrollments;
    }

    public List<CAConfigIdentity>getIdentities() {
        return identities;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CAConfigRegistry that = (CAConfigRegistry) o;
        return maxEnrollments == that.maxEnrollments &&
                Objects.equals(identities, that.identities);
    }

    @Override
    public int hashCode() {

        return Objects.hash(maxEnrollments, identities);
    }

    @Override
    public String toString() {
        return "CAConfigRegistry{" +
                "maxEnrollments=" + maxEnrollments +
                ", identities=" + identities +
                '}';
    }
}
