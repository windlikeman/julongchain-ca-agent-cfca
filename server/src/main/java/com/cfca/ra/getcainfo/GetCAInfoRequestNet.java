package com.cfca.ra.getcainfo;

/**
 * @author zhangchong
 * @create 2018/5/17
 * @Description GetCAInfo命令的网络请求对象,用于调用服务器Restful接口
 * @CodeReviewer
 * @since v3.0.0
 */
public class GetCAInfoRequestNet {
    /**
     * 所要指向的 ca 的名字
     */
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
