package com.solosw.codelab.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.solosw.codelab.entity.po.Issues;
import com.solosw.codelab.mapper.IssuesMapper;
import com.solosw.codelab.repositorys.IssuesRepository;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IssuesService extends BaseService<IssuesMapper, IssuesRepository, Issues>{


    public List<Issues> getByHouseId(Long houseId) {
        Issues probe = new Issues();
        probe.setHouseId(houseId); // 假设 Issues 类中有 setHouseId 方法

        // 创建 ExampleMatcher 来定义匹配规则
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnoreNullValues() // 忽略 probe 中为 null 的字段
                .withIgnoreCase();      // 忽略大小写（如果适用）

        // 创建 Example 对象
        Example<Issues> example = Example.of(probe, matcher);

        // 使用 repository 查询
        return repository.findAll(example);
    }
}
