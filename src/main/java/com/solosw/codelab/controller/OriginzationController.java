package com.solosw.codelab.controller;

import com.solosw.codelab.entity.bo.OrigationBo;
import com.solosw.codelab.entity.bo.ResponseBo;
import com.solosw.codelab.entity.po.House;
import com.solosw.codelab.entity.po.HouseRight;
import com.solosw.codelab.entity.po.Origization;
import com.solosw.codelab.entity.po.Users;
import com.solosw.codelab.service.HouseRightService;
import com.solosw.codelab.service.HouseService;
import com.solosw.codelab.service.OrigizationService;
import com.solosw.codelab.service.UsersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RequestMapping("/back/ori")
@Slf4j
@RestController
public class OriginzationController {

    @Autowired
    OrigizationService origizationService;
    @Autowired
    UsersService usersService;

    @Autowired
    HouseService houseService;
    @Autowired
    HouseRightService houseRightService;

    @PostMapping("/getMyOrifization/{userId}")
    public ResponseBo getMyOrifization(@PathVariable Long userId){
      List<Origization> origizationList= origizationService.getOrigizationListByUserId(userId);
      List<OrigationBo> list=new ArrayList<>();
      for(Origization o: origizationList){
          OrigationBo origationBo=new OrigationBo(o);
          if(userId.equals(o.getCreatorId())){
              origationBo.setRole(0);
          }else origationBo.setRole(1);
          List<Users> usersList=usersService.getUserByOrz(o.getId());
          origationBo.setMembers(usersList);
          list.add(origationBo);
      }
      return ResponseBo.getSuccess(list);
    }

    @PostMapping("/exit/{userId}/{orzId}")
    public ResponseBo exit(@PathVariable Long userId,@PathVariable Long orzId){
        Origization origization= origizationService.selectByIdToRespository(orzId);
        if(origization.getCreatorId().equals(userId)){
           Users users= usersService.getManager();
           if(users.getId().equals(userId)){

               return ResponseBo.getFail(null,"最高管理员不可退出",500);

           }else {
               origization.setCreatorId(users.getId());
               origizationService.updateById(origization);
               List<House> houseList=houseService.list(1,userId,orzId);
               List<Long> ids=new ArrayList<>();
               for(House house:houseList){
                   ids.add(house.getId());
                   houseRightService.deleteByHouseId(house.getId());

               }
                houseService.deleteByIdInBatch(ids);
           }
        }else {

           List<Long> list=new ArrayList<>();
           for (Long o:origization.getMemberIds()){
               if(!o.equals(userId))
                    list.add(o);
           }
            origizationService.updateById(origization.setMemberIds(list));
            List<House> houseList=houseService.list(1,userId,orzId);
            List<Long> ids=new ArrayList<>();
            for(House house:houseList){
                ids.add(house.getId());
                houseRightService.deleteByHouseId(house.getId());
            }
            houseService.deleteByIdInBatch(ids);
        }
        return ResponseBo.getSuccess(null);
    }

    @PostMapping("/addUser/{userName}/{orzId}")
    public ResponseBo addUser(@PathVariable String userName,@PathVariable Long orzId){
        Origization origization= origizationService.selectByIdToRespository(orzId);
        Users users=usersService.getUserByName(userName);
        if(users==null) return ResponseBo.getFail(null,"用户不存在",500);
        if(origization.getMemberIds().contains(users.getId())){
            return ResponseBo.getFail(null,"用户已在组织中",500);
        }
        if(origization.getMemberIds()==null|| origization.memberIds.isEmpty()){
            origization.setMemberIds(Collections.singletonList(users.getId()));
        }else{
            List<Long> list=new ArrayList<>();
            for (Long t:origization.getMemberIds()){
                list.add(t);
            }
            list.add(users.getId());
            origization.setMemberIds(list);
        }

        origizationService.updateById(origization);
        return ResponseBo.getSuccess(null);
    }

}
