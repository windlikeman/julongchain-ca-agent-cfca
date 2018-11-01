package org.bica.julongchain.cfca.ra.service;

import org.bica.julongchain.cfca.ra.RAServer;
import org.bica.julongchain.cfca.ra.RAServerException;
import org.bica.julongchain.cfca.ra.heartbeat.HeartBeatResponseNet;
import org.bica.julongchain.cfca.ra.enroll.EnrollmentRequest;
import org.bica.julongchain.cfca.ra.enroll.EnrollmentResponseNet;
import org.bica.julongchain.cfca.ra.getcainfo.GetCAInfoRequestNet;
import org.bica.julongchain.cfca.ra.getcainfo.GetCAInfoResponseNet;
import org.bica.julongchain.cfca.ra.reenroll.ReenrollmentRequestNet;
import org.bica.julongchain.cfca.ra.register.RegistrationRequestNet;
import org.bica.julongchain.cfca.ra.register.RegistrationResponseNet;
import org.bica.julongchain.cfca.ra.revoke.RevokeRequestNet;
import org.bica.julongchain.cfca.ra.revoke.RevokeResponseNet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

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
    private final GetCaInfoService cainfoService;
    private final RevokeService revokeService;

    @Autowired
    public RAServiceImpl(final RAServer raServer) {
        this.raServer = raServer;
        this.registerService = new RegisterService(raServer);
        this.enrollService = new EnrollService(raServer);
        this.cainfoService = new GetCaInfoService(raServer);
        this.reenrollService = new ReenrollService(raServer);
        this.revokeService = new RevokeService(raServer);
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
    public GetCAInfoResponseNet getCaInfo(GetCAInfoRequestNet data) {
        return cainfoService.getCaInfo(data.getCaname());
    }

    public enum Status{
        Ok("ok"),
        Unexpected("An unexpected error"),;

        private final String name;
        Status(String s) {
            this.name =s;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    @Override
    public HeartBeatResponseNet heartbeat() {
        return new HeartBeatResponseNet(new Date(), Status.Ok);
    }
}
