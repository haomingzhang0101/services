package com.haoming.house.api.controller;


import com.haoming.house.api.common.*;
import com.haoming.house.api.model.Agency;
import com.haoming.house.api.model.User;
import com.haoming.house.api.service.AccountService;
import com.haoming.house.api.service.AgencyService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class UserController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private AgencyService agencyService;

    /**
     * 注册提交：1、注册验证 2、发送邮件 3、验证失败重定向到注册页面
     * 注册页获取：根据account对象为依据判断是否为注册页获取
     * @author zhanghm
     * @date 2018-07-10 23:22
     */
    @RequestMapping("accounts/register")
    public String accountsRegister(User account, ModelMap modelMap) {
        if (account == null || account.getName() == null) {
            return "/user/accounts/register";
        }
        ResultMsg resultMsg = UserHelper.validate(account);

        if (resultMsg.isSuccess()) {
            boolean exist = accountService.isExist(account.getEmail());
            if (!exist) {
                accountService.addAccount(account);
                modelMap.put("email", account.getEmail());
                return "/user/accounts/registerSubmit";
            } else {
                return "redirect:/accounts/register?" + resultMsg.asUrlParams();
            }
        } else {
            return "redirect:/accounts/register?" + resultMsg.asUrlParams();
        }
    }

    @RequestMapping("accounts/verify")
    public String verify(String key) {
        boolean result = accountService.enable(key);
        if (result) {
            return "redirect:/index?" + ResultMsg.successMsg("激活成功").asUrlParams();
        } else {
            return "redirect:/accounts/register?" + ResultMsg.errorMsg("激活失败，链接可能过期").asUrlParams();
        }
    }

    //----------------------------登录流程-------------------------------------------


    @RequestMapping(value="/accounts/signin",method={RequestMethod.POST,RequestMethod.GET})
    public String loginSubmit(HttpServletRequest req){
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        if (username == null || password == null) {
            req.setAttribute("target", req.getParameter("target"));
            return "/user/accounts/signin";
        }
        User user =  accountService.auth(username, password);
        if (user == null) {
            return "redirect:/accounts/signin?" + "username=" + username + "&" + ResultMsg.errorMsg("用户名或密码错误").asUrlParams();
        }else {
            UserContext.setUser(user);
            return  StringUtils.isNotBlank(req.getParameter("target")) ? "redirect:" + req.getParameter("target") : "redirect:/index";
        }
    }

    /**
     *
     * @param req
     * @return
     */
    @RequestMapping("accounts/logout")
    public String logout(HttpServletRequest req){
        User user = UserContext.getUser();
        accountService.logout(user.getToken());
        return "redirect:/index";
    }

    @RequestMapping("index")
    public String accountsRegister(ModelMap modelMap){
        return "/homepage/index";
    }

    //----------------------------个人信息修改--------------------------------------
    @RequestMapping(value="accounts/profile",method={RequestMethod.POST,RequestMethod.GET})
    public String profile(ModelMap  model){
        List<Agency> list =  agencyService.getAllAgency();
        model.addAttribute("agencyList", list);
        return "/user/accounts/profile";
    }

    @RequestMapping(value="accounts/profileSubmit",method={RequestMethod.POST,RequestMethod.GET})
    public String profileSubmit(HttpServletRequest req,User updateUser,ModelMap  model){
        if (updateUser.getEmail() == null) {
            return "redirect:/accounts/profile?" + ResultMsg.errorMsg("邮箱不能为空").asUrlParams();
        }
        User user = accountService.updateUser(updateUser);
        UserContext.setUser(user);
        return "redirect:/accounts/profile?" + ResultMsg.successMsg("更新成功").asUrlParams();
    }
}
