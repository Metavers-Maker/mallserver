package com.muling.admin.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.muling.admin.constant.SystemConstants;
import com.muling.admin.converter.MenuConverter;
import com.muling.admin.mapper.SysMenuMapper;
import com.muling.admin.pojo.entity.SysMenu;
import com.muling.admin.pojo.vo.menu.MenuVO;
import com.muling.admin.pojo.vo.menu.RouteVO;
import com.muling.admin.service.ISysMenuService;
import com.muling.admin.service.ISysPermissionService;
import com.muling.common.constant.GlobalConstants;
import com.muling.common.web.domain.OptionVO;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * 菜单业务类
 *
 * @author <a href="mailto:xianrui0365@163.com">xianrui</a>
 * @date 2020-11-06
 */
@Service
@RequiredArgsConstructor
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements ISysMenuService {

    private final ISysPermissionService permissionService;

    /**
     * 菜单表格（Table）层级列表
     *
     * @param name 菜单名称
     * @return
     */
    @Override
    public List<MenuVO> listTable(String name) {
        List<SysMenu> menuList = this.list(
                new LambdaQueryWrapper<SysMenu>()
                        .like(StrUtil.isNotBlank(name), SysMenu::getName, name)
                        .orderByAsc(SysMenu::getSort)
        );
        return recursion(menuList);
    }

    /**
     * 递归生成菜单表格层级列表
     *
     * @param menuList 菜单列表
     * @return 菜单列表
     */
    private static List<MenuVO> recursion(List<SysMenu> menuList) {
        List<MenuVO> menuTableList = new ArrayList<>();
        // 保存所有节点的 id
        Set<Long> nodeIdSet = menuList.stream()
                .map(SysMenu::getId)
                .collect(Collectors.toSet());
        for (SysMenu sysMenu : menuList) {
            // 不在节点 id 集合中存在的 id 即为顶级节点 id, 递归生成列表
            Long parentId = sysMenu.getParentId();
            if (!nodeIdSet.contains(parentId)) {
                menuTableList.addAll(recursionTableList(parentId, menuList));
                nodeIdSet.add(parentId);
            }
        }
        // 如果结果列表为空说明所有的节点都是独立分散的, 直接转换后返回
        if (menuTableList.isEmpty()) {
            return menuList.stream()
                    .map(item -> {
                        MenuVO menuVO = MenuConverter.INSTANCE.entity2VO(item);
//                        MenuVO menuVO = new MenuVO();
//                        BeanUtil.copyProperties(item, menuVO);
                        return menuVO;
                    })
                    .collect(Collectors.toList());
        }
        return menuTableList;
    }

    /**
     * 递归生成菜单表格层级列表
     *
     * @param parentId 父级ID
     * @param menuList 菜单列表
     * @return
     */
    private static List<MenuVO> recursionTableList(Long parentId, List<SysMenu> menuList) {
        List<MenuVO> menuTableList = new ArrayList<>();
        Optional.ofNullable(menuList).orElse(new ArrayList<>())
                .stream()
                .filter(menu -> menu.getParentId().equals(parentId))
                .forEach(menu -> {
                    MenuVO menuVO = MenuConverter.INSTANCE.entity2VO(menu);
//                    MenuVO menuVO = new MenuVO();
//                    BeanUtil.copyProperties(menu, menuVO);
                    List<MenuVO> children = recursionTableList(menu.getId(), menuList);

                    if (CollectionUtil.isNotEmpty(children)) {
                        menuVO.setChildren(children);
                    }
                    menuTableList.add(menuVO);
                });
        return menuTableList;
    }


    /**
     * 获取菜单下拉列表
     *
     * @return
     */
    @Override
    public List<OptionVO> listMenuOptions() {
        List<SysMenu> menuList = this.list(new LambdaQueryWrapper<SysMenu>().orderByAsc(SysMenu::getSort));
        List<OptionVO> menuSelectList = recursionSelectList(SystemConstants.ROOT_MENU_ID, menuList);
        return menuSelectList;
    }


    /**
     * 递归生成菜单下拉层级列表
     *
     * @param parentId 父级ID
     * @param menuList 菜单列表
     * @return
     */
    private static List<OptionVO> recursionSelectList(Long parentId, List<SysMenu> menuList) {
        List<OptionVO> menuSelectList = new ArrayList<>();
        Optional.ofNullable(menuList).orElse(new ArrayList<>())
                .stream()
                .filter(menu -> menu.getParentId().equals(parentId))
                .forEach(menu -> {
                    OptionVO optionVO = new OptionVO(menu.getId(), menu.getName());
                    List<OptionVO> children = recursionSelectList(menu.getId(), menuList);
                    if (CollectionUtil.isNotEmpty(children)) {
                        optionVO.setChildren(children);
                    }
                    menuSelectList.add(optionVO);
                });
        return menuSelectList;
    }


    /**
     * 获取路由列表
     * <p>
     * 读多写少，缓存至Redis
     *
     * @return
     * @Cacheable cacheNames:缓存名称，不同缓存的数据是彼此隔离； key: 缓存Key。
     */
    @Override
    @Cacheable(cacheNames = "system", key = "'routes'")
    public List<RouteVO> listRoutes() {
        List<SysMenu> menuList = this.baseMapper.listRoutes();
        List<RouteVO> list = recursionRoute(SystemConstants.ROOT_MENU_ID, menuList);
        return list;
    }


    /**
     * 递归生成菜单路由层级列表
     *
     * @param parentId 父级ID
     * @param menuList 菜单列表
     * @return
     */
    private List<RouteVO> recursionRoute(Long parentId, List<SysMenu> menuList) {
        List<RouteVO> list = new ArrayList<>();
        Optional.ofNullable(menuList).ifPresent(menus -> menus.stream().filter(menu -> menu.getParentId().equals(parentId))
                .forEach(menu -> {
                    RouteVO routeVO = new RouteVO();
                    routeVO.setName(menu.getId() + ""); // 根据name路由跳转 this.$router.push({path:xxx})
                    routeVO.setPath(menu.getPath()); // 根据path路由跳转 this.$router.push({name:xxx})
                    routeVO.setRedirect(menu.getRedirect());
                    routeVO.setComponent(menu.getComponent());
                    routeVO.setRedirect(menu.getRedirect());
                    RouteVO.Meta meta = new RouteVO.Meta(menu.getName(), menu.getIcon(), menu.getRoles());
                    routeVO.setMeta(meta);
                    // 菜单显示隐藏
                    routeVO.setHidden(!GlobalConstants.STATUS_YES.equals(menu.getVisible()));
                    List<RouteVO> children = recursionRoute(menu.getId(), menuList);
                    routeVO.setChildren(children);
                    if (CollectionUtil.isNotEmpty(children)) {
                        routeVO.setAlwaysShow(Boolean.TRUE); // 显示子节点
                    }
                    list.add(routeVO);
                }));
        return list;

    }

    /**
     * 新增菜单
     *
     * @param menu
     * @return
     */
    @Override
    @CacheEvict(cacheNames = "system", key = "'routes'")
    public boolean saveMenu(SysMenu menu) {
        String component = menu.getComponent();
        if ("Layout".equals(component)) {
            menu.setPath("/" + IdUtil.simpleUUID());
        } else {
            menu.setPath(component.replaceAll("/", "_"));
        }

        boolean result = this.save(menu);
        if (result == true) {
            permissionService.refreshPermRolesRules();
        }
        return result;
    }

    /**
     * 修改菜单
     *
     * @param menu
     * @return
     */
    @Override
    @CacheEvict(cacheNames = "system", key = "'routes'")
    public boolean updateMenu(SysMenu menu) {
        String component = menu.getComponent();

        // 检测页面路径是否变化
        SysMenu oldMenu = this.getById(menu.getId());
        if (oldMenu.getComponent() != null && !oldMenu.getComponent().equals(component)) {
            if ("Layout".equals(component)) {
                menu.setPath("/" + IdUtil.simpleUUID());
            } else {
                menu.setPath(component.replaceAll("/", "_"));
            }
        }
        boolean result = this.updateById(menu);
        if (result == true) {
            permissionService.refreshPermRolesRules();
        }
        return result;
    }


    /**
     * 清理路由缓存
     */
    @Override
    @CacheEvict(cacheNames = "system", key = "'routeList'")
    public void cleanCache() {
    }


    /**
     * 修改菜单显示状态
     *
     * @param menuId  菜单ID
     * @param visible 是否显示(1->显示；2->隐藏)
     * @return
     */
    @Override
    @CacheEvict(cacheNames = "system", key = "'routes'")
    public boolean updateMenuVisible(Long menuId, Integer visible) {
        boolean result = this.update(new LambdaUpdateWrapper<SysMenu>()
                .eq(SysMenu::getId, menuId)
                .set(SysMenu::getVisible, visible)
        );
        if (result) {
            permissionService.refreshPermRolesRules();
        }
        return result;
    }
}
