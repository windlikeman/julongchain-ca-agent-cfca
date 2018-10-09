package org.bica.julongchain.cfca.ra.command.internal.getcainfo;

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

    public GetCAInfoRequestNet(String caname) {
        this.caname = caname;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("GetCAInfoRequestNet [caname=");
        builder.append(caname);
        builder.append("]");
        return builder.toString();
    }

   
    
}
