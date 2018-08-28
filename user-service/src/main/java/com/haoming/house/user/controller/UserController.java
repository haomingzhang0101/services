package com.haoming.house.user.controller;

import com.haoming.house.user.common.RestResponse;
import com.haoming.house.user.exception.IllegalParamsException;
import com.haoming.house.user.model.User;
import com.haoming.house.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("user")
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

//    @Value("${server.port}")
//    private String port;

//    @Autowired
//    private StringRedisTemplate redisTemplate;
    @Autowired
    private UserService userService;

    /*@RequestMapping("getusername")
    public RestResponse<String> getusername(Long id) {
        LOGGER.info("Incoming Request, and my server port is " + port);
        if (id == null) {
            throw new IllegalParamsException(IllegalParamsException.Type.WRONG_PAGE_NUM, "错误分页");
        }
        redisTemplate.opsForValue().set("key1", "val1");
        LOGGER.info("Test redis: " + redisTemplate.opsForValue().get("key1"));
        return RestResponse.success("test-username" + port);
    }*/
    //---------------------------------Search----------------------------------
    @RequestMapping("getById")
    public RestResponse<User> getUserById(Long id) {
        User user = userService.getUserById(id);
        return RestResponse.success(user);
    }

    @RequestMapping("getList")
    public RestResponse<List<User>> getUserList(@RequestBody User user) {
        List<User> users = userService.getUserByQuery(user);
        return RestResponse.success(users);
    }

    //----------------------------------Sign up---------------------------------
    @RequestMapping("add")
    public RestResponse<User> add(@RequestBody User user) {
        userService.addAccount(user, user.getEnableUrl());
        return RestResponse.success();
    }

    @RequestMapping("enable")
    public RestResponse<Object> enable(String key) {
        userService.enable(key);
        return RestResponse.success();
    }

    //----------------------------------Login/Authorize--------------------------
    @RequestMapping("auth")
    public RestResponse<User> auth(@RequestBody User user) {
        User finalUser = userService.auth(user.getEmail(), user.getPasswd());
        return RestResponse.success(finalUser);
    }
    @RequestMapping("get")
    public RestResponse<User> getUser(String token) {
        User finalUser = userService.getLoginedUserByToken(token);
        return RestResponse.success(finalUser);
    }
    @RequestMapping("logout")
    public RestResponse<Object> logout(String token) {
        userService.invalidate(token);
        return RestResponse.success();
    }
    @RequestMapping("update")
    public RestResponse<User> update(@RequestBody User user){
        User updateUser = userService.updateUser(user);
        return RestResponse.success(updateUser);
    }

}
