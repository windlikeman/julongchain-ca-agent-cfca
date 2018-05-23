package com.cfca.ra.command.internal;

import com.cfca.ra.command.CommandException;
import com.cfca.ra.command.internal.enroll.EnrollmentResponse;
import com.cfca.ra.command.internal.gettcert.GettCertRequest;
import com.cfca.ra.command.internal.gettcert.GettCertResponse;
import com.cfca.ra.command.internal.reenroll.ReenrollmentRequest;
import com.cfca.ra.command.internal.register.RegistrationRequest;
import com.cfca.ra.command.internal.register.RegistrationResponse;
import com.cfca.ra.command.internal.revoke.RevokeRequest;
import com.cfca.ra.command.internal.revoke.RevokeResponse;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.util.encoders.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;

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

    @Override
    public String toString() {
        return "Identity{" +
                "name='" + name + '\'' +
                ", ecert=" + ecert +
                ", client=" + client +
                '}';
    }

    public void store() throws CommandException {
        client.storeMyIdentity(ecert.getCert());
    }

    public EnrollmentResponse reenroll(ReenrollmentRequest request) throws CommandException {
        String token = addTokenAuthHdr();
        return client.reenroll(request, token, name);
    }

    /**
     * 根据ecert的cert和key 算出token
     *
     * @return
     */
    private String addTokenAuthHdr() throws CommandException {
        logger.info("Adding token-based authorization header");
        byte[] cert = ecert.getCert();
        PrivateKey key = ecert.getKey();
        return createToken(cert, key);
    }

    /**
     * @param cert       经过B64解码后的证书字节数组
     * @param privateKey 私钥
     * @return
     */
    private String createToken(byte[] cert, PrivateKey privateKey) throws CommandException {
        try {

            byte[] newCert = new byte[cert.length];
            System.arraycopy(cert, 0, newCert, 0, cert.length);

            final ASN1Primitive asn1Primitive = ASN1Primitive.fromByteArray(newCert);
            final Certificate certificate = Certificate.getInstance(asn1Primitive);
            final SubjectPublicKeyInfo subjectPublicKeyInfo = certificate.getSubjectPublicKeyInfo();
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
            PublicKey publicKey = converter.getPublicKey(subjectPublicKeyInfo);
            logger.info("createToken<<<<<<publicKey : " + publicKey);

            String b64Cert = Base64.toBase64String(cert);
            Signature signature = Signature.getInstance("SM3withSM2", "BC");
            signature.initSign(privateKey);
            signature.update(cert);
            final byte[] sign = signature.sign();
            String b64Sig = new String(Base64.encode(sign));
            final String s = b64Cert + "." + b64Sig;
            logger.info("createToken<<<<<<token : " + s);

            Signature signature1 = Signature.getInstance("SM3withSM2", "BC");
            signature1.initVerify(publicKey);
            signature1.update(cert);
            final boolean verify = signature1.verify(sign);
            if (!verify) {
                throw new CommandException(CommandException.REASON_CODE_IDENTITY_CREATE_TOKEN, "verify failed");
            }
            return s;
        } catch (Exception e) {
            throw new CommandException(CommandException.REASON_CODE_IDENTITY_CREATE_TOKEN, e);
        }
    }

    public RegistrationResponse register(RegistrationRequest registrationRequest) throws CommandException {
        final String token = addTokenAuthHdr();
        return client.register(registrationRequest, token);
    }

    public RevokeResponse revoke(RevokeRequest registrationRequest) throws CommandException {
        final String token = addTokenAuthHdr();
        return client.revoke(registrationRequest, token);
    }

    public GettCertResponse gettcert(GettCertRequest request) throws CommandException {
        final String token = addTokenAuthHdr();
        return client.gettcert(request, token);
    }
}