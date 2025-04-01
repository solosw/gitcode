package com.solosw.codelab.config.server;

import com.alibaba.fastjson.JSONArray;
import com.solosw.codelab.entity.po.*;
import com.solosw.codelab.enums.HouseRightEnum;
import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.transport.PreReceiveHook;
import org.eclipse.jgit.transport.ReceiveCommand;
import org.eclipse.jgit.transport.ReceivePack;
import org.h2.util.StringUtils;

import java.util.*;

public class MyPreRecieveHook implements PreReceiveHook {

    House house;
    Users users;
    HouseRight houseRight;
    GitPersmionHelper gitPersmionHelper;

    public MyPreRecieveHook(House house, Users users, HouseRight houseRight, GitPersmionHelper gitPersmionHelper) {
        this.gitPersmionHelper=gitPersmionHelper;
        this.house=house;
        this.users=users;
        this.houseRight=houseRight;
    }

    @Override
    public void onPreReceive(ReceivePack receivePack, Collection<ReceiveCommand> collection) {


        if(house.getCreatorId().equals(users.getId())) return;
        List<BranchRule> branchRules = gitPersmionHelper.getBranchRule(house.getId());
        List<HouseRight.Right> rightList=new ArrayList<>();
        if(houseRight!=null) rightList = JSONArray.parseArray(houseRight.getRights(),HouseRight.Right.class);
        Map<String,String> per=new HashMap<>();
        for(HouseRight.Right right:rightList){
            if(right.getOwner()) return;
            per.put(right.getBranch(),right.getRight());
        }

        List<Pull> pullList=gitPersmionHelper.getPullByHouseIdAndUserName(users.getName(), house.getId());
        if(pullList!=null){
            for(Pull p:pullList){
                if(p.getState().equals(1)){
                    if(per.containsKey(p.getBaseBranch())){
                        per.replace(p.getBaseBranch(),HouseRightEnum.READ_WRITE_FORCE.getPermission());
                    }else{
                        per.put(p.getBaseBranch(),HouseRightEnum.READ_WRITE_FORCE.getPermission());
                    }
                }
            }
        }

        for(BranchRule bb:branchRules){
            if(per.containsKey(bb.getName())){
                per.replace(bb.getName(),bb.getRule());
            }else{
                per.put(bb.getName(),bb.getRule());
            }
        }

        String cKey="-1";
        String aKey="all";
        Set<ReceiveCommand> saved=new HashSet<>();
        for(ReceiveCommand command:collection){
            ReceiveCommand.Type type=command.getType();
            System.out.println(type.name());
            String refName= command.getRefName();
            String[] strings= refName.split("/");
            if(strings.length!=3) {
                saved.add(command);
                continue;
            }
            String branch=strings[2];
            String currPer=per.get(branch);
            if(StringUtils.isNullOrEmpty(currPer)) {
                if(!per.containsKey(aKey)&&!per.containsKey(cKey)) continue;
            }
            if(per.containsKey(aKey)){
                String ss=per.get(aKey);
                if (type.equals(ReceiveCommand.Type.DELETE)) {
                    if(ss.equals(HouseRightEnum.DELETE.getPermission())) saved.add(command);
                } else if (type.equals(ReceiveCommand.Type.UPDATE_NONFASTFORWARD)) {
                    if(ss.equals(HouseRightEnum.READ_WRITE_FORCE.getPermission())) saved.add(command);
                } else  {
                    if(ss.equals(HouseRightEnum.READ_WRITE_FORCE.getPermission())||ss.equals(HouseRightEnum.READ_WRITE.getPermission())) saved.add(command);
                }
            } else if(type.name().equals("CREATE")){

                if(per.containsKey(cKey)){

                    saved.add(command);
                }

            } else if (type.equals(ReceiveCommand.Type.DELETE)) {
                if(currPer.equals(HouseRightEnum.DELETE.getPermission())) saved.add(command);
            } else if (type.equals(ReceiveCommand.Type.UPDATE_NONFASTFORWARD)) {
                if(currPer.equals(HouseRightEnum.READ_WRITE_FORCE.getPermission())) saved.add(command);
            } else  {
                if(currPer.equals(HouseRightEnum.READ_WRITE_FORCE.getPermission())||currPer.equals(HouseRightEnum.READ_WRITE.getPermission())) saved.add(command);
            }
        }

        collection.removeIf(command -> {
            if(!saved.contains(command)){
                command.setResult(ReceiveCommand.Result.REJECTED_OTHER_REASON);
                return true;
            }
          return  false;
        });
    }
}
