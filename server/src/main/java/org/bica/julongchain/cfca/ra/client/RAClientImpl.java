package org.bica.julongchain.cfca.ra.client;

import cfca.ra.common.vo.request.CertServiceRequestVO;
import cfca.ra.common.vo.request.QueryRequestVO;
import cfca.ra.common.vo.response.CertServiceResponseVO;
import cfca.ra.common.vo.response.QueryResponseVO;
import cfca.ra.common.vo.response.TxResponseVO;
import cfca.ra.toolkit.RAClient;
import org.bica.julongchain.cfca.ra.RAServerException;
import org.bica.julongchain.cfca.ra.enroll.EnrollmentRequestNet;
import org.bica.julongchain.cfca.ra.reenroll.ReenrollmentRequestNet;
import org.bica.julongchain.cfca.ra.revoke.RevokeRequestNet;
import org.bica.julongchain.cfca.ra.revoke.RevokeResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhangchong
 * @create 2018/5/15
 * @Description 调用RATK接口的客户端实现类
 * @CodeReviewer helonglong
 * @since v3.0.0
 */
public class RAClientImpl implements IRAClient {
    private static final Logger logger = LoggerFactory.getLogger(RAClientImpl.class);
    public static final String RA_CODE_NOT_FOUND_CERT_BY_SERIAL_NO = "3110";

    @Override
    public CertServiceResponseVO enroll(EnrollmentRequestNet enrollmentRequestNet, String enrollmentID) throws
            RAServerException {
        logger.info("enroll running: enrollmentID={}", enrollmentID);
        final long strTime = System.currentTimeMillis();
        CertServiceRequestVO certServiceRequestVO = buildCertServiceRequestVO(enrollmentRequestNet, enrollmentID);

        try {
            RAClient client = RAClientUtil.getInstance().getClient();
            CertServiceResponseVO certServiceResponseVO = (CertServiceResponseVO) client.process(certServiceRequestVO);

            if (RAClient.SUCCESS.equals(certServiceResponseVO.getResultCode())) {
                logger.info("enroll finished: ResultCode={}, ResultMessage={}", certServiceResponseVO.getResultCode()
                        , certServiceResponseVO.getResultMessage());
            } else {
                logger.warn("enroll finished: ResultCode={}, ResultMessage={}", certServiceResponseVO.getResultCode()
                        , certServiceResponseVO.getResultMessage());
            }

            return certServiceResponseVO;
        } catch (Exception e) {
            throw new RAServerException("enrollment service failed to process ratk request", e);
        } finally {
            final long runTime = System.currentTimeMillis() - strTime;
            if (runTime > RAClientUtil.warningTime) {
                logger.warn("RATK enroll runTime={}, enrollmentID={}, enrollmentRequestNet={}", runTime,
                        enrollmentID, enrollmentRequestNet);
            }
        }
    }

    @Override
    public CertServiceResponseVO reenroll(ReenrollmentRequestNet enrollmentRequestNet, String uniqueUserName) throws
            RAServerException {
        logger.info("reenroll running: ");
        final long strTime = System.currentTimeMillis();
        CertServiceRequestVO certServiceRequestVO = buildCertServiceRequestVO(enrollmentRequestNet, uniqueUserName);
        try {

            RAClient client = RAClientUtil.getInstance().getClient();
            CertServiceResponseVO certServiceResponseVO = (CertServiceResponseVO) client.process(certServiceRequestVO);
            if (RAClient.SUCCESS.equals(certServiceResponseVO.getResultCode())) {
                logger.info("reenroll finished: ResultCode={}, ResultMessage={}", certServiceResponseVO.getResultCode(),
                        certServiceResponseVO.getResultMessage());
            } else {
                logger.warn("reenroll finished: ResultCode={}, ResultMessage={}", certServiceResponseVO.getResultCode(),
                        certServiceResponseVO.getResultMessage());
            }
            return certServiceResponseVO;
        } catch (Exception e) {
            throw new RAServerException("enrollment service failed to process ratk request", e);
        } finally {
            final long runTime = System.currentTimeMillis() - strTime;
            if (runTime > RAClientUtil.warningTime) {
                logger.warn("RATK reenroll runTime={}, enrollmentRequestNet={}", runTime, enrollmentRequestNet);
            }
        }
    }

    @Override
    public RevokeResult revoke(RevokeRequestNet data) throws RAServerException {
        logger.info("reenroll running: ");
        final long strTime = System.currentTimeMillis();
        final QueryResult queryResult = queryDistictName(data);

        try {
            if (queryResult.isOk()) {
                CertServiceRequestVO certServiceRequestVO = new CertServiceRequestVO();
                certServiceRequestVO.setTxCode("2901");
                certServiceRequestVO.setDn(queryResult.getDistinctName());

                RAClient client = RAClientUtil.getInstance().getClient();
                final TxResponseVO certServiceResponseVO = client.process(certServiceRequestVO);

                if (RAClient.SUCCESS.equals(certServiceResponseVO.getResultCode())) {
                    logger.info("revoke finished: ResultCode={}, ResultMessage={}",
                            certServiceResponseVO.getResultCode(),
                            certServiceResponseVO.getResultMessage());
                } else {
                    logger.error("revoke finished: ResultCode={}, ResultMessage={}",
                            certServiceResponseVO.getResultCode(),
                            certServiceResponseVO.getResultMessage());
                    throw new RAServerException("the dn of cert want to be revoked not found in server ");
                }
                return new RevokeResult(true, "revoke success", data.getSerial());
            } else if (queryResult.resultCode.equalsIgnoreCase(RA_CODE_NOT_FOUND_CERT_BY_SERIAL_NO)) {
                logger.warn("RATK revoke a non-exist cert={}", data);
                return new RevokeResult(true, "证书不存在或已吊销,请查看序列号是否正确", data.getSerial());
            } else {
                logger.warn("RATK revoke failed due to cert not found RevokeRequestNet={}", data);
                return new RevokeResult(false, queryResult.getResultMessage(), data.getSerial());
            }
        } catch (Exception e) {
            throw new RAServerException("revoke service failed to process ratk request", e);
        } finally {
            final long runTime = System.currentTimeMillis() - strTime;
            if (runTime > RAClientUtil.warningTime) {
                logger.warn("RATK revoke runTime={}, RevokeRequestNet={}", runTime, data);
            }
        }
    }


    public static class QueryResult {
        private final boolean ok;
        private final String distinctName;
        private final String resultMessage;
        private final String resultCode;

        public QueryResult(boolean ok, String distinctName, String resultMessage, String resultCode) {
            this.ok = ok;
            this.distinctName = distinctName;
            this.resultMessage = resultMessage;
            this.resultCode = resultCode;
        }

        public boolean isOk() {
            return ok;
        }

        public String getDistinctName() {
            return distinctName;
        }

        public String getResultMessage() {
            return resultMessage;
        }

        public String getResultCode() {
            return resultCode;
        }
    }

    private final QueryResult queryDistictName(RevokeRequestNet data) throws RAServerException {
        final long strTime = System.currentTimeMillis();
        String resultCode = "unkown";
        try {
            String serialNo = data.getSerial();
            QueryRequestVO queryRequestVO = new QueryRequestVO();
            queryRequestVO.setTxCode("7102");
            queryRequestVO.setSerialNo(serialNo);

            RAClient client = RAClientUtil.getInstance().getClient();
            QueryResponseVO queryResponseVO = (QueryResponseVO) client.process(queryRequestVO);

            resultCode = queryResponseVO.getResultCode();

            final String resultMessage = queryResponseVO.getResultMessage();
            logger.info("resultCode={}: {}", resultCode, resultMessage);

            QueryResult dn;
            if (RAClient.SUCCESS.equals(resultCode)) {
                dn = new QueryResult(true, queryResponseVO.getDn(), "success", resultCode);
            } else {
                logger.warn("RATK queryDistictName not exists, resultCode={},RevokeRequestNet={}", resultCode, data);
                dn = new QueryResult(false, queryResponseVO.getDn(), resultMessage, resultCode);

            }
            return dn;
        } catch (Exception e) {
            logger.warn("RATK queryDistictName failed: resultCode={}, RevokeRequestNet={}", resultCode, data);
            throw new RAServerException("revoke service failed to process ratk request", e);
        } finally {
            final long runTime = System.currentTimeMillis() - strTime;

            if (runTime > RAClientUtil.warningTime) {
                logger.warn("RATK queryDistictName runTime={}, resultCode={}, RevokeRequestNet={}",
                        runTime, resultCode, data);
            }
        }
    }

    private final CertServiceRequestVO buildCertServiceRequestVO(ReenrollmentRequestNet data, String uniqueUserName)
            throws RAServerException {
        String enrollmentID = uniqueUserName;
        String identNo = data.getProfile();
        String caname = data.getCaname();
        String p10Request = data.getRequest();

        return buildCertServiceRequestVO(p10Request, identNo, caname, enrollmentID);
    }

    private final CertServiceRequestVO buildCertServiceRequestVO(EnrollmentRequestNet data, String enrollmentID)
            throws RAServerException {

        String profile = data.getProfile();
        String caname = data.getCaname();
        String p10Request = data.getRequest();

        return buildCertServiceRequestVO(p10Request, profile, caname, enrollmentID);
    }

    private final CertServiceRequestVO buildCertServiceRequestVO(final String p10Request, final String profile,
                                                                 final String caname, String enrollmentID)
            throws RAServerException {
        try {
            // 普通证书 普通：1 高级：2
            // 复合证书 单单1-1 单双1-2 双单2-1 双双2-2
            String certType = "1";
            // 个人证书：1 企业证书：2 设备证书：6 场景证书：7 个人生物识别证书：8 企业生物识别证书:9
            String customerType = "1";
            String identType = "Z";
            String identNo = "H09358028";
            String branchCode = "678";
            // String email = "test@demo.com";
            // 密钥算法"SM2"
            String keyAlg = "SM2";
            // 密钥长度"256"
            int keyLength = 256;

            CertServiceRequestVO certServiceRequestVO = new CertServiceRequestVO();
            certServiceRequestVO.setTxCode("1101");
            certServiceRequestVO.setCaName(caname);
            certServiceRequestVO.setCertType(certType);
            certServiceRequestVO.setCustomerType(customerType);
            // certServiceRequestVO.setEmail(email);
            certServiceRequestVO.setUserName(enrollmentID);
            logger.info("UserName={}", enrollmentID);
            certServiceRequestVO.setIdentType(identType);
            certServiceRequestVO.setIdentNo(identNo);
            certServiceRequestVO.setKeyLength(String.valueOf(keyLength));
            certServiceRequestVO.setKeyAlg(keyAlg);
            certServiceRequestVO.setBranchCode(branchCode);
            certServiceRequestVO.setP10(p10Request);
            return certServiceRequestVO;
        } catch (Exception e) {
            logger.error("buildCertServiceRequestVO failed", e);
            throw new RAServerException("enrollment service failed to build ratk request", e);
        }
    }
}
