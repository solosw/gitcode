package com.solosw.codelab.controller;

import com.solosw.codelab.config.GitHttpServletConfig;
import com.solosw.codelab.entity.bo.CodeBo;
import com.solosw.codelab.entity.bo.ResponseBo;
import com.solosw.codelab.entity.po.House;
import com.solosw.codelab.entity.po.Users;
import com.solosw.codelab.service.HouseService;
import com.solosw.codelab.service.UsersService;
import com.solosw.codelab.utils.GitServerUtil;
import com.solosw.codelab.utils.GitoliteUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RequestMapping("/back/git")
@Slf4j
@RestController
public class GitController {
    @Resource
    HouseService houseService;
    @Autowired
    UsersService usersService;


    @PostMapping("/changeBranch")
    public ResponseBo changeBranch(@RequestBody Map<String,String> map){


        Long houseId= Long.valueOf(map.get("id"));
        String branch=map.get("branch");
        House  house=houseService.selectById(houseId);
        if(house==null){
            return ResponseBo.getFail(null,"仓库不存在",500);
        }
        String realPath=GitoliteUtil.getRepositoryPath( house.getPath());

        List<GitServerUtil.FileInfo> fileInfoList;
        fileInfoList = GitServerUtil.lsTree(realPath, branch, false);
        List<CodeBo> codeBoList=new ArrayList<>();
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
           codeBoList.add(codeBo);
        }


        return ResponseBo.getSuccess(codeBoList);
    }
    //ToDO 权限
    @PostMapping("/getProject/{houseId}/{currentUserId}")
    public ResponseBo getProjectById(@PathVariable Long houseId,@PathVariable Long currentUserId){
        House  house=houseService.selectById(houseId);
        if(house==null){
            return ResponseBo.getFail(null,"仓库不存在",500);
        }
        String realPath=GitoliteUtil.getRepositoryPath( house.getPath());
        List<CodeBo> codeBos=new ArrayList<>();
        List<String> branches= GitServerUtil.getAllBranches(realPath);
        if(branches.isEmpty()) return  ResponseBo.getFail(null,"读取分支错误",500);
        List<GitServerUtil.FileInfo> fileInfoList;
        if(branches.contains("master")){
            fileInfoList = GitServerUtil.lsTree(realPath, "master", false);
        }else {
            fileInfoList = GitServerUtil.lsTree(realPath, branches.get(0), false);
        }

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
        Users currentUser=usersService.selectById(currentUserId);
        Map<String,Object> map=new HashMap<>();
        map.put("fileTree",codeBos);
        map.put("house",house);
        map.put("branches",branches);
        map.put("tags",GitServerUtil.getAllTags(realPath));
        map.put("clone",GitoliteUtil.getUrl(currentUser.getName(), house.getPath()));
        map.put("httpClone", GitHttpServletConfig.getClonePath(currentUserId,houseId));
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


    @PostMapping("/catFile/{houseId}/{fileHash}/{type}")
    public ResponseBo catFile(@PathVariable Long houseId,@PathVariable String fileHash,@PathVariable Integer type){
        House  house=houseService.selectById(houseId);
        if(house==null){
            return ResponseBo.getFail(null,"仓库不存在",500);
        }

        String realPath=GitoliteUtil.getRepositoryPath( house.getPath());
       if(type==0)  return ResponseBo.getSuccess(GitServerUtil.catFile(realPath,fileHash));
       if(type==1){
          byte[]  bytes=GitServerUtil.catFileByByte(realPath,fileHash);
          if(bytes.length == 0){
              return ResponseBo.getFail(null,"文件过大",500);
          }
          return ResponseBo.getSuccess( Base64.getEncoder().encodeToString(bytes));
       }
        return ResponseBo.getFail(null,"参数错误",500);
    }


}
