package com.cfca.ra.client;

import cfca.ra.common.vo.response.CertServiceResponseVO;
import com.cfca.ra.RAServerException;
import com.cfca.ra.enroll.EnrollmentRequestNet;
import com.cfca.ra.gettcert.GettCertRequest;
import com.cfca.ra.gettcert.GettCertResponse;
import com.cfca.ra.gettcert.TCertReq;
import com.cfca.ra.reenroll.ReenrollmentRequestNet;
import com.cfca.ra.revoke.RevokeRequestNet;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.util.List;

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
     * @return CertServiceResponseVO
     * @throws RAServerException 遇到异常则返回
     */
    CertServiceResponseVO reenroll(ReenrollmentRequestNet enrollmentRequestNet) throws RAServerException;

    String revoke(RevokeRequestNet data) throws RAServerException;

    GettCertResponse gettcert(GettCertRequest req, List<TCertReq> set, BouncyCastleProvider provider) throws RAServerException;
}
