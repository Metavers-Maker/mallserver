package com.muling.mall.bms;

import com.muling.mall.pms.api.SkuFeignClient;
import com.muling.mall.pms.api.SpuFeignClient;
import com.muling.mall.ums.api.MemberFeignClient;
import com.muling.mall.wms.api.WalletFeignClient;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@EnableDiscoveryClient
@EnableFeignClients(basePackageClasses = {MemberFeignClient.class, SkuFeignClient.class, SpuFeignClient.class, WalletFeignClient.class})
@EnableRabbit
@EnableTransactionManagement
@EnableScheduling
public class BmsApplication {

	public static void main(String[] args) {
		SpringApplication.run(BmsApplication.class, args);
	}

}
