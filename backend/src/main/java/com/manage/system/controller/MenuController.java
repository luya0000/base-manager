package com.manage.system.controller;

import javax.servlet.http.HttpServletRequest;

import com.github.pagehelper.util.StringUtil;
import com.manage.common.APIResponse;
import com.manage.common.BaseController;
import com.manage.common.Constants;
import com.manage.common.UrlConstants;
import com.manage.exception.impl.BizExceptionStatusEnum;
import com.manage.system.bean.DepartBean;
import com.manage.system.bean.Menu;
import com.manage.system.model.SysMenuDto;
import com.manage.system.service.DepartService;
import com.manage.system.service.MenuService;
import com.manage.system.service.RoleMenuService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = UrlConstants.URL_MENU_MODEL)
public class MenuController extends BaseController {

    Log logger = LogFactory.getLog(MenuController.class);

    @Autowired
    private MenuService menuService;

    @Autowired
    private DepartService departService;

    /**
     * 获取用户拥有的菜单列表
     *
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public APIResponse getLoginUserMenus() {
        String useId = super.getAccount();
        // 非系统管理员会获取当前角色对应部门的所有人
        List<Integer> departs = null;
        List<Integer> roles = super.getRoles();
        List<Integer> types = new ArrayList<>();
        if (!ObjectUtils.isEmpty(roles) && roles.contains(Constants.SYSTEM_TYPE)) {
            types.add(Constants.SYSTEM_TYPE);
            types.add(Constants.NOMAL_TYPE);
        } else if (!ObjectUtils.isEmpty(roles) && !roles.contains(Constants.SYSTEM_TYPE)) {
            types.add(Constants.ADMIN_TYPE);
            types.add(Constants.NOMAL_TYPE);
        }
        List<Menu> menus = menuService.getUserMenus(useId, types);
        return response(menus);
    }

    /**
     * 系统管理以外的所有菜单
     *
     * @return
     */
    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public APIResponse getAllMenus(@RequestParam(value = "menuName", required = false) String menuName,
                                   @RequestParam(value = "roleId", required = false) Integer roleId,
                                   @RequestParam(value = "parentId", required = false) Integer parentId,
                                   @RequestParam(value = "type", required = false) Integer type) {
        try {
            // 非系统管理员会获取当前角色对应部门的所有人
            List<Integer> departs;
            List<Integer> roles = super.getRoles();
            List<Integer> types = new ArrayList<>();
            if (!ObjectUtils.isEmpty(roles) && roles.contains(Constants.SYSTEM_TYPE) && roleId == Constants.SYSTEM_TYPE) {
                types.add(Constants.NOMAL_TYPE);
                return response(menuService.getFormatMenusByParam(menuName, parentId, null, types));
            } else if (!ObjectUtils.isEmpty(roles) && roles.contains(Constants.SYSTEM_TYPE)) {
                types.add(Constants.ADMIN_TYPE);
                types.add(Constants.NOMAL_TYPE);
                return response(menuService.getFormatMenusByParam(menuName, parentId, null, types));
            } else if (!ObjectUtils.isEmpty(roles) && !roles.contains(Constants.SYSTEM_TYPE)) {
                List<DepartBean> departBeanList = departService.getDepartListByRoles(roles);
                departs = new ArrayList<>();
                for (DepartBean bean : departBeanList) {
                    departs.add(bean.getId());
                }
                types.add(Constants.NOMAL_TYPE);
                return response(menuService.getFormatMenusByParam(menuName, parentId, departs, types));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 系统管理以外的所有菜单
     *
     * @return
     */
    @RequestMapping(value = "/menus", method = RequestMethod.GET)
    public List<Menu> getRoleMenus(@RequestParam("roleId") Integer roleId) {

        // 非系统管理员会获取当前角色对应部门的所有人
        List<Integer> roles = super.getRoles();
        List<Integer> types = new ArrayList<>();

        if (roles != null && roles.contains(Constants.SYSTEM_TYPE) && roleId == Constants.SYSTEM_TYPE) {
            // 系统管理员 选择系统管理员角色时不显示type=2的菜单
            // types.add(Constants.ADMIN_TYPE);
            types.add(Constants.NOMAL_TYPE);
        } else if (roles != null && roles.contains(Constants.SYSTEM_TYPE) && roleId != Constants.SYSTEM_TYPE) {
            // 系统管理员 选择非系统管理员角色时显示type=2的菜单
            types.add(Constants.ADMIN_TYPE);
            types.add(Constants.NOMAL_TYPE);
        } else {
            types.add(Constants.NOMAL_TYPE);
        }
        return menuService.getMenusByRole(roleId, types);
    }

    /**
     * 系统管理以外的所有菜单
     *
     * @return
     */
    @RequestMapping(value = "/addrolemenu", method = RequestMethod.POST)
    public APIResponse addRoleMenu(@RequestParam("roleId") Integer roleId, @RequestParam("menuIds") String menuIds) {
        List<Integer> roles = super.getRoles();
        List<Integer> menuType = new ArrayList<>();
        if (roles != null && roles.contains(Constants.SYSTEM_TYPE)) {
            // 系统管理员 选择系统管理员角色时不显示type=2的菜单
            menuType.add(Constants.ADMIN_TYPE);
            menuType.add(Constants.NOMAL_TYPE);
        } else {
            menuType.add(Constants.NOMAL_TYPE);
        }

        try {
            String[] menuIdArr = menuIds.split(",");
            menuService.insertRoleMenu(roleId, menuIdArr,menuType);
        } catch (Exception e) {
            logger.error(e);
            e.printStackTrace();
            return APIResponse.toExceptionResponse(BizExceptionStatusEnum.ROLE_UPDATE_ERROR);
        }
        return APIResponse.toOkResponse();
    }
}
