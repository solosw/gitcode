package com.solosw.codelab.controller;

import com.solosw.codelab.controller.base.BaseController;
import com.solosw.codelab.entity.bo.ResponseBo;
import com.solosw.codelab.entity.po.PublicKey;
import com.solosw.codelab.entity.po.Users;
import com.solosw.codelab.service.PublicKeyService;
import com.solosw.codelab.service.UsersService;
import com.solosw.codelab.utils.GitoliteUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping("/back/ssh")
@Slf4j
@RestController
public class PublicKeyController extends BaseController {

    @Autowired
    PublicKeyService publicKeyService;

    @Autowired
    UsersService usersService;

    @PostMapping("/createPublicKey")
    public ResponseBo createPublicKey(@RequestBody Map<String,Object> map, HttpServletRequest request) throws Exception {

        Integer creator=(Integer) map.get("creatorId");
        if(getCurrentUser(request).getId().intValue()!=(creator))return ResponseBo.getFail(null,"错误操作",500);

        String publicKey=(String) map.get("publicKey");
        String result = publicKey.replaceAll("[\\s\\n\\r]", "");
        String keyName= (String) map.get("keyName");
        PublicKey publicKey1=new PublicKey().setPublicKey(result).setCreatorId(Long.valueOf(creator)).setName(keyName);
        publicKeyService.insert(publicKey1);
        return ResponseBo.getSuccess(null);
    }

    @PostMapping("/getUserKey/{creatorId}")
    public ResponseBo getUserKey(@PathVariable Long creatorId, HttpServletRequest request){
        if(!getCurrentUser(request).getId().equals(creatorId))return ResponseBo.getFail(null,"错误操作",500);

        List<PublicKey> p=publicKeyService.getPublicKeyByCreatorIdOrId(creatorId,0);
        return p==null?ResponseBo.getSuccess(new PublicKey()):ResponseBo.getSuccess(p);
    }

    @PostMapping("/deleteKey/{creatorId}/{keyId}")
    public ResponseBo deleteKey(@PathVariable Long creatorId,@PathVariable Long keyId, HttpServletRequest request){
        if(!getCurrentUser(request).getId().equals(creatorId))return ResponseBo.getFail(null,"错误操作",500);

        publicKeyService.deleteById(keyId);
        return ResponseBo.getSuccess(null);
    }



}
