package com.cfca.ra.controller;

import com.cfca.ra.beans.*;
import com.cfca.ra.service.RAServiceImpl;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.*;

import java.security.Security;

/**
 * @author zhangchong
 * @create 2018/5/14
 * @Description 实现类
 * @CodeReviewer
 * @since v3.0.0
 */
@RestController
@Configuration
public class RAController {
    private static final Logger logger = LoggerFactory.getLogger(RAController.class);

    static {
        BouncyCastleProvider provider = new BouncyCastleProvider();
        Security.addProvider(provider);
    }

    /**
     * 因为volatile关键字无法保证操作的原子性.通常来说,使用volatile必须具备以下2个条件
     * 1.对变量的写操作不依赖于当前值
     * 2.该变量没有包含在具有其他变量的不变式中
     */
    private volatile RAServiceImpl raService;

    @Autowired
    public RAController(RAServiceImpl raService) {
        this.raService = raService;
    }

    @RequestMapping(value = "/enroll", method = RequestMethod.POST)
    public EnrollmentResponseNet enroll(@RequestBody(required = false) EnrollmentRequestNet data, @RequestHeader("Authorization") String auth) {
        logger.info("/enroll >>>>>> Runnning : EnrollmentRequestNet=" + data + ", auth=" + auth);
        EnrollmentResponseNet response = raService.enroll(data, auth);
        logger.info("/enroll <<<<<< Finished");
        return response;
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
    public GettcertResponseNet tcert(@RequestBody(required = false) GettcertRequestNet data, @RequestHeader("Authorization") String auth) {
        logger.info("/tcert >>>>>> Runnning : GettcertRequestNet=" + data);
        GettcertResponseNet response = raService.gettcert(data, auth);
        logger.info("/tcert <<<<<< Finished");
        return response;
    }
}
