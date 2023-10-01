package com.muling.common.auth;

/**
 * Created by Given on 2021/12/8
 */

public class AuthTest {
    /** 当测试authTest时候，把genSecretTest生成的secret值赋值给它  */
    private static String secret="R2Q3S52RNXBTFTOM";

    public static void genSecretTest() {// 生成密钥
        secret = GoogleAuthenticator.generateSecretKey();
        // 把这个qrcode生成二维码，用google身份验证器扫描二维码就能添加成功
        String qrcode = GoogleAuthenticator.getQRBarcode("fdas@fd.com", secret);
        System.out.println("qrcode:" + qrcode + ",key:" + secret);
    }

    public static void main(String[] args) {
		 genSecretTest();
        int code = 503042;
        long t = System.currentTimeMillis();
        GoogleAuthenticator ga = new GoogleAuthenticator();
        //should give 5 * 30 seconds of grace...
        ga.setWindowSize(5);
        boolean r = ga.checkCode("7IXYNJPLSMSZYQER", code, t);
        System.out.println("检查code是否正确？" + r);
    }
}
