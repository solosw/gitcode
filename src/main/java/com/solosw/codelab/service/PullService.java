package com.solosw.codelab.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.solosw.codelab.entity.po.Pull;
import com.solosw.codelab.mapper.PullMapper;
import com.solosw.codelab.repositorys.PullRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PullService extends BaseService<PullMapper, PullRepository, Pull>{

    public List<Pull>  getByHouseId(Long houseId) {
        return mapper.selectList(new QueryWrapper<Pull>().eq("house_id",houseId));
    }

    public List<Pull> getPullByHouseIdAndUserName(String username,Long houseId){
        return mapper.selectList(new QueryWrapper<Pull>().eq("house_id",houseId).eq("username",username));
    }
}
