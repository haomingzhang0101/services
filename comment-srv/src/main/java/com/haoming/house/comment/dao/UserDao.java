package com.haoming.house.comment.dao;

import com.haoming.house.comment.common.RestResponse;
import com.haoming.house.comment.common.Rests;
import com.haoming.house.comment.model.User;
import com.haoming.house.comment.service.GenericRest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import com.google.common.collect.ImmutableMap;


@Repository
public class UserDao {

  @Autowired
  private GenericRest rest;
  
  @Value("${user.service.name}")
  private String userServiceName;

  public User getUserDetail(Long userId) {
    RestResponse<User> resp = Rests.exc(new Rests.RestFunction<RestResponse<User>>() {

      @Override
      public RestResponse<User> call() throws Exception {
        String url = Rests.toUrl(userServiceName, "/user/getById" + "?id="+userId);
        withParams(ImmutableMap.of("userId", userId));
        ResponseEntity<RestResponse<User>> responseEntity = rest.get(url,new ParameterizedTypeReference<RestResponse<User>>() {});
        return responseEntity.getBody();

      }
     });
     return resp.getResult();
  }
  
  
}
