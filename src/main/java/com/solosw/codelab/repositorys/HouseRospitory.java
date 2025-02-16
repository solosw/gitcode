package com.solosw.codelab.repositorys;

import com.solosw.codelab.entity.po.House;
import org.springframework.data.jpa.repository.JpaRepository;

import java.nio.file.LinkOption;

public interface HouseRospitory extends JpaRepository<House,Long> {
}
