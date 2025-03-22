package com.solosw.codelab.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solosw.codelab.config.server.*;
import com.solosw.codelab.entity.po.House;
import com.solosw.codelab.entity.po.HouseRight;
import com.solosw.codelab.entity.po.Users;
import com.solosw.codelab.service.UsersService;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.net.SSLHostConfig;
import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.http.server.GitServlet;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import jakarta.servlet.Servlet;

import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.solosw.codelab.config.server.AESGCMEncryptionWithCustomKey.createKeySpec;
import static com.solosw.codelab.config.server.AESGCMEncryptionWithCustomKey.encrypt;

@Configuration
public class GitHttpServletConfig  {


    static {
        Init();
    }

    @Autowired
    GitPersmionHelper gitPersmionHelper;
    @Autowired
    UsersService usersService;

    @Autowired
    UserContext userContext;
    static  SecretKeySpec secretKeySpec;
    public static String getClonePath(Long useId,Long houseId){

        Map<String, Long> mp=new HashMap<>();
        mp.put("userId",useId);
        mp.put("houseId",houseId);
        try {
           return "https://"+config.getHost()+":"+ config.getHttpsPort()+"/server/"+ encrypt(JSON.toJSONString(mp), secretKeySpec);
        } catch (Exception e) {
            return null;
        }

    }
    // 仓库存储根目录（可配置为外部路径）
    private static  String REPOS_ROOT_DIR ;
    @Bean
    public ServletWebServerFactory servletContainer() {

        try {
            TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();

            tomcat.addAdditionalTomcatConnectors(createHttpConnector());
         //   tomcat.addAdditionalTomcatConnectors(createHttpsConnector());
            return tomcat;
        }catch (Exception e){
            throw  new RuntimeException(e);
        }
    }

    private Connector createHttpConnector() {


        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        connector.setScheme("http");
        File file = new File("./config.json");
        if(!Files.exists(file.toPath())) throw new Error("配置文件不存在");
        ObjectMapper mapper = new ObjectMapper();
        try {
            GitSSHServerConfig.Config config = mapper.readValue(file, GitSSHServerConfig.Config.class);
            connector.setPort(config.getHttpPort());  // HTTP端口
            connector.setSecure(false);
            return connector;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Bean
    public ServletRegistrationBean<Servlet> gitServletRegistration() {


        // 初始化 GitServlet
        GitServlet gitServlet = new GitServlet();
        gitServlet.setRepositoryResolver((request, name) -> {
            String path = request.getPathInfo(); // 如 "/repo1/git-upload-pack"
            try {
                Map<String,Integer> mp=getMap(name);
                Integer houseId=mp.get("houseId");
                Long userId= Long.valueOf(mp.get("userId"));
                Users users=usersService.selectById(userId);
                Users inu= userContext.getUser();
                if(inu==null)  throw new RepositoryNotFoundException("权限不足: ");
                if(!inu.getName().equals(users.getName())) throw new RepositoryNotFoundException("输入有误");
               HouseRight   houseRight=(gitPersmionHelper.getHouseRightByUserAndHouse(userId, Long.valueOf(houseId)));
               House house =(gitPersmionHelper.getHouse(Long.valueOf(houseId)));
                String repoName = "/"+ house.getPath(); // 提取仓库名称
                String service=request.getParameter("service");
                if("git-upload-pack".equals(service)){
                   if(houseRight ==null)  throw new RepositoryNotFoundException("仓库不存在: " + repoName);
                   if(house.getKind().equals(1)&&!userId.equals(house.getCreatorId())){
                       if(houseRight ==null) throw new RepositoryNotFoundException("权限不足: " + repoName);
                       List<HouseRight.Right> list= JSONArray.parseArray(houseRight.getRights(),HouseRight.Right.class);
                       if(list==null||list.isEmpty())  throw new RepositoryNotFoundException("权限不足: " + repoName);
                   }
                }

                request.setAttribute("my_house",house);
                request.setAttribute("house_right",houseRight);
                request.setAttribute("my_users",users);
                File repoDir = new File(REPOS_ROOT_DIR+repoName + ".git");
                if (!repoDir.exists()) {
                    throw new RepositoryNotFoundException("仓库不存在: " + repoName);
                }

                try {

                    return new FileRepositoryBuilder()
                            .setGitDir(repoDir)
                            .build();
                } catch (IOException e) {
                    throw new RuntimeException("仓库初始化失败", e);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        });

        // 配置 Git 操作工厂
        gitServlet.setReceivePackFactory(new MyReceivePackFactory(gitPersmionHelper));
        gitServlet.setUploadPackFactory(new MyUploadPackFactory());


        // 注册 Servlet 并映射到 /git/*
        return new ServletRegistrationBean<>(gitServlet, "/server/*");
    }

    Map getMap(String crypd) throws Exception {

        SecretKeySpec secretKeySpec = createKeySpec(config.getEncodedKey());
        String decryptedText = AESGCMEncryptionWithCustomKey.decrypt(crypd, secretKeySpec);
        return JSON.parseObject(decryptedText,Map.class);
    }



    static GitSSHServerConfig.Config config;

    public static void Init() {

        File file = new File("./config.json");
        if(!Files.exists(file.toPath())) throw new Error("配置文件不存在");
        ObjectMapper mapper = new ObjectMapper();
        try {
            config = mapper.readValue(file, GitSSHServerConfig.Config.class);

            REPOS_ROOT_DIR=config.getGitRootPath();
            secretKeySpec=createKeySpec(config.getEncodedKey());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}

