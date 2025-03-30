package com.solosw.codelab.controller;

import com.solosw.codelab.controller.base.BaseController;
import com.solosw.codelab.entity.bo.ResponseBo;
import com.solosw.codelab.entity.po.BranchRule;
import com.solosw.codelab.entity.po.House;
import com.solosw.codelab.enums.HouseRightEnum;
import com.solosw.codelab.service.BranchRuleService;
import com.solosw.codelab.service.HouseService;
import com.solosw.codelab.utils.GitServerUtil;
import com.solosw.codelab.utils.GitoliteUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.http.server.GitServlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/back/branch")
@Slf4j
@RestController
public class BranchRuleController extends BaseController {
    @Autowired
    BranchRuleService branchRuleService;
    @Autowired
    HouseService houseService;
    @PostMapping("/add")
    public ResponseBo addBranchRule(@RequestBody Map<String,String> mp, HttpServletRequest request){
        Long houseId= Long.valueOf(mp.get("houseId"));
        String name=mp.get("branchName");
        String rule=mp.get("rule");
        House h=houseService.selectById(houseId);
        if(h.getCreatorId().equals(getCurrentUser(request).getId())) {
            BranchRule branchRule=new BranchRule().setRule(rule).setName(name).setHouseId(houseId);
            branchRuleService.insert(branchRule);
            return ResponseBo.getSuccess(null);
        }

        return ResponseBo.getFail("权限错误");
    }

    @PostMapping("/deleteBranchRule")
    public ResponseBo deleteBranchRule(@RequestBody Map<String,String> mp, HttpServletRequest request){
        Long houseId= Long.valueOf(mp.get("houseId"));
        String name= (mp.get("branchName"));
        House h=houseService.selectById(houseId);
        if(h.getCreatorId().equals(getCurrentUser(request).getId())) {
            branchRuleService.deleteByNameAndHouseId(name,houseId);
            return ResponseBo.getSuccess(null);
        }

        return ResponseBo.getFail("权限错误");
    }



    @PostMapping("/deleteBranch")
    public ResponseBo deleteBranch(@RequestBody Map<String,String> mp, HttpServletRequest request) throws InterruptedException {
        Long houseId= Long.valueOf(mp.get("houseId"));
        String name=mp.get("branchName");
        House h=houseService.selectById(houseId);
        if(h.getCreatorId().equals(getCurrentUser(request).getId())) {
            branchRuleService.deleteByNameAndHouseId(name,houseId);
            try {
                GitServerUtil.deleteBranch(GitoliteUtil.getRepositoryPath(h.getPath()),name);
            }catch (Exception e){
                throw  e;
            }
            return ResponseBo.getSuccess(null);
        }

        return ResponseBo.getFail("权限错误");
    }
    @PostMapping("/getBranchRule/{houseId}")
    public ResponseBo getBranchRule(@PathVariable Long houseId){
        List<BranchRule> branchRules=branchRuleService.getRuleByHouseId(houseId);
        if(branchRules==null) branchRules=new ArrayList<>();
        House house= houseService.selectById(houseId);
        List<String> allBranches = GitServerUtil.getAllBranches(GitoliteUtil.getRepositoryPath(house.getPath()));
        if(allBranches==null) allBranches=new ArrayList<>();
        List<Object> vals=new ArrayList<>();
        for (String b:allBranches){
            Map<String,String> br=new HashMap<>();
            for(BranchRule rule:branchRules){
                if(rule.getName().equals(b)){
                    br.put("name",rule.getName());
                    br.put("rule",rule.getRule());
                }
            }
            if(br.isEmpty()) {
                br.put("name",b);
                br.put("rule","none");
            }
            vals.add(br);
        }
        return ResponseBo.getSuccess(vals);
    }

}
