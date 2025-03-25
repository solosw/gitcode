package com.solosw.codelab.controller;

import com.solosw.codelab.controller.base.BaseController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController extends BaseController {


    @GetMapping("/git-push")
    public ResponseEntity<?> handlePush(@RequestParam String username,@RequestParam String password){

        System.out.println(username);
        return new ResponseEntity<>("操作成功", HttpStatus.OK);
    }
}
