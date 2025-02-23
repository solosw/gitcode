package com.solosw.codelab.repositorys;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.solosw.codelab.entity.po.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserInfoRepository extends JpaRepository<UserInfo,Long> {
}
