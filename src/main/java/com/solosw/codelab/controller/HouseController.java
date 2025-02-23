package com.solosw.codelab.controller;

import com.solosw.codelab.entity.bo.ResponseBo;
import com.solosw.codelab.entity.po.House;
import com.solosw.codelab.service.HouseService;
import com.solosw.codelab.service.UsersService;
import com.solosw.codelab.task.GitoliteTask;
import com.solosw.codelab.utils.GitoliteUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/back/house")
@Slf4j
@RestController
public class HouseController {
    final  String publicHouse="@all";
    @Resource
    HouseService houseService;
    @Autowired
    UsersService usersService;

    @Autowired
    GitoliteTask gitoliteTask;

    @PostMapping("/create")
    public ResponseBo create(@RequestBody House house){
        try {
            houseService.insert(house);
            String userName=usersService.selectById(house.getCreatorId()).getName();
            GitoliteTask.Task task=new GitoliteTask.Task();
            task.setTaskType(GitoliteTask.TaskType.create_rep);
            GitoliteUtil.UserRule userRule=new GitoliteUtil.UserRule();
            task.setRepo(house.getPath());
            if(house.getKind()==0){
                userRule.setUserName(publicHouse);
            }
            else {
                userRule.setUserName(userName);
            }
            task.setUserRule(userRule);
            gitoliteTask.addToQueue(task);

        }catch (Exception e){
            log.error(e.toString()) ;
            return ResponseBo.getFail(null,"创建失败",500);
        }
        return ResponseBo.getSuccess(null);
    }
    @PostMapping("/list")
    public ResponseBo list(@RequestBody House house){

          return ResponseBo.getSuccess(houseService.list(house.getType(),house.getCreatorId(),house.getOrigizationId()))  ;

       // return ResponseBo.getFail(null,"查询失败",500);
    }
}
