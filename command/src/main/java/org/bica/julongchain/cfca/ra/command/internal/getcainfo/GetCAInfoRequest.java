package org.bica.julongchain.cfca.ra.command.internal.getcainfo;

/**
 * @author zhangchong
 * @create 2018/5/17
 * @Description 获取ca信息的命令内部使用的请求参数,用于适配服务器Restful接口用的参数
 * @CodeReviewer
 * @since v3.0.0
 */
public class GetCAInfoRequest {
    private final String caName;

    public GetCAInfoRequest(String caName) {
        this.caName = caName;
    }

    public String getCaName() {
        return caName;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("GetCAInfoRequest [caName=");
        builder.append(caName);
        builder.append("]");
        return builder.toString();
    }

  
    
}
