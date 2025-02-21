package com.solosw.codelab.service;

import com.solosw.codelab.entity.po.Origization;
import com.solosw.codelab.mapper.OrigizationMapper;
import com.solosw.codelab.repositorys.OrigizationRospitory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrigizationService extends BaseService<OrigizationMapper,OrigizationRospitory,Origization>{


    public List<Origization> getOrigizationListByUserId(Long userId){
        return mapper.getOrigizationListByUserId(userId);
    }

}
