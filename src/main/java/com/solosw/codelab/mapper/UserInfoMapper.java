package com.solosw.codelab.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.solosw.codelab.entity.po.PublicKey;
import com.solosw.codelab.entity.po.UserInfo;
import org.apache.ibatis.annotations.Select;

public interface UserInfoMapper extends BaseMapper<UserInfo> {

    @Select({
            "<script>",
            "SELECT * FROM user_info WHERE",
            "<choose>",
            "  <when test='type == null'>",
            "    user_id = -1",
            "  </when>",
            "  <when test='type == 0'>",
            "    user_id = #{id}",
            "  </when>",
            "  <when test='type == 1'>",
            "    id = #{id}",
            "  </when>",
            "  <otherwise>",
            "    user_id = -1", // 默认情况
            "  </otherwise>",
            "</choose>",
            "</script>"
    })
    UserInfo getUserInfoByUserIdOrId(Long id, Integer type);

}
