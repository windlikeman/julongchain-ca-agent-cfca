package org.bica.julongchain.cfca.ra.demo;

import cfca.ra.common.vo.request.TxRequestVO;
import cfca.ra.common.vo.response.TxResponseVO;
import cfca.ra.toolkit.RAClient;
import cfca.ra.toolkit.exception.RATKException;

// 测试
public class Test0000 {
    public static void main(String[] args) {
        try {
            RAClient client = TestConfig.getRAClient();

            TxRequestVO txRequestVO = new TxRequestVO();
            txRequestVO.setTxCode("0000");

            TxResponseVO txResponseVO = (TxResponseVO) client.process(txRequestVO);

            System.out.println(txResponseVO.getResultCode());
            System.out.println(txResponseVO.getResultMessage());
        } catch (RATKException e) {
            e.printStackTrace();
        }
    }
}
