package com.manage.system.dao;

import com.manage.system.model.SysMenuDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by luya on 2018/6/8.
 */
@Mapper
public interface MenuMapper {


    int insert(SysMenuDto menu) throws Exception;

    int deleteByPrimaryKey(Integer id) throws Exception;

    List<SysMenuDto> selectByParam(@Param("name") String name, @Param("parentId") Integer parentId,
                                   @Param("types") List<Integer> type) throws Exception;

    List<SysMenuDto> selectMenuByUserId(@Param("userId") String userId, @Param("types") List<Integer> types) throws Exception;

    SysMenuDto selectByPrimaryKey(Integer id) throws Exception;

    int updateByPrimaryKey(SysMenuDto menu) throws Exception;

    List<SysMenuDto> selectMenuByRoleId(@Param("roleId") Integer roleId, @Param("types")  List<Integer> types) throws Exception;

    Integer getOrder(@Param("parentId") Integer parentId) throws Exception;
}
