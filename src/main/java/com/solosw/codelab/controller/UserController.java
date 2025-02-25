package com.solosw.codelab.controller;

import com.solosw.codelab.entity.bo.ResponseBo;
import com.solosw.codelab.entity.po.Origization;
import com.solosw.codelab.entity.po.UserInfo;
import com.solosw.codelab.entity.po.Users;
import com.solosw.codelab.service.OrigizationService;
import com.solosw.codelab.service.UserInfoService;
import com.solosw.codelab.service.UsersService;
import lombok.extern.slf4j.Slf4j;
import org.h2.util.StringUtils;
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

    @Autowired
    UserInfoService userInfoService;
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

    @PostMapping("/changeInfo")
    public ResponseBo changeInfo(@RequestBody Map<String,String> info){
        Long id= Long.valueOf(info.get("id"));
        String email=info.get("email");
        String password=info.get("password");
        if (StringUtils.isNullOrEmpty(email)||StringUtils.isNullOrEmpty(password))
            return ResponseBo.getFail(null,"缺少必填项",500);
        Users user=usersService.selectById(id);
        if(user==null) return ResponseBo.getFail(null,"用户不存在",500);
        user.setEmail(email);
        user.setPassword(password);
        usersService.updateById(user);
        return ResponseBo.getSuccess(user);
    }

    @PostMapping("/changeRealInfo")
    public ResponseBo changeRealInfo(@RequestBody UserInfo info){
        UserInfo currentInf0=userInfoService.getUserInfoByUserIdOrId(info.getUserId(),0);
        if(currentInf0==null){
            userInfoService.insert(info);
        }else {
            userInfoService.updateById(info.setId(currentInf0.getId()));
        }
        return ResponseBo.getSuccess(null);
    }
    @PostMapping("/getRealInfo/{userId}")
    public ResponseBo getRealInfo(@PathVariable Long userId){
        UserInfo currentInf0=userInfoService.getUserInfoByUserIdOrId(userId,0);
        return ResponseBo.getSuccess(currentInf0==null?new UserInfo():currentInf0);
    }

    @PostMapping("/add")
    public ResponseBo getRealInfo(@RequestBody Users users){
        try {
            users.setEmail(users.getName()+"@default.com");
            usersService.insert(users);
        }catch (Exception e){
            return ResponseBo.getFail(null,"用户已存在",500);
        }

        return ResponseBo.getSuccess(null);
    }


    @PostMapping("/all")
    public ResponseBo getAll(){

        return ResponseBo.getSuccess(usersService.getAll());
    }

    @PostMapping("/delete")
    public ResponseBo delete(@RequestBody Users users){
        usersService.deleteById(users.getId());
        //ToDO 删除仓库记录，删除组织用户
        return ResponseBo.getSuccess(null);
    }
}
