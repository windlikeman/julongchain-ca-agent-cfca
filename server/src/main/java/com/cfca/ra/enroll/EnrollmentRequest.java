package com.cfca.ra.enroll;

import com.cfca.ra.beans.BaseRequest;

import java.util.Objects;

/**
 * @author zhangchong
 * @create 2018/5/11
 * @Description enroll接口内部使用对象,对 EnrollmentRequestNet 进行封装
 * @CodeReviewer helonglong
 * @since v3.0.0
 */
public class EnrollmentRequest extends BaseRequest {
    private final EnrollmentRequestNet enrollmentRequestNet;
    private final long timestamp;

    public EnrollmentRequest(EnrollmentRequestNet enrollmentRequestNet, long timestamp) {
        this.enrollmentRequestNet = enrollmentRequestNet;
        this.timestamp = timestamp;
    }

    public EnrollmentRequestNet getEnrollmentRequestNet() {
        return enrollmentRequestNet;
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
        EnrollmentRequest that = (EnrollmentRequest) o;
        return timestamp == that.timestamp &&
                Objects.equals(enrollmentRequestNet, that.enrollmentRequestNet);
    }

    @Override
    public int hashCode() {
        return Objects.hash(enrollmentRequestNet, timestamp);
    }

    @Override
    public String toString() {
        return "EnrollmentRequest{" +
                "enrollmentRequestNet=" + enrollmentRequestNet +
                ", timestamp=" + timestamp +
                '}';
    }
}
