package com.manage.system.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.manage.common.APIResponse;
import com.manage.common.BaseController;
import com.manage.common.Constants;
import com.manage.common.UrlConstants;
import com.manage.exception.DocException;
import com.manage.exception.impl.BizExceptionStatusEnum;
import com.manage.exception.impl.SysExceptionStatusEnum;
import com.manage.system.bean.DepartBean;
import com.manage.system.service.DepartService;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by luya on 2018/6/9.
 */
@RestController
@RequestMapping(value = UrlConstants.URL_DEPART_MODEL)
public class DepartController extends BaseController {

    private Log logger = LogFactory.getLog(DepartController.class);
    private Log bizLogger = LogFactory.getLog("bizLogger");

    @Autowired
    private DepartService departService;

    /**
     * 根据条件获取部门列表
     *
     * @param currPage
     * @param pageSize
     * @param departId
     * @param departName
     * @return
     */
    @GetMapping("/list")
    public APIResponse departList(@RequestParam(value = "currPage", required = false) Integer currPage,
                                  @RequestParam(value = "pageSize", required = false) Integer pageSize,
                                  @RequestParam(value = "departId", required = false) Integer departId,
                                  @RequestParam(value = "departName", required = false) String departName) {

        currPage = currPage == null ? Constants.PAGEHELPER_PAGE_CURRENT : currPage;
        pageSize = pageSize == null ? Constants.PAGEHELPER_PAGE_SIZE : pageSize;
        bizLogger.info(new StringBuilder().append("账号：").append(getAccount()).append("　　查询部门列表"));

        List<DepartBean> departBeanList = null;
        try {
            Page page = PageHelper.startPage(currPage, pageSize, true);
            departBeanList = departService.getDepartList(departId, departName);
            Map result = new HashedMap();
            result.put("data", departBeanList);
            result.put("currPage", currPage);
            result.put("total", page.getTotal());
            return APIResponse.toOkResponse(result);
        } catch (Exception e) {
            logger.error(e);
            e.printStackTrace();
            return APIResponse.toExceptionResponse(BizExceptionStatusEnum.ROLE_SELETE_ERROR);
        }
    }

    @GetMapping("/listByRole")
    public APIResponse departListByRole() {
        bizLogger.info(new StringBuilder().append("账号：").append(getAccount()).append("　　根据角色查询部门列表"));
        List<Integer> roles = super.getRoles();
        List<DepartBean> departBeanList = null;
        Map result = new HashedMap();
        try {
            if (roles != null && roles.contains(Constants.SYSTEM_TYPE)) {
                // 系统管理员 选择系统管理员角色时不显示type=2的菜单
                departBeanList = departService.getDepartList(null, null);
            } else {
                departBeanList = departService.getDepartListByRoles(getRoles());
            }
        } catch (Exception e) {
            logger.error(e);
            e.printStackTrace();
            return APIResponse.toExceptionResponse(BizExceptionStatusEnum.ROLE_SELETE_ERROR);
        }
        result.put("data", departBeanList);
        return APIResponse.toOkResponse(result);
    }

    /**
     * 根据主键获取部门信息
     *
     * @param depart
     * @return
     */
    @GetMapping("/{id}")
    public APIResponse departInfo(@PathVariable("id") Integer depart) {
        bizLogger.info(new StringBuilder().append("账号：").append(getAccount()).append(" 根据部门ID查询部门信息:").append(depart));

        try {
            DepartBean role = departService.selectPrimaryKey(depart);
            return APIResponse.toOkResponse(role);
        } catch (Exception e) {
            logger.error(e);
            e.printStackTrace();
            return APIResponse.toExceptionResponse(BizExceptionStatusEnum.ROLE_SELETE_ERROR);
        }
    }

    /**
     * 添加部门信息
     *
     * @param code
     * @param name
     * @param note
     * @return
     */
    @PostMapping("/add")
    public APIResponse addDepart(@RequestParam("departCode") String code,
                                 @RequestParam("departName") String name,
                                 @RequestParam("note") String note) {
        bizLogger.info(new StringBuilder().append("账号：").append(getAccount()).append(" 添加部门信息.编码:").append(code).append(" 名称:").append(name));

        DepartBean departBean = new DepartBean();
        departBean.setCode(code);
        departBean.setNote(note);
        departBean.setName(name);
        departBean.setUpdateUser(getUserName());

        try {
            List<Integer> roles = super.getRoles();
            boolean isSystemRole = false;
            if (roles != null && roles.contains(Constants.SYSTEM_TYPE)) {
                isSystemRole = true;
            }
            departService.insertDepart(departBean, isSystemRole);
        } catch (Exception e) {
            logger.error(e);
            return APIResponse.toExceptionResponse(BizExceptionStatusEnum.DEPART_ADD_ERROR.getCode(), e.getMessage());
        }
        return APIResponse.toOkResponse();
    }


    /**
     * 修改部门
     *
     * @param id
     * @param name
     * @param type
     * @return
     */
    @PostMapping("/update")
    public APIResponse updateRole(@RequestParam(value = "id") Integer id,
                                  @RequestParam("name") String name,
                                  @RequestParam("type") String type) {
        bizLogger.info(new StringBuilder().append("账号：").append(getAccount()).append(" 修改部门信息.").append(" 名称:").append(name));

        DepartBean departBean = new DepartBean();
        departBean.setId(id);
        departBean.setName(name);
        // roleBean.setType(type);
        departBean.setUpdateUser(getUserName());
        try {
            departService.updateByPrimaryKey(departBean);
        } catch (Exception e) {
            logger.error(e);
            e.printStackTrace();
            return APIResponse.toExceptionResponse(BizExceptionStatusEnum.DEPART_UPDATE_ERROR);
        }
        return APIResponse.toOkResponse();
    }

    /**
     * 删除用户
     *
     * @param id
     * @return
     */
    @PostMapping("/delete/{id}")
    public APIResponse deleteDepart(@PathVariable(value = "id") Integer id) {
        bizLogger.info(new StringBuilder().append("账号：").append(getAccount()).append(" 部门部门信息.部门ID").append(id));

        try {
            departService.deleteByPrimaryKey(id);
        } catch (DocException e) {
            logger.error(e);
            e.printStackTrace();
            return APIResponse.toExceptionResponse(BizExceptionStatusEnum.DEPART_DELETE_ERROR2);
        } catch (Exception e) {
            logger.error(e);
            e.printStackTrace();
            return APIResponse.toExceptionResponse(SysExceptionStatusEnum.SERVER_ERROR);
        }
        return APIResponse.toOkResponse();
    }

}
