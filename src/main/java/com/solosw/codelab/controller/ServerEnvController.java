package com.solosw.codelab.controller;

import com.solosw.codelab.entity.bo.ResponseBo;
import com.solosw.codelab.entity.vo.InitEnvDataVo;
import com.solosw.codelab.exception.EnvExceptiopn;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/back")
public class ServerEnvController {

    @GetMapping("/getEnvStatus")
    public ResponseBo getEnvStatus() throws EnvExceptiopn {
        if(System.getProperty("os.name").toLowerCase().contains("windows")) return ResponseBo.getSuccess(new String[]{"error","error","error"});

        String[] serviceNames = {"ssh", "sshd"}; // 可能的服务名称列表
        boolean sshServiceFound = false;
        List<String> data=new ArrayList<>();
        for (String serviceName : serviceNames) {
            try {
                Process process = Runtime.getRuntime().exec(new String[]{"systemctl", "status", serviceName});
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                boolean isActive = false;
                while ((line = reader.readLine()) != null) {
                    if (line.contains("Active: active (running)")) {
                        isActive = true;
                        break;
                    }
                }
                if (isActive) {
                    System.out.println("SSH服务 (" + serviceName + ") 正在运行");
                    sshServiceFound = true;
                    break;
                }
            } catch (Exception e) {
                data.add("error");

            }
        }

        data.add(sshServiceFound?"success":"error");
        String path = "/etc/ssh/sshd_config";
        try {
            String content = new String(Files.readAllBytes(Paths.get(path)));
            if(content.contains("PubkeyAuthentication yes")) {
                data.add("success");
            } else {
                data.add("error");
            }
        } catch (IOException e) {
            data.add("error");
        }

        try {
            Process process = Runtime.getRuntime().exec("git --version");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if(line.contains("git version")){
                    data.add("success");
                    break;
                }else {
                    data.add("error");
                }
            }
        } catch (Exception e) {
            data.add("error");
        }

        return ResponseBo.getSuccess(data);
    }


    @PostMapping("/initEnv")
    public ResponseBo InitEnv(InitEnvDataVo initEnvDataVo){
        if(System.getProperty("os.name").toLowerCase().contains("windows")) return ResponseBo.getFail(null,"只支持linux",500);

        try {
            int exitCode = 0;
            Process process = new ProcessBuilder("id", "-u", initEnvDataVo.getName()).start();
            exitCode = process.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            boolean isExistedUser=false;
            while ((line = reader.readLine()) != null) {

                try {
                    Long.parseLong(line);
                    isExistedUser=true;
                    break;
                } catch (NumberFormatException ignored) {

                }
            }
            if(isExistedUser) return ResponseBo.getFail(null,"linux用户已经存在，请重新创建!",500);


        } catch (Exception e) {
            return ResponseBo.getFail(null,"设置用户失败",500);
        }


        try {
            String command = String.format("echo '%s:%s' | sudo chpasswd", initEnvDataVo.getName(), initEnvDataVo.getPassword());
            Process process = new ProcessBuilder("bash", "-c", command).start();
            int exitCode = 0;
            exitCode = process.waitFor();
            if (exitCode != 0) {
                return ResponseBo.getFail(null,"设置密码失败",500);
            }
        } catch (Exception e) {
            return ResponseBo.getFail(null,"设置密码失败",500);
        }


        return ResponseBo.getFail(null,"初始化失败，服务器错误",500);
    }
}
