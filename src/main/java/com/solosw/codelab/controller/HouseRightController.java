package com.solosw.codelab.controller;

import com.alibaba.fastjson.JSONArray;
import com.solosw.codelab.entity.bo.ResponseBo;
import com.solosw.codelab.entity.po.House;
import com.solosw.codelab.entity.po.HouseRight;
import com.solosw.codelab.entity.po.Users;
import com.solosw.codelab.enums.HouseRightEnum;
import com.solosw.codelab.service.HouseRightService;
import com.solosw.codelab.service.HouseService;
import com.solosw.codelab.service.UsersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RequestMapping("/back/right")
@Slf4j
@RestController
public class HouseRightController {

    @Autowired
    HouseService houseService;
    @Autowired
    HouseRightService houseRightService;
    @Autowired
    UsersService usersService;
    @PostMapping("/addUser")
    public ResponseBo addUser(@RequestBody Map<String,String> mp){

        String right=mp.get("right");
        String branch=mp.get("branch");
        if(right.equals(HouseRightEnum.CREATE.getPermission())){
            branch="-1";
        }
        String username=mp.get("username");
        Long houseId= Long.valueOf(mp.get("houseId"));
        Users users=usersService.getUserByName(username);
        if(users==null) return ResponseBo.getFail(null,"用户不存在",500);
        House house=houseService.selectById(houseId);
        if(house==null) return ResponseBo.getFail(null,"仓库不存在",500);
        HouseRight houseRight=houseRightService.getHouseRightByUserAndHouse(users.getId(),houseId);
        if(houseRight!=null){
            List<HouseRight.Right> rightList= JSONArray.parseArray(houseRight.getRights(),HouseRight.Right.class);
            if("all".equals(branch)){
                HouseRight.Right right1=new HouseRight.Right();
                right1.setOwner(true);
                right1.setRight(right);
                rightList= List.of(right1);
                houseRight.setRights(JSONArray.toJSONString(rightList));
                houseRightService.updateById(houseRight);

            }else {
                List<HouseRight.Right> rightList1=new ArrayList<>();
                for(HouseRight.Right rr:rightList){
                    if(rr.getOwner()) continue;
                    rightList1.add(rr);
                }
                HouseRight.Right right1=new HouseRight.Right();
                right1.setOwner(false);
                right1.setRight(right);
                right1.setBranch(branch);
                rightList1.add(right1);
                houseRight.setRights(JSONArray.toJSONString(rightList1));
                houseRightService.updateById(houseRight);
            }
        }else{
            HouseRight r=new HouseRight();
            r.setUserId(users.getId());
            r.setHouseId(houseId);
            if("all".equals(branch)){
                HouseRight.Right right1=new HouseRight.Right();
                right1.setOwner(true);
                right1.setRight(right);
                r.setRights(JSONArray.toJSONString(List.of(right1)));

            }else{
                HouseRight.Right right1=new HouseRight.Right();
                right1.setOwner(false);
                right1.setRight(right);
                right1.setBranch(branch);
                r.setRights(JSONArray.toJSONString(List.of(right1)));

            }
            houseRightService.insert(r);
        }

        return ResponseBo.getSuccess(null);
    }

    @PostMapping("/getUser/{houseId}")
    public ResponseBo getUser(@PathVariable Long houseId){

        List<HouseRight> houseRights=houseRightService.getHouseRightByHouse(houseId);
        List<Long> ids=new ArrayList<>();
        House house=houseService.selectById(houseId);
        for(HouseRight right:houseRights)  {
            if(house.getCreatorId().equals(right.getUserId())) continue;
            ids.add(right.getUserId());
        }
        List<Users> usersList=usersService.getBatchesById(ids);
        return ResponseBo.getSuccess(usersList);
    }
    @PostMapping("/deleteUser/{userId}/{houseId}")
    public ResponseBo deleteUser(@PathVariable Long houseId,@PathVariable Long userId){

        houseRightService.deleteHouseByHouseIdAndUserId(houseId,userId);
        return ResponseBo.getSuccess(null);
    }
}
