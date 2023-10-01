package com.muling.mall.pms.config;

import com.muling.mall.pms.common.constant.PmsConstants;
import com.muling.mall.pms.component.BloomRedisService;
import com.muling.mall.pms.pojo.entity.PmsSpu;
import com.muling.mall.pms.service.IPmsSpuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class SpuFilterConfig {

    private final IPmsSpuService spuService;

    private final BloomRedisService bloomRedisService;

    @PostConstruct
    public void afterPropertiesSet() {
        List<PmsSpu> list = spuService.listAll();
        log.info("加载产品到布隆过滤器当中,size:{}", list.size());
        if (!CollectionUtils.isEmpty(list)) {
            list.stream().filter(item -> item.getId() > 0).forEach(item -> {
                bloomRedisService.addByBloomFilter(PmsConstants.GOODS_BLOOM_FILTER, item.getId() + "");
            });
        }
    }
}
