package com.solosw.codelab.repositorys;

import com.solosw.codelab.entity.po.Issues;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IssuesRepository extends JpaRepository<Issues,Long> {
}
