package com.solosw.codelab.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.solosw.codelab.entity.po.Origization;
import com.solosw.codelab.entity.po.Users;
import com.solosw.codelab.mapper.OrigizationMapper;
import com.solosw.codelab.mapper.UsersMapper;
import com.solosw.codelab.repositorys.OrigizationRospitory;
import com.solosw.codelab.repositorys.UsersRepository;
import org.antlr.v4.runtime.InterpreterRuleContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsersService extends BaseService<UsersMapper, UsersRepository, Users>{

    public List<Users> getAll(){
        return mapper.selectList(new QueryWrapper<Users>().eq("role",1));
    }

    public Users getUsersByNameOrEmailAndPassword(String info,String password){
        return mapper.getUsersByNameOrEmailAndPassword(info,password);
    }
    public List<Users> getUserByOrz(Long OrzId){
        return mapper.getUserByOrz(OrzId);
    }


    public Users getManager(){
       return mapper.selectOne(new QueryWrapper<Users>().eq("role",0));
    }

    public Users getUserByName(String name){
        return mapper.selectOne(new QueryWrapper<Users>().eq("name",name));
    }
}
