package com.solosw.codelab.repositorys;

import com.solosw.codelab.entity.po.House;
import com.solosw.codelab.entity.po.HouseRight;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HouseRightRospitory extends JpaRepository<HouseRight,Long> {
}
