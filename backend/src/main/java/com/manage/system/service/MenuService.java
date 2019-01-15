package com.manage.system.service;

import com.github.pagehelper.util.StringUtil;
import com.manage.common.Constants;
import com.manage.system.bean.Menu;
import com.manage.system.dao.MenuMapper;
import com.manage.system.model.SysMenuDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class MenuService {

    @Autowired
    private MenuMapper menuMapper;

    @Autowired
    private RoleMenuService roleMenuService;

    /*插入菜单表数据*/
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int insertMenu(Integer parentId, String name, String url, Integer type, Integer status) throws Exception {
        SysMenuDto menuDto = new SysMenuDto();
        menuDto.setName(name);
        menuDto.setParentId(parentId);
        menuDto.setType(type);
        menuDto.setUrl(url);
        int order = menuMapper.getOrder(parentId);
        menuDto.setOrder(order + 1);
        menuDto.setStatus(status);
        menuMapper.insert(menuDto);
        return menuDto.getId();
    }

    /*插入角色菜单表数据*/
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int deleteByPrimaryKey(Integer id) throws Exception {
        // 删除菜单
        return menuMapper.deleteByPrimaryKey(id);
    }

    /**
     * @param menuName 菜单名
     * @param parentId 父级菜单id
     * @param type     菜单类型，1：系统，2：文档
     * @return
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public List<Menu> getFormatMenusByParam(String menuName, Integer parentId, List<Integer> departIds, List<Integer> type) {

        List<Menu> menus = new ArrayList<>();
        List<SysMenuDto> menuList;
        // 获取用户菜单
        try {
            menuList = menuMapper.selectByParam(menuName, parentId, type);
        } catch (Exception e) {
            menuList = null;
            e.printStackTrace();
        }
        // 菜单二级设置
        getRootMenus(menus, menuList, departIds);

        return menus;
    }
    public List<SysMenuDto> getAllMenusByParam(String menuName, Integer parentId,List<Integer> type) {
        List<SysMenuDto> menuList;
        // 获取用户菜单
        try {
            menuList = menuMapper.selectByParam(menuName, parentId, type);
        } catch (Exception e) {
            menuList = null;
            e.printStackTrace();
        }
        return menuList;
    }
    /**
     * @param userId
     * @return
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public List<Menu> getUserMenus(String userId, List<Integer> types) {

        List<Menu> menus = new ArrayList<>();
        List<SysMenuDto> menuList;
        // 获取用户菜单
        try {
            menuList = menuMapper.selectMenuByUserId(userId, types);
        } catch (Exception e) {
            menuList = null;
            e.printStackTrace();
        }
        // 菜单二级设置
        getRootMenus(menus, menuList, null);

        return menus;
    }

    /**
     * @param roleId
     * @return
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public List<Menu> getMenusByRole(Integer roleId, List<Integer> types) {

        List<Menu> menus = new ArrayList<>();
        List<SysMenuDto> menuList;
        // 获取用户菜单
        try {
            menuList = menuMapper.selectMenuByRoleId(roleId, types);
        } catch (Exception e) {
            menuList = null;
            e.printStackTrace();
        }
        // 菜单二级设置
        getRootMenus(menus, menuList, null);

        return menus;
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    private void getRootMenus(List<Menu> menus, List<SysMenuDto> menuList, List<Integer> departIds) {
        // 菜单二级设置
        if (menuList != null && menuList.size() > 0) {
            for (SysMenuDto dto : menuList) {
                if (dto.getParentId() == 0) {
                    Menu menu = new Menu(dto.getId(), dto.getName(), dto.getUrl(), dto.getIcon(), dto.getType());
                    menus.add(menu);
                }
            }
            getSubMenu(menus, menuList, departIds);
        }
    }

    /**
     * 获取子菜单
     *
     * @param menus    当前菜单列表
     * @param allMenus 所有菜单
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    private void getSubMenu(List<Menu> menus, List<SysMenuDto> allMenus, List<Integer> departIds) {
        for (Menu menu : menus) {
            for (SysMenuDto dto : allMenus) {
                if ((departIds == null && menu.getMenuId() == dto.getParentId())
                        || (departIds != null && menu.getMenuId() == dto.getParentId()
                        && departIds.contains(dto.getDepartId()))) {
                    Menu subMenu = new Menu(dto.getId(), dto.getName(), dto.getUrl(), dto.getIcon(), dto.getType());
                    menu.getSubMenus().add(subMenu);
                }
            }
            if (menu.getSubMenus().size() > 0) {
                getSubMenu(menu.getSubMenus(), allMenus, departIds);
            }
        }
    }

    /*插入角色菜单表数据*/
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void insertRoleMenu(Integer roleId, String[] menuList, List<Integer> menuTypes) throws Exception {
        // 删除菜单关系
        roleMenuService.deleteByPrimaryKey(null, roleId, menuTypes);
        for (String id : menuList) {
            if (StringUtils.isEmpty(id)) {
                continue;
            }
            roleMenuService.insertRoleMenu(Integer.parseInt(id), roleId);
        }
    }
}
