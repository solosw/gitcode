package com.solosw.codelab.controller;

import com.solosw.codelab.entity.bo.CodeBo;
import com.solosw.codelab.entity.bo.ResponseBo;
import com.solosw.codelab.entity.po.House;
import com.solosw.codelab.service.HouseService;
import com.solosw.codelab.service.UsersService;
import com.solosw.codelab.task.GitoliteTask;
import com.solosw.codelab.utils.GitServerUtil;
import com.solosw.codelab.utils.GitoliteUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/back/git")
@Slf4j
@RestController
public class GitController {
    @Resource
    HouseService houseService;
    @Autowired
    UsersService usersService;

    @Autowired
    GitoliteTask gitoliteTask;

    @PostMapping("/getProject/{houseId}")
    public ResponseBo getProjectById(@PathVariable Long houseId){
        House  house=houseService.selectById(houseId);
        if(house==null){
            return ResponseBo.getFail(null,"仓库不存在",500);
        }
        String realPath=GitoliteUtil.getRepositoryPath( house.getPath());
        List<CodeBo> codeBos=new ArrayList<>();
        List<String> branches= GitServerUtil.getAllBranches(realPath);
        if(branches.isEmpty()) return  ResponseBo.getFail(null,"读取分支错误",500);
        List<GitServerUtil.FileInfo> fileInfoList;
        fileInfoList = GitServerUtil.lsTree(realPath, branches.get(0), false);
        for(GitServerUtil.FileInfo fileInfo:fileInfoList){
            CodeBo codeBo=new CodeBo();
            if("tree".equals(fileInfo.getType())){
                codeBo.setDir(true);
            }else {
                codeBo.setDir(false);
            }
            codeBo.setName(fileInfo.getName());
            codeBo.setPath(fileInfo.getName());
            codeBo.setFileHash(fileInfo.getHash());
            List<GitServerUtil.CommitInfo> commitInfoList=GitServerUtil.findFileLatestCommit(realPath,fileInfo.getHash());
            if(!commitInfoList.isEmpty()){
                GitServerUtil.CommitInfo commitInfo=commitInfoList.getFirst();
                codeBo.setSubmitHash(commitInfo.getHash());
                codeBo.setMessage(commitInfo.getMessage());
                codeBo.setUpdateTime(commitInfo.getTime());
                codeBo.setUsername(commitInfo.getAuthor());
            }
            codeBos.add(codeBo);
        }

        Map<String,Object> map=new HashMap<>();
        map.put("fileTree",codeBos);
        map.put("house",house);
        map.put("branches",branches);
        map.put("tags",GitServerUtil.getAllTags(realPath));
        return ResponseBo.getSuccess(map);
    }


    @PostMapping("/openDir")
    public ResponseBo openDir(@RequestBody Map<String,String> mp){
        Long houseId= Long.valueOf(mp.get("houseId"));
        String fileHash=mp.get("fileHash");
        String filePath=mp.get("path");
        House  house=houseService.selectById(houseId);
        if(house==null){
            return ResponseBo.getFail(null,"仓库不存在",500);
        }
        String realPath=GitoliteUtil.getRepositoryPath( house.getPath());
        List<CodeBo> codeBos=new ArrayList<>();
        List<GitServerUtil.FileInfo> fileInfoList;
        fileInfoList = GitServerUtil.lsTree(realPath, fileHash);
        for(GitServerUtil.FileInfo fileInfo:fileInfoList){
            CodeBo codeBo=new CodeBo();
            if("tree".equals(fileInfo.getType())){
                codeBo.setDir(true);
            }else {
                codeBo.setDir(false);
            }
            codeBo.setName(fileInfo.getName());
            codeBo.setPath(filePath+"/"+fileInfo.getName());
            codeBo.setFileHash(fileInfo.getHash());
            List<GitServerUtil.CommitInfo> commitInfoList=GitServerUtil.findFileLatestCommit(realPath,fileInfo.getHash());
            if(!commitInfoList.isEmpty()){
                GitServerUtil.CommitInfo commitInfo=commitInfoList.getFirst();
                codeBo.setSubmitHash(commitInfo.getHash());
                codeBo.setMessage(commitInfo.getMessage());
                codeBo.setUpdateTime(commitInfo.getTime());
                codeBo.setUsername(commitInfo.getAuthor());
            }
            codeBos.add(codeBo);
        }
        return ResponseBo.getSuccess(codeBos);
    }


    @PostMapping("/catFile/{houseId}/{fileHash}")
    public ResponseBo catFile(@PathVariable Long houseId,@PathVariable String fileHash){
        House  house=houseService.selectById(houseId);
        if(house==null){
            return ResponseBo.getFail(null,"仓库不存在",500);
        }
        String realPath=GitoliteUtil.getRepositoryPath( house.getPath());
        return ResponseBo.getSuccess(GitServerUtil.catFile(realPath,fileHash));
    }

}
