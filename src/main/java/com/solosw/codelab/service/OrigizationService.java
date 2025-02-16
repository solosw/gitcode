package com.solosw.codelab.service;

import com.solosw.codelab.mapper.OrigizationMapper;
import com.solosw.codelab.repositorys.OrigizationRospitory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrigizationService {

    @Autowired
    OrigizationMapper origizationMapper;
    @Autowired
    OrigizationRospitory origizationRospitory;


}
