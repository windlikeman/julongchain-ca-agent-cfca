package com.cfca.ra.client;

import cfca.ra.common.vo.request.CertServiceRequestVO;
import cfca.ra.common.vo.request.QueryRequestVO;
import cfca.ra.common.vo.response.CertServiceResponseVO;
import cfca.ra.common.vo.response.QueryResponseVO;
import cfca.ra.common.vo.response.TxResponseVO;
import cfca.ra.toolkit.RAClient;
import cfca.ra.toolkit.exception.RATKException;
import com.cfca.ra.RAServerException;
import com.cfca.ra.beans.EnrollmentRequestNet;
import com.cfca.ra.beans.ReenrollmentRequestNet;
import com.cfca.ra.beans.RevokeRequestNet;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhangchong
 * @create 2018/5/15
 * @Description 调用RATK接口的客户端实现类
 * @CodeReviewer
 * @since v3.0.0
 */
public class RAClientImpl implements IRAClient {
    private static final Logger logger = LoggerFactory.getLogger(RAClientImpl.class);
    private final String RA_SERVER_URL = "http://192.168.123.177:8084/raWeb/CSHttpServlet";
    private final int CONNECT_TIMEOUT = 5000;
    private final int READ_TIMEOUT = 5000;
    private final RAClient client = new RAClient(RA_SERVER_URL, CONNECT_TIMEOUT, READ_TIMEOUT);

    @Override
    public CertServiceResponseVO enroll(EnrollmentRequestNet enrollmentRequestNet, String enrollmentID) throws RAServerException {
        CertServiceRequestVO certServiceRequestVO = buildCertServiceRequestVO(enrollmentRequestNet, enrollmentID);

        try {
            CertServiceResponseVO certServiceResponseVO = (CertServiceResponseVO) client.process(certServiceRequestVO);
            return certServiceResponseVO;
        } catch (RATKException e) {
            throw new RAServerException(RAServerException.REASON_CODE_ENROLL_SERVICE_RATK_PROCESS, e);
        }
    }

    @Override
    public CertServiceResponseVO reenroll(ReenrollmentRequestNet enrollmentRequestNet, String enrollmentID) throws RAServerException {
        CertServiceRequestVO certServiceRequestVO = buildCertServiceRequestVO(enrollmentRequestNet, enrollmentID);

        try {
            CertServiceResponseVO certServiceResponseVO = (CertServiceResponseVO) client.process(certServiceRequestVO);
            return certServiceResponseVO;
        } catch (RATKException e) {
            throw new RAServerException(RAServerException.REASON_CODE_ENROLL_SERVICE_RATK_PROCESS, e);
        }
    }

    @Override
    public String revoke(RevokeRequestNet data) throws RAServerException {
        try {
            String dn = getDistictName(data);
            if (!StringUtils.isBlank(dn)) {
                CertServiceRequestVO certServiceRequestVO = new CertServiceRequestVO();
                certServiceRequestVO.setTxCode("2901");
                certServiceRequestVO.setDn(dn);
                final TxResponseVO certServiceResponseVO = client.process(certServiceRequestVO);
                logger.info("revoke<<<<<<" + certServiceResponseVO.getResultCode());
                logger.info("revoke<<<<<<" + certServiceResponseVO.getResultMessage());
                return "revoke success";
            } else {
                throw new RAServerException(RAServerException.REASON_CODE_REVOKE_SERVICE_RATK_PROCESS, "the dn of cert want to be revoked not found in server ");
            }
        } catch (RATKException e) {
            throw new RAServerException(RAServerException.REASON_CODE_REVOKE_SERVICE_RATK_PROCESS, e);
        }
    }

    private String getDistictName(RevokeRequestNet data) throws RAServerException {
        try {
            String serialNo = data.getSerial();
            QueryRequestVO queryRequestVO = new QueryRequestVO();
            queryRequestVO.setTxCode("7102");
            queryRequestVO.setSerialNo(serialNo);
            QueryResponseVO queryResponseVO = (QueryResponseVO) client.process(queryRequestVO);

            logger.info(queryResponseVO.getResultCode());
            logger.info(queryResponseVO.getResultMessage());
            String dn = "";
            if (RAClient.SUCCESS.equals(queryResponseVO.getResultCode())) {
                dn = queryResponseVO.getDn();
            }
            return dn;
        } catch (RATKException e) {
            throw new RAServerException(RAServerException.REASON_CODE_REVOKE_SERVICE_RATK_PROCESS, e);
        }
    }

    private CertServiceRequestVO buildCertServiceRequestVO(ReenrollmentRequestNet data, String enrollmentID) throws RAServerException {
        try {
            // 普通证书 普通：1 高级：2
            // 复合证书 单单1-1 单双1-2 双单2-1 双双2-2
            String certType = "1";
            // 个人证书：1 企业证书：2 设备证书：6  场景证书：7  个人生物识别证书：8  企业生物识别证书:9
            String customerType = "1";
            String identType = "Z";
            String identNo = data.getProfile();

            String branchCode = "678";
            String email = "zc@demo.com";

            String caName = data.getCaname();
            String p10 = data.getRequest();

            //密钥算法"SM2"
            String keyAlg = data.getCsrInfo().getKey().getAlgo();
            //密钥长度"256"
            int keyLength = data.getCsrInfo().getKey().getSize();

            CertServiceRequestVO certServiceRequestVO = new CertServiceRequestVO();
            certServiceRequestVO.setTxCode("1101");
            certServiceRequestVO.setCaName(caName);
            certServiceRequestVO.setCertType(certType);
            certServiceRequestVO.setCustomerType(customerType);
            certServiceRequestVO.setUserName(enrollmentID);
            certServiceRequestVO.setIdentType(identType);
            certServiceRequestVO.setIdentNo(identNo);
            certServiceRequestVO.setKeyLength(String.valueOf(keyLength));
            certServiceRequestVO.setKeyAlg(keyAlg);
            certServiceRequestVO.setBranchCode(branchCode);
            certServiceRequestVO.setEmail(email);
            certServiceRequestVO.setP10(p10);
            return certServiceRequestVO;
        } catch (Exception e) {
            throw new RAServerException(RAServerException.REASON_CODE_ENROLL_SERVICE_BUILD_RATK_REQUEST, e);
        }
    }

    private CertServiceRequestVO buildCertServiceRequestVO(EnrollmentRequestNet data, String enrollmentID) throws RAServerException {
        try {
            // 普通证书 普通：1 高级：2
            // 复合证书 单单1-1 单双1-2 双单2-1 双双2-2
            String certType = "1";
            // 个人证书：1 企业证书：2 设备证书：6  场景证书：7  个人生物识别证书：8  企业生物识别证书:9
            String customerType = "1";
            String identType = "Z";
            String branchCode = "678";
            String email = "test@demo.com";

            String identNo = data.getProfile();
            String caName = data.getCaname();
            String p10 = data.getRequest();
            //密钥算法"SM2"
            String keyAlg = data.getCsrInfo().getKey().getAlgo();
            //密钥长度"256"
            int keyLength = data.getCsrInfo().getKey().getSize();

            CertServiceRequestVO certServiceRequestVO = new CertServiceRequestVO();
            certServiceRequestVO.setTxCode("1101");
            certServiceRequestVO.setCaName(caName);
            certServiceRequestVO.setCertType(certType);
            certServiceRequestVO.setCustomerType(customerType);
            certServiceRequestVO.setUserName(enrollmentID);
            certServiceRequestVO.setIdentType(identType);
            certServiceRequestVO.setIdentNo(identNo);
            certServiceRequestVO.setKeyLength(String.valueOf(keyLength));
            certServiceRequestVO.setKeyAlg(keyAlg);
            certServiceRequestVO.setBranchCode(branchCode);
            certServiceRequestVO.setEmail(email);
            certServiceRequestVO.setP10(p10);
            return certServiceRequestVO;
        } catch (Exception e) {
            throw new RAServerException(RAServerException.REASON_CODE_ENROLL_SERVICE_BUILD_RATK_REQUEST, e);
        }
    }
}
