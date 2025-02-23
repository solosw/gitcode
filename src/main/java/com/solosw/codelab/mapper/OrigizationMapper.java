package com.solosw.codelab.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.solosw.codelab.entity.po.Origization;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface OrigizationMapper extends BaseMapper<Origization> {

    @Select("select id,name,description,create_time,update_time,creator_id from origization where creator_id=#{userId} or CONCAT('%', CAST(#{userId} AS VARCHAR), '%') LIKE  member_ids")
    List<Origization> getOrigizationListByUserId(Long userId);

}
