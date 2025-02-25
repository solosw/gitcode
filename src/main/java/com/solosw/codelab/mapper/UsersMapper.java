package com.solosw.codelab.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.solosw.codelab.entity.po.Users;
import jakarta.persistence.criteria.From;
import org.apache.ibatis.annotations.Select;

import java.util.List;


public interface UsersMapper extends BaseMapper<Users> {

    @Select("select * from users where (name=#{info} or email=#{info}) and password=#{password}")
    Users getUsersByNameOrEmailAndPassword(String info,String password);

    @Select("select * from users as u ,(select creator_id, member_ids from origization where id=#{OrzId}) as orz " +
            " where u.id=orz.creator_id or   CAST(orz.member_ids AS VARCHAR) like CONCAT('%', CAST(u.id AS VARCHAR), '%') ")
    List<Users> getUserByOrz(Long OrzId);
}
