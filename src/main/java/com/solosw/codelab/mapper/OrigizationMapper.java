package com.solosw.codelab.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.solosw.codelab.entity.po.Origization;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface OrigizationMapper extends BaseMapper<Origization> {

    @Select("select id,name,description from origization where creator_id=#{userId} or CONCAT(',', member_ids, ',') LIKE CONCAT('%,', CAST(#{userId} AS VARCHAR), ',%') ")
    List<Origization> getOrigizationListByUserId(Long userId);

}
