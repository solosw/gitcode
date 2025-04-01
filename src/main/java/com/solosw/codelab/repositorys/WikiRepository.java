package com.solosw.codelab.repositorys;

import com.solosw.codelab.entity.po.Wiki;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WikiRepository extends JpaRepository<Wiki,Long> {
}
