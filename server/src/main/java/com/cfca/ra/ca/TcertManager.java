package com.cfca.ra.ca;

import com.cfca.ra.RAServerException;
import com.cfca.ra.gettcert.ExtensionsAndks;
import com.cfca.ra.gettcert.GettCertRequest;
import com.cfca.ra.gettcert.GettCertResponse;
import com.cfca.ra.gettcert.TCertReq;
import com.cfca.ra.client.IRAClient;
import com.cfca.ra.client.RAClientImpl;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CRLConverter;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMException;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.pqc.jcajce.provider.util.KeyUtil;
import org.bouncycastle.util.encoders.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.*;

/**
 * @author zhangchong
 * @create 2018/5/24
 * @Description 交易证书管理类
 * @CodeReviewer
 * @since
 */
public class TcertManager {

    private static final Logger logger = LoggerFactory.getLogger(TcertManager.class);

    private static final long NANOSECOND = 1;
    private static final long MICROSECOND = 1000 * NANOSECOND;
    private static final long MILLISECOND = 1000 * MICROSECOND;
    private static final long SECOND = 1000 * MILLISECOND;
    private static final long MINUTE = 60 * SECOND;
    public static final long HOUR = 60 * MINUTE;

    private static final String ANONYMOUS_SUBJECT_NAME = "CN=Transaction Certificate";

    private static final byte[] PADDING = new byte[]{
            (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff,
            (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff,
            (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff,
            (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff
    };

    private final byte[] iv = {
            0x32, 0x37, 0x36, 0x35, 0x38, 0x33, 0x32, 0x31,
            0x38, 0x33, 0x36, 0x31, 0x34, 0x33, 0x32, 0x31
    };

    private final int seedLength = 20;

    private final ASN1ObjectIdentifier TCertEncTCertIndex = new ASN1ObjectIdentifier("1.2.3.4.5.6.7");
    private final ASN1ObjectIdentifier TCertEncEnrollmentID = new ASN1ObjectIdentifier("1.2.3.4.5.6.8");
    private final ASN1ObjectIdentifier TCertAttributesHeaders= new ASN1ObjectIdentifier("1.2.3.4.5.6.9");

    /**
     * CAKey 用于签名csr
     */
    private final PrivateKey caKey;

    /**
     * CACert用于提取CA数据并用于关联颁发的证书
     */
    private final Certificate caCert;

    /**
     * ValidityPeriod是发行证书有效的持续时间
     * 除非用户请求较短的有效期,默认值是1年
     */
    private final long validityPeriod;

    /**
     * MaxAllowedBatchSize是一次可以请求的TCerts的最大数量
     * 默认值是 1000.
     */
    private final int maxAllowedBatchSize;

    private final X500Name anonymousSubject;
    private final ContentSigner caCertSigner;
    private IRAClient raClient = new RAClientImpl();


    private TcertManager(Builder builder) {
        this.caKey = builder.caKey;
        this.caCert = builder.caCert;
        this.validityPeriod = builder.validityPeriod;
        this.maxAllowedBatchSize = builder.maxAllowedBatchSize;
        this.anonymousSubject = builder.anonymousSubject;
        this.caCertSigner = builder.caCertSigner;
    }

    public PrivateKey getCaKey() {
        return caKey;
    }

    public Certificate getCaCert() {
        return caCert;
    }

    public long getValidityPeriod() {
        return validityPeriod;
    }

    public int getMaxAllowedBatchSize() {
        return maxAllowedBatchSize;
    }

    public static class Builder {
        private final PrivateKey caKey;
        private final Certificate caCert;
        /**
         * 因为 交易证书的匿名性和不可链接性, 不能包含使用者主题名称,都统一写入固定值
         */
        private final X500Name anonymousSubject = new X500Name(ANONYMOUS_SUBJECT_NAME);
        /**
         * 默认是支持一年
         */
        private long validityPeriod = HOUR * 24 * 365;
        /**
         * 默认最大批量申请数是 1000
         */
        private int maxAllowedBatchSize = 1000;


        private ContentSigner caCertSigner = null;

        public Builder(PrivateKey caKey, Certificate caCert) {
            this.caKey = caKey;
            this.caCert = caCert;
        }

        public Builder caCertSigner(ContentSigner v) {
            caCertSigner = v;
            return this;
        }

        public Builder validityPeriod(long v) {
            validityPeriod = v;
            return this;
        }

        public Builder maxAllowedBatchSize(int v) {
            maxAllowedBatchSize = v;
            return this;
        }

        public TcertManager builder() {
            return new TcertManager(this);
        }
    }

    public GettCertResponse getBatch(GettCertRequest req, Certificate ecert, BouncyCastleProvider provider) throws RAServerException {
        logger.info("GetBatch req={}", req);

        try {
            // 将numTCertsInBatch设置为要获取的 TCerts 的数量
            // 如果请求 0, 就使用允许的最大值;
            int numTCertsInBatch;
            if (req.getCount() == 0) {
                numTCertsInBatch = maxAllowedBatchSize;
            } else if (req.getCount() <= maxAllowedBatchSize) {
                numTCertsInBatch = req.getCount();
            } else {
                final String message = String.format("You may not request %d TCerts; the maximum is %d",
                        req.getCount(), maxAllowedBatchSize);
                throw new RAServerException(RAServerException.REASON_CODE_GETTCERT_SERVICE_GEN_CSR, message);
            }

            long vp = validityPeriod;
            // 证书有效期选 请求的值和 配置文件中的最大值中的最小值
            if (req.getValidityPeriod() > 0 && req.getValidityPeriod() < validityPeriod) {
                vp = req.getValidityPeriod();
            }

            SecureRandom random = new SecureRandom();
            final Date notBefore = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(notBefore);
            calendar.add(Calendar.SECOND, (int) (vp / 1000000));
            final Date notAfter = calendar.getTime();

            final SubjectPublicKeyInfo subjectPublicKeyInfo = ecert.getSubjectPublicKeyInfo();
            final BigInteger serial = BigInteger.valueOf(random.nextLong());

            // Generate nonce for TCertIndex
            // 8 bytes rand, 8 bytes timestamp
            byte[] nonce = new byte[16];
            byte[] tmp = new byte[8];
            random.nextBytes(tmp);
            System.arraycopy(tmp, 0, nonce, 8, 8);

            byte[] kdfKey = generateKdfKey(ecert);

            List<TCertReq> set = new ArrayList<>();

            long tcertid;
            byte[] tidx;
            Key extKey;
            byte[] encryptedTidx;
            PublicKey txPub;
            byte[] seeds = SecureRandom.getSeed(seedLength);
            for (int i = 0; i < numTCertsInBatch; i++) {
                random.setSeed(seeds);
                tcertid = random.nextLong();

                tidx = computeTCertIndex(i, nonce);
                //FIXME:extKey 存在本地
                extKey = generateExtKey(kdfKey);
                txPub = generatePublicKey(ecert);
                encryptedTidx = cbcPKCS7Encrypt(extKey, tidx);
                ExtensionsAndks extensionsAndks = generateExtensions(tcertid, encryptedTidx, ecert, req);

                final TCertReq tCertReq = new TCertReq.Builder()
                        .issuer(anonymousSubject)
                        .extensionsAndks(extensionsAndks)
                        .notAfter(notAfter)
                        .notBefore(notBefore)
                        .serial(serial)
                        .subjectPublicKeyInfo(subjectPublicKeyInfo)
                        .thisAcSigner(caCertSigner)
                        .txPub(txPub)
                        .IsCA(false)
                        .build();

                set.add(tCertReq);
                random.nextBytes(seeds);
            }

            return raClient.gettcert(req, set, provider);
        } catch (Exception e) {
            throw new RAServerException(RAServerException.REASON_CODE_GETTCERT_SERVICE_GET_BATCH, e);
        }
    }

    private byte[] generateHKdfKey(Certificate ecert) throws RAServerException {
        try {
            final SubjectPublicKeyInfo info = ecert.getSubjectPublicKeyInfo();
            byte[] raw = KeyUtil.getEncodedSubjectPublicKeyInfo(info);
            KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacSHA384");
            SecretKey secretKey = keyGenerator.generateKey();
            Mac mac = Mac.getInstance(secretKey.getAlgorithm());
            mac.init(secretKey);
            return mac.doFinal(raw);
        } catch (Exception e) {
            throw new RAServerException(RAServerException.REASON_CODE_GETTCERT_SERVICE_GEN_KDF_KEY, e);
        }
    }

    Key generateHKdfKey(byte[] preKey) throws RAServerException {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacSHA384");
            SecretKey secretKey = keyGenerator.generateKey();
            Mac mac = Mac.getInstance(secretKey.getAlgorithm());
            mac.init(secretKey);
            final byte[] hmac = mac.doFinal(preKey);

            keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(256, new SecureRandom(hmac));
            //获得原始对称密钥的字节数组
            SecretKey originalKey = keyGenerator.generateKey();
            //根据字节数组生成AES密钥
            final byte[] raw = originalKey.getEncoded();
            return new SecretKeySpec(raw, "AES");
        } catch (Exception e) {
            throw new RAServerException(RAServerException.REASON_CODE_GETTCERT_SERVICE_GEN_KDF_KEY, e);
        }
    }

    byte[] generateKdfKey(Certificate ecert) throws RAServerException {
        try {
            final SubjectPublicKeyInfo info = ecert.getSubjectPublicKeyInfo();
            byte[] publicKeyInfo = KeyUtil.getEncodedSubjectPublicKeyInfo(info);
//            Key key = new SecretKeySpec(publicKeyInfo, "AES256");
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            //根据 ecert 公钥生成一个 256 位的随机源
            keyGenerator.init(256, new SecureRandom(publicKeyInfo));
            //获得原始对称密钥的字节数组
            SecretKey secretKey = keyGenerator.generateKey();
            //根据字节数组生成AES密钥
            final byte[] raw = secretKey.getEncoded();
            SecretKey key = new SecretKeySpec(raw, "AES");
            return key.getEncoded();
        } catch (Exception e) {
            throw new RAServerException(RAServerException.REASON_CODE_GETTCERT_SERVICE_GEN_KDF_KEY, e);
        }
    }

    /**
     * 基于kdfKey 派生出新的扩展密钥
     *
     * @param kdfKey
     * @return
     */
    Key generateExtKey(byte[] kdfKey) {
        return new SecretKeySpec(kdfKey, "AES256");
    }

    /**
     * @param tcertid        证书序列号
     * @param encryptedTidx
     * @param enrollmentCert
     * @param batchRequest
     * @return
     * @throws RAServerException
     */
    private ExtensionsAndks generateExtensions(long tcertid, byte[] encryptedTidx, Certificate enrollmentCert, GettCertRequest batchRequest) throws RAServerException {
        // For each TCert we need to store and retrieve to the user the list of Ks used to encrypt the EnrollmentID and the attributes.
        final HashMap<String, byte[]> ks = new HashMap<>();
        List<Attribute> attrs = batchRequest.getAttrs();

        List<Extension> extensions = new ArrayList<Extension>(attrs.size());

        String preK1 = batchRequest.getPreKey();
        Key preK0 = generateHKdfKey(preK1.getBytes());

        final byte[] bytes = "enrollmentID".getBytes();
        final byte[] preK0bytes = preK0.getEncoded();
        final byte[] newbytes = new byte[bytes.length + preK0bytes.length];
        System.arraycopy(bytes, 0, newbytes, 0, bytes.length);
        System.arraycopy(preK0bytes, 0, newbytes, bytes.length, preK0bytes.length);
        Key enrollmentIDKey = generateHKdfKey(newbytes);

        byte[] enrollmentID = buildPaddingEnrollmentId(enrollmentCert);

        byte[] encEnrollmentID = cbcPKCS7Encrypt(enrollmentIDKey, enrollmentID);
        // 保存用于加密EnrollmentID 的 key
        ks.put("enrollmentId", enrollmentIDKey.getEncoded());

        int attributeIdentifierIndex = 9;
        int count = 0;
        Map<String, Integer> attributesHeader = new HashMap<String, Integer>();

        String name;
        byte[] value;
        byte[] paddingValue;
        Key attributeKey;
        Extension extension;
        for (int i = 0; i < attrs.size(); i++) {
            count++;
            name = attrs.get(i).getName();

            value = attrs.get(i).getValue().getBytes();

            // 在header处保存属性扩展的位置
            attributesHeader.put(name, count);

            if (batchRequest.isEncryptAttrs()) {
                attributeKey = generateHKdfKey(preK0.getEncoded());
                paddingValue = new byte[value.length + PADDING.length];
                System.arraycopy(value, 0, paddingValue, 0, value.length);
                System.arraycopy(PADDING, 0, paddingValue, value.length, PADDING.length);
                value = cbcPKCS7Encrypt(attributeKey, paddingValue);

                // 保存用于加密属性的密钥
                ks.put(name, attributeKey.getEncoded());
            }

            // 为保存属性的扩展生成一个 ObjectIdentifier
            extension = new Extension(new ASN1ObjectIdentifier("1.2.3.4.5.6." + attributeIdentifierIndex + count), true, value);
            extensions.add(count - 1, extension);
        }

        Extension tCertEncTCertIndex = new Extension(TCertEncTCertIndex, true, encryptedTidx);
        extensions.add(tCertEncTCertIndex);

        final Extension tCertEncEnrollmentID = new Extension(TCertEncEnrollmentID, false, encEnrollmentID);
        extensions.add(tCertEncEnrollmentID);

        if (attrs.size() > 0) {
            final Extension tCertAttributesHeaders = new Extension(TCertAttributesHeaders, false, buildAttributesHeader(attributesHeader));
            extensions.add(tCertAttributesHeaders);
        }

        return new ExtensionsAndks(extensions, ks);
    }

    private byte[] buildAttributesHeader(Map<String, Integer> attributesHeader) {
        StringBuilder headerBuilder = new StringBuilder(100);
        String k;
        Integer v;
        for (Map.Entry<String, Integer> entry : attributesHeader.entrySet()) {
            k = entry.getKey();
            v = entry.getValue();
            headerBuilder.append(k).append("->").append(String.valueOf(v)).append("#");
        }

        return headerBuilder.toString().getBytes();
    }

    byte[] buildPaddingEnrollmentId(Certificate enrollmentCert) {
        final String enrollmentIDFromCert = getEnrollmentIDFromCert(enrollmentCert);
        final byte[] enrollmentIDFromCertBytes = enrollmentIDFromCert.getBytes();

        byte[] newbytes = new byte[PADDING.length + enrollmentIDFromCertBytes.length];
        System.arraycopy(enrollmentIDFromCertBytes, 0, newbytes, 0, enrollmentIDFromCertBytes.length);
        System.arraycopy(PADDING, 0, newbytes, enrollmentIDFromCertBytes.length, PADDING.length);
        return newbytes;
    }

    private String getEnrollmentIDFromCert(Certificate enrollmentCert) {
        return enrollmentCert.getSubject().toString();
    }

    /**
     * CBC密码分组链接(Cipher-block chaining)模式
     *
     * @param key
     * @param src
     * @return
     * @throws RAServerException
     */
    byte[] cbcPKCS7Encrypt(Key key, byte[] src) throws RAServerException {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
            cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
            byte[] encrypted = cipher.doFinal(src);
            logger.info("Encrypted Content:\n" + new String(Hex.encode(encrypted)));
            return encrypted;
        } catch (Exception e) {
            throw new RAServerException(RAServerException.REASON_CODE_GETTCERT_SERVICE_CBC_PKCS7_ENCRYPT, e);
        }
    }

    /**
     * CBC密码分组链接(Cipher-block chaining)模式
     *
     * @param key
     * @param encrypt
     * @return
     * @throws RAServerException
     */
    byte[] cbcPKCS7Decrypt(Key key, byte[] encrypt) throws RAServerException {
        try {
            Cipher out = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
            out.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
            byte[] dec = out.doFinal(encrypt);
//            logger.info("Decrypted Content:\n" + new String(dec));
            return dec;
        } catch (Exception e) {
            throw new RAServerException(RAServerException.REASON_CODE_GETTCERT_SERVICE_CBC_PKCS7_DECRYPT, e);
        }
    }

    /**
     * 从身份证书派生出新的公钥
     * FIXME: 从身份证书(ecert)派生出新的公钥,思路是如果是SM2公钥,先得到公钥使用的 ECC曲线参数,然后修改公钥点,得到新的公钥
     * FIXME: 目前不知道怎么派生,所以直接使用 ecert 的公钥
     *
     * @param ecert
     * @return 派生出的新的 PublicKey
     * @throws RAServerException 遇到异常则返回错误
     */
    private PublicKey generatePublicKey(Certificate ecert) throws RAServerException {
        try {
            final SubjectPublicKeyInfo subjectPublicKeyInfo = ecert.getSubjectPublicKeyInfo();
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
            return converter.getPublicKey(subjectPublicKeyInfo);
        } catch (PEMException e) {
            throw new RAServerException(RAServerException.REASON_CODE_GETTCERT_SERVICE_GENERATE_PUBLIC_KEY, e);
        }
    }

    /**
     * 计算TCertIndex
     *
     * @param i     索引
     * @param nonce 16字节数组
     * @return 封装后的 TcertIndex
     */
    private byte[] computeTCertIndex(int i, byte[] nonce) {
        final String s = String.valueOf(i);
        final byte[] bytes = s.getBytes();
        byte[] tidx = new byte[bytes.length + nonce.length + PADDING.length];
        System.arraycopy(bytes, 0, tidx, 0, bytes.length);
        System.arraycopy(nonce, 0, tidx, bytes.length, nonce.length);
        System.arraycopy(PADDING, 0, tidx, bytes.length + nonce.length, PADDING.length);
        return tidx;
    }

    /**
     * convert to X509Certificate
     */
    static X509Certificate convert(X509CertificateHolder h) throws Exception {
        return new JcaX509CertificateConverter().getCertificate(h);
    }

    /**
     * convert to X509CRL
     */
    static X509CRL convert(X509CRLHolder h) throws Exception {
        return new JcaX509CRLConverter().getCRL(h);
    }

    /**
     * 转为 SubjectPublicKeyInfo
     *
     * @param k 公钥
     * @return 产生的 SubjectPublicKeyInfo
     * @throws Exception 遇到异常则返回
     */
    static SubjectPublicKeyInfo getPublicKeyInfo(PublicKey k) throws Exception {
        return SubjectPublicKeyInfo.getInstance(k.getEncoded());
    }

    /**
     * SecureRandom类提供加密的强随机数生成器 (RNG)
     * SecureRandom类收集了一些随机事件,比如鼠标点击,键盘点击等等,SecureRandom 使用这些随机事件作为种子
     * 这意味着,种子是不可预测的
     *
     * @return 产生的随机数
     */
    private long generateNumber() {
        final SecureRandom secureRandom = new SecureRandom();
        return secureRandom.nextLong();
    }

}
