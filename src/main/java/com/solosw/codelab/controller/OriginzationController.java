package com.solosw.codelab.controller;

import com.solosw.codelab.entity.bo.OrigationBo;
import com.solosw.codelab.entity.bo.ResponseBo;
import com.solosw.codelab.entity.po.Origization;
import com.solosw.codelab.entity.po.Users;
import com.solosw.codelab.service.OrigizationService;
import com.solosw.codelab.service.UsersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RequestMapping("/back/ori")
@Slf4j
@RestController
public class OriginzationController {

    @Autowired
    OrigizationService origizationService;
    @Autowired
    UsersService usersService;

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


}
