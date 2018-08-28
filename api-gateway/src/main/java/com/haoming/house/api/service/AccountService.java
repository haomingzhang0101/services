package com.haoming.house.api.service;

import com.google.common.collect.Lists;
import com.haoming.house.api.dao.UserDao;
import com.haoming.house.api.model.User;
import com.haoming.house.api.utils.BeanHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private FileService fileService;

    @Value("${domain.name}")
    private String domainName;

    public boolean isExist(String email) {
        return getUser(email) != null;
    }

    private User getUser(String email) {
        User queryUser = new User();
        queryUser.setEmail(email);
        List<User> users = getUserByQuery(queryUser);
        if (!users.isEmpty()) {
            return users.get(0);
        }
        return null;
    }

    private List<User> getUserByQuery(User queryUser) {
        return userDao.getUserList(queryUser);
    }

    public boolean addAccount(User account) {
        if (account.getAvatarFile() != null) {
            List<String> imgs = fileService.getImgPath(Lists.newArrayList(account.getAvatarFile()));
            account.setAvatar(imgs.get(0));
        }
        account.setEnableUrl("http://"+domainName+"/accounts/verify");
        BeanHelper.setDefaultProp(account, User.class);
        userDao.addUser(account);
        return true;
    }

    public boolean enable(String key) {
       return userDao.enable(key);
    }

    public User auth(String username, String password) {
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            return null;
        }
        User user = new User();
        user.setEmail(username);
        user.setPasswd(password);
        try {
           user = userDao.authUser(user);
        } catch (Exception e) {
            return null;
        }
        return user;
    }

    public void logout(String token) {
        userDao.logout(token);
    }

    public User updateUser(User user) {
        BeanHelper.onUpdate(user);
        return userDao.updateUser(user);
    }
}
