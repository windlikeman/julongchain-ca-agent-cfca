package org.bica.julongchain.cfca.ra.command.utils;

import org.bica.julongchain.cfca.ra.command.CommandException;
import org.bica.julongchain.cfca.ra.command.internal.CsrResult;
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
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.Security;
import java.security.spec.AlgorithmParameterSpec;

/**
 * @author zhangchong
 * @create 2018/5/31
 * @Description 证书申请CSR生成工具
 * @CodeReviewer
 * @since v3.0.0.0
 */
public class CsrUtils {
    private static final Logger logger = LoggerFactory.getLogger(CsrUtils.class);
    private static final BouncyCastleProvider provider;

    static {
        provider = new BouncyCastleProvider();
        Security.addProvider(provider);
    }

    public final static String MSPDIR = "ca-client\\config\\msp";

    /**
     * @param keyAlg      签名算法名称
     * @param distictName 证书使用者 DN
     * @return CsrResult
     * @throws CommandException 失败时报错
     */
    public static CsrResult genCSR(String keyAlg, String distictName) throws CommandException {
        if (StringUtils.isEmpty(keyAlg) || StringUtils.isEmpty(distictName)) {
            throw new CommandException("keyAlg or distictName is empty");
        }

        CsrResult result;
        switch (keyAlg.toUpperCase()) {
            case "SM2":
                result = getSM2CsrResult(distictName);
                break;
            default:
                throw new CommandException("Unsupport keyAlg type[" + keyAlg + "]");
        }

        return result;
    }

    private static CsrResult getSM2CsrResult(String distictName) throws CommandException {
        final AlgorithmParameterSpec sm2p256v1 = new ECNamedCurveGenParameterSpec("sm2p256v1");
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("EC", provider);
            generator.initialize(sm2p256v1);
            KeyPair keypair = generator.generateKeyPair();
            logger.info("getSM2CsrResult>>>>>>publicKey : " + keypair.getPublic());
            logger.info("getSM2CsrResult>>>>>>privateKey : " + keypair.getPrivate());
            String csr = genSM2CSR(distictName, keypair);
            return new CsrResult(csr, keypair);
        } catch (Exception e) {
            throw new CommandException(e);
        }
    }

    private static String genSM2CSR(String distictName, KeyPair keypair) throws CommandException {

        try {
            if (StringUtils.isEmpty(distictName)) {
                throw new CommandException("distictName is empty");
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
            throw new CommandException(e);
        }
    }

    private static String initializeIfNeeded(String userName) throws CommandException {
        try {
            // 密钥目录和文件
            String keyDir;
            if (StringUtils.isBlank(userName) || "admin".equalsIgnoreCase(userName)) {
                keyDir = String.join(File.separator, MSPDIR, "keystore");
            } else {
                keyDir = String.join(File.separator, MSPDIR, userName, "keystore");
            }

            boolean mkdirs = new File(keyDir).mkdirs();
            if (!mkdirs) {
                logger.info("initializeIfNeeded<<<<<<failed to create keystore directory");
            }
            String keyFile = String.join(File.separator, keyDir, "key.pem");
            logger.info("initializeIfNeeded<<<<<<use keyFile at " + keyFile);
            return keyFile;
        } catch (Exception e) {
            throw new CommandException("failed to init client", e);
        }
    }

    public static void storeMyPrivateKey(CsrResult result, String username) throws CommandException {
        try {
            String keyFile = initializeIfNeeded(username);
//            final PublicKey publicKey = result.getKeyPair().getPublic();
            final PrivateKey privateKey = result.getKeyPair().getPrivate();
            PemUtils.storePrivateKey(keyFile, privateKey);
            logger.info("storeMyPrivateKey  <<<<<< keyFile =>[{}] ", keyFile);
//            logger.info("storeMyPrivateKey  <<<<<< publicKey :" + publicKey);
            logger.info("storeMyPrivateKey  <<<<<< privateKey :" + privateKey);

        } catch (Exception e) {
            throw new CommandException(e);
        }
    }

    public static void storePrivateKey(final PrivateKey privateKey, String keyFile) throws CommandException {
        try {
            PemUtils.storePrivateKey(keyFile, privateKey);
            logger.info("storePrivateKey  <<<<<< keyFile =>[{}] ", keyFile);
            logger.info("storePrivateKey  <<<<<< privateKey :" + privateKey);

        } catch (Exception e) {
            throw new CommandException(e);
        }
    }

    public static void storePrivateKey(CsrResult result, String keyDir, String keyFile) throws CommandException {
        try {
            boolean mkdirs = new File(keyDir).mkdirs();
            if (!mkdirs) {
                logger.info("storePrivateKey<<<<<<failed to create keystore directory");
            }
            final PrivateKey privateKey = result.getKeyPair().getPrivate();
            PemUtils.storePrivateKey(String.join(File.separator,keyDir, keyFile), privateKey);
            logger.info("storePrivateKey  <<<<<< keyFile =>[{}] ", keyFile);
            logger.info("storePrivateKey  <<<<<< privateKey :" + privateKey);

        } catch (Exception e) {
            throw new CommandException(e);
        }
    }
}
