package com.haoming.house.api.dao;

import com.google.common.collect.Lists;
import com.haoming.house.api.common.RestResponse;
import com.haoming.house.api.config.GenericRest;
import com.haoming.house.api.model.Agency;
import com.haoming.house.api.model.User;
import com.haoming.house.api.utils.Rests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserDao {

    @Autowired
    private GenericRest rest;

    @Value("${user.service.name}")
    private String userServiceName;

    public List<User> getUserList(User query) {
        ResponseEntity<RestResponse<List<User>>> resultEntity = rest.post("http://" + userServiceName + "/user/getList", query, new ParameterizedTypeReference<RestResponse<List<User>>>() {
        });
        RestResponse<List<User>> restResponse = resultEntity.getBody();
        if (restResponse.getCode() == 0) {
            return restResponse.getResult();
        } else {
            return Lists.newArrayList();
        }
    }


    public User addUser(User account) {
        String url = "http://" + userServiceName + "/user/add";
        ResponseEntity<RestResponse<User>> responseEntity = rest.post(url, account, new ParameterizedTypeReference<RestResponse<User>>() {
        });
        RestResponse<User> restResponse = responseEntity.getBody();
        if (restResponse.getCode() == 0) {
            return restResponse.getResult();
        } else {
            throw new IllegalStateException("Can not add user");
        }
    }

    public boolean enable(String key) {
        String url = "http://" + userServiceName + "/user/enable?key=" + key;
        RestResponse<Object> response = rest.get(url, new ParameterizedTypeReference<RestResponse<Object>>() {
        }).getBody();
        return response.getCode() == 0;
    }


    public User authUser(User user) {
        String url = "http://" + userServiceName + "/user/auth";
        ResponseEntity<RestResponse<User>> responseEntity = rest.post(url, user, new ParameterizedTypeReference<RestResponse<User>>() {
        });
        RestResponse<User> restResponse = responseEntity.getBody();
        if (restResponse.getCode() == 0) {
            return restResponse.getResult();
        } else {
            throw new IllegalStateException("Can not auth user");
        }
    }

    public void logout(String token) {
        String url = "http://" + userServiceName + "/user/logout?token=" + token;
        rest.get(url, new ParameterizedTypeReference<RestResponse<Object>>() {
        });
    }

    public User getUserByToken(String token) {
        String url = "http://" + userServiceName + "/user/get?token=" + token;
        ResponseEntity<RestResponse<User>> responseEntity = rest.get(url, new ParameterizedTypeReference<RestResponse<User>>() {
        });
        RestResponse<User> response = responseEntity.getBody();
        if (response == null || response.getCode() != 0) {
            return null;
        }
        return response.getResult();
    }

    public List<Agency> getAllAgency() {
        return Rests.exc(() -> {
            String url = Rests.toUrl(userServiceName, "/agency/list");
            ResponseEntity<RestResponse<List<Agency>>> responseEntity =
                    rest.get(url, new ParameterizedTypeReference<RestResponse<List<Agency>>>() {
            });
            return responseEntity.getBody();
        }).getResult();
    }

    public User updateUser(User user) {
        return Rests.exc(() -> {
            String url = Rests.toUrl(userServiceName, "/user/update");
            ResponseEntity<RestResponse<User>> responseEntity = rest.post(url, user, new ParameterizedTypeReference<RestResponse<User>>() {
            });
            return responseEntity.getBody();
        }).getResult();
    }

    public User getAgentById(Long id) {
        return Rests.exc(() -> {
            String url = Rests.toUrl(userServiceName, "/agency/agentDetail?id="+id);
            ResponseEntity<RestResponse<User>> responseEntity = rest.get(url, new ParameterizedTypeReference<RestResponse<User>>() {
            });
            return responseEntity.getBody();
        }).getResult();
    }
}
