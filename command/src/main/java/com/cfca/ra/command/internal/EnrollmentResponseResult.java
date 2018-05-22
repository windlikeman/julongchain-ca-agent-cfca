package com.cfca.ra.command.internal;
/**
 * @author zhangchong
 * @create 2018/5/11
 * @Description
 * @CodeReviewer
 * @since v3.0.0
 */
class EnrollmentResponseResult {
    /**
     * The name of the root CA associated with this server.
     */
    private final String caname;

    /**
     * Base 64 encoded PEM-encoded certificate chain of the server's signing certificate.
     */
    private final String cachain;

    public EnrollmentResponseResult(String caname, String cachain) {
        this.caname = caname;
        this.cachain = cachain;
    }
}
