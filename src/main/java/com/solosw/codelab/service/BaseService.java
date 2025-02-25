package com.solosw.codelab.service;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.List;


public class BaseService<M extends BaseMapper,R extends JpaRepository,data> {

    @Autowired
    protected M mapper;
    @Autowired

    protected R repository;
    public Long insert(data d){
        repository.save(d);
        return getId(d);
    }
    private Long getId(data entity) {
        try {
            // 获取类型
            Class<?> clazz = entity.getClass();
            // 尝试找到'id'字段
            Field idField = clazz.getDeclaredField("id");
            // 设置为可访问（如果字段是私有的）
            idField.setAccessible(true);
            // 返回'id'字段的值
            return (Long) idField.get(entity);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("无法通过反射获取'id'字段", e);
        }
    }
    public data selectByIdToRespository(Long id){
        return (data) repository.findById(id).get();
    }
    public data selectById(Long id){
        return (data) mapper.selectById(id);
    }
    public void deleteById(Long id){
        repository.deleteById(id);
    }

    public void updateById(data entity){
        repository.save(entity);
    }

    public void deleteByIdInBatch(List<Long> ids){
        repository.deleteAllInBatch(ids);
    }

}
