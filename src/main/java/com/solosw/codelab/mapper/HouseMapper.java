package com.solosw.codelab.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.solosw.codelab.entity.po.House;
import jakarta.persistence.criteria.From;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface HouseMapper extends BaseMapper<House> {

    @Select({

            "<script>",
            "SELECT * FROM house",
            "<where>",
            "<choose>",
            "<when test='type == 0 and creatorId != null'>",
            "creator_id = #{creatorId}",
            "</when>",
            "<otherwise>",
            "<if test='originzationId != null and type == 1'>",
            "origization_id = #{originzationId} and creator_id = #{creatorId}",
            "</if>",
            "</otherwise>",
            "</choose>",
            "</where>",
            "</script>"
    })
    List<House> list(Integer type, Long creatorId, Long originzationId);


}
