package com.cfca.ra.client;

import cfca.ra.common.vo.response.CertServiceResponseVO;
import com.cfca.ra.ServerRequestContext;
import com.cfca.ra.beans.EnrollmentRequestNet;
import com.cfca.ra.RAServerException;
import com.cfca.ra.beans.ReenrollmentRequestNet;
import com.cfca.ra.beans.RevokeRequestNet;

/**
 * @author zhangchong
 * @create 2018/5/14
 * @Description 调用RATK的接口的客户端
 * @CodeReviewer
 * @since v3.0.0
 */
public interface IRAClient {
    /**
     * 签发证书
     * @param enrollmentRequestNet
     * @param serverRequestContext
     * @return CertServiceResponseVO
     * @throws RAServerException
     */
    CertServiceResponseVO enroll(EnrollmentRequestNet enrollmentRequestNet, ServerRequestContext serverRequestContext) throws RAServerException;

    /**
     * 重新签发证书
     * @param enrollmentRequestNet
     * @param serverRequestContext
     * @return CertServiceResponseVO
     * @throws RAServerException
     */
    CertServiceResponseVO reenroll(ReenrollmentRequestNet enrollmentRequestNet, ServerRequestContext serverRequestContext) throws RAServerException;

    String revoke(RevokeRequestNet data) throws RAServerException;
}
