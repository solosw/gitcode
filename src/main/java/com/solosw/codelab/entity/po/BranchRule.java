package com.solosw.codelab.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@Entity
public class BranchRule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    @TableId(type = IdType.AUTO)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "house_id", nullable = false)
    private Long houseId;
    @Column(name = "rule", nullable = false)
    private String rule;
    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;
    @Column(name = "update_time", nullable = false)
    private LocalDateTime updateTime;

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
