package com.solosw.codelab.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.solosw.codelab.entity.po.Wiki;
import com.solosw.codelab.mapper.WikiMapper;
import com.solosw.codelab.repositorys.WikiRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WikiService extends BaseService<WikiMapper, WikiRepository, Wiki>{

    public List<Wiki> getByHouseId(Long houseId){
        return  mapper.selectList(new QueryWrapper<Wiki>().eq("house_id",houseId));
    }
}
