package com.cfca.ra.beans;

/**
 * @author zhangchong
 * @create 2018/5/15
 * @Description CA 配置信息
 * @CodeReviewer
 * @since v3.0.0
 */
public class CAConfig {
    /**
     *
     */
    private int pathlen;
    /**
     *
     */
    private int pathlenzero;
    /**
     *
     */
    private int expiry;

    public int getPathlen() {
        return pathlen;
    }

    public void setPathlen(final int pathlen) {
        this.pathlen = pathlen;
    }

    public int getPathlenzero() {
        return pathlenzero;
    }

    public void setPathlenzero(final int pathlenzero) {
        this.pathlenzero = pathlenzero;
    }

    public int getExpiry() {
        return expiry;
    }

    public void setExpiry(final int expiry) {
        this.expiry = expiry;
    }

    @Override
    public String toString() {
        return "CAConfig{" +
                "pathlen=" + pathlen +
                ", pathlenzero=" + pathlenzero +
                ", expiry=" + expiry +
                '}';
    }
}
