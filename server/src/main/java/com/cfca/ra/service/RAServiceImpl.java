package com.cfca.ra.service;

import com.cfca.ra.RAServer;
import com.cfca.ra.RAServerException;
import com.cfca.ra.beans.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author zhangchong
 * @create 2018/5/14
 * @Description 实现类
 * @CodeReviewer
 * @since v3.0.0
 */
@Service
public class RAServiceImpl implements IRAService {
    private static final Logger logger = LoggerFactory.getLogger(RAServiceImpl.class);


    private final RAServer raServer;
    private final RegisterService registerService;
    private final EnrollService enrollService;
    private final ReenrollService reenrollService;
    private final GetCaInfoService getCaInfoService;
    private final RevokeService revokeService;
    private final GettCertService gettCertService;

    @Autowired
    public RAServiceImpl(RAServer raServer) {
        this.raServer = raServer;
        this.registerService = new RegisterService(raServer);
        this.enrollService = new EnrollService(raServer);
        this.getCaInfoService = new GetCaInfoService(raServer);
        this.reenrollService = new ReenrollService(raServer);
        this.revokeService = new RevokeService(raServer);
        this.gettCertService = new GettCertService(raServer);
    }

    @Override
    public void initialize() throws RAServerException {
        raServer.initialize();
    }

    @Override
    public EnrollmentResponseNet enroll(EnrollmentRequestNet data, String auth) {
        return enrollService.enroll(data, auth);
    }

    @Override
    public EnrollmentResponseNet reenroll(ReenrollmentRequestNet data, String auth) {
        return reenrollService.reenroll(data, auth);
    }

    @Override
    public RegistrationResponseNet register(RegistrationRequestNet data, String auth) {
        return registerService.registerUser(data, auth);
    }

    @Override
    public RevokeResponseNet revoke(RevokeRequestNet data, String auth) {
        return revokeService.revoke(data, auth);
    }

    @Override
    public GettCertResponseNet gettcert(GettCertRequestNet data, String auth) {
        return gettCertService.gettcert(data, auth);
    }

    @Override
    public GetCAInfoResponseNet getCaInfo(GetCAInfoRequestNet data) {
        return getCaInfoService.getCaInfo(data.getCaname());
    }

}
