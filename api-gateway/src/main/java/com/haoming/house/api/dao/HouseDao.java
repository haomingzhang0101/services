package com.haoming.house.api.dao;

import com.haoming.house.api.common.HouseUserType;
import com.haoming.house.api.common.RestResponse;
import com.haoming.house.api.config.GenericRest;
import com.haoming.house.api.model.House;
import com.haoming.house.api.model.HouseQueryReq;
import com.haoming.house.api.model.HouseUserReq;
import com.haoming.house.api.model.ListResponse;
import com.haoming.house.api.utils.Rests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class HouseDao {

    @Autowired
    private GenericRest rest;

    @Value("${house.service.name}")
    private String houseServiceName;


    public ListResponse<House> getHouses(House query, Integer limit, Integer offset) {
        return Rests.exc(() -> {
            HouseQueryReq req = new HouseQueryReq();
            req.setLimit(limit);
            req.setOffset(offset);
            req.setQuery(query);
            String url = Rests.toUrl(houseServiceName, "/house/list");
            ResponseEntity<RestResponse<ListResponse<House>>> responseEntity = rest.post(url,req,new ParameterizedTypeReference<RestResponse<ListResponse<House>>>() {});
            return responseEntity.getBody();
        }).getResult();
    }

    public List<House> getHotHouse(Integer recomSize) {
        return Rests.exc(() ->{
            String url = Rests.toUrl(houseServiceName, "/house/hot" + "?size="+recomSize);
            ResponseEntity<RestResponse<List<House>>> responseEntity = rest.get(url, new ParameterizedTypeReference<RestResponse<List<House>>>() {});
            return responseEntity.getBody();
        }).getResult();
    }

    public House getOneHouse(long id) {
        return Rests.exc(() -> {
            String url = Rests.toUrl(houseServiceName, "/house/detail?id=" + id);
            ResponseEntity<RestResponse<House>> responseEntity = rest.get(url, new ParameterizedTypeReference<RestResponse<House>>() {});
            return responseEntity.getBody();
        }).getResult();
    }

    public void bindUser2House(Long houseId, Long userId, boolean bookmark) {
        HouseUserReq req = new HouseUserReq();
        req.setUnBind(false);
        req.setBindType(HouseUserType.BOOKMARK.value);
        req.setUserId(userId);
        req.setHouseId(houseId);
        bindOrUnbind(req);
    }

    private void bindOrUnbind(HouseUserReq req) {
        Rests.exc(() ->{
            String url = Rests.toUrl(houseServiceName, "/house/bind");
            ResponseEntity<RestResponse<Object>> responseEntity = rest.post(url,req,new ParameterizedTypeReference<RestResponse<Object>>() {});
            return responseEntity.getBody();
        });
    }

    public void unbindUser2House(Long houseId, Long userId, boolean bookmark) {
        HouseUserReq req = new HouseUserReq();
        req.setUnBind(true);
        req.setBindType(HouseUserType.BOOKMARK.value);
        req.setUserId(userId);
        req.setHouseId(houseId);
        bindOrUnbind(req);
    }
}
