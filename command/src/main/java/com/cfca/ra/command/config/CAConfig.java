package com.cfca.ra.command.config;

/**
 * @author zhangchong
 * @create 2018/5/15
 * @Description
 * @CodeReviewer
 * @since v3.0.0
 */
public class CAConfig {
    private int pathlen;
    private int pathlenzero;
    private int expiry;

    public int getPathlen() {
        return pathlen;
    }

    public void setPathlen(int pathlen) {
        this.pathlen = pathlen;
    }

    public int getPathlenzero() {
        return pathlenzero;
    }

    public void setPathlenzero(int pathlenzero) {
        this.pathlenzero = pathlenzero;
    }

    public int getExpiry() {
        return expiry;
    }

    public void setExpiry(int expiry) {
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
