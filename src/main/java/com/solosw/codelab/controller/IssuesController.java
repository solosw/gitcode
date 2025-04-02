package com.solosw.codelab.controller;

import com.solosw.codelab.entity.bo.ResponseBo;
import com.solosw.codelab.entity.po.Issues;
import com.solosw.codelab.entity.po.Wiki;
import com.solosw.codelab.service.IssuesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/back/issues")
public class IssuesController {

    @Autowired
    IssuesService issuesService;
    @PostMapping("/add")
    public ResponseBo add(@RequestBody Issues wiki){
        issuesService.insert(wiki);
        return ResponseBo.getSuccess(null);
    }
    @PostMapping("/update")
    public ResponseBo update(@RequestBody Issues wiki){
        issuesService.updateById(wiki);
        return ResponseBo.getSuccess(null);
    }
    @PostMapping("/get/{houseId}")
    public ResponseBo get(@PathVariable Long houseId){

        return ResponseBo.getSuccess(issuesService.getByHouseId(houseId));
    }

    @PostMapping("/delete/{id}")
    public ResponseBo delete(@PathVariable Long id){
        issuesService.deleteById(id);
        return ResponseBo.getSuccess(null);
    }
}
