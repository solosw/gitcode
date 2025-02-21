package  com.solosw.codelab.utils;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
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
 public static   String gitoliteAdminRepoUrl = "mike@45.207.211.56:gitolite-admin";
    public static void main(String[] args) {


        try {
            // Clone the gitolite-admin repository if not already cloned
//cloneOrPullRepo(workingDirectory, gitoliteAdminRepoUrl);

            // Example operations
            //createRepository("newRepo1","solosw");
            //addUsersToRepository("newRepo1", List.of("user1","user2"),"RW+");
            //modifyUserAccessInRepository("newRepo1", "user1", "RW");
            //removeUserFromRepository("newRepo", "user1");
           // deleteRepository("newRepo1");
            //addUserSSHKeyToGitolite("so","111jkghih");
            // Commit and push changes
          //  commitAndPushChanges(workingDirectory);
            //deleteUserSSHKeyToGitolite("so");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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

    /**
     * Creates a new repository in the Gitolite configuration.
     *
     * @param repoName The name of the new repository.
     * @throws IOException If an I/O error occurs.
     */
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

    /**
     * Deletes a repository from the Gitolite configuration.
     *
     * @param repoName The name of the repository to delete.
     * @throws IOException If an I/O error occurs.
     */
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

    /**
     * Adds users to a repository in the Gitolite configuration.
     *
     * @param repoName         The name of the repository.
     * @param users            A list of usernames for the new users.
     * @throws IOException          If an I/O error occurs.
     * @throws InterruptedException If the process is interrupted.
     */
    public static synchronized void addUsersToRepository(String repoName, List<String> users,String permission) throws IOException, InterruptedException {

        updateConfForUsers(repoName, users, permission);
    }
    public static synchronized void addUsersToRepository(String repoName, List<UserRule> users) throws IOException, InterruptedException {

        updateConfForUsers(repoName, users);
    }
    /**
     * Modifies user access in a repository in the Gitolite configuration.
     *
     * @param repoName   The name of the repository.
     * @param username   The username of the user whose access needs modification.
     * @param permission The new permission level (e.g., RW+, R, W).
     * @throws IOException If an I/O error occurs.
     */
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

    /**
     * Removes a user from a repository in the Gitolite configuration.
     *
     * @param repoName The name of the repository.
     * @param username The username of the user to remove.
     * @throws IOException If an I/O error occurs.
     */
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

    /**
     * Updates the conf/gitolite.conf file for users with specified permissions.
     *
     * @param repoName   The name of the repository.
     * @param users      A list of usernames.
     * @param permission The permission level (e.g., RW+, R, W).
     * @throws IOException If an I/O error occurs.
     */
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


    public static synchronized  void commitAndPushChanges() throws IOException, InterruptedException {
        // Add all changes
        Process addProcess = new ProcessBuilder("git", "-C", workingDirectory + "/gitolite-admin", "add", ".")
                .start();
        int exitCode = addProcess.waitFor();
        System.out.println("Add process exited with code " + exitCode);

        // Commit changes
        Process commitProcess = new ProcessBuilder("git", "-C", workingDirectory + "/gitolite-admin", "commit", "-m", "Update Gitolite configuration")
                .start();
        exitCode = commitProcess.waitFor();
        System.out.println("Commit process exited with code " + exitCode);

        // Push changes
        Process pushProcess = new ProcessBuilder("git", "-C", workingDirectory + "/gitolite-admin", "push")
                .start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(pushProcess.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
        exitCode = pushProcess.waitFor();
        System.out.println("Push process exited with code " + exitCode);
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
}




