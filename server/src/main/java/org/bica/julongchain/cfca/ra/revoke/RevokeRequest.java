package org.bica.julongchain.cfca.ra.revoke;

import org.bica.julongchain.cfca.ra.beans.BaseRequest;

import java.util.Objects;

/**
 * @author zhangchong
 * @create 2018/5/14
 * @Description 吊销接口内部使用的请求对象,用于封装 RevokeRequestNet 对象
 * @CodeReviewer helonglong
 * @since v3.0.0
 */
public class RevokeRequest  extends BaseRequest {
    private final RevokeRequestNet revokeRequestNet;

    private final long timestamp;

    public RevokeRequest(RevokeRequestNet revokeRequestNet, long timestamp) {
        this.revokeRequestNet = revokeRequestNet;
        this.timestamp = timestamp;
    }

    public RevokeRequestNet getRevokeRequestNet() {
        return revokeRequestNet;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RevokeRequest that = (RevokeRequest) o;
        return timestamp == that.timestamp &&
                Objects.equals(revokeRequestNet, that.revokeRequestNet);
    }

    @Override
    public int hashCode() {
        return Objects.hash(revokeRequestNet, timestamp);
    }

    @Override
    public String toString() {
        return "RevokeRequest{" +
                "revokeRequestNet=" + revokeRequestNet +
                ", timestamp=" + timestamp +
                '}';
    }
}
