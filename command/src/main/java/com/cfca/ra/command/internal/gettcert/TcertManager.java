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
 * @since
 */
public class TcertManager {

    private static final Logger logger = LoggerFactory.getLogger(TcertManager.class);

    /**
     * CAKey is used for signing a certificate request
     */
    private final PrivateKey caKey;

    /**
     * CACert is used for extracting CA data to associate with issued certificates
     */
    private final Certificate caCert;

    /**
     * ValidityPeriod is the duration that the issued certificate will be valid
     * unless the user requests a shorter validity period.
     * The default value is 1 year.
     */
    private final long validityPeriod;

    /**
     * MaxAllowedBatchSize is the maximum number of TCerts which can be requested at a time.
     * The default value is 1000.
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
            // Set numTCertsInBatch to the number of TCerts to get.
            // If 0 are requested, retrieve the maximum allowable;
            // otherwise, retrieve the number requested it not too many.
            int numTCertsInBatch;
            if (req.getCount() == 0) {
                numTCertsInBatch = maxAllowedBatchSize;
            } else if (req.getCount() <= maxAllowedBatchSize) {
                numTCertsInBatch = req.getCount();
            } else {
                final String message = String.format("You may not request %d TCerts; the maximum is %d",
                        req.getCount(), maxAllowedBatchSize);
                throw new CommandException(CommandException.REASON_CODE_GETTCERT_COMMAND_COMMS_FAILED, message);
            }

            long vp = validityPeriod;
            // Certs are valid for the min of requested and configured max
            if (req.getValidityPeriod() > 0 && req.getValidityPeriod() < validityPeriod) {
                vp = req.getValidityPeriod();
            }

            // Create a template from which to create all other TCerts.
            // Since a TCert is anonymous and unlinkable, do not include
//            template:= &x509.Certificate {
//                Subject:
//                tcertSubject,
//            }
//            template.NotBefore = time.Now();
//            template.NotAfter = template.NotBefore.Add(vp);
//            template.IsCA = false;
//            template.KeyUsage = x509.KeyUsageDigitalSignature;
//            template.SubjectKeyId = []byte {
//                1, 2, 3, 4
//            } ;

            // Generate nonce for TCertIndex
            byte[] nonce = new byte[16];// 8 bytes rand, 8 bytes timestamp
//            rand.Reader.Read(nonce[:8]);

            final SubjectPublicKeyInfo info = ecert.getSubjectPublicKeyInfo();
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
            PublicKey pub = converter.getPublicKey(info);

            byte[] kdfKey = KeyUtil.getEncodedSubjectPublicKeyInfo(info);
//            mac:=hmac.New(sha512.New384,[]byte(createHMACKey()));
//            raw, _ :=x509.MarshalPKIXPublicKey(pub);
//            mac.Write(raw);
//            kdfKey:=mac.Sum(nil);

            List<TCert> set = new ArrayList<>();

            for (int i = 0; i < numTCertsInBatch; i++) {
                String tcertid = UUID.randomUUID().toString();
//                tcertid = generateIntUUID();

                // Compute TCertIndex
//                tidx:= []byte(strconv.Itoa(2 * i + 1));
//                tidx = append(tidx[:],nonce[:]...);
//                tidx = append(tidx[:],Padding...);
//
//                mac:=hmac.New(sha512.New384, kdfKey);
//                mac.Write([]byte {
//                    1
//                });
//                extKey:=mac.Sum(nil)[:32];
//
//                mac = hmac.New(sha512.New384, kdfKey);
//                mac.Write([]byte {
//                    2
//                });
//                mac = hmac.New(sha512.New384, mac.Sum(nil));
//                mac.Write(tidx);
//
//                one:=new (big.Int).SetInt64(1);
//                k:=new (big.Int).SetBytes(mac.Sum(nil));
//                k.Mod(k, new (big.Int).Sub(pub.Curve.Params().N, one));
//                k.Add(k, one);
//
//                tmpX, tmpY :=pub.ScalarBaseMult(k.Bytes())
//                txX, txY :=pub.Curve.Add(pub.X, pub.Y, tmpX, tmpY)
//                txPub:=ecdsa.PublicKey {
//                    Curve:
//                    pub.Curve, X:txX, Y:txY
//                }

                // Compute encrypted TCertIndex
//                encryptedTidx = CBCPKCS7Encrypt(extKey, tidx);
//
//                (extensions, ks) =generateExtensions(tcertid, encryptedTidx, ecert, req);
//
//                template.PublicKey = txPub;
//                template.Extensions = extensions;
//                template.ExtraExtensions = extensions;
//                template.SerialNumber = tcertid;

//                raw =x509.CreateCertificate(rand.Reader, template, tm.CACert, & txPub, tm.CAKey)
//            if err != nil {
//                return nil, fmt.Errorf("Failed in TCert x509.CreateCertificate: %s", err)
//            }

//                pem:=ConvertDERToPEM(raw, "CERTIFICATE");
//
//                set = append(set, api.TCert {
//                    Cert: pem,
//                    Keys: ks
//                });
            }

            long tcertID = genNumber(/*big.NewInt(20)*/);

            GettCertResponse tcertResponse = new GettCertResponse(null);
            tcertResponse.id = tcertID;
            tcertResponse.ts = System.currentTimeMillis();
            tcertResponse.key = kdfKey;
//            tcertResponse.tCerts = tCerts;

            return tcertResponse;
        } catch (Exception e) {
            throw new CommandException(CommandException.REASON_CODE_GETTCERT_COMMAND_ARGS_INVALID);
        }
    }

    private long genNumber() {
        return 0;
    }
}
