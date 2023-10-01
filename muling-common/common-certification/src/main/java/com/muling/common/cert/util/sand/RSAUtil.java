package com.muling.common.cert.util.sand;

import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.Signature;
import java.text.SimpleDateFormat;
import java.util.*;

public class RSAUtil {

    public static String getSignContent(Map<String, String> params) {
        StringBuilder content = new StringBuilder();
        List<String> keys = new ArrayList<>(params.keySet());
        Collections.sort(keys);
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key);
            if (key != null && !key.isEmpty() && value != null && !value.isEmpty()) {
                content.append(i == 0 ? "" : "&").append(key).append("=").append(value);
            }
        }
        return content.toString();
    }

    public static String sign(String content, PrivateKey privateKey) {
        try {
            Signature signature = Signature.getInstance("SHA1WithRSA");
            signature.initSign(privateKey);
            signature.update(content.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(signature.sign());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static String createTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        Calendar calendar = Calendar.getInstance();
        String createTime = sdf.format(calendar.getTime());
        return createTime;
    }

    public static String endTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 3);
        String endTime = sdf.format(calendar.getTime());
        return endTime;
    }

    public static String getCustomerOrderNo() {
        Random rand = new Random();
        int num = rand.nextInt(100) + 1;
        return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + num;
    }
}
