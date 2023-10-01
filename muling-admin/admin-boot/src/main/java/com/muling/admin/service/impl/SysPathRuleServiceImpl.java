package com.muling.admin.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.muling.admin.converter.PathRuleConverter;
import com.muling.admin.mapper.SysPathRuleMapper;
import com.muling.admin.pojo.entity.SysPathRule;
import com.muling.admin.pojo.form.PathRuleForm;
import com.muling.admin.service.ISysPathRuleService;
import com.muling.common.constant.SecurityConstants;
import com.muling.common.exception.BizException;
import com.muling.common.result.ResultCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 业务类
 */
@Service
@RequiredArgsConstructor
public class SysPathRuleServiceImpl extends ServiceImpl<SysPathRuleMapper, SysPathRule> implements ISysPathRuleService {

    private final RedisTemplate redisTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(PathRuleForm pathRuleForm) {
        // 实体对象转换 form->entity
        SysPathRule entity = PathRuleConverter.INSTANCE.form2Entity(pathRuleForm);
        // 持久化
        boolean result = this.save(entity);
        if (result) {
            String data = JSONUtil.createObj().set("type", entity.getType()).set("value", entity.getValue()).toString();
            redisTemplate.opsForValue().set(SecurityConstants.PATH_PREM_RULE_PREFIX + entity.getPath(), data);
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(Long id, PathRuleForm pathRuleForm) {
        SysPathRule entity = this.getById(id);
        if (entity == null) {
            throw new BizException(ResultCode.SYSTEM_EXECUTION_ERROR);
        }
        PathRuleConverter.INSTANCE.updatePo(pathRuleForm, entity);
        boolean b = updateById(entity);
        if (b) {
            String data = JSONUtil.createObj().set("type", entity.getType()).set("value", entity.getValue()).toString();
            redisTemplate.opsForValue().set(SecurityConstants.PATH_PREM_RULE_PREFIX + entity.getPath(), data);
        }
        return b;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(Long id) {
        SysPathRule pathRule = getById(id);
        boolean b = removeById(id);
        if (b) {
            redisTemplate.delete(SecurityConstants.PATH_PREM_RULE_PREFIX + pathRule.getPath());
        }
        return b;
    }
}
