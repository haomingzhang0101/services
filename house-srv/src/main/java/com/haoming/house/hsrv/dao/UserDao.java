package com.haoming.house.hsrv.dao;

import com.haoming.house.hsrv.common.RestResponse;
import com.haoming.house.hsrv.model.User;
import com.haoming.house.hsrv.service.GenericRest;
import com.haoming.house.hsrv.utils.Rests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

@Repository
public class UserDao {

    @Autowired
    private GenericRest rest;

    @Value("${user.service.name}")
    private String userServiceName;

    public User getAgentDetail(Long agentId) {
        return Rests.exc(() -> {
            String url = Rests.toUrl(userServiceName, "/agency/agentDetail?id=" + agentId);
            ResponseEntity<RestResponse<User>> responseEntity = rest.get(url, new ParameterizedTypeReference<RestResponse<User>>() {
            });
            return responseEntity.getBody();
        }).getResult();
    }
}
