package com.solosw.codelab.controller;

import com.solosw.codelab.entity.bo.ResponseBo;
import com.solosw.codelab.entity.po.House;
import com.solosw.codelab.entity.po.Users;
import com.solosw.codelab.service.HouseService;
import com.solosw.codelab.service.UsersService;
import com.solosw.codelab.task.GitoliteTask;
import com.solosw.codelab.utils.GitoliteUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @PostMapping("/Ownlist")
    public ResponseBo Ownlist(@RequestBody House house){

        List<House> list=houseService.ownList(house.getCreatorId());
        List<House> plist=new ArrayList<>();
        List<House> pulist=new ArrayList<>();
        List<House> orlist=new ArrayList<>();
        for(House h:list){
            if(h.getType().equals(0)){
                if(h.getKind().equals(0)){
                    pulist.add(h);
                }else {
                    plist.add(h);
                }
            }else {
                orlist.add(h);
            }

        }
        Map<String,Object> stringObjectMap=new HashMap<>();
        stringObjectMap.put("private",plist);
        stringObjectMap.put("public",pulist);
        stringObjectMap.put("orz",orlist);

        return ResponseBo.getSuccess(stringObjectMap);
    }



    @PostMapping("/changeType/{id}/{kind}")
    public ResponseBo changeType(@PathVariable Long id,@PathVariable Integer kind){

        House house=houseService.selectById(id);
        house.setKind(kind);
        houseService.updateById(house);
        Users users=usersService.selectById(house.getCreatorId());
        if(kind.equals(0)){

            GitoliteTask.Task task=new GitoliteTask.Task().setRepo(house.getPath()).
                    setTaskType(GitoliteTask.TaskType.delete_user).setUserRule(new GitoliteUtil.UserRule().setUserName(users.getName()));
            gitoliteTask.addToQueue(task);
            GitoliteTask.Task task1=new GitoliteTask.Task().setRepo(house.getPath()).
                    setTaskType(GitoliteTask.TaskType.add_user).setUserRule(new GitoliteUtil.UserRule().setUserName("@all").setPermission(GitoliteUtil.GitolitePermission.READ_WRITE_FORCE));
            gitoliteTask.addToQueue(task1);
        } else if (kind.equals(1)) {
            GitoliteTask.Task task=new GitoliteTask.Task().setRepo(house.getPath()).
                    setTaskType(GitoliteTask.TaskType.delete_user).setUserRule(new GitoliteUtil.UserRule().setUserName("@all"));
            gitoliteTask.addToQueue(task);
            GitoliteTask.Task task1=new GitoliteTask.Task().setRepo(house.getPath()).
                    setTaskType(GitoliteTask.TaskType.add_user).setUserRule(new GitoliteUtil.UserRule().setUserName(users.getName()).setPermission(GitoliteUtil.GitolitePermission.READ_WRITE_FORCE));
            gitoliteTask.addToQueue(task1);
        }

        return ResponseBo.getSuccess(null);
    }
}
