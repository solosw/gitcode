package com.solosw.codelab.service;

import com.solosw.codelab.entity.po.UserInfo;
import com.solosw.codelab.entity.po.Users;
import com.solosw.codelab.mapper.UserInfoMapper;
import com.solosw.codelab.repositorys.UserInfoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserInfoService extends BaseService<UserInfoMapper, UserInfoRepository, UserInfo>{

    public UserInfo getUserInfoByUserIdOrId(Long id, Integer type){
        return mapper.getUserInfoByUserIdOrId(id,type);
    }


}
