package com.solosw.codelab.controller;

import com.alibaba.fastjson.JSON;
import com.solosw.codelab.annations.PermissionCheck;
import com.solosw.codelab.controller.base.BaseController;
import com.solosw.codelab.entity.bo.CodeBo;
import com.solosw.codelab.entity.bo.ResponseBo;
import com.solosw.codelab.entity.po.House;
import com.solosw.codelab.entity.po.HouseRight;
import com.solosw.codelab.entity.po.Users;
import com.solosw.codelab.enums.HouseRightEnum;
import com.solosw.codelab.service.HouseRightService;
import com.solosw.codelab.service.HouseService;
import com.solosw.codelab.service.UsersService;
import com.solosw.codelab.utils.GitServerUtil;
import com.solosw.codelab.utils.GitoliteUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.h2.util.StringUtils;
import org.h2.util.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RequestMapping("/back/house")
@Slf4j
@RestController
public class HouseController extends BaseController {
    final  String publicHouse="@all";
    @Resource
    HouseService houseService;
    @Autowired
    UsersService usersService;
    @Autowired
    HouseRightService houseRightService;

    @PostMapping("/create")
    public ResponseBo create(@RequestBody House house){
        try {
            houseService.insert(house);
            Users users=usersService.selectById(house.getCreatorId());
            String userName=users.getName();
            HouseRight houseRight=new HouseRight();
            houseRight.setHouseId(house.getId());
            HouseRight.Right right=new HouseRight.Right();

            if(house.getKind()==0){// 公开为-1
                houseRight.setUserId(-1L);
                right.setOwner(true).setRight(HouseRightEnum.READ_WRITE_FORCE.getPermission());
            }
            else {
                houseRight.setUserId(users.getId());
                right.setOwner(true).setRight(HouseRightEnum.READ_WRITE_FORCE.getPermission());
            }
            houseRight.setRights(JSON.toJSONString(List.of(right)));
            houseRightService.insert(houseRight);
            GitServerUtil.initGitArea(GitoliteUtil.getRepositoryPath(house.getPath()));
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
        List<House> partIn=new ArrayList<>();
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
        List<HouseRight> houseRightList=houseRightService.getHouseRightByUser(house.getCreatorId());
        List<Long> ids=new ArrayList<>();
        if(houseRightList!=null){
            for(HouseRight right:houseRightList)  {
                ids.add(right.getHouseId());
            }
            partIn=houseService.getBatchesById(ids);
        }


        Map<String,Object> stringObjectMap=new HashMap<>();
        stringObjectMap.put("private",plist);
        stringObjectMap.put("public",pulist);
        stringObjectMap.put("orz",orlist);
        stringObjectMap.put("part",partIn);
        return ResponseBo.getSuccess(stringObjectMap);
    }



    @PostMapping("/changeType")
    public ResponseBo changeType(@RequestBody Map<String,String> mp,HttpServletRequest request){

        Long id= Long.valueOf(mp.get("id"));
        Integer kind= Integer.valueOf(mp.get("kind"));
        String name=mp.get("name");
        String des=mp.get("des");
        House house=houseService.selectById(id);
        if(!house.getCreatorId().equals(getCurrentUser(request).getId())) return ResponseBo.getFail("权限不足");
        Integer oriKind=house.getKind();
        house.setKind(kind);
        if(!StringUtils.isNullOrEmpty(name)){
            house.setName(name);
        }
        if(!StringUtils.isNullOrEmpty(des)){
            house.setDescription(des);
        }
        houseService.updateById(house);
        if(!oriKind.equals(kind)){
            Users users=usersService.selectById(house.getCreatorId());
            HouseRight.Right right=new HouseRight.Right();
            HouseRight houseRight=new HouseRight();
            houseRight.setHouseId(house.getId());
            houseRight.setRights(JSON.toJSONString(List.of(right)));
            if(kind.equals(0)){

                houseRightService.deleteHouseByHouseIdAndUserId(house.getId(), users.getId());
                houseRight.setUserId(-1L);
                right.setOwner(true).setRight(HouseRightEnum.READ_WRITE_FORCE.getPermission());
            } else if (kind.equals(1)) {
                houseRightService.deleteHouseByHouseIdAndUserId(house.getId(), -1L);
                houseRight.setUserId(users.getId());
                right.setOwner(true).setRight(HouseRightEnum.READ_WRITE_FORCE.getPermission());
            }
            houseRight.setRights(JSON.toJSONString(List.of(right)));
            houseRightService.insert(houseRight);
        }

        return ResponseBo.getSuccess(null);
    }


    @PostMapping("/getHouseBySearch/{content}")
    public ResponseBo getHouseBySearch(@PathVariable String content){



        return ResponseBo.getSuccess(houseService.getHouseBySearch(content));
    }


    @PostMapping("/getHouseById/{id}")
    public ResponseBo getHouseById(@PathVariable Long id){

        return ResponseBo.getSuccess(houseService.selectById(id));
    }
    @PostMapping("/getBranch/{houseId}")
    public ResponseBo getProjectById(@PathVariable Long houseId){
        House  house=houseService.selectById(houseId);
        if(house==null){
            return ResponseBo.getFail(null,"仓库不存在",500);
        }
        String realPath=GitoliteUtil.getRepositoryPath( house.getPath());
        List<String> branches= GitServerUtil.getAllBranches(realPath);
        if(branches.isEmpty()) return  ResponseBo.getFail(null,"读取分支错误",500);
        return ResponseBo.getSuccess(branches);
    }


    @PostMapping("/deleteRep/{houseId}")
    public ResponseBo deleteRep(@PathVariable Long houseId, HttpServletRequest request){
        House  house=houseService.selectById(houseId);
        Users users=getCurrentUser(request);
        if(house==null||users==null) return ResponseBo.getFail(null,"权限不足",500);
        if(house.getCreatorId().equals(users.getId())){
            houseService.deleteById(houseId);
            GitServerUtil.deleteRep(GitoliteUtil.getRepositoryPath(house.getPath()));
            return ResponseBo.getSuccess(null);
        }
        return ResponseBo.getFail(null,"权限不足",500);
    }



}
