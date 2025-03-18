package com.solosw.codelab.config.server;

import com.alibaba.fastjson.JSONArray;
import com.solosw.codelab.entity.po.House;
import com.solosw.codelab.entity.po.HouseRight;
import com.solosw.codelab.entity.po.Users;
import com.solosw.codelab.enums.HouseRightEnum;
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

        List<HouseRight.Right> rightList= JSONArray.parseArray(houseRight.getRights(),HouseRight.Right.class);
        Map<String,String> per=new HashMap<>();
        for(HouseRight.Right right:rightList){
            if(right.getOwner()) return;
            per.put(right.getBranch(),right.getRight());
        }
        String cKey="-1";
        String aKey="all";
        Set<ReceiveCommand> saved=new HashSet<>();
        for(ReceiveCommand command:collection){
            ReceiveCommand.Type type=command.getType();
            System.out.println(type.name());
            String refName= command.getRefName();
            String[] strings= refName.split("/");
            if(strings.length!=3) continue;
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
                    if(ss.equals(HouseRightEnum.READ_WRITE.getPermission())) saved.add(command);
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
                if(currPer.equals(HouseRightEnum.READ_WRITE.getPermission())) saved.add(command);
            }
        }
        for(ReceiveCommand command:collection){
            if(saved.contains(command)) continue;;
            collection.remove(command);
        }

    }
}
