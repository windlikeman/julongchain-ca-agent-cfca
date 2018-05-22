package com.cfca.ra.command.internal;

/**
 * @author zhangchong
 * @create 2018/5/17
 * @Description GetCAInfo命令的网络请求对象
 * @CodeReviewer
 * @since v3.0.0
 */
class GetCAInfoRequestNet {
    private final String caname;

    GetCAInfoRequestNet(String caname) {
        this.caname = caname;
    }

    @Override
    public String toString() {
        return "GetCAInfoRequestNet{" +
                "caname='" + caname + '\'' +
                '}';
    }
}
