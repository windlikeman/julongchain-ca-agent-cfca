package org.bica.julongchain.cfca.ra.command.internal.getcainfo;

/**
 * @author zhangchong
 * @create 2018/5/17
 * @Description GetCAInfo命令的返回的CA信息
 * @CodeReviewer
 * @since v3.0.0
 */
public class GetCAInfoResponseResult {
    /**
     * The name of the root CA associated with this server.
     */
    private final String caname;

    /**
     * Base 64 encoded PEM-encoded certificate chain of the server's signing
     * certificate.
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
        StringBuilder builder = new StringBuilder();
        builder.append("GetCAInfoResponseResult [caname=");
        builder.append(caname);
        builder.append(", cachain=");
        builder.append(cachain);
        builder.append("]");
        return builder.toString();
    }

}
