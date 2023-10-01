package com.muling.mall.pms.mapper;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.muling.mall.pms.pojo.entity.PmsSpu;
import com.muling.mall.pms.pojo.query.SpuPageQuery;
import com.muling.mall.pms.service.IPmsSpuService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Given on 2021/12/6
 */
@SpringBootTest
@Slf4j
public class PmsSpuMapperTest {

    @Resource
    private IPmsSpuService pmsSpuService;

    @Resource
    private PmsSpuMapper pmsSpuMapper;

    @Test
    void listTest() {
        List<PmsSpu> pmsSpus = pmsSpuMapper.selectList(Wrappers.emptyWrapper());
        System.out.println(pmsSpus);
    }

    @Test
    void logicDeleteTest() {
        List<Long> ss = new ArrayList<>();
        ss.add(20230525600000004L);
        pmsSpuService.getAppSpuDetails(ss);
    }

    @Test
    void deleteTest() {
        SpuPageQuery spuPageQuery = new SpuPageQuery();
        spuPageQuery.setPageNum(1);
        spuPageQuery.setPageSize(10);
        pmsSpuService.pageSpuDetails(spuPageQuery);
    }

    @Test
    void mintGoodsTest() {
//        pmsSpuService.mintGoods("BSN",20230525600000004L);
//        pmsSpuService.queryGoods("BSN",20230525600000004L);
    }

}
