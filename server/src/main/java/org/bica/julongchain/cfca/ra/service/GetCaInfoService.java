package org.bica.julongchain.cfca.ra.service;

import org.bica.julongchain.cfca.ra.RAServer;
import org.bica.julongchain.cfca.ra.RAServerException;
import org.bica.julongchain.cfca.ra.beans.ServerResponseError;
import org.bica.julongchain.cfca.ra.getcainfo.GetCAInfoResponseNet;
import org.bica.julongchain.cfca.ra.utils.LoggerManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangchong
 * @create 2018/5/18
 * @Description 获取 CA 相关信息的服务
 * @CodeReviewer helonglong
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
        } catch (RAServerException e) {
            LoggerManager.exceptionLogger.error("GetCaInfoService@getCaInfo failed", e);
            return buildGetCAInfoErrorServerResponse(e);
        }
    }

    private GetCAInfoResponseNet buildGetCAInfoErrorServerResponse(RAServerException e) {
        List<ServerResponseError> errors = new ArrayList<>();
        ServerResponseError elem = new ServerResponseError(e.getMessage());
        errors.add(elem);
        return new GetCAInfoResponseNet(false, errors);
    }
}
