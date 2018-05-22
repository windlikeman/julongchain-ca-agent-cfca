package com.cfca.ra;

import com.cfca.ra.ca.CA;
import com.cfca.ra.register.IUser;

import java.security.cert.X509Certificate;

/**
 * @author zhangchong
 * @create 2018/5/16
 * @Description 服务器请求的上下文
 * @CodeReviewer
 * @since v3.0.0
 */
public class ServerRequestContext {
    private final CA ca;
    private final String enrollmentID;
    private final X509Certificate enrollmentCert;
    private final RAServer server;
    private final IUser caller;

    private ServerRequestContext(final Builder builder) {
        this.ca = builder.ca;
        this.enrollmentID = builder.enrollmentID;
        this.enrollmentCert = builder.enrollmentCert;
        this.server = builder.server;
        this.caller = builder.caller;
    }

    public CA getCa() {
        return ca;
    }

    public String getEnrollmentID() {
        return enrollmentID;
    }

    public X509Certificate getEnrollmentCert() {
        return enrollmentCert;
    }

    public RAServer getServer() {
        return server;
    }

    public IUser getCaller() {
        return caller;
    }

    @Override
    public String toString() {
        return "ServerRequestContext{" +
                "ca=" + ca +
                ", enrollmentID='" + enrollmentID + '\'' +
                ", enrollmentCert=" + enrollmentCert +
                ", server=" + server +
                '}';
    }

    public static class Builder{
        private CA ca;
        private String enrollmentID;
        private X509Certificate enrollmentCert;
        private RAServer server;
        private IUser caller;

        public Builder CA(final CA ca){
            this.ca = ca;
            return this;
        }

        public Builder caller(final IUser v){
            this.caller = v;
            return this;
        }

        public Builder enrollmentID(final String v){
            this.enrollmentID = v;
            return this;
        }

        public Builder enrollmentCert(final X509Certificate v){
            this.enrollmentCert = v;
            return this;
        }
        public Builder server(final RAServer v){
            this.server = v;
            return this;
        }

        public ServerRequestContext build(){
            return new ServerRequestContext(this);
        }
    }
}
