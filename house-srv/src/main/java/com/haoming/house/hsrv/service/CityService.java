package com.haoming.house.hsrv.service;

import java.util.List;

import com.haoming.house.hsrv.mapper.CityMapper;
import com.haoming.house.hsrv.model.City;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



@Service
public class CityService {
  
  @Autowired
  private CityMapper cityMapper;
  
  public List<City> getAllCitys(){
    City query = new City();
    return cityMapper.selectCitys(query);
  }

}
