package com.cfca.ra.service;

import com.cfca.ra.RAServer;
import com.cfca.ra.RAServerException;
import com.cfca.ra.enroll.EnrollmentRequest;
import com.cfca.ra.enroll.EnrollmentResponseNet;
import com.cfca.ra.getcainfo.GetCAInfoRequestNet;
import com.cfca.ra.getcainfo.GetCAInfoResponseNet;
import com.cfca.ra.gettcert.GettCertRequestNet;
import com.cfca.ra.gettcert.GettCertResponseNet;
import com.cfca.ra.reenroll.ReenrollmentRequest;
import com.cfca.ra.register.RegistrationRequest;
import com.cfca.ra.register.RegistrationResponseNet;
import com.cfca.ra.revoke.RevokeRequest;
import com.cfca.ra.revoke.RevokeResponseNet;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
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
    public EnrollmentResponseNet enroll(EnrollmentRequest data, String auth) {
        return enrollService.enroll(data, auth);
    }

    @Override
    public EnrollmentResponseNet reenroll(ReenrollmentRequest data, String auth) {
        return reenrollService.reenroll(data, auth);
    }

    @Override
    public RegistrationResponseNet register(RegistrationRequest data, String auth) {
        return registerService.registerUser(data, auth);
    }

    @Override
    public RevokeResponseNet revoke(RevokeRequest data, String auth) {
        return revokeService.revoke(data, auth);
    }

    @Override
    public GettCertResponseNet gettcert(GettCertRequestNet data, String auth, BouncyCastleProvider provider) {
        return gettCertService.gettcert(data, auth, provider);
    }

    @Override
    public GetCAInfoResponseNet getCaInfo(GetCAInfoRequestNet data) {
        return getCaInfoService.getCaInfo(data.getCaname());
    }

}
