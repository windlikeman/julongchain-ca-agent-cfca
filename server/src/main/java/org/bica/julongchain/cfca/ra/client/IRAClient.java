package org.bica.julongchain.cfca.ra.client;

import cfca.ra.common.vo.response.CertServiceResponseVO;
import org.bica.julongchain.cfca.ra.RAServerException;
import org.bica.julongchain.cfca.ra.enroll.EnrollmentRequestNet;
import org.bica.julongchain.cfca.ra.reenroll.ReenrollmentRequestNet;
import org.bica.julongchain.cfca.ra.revoke.RevokeRequestNet;
import org.bica.julongchain.cfca.ra.revoke.RevokeResult;

/**
 * @author zhangchong
 * @create 2018/5/14
 * @Description 调用RATK的接口的客户端
 * @CodeReviewer helonglong
 * @since v3.0.0
 */
public interface IRAClient {
    /**
     * 签发证书
     * @param enrollmentRequestNet 网络请求
     * @param enrollmentID 身份ID
     * @return CertServiceResponseVO
     * @throws RAServerException 遇到异常则返回
     */
    CertServiceResponseVO enroll(EnrollmentRequestNet enrollmentRequestNet, String enrollmentID) throws RAServerException;

    /**
     * 重新签发证书
     * @param enrollmentRequestNet 网络请求
     * @param uniqueUserName
     * @return CertServiceResponseVO
     * @throws RAServerException 遇到异常则返回
     */
    CertServiceResponseVO reenroll(ReenrollmentRequestNet enrollmentRequestNet, String uniqueUserName) throws RAServerException;

    /**
     * 吊销证书
     * @param data
     * @return
     * @throws RAServerException
     */
    RevokeResult revoke(RevokeRequestNet data) throws RAServerException;
}
