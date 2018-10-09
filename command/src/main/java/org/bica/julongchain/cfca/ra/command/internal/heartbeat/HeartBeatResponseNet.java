package org.bica.julongchain.cfca.ra.command.internal.heartbeat;

import java.util.Date;

/**
 * @author zhangchong
 * @Create 2018/7/15 9:11
 * @CodeReviewer
 * @Description 心跳响应报文
 * @since
 */
public class HeartBeatResponseNet {
    private final Date date;
    private final String status;

    public HeartBeatResponseNet(Date date, String status) {
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
        return "HeartBeatResponseNet{" +
                "date=" + date +
                ", status='" + status + '\'' +
                '}';
    }
}
