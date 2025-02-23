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
public class UserInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    @TableId(type = IdType.AUTO)
    private Long id;
    @Column(name = "user_id", nullable = false, unique = true)
    Long userId;

    @Column(name = "name", nullable = true, unique = false)
    private String name;
    @Column(name = "job", nullable = true, unique = false)
    private String job;
    @Column(name = "company", nullable = true, unique = false)
    private String company;
    @Column(name = "qq", nullable = true, unique = false)
    private String qq;
    @Column(name = "wx", nullable = true, unique = false)
    private String wx;
    @Column(name = "des", nullable = true)
    private String des;
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
