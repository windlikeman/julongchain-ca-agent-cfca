package com.cfca.ra.utils;

import com.cfca.ra.RAServerException;
import com.cfca.ra.service.ReenrollService;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.util.ASN1Dump;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.util.encoders.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhangchong
 * @create 2018/5/28
 * @Description 证书工具类, 用于提取证书相关信息
 * @CodeReviewer
 * @since
 */
public class CertUtils {
    private static final Logger logger = LoggerFactory.getLogger(CertUtils.class);

    public static String getSubjectName(String b64cert) throws RAServerException {
        try {
            final ASN1Primitive asn1Primitive = ASN1Primitive.fromByteArray(Base64.decode(b64cert));
            final Certificate instance = Certificate.getInstance(asn1Primitive);
            final String s = instance.getSubject().toString();
            logger.info("getSubjectName>>>>>>" + s);
            return s;
        }catch (Exception e){
            throw new RAServerException(RAServerException.REASON_CODE_CERTUTILS_GET_SUBJECT_NAME, e);
        }
    }
}
