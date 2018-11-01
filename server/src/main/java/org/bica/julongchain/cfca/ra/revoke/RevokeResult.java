package org.bica.julongchain.cfca.ra.revoke;

/**
 * @author zhangchong
 * @Create 2018/7/25 15:22
 * @CodeReviewer
 * @Description
 * @since
 */
public class RevokeResult {
    private final String resultMessage;
    private final boolean ok;
    private final String serialNo;

    public RevokeResult(boolean ok, String resultMessage, String serialNo) {
        this.ok = ok;
        this.resultMessage = resultMessage;
        this.serialNo = serialNo;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public boolean isOk() {
        return ok;
    }

    public String getSerialNo() {
        return serialNo;
    }

    @Override
    public String toString() {
        return "RevokeResult{" +
                "resultMessage='" + resultMessage + '\'' +
                ", ok=" + ok +
                ", serialNo='" + serialNo + '\'' +
                '}';
    }
}
