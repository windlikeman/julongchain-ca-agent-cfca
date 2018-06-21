package com.cfca.ra.demo;

import cfca.ra.common.vo.request.CertServiceRequestVO;
import cfca.ra.common.vo.response.CertServiceResponseVO;
import cfca.ra.toolkit.RAClient;
import cfca.ra.toolkit.exception.RATKException;

// 证书申请并下载
public class Test1101 {
    public static void main(String[] args) {
        // String locale = "zh_CN";
        // CA名称
        String caName = "OCA1";
        // 普通证书 普通：1 高级：2
        // 复合证书 单单1-1 单双1-2 双单2-1 双双2-2
        String certType = "1";
        // 个人证书：1 企业证书：2 设备证书：6 场景证书：7 个人生物识别证书：8 企业生物识别证书:9
        String customerType = "1";
        String userName = "aaaaa";
        // String userNameInDn = "testName";
        // String userIdent = "Z1234567890";
        String identType = "Z";
        String identNo = "H09358028";
        String keyAlg = "SM2";
        String keyLength = "256";
        String branchCode = "678";
        String email = "test@demo.com";
        // String phoneNo = "12345678";
        // String address = "address";
        // String duration = "24";
        String addIdentNoExt = "true";
        String selfExtValue = "extValue";
        String p10 = "MIIBLzCB1gIBADB0MSQwIgYDVQQDDBswNTFAdGVzdE5hbWVAWjEyMzQ1Njc4OTBANTMxFTATBgNVBAsMDEluZGl2aWR1YWwtMzERMA8GA1UECwwITG9jYWwgUkExFTATBgNVBAoMDENGQ0EgVEVTVCBDQTELMAkGA1UEBhMCQ04wWTATBgcqhkjOPQIBBggqgRzPVQGCLQNCAASUFmdyrhjvzx8o8Dm4KZ49h9cNHfBJuZKb9h7XwEcow5mzDvPMRIS0g2WPXaQwFxFDGsTjevQpuED7OEgaAMk/oAAwCgYIKoEcz1UBg3UDSAAwRQIgUX2BV55Qw50LMHewEQdq57QJ6zm4NEgxZ+QUQ8kKqHgCIQDN3Rvw7ifgSikfWwgrlb4CEuDa7cTeCwDWuiuLvgNGvQ==";
        // String p10Sub =
        // "MIIBtDCCAVgCAQAwPjELMAkGA1UEBhMCQ04xFTATBgNVBAoMDENGQ0EgVEVTVCBDQTEYMBYGA1UEAwwPY2VydFJlcXVpc2l0aW9uMFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAEsD7nwqQKQWe1ghN+Zy4FbByQPxg3Y/aHMqiz4rDz0liG8Fq5250T5mKYh1leWulAZps2wPaLFIUMqZ7+eT1qyqCBtzATBgkqhkiG9w0BCQcTBjExMTExMTCBnwYJKoZIhvcNAQk/BIGRMIGOAgEBBIGIALQAAAABAACiwcr2l/0TmUagstfNwWb8O4cJdD1Wac1Yy9gkf8rttAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAGXhheoBu6FadqKQNDP7+GOw7xXD4FNQA3NYnu55mBIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADAMBggqgRzPVQGDdQUAA0gAMEUCIEpASo/MKXIIETdxurjmuracNklJbHt+n6Olus12HFHlAiEAwAX1CfamNX0DhMysjkwan9ASmZsdinEQEs59kJJGSuQ=";
        // // 设备标识
        // String deviceIdentifier = "192.168.117.101";
        // // 部门名称
        // String departmentNameInCert = "部门名称";
        // // 组织机构名称
        // String organizationNameInCert = "组织机构名称";
        // // 营业地点所在地市
        // String locality = "北京";
        // // 营业地点所在省
        // String stateOrProvince = "北京";
        // // 国家,不填默认为CN
        // String country = "CN";
        // 开始时间(不能早于当前时间一定范围，该值可在sys.ini中配置)
        String startTime = "20180526154323";
        String endTime = "20180531235959"; // endTime与duration同时非空时，证书截止时间以endTime为准，duration作为证书默认有效期记入数据库
        try {
            RAClient client = TestConfig.getRAClient();

            CertServiceRequestVO certServiceRequestVO = new CertServiceRequestVO();
            certServiceRequestVO.setTxCode("1101");
            // certServiceRequestVO.setLocale(locale);
            certServiceRequestVO.setCaName(caName);
            certServiceRequestVO.setCertType(certType);
            certServiceRequestVO.setCustomerType(customerType);
            certServiceRequestVO.setUserName(userName);
            // certServiceRequestVO.setUserNameInDn(userNameInDn);
            // certServiceRequestVO.setUserIdent(userIdent);
            certServiceRequestVO.setIdentType(identType);
            certServiceRequestVO.setIdentNo(identNo);
            certServiceRequestVO.setKeyLength(keyLength);
            certServiceRequestVO.setKeyAlg(keyAlg);
            certServiceRequestVO.setBranchCode(branchCode);
            certServiceRequestVO.setEmail(email);
            // certServiceRequestVO.setPhoneNo(phoneNo);
            // certServiceRequestVO.setAddress(address);
            // certServiceRequestVO.setDuration(duration);

            certServiceRequestVO.setStartTime(startTime);
            certServiceRequestVO.setEndTime(endTime);
            certServiceRequestVO.setAddIdentNoExt(addIdentNoExt);
            certServiceRequestVO.setSelfExtValue(selfExtValue);
            certServiceRequestVO.setP10(p10);
            // certServiceRequestVO.setP10Sub(p10Sub);
            // certServiceRequestVO.setDeviceIdentifier(deviceIdentifier);
            // certServiceRequestVO.setDepartmentNameInCert(departmentNameInCert);
            // certServiceRequestVO.setOrganizationNameInCert(organizationNameInCert);
            // certServiceRequestVO.setLocality(locality);
            // certServiceRequestVO.setStateOrProvince(stateOrProvince);
            // certServiceRequestVO.setCountry(country);

            CertServiceResponseVO certServiceResponseVO = (CertServiceResponseVO) client.process(certServiceRequestVO);

            System.out.println(certServiceResponseVO.getResultCode());
            System.out.println(certServiceResponseVO.getResultMessage());
            if (RAClient.SUCCESS.equals(certServiceResponseVO.getResultCode())) {
                // System.out.println(certServiceResponseVO.getDn());
                // System.out.println(certServiceResponseVO.getSequenceNo());
                // System.out.println(certServiceResponseVO.getSerialNo());
                // System.out.println(certServiceResponseVO.getStartTime());
                // System.out.println(certServiceResponseVO.getEndTime());
                // System.out.println(certServiceResponseVO.getSignatureCert());
                // System.out.println(certServiceResponseVO.getEncryptionCert());
                // System.out.println(certServiceResponseVO.getEncryptionPrivateKey());
                // System.out.println(certServiceResponseVO.getSignatureCertSub());
                // System.out.println(certServiceResponseVO.getEncryptionCertSub());
                // System.out.println(certServiceResponseVO.getEncryptionPrivateKeySub());
                System.out.println(certServiceResponseVO);
            }
        } catch (RATKException e) {
            e.printStackTrace();
        }
    }
}
