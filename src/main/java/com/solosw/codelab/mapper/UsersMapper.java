package com.solosw.codelab.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.solosw.codelab.entity.po.Users;
import org.apache.ibatis.annotations.Select;


public interface UsersMapper extends BaseMapper<Users> {

    @Select("select * from users where (name=#{info} or email=#{info}) and password=#{password}")
    Users getUsersByNameOrEmailAndPassword(String info,String password);
}
