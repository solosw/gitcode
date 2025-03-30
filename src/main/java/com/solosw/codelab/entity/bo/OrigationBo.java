package com.solosw.codelab.entity.bo;

import com.solosw.codelab.entity.po.Origization;
import com.solosw.codelab.entity.po.Users;
import jakarta.persistence.Column;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Accessors(chain = true)
@NoArgsConstructor
public class OrigationBo {
    private Long id;
    private String name;
    private Long creatorId;
    private String description;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer role; //0 创建者
    List<Users> members;
    String av;

    public OrigationBo(Origization origization){
        id=origization.getId();
        name=origization.getName();
        creatorId=origization.getCreatorId();
        description=origization.getDescription();
        createTime=origization.getCreateTime();
        updateTime=origization.getUpdateTime();
        av=origization.getAv();
    }

}
