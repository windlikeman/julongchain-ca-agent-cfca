package org.bica.julongchain.cfca.ra.command.internal;

import java.security.PrivateKey;
import java.security.PublicKey;

import org.bica.julongchain.cfca.ra.command.CommandException;
import org.bica.julongchain.cfca.ra.command.internal.enroll.EnrollmentResponse;
import org.bica.julongchain.cfca.ra.command.internal.reenroll.ReenrollmentRequest;
import org.bica.julongchain.cfca.ra.command.internal.register.RegistrationRequest;
import org.bica.julongchain.cfca.ra.command.internal.register.RegistrationResponse;
import org.bica.julongchain.cfca.ra.command.internal.revoke.RevokeRequest;
import org.bica.julongchain.cfca.ra.command.internal.revoke.RevokeResponse;
import org.bica.julongchain.cfca.ra.command.utils.SignatureUtil;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhangchong
 * @create 2018/5/11
 * @Description 每一个用户实体, 标识每一个用户, 使用用户的名称, 签名私钥和公钥证书 以及内部客户端来标识
 * @CodeReviewer
 * @since v3.0.0
 */
public class Identity {

    private static final Logger logger = LoggerFactory.getLogger(Identity.class);
    private final String name;
    private Signer ecert;
    private final Client client;

    Identity(String name, Signer ecert, Client client) {
        this.name = name;
        this.ecert = ecert;
        this.client = client;
    }

    public void setEcert(Signer ecert) {
        this.ecert = ecert;
    }

    public String getName() {
        return name;
    }

    public Signer getEcert() {
        return ecert;
    }

    public Client getClient() {
        return client;
    }

    public void store() throws CommandException {
        client.storeMyIdentity(ecert.getCert());
    }

    public EnrollmentResponse reenroll(ReenrollmentRequest request) throws CommandException {
        return client.reenroll(request, name, this);
    }

    /**
     * 根据ecert的cert和key 算出token
     *
     * @param body
     * @param isReenroll 是否是Reenroll 命令
     * @return
     */
    public String addTokenAuthHdr(byte[] body, boolean isReenroll) throws CommandException {
        logger.info("addTokenAuthHdr<<<<<<Adding token-based authorization header");
        byte[] cert = ecert.getCert();
        PrivateKey key = ecert.getKey();
        return createToken(cert, key, body, isReenroll);
    }

    /**
     * @param cert       经过B64解码后的证书字节数组
     * @param privateKey 私钥
     * @param body
     * @param isReenroll
     * @return
     */
    private String createToken(final byte[] cert, PrivateKey privateKey, byte[] body, boolean isReenroll) throws CommandException {
        try {
            final Certificate certificate = Certificate.getInstance(cert);
            final SubjectPublicKeyInfo subjectPublicKeyInfo = certificate.getSubjectPublicKeyInfo();
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
            PublicKey publicKey = converter.getPublicKey(subjectPublicKeyInfo);
            logger.info("createToken<<<<<<publicKey  : {}", publicKey);
            logger.info("createToken<<<<<<privateKey : {}", privateKey);

            final byte[] sign = SignatureUtil.sign(privateKey, body);

            SignatureUtil.verify(publicKey, body, sign);
            logger.info("createToken<<<<<<name : {}", name);
            final byte[] enrollmentIdBytes = name.getBytes();
            final String b64EnrollmentId = Base64.toBase64String(enrollmentIdBytes);
            final String b64Sig = new String(Base64.encode(sign));
            final String token;
            if (isReenroll) {
                final String userName = client.getClientCfg().getAdmin();
                final String sequenceNo = client.getClientCfg().getSequenceNo();
                final String uniqueName = String.format("%s-%s", userName, sequenceNo);
                token = b64EnrollmentId + "." + b64Sig + "." + uniqueName;
                logger.info("createToken<<<<<<uniqueName : {}", uniqueName);
            } else {
                token = b64EnrollmentId + "." + b64Sig;
            }

            logger.info("createToken<<<<<<body  : {}", Hex.toHexString(body));
            logger.info("createToken<<<<<<token : {}", token);

            return token;
        } catch (Exception e) {
            throw new CommandException("fail to create token", e);
        }
    }

    public RegistrationResponse register(RegistrationRequest registrationRequest) throws CommandException {
        return client.register(registrationRequest, this);
    }

    public RevokeResponse revoke(RevokeRequest revokeRequest) throws CommandException {
        return client.revoke(revokeRequest, this);
    }

    public String getKeyFile() {
        return client.getKeyFile();
    }

    @Override
    public String toString() {
        return "Identity{" + "name='" + name + '\'' + ", ecert=" + ecert + ", client=" + client + '}';
    }
}
