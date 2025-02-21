package com.solosw.codelab.service;

import com.solosw.codelab.entity.po.Origization;
import com.solosw.codelab.entity.po.Users;
import com.solosw.codelab.mapper.OrigizationMapper;
import com.solosw.codelab.mapper.UsersMapper;
import com.solosw.codelab.repositorys.OrigizationRospitory;
import com.solosw.codelab.repositorys.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsersService extends BaseService<UsersMapper, UsersRepository, Users>{



    public Users getUsersByNameOrEmailAndPassword(String info,String password){
        return mapper.getUsersByNameOrEmailAndPassword(info,password);
    }
}
