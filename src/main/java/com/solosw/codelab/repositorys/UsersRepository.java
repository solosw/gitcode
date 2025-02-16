package com.solosw.codelab.repositorys;

import com.solosw.codelab.entity.po.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepository extends JpaRepository<Users,Long> {
}
