package  com.solosw.codelab.utils;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.transport.*;
import org.h2.util.StringUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GitoliteUtil {
 public  static  String workingDirectory = "C:/Users/solosw/Desktop/CodeLab";
 public static  String gitRepoUrlPrx="45.207.211.56";

 public static String getUrl(String username,String path){
     return "ssh://"+username+"@"+gitRepoUrlPrx+"/"+path;
 }
 public static String getRepositoryPath(String path){
     return workingDirectory+"/"+path+".git";
 }


    /*
    public static synchronized void deleteUserSSHKeyToGitolite(String username) throws IOException, InterruptedException {
        String keyDirPath = workingDirectory + "/gitolite-admin/keydir/";
        String publicKeyFilePath = keyDirPath + username + ".pub";
        File keyDir = new File(publicKeyFilePath);
        if (!keyDir.exists()) {

            throw new IOException("Failed to find key directory");

        }else {
            keyDir.delete();
        }



    }


    public static synchronized void addOrModifyUserSSHKeyToGitolite(String username, String publicKeyContent) throws IOException, InterruptedException {
        String keyDirPath = workingDirectory + "/gitolite-admin/keydir/";
        String publicKeyFilePath = keyDirPath + username + ".pub";
        int exitCode;
        // Create key directory if it doesn't exist
        File keyDir = new File(keyDirPath);
        if (!keyDir.exists()) {

            throw new IOException("Failed to find key directory");

        }

        // Write public key content to file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(publicKeyFilePath))) {
            writer.write(publicKeyContent);
        }



    }

    public static synchronized void cloneOrPullRepo( ) throws IOException, InterruptedException {
        File repoDir = new File(workingDirectory);
        if (!repoDir.exists()) {
            Process cloneProcess = new ProcessBuilder("git", "clone", gitoliteAdminRepoUrl)
                    .directory(new File(workingDirectory))
                    .start();
            int exitCode = cloneProcess.waitFor();
            System.out.println("Clone process exited with code " + exitCode);
        } else {
            Process pullProcess = new ProcessBuilder("git", "-C", workingDirectory + "/gitolite-admin", "pull")
                    .start();
            int exitCode = pullProcess.waitFor();
            System.out.println("Pull process exited with code " + exitCode);
        }
    }


    public static synchronized void createRepository(String repoName,String owerName) throws IOException {
        Path confFilePath = Paths.get(workingDirectory+"/gitolite-admin/conf/gitolite.conf");
        StringBuilder confContent = new StringBuilder();

        // Read existing conf file content
        if (Files.exists(confFilePath)) {
            confContent.append(new String(Files.readAllBytes(confFilePath)));
        }

        // Append new repository section
        confContent.append("\nrepo ").append(repoName).append("\n");
        confContent.append("    RW+ = "+owerName+"\n"); // Assuming 'admin' has full access

        // Write updated conf content back to the file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(confFilePath.toFile()))) {
            writer.write(confContent.toString());
        }
        System.out.println("Created repository " + repoName);
    }


    public static synchronized void deleteRepository(String repoName) throws IOException {
        Path confFilePath = Paths.get(workingDirectory+"/gitolite-admin/conf/gitolite.conf");
        List<String> lines = Files.readAllLines(confFilePath);
        Map<String, List<String>> repoPermissions = new HashMap<>();
        String currentRepo = null;
        for (String line : lines) {
            if (line.trim().startsWith("repo ")) {
                currentRepo = line.split("\\s+")[1];
                repoPermissions.put(currentRepo, new ArrayList<>());
            }
            if (currentRepo!=null&&!line.trim().isEmpty() && !line.startsWith("repo ")) {
                repoPermissions.get(currentRepo).add(line.trim());
            }
        }
        if (!repoPermissions.containsKey(repoName)) {
            throw new IOException("Repository " + repoName + " not found in conf/gitolite.conf");
        }else {
            repoPermissions.remove(repoName);
        }

        StringBuilder confContent = new StringBuilder();
        for(var temp:repoPermissions.entrySet()){
            List<String> rules=temp.getValue();
            confContent.append("repo ").append(temp.getKey()).append("\n");
            for (String line:rules){
                if(!line.trim().isEmpty())
                    confContent.append("    ").append(line).append("\n");
            }
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(confFilePath.toFile()))) {
            writer.write(confContent.toString());
        }

        System.out.println("Deleted repository " + repoName);
    }


    public static synchronized void addUsersToRepository(String repoName, List<String> users,String permission) throws IOException, InterruptedException {

        updateConfForUsers(repoName, users, permission);
    }
    public static synchronized void addUsersToRepository(String repoName, List<UserRule> users) throws IOException, InterruptedException {

        updateConfForUsers(repoName, users);
    }

    public static synchronized void modifyUserAccessInRepository(String repoName, String username, String permission) throws IOException {
        Path confFilePath = Paths.get(workingDirectory+"/gitolite-admin/conf/gitolite.conf");
        List<String> lines = Files.readAllLines(confFilePath);
        Map<String, List<String>> repoPermissions = new HashMap<>();
        String currentRepo = null;
        for (String line : lines) {
            if (line.trim().startsWith("repo ")) {
                currentRepo = line.split("\\s+")[1];
                repoPermissions.put(currentRepo, new ArrayList<>());
            }
            if (currentRepo!=null&&!line.trim().isEmpty() && !line.startsWith("repo ")) {
                repoPermissions.get(currentRepo).add(line.trim());
            }
        }
        if (!repoPermissions.containsKey(repoName)) {
            throw new IOException("Repository " + repoName + " not found in conf/gitolite.conf");
        }

        // Add new users with specified permissions
            boolean userExists=false;
            for (String permLine : repoPermissions.get(repoName)) {
                if (permLine.contains(username)) {
                    userExists = true;
                    repoPermissions.get(repoName).remove(permLine);
                    break;
                }
            }
            if (!userExists) {
                throw new IOException("Repository userName called " + username + " not found in conf/gitolite.conf");
            }else {
                repoPermissions.get(repoName).add(permission+" = "+username);
            }
        StringBuilder confContent = new StringBuilder();
        for(var temp:repoPermissions.entrySet()){
            List<String> rules=temp.getValue();
            confContent.append("repo ").append(temp.getKey()).append("\n");
            for (String line:rules){
                if(!line.trim().isEmpty())
                    confContent.append("    ").append(line).append("\n");
            }
        }


        // Write updated conf content back to the file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(confFilePath.toFile()))) {
            writer.write(confContent.toString());
        }


        System.out.println("Modified user " + username + " access in repository " + repoName);
    }
    public static synchronized void modifyUserAccessInRepository(String repoName, UserRule user) throws IOException {
        Path confFilePath = Paths.get(workingDirectory+"/gitolite-admin/conf/gitolite.conf");
        List<String> lines = Files.readAllLines(confFilePath);
        Map<String, List<String>> repoPermissions = new HashMap<>();
        String currentRepo = null;
        for (String line : lines) {
            if (line.trim().startsWith("repo ")) {
                currentRepo = line.split("\\s+")[1];
                repoPermissions.put(currentRepo, new ArrayList<>());
            }
            if (currentRepo!=null&&!line.trim().isEmpty() && !line.startsWith("repo ")) {
                repoPermissions.get(currentRepo).add(line.trim());
            }
        }
        if (!repoPermissions.containsKey(repoName)) {
            throw new IOException("Repository " + repoName + " not found in conf/gitolite.conf");
        }

        // Add new users with specified permissions
        boolean userExists=false;
        for (String permLine : repoPermissions.get(repoName)) {
            if (permLine.contains(user.getUserName())) {
                userExists = true;
                repoPermissions.get(repoName).remove(permLine);
                break;
            }
        }
        if (!userExists) {
            throw new IOException("Repository userName called " + user.getUserName() + " not found in conf/gitolite.conf");
        }else {
            repoPermissions.get(repoName).add(user.getRule());
        }
        StringBuilder confContent = new StringBuilder();
        for(var temp:repoPermissions.entrySet()){
            List<String> rules=temp.getValue();
            confContent.append("repo ").append(temp.getKey()).append("\n");
            for (String line:rules){
                if(!line.trim().isEmpty())
                    confContent.append("    ").append(line).append("\n");
            }
        }


        // Write updated conf content back to the file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(confFilePath.toFile()))) {
            writer.write(confContent.toString());
        }


        System.out.println("Modified user " + user.getUserName() + " access in repository " + repoName);
    }


    public static synchronized void removeUserFromRepository(String repoName, String username) throws IOException {
        Path confFilePath = Paths.get(workingDirectory+"/gitolite-admin/conf/gitolite.conf");
        List<String> lines = Files.readAllLines(confFilePath);
        Map<String, List<String>> repoPermissions = new HashMap<>();
        String currentRepo = null;
        for (String line : lines) {
            if (line.trim().startsWith("repo ")) {
                currentRepo = line.split("\\s+")[1];
                repoPermissions.put(currentRepo, new ArrayList<>());
            }
            if (currentRepo!=null&&!line.trim().isEmpty() && !line.startsWith("repo ")) {
                repoPermissions.get(currentRepo).add(line.trim());
            }
        }
        if (!repoPermissions.containsKey(repoName)) {
            throw new IOException("Repository " + repoName + " not found in conf/gitolite.conf");
        }

        // Add new users with specified permissions
        boolean userExists=false;
        for (String permLine : repoPermissions.get(repoName)) {
            if (permLine.contains(username)) {
                userExists = true;
                repoPermissions.get(repoName).remove(permLine);
                break;
            }
        }
        if (!userExists) {
            throw new IOException("Repository userName called " + username + " not found in conf/gitolite.conf");
        }
        StringBuilder confContent = new StringBuilder();
        for(var temp:repoPermissions.entrySet()){
            List<String> rules=temp.getValue();
            confContent.append("repo ").append(temp.getKey()).append("\n");
            for (String line:rules){
                if(!line.trim().isEmpty())
                    confContent.append("    ").append(line).append("\n");
            }
        }


        // Write updated conf content back to the file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(confFilePath.toFile()))) {
            writer.write(confContent.toString());
        }

        System.out.println("Removed user " + username + " from repository " + repoName);
    }


     static void updateConfForUsers(String repoName, List<String> users, String permission) throws IOException {
        Path confFilePath = Paths.get(workingDirectory+"/gitolite-admin/conf/gitolite.conf");
        List<String> lines = Files.readAllLines(confFilePath);

        Map<String, List<String>> repoPermissions = new HashMap<>();
        String currentRepo = null;
        for (String line : lines) {
            if (line.trim().startsWith("repo ")) {
                currentRepo = line.split("\\s+")[1];
                repoPermissions.put(currentRepo, new ArrayList<>());
            }
            if (currentRepo!=null&&!line.trim().isEmpty() && !line.startsWith("repo ")) {
                repoPermissions.get(currentRepo).add(line.trim());
            }
        }

        if (!repoPermissions.containsKey(repoName)) {
            throw new IOException("Repository " + repoName + " not found in conf/gitolite.conf");
        }

        // Add new users with specified permissions
        for (String user : users) {
            boolean userExists = false;
            for (String permLine : repoPermissions.get(repoName)) {
                if (permLine.contains(user)) {
                    userExists = true;
                    break;
                }
            }
            if (!userExists) {
                repoPermissions.get(repoName).add(permission + " = " + user);
            }
        }

        // Reconstruct conf content
        StringBuilder confContent = new StringBuilder();
        for(var temp:repoPermissions.entrySet()){
            List<String> rules=temp.getValue();
            confContent.append("repo ").append(temp.getKey()).append("\n");
            for (String line:rules){
                if(!line.trim().isEmpty())
                    confContent.append("    ").append(line).append("\n");
            }
        }


        // Write updated conf content back to the file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(confFilePath.toFile()))) {
            writer.write(confContent.toString());
        }
        System.out.println("Updated conf/gitolite.conf for repository " + repoName);
    }


    public static synchronized  void commitAndPushChanges() throws Exception {
        File repoDir = new File(workingDirectory+"/gitolite-admin");

            Git git = Git.open(repoDir);
            // Add all changes
            git.add().addFilepattern(".").call();
            System.out.println("All changes added.");

            // Commit changes
            git.commit().setMessage("Update Gitolite configuration").call();
            System.out.println("Changes committed.");

        SshSessionFactory sshSessionFactory = new JschConfigSessionFactory() {
            @Override
            protected void configure(OpenSshConfig.Host host, Session session) {
                // 设置 StrictHostKeyChecking 为 no（可选，避免首次连接时的确认提示）
                session.setConfig("StrictHostKeyChecking", "no");
            }

            @Override
            protected JSch createDefaultJSch(org.eclipse.jgit.util.FS fs) throws JSchException {
                JSch jsch = super.createDefaultJSch(fs);
                // 添加私钥路径
                jsch.addIdentity(privateKey); // 替换为你的私钥路径
                return jsch;
            }
        };
        Git local = Git.open(new java.io.File(repoDir+"/.git"));
        // 3. 设置全局 SSH 会话工厂
        SshSessionFactory.setInstance(sshSessionFactory);

        // 4. 创建 PushCommand 并设置 SSH 传输配置
        PushCommand pushCommand = local.push();
        pushCommand.setTransportConfigCallback(transport -> {
            if (transport instanceof SshTransport) {
                ((SshTransport) transport).setSshSessionFactory(sshSessionFactory);
            }
        });

        // 5. 执行推送
        Iterable iterable = pushCommand.call();
        System.out.println("Changes pushed.");

    }

     static void updateConfForUsers(String repoName, List<UserRule> users) throws IOException {
        Path confFilePath = Paths.get(workingDirectory+"/gitolite-admin/conf/gitolite.conf");
        List<String> lines = Files.readAllLines(confFilePath);

        Map<String, List<String>> repoPermissions = new HashMap<>();
        String currentRepo = null;
        for (String line : lines) {
            if (line.trim().startsWith("repo ")) {
                currentRepo = line.split("\\s+")[1];
                repoPermissions.put(currentRepo, new ArrayList<>());
            }
            if (currentRepo!=null&&!line.trim().isEmpty() && !line.startsWith("repo ")) {
                repoPermissions.get(currentRepo).add(line.trim());
            }
        }

        if (!repoPermissions.containsKey(repoName)) {
            throw new IOException("Repository " + repoName + " not found in conf/gitolite.conf");
        }

        // Add new users with specified permissions
        for (UserRule user : users) {
            boolean userExists = false;
            for (String permLine : repoPermissions.get(repoName)) {
                if (permLine.contains(user.getUserName())) {
                    userExists = true;
                    break;
                }
            }
            if (!userExists) {
                repoPermissions.get(repoName).add(user.getRule());
            }
        }

        // Reconstruct conf content
        StringBuilder confContent = new StringBuilder();
        for(var temp:repoPermissions.entrySet()){
            List<String> rules=temp.getValue();
            confContent.append("repo ").append(temp.getKey()).append("\n");
            for (String line:rules){
                if(!line.trim().isEmpty())
                    confContent.append("    ").append(line).append("\n");
            }
        }


        // Write updated conf content back to the file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(confFilePath.toFile()))) {
            writer.write(confContent.toString());
        }
        System.out.println("Updated conf/gitolite.conf for repository " + repoName);
    }


    @Data
    @Accessors(chain = true)
    @NoArgsConstructor
    public static class UserRule{
        String userName;
        GitolitePermission permission;
        String branch;
        String repo;
        String sshKey;
        public String getRule(){
            if(!StringUtils.isNullOrEmpty(branch)) {
                return permission.getPermission()+" "+branch+" = "+userName;
            }
            return permission.getPermission()+" = "+userName;
        }
    }

    public static enum GitolitePermission {
        READ_ONLY("R"),       // 只读权限
        READ_WRITE("RW"),     // 读写权限
        READ_WRITE_FORCE("RW+"), // 读写及强制更新权限
        NONE("-"),            // 没有权限
        CREATE("C"),          // 创建权限
        DELETE("D");          // 删除权限

        private final String permission;

        GitolitePermission(String permission) {
            this.permission = permission;
        }

        public String getPermission() {
            return permission;
        }

        @Override
        public String toString() {
            return String.format("%s: %s", name(), permission);
        }

    }
    */
}




