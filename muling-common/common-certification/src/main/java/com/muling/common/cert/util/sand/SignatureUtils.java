package com.muling.common.cert.util.sand;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.*;

public class SignatureUtils {
    public SignatureUtils() {
    }

    public static String sign(String plainText, String algorithm, PrivateKey privateKey) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature signature = Signature.getInstance(algorithm);
        signature.initSign(privateKey);
        signature.update(plainText.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(signature.sign());
//        return Base64.encodeBase64String(signature.sign());
    }

    public static boolean verify(String plainText, String sign, String algorithm, PublicKey publicKey) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature signature = Signature.getInstance(algorithm);
        signature.initVerify(publicKey);
        signature.update(plainText.getBytes(StandardCharsets.UTF_8));
        return signature.verify(Base64.getDecoder().decode(sign));
//        return signature.verify(Base64.decodeBase64(sign));
    }
}
