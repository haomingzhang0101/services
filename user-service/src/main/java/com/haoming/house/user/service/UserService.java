package com.haoming.house.user.service;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.haoming.house.user.common.UserException;
import com.haoming.house.user.mapper.UserMapper;
import com.haoming.house.user.model.User;
import com.haoming.house.user.utils.BeanHelper;
import com.haoming.house.user.utils.HashUtils;
import com.haoming.house.user.utils.JwtHelper;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.haoming.house.user.common.UserException.Type.USER_AUTH_FAIL;
import static com.haoming.house.user.common.UserException.Type.USER_NOT_FOUND;
import static com.haoming.house.user.common.UserException.Type.USER_NOT_LOGIN;

@Service
public class UserService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MailService mailService;

    @Value("${file.prefix}")
    private String imgPrefix;

    /**
     * 1、首先通过缓存获取，设置缓存时间5分钟
     * 2、不存在将从通过数据库获取用户对象
     * 3、将用户对象写入缓存，设置缓存时间5分钟
     * 4、返回对象
     * @author zhanghm
     * @date 2018-08-12 04:47
     */
    public User getUserById(Long id) {
        String key = "user:" + id;
        String json = redisTemplate.opsForValue().get(key);
        User user = null;
        if (Strings.isNullOrEmpty(json)) {
            user = userMapper.selectById(id);
            user.setAvatar(imgPrefix + user.getAvatar());
            String string = JSON.toJSONString(user);
            redisTemplate.opsForValue().set(key, string);
            redisTemplate.expire(key, 5, TimeUnit.MINUTES);
        } else {
            user = JSON.parseObject(json, User.class);
        }
        return user;
    }

    public List<User> getUserByQuery(User user) {
        List<User> users = userMapper.select(user);
        users.forEach(u -> {
            u.setAvatar(imgPrefix + u.getAvatar());
        });
        return users;
    }

    /**
     * 注册：添加用户
     * @author zhanghm
     * @date 2018-08-13 03:56
     */
    public void addAccount(User user, String enableUrl) {
        user.setPasswd(HashUtils.encryPassword(user.getPasswd()));
        BeanHelper.onInsert(user);
        userMapper.insert(user);
        registerNotify(user.getEmail(), enableUrl);
    }

    private void registerNotify(String email, String enableUrl) {
        String randomKey = HashUtils.hashString(email) + RandomStringUtils.randomAlphabetic(10);
        redisTemplate.opsForValue().set(randomKey, email);
        redisTemplate.expire(randomKey, 1, TimeUnit.HOURS);
        String content = enableUrl + "?key=" + randomKey;
        mailService.sendSimpleMail("Activation code from Haofang", content, email);
    }

    public boolean enable(String key) {
        String email = redisTemplate.opsForValue().get(key);
        if (StringUtils.isBlank(email)) {
            throw new UserException(USER_NOT_FOUND, "Invalid key");
        }
        User updateUser = new User();
        updateUser.setEmail(email);
        updateUser.setEnable(1);
        userMapper.update(updateUser);
        return true;
    }

    public User auth(String email, String passwd) {
        if (StringUtils.isBlank(email) || StringUtils.isBlank(passwd)) {
            throw new UserException(UserException.Type.USER_AUTH_FAIL, "User auth failed");
        }
        User user = new User();
        user.setEmail(email);
        user.setPasswd(HashUtils.encryPassword(passwd));
        user.setEnable(1);
        List<User> list = getUserByQuery(user);
        if (!list.isEmpty()) {
            User retUser = list.get(0);
            onLogin(retUser);
            return retUser;
        }
        throw new UserException(USER_AUTH_FAIL, "User auth failed");
    }

    private void onLogin(User user) {
        String token = JwtHelper.genToken(ImmutableMap.of("email", user.getEmail(), "name", user.getName(), "ts", Instant.now().getEpochSecond() + ""));
        renewToken(token, user.getEmail());
        user.setToken(token);
    }

    private String renewToken(String token, String email) {
        redisTemplate.opsForValue().set(email, token);
        redisTemplate.expire(email, 30, TimeUnit.MINUTES);
        return token;
    }

    public User getLoginedUserByToken(String token) {
        Map<String, String> map = null;
        try {
            map = JwtHelper.verifyToken(token);
        } catch (Exception e) {
            throw new UserException(USER_NOT_LOGIN, "User not log in");
        }
        String email = map.get("email");
        Long expired = redisTemplate.getExpire(email);
        if (expired > 0) {
            renewToken(token, email);
            User user = getUserByEmail(email);
            user.setToken(token);
            return user;
        }
        throw new UserException(USER_NOT_LOGIN, "User not log in");
    }

    private User getUserByEmail(String email) {
        User user = new User();
        user.setEmail(email);
        List<User> list = getUserByQuery(user);
        if (!list.isEmpty()) {
            return list.get(0);
        }
        throw new UserException(UserException.Type.USER_NOT_FOUND, "User not found for " + email);
    }

    public void invalidate(String token) {
        Map<String, String> map = JwtHelper.verifyToken(token);
        redisTemplate.delete(map.get("email"));

    }

    @Transactional(rollbackFor = Exception.class)
    public User updateUser(User user) {
        if (user.getEmail() == null) {
            return null;
        }
        if (!Strings.isNullOrEmpty(user.getPasswd()) ) {
            user.setPasswd(HashUtils.encryPassword(user.getPasswd()));
        }
        userMapper.update(user);
        return userMapper.selectByEmail(user.getEmail());
    }
}
