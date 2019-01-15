package com.manage.common;

import com.manage.exception.DocException;
import com.manage.system.bean.UserBean;
import com.manage.system.service.UserService;
import com.manage.util.HttpKit;
import com.manage.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by luya on 2018/6/6.
 */
public class BaseController {
    @Autowired
    UserService userService;

    /* 获取部门 */
    public String getDepart() {
        return (HttpKit.getRequest().getAttribute(Constants.JWT_SUB_KEY)).toString();
    }
    /* 获取账号 */
    public String getAccount() {
        return HttpKit.getRequest().getAttribute(Constants.JWT_ACCOUNT_KEY).toString();
    }
    /* 获取角色 */
    public List<Integer> getRoles() {
        return (List<Integer>) HttpKit.getRequest().getAttribute(Constants.JWT_ROLES_KEY);
    }

    public String getUserName() {
        String userId = getAccount();
        try {
            UserBean user = userService.selectByPrimaryKey(userId);
            return user.getUserName();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 无任何信息，直接返回。
     *
     * @return
     */
    protected APIResponse response() {
        return APIResponse.toOkResponse();
    }

    /**
     * 无任何信息，直接返回数据对象。
     *
     * @param data
     * @return
     */
    protected APIResponse response(Object data) {
        return APIResponse.toOkResponse(data);
    }

    /**
     * 正常返回数据。
     *
     * @param data
     * @param msg
     * @return
     */
    protected APIResponse response(Object data, String msg) {
        return APIResponse.toOkResponse(data, msg);
    }

    /**
     * 异常返回数据操作。
     *
     * @param exception
     * @return
     */
    protected APIResponse responseException(DocException exception) {
        return toExceptionResponse(exception.getCode(), exception.getMessage());
    }

    /**
     * 异常返回数据
     *
     * @param code
     * @param msg
     * @return
     */
    protected APIResponse toExceptionResponse(int code, String msg) {
        return APIResponse.toExceptionResponse(code, msg);
    }

}
