package org.bica.julongchain.cfca.ra.controller;

import java.security.Security;

import org.bica.julongchain.cfca.ra.heartbeat.HeartBeatResponseNet;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import org.bica.julongchain.cfca.ra.enroll.EnrollmentRequest;
import org.bica.julongchain.cfca.ra.enroll.EnrollmentRequestNet;
import org.bica.julongchain.cfca.ra.enroll.EnrollmentResponseNet;
import org.bica.julongchain.cfca.ra.getcainfo.GetCAInfoRequestNet;
import org.bica.julongchain.cfca.ra.getcainfo.GetCAInfoResponseNet;
import org.bica.julongchain.cfca.ra.reenroll.ReenrollmentRequestNet;
import org.bica.julongchain.cfca.ra.register.RegistrationRequestNet;
import org.bica.julongchain.cfca.ra.register.RegistrationResponseNet;
import org.bica.julongchain.cfca.ra.revoke.RevokeRequestNet;
import org.bica.julongchain.cfca.ra.revoke.RevokeResponseNet;
import org.bica.julongchain.cfca.ra.service.RAServiceImpl;

/**
 * @author zhangchong
 * @create 2018/5/14
 * @Description Restful 接口控制类,目前定义了 enroll reenroll register gettcert getcainfo
 *              revoke等接口的处理
 * @CodeReviewer helonglong
 * @since v3.0.0
 */
@RestController
@Configuration
public class RAController {
    private static final Logger logger = LoggerFactory.getLogger(RAController.class);

    private final BouncyCastleProvider provider;

    private final RAServiceImpl raService;

    @Autowired
    public RAController(RAServiceImpl raService) {
        this.raService = raService;
        this.provider = new BouncyCastleProvider();
        Security.addProvider(provider);
    }

    @RequestMapping(value = "/enroll", method = RequestMethod.POST)
    public EnrollmentResponseNet enroll(@RequestBody(required = false) EnrollmentRequestNet data,
                                        @RequestHeader("Authorization") String auth) {
        logger.info("/enroll >>>>>> Runnning : EnrollmentRequestNet=" + data + ", auth=" + auth);

        EnrollmentRequest request = buildEnrollmentRequest(data);
        EnrollmentResponseNet response = raService.enroll(request, auth);
        if (logger.isInfoEnabled()) {
            logger.info("/enroll <<<<<< Finished : response=>{}", response);
        }
        return response;
    }

    private EnrollmentRequest buildEnrollmentRequest(EnrollmentRequestNet data) {
        return new EnrollmentRequest(data, System.currentTimeMillis());
    }

    @RequestMapping(value = "/cainfo", method = RequestMethod.POST)
    public GetCAInfoResponseNet cainfo(@RequestBody(required = false) GetCAInfoRequestNet data) {
        logger.info("/cainfo >>>>>> Runnning : GetCAInfoRequestNet=" + data);
        GetCAInfoResponseNet response = raService.getCaInfo(data);
        logger.info("/cainfo <<<<<< Finished");
        return response;
    }

    @RequestMapping(value = "/reenroll", method = RequestMethod.POST)
    public EnrollmentResponseNet reenroll(@RequestBody(required = false) ReenrollmentRequestNet data, @RequestHeader("Authorization") String auth) {
        logger.info("/reenroll >>>>>> Runnning : ReenrollmentRequestNet=" + data + ",auth=" + auth);
        EnrollmentResponseNet response = raService.reenroll(data, auth);
        logger.info("/reenroll <<<<<< Finished");
        return response;
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public RegistrationResponseNet register(@RequestBody(required = false) RegistrationRequestNet data, @RequestHeader("Authorization") String auth) {
        logger.info("/register >>>>>> Runnning : " + data + " ,auth=" + auth);
        RegistrationResponseNet response = raService.register(data, auth);
        logger.info("/register <<<<<< Finished");
        return response;
    }

    @RequestMapping(value = "/revoke", method = RequestMethod.POST)
    public RevokeResponseNet revoke(@RequestBody(required = false) RevokeRequestNet data, @RequestHeader("Authorization") String auth) {
        logger.info("/revoke >>>>>> Runnning : RevokeRequestNet=" + data + ",auth=" + auth);
        RevokeResponseNet response = raService.revoke(data, auth);
        logger.info("/revoke <<<<<< Finished");
        return response;
    }


    @RequestMapping(value = "/heartbeat", method = RequestMethod.GET)
    public HeartBeatResponseNet heartbeat() {
        logger.info("/heartbeat >>>>>> Runnning");
        HeartBeatResponseNet response = raService.heartbeat();
        logger.info("/heartbeat <<<<<< Finished");
        return response;
    }
}
