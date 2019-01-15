package com.manage.system.service;

import com.manage.common.Constants;
import com.manage.system.dao.PermissionMapper;
import com.manage.system.model.SysPermissionDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by luya on 2018/6/8.
 */
@Service
public class PermService {

    @Autowired
    private PermissionMapper permissionMapper;

    /*获取权限列表*/
    @Transactional(readOnly = true)
    public List<SysPermissionDto> getPermissionList() throws Exception {
        List<SysPermissionDto> permissionDtoList = permissionMapper.selectAll();
        return permissionDtoList;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int insertPermission(SysPermissionDto permissionDto) throws Exception {
        permissionDto.setId(1);
        return permissionMapper.insert(permissionDto);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int deleteByPrimaryKey(Integer id) throws Exception {
        return permissionMapper.deleteByPrimaryKey(id);
    }
}
