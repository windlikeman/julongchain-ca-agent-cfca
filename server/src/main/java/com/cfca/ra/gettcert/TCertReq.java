package com.cfca.ra.gettcert;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.operator.ContentSigner;

import java.math.BigInteger;
import java.security.PublicKey;
import java.util.Date;

/**
 * @author zhangchong
 * @create 2018/5/25
 * @Description
 * @CodeReviewer helonglong
 * @since v3.0.0
 */
public class TCertReq {
    private final Extension keyUsageExtension;
    private final Extension skiExtension;
    private final SubjectPublicKeyInfo subjectPublicKeyInfo;
    private final BigInteger serial;
    private final ExtensionsAndks extensionsAndks;
    private final PublicKey txPub;
    private final X500Name issuer;
    private final ContentSigner thisAcSigner;
    private final Date notBefore;
    private final Date notAfter;
    private final boolean isCA;

    public TCertReq(Builder builder) {
        this.keyUsageExtension = builder.keyUsageExtension;
        this.skiExtension = builder.skiExtension;
        this.subjectPublicKeyInfo = builder.subjectPublicKeyInfo;
        this.serial = builder.serial;
        this.txPub = builder.txPub;
        this.issuer = builder.issuer;
        this.thisAcSigner = builder.thisAcSigner;
        this.notBefore = builder.notBefore;
        this.notAfter = builder.notAfter;
        this.extensionsAndks = builder.extensionsAndks;
        this.isCA = builder.isCA;
    }

    public static class Builder{
        private final Extension keyUsageExtension = new Extension(Extension.keyUsage, true, ASN1OctetString.getInstance(new KeyUsage(KeyUsage.digitalSignature)));
        private final Extension skiExtension = new Extension(Extension.subjectKeyIdentifier, true, ASN1OctetString.getInstance(new ASN1ObjectIdentifier("1.2.3.4")));
        private SubjectPublicKeyInfo subjectPublicKeyInfo;
        private BigInteger serial;
        private ExtensionsAndks extensionsAndks;
        private PublicKey txPub;
        private X500Name issuer;
        private ContentSigner thisAcSigner;
        private Date notBefore;
        private Date notAfter;
        private boolean isCA;

        public Builder subjectPublicKeyInfo(SubjectPublicKeyInfo v){
            subjectPublicKeyInfo = v;
            return this;
        }

        public Builder serial(BigInteger v){
            serial = v;
            return this;
        }

        public Builder extensionsAndks(ExtensionsAndks v){
            extensionsAndks = v;
            return this;
        }

        public Builder txPub(PublicKey v){
            txPub = v;
            return this;
        }

        public Builder issuer(X500Name v){
            issuer = v;
            return this;
        }

        public Builder thisAcSigner(ContentSigner v){
            thisAcSigner = v;
            return this;
        }

        public Builder notBefore(Date v){
            notBefore = v;
            return this;
        }

        public Builder notAfter(Date v){
            notAfter = v;
            return this;
        }

        public TCertReq build(){
            return new TCertReq(this);
        }

        public Builder IsCA(boolean b) {
            isCA = b;
            return this;
        }
    }

    public boolean isCA() {
        return isCA;
    }

    public X500Name getIssuer() {
        return issuer;
    }

    public ContentSigner getThisAcSigner() {
        return thisAcSigner;
    }

    public Date getNotBefore() {
        return notBefore;
    }

    public Date getNotAfter() {
        return notAfter;
    }

    public Extension getKeyUsageExtension() {
        return keyUsageExtension;
    }

    public Extension getSkiExtension() {
        return skiExtension;
    }

    public SubjectPublicKeyInfo getSubjectPublicKeyInfo() {
        return subjectPublicKeyInfo;
    }

    public BigInteger getSerial() {
        return serial;
    }

    public ExtensionsAndks getExtensionsAndks() {
        return extensionsAndks;
    }

    public PublicKey getTxPub() {
        return txPub;
    }
}
