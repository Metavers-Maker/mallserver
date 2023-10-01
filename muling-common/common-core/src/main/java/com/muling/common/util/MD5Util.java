package com.muling.common.util;

import cn.hutool.core.util.RandomUtil;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;

/**
 * Created by Given on 2021/12/6
 */
public class MD5Util {
    //
    public static String encodeMD5(String input) {
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        try {
            md5.update(input.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        StringBuilder sb = new StringBuilder();
        for (byte b : md5.digest()) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    public static String encodeSaltMD5(String input, String salt) {
        return encodeMD5(input + salt);
    }

    public static void main(String[] args) {
        //System.out.println(MD5Util.encodeMD5("8ac193eb2f0af222012f103eb3512c0eapi_key2f0af222012faa8975782c0d103eb351cm_flagyserver_idgs_nba3_ftx_gameservertimestamp2010-04-22 12:12:12user_id110"));
//		String s="gamefy_2047540|1305615686625|%W%&X1MY@C%D";
        System.out.println(MD5Util.encodeMD5("654321"));

        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        System.out.println(sf.format(1305860993927l));

        System.out.println(MD5Util.encodeMD5("123456"));

        System.out.println(MD5Util.encodeSaltMD5("123456", "hvewrg"));

        System.out.println(RandomUtil.randomString(6));

    }
}
