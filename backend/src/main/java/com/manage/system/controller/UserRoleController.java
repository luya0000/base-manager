package com.manage.system.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.manage.common.APIResponse;
import com.manage.common.BaseController;
import com.manage.common.Constants;
import com.manage.common.UrlConstants;
import com.manage.exception.impl.BizExceptionStatusEnum;
import com.manage.system.bean.DepartBean;
import com.manage.system.bean.RoleBean;
import com.manage.system.bean.UserRoleBean;
import com.manage.system.service.DepartService;
import com.manage.system.service.RoleService;
import com.manage.system.service.UserRoleService;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by luya on 2018/6/9.
 */
@RestController
@RequestMapping(value = UrlConstants.URL_USER_ROLE_MODEL)
public class UserRoleController extends BaseController {

    private Log logger = LogFactory.getLog(UserRoleController.class);

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private DepartService departService;

    /**
     * 按部门分类获取角色列表
     *
     * @return
     */
    @GetMapping("/list")
    public APIResponse roleListByDepart() {

        try {
            // 会获取当前角色对应部门的所有人
            List<Integer> departs = null;
            List<Integer> roles = super.getRoles();
            if (!ObjectUtils.isEmpty(roles) && !roles.contains(Constants.SYSTEM_TYPE)) {
                List<DepartBean> departBeanList = departService.getDepartListByRoles(roles);
                departs = new ArrayList<>();
                for(DepartBean bean : departBeanList){
                    departs.add(bean.getId());
                }
            }

            Map<String, List<RoleBean>> roleBeanMap = userRoleService.getRolesGroupByDepart(departs);
            Set<String> keys = roleBeanMap.keySet();
            // 将key值和 value值分别放到bean里
            List<UserRoleBean> result = new ArrayList<>();
            for (String key : keys) {
                // 便于前段循环获取
                UserRoleBean roleBean = new UserRoleBean();
                roleBean.setDepartName(key);
                roleBean.setRoleBeanList(roleBeanMap.get(key));
                result.add(roleBean);
            }
            Map<String, List<UserRoleBean>> st = new HashedMap();
            st.put("data", result);
            return APIResponse.toOkResponse(st);
        } catch (Exception e) {
            logger.error(e);
            e.printStackTrace();
            return APIResponse.toExceptionResponse(BizExceptionStatusEnum.ROLE_SELETE_ERROR);
        }
    }


    /**
     * 根据条件获取角色列表
     *
     * @return
     */
    @GetMapping("/user")
    public APIResponse roleListByUser(@RequestParam(value = "userId", required = false) String userId) {

        try {
            List<Integer> roleIdList = userRoleService.getRolesIdByParam(userId, null);
            return APIResponse.toOkResponse(roleIdList);
        } catch (Exception e) {
            logger.error(e);
            e.printStackTrace();
            return APIResponse.toExceptionResponse(BizExceptionStatusEnum.ROLE_SELETE_ERROR);
        }
    }

    /**
     * 添加角色
     *
     * @param userId
     * @param roleIds
     * @return
     */
    @PostMapping("/add")
    public APIResponse addRole(@RequestParam("userId") String userId,
                               @RequestParam(value = "roleIds", required = false) String roleIds) {
        try {
            // 删除关系
            userRoleService.delUserRoleByUserId(userId, null);
            if(StringUtils.isEmpty(roleIds)) return APIResponse.toOkResponse();

            // 创建关系
            String[] roleId = roleIds.split(",");
            List<Integer> roleList = new ArrayList<>();
            for (String id : roleId) {
                roleList.add(Integer.parseInt(id));
            }
            userRoleService.insertUserRole(userId, roleList);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e);
            return APIResponse.toExceptionResponse(BizExceptionStatusEnum.ROLE_ADD_ERROR);
        }
        return APIResponse.toOkResponse();
    }

}
