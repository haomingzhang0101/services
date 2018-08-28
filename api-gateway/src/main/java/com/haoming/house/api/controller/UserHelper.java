package com.haoming.house.api.controller;


import com.haoming.house.api.common.ResultMsg;
import com.haoming.house.api.model.User;
import org.apache.commons.lang3.StringUtils;

public class UserHelper {

    public static ResultMsg validate(User account) {
        if (StringUtils.isEmpty(account.getEmail())) {
            return ResultMsg.errorMsg("Email address is empty");
        }

        if (StringUtils.isEmpty(account.getName())) {
            return ResultMsg.errorMsg("Name is empty");
        }

        if (StringUtils.isEmpty(account.getPasswd()) || StringUtils.isEmpty(account.getConfirmPasswd())
                || !account.getPasswd().equals(account.getConfirmPasswd())) {
            return ResultMsg.errorMsg("Password is empty or wrong password confirm");
        }

        if (account.getPasswd().length() < 6) {
            return ResultMsg.errorMsg("Password should have at least 6 characters");
        }

        return ResultMsg.successMsg("");
    }
}
