package com.manage.system.service;

import com.manage.common.Constants;
import com.manage.exception.DocException;
import com.manage.exception.impl.BizExceptionStatusEnum;
import com.manage.source.service.FileService;
import com.manage.system.bean.DepartBean;
import com.manage.system.bean.Menu;
import com.manage.system.bean.RoleBean;
import com.manage.system.dao.DepartMapper;
import com.manage.system.model.SysDepartDto;
import com.manage.system.model.SysRoleDto;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 部门服务类
 * Created by luya on 2018/9/14.
 */
@Service
public class DepartService {

    private Log logger = LogFactory.getLog(DepartService.class);

    // 系统文件根目录
    @Value("${file.fileRootPath}")
    private String fileRootPath;

    @Autowired
    private DepartMapper departMapper;

    @Autowired
    private MenuService menuService;

    @Autowired
    private FileService fileService;
    @Autowired
    private RoleMenuService roleMenuService;

    @Autowired
    private RoleService roleService;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int insertDepart(DepartBean departBean, boolean isAdmin) throws Exception {
        SysDepartDto departDto = new SysDepartDto();
        BeanUtils.copyProperties(departBean, departDto);
        try {
            List<Integer> types = new ArrayList<>();
            types.add(Constants.NOMAL_TYPE);
            int menuId = menuService.insertMenu(Constants.MENU_DOC_PARENTID, departBean.getName(), Constants.DEPART_FILE_PATH, Constants.MENU_TYPE_DEFAULT, Constants.STATUE_VALID);
            departDto.setMenuId(menuId);
            int departId = departMapper.insert(departDto);
            fileService.makeDirs(fileRootPath + File.separator + departDto.getCode());
            //fileUtil.makeSureDirs(departDto.getCode());
            if (isAdmin) {
                roleMenuService.insertRoleMenu(menuId,Constants.SYSTEM_TYPE);
            }
            return departId;
        } catch (DuplicateKeyException e) {
            logger.error(e);
            throw new Exception("部门名称重复！");
        } catch (Exception e) {
            logger.error(e);
            throw new Exception(BizExceptionStatusEnum.DEPART_ADD_ERROR.getMessage());
        }
    }

    /**
     * 删除部门同时删除菜单
     *
     * @param id
     * @return
     * @throws Exception
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int deleteByPrimaryKey(Integer id) throws Exception {
        SysDepartDto depart = departMapper.selectByPrimaryKey(id);
        // 根据部门获取部门内角色列表
        List<SysRoleDto> roleDtoList = roleService.selectByDepartId(id);
        if (roleDtoList != null && roleDtoList.size() > 0) {
            throw new DocException(BizExceptionStatusEnum.DEPART_DELETE_ERROR2);
        }
        // 根据角色ｉｄ删除角色对应的菜单和权限信息。
        List<Integer> departIds = null;
        if (id != null) {
            departIds = new ArrayList<>();
            departIds.add(id);
        }
        List<RoleBean> roleDtos = roleService.getRoleList(null, departIds);
        for (RoleBean dto : roleDtos) {
            roleService.deleteByPrimaryKey(dto.getId());
        }
        roleMenuService.deleteByPrimaryKey(depart.getMenuId(),null,null);
        menuService.deleteByPrimaryKey(depart.getMenuId());
        fileService.deleteFile(fileRootPath + File.separator + depart.getCode());
        return departMapper.deleteByPrimaryKey(id);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public DepartBean selectPrimaryKey(Integer id) throws Exception {
        SysDepartDto departDto = departMapper.selectByPrimaryKey(id);
        DepartBean departBean = new DepartBean();
        BeanUtils.copyProperties(departDto, departBean);
        return departBean;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int updateByPrimaryKey(DepartBean departBean) throws Exception {
        SysDepartDto departDto = new SysDepartDto();
        BeanUtils.copyProperties(departBean, departDto);
        return departMapper.updateByPrimaryKey(departDto);
    }

    /**
     * 根据menuid查看部门信息
     *
     * @param id
     * @return
     * @throws Exception
     */
    @Transactional(readOnly = true)
    public DepartBean getDepartByMenuId(Integer id) {
        SysDepartDto departDtos = departMapper.getDepartByMenuId(id);
        DepartBean departBean = new DepartBean();
        BeanUtils.copyProperties(departDtos, departBean);
        return departBean;
    }

    @Transactional(readOnly = true)
    public List<DepartBean> getDepartList(Integer id, String name) throws Exception {
        List<SysDepartDto> departDtos = departMapper.selectAll(id, name);
        List<DepartBean> departBeanList = new ArrayList<>();
        if (departDtos != null) {
            for (SysDepartDto dto : departDtos) {
                DepartBean bean = new DepartBean();
                BeanUtils.copyProperties(dto, bean);
                String datetime = DateFormatUtils.format(dto.getUpdateTime().getTime(), Constants.DATE_FORMAT_YMDHM);
                bean.setUpdateTime(datetime);
                departBeanList.add(bean);
            }
        }

        return departBeanList;
    }

    /*根据角色id返回所有部门*/
    @Transactional(readOnly = true)
    public List<DepartBean> getDepartListByRoles(List<Integer> roleIds) throws Exception {
        List<SysDepartDto> departDtos = departMapper.selectDepartByRoles(roleIds);
        List<DepartBean> departBeanList = new ArrayList<>();
        if (departDtos != null) {
            for (SysDepartDto dto : departDtos) {
                DepartBean bean = new DepartBean();
                BeanUtils.copyProperties(dto, bean);
                departBeanList.add(bean);
            }
        }
        return departBeanList;
    }

}
