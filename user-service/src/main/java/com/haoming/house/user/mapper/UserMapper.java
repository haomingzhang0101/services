package com.haoming.house.user.mapper;

import com.haoming.house.user.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface UserMapper {
    User selectById(Long id);

    List<User> select(User user);

    int update(User user);

    int insert(User account);

    int delete(String email);

    User selectByEmail(String email);
}
