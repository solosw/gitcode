package com.solosw.codelab.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.solosw.codelab.entity.po.PublicKey;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface PublicKeyMapper extends BaseMapper<PublicKey> {


    @Select({
            "<script>",
            "SELECT * FROM public_key WHERE",
            "<choose>",
            "  <when test='type == null'>",
            "    creator_id = -1",
            "  </when>",
            "  <when test='type == 0'>",
            "    creator_id = #{id}",
            "  </when>",
            "  <when test='type == 1'>",
            "    id = #{id}",
            "  </when>",
            "  <otherwise>",
            "    creator_id = -1", // 默认情况
            "  </otherwise>",
            "</choose>",
            "</script>"
    })
    List<PublicKey> getPublicKeyByCreatorIdOrId(Long id, Integer type);

}
