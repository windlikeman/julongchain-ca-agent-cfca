package org.bica.julongchain.cfca.ra.reenroll;

import org.bica.julongchain.cfca.ra.beans.BaseRequest;

import java.util.Objects;

/**
 * @author zhangchong
 * @create 2018/5/11
 * @Description reenroll 命令服务器内部使用对象,用于封装 ReenrollmentRequestNet 对象
 * @CodeReviewer helonglong
 * @since v3.0.0
 */
public class ReenrollmentRequest extends BaseRequest {
    private final ReenrollmentRequestNet reenrollmentRequestNet;
    private final long timestamp;

    public ReenrollmentRequest(ReenrollmentRequestNet reenrollmentRequestNet, long timestamp) {
        this.reenrollmentRequestNet = reenrollmentRequestNet;
        this.timestamp = timestamp;
    }

    public ReenrollmentRequestNet getReenrollmentRequestNet() {
        return reenrollmentRequestNet;
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
        ReenrollmentRequest that = (ReenrollmentRequest) o;
        return timestamp == that.timestamp &&
                Objects.equals(reenrollmentRequestNet, that.reenrollmentRequestNet);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reenrollmentRequestNet, timestamp);
    }

    @Override
    public String toString() {
        return "ReenrollmentRequest{" +
                "reenrollmentRequestNet=" + reenrollmentRequestNet +
                ", timestamp=" + timestamp +
                '}';
    }
}
