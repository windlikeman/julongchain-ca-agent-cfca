package org.bica.julongchain.cfca.ra.command.utils;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;

import org.bica.julongchain.cfca.ra.command.CommandException;

/**
 * @author qazhang
 * @Description SM2签名工具包（使用默认Z值签名与校验）
 * @CodeReviewer zhangchong
 *
 */
public final class SignatureUtil {

    private SignatureUtil() {

    }

    /**
     * SM2签名（使用默认Z值签名）
     * 
     * @param privateKey
     * @param sourceData
     * @return
     * @throws CommandException
     */
    static public final byte[] sign(PrivateKey privateKey, byte[] sourceData) throws CommandException {
        try {
            Signature signature = Signature.getInstance("SM3withSM2", "BC");
            signature.initSign(privateKey);
            signature.update(sourceData);
            return signature.sign();
        } catch (InvalidKeyException e) {
            throw new CommandException("fail to sign token: InvalidKey", e);
        } catch (NoSuchAlgorithmException e) {
            throw new CommandException("fail to sign token: NoSuchAlgorithm", e);
        } catch (NoSuchProviderException e) {
            throw new CommandException("fail to sign token: NoSuchProvider", e);
        } catch (Exception e) {
            throw new CommandException("fail to sign token: Signature", e);
        }

    }

    /**
     * SM2校验（使用默认Z值校验）
     * 
     * @param publicKey
     * @param sourceData
     * @param sign
     * @return
     * @throws CommandException
     */
    static public final boolean verify(PublicKey publicKey, byte[] sourceData, byte[] sign) throws CommandException {
        boolean verify;
        try {
            Signature signature = Signature.getInstance("SM3withSM2", "BC");
            signature.initVerify(publicKey);
            signature.update(sourceData);
            verify = signature.verify(sign);
        } catch (InvalidKeyException e) {
            throw new CommandException("fail to verify token: InvalidKey", e);
        } catch (NoSuchAlgorithmException e) {
            throw new CommandException("fail to verify token: NoSuchAlgorithm", e);
        } catch (NoSuchProviderException e) {
            throw new CommandException("fail to verify token: NoSuchProvider", e);
        } catch (SignatureException e) {
            throw new CommandException("fail to verify token: Signature", e);
        }
        if (!verify) {
            throw new CommandException("verify failed due to public and private keys do not match");
        }
        return verify;
    }

}
