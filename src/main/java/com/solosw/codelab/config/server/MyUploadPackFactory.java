package com.solosw.codelab.config.server;

import com.alibaba.fastjson.JSONArray;
import com.solosw.codelab.entity.po.House;
import com.solosw.codelab.entity.po.HouseRight;
import com.solosw.codelab.entity.po.Users;
import com.solosw.codelab.enums.HouseRightEnum;
import jakarta.servlet.http.HttpServletRequest;
import org.eclipse.jgit.http.server.resolver.DefaultUploadPackFactory;
import org.eclipse.jgit.lib.Config;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.UploadPack;
import org.eclipse.jgit.transport.resolver.ServiceNotAuthorizedException;
import org.eclipse.jgit.transport.resolver.ServiceNotEnabledException;
import org.eclipse.jgit.transport.resolver.UploadPackFactory;

import java.util.*;

public class MyUploadPackFactory implements UploadPackFactory<HttpServletRequest> {


    public MyUploadPackFactory() {
    }



    public UploadPack create(HttpServletRequest req, Repository db) throws ServiceNotEnabledException, ServiceNotAuthorizedException {

        House   house= (House) req.getAttribute("my_house");
        HouseRight houseRight= (HouseRight) req.getAttribute("house_right");
        if (((MyUploadPackFactory.ServiceConfig)db.getConfig().get(MyUploadPackFactory.ServiceConfig::new)).enabled) {
            UploadPack up = new UploadPack(db);
            String header = req.getHeader("Git-Protocol");
            if (header != null) {
                String[] params = header.split(":");
                up.setExtraParameters(Arrays.asList(params));
            }
            List<HouseRight.Right> rightList= new ArrayList<>();
            if(houseRight!=null){
                rightList.addAll( JSONArray.parseArray(houseRight.getRights(),HouseRight.Right.class));
            }
            if(house.getKind().equals(1)){
                up.setRefFilter((ref)->{
                    Map<String, Ref> saved=new HashMap<>();
                    for(String bran:ref.keySet()){
                        String[] strings=bran.split("/");
                        if(strings.length!=3) {
                            saved.put(bran, ref.get(bran));
                            continue;
                        }
                        String branch=strings[2];
                        for(HouseRight.Right right:rightList){
                            if(right.getOwner()&& !right.getRight().equals(HouseRightEnum.NONE.getPermission())) {
                                return ref;

                            }
                            if(branch.equals(right.getBranch())){
                                if(!right.getRight().equals(HouseRightEnum.NONE.getPermission())){
                                    saved.put(bran, ref.get(bran));
                                    break;
                                }
                            }
                        }
                    }
                    for(String r:ref.keySet()) {
                        if(!saved.containsKey(r)){
                            ref.remove(r);
                        }
                    }
                    return ref;
                });
            }


            return up;
        } else {
            throw new ServiceNotEnabledException();
        }
    }

    private static class ServiceConfig {
        final boolean enabled;

        ServiceConfig(Config cfg) {
            this.enabled = cfg.getBoolean("http", "uploadpack", true);
        }
    }


}
