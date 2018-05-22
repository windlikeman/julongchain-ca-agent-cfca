package com.cfca.ra.service;

import com.cfca.ra.RAServerException;
import com.cfca.ra.beans.*;
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
    EnrollmentResponseNet enroll(EnrollmentRequestNet data, String auth);

    /**
     * 用户重新登记和签发证书接口
     *
     * @param data
     * @param auth
     * @return
     */
    EnrollmentResponseNet reenroll(ReenrollmentRequestNet data,String auth);


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
     * 批量查询证书接口
     *
     * @param data
     * @param auth
     * @return GettcertResponseNet
     */
    GettcertResponseNet gettcert(GettcertRequestNet data, String auth);

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
}
