package com.manage.system.controller;

import com.manage.common.APIResponse;
import com.manage.common.BaseController;
import com.manage.common.UrlConstants;
import com.manage.exception.impl.BizExceptionStatusEnum;
import com.manage.exception.impl.SysExceptionStatusEnum;
import com.manage.system.bean.UserBean;
import com.manage.system.service.UserService;
import com.manage.util.JwtTokenUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Controller
public class LoginController extends BaseController {

    private Log logger = LogFactory.getLog(LoginController.class);

    public static final String LOGIN_HTML = "index.html";

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserService userService;

    @RequestMapping("/")
    public String index() {
        return LOGIN_HTML;
    }

    @RequestMapping(value = "/index")
    public ModelAndView goIndex() {
        ModelAndView model = new ModelAndView("/view/main.html");
        return model;
    }

    @PostMapping(value = "/signout")
    public ModelAndView logout() {
        return new ModelAndView(LOGIN_HTML);
    }


    @PostMapping(value = "${jwt.auth-path}")
    @ResponseBody
    public APIResponse login(
            @RequestParam("account") String account,
            @RequestParam("password") String password) {

        final String randomKey = jwtTokenUtil.getRandomKey();
        //获取用户和对应的角色
        UserBean userBean = null;
        try {
            userBean = userService.selectUserAndRoleByAccount(account);
        } catch (Exception e) {
            logger.error(e);
            return APIResponse.toExceptionResponse(SysExceptionStatusEnum.SERVER_ERROR);
        }

        if (userBean != null && BCrypt.checkpw(password, userBean.getPassword())) {
            final String token = jwtTokenUtil.generateToken(userBean, randomKey);
            return APIResponse.toOkResponse(token);
        }
        return APIResponse.toExceptionResponse(BizExceptionStatusEnum.AUTH_REQUEST_ERROR.getCode(),
                BizExceptionStatusEnum.AUTH_REQUEST_ERROR.getMessage());
    }

    @GetMapping(value = "/loginUser")
    @ResponseBody
    public APIResponse getUserInfo(){
        String userId = super.getAccount();
        try {
            UserBean userBean = userService.selectByPrimaryKey(userId);
            return APIResponse.toOkResponse(userBean);
        } catch (Exception e) {
            logger.error(e);
            return APIResponse.toExceptionResponse(SysExceptionStatusEnum.SERVER_ERROR);
        }
    }

}
