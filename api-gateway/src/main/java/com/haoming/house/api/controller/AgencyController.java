package com.haoming.house.api.controller;

import com.haoming.house.api.model.User;
import com.haoming.house.api.service.AgencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AgencyController {

    @Autowired
    private AgencyService agencyService;

    @RequestMapping("/agency/agentDetail")
    public String agentDetail(Long id, ModelMap modelMap) {
        User user = agencyService.getAgentDetail(id);
        modelMap.put("agent", user);
        return "/user/agent/agentDetail";
    }
}
