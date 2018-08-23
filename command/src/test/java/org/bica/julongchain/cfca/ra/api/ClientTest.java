package org.bica.julongchain.cfca.ra.api;

import com.sun.org.apache.xerces.internal.impl.dv.util.HexBin;
import org.bouncycastle.asn1.util.ASN1Dump;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveGenParameterSpec;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;
import org.bouncycastle.util.encoders.Base64;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.OutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.AlgorithmParameterSpec;
import java.security.cert.Certificate;
import java.security.spec.AlgorithmParameterSpec;


public class ClientTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void enroll() throws Exception {
        final Provider provider = new BouncyCastleProvider();
        Security.addProvider(provider);

        final AlgorithmParameterSpec sm2p256v1 = new ECNamedCurveGenParameterSpec("sm2p256v1");

        System.err.println(provider);
        KeyPairGenerator generator = KeyPairGenerator.getInstance("EC", provider);
        generator.initialize(sm2p256v1);
        final KeyPair keypair = generator.generateKeyPair();

        final PrivateKey privateKey = keypair.getPrivate();
        final PublicKey publickey = keypair.getPublic();
        System.out.println("privateKey=" + privateKey);
        System.out.println("publickey=" + publickey);
        PKCS10CertificationRequestBuilder pkcs10Builder = new JcaPKCS10CertificationRequestBuilder(new X500Name("CN=TEST,C=CN"), keypair.getPublic());

        ContentSigner contentSigner = new JcaContentSignerBuilder("SM3WITHSM2").setProvider("BC").build(keypair.getPrivate());
        PKCS10CertificationRequest csr = pkcs10Builder.build(contentSigner);
        final byte[] encoded = csr.getEncoded();
        final byte[] base64Encoded = Base64.encode(encoded);
        System.out.println(new String(base64Encoded));
        System.out.println(HexBin.encode(encoded));
        System.out.println(ASN1Dump.dumpAsString(csr.toASN1Structure(), true));
    }
}
