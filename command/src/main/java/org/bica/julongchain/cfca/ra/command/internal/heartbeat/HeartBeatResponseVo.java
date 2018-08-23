package org.bica.julongchain.cfca.ra.command.internal.heartbeat;

import java.util.Date;

/**
 * @author zhangchong
 * @Create 2018/7/15 10:28
 * @CodeReviewer
 * @Description
 * @since
 */
public class HeartBeatResponseVo {
    private final Date date;
    private final String status;

    public HeartBeatResponseVo(Date date, String status) {
        this.date = date;
        this.status = status;
    }

    public Date getDate() {
        return date;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "HeartBeatResponseVo{" +
                "date=" + date +
                ", status='" + status + '\'' +
                '}';
    }
}
