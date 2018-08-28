package com.haoming.house.user.service;

import com.haoming.house.user.common.PageParams;
import com.haoming.house.user.mapper.AgencyMapper;
import com.haoming.house.user.model.Agency;
import com.haoming.house.user.model.User;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AgencyService {

    @Autowired
    private AgencyMapper agencyMapper;

    @Value("${file.prefix}")
    private String imgPrefix;

    public Pair<List<User>,Long> getAllAgent(PageParams pageParams) {
        List<User> agents = agencyMapper.selectAgent(new User(), pageParams);
        setImg(agents);
        Long count = agencyMapper.selectAgentCount(new User());
        return ImmutablePair.of(agents, count);
    }

    public void setImg(List<User> users){
        users.forEach(u -> {
            u.setAvatar(imgPrefix + u.getAvatar());
        });
    }

    public User getAgentDetail(Long id) {
        User user = new User();
        user.setId(id);
        user.setType(2);
        List<User> list = agencyMapper.selectAgent(user, new PageParams());
        setImg(list);
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    public List<Agency> getAllAgency() {
        return agencyMapper.select(new Agency());
    }
}
