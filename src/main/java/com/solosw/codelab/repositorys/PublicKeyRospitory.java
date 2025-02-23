package com.solosw.codelab.repositorys;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.solosw.codelab.entity.po.PublicKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PublicKeyRospitory extends JpaRepository<PublicKey,Long> {
}
