package com.solosw.codelab.controller;

import com.solosw.codelab.entity.bo.ResponseBo;
import com.solosw.codelab.entity.po.Users;
import com.solosw.codelab.entity.vo.InitEnvDataVo;
import com.solosw.codelab.exception.EnvExceptiopn;
import com.solosw.codelab.service.UsersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/back")
public class ServerEnvController {
    @Autowired
    UsersService usersService;
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
    public ResponseBo InitEnv(@RequestBody InitEnvDataVo initEnvDataVo) throws InterruptedException {
        Thread.sleep(3000);
        System.out.println(initEnvDataVo);
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
                System.out.println("line:"+line);
                try {
                    Long.parseLong(line);
                    isExistedUser=true;
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("lineError:"+e.getMessage());
                }
            }



            if(isExistedUser) return ResponseBo.getFail(null,"linux用户已经存在，请重新创建!",500);


        } catch (Exception e) {
            System.out.println("设置用户失败:"+e);
            return ResponseBo.getFail(null,"设置用户失败",500);
        }


        try {

            Process processaddUser = new ProcessBuilder("useradd","-s","/bin/bash", initEnvDataVo.getName()).start();
            processaddUser.waitFor();
            String command = String.format("echo '%s:%s' | sudo chpasswd", initEnvDataVo.getName(), initEnvDataVo.getPassword());
            Process process = new ProcessBuilder("bash", "-c", command).start();
            int exitCode = 0;
            exitCode = process.waitFor();
            if (exitCode != 0) {
                return ResponseBo.getFail(null,"设置密码失败",500);
            }
        } catch (Exception e) {
            System.out.println(e);
            return ResponseBo.getFail(null,"设置密码失败",500);
        }

        String scriptContent =
                "#!/bin/bash\n" +
                        "su - "+initEnvDataVo.getName()+" << 'EOF'\n" +
                        "git clone https://github.com/sitaramc/gitolite\n" +
                        "mkdir -p $HOME/bin\n" +
                        "gitolite/install -to $HOME/bin\n" +
                        "ssh-keygen -t rsa -f ~/.ssh/id_rsa -N ''\n" +
                        "mv ~/.ssh/id_rsa.pub admin.pub\n" +
                        "$HOME/bin/gitolite setup -pk admin.pub\n" +
                        "ssh-keyscan -H 127.0.0.1 >> ~/.ssh/known_hosts\n"+
                        "git clone "+initEnvDataVo.getName()+"@127.0.0.1:gitolite-admin\n" +
                        "EOF";

        // Define the path to the script file
        String scriptPath = "./run_as_user.sh";

        try {
            // Write the script content to the file
            Files.write(Paths.get(scriptPath), scriptContent.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            // Make the script executable
            Process chmodProcess = new ProcessBuilder("chmod", "+x", scriptPath).start();
            chmodProcess.waitFor();

            // Execute the script
            Process process = new ProcessBuilder(scriptPath).start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            int exitCode = process.waitFor();
            if(exitCode!=0){
                System.out.println("Exit Code: " + process.getErrorStream().toString());
                return ResponseBo.getFail(null,"权限初始化失败",500);
            }


        } catch (Exception e) {
            System.out.println(e);
            return ResponseBo.getFail(null,"权限初始化失败",500);
        }

        addUser(initEnvDataVo);
        return ResponseBo.getSuccess(null);
    }


    @PostMapping("/addUser")
    public ResponseBo addUser(@RequestBody InitEnvDataVo initEnvDataVo) throws InterruptedException {


        Users users=new Users();
        users.setName(initEnvDataVo.getName()).setEmail(initEnvDataVo.getEmail()).
                setPassword(initEnvDataVo.getPassword()).setRole(initEnvDataVo.getRole());
        usersService.insert(users);
        return ResponseBo.getSuccess(null);
    }


    private static String runCommand(String command)  {

        try {
            ProcessBuilder processBuilder = new ProcessBuilder("/bin/bash", "-c", command);
            processBuilder.redirectErrorStream(true);

            Process process = null;
            process = processBuilder.start();
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.out.println(process.getErrorStream().toString());
                return ("Command exited with code: " + command);
            }
        } catch (Exception e) {
            System.out.println(e);
            return ("Command exited with code: " + command);
        }

        return null;
    }
}
