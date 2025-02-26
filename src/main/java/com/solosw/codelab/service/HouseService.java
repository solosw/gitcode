package com.solosw.codelab.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.solosw.codelab.entity.po.House;
import com.solosw.codelab.mapper.HouseMapper;
import com.solosw.codelab.repositorys.HouseRospitory;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HouseService extends BaseService<HouseMapper, HouseRospitory, House>{

    public List<House> list(Integer type,Long creatorId,Long originzationId){
        return mapper.list(type,creatorId,originzationId);
    }
    public List<House> ownList(Long creatorId){
        return mapper.ownList(creatorId);
    }

    public List<House> getHouseBySearch(String content){
      return   mapper.getHouseBySearch(content);
    }


}
