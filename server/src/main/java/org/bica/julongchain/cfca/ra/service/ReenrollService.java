package org.bica.julongchain.cfca.ra.service;

import cfca.ra.common.vo.response.CertServiceResponseVO;
import cfca.ra.toolkit.RAClient;
import org.bica.julongchain.cfca.ra.Identity;
import org.bica.julongchain.cfca.ra.RAServer;
import org.bica.julongchain.cfca.ra.RAServerException;
import org.bica.julongchain.cfca.ra.beans.ServerResponseError;
import org.bica.julongchain.cfca.ra.client.IRAClient;
import org.bica.julongchain.cfca.ra.client.RAClientImpl;
import org.bica.julongchain.cfca.ra.enroll.EnrollmentResponseNet;
import org.bica.julongchain.cfca.ra.reenroll.ReenrollmentRequestNet;
import org.bica.julongchain.cfca.ra.utils.AuthUtils;
import org.bica.julongchain.cfca.ra.utils.CertUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangchong
 * @create 2018/5/18
 * @Description 重新签发证书的服务
 * @CodeReviewer helonglong
 * @since v3.0.0
 */
@Service
public class ReenrollService {
    private static final Logger logger = LoggerFactory.getLogger(ReenrollService.class);
    private final RAServer server;

    private final IRAClient raClient;

    @Autowired
    public ReenrollService(RAServer server) {
        this.server = server;
        this.raClient = new RAClientImpl();
        final String serverHomeDir = this.server.getServerHomeDir();
        logger.info("ReenrollService@init : serverHomeDir="+serverHomeDir);
    }

    public EnrollmentResponseNet reenroll(ReenrollmentRequestNet data, String auth) {
        try {
            final byte[] body = AuthUtils.marshal(data);
            final String caname = data.getCaname();
            final Identity i = new Identity(server, caname, auth, true);
            final Identity.AuthPair authPair = i.verify(body);

            final CertServiceResponseVO reenrollResponseFromRA = raClient.reenroll(data, authPair.getUniqueName());
            final String resultCode = reenrollResponseFromRA.getResultCode();
            final String resultMessage = reenrollResponseFromRA.getResultMessage();
            logger.info(resultCode);
            logger.info(resultMessage);
            EnrollmentResponseNet response;
            switch (resultCode) {
                case RAClient.SUCCESS:
                    logger.info(reenrollResponseFromRA.toString());
                    String b64cert = reenrollResponseFromRA.getSignatureCert();
                    final String serialNo = reenrollResponseFromRA.getSerialNo();
                    response = new EnrollmentResponseNet(true, b64cert, null, null);
                    String enrollmentId = CertUtils.getSubjectName(b64cert);
                    server.fillCAInfo(caname, response, enrollmentId);
                    server.storeCert(caname, enrollmentId, b64cert, serialNo);
                    break;
                default:
                    List<ServerResponseError> errors = new ArrayList<>();
                    ServerResponseError error = new ServerResponseError(resultMessage);
                    errors.add(error);
                    response = new EnrollmentResponseNet(false, null, errors, null);
                    break;
            }
            return response;
        } catch (RAServerException e) {
            logger.error("reenroll >>>>>> Failure : " + e.getMessage(), e);
            return buildErrorServerResponse(e);
        }
    }

    private EnrollmentResponseNet buildErrorServerResponse(RAServerException e) {
        List<ServerResponseError> errors = new ArrayList<>();
        ServerResponseError elem = new ServerResponseError(e.getMessage());
        errors.add(elem);
        return new EnrollmentResponseNet(false, null, errors, null);
    }
}
