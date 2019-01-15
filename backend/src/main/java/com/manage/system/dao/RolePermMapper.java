package com.manage.system.dao;

import com.manage.system.model.SysPermissionDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by luya on 2018/9/22.
 */
@Mapper
public interface RolePermMapper {

    List<SysPermissionDto> selectByRoleIds(@Param("roleIds") List<Integer> roleIds) throws Exception;

    List<SysPermissionDto> selectPermByRoleId(@Param("menuId") Integer menuId,
                                              @Param("userId") String userId) throws Exception;

    int insert(@Param("permId") Integer permId, @Param("roleId") Integer roleId) throws Exception;

    int deleteByPrimaryKey(@Param("permId") Integer permId, @Param("roleId") Integer roleId) throws Exception;
}