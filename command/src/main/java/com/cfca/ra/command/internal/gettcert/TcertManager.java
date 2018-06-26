package com.cfca.ra.command.internal.gettcert;

import com.cfca.ra.command.CommandException;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.pqc.jcajce.provider.util.KeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author zhangchong
 * @create 2018/5/24
 * @Description 交易证书管理类
 * @CodeReviewer
 * @since v3.0.0
 */
public class TcertManager {

    private static final Logger logger = LoggerFactory.getLogger(TcertManager.class);

    /**
     * CAKey 用于证书签名
     */
    private final PrivateKey caKey;

    /**
     * CACert 是CA用于提取与颁发证书相关联的数据
     */
    private final Certificate caCert;

    /**
     * ValidityPeriod是颁发的证书有效的持续时间 除非用户请求更短的有效期. 默认是 1 年.
     */
    private final long validityPeriod;

    /**
     * MaxAllowedBatchSize是一次可以请求的TCerts的最大数量. 默认值是 1000.
     */
    private final int maxAllowedBatchSize;

    public TcertManager(PrivateKey caKey, Certificate caCert, long validityPeriod, int maxAllowedBatchSize) {
        this.caKey = caKey;
        this.caCert = caCert;
        this.validityPeriod = validityPeriod;
        this.maxAllowedBatchSize = maxAllowedBatchSize;
    }

    public GettCertResponse getBatch(GettCertRequest req, Certificate ecert) throws CommandException {
        logger.info("GetBatch req={}", req);

        try {
            int numTCertsInBatch;
            if (req.getCount() == 0) {
                numTCertsInBatch = maxAllowedBatchSize;
            } else if (req.getCount() <= maxAllowedBatchSize) {
                numTCertsInBatch = req.getCount();
            } else {
                final String message = String.format("You may not request %d TCerts; the maximum is %d", req.getCount(), maxAllowedBatchSize);
                throw new CommandException(CommandException.REASON_CODE_GETTCERT_COMMAND_COMMS_FAILED, message);
            }

            long vp = validityPeriod;
            if (req.getValidityPeriod() > 0 && req.getValidityPeriod() < validityPeriod) {
                vp = req.getValidityPeriod();
            }

            byte[] nonce = new byte[16];

            final SubjectPublicKeyInfo info = ecert.getSubjectPublicKeyInfo();
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
            PublicKey pub = converter.getPublicKey(info);

            byte[] kdfKey = KeyUtil.getEncodedSubjectPublicKeyInfo(info);

            List<TCert> set = new ArrayList<>();

            for (int i = 0; i < numTCertsInBatch; i++) {
                String tcertid = UUID.randomUUID().toString();
            }

            long tcertID = genNumber();

            GettCertResponse tcertResponse = new GettCertResponse(null);
            tcertResponse.id = tcertID;
            tcertResponse.ts = System.currentTimeMillis();
            tcertResponse.key = kdfKey;

            return tcertResponse;
        } catch (Exception e) {
            throw new CommandException(CommandException.REASON_CODE_GETTCERT_COMMAND_ARGS_INVALID);
        }
    }

    private long genNumber() {
        return 0;
    }
}
