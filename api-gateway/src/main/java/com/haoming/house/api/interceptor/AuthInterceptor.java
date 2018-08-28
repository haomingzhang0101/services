package com.haoming.house.api.interceptor;

import com.google.common.base.Joiner;
import com.haoming.house.api.common.CommonConstants;
import com.haoming.house.api.common.UserContext;
import com.haoming.house.api.dao.UserDao;
import com.haoming.house.api.model.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    public static final String TOKEN_COOKIE = "token";

    @Autowired
    private UserDao userDao;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        Map<String, String[]> map = request.getParameterMap();
        map.forEach((k,v) ->{
            if (k.equals("errorMsg") || k.equals("successMsg") || k.equals("target")) {
                request.setAttribute(k, Joiner.on(",").join(v));
            }
        });
        String reqUri = request.getRequestURI();
        if (reqUri.startsWith("/static") || reqUri.startsWith("/error")) {
            return true;
        }
        Cookie cookie = WebUtils.getCookie(request, TOKEN_COOKIE);
        if (cookie != null && StringUtils.isNoneBlank(cookie.getValue())) {
            User user = userDao.getUserByToken(cookie.getValue());
            request.setAttribute(CommonConstants.LOGIN_USER_ATTRIBUTE, user);
            UserContext.setUser(user);
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
        String requestURI = request.getRequestURI();
        if (requestURI.startsWith("/static") || requestURI.startsWith("/error")) {
            return ;
        }
        User user = UserContext.getUser();
        if (user != null && StringUtils.isNotBlank(user.getToken())) {
            String token = requestURI.startsWith("logout")? "":user.getToken();
            Cookie cookie = new Cookie(TOKEN_COOKIE, token);
            cookie.setPath("/");
            cookie.setHttpOnly(false);
            response.addCookie(cookie);
        }

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception e) throws Exception {
        UserContext.remove();
    }
}
