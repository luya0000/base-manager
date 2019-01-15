package com.manage.interceptor;

import com.manage.auth.properties.JwtProperties;
import com.manage.common.APIResponse;
import com.manage.common.Constants;
import com.manage.exception.impl.SysExceptionStatusEnum;
import com.manage.util.JwtTokenUtil;
import com.manage.util.RenderUtil;
import io.jsonwebtoken.JwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;

/**
 * 对客户端请求的jwt token验证过滤器
 */
public class AuthFilter extends OncePerRequestFilter {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JwtProperties jwtProperties;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        // 跟路径。资源路径跳过
        if (request.getServletPath().equals("/") || request.getServletPath().contains("/plugins") ||
                request.getServletPath().contains("/resource") || request.getServletPath().contains("/view") ||
                request.getServletPath().equals("/" + jwtProperties.getAuthPath()) || request.getServletPath().contains("/index")) {
            chain.doFilter(request, response);
            return;
        }
        String requestHeader = request.getHeader(jwtProperties.getHeader().toLowerCase());
        String authToken = null;
        if (requestHeader == null && request.getParameterMap() != null && request.getParameterMap().get(jwtProperties.getHeader()) != null) {
            requestHeader = request.getParameterMap().get(jwtProperties.getHeader())[0];
        }
        if (requestHeader != null && requestHeader.startsWith("Bearer")) {
            authToken = requestHeader.substring(7);
            logger.debug(authToken);
            //验证token是否过期,包含了验证jwt是否正确
            try {
                boolean flag = jwtTokenUtil.isTokenExpired(authToken);
                if (flag) {
                    RenderUtil.renderJson(response, APIResponse.toExceptionResponse(SysExceptionStatusEnum.TOKEN_EXPIRED));
                    response.sendRedirect("/");
                    return;
                }
            } catch (JwtException e) {
                //有异常就是token解析失败
                RenderUtil.renderJson(response, APIResponse.toExceptionResponse(SysExceptionStatusEnum.TOKEN_ERROR));
                response.sendRedirect("/");
                return;
            }

            request.setAttribute(Constants.JWT_ROLES_KEY, jwtTokenUtil.getRolesKeyFromToken(authToken));
            request.setAttribute(Constants.JWT_SUB_KEY, jwtTokenUtil.getDepartFromToken(authToken));
            request.setAttribute(Constants.JWT_ACCOUNT_KEY, jwtTokenUtil.getAccountFromToken(authToken));
        } else {
            //header没有带Bearer字段
            RenderUtil.renderJson(response, APIResponse.toExceptionResponse(SysExceptionStatusEnum.TOKEN_ERROR));
            response.sendRedirect("/");
            return;
        }
        chain.doFilter(request, response);
    }
}