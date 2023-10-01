package com.muling.admin;

import com.muling.mall.oms.api.OrderFeignClient;
import com.muling.mall.pms.api.SkuFeignClient;
import com.muling.mall.pms.api.SpuFeignClient;
import com.muling.mall.ums.api.MemberFeignClient;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableDiscoveryClient
@EnableRabbit
@EnableTransactionManagement
@EnableScheduling
@EnableFeignClients(basePackageClasses = {OrderFeignClient.class, SkuFeignClient.class, MemberFeignClient.class})
public class AdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(AdminApplication.class, args);
    }
}
