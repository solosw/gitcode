package com.solosw.codelab.controller;

import com.solosw.codelab.entity.bo.ResponseBo;
import com.solosw.codelab.entity.po.Pull;
import com.solosw.codelab.entity.po.Wiki;
import com.solosw.codelab.service.HouseService;
import com.solosw.codelab.service.PullService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/back/pull")
public class PullRequestController {

    @Autowired
    PullService pullService;
    @Autowired
    HouseService houseService;

    @PostMapping("/add")
    public ResponseBo add(@RequestBody Pull wiki){
        pullService.insert(wiki);
        return ResponseBo.getSuccess(null);
    }
    @PostMapping("/update")
    public ResponseBo update(@RequestBody Pull wiki){
        pullService.updateById(wiki);
        return ResponseBo.getSuccess(null);
    }
    @PostMapping("/get/{houseId}")
    public ResponseBo get(@PathVariable Long houseId){

        return ResponseBo.getSuccess(pullService.getByHouseId(houseId));
    }



}
