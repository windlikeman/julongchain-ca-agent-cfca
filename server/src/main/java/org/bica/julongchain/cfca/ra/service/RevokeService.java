package org.bica.julongchain.cfca.ra.service;

import org.apache.commons.lang.StringUtils;
import org.bica.julongchain.cfca.ra.Identity;
import org.bica.julongchain.cfca.ra.RAServer;
import org.bica.julongchain.cfca.ra.RAServerException;
import org.bica.julongchain.cfca.ra.beans.ServerResponseError;
import org.bica.julongchain.cfca.ra.client.IRAClient;
import org.bica.julongchain.cfca.ra.client.RAClientImpl;
import org.bica.julongchain.cfca.ra.register.IUser;
import org.bica.julongchain.cfca.ra.revoke.RevokeRequestNet;
import org.bica.julongchain.cfca.ra.revoke.RevokeResponseNet;
import org.bica.julongchain.cfca.ra.revoke.RevokeResult;
import org.bica.julongchain.cfca.ra.utils.AuthUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangchong
 * @create 2018/5/18
 * @Description 吊销服务
 * @CodeReviewer helonglong
 * @since v3.0.0
 */
@Service
public class RevokeService {
    private static final Logger logger = LoggerFactory.getLogger(RAServiceImpl.class);
    private final RAServer server;
    private final IRAClient raClient;

    @Autowired
    public RevokeService(RAServer server) {
        this.server = server;
        this.raClient = new RAClientImpl();
        final String serverHomeDir = this.server.getServerHomeDir();
        logger.info("RevokeService@init : serverHomeDir="+serverHomeDir);
    }

    public RevokeResponseNet revoke(RevokeRequestNet data, String auth) {
        try {
            logger.info("revoke >>>>>> RevokeRequestNet : " + data + ", auth=" + auth);

            final String caName = data.getCaname();
            final String id = data.getId();

            final Identity i = new Identity(server, caName, auth, false);
            final byte[] body = AuthUtils.marshal(data);
            i.verify(body);

            server.attributeIsTrue(caName, id, "hf.Revoker");
            if (StringUtils.isEmpty(data.getSerial()) && StringUtils.isEmpty(data.getAki()) && StringUtils.isEmpty(data.getId())) {
                throw new RAServerException( "Either Name or Serial and AKI are required for a revoke request");
            }

            if (!StringUtils.isEmpty(data.getId())) {
                final IUser user = server.getUser(caName, data.getId(), null);
                if (user != null) {
                    user.revoke();
                }
            }
            final RevokeResult revokeResult = raClient.revoke(data);
            final String serialNo = revokeResult.getSerialNo();
            server.revokeCert(caName, serialNo);
            if (revokeResult.isOk()){
                return new RevokeResponseNet(true, revokeResult.getResultMessage(), null, null);
            } else {
                return buildErrorResponse(revokeResult);
            }

        } catch (RAServerException e) {
            logger.error("revoke >>>>>> Failure : " + e.getMessage(), e);
            return buildErrorResponse(e);
        }
    }

    private RevokeResponseNet buildErrorResponse(RevokeResult revokeResult) {
        List<ServerResponseError> errors = new ArrayList<>();
        ServerResponseError elem = new ServerResponseError(revokeResult.getResultMessage());
        errors.add(elem);
        return new RevokeResponseNet(false, null, errors, null);
    }

    private RevokeResponseNet buildErrorResponse(RAServerException e) {
        List<ServerResponseError> errors = new ArrayList<>();
        ServerResponseError elem = new ServerResponseError(e.getMessage());
        errors.add(elem);
        return new RevokeResponseNet(false, null, errors, null);
    }
}
