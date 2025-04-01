package com.solosw.codelab.repositorys;

import com.solosw.codelab.entity.po.Pull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PullRepository extends JpaRepository<Pull,Long> {
}
