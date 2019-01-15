package com.manage.system.dao;

import com.manage.system.model.SysDepartDto;
import com.manage.system.model.SysUserDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DepartMapper {

    int insert(SysDepartDto departDto) throws Exception;

    int deleteByPrimaryKey(@Param("id") Integer id) throws Exception;

    int updateByPrimaryKey(SysDepartDto departDto) throws Exception;

    List<SysDepartDto> selectAll(@Param("id") Integer id, @Param("name") String name) throws Exception;

    /*根据角色id返回所有部门*/
    List<SysDepartDto> selectDepartByRoles(@Param("roleIds") List<Integer> roleIds) throws Exception;

    /*根据角色id返回所有部门*/
    SysDepartDto getDepartByMenuId(@Param("menuId") Integer menuId);

    SysDepartDto selectByPrimaryKey(@Param("id") Integer id) throws Exception;

}
