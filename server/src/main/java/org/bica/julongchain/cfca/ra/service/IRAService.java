package org.bica.julongchain.cfca.ra.service;

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
import org.springframework.stereotype.Service;

/**
 * @author zhangchong
 * @create 2018/5/14
 * @Description CMBC接口服务
 * @CodeReviewer
 * @since v3.0.0
 */
@Service
public interface IRAService {
    /**
     * 接收用户信息，进行用户登记，签发证书接口
     *
     * @param data
     * @param auth
     * @return EnrollmentResponseNet
     */
    EnrollmentResponseNet enroll(EnrollmentRequest data, String auth);

    /**
     * 用户重新登记和签发证书接口
     *
     * @param data
     * @param auth
     * @return
     */
    EnrollmentResponseNet reenroll(ReenrollmentRequestNet data, String auth);


    /**
     * 接收用户信息，进行新用户注册接口
     *
     * @param data
     * @param auth
     * @return RegistrationResponseNet
     */
    RegistrationResponseNet register(RegistrationRequestNet data, String auth) throws RAServerException;

    /**
     * 吊销证书接口
     *
     * @param data
     * @return RevokeResponseNet
     */
    RevokeResponseNet revoke(RevokeRequestNet data, String auth);

    /**
     * 初始化:包括初始化 CA 配置列表等
     */
    void initialize() throws RAServerException;

    /**
     * 获取指定CA信息:证书链和名字
     *
     * @param data
     * @return GetCAInfoResponseNet
     */
    GetCAInfoResponseNet getCaInfo(GetCAInfoRequestNet data) throws RAServerException;

    /**
     * 心跳检测
     * @return
     */
    HeartBeatResponseNet heartbeat();

}
