package com.solosw.codelab.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solosw.codelab.config.server.CoreTestSupportUtils;
import com.solosw.codelab.config.server.GitPersmionHelper;
import com.solosw.codelab.entity.po.Users;
import com.solosw.codelab.service.UsersService;
import com.solosw.codelab.utils.GitoliteUtil;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.sshd.server.SshServer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Configuration
public class GitSSHServerConfig implements ApplicationContextAware {

    @Autowired
    UsersService usersService;

    @Autowired
    GitPersmionHelper gitPersmionHelper;

    Config config;

    public void Init() {

        File file = new File("./config.json");
        if(!Files.exists(file.toPath())) throw new Error("配置文件不存在");
        ObjectMapper mapper = new ObjectMapper();
        try {
            // 假设ConfigClass是你用来映射JSON的对象类
            config = mapper.readValue(file, Config.class);
            SshServer sshd= CoreTestSupportUtils.setupTestServer(SshServer.setUpDefaultServer(),
                    config.getMaxThread(),config.getGitRootPath(),gitPersmionHelper);
            sshd.setPort(config.getSshPort());
            GitoliteUtil.workingDirectory=config.getGitRootPath();
            GitoliteUtil.gitRepoUrlPrx=config.getHost()+":"+config.getSshPort();

            Thread thread=new Thread(()->{
                try {
                    sshd.start();
                    while(true){
                        Thread.sleep(Integer.MAX_VALUE);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
            thread.start();
            Users users=  usersService.getUserByName(config.admin);
            if(users==null){
                users =new Users();
                users.setName(config.admin).setPassword(config.password).setRole(0).setEmail("admin@qq.com");
                usersService.insert(users);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

        Init();

    }


    @Data
    @NoArgsConstructor
    @Accessors(chain = true)
    static class Config{
        int sshPort;
        int maxThread;
        String gitRootPath;
        String host;
        String admin;
        String password;
        String encodedKey;
        Integer httpPort;
        Integer httpsPort;
        String keystoreFile;
        String keystorePassword;
        String keystoreType;
        String keyAlias;
    }
}
