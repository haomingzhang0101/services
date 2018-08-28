package com.haoming.house.api.service;

import com.haoming.house.api.dao.UserDao;
import com.haoming.house.api.model.Agency;
import com.haoming.house.api.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AgencyService {
    
    @Autowired
    private UserDao userDao;

    public List<Agency> getAllAgency() {
        return userDao.getAllAgency();
    }

    public User getAgentDetail(Long id) {
        return userDao.getAgentById(id);
    }
}
