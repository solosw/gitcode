package com.solosw.codelab.controller;

import com.solosw.codelab.entity.bo.ResponseBo;
import com.solosw.codelab.entity.po.PublicKey;
import com.solosw.codelab.entity.po.Users;
import com.solosw.codelab.service.PublicKeyService;
import com.solosw.codelab.service.UsersService;
import com.solosw.codelab.task.GitoliteTask;
import com.solosw.codelab.utils.GitoliteUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.javassist.expr.NewArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequestMapping("/back/ssh")
@Slf4j
@RestController
public class PublicKeyController {

    @Autowired
    PublicKeyService publicKeyService;
    @Autowired
    GitoliteTask gitoliteTask;
    @Autowired
    UsersService usersService;

    @PostMapping("/createPublicKey")
    public ResponseBo createPublicKey(@RequestBody Map<String,Object> map) throws Exception {
        Integer creator=(Integer) map.get("creatorId");
        String publicKey=(String) map.get("publicKey");
        PublicKey publicKey1=new PublicKey().setPublicKey(publicKey).setCreatorId(Long.valueOf(creator));
        try {
            PublicKey publicKey2=publicKeyService.getPublicKeyByCreatorIdOrId(Long.valueOf(creator),0);
            if(publicKey2!=null) {
                publicKey2.setPublicKey(publicKey);
                publicKeyService.updateById(publicKey2);
            }
            else  publicKeyService.insert(publicKey1);



        }catch (Exception e){
            throw new Exception(e);
           // return ResponseBo.getFail(null,"创建或更新publicKey失败",500);

        }

        try {
            Users users=usersService.selectById(Long.valueOf(creator));
            GitoliteTask.Task task=new GitoliteTask.Task().setTaskType(GitoliteTask.TaskType.add_ssh).
                    setUserRule(new GitoliteUtil.UserRule().setUserName(users.getName()).setSshKey(publicKey));
            gitoliteTask.addToQueue(task);
        }catch (Exception e){
            throw new Exception(e);
           // return ResponseBo.getFail(null,"创建或更新publicKey失败",500);
        }
        return ResponseBo.getSuccess(null);
    }

    @PostMapping("/getUserKey/{creatorId}")
    public ResponseBo getUserKey(@PathVariable Long creatorId){
        PublicKey p=publicKeyService.getPublicKeyByCreatorIdOrId(creatorId,0);
        return p==null?ResponseBo.getSuccess(new PublicKey()):ResponseBo.getSuccess(p);
    }
}
