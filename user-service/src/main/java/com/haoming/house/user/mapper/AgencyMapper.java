package com.haoming.house.user.mapper;

import com.haoming.house.user.common.PageParams;
import com.haoming.house.user.model.Agency;
import com.haoming.house.user.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface AgencyMapper {

    List<Agency> select(Agency agency);

    int insert(Agency agency);

    List<User> selectAgent(@Param("user") User user, @Param("pageParams")PageParams pageParams);

    Long selectAgentCount(@Param("user") User user);
}
