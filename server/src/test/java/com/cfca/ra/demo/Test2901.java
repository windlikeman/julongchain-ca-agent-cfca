package com.cfca.ra.demo;

import cfca.ra.common.vo.request.CertServiceRequestVO;
import cfca.ra.common.vo.response.CertServiceResponseVO;
import cfca.ra.toolkit.RAClient;
import cfca.ra.toolkit.exception.RATKException;
import com.cfca.ra.demo.TestConfig;

// 证书吊销
public class Test2901 {
    public static void main(String[] args) {
        // String locale = "zh_CN";
        String dn = "CN=051@admin@ZH09358028@1,OU=Individual-1,OU=Local RA,O=CFCA TEST OCA1,C=CN";
        try {
            RAClient client = TestConfig.getRAClient();

            CertServiceRequestVO certServiceRequestVO = new CertServiceRequestVO();
            certServiceRequestVO.setTxCode("2901");
            // certServiceRequestVO.setLocale(locale);
            certServiceRequestVO.setDn(dn);

            CertServiceResponseVO certServiceResponseVO = (CertServiceResponseVO) client.process(certServiceRequestVO);

            System.out.println(certServiceResponseVO.getResultCode());
            System.out.println(certServiceResponseVO.getResultMessage());
        } catch (RATKException e) {
            e.printStackTrace();
        }
    }
}
