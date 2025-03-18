package com.solosw.codelab.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.solosw.codelab.entity.po.House;
import com.solosw.codelab.entity.po.HouseRight;
import com.solosw.codelab.mapper.HouseRightMapper;
import com.solosw.codelab.repositorys.HouseRightRospitory;
import com.solosw.codelab.repositorys.HouseRospitory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HouseRightService extends BaseService<HouseRightMapper, HouseRightRospitory, HouseRight>{

    public void deleteByHouseId(Long houseId){
        mapper.delete(new QueryWrapper<HouseRight>().eq("house_id",houseId));
    }

    public void deleteHouseByHouseIdAndUserId(Long houseId,Long userId){
        mapper.delete(new QueryWrapper<HouseRight>().eq("house_id",houseId).eq("user_id",userId));
    }
    public HouseRight getHouseRightByUserAndHouse(Long userId, Long houseId){
        return mapper.selectOne(new QueryWrapper<HouseRight>().eq("user_id",userId).eq("house_id",houseId));
    }

    public List<HouseRight> getHouseRightByHouse(Long houseId){
        return mapper.selectList(new QueryWrapper<HouseRight>().eq("house_id",houseId));
    }
    public List<HouseRight> getHouseRightByUser(Long userId){
        return mapper.selectList(new QueryWrapper<HouseRight>().eq("user_id",userId));
    }

}
