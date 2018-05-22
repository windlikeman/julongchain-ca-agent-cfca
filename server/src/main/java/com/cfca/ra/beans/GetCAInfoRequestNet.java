package com.cfca.ra.beans;

/**
 * @author zhangchong
 * @create 2018/5/17
 * @Description GetCAInfo命令的网络请求对象
 * @CodeReviewer
 * @since v3.0.0
 */
public class GetCAInfoRequestNet {
    private final String caname;

    public GetCAInfoRequestNet(final String caname) {
        this.caname = caname;
    }

    public String getCaname() {
        return caname;
    }

    @Override
    public String toString() {
        return "GetCAInfoRequestNet{" +
                "caname='" + caname + '\'' +
                '}';
    }
}
