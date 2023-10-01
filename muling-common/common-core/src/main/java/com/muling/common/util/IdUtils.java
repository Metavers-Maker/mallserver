package com.muling.common.util;

import cn.hutool.core.util.RandomUtil;
import org.apache.commons.codec.digest.DigestUtils;

public class IdUtils {

    //重复需要替换
    @Deprecated
    public static String makeId() {
        long number = System.currentTimeMillis() - 1640966400000L;//2022-01-01 00:00:00
        String uid = String.format("%x", number);
        return uid;
    }

    public static String make64NanoId() {
        String hash = DigestUtils.md5Hex(String.format("%064x", System.nanoTime()));
        return hash;
    }

    public static String makeOrderId(Long memberId, String prefix) {
        // 用户id前补零保证五位，对超出五位的保留后五位
        String filledZero = String.format("%05d", memberId);
        String interceptPreFive = filledZero.substring(filledZero.length() - 5);
        // 在前面加上wxo（weixin order）等前缀是为了人工可以快速分辨订单号是下单还是退款、来自哪家支付机构等 将时间戳+3位随机数+五位id组成商户订单号，规则参考自<a href="https://tech.meituan.com/2016/11/18/dianping-order-db-sharding.html">大众点评</a>
        String out_refund_no = prefix + System.currentTimeMillis() + RandomUtil.randomNumbers(3) + interceptPreFive;
        return out_refund_no;
    }

    public static String[] ALPHANUMERIC_SET = {
//            "0",
            "1", "2", "3", "4", "5", "6", "7", "8", "9",
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K",
//            "L",
            "M", "N",
//            "O",
            "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z",
            "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k",
//            "l",
            "m", "n",
//            "o",
            "p", "r", "s", "t", "u", "v", "w", "x", "y", "z"};

    public static Integer PRIME1 = 3;// 与字符集长度 62 互质
    public static Integer PRIME2 = 5; // 与邀请码长度 6 互质
    public static Integer SALT = 123456789; // 随意取一个数值

    /**
     * makeCodeByUIDUnique 获取指定长度的邀请码。
     */
    public static String makeCodeByUIDUnique(Long uid, int l) {
//        //更改生成邀请码的算法
//        String source_string = "01234567891ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnoprstuvwxyz";
//        Long num = 100000000000l + uid;
//        StringBuilder code = new StringBuilder();
//        while (num > 0) {
//            int mod = (int)Math.floorMod(num,35l);
//            num = (num - mod) / 35;
//            String code_single = source_string.substring(mod, 1);
//            code.append(code_single);
//        }
//        return code.toString();
        //
        StringBuilder code = new StringBuilder();
        byte[] bytes = new byte[l];
        int length = ALPHANUMERIC_SET.length;
        for (int i = 0; i < l; i++) {
            bytes[i] = (byte) (uid % length);// 获取 62 进制的每一位值
            byte idx = (byte) ((bytes[i] + i * bytes[0]) % length); // 其他位与个位加和再取余（让个位的变化影响到所有位）
            code.append(ALPHANUMERIC_SET[idx]);
            uid = uid / length; // 相当于右移一位（62进制）
        }
        return code.toString();
    }

    /**
     * makeByUidUniqueNew 获取指定长度的邀请码。
     */
    public static String makeCodeByUidUniqueNew(Long uid, int l) {
        // 放大 + 加盐。
        uid = uid * PRIME1 + SALT;

        StringBuilder code = new StringBuilder();
        byte[] bytes = new byte[l];
        int length = ALPHANUMERIC_SET.length;

        // 扩散。
        for (byte i = 0; i < l; i++) {
            bytes[i] = (byte) (uid % length);                  // 获取 62 进制的每一位值
            bytes[i] = (byte) ((bytes[i] + i * bytes[0]) % (byte) length); // 其他位与个位加和再取余（让个位的变化影响到所有位）
            uid = uid / length;                            // 相当于右移一位（62进制）
        }

        // 混淆。
        for (byte i = 0; i < l; i++) {
            byte idx = (byte) ((i * PRIME2) % l);
            code.append(ALPHANUMERIC_SET[bytes[idx]]);
        }
        return code.toString();
    }

    public static void main(String[] args) {
        System.out.println(IdUtils.makeOrderId(2222L, "wxo"));

        Long s = 1L;
        for (int i = 0; i < 100; i++) {
//            System.out.println(IdUtils.makeCodeByUidUniqueNew(s + i, 8));
        }
//        Long old = 425L;
//        Long n = 873L;
        Long old = 481L;
        Long n = 929L;
        System.out.println(IdUtils.makeCodeByUidUniqueNew(old, 8));
        System.out.println(IdUtils.makeCodeByUidUniqueNew(n, 8));
        System.out.println(IdUtils.makeCodeByUIDUnique(old, 8));
        System.out.println(IdUtils.makeCodeByUIDUnique(n, 8));
    }
}
