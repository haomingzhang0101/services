package com.haoming.house.user.controller;

import com.haoming.house.user.common.PageParams;
import com.haoming.house.user.common.RestResponse;
import com.haoming.house.user.model.Agency;
import com.haoming.house.user.model.ListResponse;
import com.haoming.house.user.model.User;
import com.haoming.house.user.service.AgencyService;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("agency")
public class AgencyController {

    @Autowired
    private AgencyService agencyService;

    @RequestMapping("list")
    public RestResponse<List<Agency>> agencyList() {
        List<Agency> agencies = agencyService.getAllAgency();
        return RestResponse.success(agencies);
    }

    @RequestMapping("agentList")
    public RestResponse<ListResponse<User>> agentList(Integer limit, Integer offset) {
        PageParams pageParams = new PageParams();
        pageParams.setLimit(limit);
        pageParams.setOffset(offset);
        Pair<List<User>, Long> pair = agencyService.getAllAgent(pageParams);
        ListResponse<User> response = ListResponse.build(pair.getKey(), pair.getValue());
        return RestResponse.success(response);
    }

    @RequestMapping("agentDetail")
    public RestResponse<User> agentDetail(Long id) {
        User user = agencyService.getAgentDetail(id);
        return RestResponse.success(user);
    }
}
