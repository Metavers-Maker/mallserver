package com.muling.common.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.*;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.util.Arrays;

/**
 * 以太坊签名消息校验工具
 */
public class CryptoUtils {
    public static Logger logger = LoggerFactory.getLogger(CryptoUtils.class);

    /**
     * 以太坊自定义的签名消息都以以下字符开头
     * 参考 eth_sign in https://github.com/ethereum/wiki/wiki/JSON-RPC
     */
    public static final String PERSONAL_MESSAGE_PREFIX = "\u0019Ethereum Signed Message:\n";

    /**
     * 校验以太坊签名是否合法
     * 先通过 signature 解码出 v，r，s 签名数据，再求出公钥，再求出地址并校验
     *
     * @param from      发送者地址
     * @param resource  被签名的原数据
     * @param signature 签名
     * @return match
     */
    public static boolean verifySignature(String from, String resource, String signature) {
        // The sign method calculates an Ethereum specific signature with:
        //    sign(keccak256("\x19Ethereum Signed Message:\n" + len(message) + message))).
        // By adding a prefix to the message makes the calculated signature recognisable as an Ethereum specific signature.
        // This prevents misuse where a malicious DApp can sign arbitrary data (e.g. transaction) and use the signature to
        // impersonate the victim.
        String prefix = PERSONAL_MESSAGE_PREFIX + resource.length();
        byte[] msgHash = Hash.sha3((prefix + resource).getBytes());

        byte[] signatureBytes = Numeric.hexStringToByteArray(signature);
        //取出v
        byte v = signatureBytes[64];
        if (v < 27) {
            v += 27;
        }
        //取出r,s,和v一起可以求出公钥
        Sign.SignatureData sd = new Sign.SignatureData(
                v,
                Arrays.copyOfRange(signatureBytes, 0, 32),
                Arrays.copyOfRange(signatureBytes, 32, 64));

        String addressRecovered = null;
        boolean match = false;
        // 遍历recId
        for (int i = 0; i < 4; i++) {
            //计算公钥
            BigInteger publicKey = Sign.recoverFromSignature(
                    (byte) i,
                    new ECDSASignature(new BigInteger(1, sd.getR()), new BigInteger(1, sd.getS())),
                    msgHash);
            if (publicKey != null) {
                //通过公钥计算地址
                addressRecovered = "0x" + Keys.getAddress(publicKey);
                if (addressRecovered.equals(from)) {
                    match = true;
                    break;
                }
            }
        }
        logger.info("verify:" + resource + " : " + signature + " ==> " + addressRecovered);
        return match;
    }

    public static void main(String[] args) {
        //签名后的数据
        String signature = "0xb319edba9d9d8c12a83d3a6e2a072fa5813dd8a5e9460ac3a475ab40a9988ddd059eed15f8c748a2b18c303b2a290a40c464b0a72274dfe1a96820bb02617b4f1b";
        //签名原文
        String message = "123456";
        //签名的钱包地址
        String address = "0xc9fa719138a0d8fec944ed2bdc6d191e3ef08721";

        boolean validAddress = WalletUtils.isValidAddress(address);
        if (validAddress) {
            Boolean result = CryptoUtils.verifySignature(address, message, signature);
            System.out.println(result);
        }
    }
}
