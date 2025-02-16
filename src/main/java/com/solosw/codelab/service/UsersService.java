package com.solosw.codelab.service;

import com.solosw.codelab.entity.po.Users;
import com.solosw.codelab.mapper.UsersMapper;
import com.solosw.codelab.repositorys.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsersService {
    @Autowired
    UsersMapper usersMapper;

    @Autowired
    UsersRepository usersRepository;
    public Long insert(Users users){
        usersRepository.save(users);
        return users.getId();
    }

    public Users getUsersByNameOrEmailAndPassword(String info,String password){
        return usersMapper.getUsersByNameOrEmailAndPassword(info,password);
    }
}
