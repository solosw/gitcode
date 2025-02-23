package com.solosw.codelab.service;

import com.solosw.codelab.entity.po.PublicKey;
import com.solosw.codelab.mapper.PublicKeyMapper;
import com.solosw.codelab.repositorys.PublicKeyRospitory;
import org.springframework.stereotype.Service;

@Service
public class PublicKeyService extends BaseService<PublicKeyMapper, PublicKeyRospitory, PublicKey>{

    public PublicKey getPublicKeyByCreatorIdOrId(Long id,Integer type){
        return mapper.getPublicKeyByCreatorIdOrId(id,type);
    }
}
