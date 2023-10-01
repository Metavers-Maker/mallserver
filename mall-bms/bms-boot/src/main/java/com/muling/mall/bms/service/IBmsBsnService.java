package com.muling.mall.bms.service;

public interface IBmsBsnService {

    /**
     * 铸造上链
     */
    public boolean mintById(Long id);

    /**
     * 批量铸造
     */
    public boolean mintBySpu(Long spuId);

    /**
     * 铸造更新
     */
    public boolean mintUpdateById(Long id);

    /**
     * 铸造查询
     */
    public boolean mintQueryById(Long id);

    /**
     * 转移查询
     */
    public boolean transQueryById(Long id);
    /**
     * 转移执行
     */
    public void execTransSchedule();

}
