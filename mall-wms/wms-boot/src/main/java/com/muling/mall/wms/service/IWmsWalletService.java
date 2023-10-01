package com.muling.mall.wms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.muling.mall.wms.pojo.dto.WalletDTO;
import com.muling.mall.wms.pojo.entity.WmsWallet;
import com.muling.mall.wms.pojo.form.admin.WalletForm;
import com.muling.mall.wms.pojo.form.app.SwapForm;
import com.muling.mall.wms.pojo.query.app.WalletPageQuery;
import com.muling.mall.wms.pojo.vo.WalletVO;

import java.math.BigDecimal;
import java.util.List;

public interface IWmsWalletService extends IService<WmsWallet> {

    public IPage<WalletVO> page(WalletPageQuery queryParams);

    public boolean save(WalletForm walletForm);

    /**
     * 更新余额(数量和费用分别传入，底层会自动计算出总金额)
     *
     * @param walletDTO
     * @return
     */
    public boolean updateBalance(WalletDTO walletDTO);

    public boolean updateBalances(List<WalletDTO> list);

    public WmsWallet getCoinByMemberIdAndCoinType(Long memberId, Integer coinType);

    public WalletVO swap(SwapForm swapForm);

    public BigDecimal total(Integer coinType);

    public Boolean feng(Long memberId, Integer status);
}
