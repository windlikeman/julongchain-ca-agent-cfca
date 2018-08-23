package org.bica.julongchain.cfca.ra.command.config;

import java.util.Objects;

/**
 * @author zhangchong
 * @create 2018/5/15
 * @Description CA 配置信息
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
        return "CAConfig{" + "pathlen=" + pathlen + ", pathlenzero=" + pathlenzero + ", expiry=" + expiry + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CAConfig caConfig = (CAConfig) o;
        return pathlen == caConfig.pathlen && pathlenzero == caConfig.pathlenzero && expiry == caConfig.expiry;
    }

    @Override
    public int hashCode() {

        return Objects.hash(pathlen, pathlenzero, expiry);
    }
}
