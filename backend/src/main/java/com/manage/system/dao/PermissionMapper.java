package com.manage.system.dao;

import com.manage.system.model.SysPermissionDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by luya on 2018/6/8.
 */
@Mapper
public interface PermissionMapper {

    List<SysPermissionDto> selectAll() throws Exception;

    int insert(SysPermissionDto user) throws Exception;

    int deleteByPrimaryKey(Integer id) throws Exception;

    SysPermissionDto selectByPrimaryKey(Integer id) throws Exception;
}
