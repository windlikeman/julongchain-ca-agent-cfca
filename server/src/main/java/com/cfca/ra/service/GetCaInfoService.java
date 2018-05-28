package com.cfca.ra.service;

import com.cfca.ra.RAServer;
import com.cfca.ra.RAServerException;
import com.cfca.ra.beans.ServerResponseError;
import com.cfca.ra.getcainfo.GetCAInfoResponseNet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangchong
 * @create 2018/5/18
 * @Description 获取 CA 相关信息的服务
 * @CodeReviewer
 * @since v3.0.0
 */
@Service
public class GetCaInfoService {
    private final RAServer server;

    @Autowired
    public GetCaInfoService(RAServer raServer) {
        this.server = raServer;
    }

    public GetCAInfoResponseNet getCaInfo(String caname) {
        try {
            GetCAInfoResponseNet resp = new GetCAInfoResponseNet(true, null);
            server.fillCAInfo(caname, resp);
            return resp;
        }catch (RAServerException e){
            return buildGetCAInfoErrorServerResponse(e);
        }
    }

    private GetCAInfoResponseNet buildGetCAInfoErrorServerResponse(RAServerException e) {
        List<ServerResponseError> errors = new ArrayList<>();
        ServerResponseError elem = new ServerResponseError(e.getReasonCode(), e.getMessage());
        errors.add(elem);
        return new GetCAInfoResponseNet(false, errors);
    }
}
