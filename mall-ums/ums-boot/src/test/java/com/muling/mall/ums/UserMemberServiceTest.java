package com.muling.mall.ums;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Lists;
import com.muling.common.constant.RedisConstants;
import com.muling.mall.ums.event.MemberAuthSuccessEvent;
import com.muling.mall.ums.mapper.UmsMemberAuthMapper;
import com.muling.mall.ums.mapper.UmsMemberInviteMapper;
import com.muling.mall.ums.pojo.entity.UmsMember;
import com.muling.mall.ums.pojo.entity.UmsMemberAuth;
import com.muling.mall.ums.pojo.entity.UmsMemberInvite;
import com.muling.mall.ums.pojo.form.BankBindEnsureForm;
import com.muling.mall.ums.pojo.form.BankBindForm;
import com.muling.mall.ums.pojo.form.BankUnbindForm;
import com.muling.mall.ums.pojo.vo.BankBindVO;
import com.muling.mall.ums.service.*;
import com.muling.mall.wms.api.WalletFeignClient;
import com.muling.mall.wms.enums.WalletOpTypeEnum;
import com.muling.mall.wms.pojo.dto.WalletDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

/**
 * Created by Given on 2021/12/6
 */
@SpringBootTest
@Slf4j
public class UserMemberServiceTest {

    //    @Resource
//    private CertService certService;
    @Resource
    private IUmsMemberAuthLogService memberAuthLogService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private UmsMemberAuthMapper memberAuthMapper;

    @Resource
    private IUmsMemberService umsMemberService;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private IUmsMemberAuthService memberAuthService;

    @Resource
    private IUmsAccountChainService accountChainService;

    @Resource
    private IAuthService authService;

    @Resource
    private IUmsRelationService relationService;

    @Resource
    private IUmsBankService bankService;

    @Resource
    private ISandAccountService sandAccountService;

    @Test
    void authTest() {
        stringRedisTemplate.opsForValue().set(RedisConstants.UMS_AUTH_SUFFIX + 2L, "1");
    }

    @Test
    void genAddrTest() {
        relationService.rankByCoin(1l, 10l, 0);
//        accountChainService.genBsnAccount();
    }

    @Test
    void test() {
        bankService.listBank(0);
//        umsMemberService.checkTradePassword("eeeeee");
//        ApiResponse response = certService.certSyncMode("王守政", "131126197802036015");
//        String content = new String(response.getBody(), SdkConstant.CLOUDAPI_ENCODING);
//        log.info("实名认证返回结果：{} {} ", response.getCode(), content);
//        String result = response.getCode() + " " + response.getMessage();
//        UmsMemberAuthLog entity = new UmsMemberAuthLog()
//                .setMemberId(2L)
//                .setRequest("{\"name\":\"王守政\",\"idCard\":\"131126197802036015\"}")
//                .setResponse(result);
//        memberAuthLogService.save(entity);
//        log.error("调用认证服务失败：{} {}", entity.getRequest(), result);
//        DeviceForm deviceForm = new DeviceForm();
//        deviceForm.setDeviceUuid("1234567890");
//        deviceForm.setChannel("xiaomi");
//        iUmsDeviceService.addDevice(deviceForm);

//        System.out.println(iUmsDeviceService.list(new Page<>(1, 10), "omi",null).getRecords());


    }

    @Test
    void sss() {

        boolean exists = memberAuthMapper.exists(new LambdaQueryWrapper<UmsMemberAuth>()
                .eq(UmsMemberAuth::getIdCard, "130302197707223516")
                .or()
                .eq(UmsMemberAuth::getMemberId, 11L)
        );

        System.out.println(exists);
    }

    @Resource
    private WalletFeignClient walletFeignClient;

    @Test
    void bbbb() {

        WalletDTO invite = new WalletDTO()
                .setMemberId(2L)
                .setCoinType(0)
                .setBalance(new BigDecimal(150))
                .setOpType(WalletOpTypeEnum.INVITE_TO_REGISTER_REWARD.getValue())
                .setRemark("注册邀请");
        WalletDTO invitee = new WalletDTO()
                .setMemberId(1L)
                .setCoinType(0)
                .setBalance(new BigDecimal(150))
                .setOpType(WalletOpTypeEnum.INVITE_TO_REGISTER_REWARD.getValue())
                .setRemark("注册被邀请");
        List<WalletDTO> list = Lists.newArrayList(invite, invitee);
        walletFeignClient.updateBalances(list);
    }

    @Resource
    private IUmsMemberInviteService umsMemberInviteService;

    @Test
    void cccc() {

        boolean dfdasfdas = umsMemberInviteService.setInviteCode(2L, "dfdasfdas");
        System.out.println(dfdasfdas);
    }

    @Test
    void dddd() {
        UmsMember member = umsMemberService.getById(4547L);

        MemberAuthSuccessEvent event = new MemberAuthSuccessEvent().setMember(member);
        memberAuthService.authReward(event);
    }

    @Resource
    private IUmsMemberInviteService memberInviteService;

    @Test
    void ffff() throws Exception {
        sandAccountService.createSandAccount("192.168.11.42", 1l);
//        BankBindForm from = new BankBindForm();
//        from.setMobile("13810317769");
//        from.setCardNo("6217000010077536700");
//        from.setCardType("1");
//        BankBindVO bindVO = bankService.bindCard(from,1l);
//        int a = 0;

//        BankBindEnsureForm ensureform = new BankBindEnsureForm();
//        ensureform.setMobile("13810317769");
//        ensureform.setCode("608297");
//        ensureform.setSdMsgNo("2023060600029661000170680311308");
//        bankService.bindCardEnsure(ensureform,1l);
    }

    @Resource
    private UmsMemberInviteMapper memberInviteMapper;

    @Test
    void iiii() {
        UmsMemberInvite exist = memberInviteMapper.getOneByInviteCode("Zs81VARF");
    }

    public static void main(String[] args) {
//        Multiset<String> set = HashMultiset.create();
//        set.add("1");
//        set.add("1");
//        for (long i = 1; i < 50000000; i++) {
//            String s = InviteCodeUtil.gen(i);
////            String s = IdUtils.makeCodeByUidUniqueNew(i, 8);
//            set.add(s);
//        }
//
//        //获取所有单词 Set不可重复
//        Set<String> letters = set.elementSet();
//        for (String temp : letters) {
////            System.out.println(temp + "-->" + set.count(temp));
//            if (set.count(temp) > 1) {
//                System.out.println(temp);
//            }
//        }
    }

}
