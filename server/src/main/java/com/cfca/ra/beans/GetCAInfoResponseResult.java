package com.cfca.ra.beans;

/**
 * @author zhangchong
 * @create 2018/5/17
 * @Description  GetCAInfo命令的返回的CA信息
 * @CodeReviewer
 * @since v3.0.0
 */
public class GetCAInfoResponseResult {
    /**
     * The name of the root CA associated with this server.
     */
    private final String caname;

    /**
     * Base 64 encoded PEM-encoded certificate chain of the server's signing certificate.
     */
    private final String cachain;

    public GetCAInfoResponseResult(String caname, String cachain) {
        this.caname = caname;
        this.cachain = cachain;
    }

    public String getCaname() {
        return caname;
    }

    public String getCachain() {
        return cachain;
    }

    @Override
    public String toString() {
        return "GetCAInfoResponseResult{" +
                "caname='" + caname + '\'' +
                ", cachain='" + cachain + '\'' +
                '}';
    }
}
