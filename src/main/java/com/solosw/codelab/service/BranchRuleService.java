package com.solosw.codelab.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.solosw.codelab.entity.po.BranchRule;
import com.solosw.codelab.mapper.BranchRuleMapper;
import com.solosw.codelab.repositorys.BranchRuleRospitory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BranchRuleService extends BaseService<BranchRuleMapper, BranchRuleRospitory, BranchRule>{

    public void deleteByNameAndHouseId(String name,Long houseId){
        mapper.delete(new QueryWrapper<BranchRule>().eq("name",name).eq("house_id",houseId));
    }

    public List<BranchRule> getRuleByHouseId( Long houseId){
      return  mapper.selectList(new QueryWrapper<BranchRule>().eq("house_id",houseId));
    }
}
