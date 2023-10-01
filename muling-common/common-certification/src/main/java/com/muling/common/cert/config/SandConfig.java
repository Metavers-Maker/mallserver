package com.muling.common.cert.config;

import com.muling.common.cert.util.sand.CertUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.File;

@Configuration
public class SandConfig {

    // 外部找不到读默认的
    public static final String CLASSPATH_CONFIG_CERTS_SAND_CER = "classpath:certs/sand.cer";
    public static final String CLASSPATH_CONFIG_CERTS_PRI_KEY = "classpath:certs/prikey.pfx";
    public static final String CLASSPATH_CONFIG_CERTS_STORE_KEY = "classpath:certs/6888806110476.cer";
    //杉德颁发的公钥
    @Value("${sandpay.public.key:/root/appservice/config/certs/sand.cer}")
    private String publicKeyPath;

    //商户颁发的私钥（公钥需要在杉德后台配置）
    @Value("${sandpay.private.key:/root/appservice/config/certs/6888806110476.pfx}")
    private String privateKeyPath;

    //商户颁发的私钥密码（公钥需要在杉德后台配置）
    @Value("${sandpay.private.key.password:Link2meta123}")
    private String password;

    @Value("${sandpay.mid:6888806110476}")
    private String mid;

    @Value("${sand.host:https://cashier.sandpay.com.cn}")
    private String host;

    public String getPublicKeyPath() {
        return publicKeyPath;
    }

    public SandConfig setPublicKeyPath(String publicKeyPath) {
        this.publicKeyPath = publicKeyPath;
        return this;
    }

    public String getPrivateKeyPath() {
        return privateKeyPath;
    }

    public SandConfig setPrivateKeyPath(String privateKeyPath) {
        this.privateKeyPath = privateKeyPath;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public SandConfig setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getMid() {
        return mid;
    }

    public SandConfig setMid(String mid) {
        this.mid = mid;
        return this;
    }

    public String getHost() {
        return host;
    }

    public SandConfig setHost(String host) {
        this.host = host;
        return this;
    }

    @PostConstruct
    public void init() throws Exception {
        File file1 = new File(publicKeyPath);
        File file2 = new File(privateKeyPath);
        if (!file1.exists() || !file2.exists()) {
            CertUtil.init(CLASSPATH_CONFIG_CERTS_SAND_CER, CLASSPATH_CONFIG_CERTS_PRI_KEY, password);
            CertUtil.initStorePulbicKey(CLASSPATH_CONFIG_CERTS_STORE_KEY);
        } else {
            CertUtil.init(publicKeyPath, privateKeyPath, password);
        }
    }
}
