package com.haoming.house.api.controller;

import com.google.common.base.Objects;
import com.haoming.house.api.common.*;
import com.haoming.house.api.model.House;
import com.haoming.house.api.model.User;
import com.haoming.house.api.service.AgencyService;
import com.haoming.house.api.service.HouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class HouseController {

    @Autowired
    private HouseService houseService;

    @Autowired
    private AgencyService agencyService;

    @RequestMapping(value="house/list",method={RequestMethod.POST,RequestMethod.GET})
    public String houseList(Integer pageSize, Integer pageNum, House query, ModelMap modelMap){
        PageData<House> ps = houseService.queryHouse(query, PageParams.build(pageSize, pageNum));
        List<House> rcHouses =  houseService.getHotHouse(CommonConstants.RECOM_SIZE);
        modelMap.put("recomHouses", rcHouses);
        modelMap.put("vo", query);
        modelMap.put("ps", ps);
        return "/house/listing";
    }

    @RequestMapping(value="house/detail",method={RequestMethod.POST,RequestMethod.GET})
    public String houseDetail(long id,ModelMap modelMap){
        House house = houseService.queryOneHouse(id);
        List<House> rcHouses =  houseService.getHotHouse(CommonConstants.RECOM_SIZE);
        if (house.getUserId() != null) {
            if (!Objects.equal(0L, house.getUserId())) {
                modelMap.put("agent", agencyService.getAgentDetail(house.getUserId()));
            }
        }
        modelMap.put("house", house);
        modelMap.put("recomHouses", rcHouses);
        return "/house/detail";
    }

    @RequestMapping(value="house/bookmark",method={RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public ResultMsg bookmark(Long id, ModelMap modelMap){
        User user = UserContext.getUser();
        houseService.bindUser2House(id, user.getId(), true);
        return ResultMsg.success();
    }

    @RequestMapping(value="house/unbookmark",method={RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public ResultMsg unbookmark(Long id,ModelMap modelMap){
        User user = UserContext.getUser();
        houseService.unbindUser2House(id, user.getId(), true);
        return ResultMsg.success();
    }

}
