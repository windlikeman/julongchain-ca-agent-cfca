package demo;

import com.cfca.ra.command.CommandException;
import com.cfca.ra.command.internal.CsrResult;
import com.cfca.ra.command.utils.MyStringUtils;
import com.cfca.ra.command.utils.PemUtils;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveGenParameterSpec;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;
import org.bouncycastle.util.encoders.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.security.*;
import java.security.spec.AlgorithmParameterSpec;

/**
 * @author zhangchong
 * @create 2018/5/31
 * @Description CSR生成工具
 * @CodeReviewer
 * @since
 */
public class CsrUtils {
    private static final Logger logger = LoggerFactory.getLogger(CsrUtils.class);
    private static final BouncyCastleProvider provider;

    static {
        provider = new BouncyCastleProvider();
        Security.addProvider(provider);
    }

    public final static String MSPDIR = "D:\\R15\\P1552\\dev\\blockchain\\command\\ca-client\\config\\msp";

    /**
     * @param keyAlg      签名算法名称
     * @param distictName 证书使用者 DN
     * @return CsrResult
     * @throws CommandException 失败时报错
     */
    public static CsrResult genCSR(String keyAlg, String distictName) throws CommandException {
        if (MyStringUtils.isEmpty(keyAlg) || MyStringUtils.isEmpty(distictName)) {
            throw new CommandException(CommandException.REASON_CODE_INTERNAL_CLIENT_GENCSR_FAILED, "keyAlg or distictName is empty");
        }

        CsrResult result;
        switch (keyAlg.toUpperCase()) {
            case "SM2":
                result = getSM2CsrResult(distictName);
                break;
            default:
                throw new CommandException(CommandException.REASON_CODE_INTERNAL_CLIENT_GENCSR_FAILED, "Unsupport keyAlg type[" + keyAlg + "]");
        }

        return result;
    }

    private static CsrResult getSM2CsrResult(String distictName) throws CommandException {
        final AlgorithmParameterSpec sm2p256v1 = new ECNamedCurveGenParameterSpec("sm2p256v1");
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("EC", provider);
            generator.initialize(sm2p256v1);
            KeyPair keypair = generator.generateKeyPair();
            logger.debug("getSM2CsrResult>>>>>>publicKey : " + keypair.getPublic());
            logger.debug("getSM2CsrResult>>>>>>privateKey : " + keypair.getPrivate());
            String csr = genSM2CSR(distictName, keypair);
            return new CsrResult(csr, keypair);
        } catch (Exception e) {
            throw new CommandException(CommandException.REASON_CODE_INTERNAL_CLIENT_GEN_SM2_CSR_FAILED, e);
        }
    }

    private static String genSM2CSR(String distictName, KeyPair keypair) throws CommandException {

        try {
            if (MyStringUtils.isEmpty(distictName)) {
                throw new CommandException(CommandException.REASON_CODE_INTERNAL_CLIENT_GEN_SM2_CSR_FAILED, "distictName is empty");
            }

            PKCS10CertificationRequestBuilder pkcs10Builder = new JcaPKCS10CertificationRequestBuilder(
                    new X500Name(distictName),
                    keypair.getPublic());

            ContentSigner contentSigner = new JcaContentSignerBuilder("SM3WITHSM2").setProvider("BC").build(keypair.getPrivate());
            PKCS10CertificationRequest csr = pkcs10Builder.build(contentSigner);
            final byte[] base64Encoded = Base64.encode(csr.getEncoded());
            return new String(base64Encoded);
        } catch (CommandException e) {
            throw e;
        } catch (Exception e) {
            throw new CommandException(CommandException.REASON_CODE_INTERNAL_CLIENT_GEN_SM2_CSR_FAILED, e);
        }
    }

    private static String initializeIfNeeded() throws CommandException {
        try {
            // 密钥目录和文件
            String keyDir = String.join(File.separator, MSPDIR, "keystore");
            boolean mkdirs = new File(keyDir).mkdirs();
            if (!mkdirs) {
                logger.debug("initializeIfNeeded<<<<<<failed to create keystore directory");
            }
            String keyFile = String.join(File.separator, keyDir, "key.pem");
            logger.debug("initializeIfNeeded<<<<<<use keyFile at "+ keyFile);
            return keyFile;
        } catch (Exception e) {
            throw new CommandException(CommandException.REASON_CODE_INTERNAL_CLIENT_INIT_FAILED, "failed to init client", e);
        }
    }

    static void storeMyPrivateKey(CsrResult result) throws CommandException {
        try {
            String keyFile = initializeIfNeeded();
            final PublicKey publicKey = result.getKeyPair().getPublic();
            final PrivateKey privateKey = result.getKeyPair().getPrivate();
            PemUtils.storePrivateKey(keyFile, privateKey);
            logger.debug("storeMyPrivateKey<<<<<<store private key at {"+keyFile+"}");
            logger.debug("storeMyPrivateKey<<<<<<publicKey :"+publicKey);
            logger.debug("storeMyPrivateKey<<<<<<privateKey :"+privateKey);

        } catch (Exception e) {
            throw new CommandException(CommandException.REASON_CODE_INTERNAL_CLIENT_STORE_PRIVATEKEY_FAILED, e);
        }
    }
}
