package com.muling.mall.oms;

import com.muling.mall.bms.api.ItemFeignClient;
import com.muling.mall.bms.api.MarketFeignClient;
import com.muling.mall.pms.api.SkuFeignClient;
import com.muling.mall.pms.api.SpuFeignClient;
import com.muling.mall.ums.api.MemberAuthFeignClient;
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
@EnableFeignClients(basePackageClasses = {MemberFeignClient.class, MemberAuthFeignClient.class, SkuFeignClient.class, SpuFeignClient.class, WalletFeignClient.class, ItemFeignClient.class, MarketFeignClient.class})
@EnableTransactionManagement
public class OmsApplication {
    public static void main(String[] args) {
        SpringApplication.run(OmsApplication.class, args);
    }
}
