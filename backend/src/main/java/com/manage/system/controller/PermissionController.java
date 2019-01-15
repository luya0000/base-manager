package com.manage.system.controller;

import com.manage.common.APIResponse;
import com.manage.common.BaseController;
import com.manage.common.UrlConstants;
import com.manage.exception.impl.BizExceptionStatusEnum;
import com.manage.system.model.SysPermissionDto;
import com.manage.system.service.PermService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by luya on 2018/6/8.
 * 暂时未使用
 * 权限内容初始化时加入，此类用于后期单独对权限维护，不参与前端操作
 */
@RestController
@RequestMapping(value = UrlConstants.URL_PERM_MODEL)
public class PermissionController extends BaseController {

    Log logger = LogFactory.getLog(PermissionController.class);

    @Autowired
    private PermService permService;

    /**
     * 权限列表
     *
     * @return
     */
    @GetMapping("/list")
    public APIResponse permissionList() {

        List<SysPermissionDto> permissionList = null;
        try {
            permissionList = permService.getPermissionList();
            return APIResponse.toOkResponse(permissionList);
        } catch (Exception e) {
            logger.error(e);
            e.printStackTrace();
            return APIResponse.toExceptionResponse(BizExceptionStatusEnum.PERMIS_SELETE_ERROR);
        }
    }

    /**
     * 添加权限
     *
     * @param name
     * @param type
     * @return
     */
    @PostMapping("/add")
    public APIResponse addPermission(
            @RequestParam("name") String name,
            @RequestParam("type") String type) {

        SysPermissionDto permission = new SysPermissionDto();
        permission.setName(name);
        try {
            permService.insertPermission(permission);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e);
            return APIResponse.toExceptionResponse(BizExceptionStatusEnum.PERMIS_ADD_ERROR);
        }
        return APIResponse.toOkResponse();
    }


    /**
     * 删除权限
     *
     * @param id
     * @return
     */
    @PostMapping("/delete/{id}")
    public APIResponse deletePermission(@PathVariable(value = "id") Integer id) {

        try {
            permService.deleteByPrimaryKey(id);
        } catch (Exception e) {
            logger.error(e);
            e.printStackTrace();
            return APIResponse.toExceptionResponse(BizExceptionStatusEnum.PERMIS_DELETE_ERROR);
        }
        return APIResponse.toOkResponse();
    }
}
