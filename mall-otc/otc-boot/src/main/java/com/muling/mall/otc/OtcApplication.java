package com.muling.mall.otc;

import com.muling.mall.pms.api.SkuFeignClient;
import com.muling.mall.pms.api.SpuFeignClient;
import com.muling.mall.ums.api.MemberFeignClient;
import com.muling.mall.wms.api.WalletFeignClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@EnableDiscoveryClient
@EnableFeignClients(basePackageClasses = {MemberFeignClient.class, SkuFeignClient.class, SpuFeignClient.class, WalletFeignClient.class})
@EnableTransactionManagement
public class OtcApplication {
    public static void main(String[] args) {
        SpringApplication.run(OtcApplication.class, args);
    }
}
