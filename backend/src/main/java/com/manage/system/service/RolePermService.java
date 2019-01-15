package com.manage.system.service;

import com.manage.system.bean.DepartBean;
import com.manage.system.dao.DepartMapper;
import com.manage.system.dao.RolePermMapper;
import com.manage.system.model.SysDepartDto;
import com.manage.system.model.SysPermissionDto;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 角色权限服务类
 * Created by luya on 2018/9/14.
 */
@Service
public class RolePermService {

    @Autowired
    private RolePermMapper rolePermMapper;

    @Transactional(readOnly = true)
    public List<SysPermissionDto> getPermByRoleIds(List<Integer> roleIds) throws Exception {
        List<SysPermissionDto> permIds = rolePermMapper.selectByRoleIds(roleIds);
        return permIds == null ? Collections.emptyList() : permIds;
    }

    @Transactional(readOnly = true)
    public List<SysPermissionDto> getPermByRoleIdMenuId(Integer menuId, String userId) throws Exception {
        List<SysPermissionDto> permIds = rolePermMapper.selectPermByRoleId(menuId, userId);
        return permIds;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int insertRolePerm(Integer permId, Integer roleId) throws Exception {
        return rolePermMapper.insert(permId, roleId);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int deleteByPrimaryKey(Integer permId, Integer roleId) throws Exception {
        return rolePermMapper.deleteByPrimaryKey(permId, roleId);
    }

}
