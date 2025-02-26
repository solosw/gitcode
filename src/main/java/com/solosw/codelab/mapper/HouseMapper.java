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

    @Select("select * from house where creator_id=#{creatorId} or origization_id in  " +
            "(select id from origization where creator_id=#{creatorId} or CAST(member_ids AS VARCHAR) like CONCAT('%', CAST(#{creatorId} AS VARCHAR), '%') )")
    List<House> ownList(Long creatorId);


    @Select("<script>" +
            "select * from house where kind=0" +
            "<if test='content != null and content != \"\"'>" +
            " and (description like concat('%', #{content}, '%') or name like concat('%', #{content}, '%'))" +
            "</if>" +
            "</script>")
    List<House> getHouseBySearch(String content);
}
