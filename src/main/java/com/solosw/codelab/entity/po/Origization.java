package com.solosw.codelab.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.solosw.codelab.converters.LongListConverter;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@Entity
public class Origization {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    @TableId(type = IdType.AUTO)
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "creator_id", nullable = false)
    private Long creatorId;
    @Column(name = "description", nullable = true)
    private String description;
    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;

    @Column(name = "update_time", nullable = false)
    private LocalDateTime updateTime;
    // 使用 @PrePersist 注解的方法，在插入之前自动设置 createTime 和 updateTime
    @Column(name = "member_ids", nullable = true)
    @Convert(converter = LongListConverter.class)
    public List<Long> memberIds;
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createTime = now;
        updateTime = now;

    }

    // 使用 @PreUpdate 注解的方法，在更新之前自动设置 updateTime
    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }


}
