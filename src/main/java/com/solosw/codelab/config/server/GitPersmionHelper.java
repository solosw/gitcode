package com.solosw.codelab.config.server;

import com.solosw.codelab.entity.po.*;
import com.solosw.codelab.service.*;
import org.h2.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GitPersmionHelper {
    @Autowired
    UsersService usersService;
    @Autowired
    PublicKeyService publicKeyService;
    @Autowired
    HouseRightService houseRightService;
    @Autowired
    BranchRuleService branchRuleService;
    @Autowired
    HouseService houseService;
    public Users getUserByName(String name){
        if(StringUtils.isNullOrEmpty(name)) return null;
        return  usersService.getUserByName(name);
    }

    public List<PublicKey> getPublicKeyByUser(Long userId){
        return publicKeyService.getPublicKeyByCreatorIdOrId(userId,0);
    }

    public House getHouseByPath(String path){
        return houseService.getHouseByPath(path);
    }

    public HouseRight getHouseRightByUserAndHouse(Long userId, Long houseId){
        return houseRightService.getHouseRightByUserAndHouse(userId,houseId);
    }

    public House getHouse(Long id){
        return houseService.selectById(id);
    }

    public List<BranchRule> getBranchRule(long houseID){
        return branchRuleService.getRuleByHouseId(houseID);
    }
}
