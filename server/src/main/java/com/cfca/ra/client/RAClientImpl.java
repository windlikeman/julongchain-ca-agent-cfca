package com.cfca.ra.client;

import cfca.ra.common.vo.request.CertServiceRequestVO;
import cfca.ra.common.vo.request.QueryRequestVO;
import cfca.ra.common.vo.response.CertServiceResponseVO;
import cfca.ra.common.vo.response.QueryResponseVO;
import cfca.ra.common.vo.response.TxResponseVO;
import cfca.ra.toolkit.RAClient;
import cfca.ra.toolkit.exception.RATKException;
import com.cfca.ra.RAServerException;
import com.cfca.ra.enroll.EnrollmentRequestNet;
import com.cfca.ra.gettcert.GettCertRequest;
import com.cfca.ra.gettcert.GettCertResponse;
import com.cfca.ra.gettcert.TCert;
import com.cfca.ra.gettcert.TCertReq;
import com.cfca.ra.reenroll.ReenrollmentRequestNet;
import com.cfca.ra.revoke.RevokeRequestNet;
import org.apache.commons.lang.StringUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    public GettCertResponse gettcert(GettCertRequest req, List<TCertReq> set, BouncyCastleProvider provider) throws RAServerException {
        if (set == null || set.isEmpty()) {
            throw new RAServerException(RAServerException.REASON_CODE_GETTCERT_SERVICE_RATK_PROCESS, "invalid request,request is empty");
        }
        try {
            for (TCertReq tCertReq : set) {
                CertServiceRequestVO certServiceRequestVO = buildCertServiceRequestVO(req, tCertReq, provider);
                final CertServiceResponseVO certServiceResponseVO = (CertServiceResponseVO) client.process(certServiceRequestVO);
                buildTcertResponse(certServiceResponseVO);
            }
        } catch (RATKException e) {
            throw new RAServerException(RAServerException.REASON_CODE_GETTCERT_SERVICE_RATK_PROCESS, e);
        }
        return null;
    }

    private GettCertResponse buildTcertResponse(CertServiceResponseVO certServiceResponseVO) {
        GettCertResponse tcertResponse = new GettCertResponse();
        final String serialNo = certServiceResponseVO.getSerialNo();
        long serial = Long.parseLong(serialNo, 16);
        tcertResponse.setId(serial);
        tcertResponse.setTs(System.currentTimeMillis());
        byte[] kdfKey = new byte[]{};
        tcertResponse.setKey(kdfKey);
        List<TCert> set = new ArrayList<>();
        tcertResponse.settCerts(set);
        return tcertResponse;
    }

    private CertServiceRequestVO buildCertServiceRequestVO(GettCertRequest req, TCertReq tCertReq, BouncyCastleProvider provider) {
        CertServiceRequestVO certServiceRequestVO = new CertServiceRequestVO();
        certServiceRequestVO.setTxCode("1101");
        // certServiceRequestVO.setLocale(locale);
        certServiceRequestVO.setCaName(req.getCaname());
        certServiceRequestVO.setCertType("1");
        certServiceRequestVO.setCustomerType("1");
        certServiceRequestVO.setUserName("admin");
        certServiceRequestVO.setIdentType("Z");
        certServiceRequestVO.setIdentNo("H09358028");
        certServiceRequestVO.setKeyLength("256");
        certServiceRequestVO.setKeyAlg("SM2");
        certServiceRequestVO.setBranchCode("678");

        //20180531235959
        final long start = tCertReq.getNotBefore().getTime();
//        certServiceRequestVO.setStartTime(start);
//        certServiceRequestVO.setEndTime(endTime);
//        certServiceRequestVO.setAddIdentNoExt(addIdentNoExt);
//        certServiceRequestVO.setSelfExtValue(selfExtValue);
//        certServiceRequestVO.setP10(p10);

        return null;
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
//            String email = "zc@demo.com";
            String keyAlg = "SM2";
            int keyLength = 256;

            String caName = data.getCaname();
            String p10 = data.getRequest();

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
//            certServiceRequestVO.setEmail(email);
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
//            String email = "test@demo.com";
            //密钥算法"SM2"
            String keyAlg = "SM2";
            //密钥长度"256"
            int keyLength = 256;

            String identNo = data.getProfile();
            String caName = data.getCaname();
            String p10 = data.getRequest();

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
//            certServiceRequestVO.setEmail(email);
            certServiceRequestVO.setP10(p10);
            return certServiceRequestVO;
        } catch (Exception e) {
            throw new RAServerException(RAServerException.REASON_CODE_ENROLL_SERVICE_BUILD_RATK_REQUEST, e);
        }
    }
}
