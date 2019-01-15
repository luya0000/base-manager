package com.manage.system.dao;

import com.manage.system.model.SysRoleDto;
import com.manage.system.model.SysUserRoleDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by luya on 2018/6/8.
 */
@Mapper
public interface UserRoleMapper {

    public int insert(SysUserRoleDto userRoleDto);

    public int updateByParam(SysUserRoleDto userRoleDto);

    public int deleteByParam(@Param("userId") String userId, @Param("roleId") Integer roleId);

    public List<SysRoleDto> selectByParam(@Param("userId") String userId, @Param("roleId") Integer roleId);
}
