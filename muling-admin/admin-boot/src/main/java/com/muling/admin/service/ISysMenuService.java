package com.muling.admin.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.muling.admin.pojo.entity.SysMenu;
import com.muling.admin.pojo.vo.menu.MenuVO;
import com.muling.admin.pojo.vo.menu.RouteVO;
import com.muling.common.web.domain.OptionVO;

import java.util.List;

/**
 * @author <a href="mailto:xianrui0365@163.com">xianrui</a>
 * @date 2020-11-06
 */
public interface ISysMenuService extends IService<SysMenu> {


    /**
     * 菜单表格（Table）层级列表
     *
     * @param name 菜单名称
     * @return
     */
    List<MenuVO> listTable(String name);


    /**
     * 获取菜单下拉列表
     *
     * @return
     */
    public List<OptionVO> listMenuOptions();


    /**
     * 获取路由列表
     *
     * @return
     */
    List<RouteVO> listRoutes();

    /**
     * 新增菜单
     *
     * @param menu
     * @return
     */
    boolean saveMenu(SysMenu menu);


    /**
     * 修改菜单
     *
     * @param menu
     * @return
     */
    boolean updateMenu(SysMenu menu);

    /**
     * 清理路由缓存
     */
    void cleanCache();

    /**
     * 修改菜单显示状态
     *
     * @param menuId 菜单ID
     * @param visible 是否显示(1->显示；2->隐藏)
     * @return
     */
    boolean updateMenuVisible(Long menuId, Integer visible);
}
