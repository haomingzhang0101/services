package com.haoming.house.hsrv.mapper;

import java.util.List;

import com.haoming.house.hsrv.model.City;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;


@Mapper
@Repository
public interface CityMapper {
  
  public List<City> selectCitys(City city);

}
