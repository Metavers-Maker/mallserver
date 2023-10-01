package com.muling.common.cert.util.sand;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.security.Signature;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EncryptUtil {
    private static final Logger logger = LoggerFactory.getLogger(EncryptUtil.class);

    public static List<NameValuePair> genEncryptData(String merchId, String transCode, String data) throws Exception {
        if ((null == merchId) || (null == transCode) || (null == data)) {
            logger.error("merchId or transCode or data is null");
            return null;
        }
        List<NameValuePair> formparams = new ArrayList();
        formparams.add(new BasicNameValuePair("merId", merchId));
        formparams.add(new BasicNameValuePair("transCode", transCode));
        try {
            byte[] plainBytes = data.getBytes("UTF-8");
            String aesKey = RandomStringGenerator.getRandomStringByLength(16);
            byte[] aesKeyBytes = aesKey.getBytes("UTF-8");
            String encryptData = new String(
                    Base64.encodeBase64(
                            CryptoUtil.AESEncrypt(plainBytes, aesKeyBytes, "AES", "AES/ECB/PKCS5Padding", null)),
                    "UTF-8");
            String sign = new String(
                    Base64.encodeBase64(CryptoUtil.digitalSign(plainBytes, CertUtil.getPrivateKey(), "SHA1WithRSA")),
                    "UTF-8");
            String encryptKey = new String(Base64.encodeBase64(
                    CryptoUtil.RSAEncrypt(aesKeyBytes, CertUtil.getPublicKey(), 2048, 11, "RSA/ECB/PKCS1Padding")),
                    "UTF-8");
            formparams.add(new BasicNameValuePair("encryptData", encryptData));
            formparams.add(new BasicNameValuePair("encryptKey", encryptKey));
            formparams.add(new BasicNameValuePair("sign", sign));
            logger.info("encryptData:{}", encryptData);
            logger.info("encryptKey:{}", encryptKey);
            logger.info("sign:{}", sign);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return formparams;
    }

    public static List<NameValuePair> genEncryptData(String merchId, String transCode, String accessType, String plId,
                                                     String data) throws Exception {
        if ((null == merchId) || (null == transCode) || (null == data)) {
            logger.error("merchId or transCode or data is null");
            return null;
        }
        List<NameValuePair> formparams = new ArrayList();
        formparams.add(new BasicNameValuePair("merId", merchId));
        formparams.add(new BasicNameValuePair("transCode", transCode));
        formparams.add(new BasicNameValuePair("accessType", accessType));
        formparams.add(new BasicNameValuePair("plId", plId));
        try {
            byte[] plainBytes = data.getBytes("UTF-8");
            String aesKey = RandomStringGenerator.getRandomStringByLength(16);
            byte[] aesKeyBytes = aesKey.getBytes("UTF-8");
            String encryptData = new String(
                    Base64.encodeBase64(
                            CryptoUtil.AESEncrypt(plainBytes, aesKeyBytes, "AES", "AES/ECB/PKCS5Padding", null)),
                    "UTF-8");
            String sign = new String(
                    Base64.encodeBase64(CryptoUtil.digitalSign(plainBytes, CertUtil.getPrivateKey(), "SHA1WithRSA")),
                    "UTF-8");
            String encryptKey = new String(Base64.encodeBase64(
                    CryptoUtil.RSAEncrypt(aesKeyBytes, CertUtil.getPublicKey(), 2048, 11, "RSA/ECB/PKCS1Padding")),
                    "UTF-8");
            formparams.add(new BasicNameValuePair("encryptData", encryptData));
            formparams.add(new BasicNameValuePair("encryptKey", encryptKey));
            formparams.add(new BasicNameValuePair("sign", sign));
            logger.info("encryptData:{}", encryptData);
            logger.info("encryptKey:{}", encryptKey);
            logger.info("sign:{}", sign);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return formparams;
    }

    public static List<NameValuePair> genEncryptData(String merchId, String transCode, String accessType, String plId,
                                                     String accessPlatform, String data) throws Exception {
        if ((null == merchId) || (null == transCode) || (null == data)) {
            logger.error("merchId or transCode or data is null");
            return null;
        }
        List<NameValuePair> formparams = new ArrayList();
        formparams.add(new BasicNameValuePair("merId", merchId));
        formparams.add(new BasicNameValuePair("transCode", transCode));
        formparams.add(new BasicNameValuePair("accessType", accessType));
        formparams.add(new BasicNameValuePair("plId", plId));
        formparams.add(new BasicNameValuePair("accessPlatform", accessPlatform));
        try {
            byte[] plainBytes = data.getBytes("UTF-8");
            String aesKey = RandomStringGenerator.getRandomStringByLength(16);
            byte[] aesKeyBytes = aesKey.getBytes("UTF-8");
            String encryptData = new String(
                    Base64.encodeBase64(
                            CryptoUtil.AESEncrypt(plainBytes, aesKeyBytes, "AES", "AES/ECB/PKCS5Padding", null)),
                    "UTF-8");
            String sign = new String(
                    Base64.encodeBase64(CryptoUtil.digitalSign(plainBytes, CertUtil.getPrivateKey(), "SHA1WithRSA")),
                    "UTF-8");
            String encryptKey = new String(Base64.encodeBase64(
                    CryptoUtil.RSAEncrypt(aesKeyBytes, CertUtil.getPublicKey(), 2048, 11, "RSA/ECB/PKCS1Padding")),
                    "UTF-8");

            formparams.add(new BasicNameValuePair("encryptData", encryptData));
            formparams.add(new BasicNameValuePair("encryptKey", encryptKey));
            formparams.add(new BasicNameValuePair("sign", sign));
            logger.info("encryptData:{}", encryptData);
            logger.info("encryptKey:{}", encryptKey);
            logger.info("sign:{}", sign);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return formparams;
    }

    public static String decryptRetData(String data) throws Exception {
        Map<String, String> responseMap = convertResultStringToMap(data);
        String retEncryptKey = (String) responseMap.get("encryptKey");
        String retEncryptData = (String) responseMap.get("encryptData");
        String retSign = (String) responseMap.get("sign");
        logger.info("retEncryptKey:{}", retEncryptKey);
        logger.info("retEncryptData:{}", retEncryptData);
        logger.info("retSign:{}", retSign);
        byte[] decodeBase64KeyBytes = Base64.decodeBase64(retEncryptKey.getBytes("UTF-8"));
        byte[] merchantAESKeyBytes = CryptoUtil.RSADecrypt(decodeBase64KeyBytes, CertUtil.getPrivateKey(), 2048, 11,
                "RSA/ECB/PKCS1Padding");
        byte[] decodeBase64DataBytes = Base64.decodeBase64(retEncryptData.getBytes("UTF-8"));
        byte[] retDataBytes = CryptoUtil.AESDecrypt(decodeBase64DataBytes, merchantAESKeyBytes, "AES",
                "AES/ECB/PKCS5Padding", null);
        logger.info("retData:{}", new String(retDataBytes, "UTF-8"));
        byte[] signBytes = Base64.decodeBase64(retSign.getBytes("UTF-8"));
        boolean isValid = CryptoUtil.verifyDigitalSign(retDataBytes, signBytes, CertUtil.getPublicKey(), "SHA1WithRSA");
        if (!isValid) {
            logger.error("报文验签不通过");
            throw new Exception("报文验签不通过");
        }
        logger.info("报文验签通过");
        String ret = new String(retDataBytes, "UTF-8");
        return ret;
    }

    private static Map<String, String> convertResultStringToMap(String result) {
        Map<String, String> map = null;
        if (StringUtils.isNotBlank(result)) {
            if ((result.startsWith("\"")) && (result.endsWith("\""))) {
                if (logger.isDebugEnabled()) {
                    logger.debug("convertResultStringToMap(String) - " + result.length());
                }
                result = result.substring(1, result.length() - 1);
            }
            map = SdkUtil.convertResultStringToMap(result);
        }
        return map;
    }

    public static void encryptFile(String decryptFileName, String encryptFileName) throws Exception {
        try {
            InputStream in = new FileInputStream(new File(decryptFileName));
            FileWriter fileWriter = new FileWriter(encryptFileName);
            byte[] plainBytes = new byte[in.available()];
            in.read(plainBytes);
            String aesKey = RandomStringGenerator.getRandomStringByLength(16);
            byte[] aesKeyBytes = aesKey.getBytes("UTF-8");
            String encryptData = new String(
                    Base64.encodeBase64(
                            CryptoUtil.AESEncrypt(plainBytes, aesKeyBytes, "AES", "AES/ECB/PKCS5Padding", null)),
                    "UTF-8");
            String sign = new String(
                    Base64.encodeBase64(CryptoUtil.digitalSign(plainBytes, CertUtil.getPrivateKey(), "SHA1WithRSA")),
                    "UTF-8");
            String encryptKey = new String(Base64.encodeBase64(
                    CryptoUtil.RSAEncrypt(aesKeyBytes, CertUtil.getPublicKey(), 2048, 11, "RSA/ECB/PKCS1Padding")),
                    "UTF-8");
            logger.info("encryptData:{}", encryptData);
            logger.info("encryptKey:{}", encryptKey);
            logger.info("sign:{}", sign);
            fileWriter.write(encryptKey + "\n");
            fileWriter.write(sign + "\n");
            fileWriter.write(encryptData + "\n");
            if (in != null) {
                in.close();
            }
            if (fileWriter != null) {
                fileWriter.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static void decryptFile(String encryptFileName, String decryptFileName) throws Exception {
        try {
            BufferedReader bReader = new BufferedReader(new FileReader(encryptFileName));
            FileWriter fileWriter = new FileWriter(decryptFileName);
            String encryptKey = bReader.readLine();
            String signature = bReader.readLine();
            String encryptData = bReader.readLine();
            byte[] decodeBase64KeyBytes = Base64.decodeBase64(encryptKey.getBytes("UTF-8"));
            byte[] merchantAESKeyBytes = CryptoUtil.RSADecrypt(decodeBase64KeyBytes, CertUtil.getPrivateKey(), 2048, 11,
                    "RSA/ECB/PKCS1Padding");
            byte[] decodeBase64DataBytes = Base64.decodeBase64(encryptData.getBytes("UTF-8"));
            byte[] decryptBytes = CryptoUtil.AESDecrypt(decodeBase64DataBytes, merchantAESKeyBytes, "AES",
                    "AES/ECB/PKCS5Padding", null);
            logger.info("retData:{}", new String(decryptBytes, "UTF-8"));
            byte[] signBytes = Base64.decodeBase64(signature.getBytes("UTF-8"));
            boolean isValid = CryptoUtil.verifyDigitalSign(decryptBytes, signBytes, CertUtil.getPublicKey(),
                    "SHA1WithRSA");
            if (!isValid) {
                logger.error("报文验签不通过");
                throw new Exception("报文验签不通过");
            }
            logger.info("报文验签通过");
            String decryptData = new String(decryptBytes, "UTF-8");
            fileWriter.write(decryptData);
            if (bReader != null) {
                bReader.close();
            }
            if (fileWriter != null) {
                fileWriter.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static List<NameValuePair> getEncryptMerchData(String mid, String data, String extend) {
        if ((null == mid) || (null == data)) {
            return null;
        }
        List<NameValuePair> formParams = new ArrayList();
        formParams.add(new BasicNameValuePair("mid", mid));
        formParams.add(new BasicNameValuePair("plMid", mid));
        formParams.add(new BasicNameValuePair("extend", extend));
        try {
//            String merchPublicKeyPath = DynamicPropertyHelper.getStringProperty("sandpay.merech.public.key", "").get();
//            CertUtil.init(merchPublicKeyPath, this.privateKeyPath, this.keyPassword);
            byte[] dataBytes = data.getBytes("UTF-8");
            String aesKey = RandomStringGenerator.getRandomStringByLength(16);
            byte[] aesKeyBytes = aesKey.getBytes("UTF-8");
            String encryptData = new String(
                    Base64.encodeBase64(
                            CryptoUtil.AESEncrypt(dataBytes, aesKeyBytes, "AES", "AES/ECB/PKCS5Padding", null)),
                    "UTF-8");
            String signData = new String(
                    Base64.encodeBase64(CryptoUtil.digitalSign(dataBytes, CertUtil.getPrivateKey(), "SHA1WithRSA")),
                    "UTF-8");
            String encryptKey = new String(Base64.encodeBase64(
                    CryptoUtil.RSAEncrypt(aesKeyBytes, CertUtil.getPublicKey(), 2048, 11, "RSA/ECB/PKCS1Padding")),
                    "UTF-8");
            formParams.add(new BasicNameValuePair("encryptData", encryptData));
            formParams.add(new BasicNameValuePair("encryptKey", encryptKey));
            formParams.add(new BasicNameValuePair("sign", signData));
            logger.info("encryptData:{}", encryptData);
            logger.info("encryptKey:{}", encryptKey);
            logger.info("sign:{}", signData);
        } catch (Exception e) {
            logger.error("getEncryptMerchData(String, String, String)", e);
            return formParams;
        }
        return formParams;
    }

    public static String decryptMerchRetData(String data) throws Exception {
        logger.info("decryptMerchRetData(Map<String,String>, PublicKey) - start");
        Map<String, String> paramMap = SdkUtil.convertResultStringToMap(data);
        String encryptKey = (String) paramMap.get("encryptKey");
        String encryptData = (String) paramMap.get("encryptData");
        String signData = (String) paramMap.get("sign");
        logger.info("encryptKey:{}" + encryptKey);
        logger.info("encryptData:{}" + encryptData);
        logger.info("signData:{}" + signData);
//        String merchPublicKeyPath = DynamicPropertyHelper.getStringProperty("sandpay.merech.public.key", "").get();
//        CertUtil.init(merchPublicKeyPath, this.privateKeyPath, this.keyPassword);
        byte[] encryptKeyBytes = Base64Util.decodeBytes(encryptKey);
        byte[] keyBytes = CryptoUtil.RSADecrypt(encryptKeyBytes, CertUtil.getPrivateKey(), 2048, 11,
                "RSA/ECB/PKCS1Padding");
        byte[] decodeDataBytes = Base64Util.decodeBytes(encryptData);
        byte[] dataBytes = CryptoUtil.AESDecrypt(decodeDataBytes, keyBytes, "AES", "AES/ECB/PKCS5Padding", null);
        logger.info("dataBytes:{}" + new String(dataBytes, "UTF-8"));
        byte[] signDataBytes = Base64Util.decodeBytes(signData);
        boolean isValid = CryptoUtil.verifyDigitalSign(dataBytes, signDataBytes, CertUtil.getPublicKey(),
                "SHA1WithRSA");
        if (!isValid) {
            logger.error("报文验签不通过");
            throw new Exception("报文验签不通过");
        }
        logger.info("报文验签通过");
        String result = new String(dataBytes, "UTF-8");
        logger.info("decryptresData(Map<String,String>, PublicKey) - end");
        return result;
    }

    public static List<NameValuePair> getEncryptGateWayData(String data, String extend) {
        logger.info("getEncryptGateWayData(String, String) - start =>>{}", data);
        if (null == data) {
            return null;
        }
        List<NameValuePair> formParams = new ArrayList();
        try {
            byte[] dataBytes = data.getBytes("UTF-8");
            String signData = new String(
                    Base64.encodeBase64(CryptoUtil.digitalSign(dataBytes, CertUtil.getPrivateKey(), "SHA1WithRSA")),
                    "UTF-8");
            formParams.add(new BasicNameValuePair("charset", "UTF-8"));
            formParams.add(new BasicNameValuePair("data", data));
            formParams.add(new BasicNameValuePair("signType", "01"));
            formParams.add(new BasicNameValuePair("sign", signData));
            formParams.add(new BasicNameValuePair("extend", extend));
            logger.info("(String, String) =>>sign:{}", signData);
        } catch (Exception e) {
            logger.error("getEncryptGateWayData(String, String)", e);
            return formParams;
        }
        logger.info("getEncryptGateWayData(String, String) - end");
        return formParams;
    }

    public static String decryptGateWayRetData(String data) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("decryptGateWayRetData(String) - start");
        }
        Map<String, String> respMap = SdkUtil.convertResultStringToMap(data);
        String respData = (String) respMap.get("data");
        logger.info("decryptGateWayRetData(String) =>>respData:{}" + respData);
        String respSign = (String) respMap.get("sign");
//        CertUtil.init(this.publicKeyPath, this.privateKeyPath, this.keyPassword);
        byte[] respDataBytes = respData.getBytes("UTF-8");
        byte[] signDataBytes = Base64.decodeBase64(respSign);
        boolean isValid = CryptoUtil.verifyDigitalSign(respDataBytes, signDataBytes, CertUtil.getPublicKey(),
                "SHA1WithRSA");
        if (!isValid) {
            logger.error("报文验签不通过");
            throw new Exception("报文验签不通过");
        }
        logger.info("报文验签通过");
        if (logger.isDebugEnabled()) {
            logger.debug("decryptGateWayRetData(String) - end");
        }
        return respData;
    }

    /**
     * 测试，检验发送的数据
     */
    public static boolean decryptStoreSendData(String respData, String respSign) throws Exception {
        byte[] respDataBytes = respData.getBytes("UTF-8");
        byte[] signDataBytes = Base64.decodeBase64(respSign);
        //
        final Signature signature = Signature.getInstance("SHA1WithRSA");
        signature.initVerify(CertUtil.getStorePublicKey());
        signature.update(respDataBytes);
        boolean isValid = signature.verify(signDataBytes);
        if (!isValid) {
            logger.error("报文验签不通过");
            throw new Exception("报文验签不通过");
        }
        logger.info("报文验签通过");
        if (logger.isDebugEnabled()) {
            logger.debug("decryptGateWayRetData(String) - end");
        }
        return isValid;
    }
}
