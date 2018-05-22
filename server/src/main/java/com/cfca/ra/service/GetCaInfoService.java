package com.cfca.ra.service;

import com.cfca.ra.RAServer;
import com.cfca.ra.RAServerException;
import com.cfca.ra.beans.ServerResponseError;
import com.cfca.ra.beans.GetCAInfoResponseNet;
import com.cfca.ra.ca.CA;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangchong
 * @create 2018/5/18
 * @Description
 * @CodeReviewer
 * @since
 */
@Service
public class GetCaInfoService {
    private static final Logger logger = LoggerFactory.getLogger(GetCaInfoService.class);
    private final RAServer server;

    @Autowired
    public GetCaInfoService(RAServer raServer) {
        this.server = raServer;
    }

    public GetCAInfoResponseNet getCaInfo(String caname) {
        try {
            GetCAInfoResponseNet resp = new GetCAInfoResponseNet(true, null);
            final CA ca = server.getCA(caname);
            ca.fillCAInfo(resp);
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
