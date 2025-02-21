package com.solosw.codelab.controller;

import com.solosw.codelab.entity.bo.ResponseBo;
import com.solosw.codelab.entity.po.Origization;
import com.solosw.codelab.entity.po.Users;
import com.solosw.codelab.service.OrigizationService;
import com.solosw.codelab.service.UsersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@Slf4j
@RequestMapping("/back/user")
public class UserController {

    @Autowired
    UsersService usersService;
    @Autowired
    OrigizationService origizationService;

    @PostMapping("/login")
    public ResponseBo login(@RequestBody Map<String,String> userInfos){
        String infos=userInfos.get("username");
        String password=userInfos.get("password");
        Users users= usersService.getUsersByNameOrEmailAndPassword(infos,password);
        if(users==null) return ResponseBo.getFail(null,"账户输入错误",500);
        Map<String, Object> map=new HashMap<>();
        map.put("user",users);
        return ResponseBo.getSuccess(map);
    }
    @PostMapping("/createOrganization")
    public ResponseBo createOrganization(@RequestBody Map<String,String> userInfos){
        String name=userInfos.get("name");
        String description=userInfos.get("description");
        Long createId= Long.valueOf(userInfos.get("userId"));
        Origization origization=new Origization();
        origization.setName(name).setCreatorId(createId).setDescription(description);
        try {
           origizationService.insert(origization);
       }catch (Exception e){
            log.error(e.toString());
            return ResponseBo.getFail(null,"组织名已存在",500);
       }
       return ResponseBo.getSuccess(null);
    }


    @RequestMapping("/getIdentityList/{userId}")
    public ResponseBo getIdentityList(@PathVariable Long userId){
        System.out.println(userId);
        try {
            return ResponseBo.getSuccess(origizationService.getOrigizationListByUserId(userId));
        }catch (Exception e){
            log.error(e.toString());
            return ResponseBo.getFail(null,"组织查询失败",500);
        }

    }
}
