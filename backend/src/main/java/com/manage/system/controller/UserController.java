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
import com.manage.system.bean.UserBean;
import com.manage.system.model.SysPermissionDto;
import com.manage.system.service.DepartService;
import com.manage.system.service.RolePermService;
import com.manage.system.service.UserService;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by luya on 2018/6/8.
 */
@RestController
@RequestMapping(value = UrlConstants.URL_USER_MODEL)
public class UserController extends BaseController {

    private Log logger = LogFactory.getLog(UserController.class);
    private Log bizLogger = LogFactory.getLog("bizLogger");

    @Autowired
    private UserService userService;

    @Autowired
    private DepartService departService;

    @Autowired
    private RolePermService permService;

    /**
     * 根据条件获取用户列表
     *
     * @param currPage
     * @param pageSize
     * @return
     */
    @GetMapping("/list")
    public APIResponse userList(@RequestParam(value = "currPage", required = false) Integer currPage,
                                @RequestParam(value = "pageSize", required = false) Integer pageSize,
                                @RequestParam(value = "account", required = false) String account,
                                @RequestParam(value = "depart", required = false) String depart) {

        bizLogger.info(new StringBuilder().append("账号：").append(getAccount()).append("　查询用户列表."));
        currPage = currPage == null ? Constants.PAGEHELPER_PAGE_CURRENT : currPage;
        pageSize = pageSize == null ? Constants.PAGEHELPER_PAGE_SIZE : pageSize;

        List<UserBean> userList = null;
        try {
            Page page = PageHelper.startPage(currPage, pageSize, true);

            // 非系统管理员会获取当前角色对应部门的所有人
            List<Integer> departs = null;
            List<Integer> roles = super.getRoles();
            if (!ObjectUtils.isEmpty(roles) && !roles.contains(Constants.SYSTEM_TYPE)) {
                List<DepartBean> departBeanList = departService.getDepartListByRoles(roles);
                departs = new ArrayList<>();
                for (DepartBean bean : departBeanList) {
                    departs.add(bean.getId());
                }
            }
            userList = userService.selectUserList(account, departs, Constants.STATUE_INVALID);
            Map result = new HashedMap();
            result.put("data", userList);
            result.put("currPage", currPage);
            result.put("total", page.getTotal());
            return APIResponse.toOkResponse(result);
        } catch (Exception e) {
            logger.error(e);
            e.printStackTrace();
            return APIResponse.toExceptionResponse(BizExceptionStatusEnum.USER_SELETE_ERROR);
        }
    }

    /**
     * 根据主键获取用户
     * id 为－１表示查询自己数据
     *
     * @param userId
     * @return
     */
    @GetMapping("/{userId}")
    public APIResponse getUserByKey(@PathVariable("userId") String userId) {

        bizLogger.info(new StringBuilder().append("账号：").append(getAccount()).append("　根据主键获取用户ID.").append(userId));
        try {
            if (StringUtils.isEmpty(userId)) {
                userId = getAccount();
            }
            UserBean userBean = userService.selectByPrimaryKey(userId);
            userBean.setPassword(null);
            return APIResponse.toOkResponse(userBean);
        } catch (Exception e) {
            logger.error(e);
            e.printStackTrace();
            return APIResponse.toExceptionResponse(BizExceptionStatusEnum.USER_SELETE_ERROR);
        }
    }

    /**
     * 添加用户
     *
     * @param userId
     * @param userName
     * @param password
     * @param sex
     * @param phone
     * @param status
     * @param email
     * @param note
     * @return
     */
    @PostMapping("/add")
    public APIResponse addUser(@RequestParam("userId") String userId,
                               @RequestParam("userName") String userName,
                               @RequestParam("password") String password,
                               @RequestParam("sex") String sex,
                               @RequestParam("phone") String phone,
                               @RequestParam("status") int status,
                               @RequestParam("email") String email,
                               @RequestParam("note") String note) {

        if (StringUtils.isEmpty(password)) {
            return APIResponse.toExceptionResponse(BizExceptionStatusEnum.USER_PWD_DIFF_ERROR);
        }
        UserBean userBean = new UserBean();
        userBean.setUserId(userId);
        userBean.setUserName(userName);
        userBean.setPassword(password);
        userBean.setSex(sex);
        userBean.setPhone(phone);
        userBean.setEmail(email);
        userBean.setNote(note);
        userBean.setStatus(status);
        userBean.setUpdateUser(getUserName());
        bizLogger.info(new StringBuilder().append("账号：").append(getAccount()).append("　添加用户信息：").append(userName));
        try {
            userService.insertUser(userBean);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e);
            return APIResponse.toExceptionResponse(BizExceptionStatusEnum.USER_ADD_ERROR);
        }
        return APIResponse.toOkResponse();
    }


    /**
     * 修改用户
     *
     * @param userId
     * @param userName
     * @param password
     * @param sex
     * @param phone
     * @param status
     * @param email
     * @param note
     * @return
     */
    @PostMapping("/update")
    public APIResponse updateUser(@RequestParam("userId") String userId,
                                  @RequestParam("userName") String userName,
                                  @RequestParam("password") String password,
                                  @RequestParam("sex") String sex,
                                  @RequestParam("phone") String phone,
                                  @RequestParam("status") int status,
                                  @RequestParam("email") String email,
                                  @RequestParam("note") String note) {
        // bean赋值
        UserBean userBean = new UserBean();
        userBean.setUserId(userId);
        userBean.setUserName(userName);
        userBean.setSex(sex);
        userBean.setStatus(status);
        userBean.setPhone(phone);
        userBean.setEmail(email);
        userBean.setNote(note);
        userBean.setPassword(password);
        bizLogger.info(new StringBuilder().append("账号：").append(getAccount()).append("　更新用户信息：").append(userName));

        try {
            userService.updateUser(userBean);
        } catch (Exception e) {
            logger.error(e);
            e.printStackTrace();
            return APIResponse.toExceptionResponse(BizExceptionStatusEnum.USER_UPDATE_ERROR);
        }
        return APIResponse.toOkResponse();
    }


    /**
     * 删除用户
     *
     * @param userId
     * @return
     */
    @PostMapping("/delete/{userId}")
    public APIResponse deleteUser(@PathVariable(value = "userId") String userId) {

        bizLogger.info(new StringBuilder().append("账号：").append(getAccount()).append("　删除用户信息：").append(userId));
        try {
            userService.deleteUser(userId);
        } catch (Exception e) {
            logger.error(e);
            e.printStackTrace();
            return APIResponse.toExceptionResponse(BizExceptionStatusEnum.USER_DELETE_ERROR);
        }
        return APIResponse.toOkResponse();
    }

    /**
     * userId 不为空修改该id的密码。为空则修改自己密码
     *
     * @param userId
     * @param oldPass
     * @param newPass
     * @param confPass
     * @return
     */
    @PostMapping("/password")
    public APIResponse changePassword(
            @RequestParam(value = "userId", required = false) String userId,
            @RequestParam("oldPass") String oldPass,
            @RequestParam("newPass") String newPass,
            @RequestParam("confPass") String confPass) {

        bizLogger.info(new StringBuilder().append("账号：").append(getAccount()).append("　修改用户密码").append(userId));
        try {
            // 两次密码一致
            if (!newPass.equals(confPass)) {
                return APIResponse.toExceptionResponse(BizExceptionStatusEnum.USER_OLD_NEW_PWD_ERROR);
            }
            // 传入userId是修改其他人密码，不传入则是修改自己密码.并判断是否有权限修改
            if (StringUtils.isEmpty(userId)) {
                // 修改自己密码
                if (userService.changePassword(super.getAccount(), oldPass, newPass, true)) {
                    return APIResponse.toOkResponse();
                } else {
                    return APIResponse.toExceptionResponse(BizExceptionStatusEnum.USER_OLD_PWD_ERROR);
                }
            } else {
                // 判断当前用户是否有权限修改别人密码
                // 获取操作者角色
                List<Integer> roleList = super.getRoles();

                // 根据角色获取权限
                List<SysPermissionDto> perms = permService.getPermByRoleIds(roleList);
                boolean permFlg = false;
                for (SysPermissionDto dto : perms) {
                    if (Constants.PERM_TYPE_DEPART_ADMIN.equals(dto.getName()) ||
                            Constants.PERM_TYPE_USER_MANAGE.equals(dto.getName())) {
                        permFlg = true;
                        break;
                    }
                }
                // 有权限修改他人密码
                if (permFlg) {
                    if (userService.changePassword(userId, oldPass, newPass, false)) {
                        return APIResponse.toOkResponse();
                    }
                } else {
                    // 无权限修改他人密码
                    return APIResponse.toExceptionResponse(BizExceptionStatusEnum.USER_HAS_NO_ROLE_ERROR);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e);
            return APIResponse.toExceptionResponse(SysExceptionStatusEnum.SERVER_ERROR);
        }

        return APIResponse.toExceptionResponse(BizExceptionStatusEnum.USER_CHG_PWD_ERROR);

    }
}
