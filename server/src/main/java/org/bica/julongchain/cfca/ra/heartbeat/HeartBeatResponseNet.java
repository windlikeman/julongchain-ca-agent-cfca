package org.bica.julongchain.cfca.ra.heartbeat;

import org.bica.julongchain.cfca.ra.service.RAServiceImpl;

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
    private final RAServiceImpl.Status status;

    public HeartBeatResponseNet(Date date, RAServiceImpl.Status status) {
        this.date = date;
        this.status = status;
    }

    public Date getDate() {
        return date;
    }

    public RAServiceImpl.Status getStatus() {
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
