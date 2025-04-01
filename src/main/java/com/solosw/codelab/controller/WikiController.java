package com.solosw.codelab.controller;

import com.solosw.codelab.entity.bo.ResponseBo;
import com.solosw.codelab.entity.po.Wiki;
import com.solosw.codelab.service.HouseService;
import com.solosw.codelab.service.WikiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/back/wiki")
public class WikiController {

    @Autowired
    WikiService wikiService;
    @Autowired
    HouseService houseService;

    @PostMapping("/add")
    public ResponseBo add(@RequestBody Wiki wiki){
        wikiService.insert(wiki);
        return ResponseBo.getSuccess(null);
    }
    @PostMapping("/update")
    public ResponseBo update(@RequestBody Wiki wiki){
        wikiService.updateById(wiki);
        return ResponseBo.getSuccess(null);
    }
    @PostMapping("/get/{houseId}")
    public ResponseBo get(@PathVariable Long houseId){

        return ResponseBo.getSuccess(wikiService.getByHouseId(houseId));
    }

    @PostMapping("/delete/{id}")
    public ResponseBo delete(@PathVariable Long id){
        wikiService.deleteById(id);
        return ResponseBo.getSuccess(null);
    }
}
