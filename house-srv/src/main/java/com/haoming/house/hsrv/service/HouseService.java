package com.haoming.house.hsrv.service;

import com.google.common.collect.Lists;
import com.haoming.house.hsrv.common.BeanHelper;
import com.haoming.house.hsrv.common.HouseUserType;
import com.haoming.house.hsrv.common.LimitOffset;
import com.haoming.house.hsrv.dao.UserDao;
import com.haoming.house.hsrv.mapper.HouseMapper;
import com.haoming.house.hsrv.model.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HouseService {

    @Autowired
    private HouseMapper houseMapper;

    @Autowired
    private MailService mailService;

    @Autowired
    private UserDao userDao;

    @Value("${file.prefix}")
    private String imgPrefix;


    public List<House> queryAndSetImg(House query, LimitOffset pageParams){
        List<House> houses =  houseMapper.selectHouse(query,pageParams);
        houses.forEach(h -> {
            h.setFirstImg(imgPrefix + h.getFirstImg());
            h.setImageList(h.getImageList().stream().map(img -> imgPrefix + img).collect(Collectors.toList()));
            h.setFloorPlanList(h.getFloorPlanList().stream().map(img -> imgPrefix + img).collect(Collectors.toList()));
        });
        return houses;
    }

    /**
     * 1. Add property.
     * 2. Bind the property to a user.
     * @author zhanghm
     * @date 2018-08-19 18:17
     */
    @Transactional(rollbackFor = Exception.class)
    public void addHouse(House house, Long userId) {
        BeanHelper.setDefaultProp(house, House.class);
        BeanHelper.onInsert(house);
        houseMapper.insert(house);
        bindUser2House(house.getId(), userId, HouseUserType.SALE);
    }

    @Transactional(rollbackFor = Exception.class)
    public void bindUser2House(Long id, Long userId, HouseUserType sale) {
        HouseUser existHouseUser = houseMapper.selectHouseUser(userId, id, sale.value);
        if (existHouseUser != null) {
            return;
        }
        HouseUser houseUser = new HouseUser();
        houseUser.setHouseId(id);
        houseUser.setUserId(userId);
        houseUser.setType(sale.value);
        BeanHelper.setDefaultProp(houseUser, HouseUser.class);
        BeanHelper.onInsert(houseUser);
        houseMapper.insertHouseUser(houseUser);
    }

    public void unbindUser2Houser(Long houseId, Long userId, HouseUserType type) {
        houseMapper.deleteHouseUser(houseId, userId, type.value);
    }

    public Pair<List<House>,Long> queryHouse(House query, LimitOffset build) {
        List<House> houses = Lists.newArrayList();
        House houseQuery = query;
        if (StringUtils.isNoneBlank(query.getName())) {
            Community community = new Community();
            community.setName(query.getName());
            List<Community> communities = houseMapper.selectCommunity(community);
            if (!communities.isEmpty()) {
                houseQuery = new House();
                houseQuery.setCommunityId(communities.get(0).getId());
            }
        }
        houses = queryAndSetImg(houseQuery, build);
        Long count = houseMapper.selectHouseCount(houseQuery);
        return ImmutablePair.of(houses, count);
    }

    public House queryOneHouse(long id) {
        House query = new House();
        query.setId(id);
        List<House> houses = queryAndSetImg(query, LimitOffset.build(1, 0));
        if (!houses.isEmpty()) {
            return houses.get(0);
        }
        return null;
    }

    public void addUserMsg(UserMsg userMsg) {
        BeanHelper.onInsert(userMsg);
        BeanHelper.setDefaultProp(userMsg, UserMsg.class);
        houseMapper.insertUserMsg(userMsg);
        User user = userDao.getAgentDetail(userMsg.getAgentId());
        mailService.sendSimpleMail("From user" + userMsg.getEmail(), userMsg.getMsg(), user.getEmail());
    }

    public void updateRating(Long id, Double rating) {
        House house = queryOneHouse(id);
        Double oldRating = house.getRating();
        Double newRating = oldRating.equals(0D) ? rating : Math.min(Math.round(oldRating + rating)/2, 5);
        House updateHouse = new House();
        updateHouse.setId(id);
        updateHouse.setRating(newRating);
        houseMapper.updateHouse(updateHouse);
    }
}
