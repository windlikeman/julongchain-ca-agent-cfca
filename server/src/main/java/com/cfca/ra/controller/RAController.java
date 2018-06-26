package com.cfca.ra.controller;

import com.cfca.ra.enroll.EnrollmentRequest;
import com.cfca.ra.enroll.EnrollmentRequestNet;
import com.cfca.ra.enroll.EnrollmentResponseNet;
import com.cfca.ra.getcainfo.GetCAInfoRequestNet;
import com.cfca.ra.getcainfo.GetCAInfoResponseNet;
import com.cfca.ra.gettcert.GettCertRequestNet;
import com.cfca.ra.gettcert.GettCertResponseNet;
import com.cfca.ra.reenroll.ReenrollmentRequestNet;
import com.cfca.ra.register.RegistrationRequestNet;
import com.cfca.ra.register.RegistrationResponseNet;
import com.cfca.ra.revoke.RevokeRequestNet;
import com.cfca.ra.revoke.RevokeResponseNet;
import com.cfca.ra.service.RAServiceImpl;
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

import java.security.Security;

/**
 * @author zhangchong
 * @create 2018/5/14
 * @Description Restful 接口控制类,目前定义了 enroll reenroll register gettcert getcainfo revoke等接口的处理
 * @CodeReviewer helonglong
 * @since v3.0.0
 */
@RestController
@Configuration
public class RAController {
    private static final Logger logger = LoggerFactory.getLogger(RAController.class);

    private final BouncyCastleProvider provider;

    /**
     * 因为volatile关键字无法保证操作的原子性.通常来说,使用volatile必须具备以下2个条件
     * 1.对变量的写操作不依赖于当前值
     * 2.该变量没有包含在具有其他变量的不变式中
     */
    private volatile RAServiceImpl raService;

    @Autowired
    public RAController(RAServiceImpl raService) {
        this.raService = raService;
        this.provider = new BouncyCastleProvider();
        Security.addProvider(provider);
    }

    @RequestMapping(value = "/enroll", method = RequestMethod.POST)
    public EnrollmentResponseNet enroll(@RequestBody(required = false) EnrollmentRequestNet data, @RequestHeader("Authorization") String auth) {
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

    @RequestMapping(value = "/tcert", method = RequestMethod.POST)
    public GettCertResponseNet tcert(@RequestBody(required = false) GettCertRequestNet data, @RequestHeader("Authorization") String auth) {
        logger.info("/tcert >>>>>> Runnning : GettCertRequestNet=" + data);
        GettCertResponseNet response = raService.gettcert(data, auth, provider);
        logger.info("/tcert <<<<<< Finished");
        return response;
    }
}
