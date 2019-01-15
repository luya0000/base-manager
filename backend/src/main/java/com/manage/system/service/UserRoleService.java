package com.manage.system.service;

import com.fasterxml.jackson.core.PrettyPrinter;
import com.github.pagehelper.util.StringUtil;
import com.manage.common.Constants;
import com.manage.system.bean.RoleBean;
import com.manage.system.dao.RoleMapper;
import com.manage.system.dao.UserMapper;
import com.manage.system.dao.UserRoleMapper;
import com.manage.system.model.SysRoleDto;
import com.manage.system.model.SysUserRoleDto;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by luya on 2018/6/8.
 */
@Service
public class UserRoleService {

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Transactional(readOnly = true)
    public List<Integer> getRolesIdByParam(String userId, Integer roleId) {
        List<SysRoleDto> roleList = userRoleMapper.selectByParam(userId, roleId);
        List<Integer> roles = new ArrayList<>();
        if (roleList != null && roleList.size() > 0) {
            for (SysRoleDto dto : roleList) {
                roles.add(dto.getId());
            }
        }
        return roles != null ? roles : Collections.<Integer>emptyList();
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int delUserRoleByUserId(String userId, Integer roleId) {
        int result = userRoleMapper.deleteByParam(userId, roleId);
        return result;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Map<String, List<RoleBean>> getRolesGroupByDepart(List<Integer> departs) throws Exception {

        List<SysRoleDto> roleDtoList = roleMapper.selectAll(null, departs);

        Map<String, List<RoleBean>> result = new HashedMap();

        if (roleDtoList != null) {
            for (SysRoleDto dto : roleDtoList) {
                if(dto.getId() != Constants.SYSTEM_TYPE) {
                    if (result.containsKey(dto.getDepartName())) {
                        RoleBean bean = new RoleBean();
                        BeanUtils.copyProperties(dto, bean);
                        result.get(dto.getDepartName()).add(bean);
                    } else {
                        List<RoleBean> roleList = new ArrayList<>();
                        RoleBean bean = new RoleBean();
                        BeanUtils.copyProperties(dto, bean);
                        roleList.add(bean);
                        result.put(dto.getDepartName(), roleList);
                    }
                }
            }
        }

        return result;
    }

    /**
     * 插入角色和用户关系
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Integer insertUserRole(String userId, List<Integer> roleIds) throws Exception {
        int count = 0;
        for (Integer roleId : roleIds) {
            SysUserRoleDto roleDto = new SysUserRoleDto();
            roleDto.setRoleId(roleId);
            roleDto.setUserId(userId);
            userRoleMapper.insert(roleDto);
            count++;
        }
        return count;
    }

}
