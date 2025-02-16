package com.solosw.codelab.controller;

import com.solosw.codelab.entity.bo.ResponseBo;
import com.solosw.codelab.entity.po.Users;
import com.solosw.codelab.service.UsersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@Slf4j
@RequestMapping("/back/user")
public class UserController {

    @Autowired
    UsersService usersService;

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

}
