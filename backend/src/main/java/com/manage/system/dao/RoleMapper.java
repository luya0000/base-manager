package com.manage.system.dao;

import com.manage.system.model.SysRoleDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by luya on 2018/6/8.
 */
@Mapper
public interface RoleMapper {

    List<SysRoleDto> selectAll(@Param("name") String name,@Param("departIds") List<Integer> departId) throws Exception;

    int insert(SysRoleDto user) throws Exception;

    int deleteByPrimaryKey(@Param("id") Integer id,@Param("departId") Integer departId) throws Exception;

    SysRoleDto selectByPrimaryKey(Integer id) throws Exception;

    List<SysRoleDto> selectByDepartId(@Param("departId") Integer departId) throws Exception;

    int updateByPrimaryKey(SysRoleDto user) throws Exception;
}
