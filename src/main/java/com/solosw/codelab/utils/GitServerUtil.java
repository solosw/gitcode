package com.solosw.codelab.utils;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.revwalk.*;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.TreeWalk;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.*;


public class GitServerUtil {

    private static final long MAX_IMAGE_SIZE = 5 * 1024 * 1024;


    public static String getFatherCommitHash(String gitDir,String hash){
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "git",
                    "show",
                    "--pretty=format:%P", // 提交说明（标题）
                    "-s",                 // 不显示补丁差异，仅显示提交信息
                    hash                  // 提交哈希值
            );

            processBuilder.directory(new File(gitDir));
            // 启动进程
            Process process = processBuilder.start();

            // 获取命令输出
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {

                return line;
            }
            int exitCode = process.waitFor();

        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }

    public static List<DiffInfo> diffByHash(String gitDir,String newHash,String oldHash) throws Exception{
        File repoDir = new File(gitDir);
        Repository repository = new FileRepositoryBuilder()
                .setGitDir(repoDir)
                .build();
        List<DiffInfo> diffInfoList=new ArrayList<>();
        // 获取提交的对象ID
        ObjectId oldHead = repository.resolve(oldHash);
        ObjectId newHead = repository.resolve(newHash);

        if(oldHead!=null&&newHead!=null){
            AbstractTreeIterator oldTreeParser = prepareTreeParser(repository, oldHead);
            AbstractTreeIterator newTreeParser = prepareTreeParser(repository, newHead);


            ByteArrayOutputStream out = new ByteArrayOutputStream();
            try (DiffFormatter diffFormatter = new DiffFormatter(out)) {
                diffFormatter.setRepository(repository);
                List<DiffEntry> diffs = diffFormatter.scan(oldTreeParser, newTreeParser);

                for (DiffEntry diff : diffs) {
                    // 格式化当前差异条目
                    diffFormatter.format(diff);
                    DiffInfo diffInfo=new DiffInfo().setNewHash(diff.getNewId().name()).
                            setOldHash(diff.getOldId().name()).setStatus(diff.getChangeType().name()).setFileName(diff.getNewPath());

                    if(diff.getChangeType().equals(DiffEntry.ChangeType.ADD)){
                        diffInfo.setNewContent(catFile(gitDir,diffInfo.newHash)).setOldContent("");
                    }else if(diff.getChangeType().equals(DiffEntry.ChangeType.DELETE)){
                        diffInfo.setNewContent("").setOldContent(catFile(gitDir,diffInfo.oldHash));
                    }else{
                        diffInfo.setNewContent(catFile(gitDir,diffInfo.oldHash)).setOldContent(catFile(gitDir,diffInfo.oldHash));
                    }

                    diffInfoList.add(diffInfo);
                }


            }

            // 关闭仓库
            repository.close();
            out.close();
        }


        // 使用 DiffFormatter 来格式化输出差异

        return diffInfoList;
    }


    private static AbstractTreeIterator prepareTreeParser(Repository repository, ObjectId objectId) throws Exception {
        // 从指定的提交对象ID中读取树
        try (RevWalk walk = new RevWalk(repository)) {
            RevCommit commit = walk.parseCommit(objectId);
            RevTree tree = commit.getTree();

            CanonicalTreeParser treeParser = new CanonicalTreeParser();
            try (ObjectReader reader = repository.newObjectReader()) {
                treeParser.reset(reader, tree.getId());
            }
            walk.dispose();

            return treeParser;
        }
    }


    public static List<String> getAllBranches(String gitDir){
        List<String> branches=new ArrayList<>();
        try {
            ProcessBuilder processBuilder=new ProcessBuilder("git","branch");
            processBuilder.directory(new File(gitDir));
            // 启动进程
            Process process = processBuilder.start();

            // 获取命令输出
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("*")) {
                    line = line.substring(1).trim();
                }
                branches.add(line.trim());
            }
            int exitCode = process.waitFor();
            return branches;
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
    public static Map<String,TagInfo> getAllTags(String gitDir){
        try {
            FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
            org.eclipse.jgit.lib.Repository repository = repositoryBuilder.setGitDir(new File(gitDir))
                    .readEnvironment() // 读取环境变量中的配置
                    .findGitDir() // 自动查找.git目录
                    .build();

            // 使用 Git 对象操作仓库
            Git git = new Git(repository);
            Map<String,TagInfo> map=new HashMap<>();
            // 获取所有标签
            List<Ref> tags = git.tagList().call();


            // 打印每个标签的名称和对应的提交哈希
            for (Ref tag : tags) {
                System.out.println("Tag: " + tag.getName());

                // 解析标签对象
                RevWalk revWalk = new RevWalk(repository);
                RevObject obj = revWalk.parseAny(tag.getObjectId());

                if (obj instanceof RevTag) {
                    // 如果是附注标签
                    RevTag revTag = (RevTag) obj;
                    // 获取指向的提交对象
                    RevCommit commit = revWalk.parseCommit(revTag.getObject().getId());
                    TagInfo tagInfo=new TagInfo(revTag.getFullMessage(),"Annotated Tag",revTag.getTaggerIdent().getName(),revTag.getTaggerIdent().getWhen().toString(),
                            tag.getObjectId().getName(),commit.getName(),tag.getName().split("/")[2]);

                    map.put( tag.getObjectId().getName(),tagInfo);
                } else if (obj instanceof RevCommit) {
                    // 如果是轻量标签
                    RevCommit commit = (RevCommit) obj;

                    TagInfo tagInfo=new TagInfo("","Lightweight Tag","",commit.getAuthorIdent().getWhen().toString(),
                            tag.getObjectId().getName(),commit.getName(),tag.getName().split("/")[2]);

                    map.put( tag.getObjectId().getName(),tagInfo);
                }

                revWalk.close();
            }

            // 关闭 Git 对象
            git.close();
            return map;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new HashMap<>();
    }

    public static List<FileInfo> lsTree(String gitDir,String treeNameOrHash,boolean setRecursive){

        try {
            List<FileInfo> fileInfoList=new ArrayList<>();
            // 打开本地仓库
            FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
            Repository repository = repositoryBuilder.setGitDir(new File(gitDir))
                    .readEnvironment() // 读取环境变量中的配置
                    .findGitDir() // 自动查找.git目录
                    .build();

            // 使用 Git 对象操作仓库
            Git git = new Git(repository);

            // 获取指定的提交对象
            ObjectId lastCommitId = repository.resolve(treeNameOrHash);
            RevWalk revWalk = new RevWalk(repository);
            RevCommit commit = revWalk.parseCommit(lastCommitId);
            revWalk.close();

            // 开始遍历树对象
            TreeWalk treeWalk = new TreeWalk(repository);
            treeWalk.addTree(commit.getTree());
            treeWalk.setRecursive(setRecursive); // 设置是否递归遍历子目录

            while (treeWalk.next()) {

                if (treeWalk.isSubtree()) {

                    FileInfo fileInfo=new FileInfo(treeWalk.getFileMode(0).toString(),treeWalk.getPathString(),
                            treeWalk.getObjectId(0).getName(),"tree");
                    fileInfoList.add(fileInfo);
                } else {

                    FileInfo fileInfo=new FileInfo(treeWalk.getFileMode(0).toString(),treeWalk.getPathString(),
                            treeWalk.getObjectId(0).getName(),"blob");
                    fileInfoList.add(fileInfo);
                }
            }
            treeWalk.close();
            repository.close();
            return fileInfoList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
    public static List<FileInfo> lsTree(String gitDir,String treeNameOrHash){

        try {
            List<FileInfo> fileInfoList=new ArrayList<>();
            ProcessBuilder processBuilder=new ProcessBuilder("git","ls-tree",treeNameOrHash);
            processBuilder.directory(new File(gitDir));
            // 启动进程
            Process process = processBuilder.start();

            // 获取命令输出
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            while ((line = reader.readLine()) != null) {

                String[] data = line.split("\\s+", 4);
                String mode=data[0].trim();
                String type=data[1].trim();
                String hash=data[2].trim();
                String name=data[3].trim();
                FileInfo fileInfo=new FileInfo(mode,name,hash,type);
                fileInfoList.add(fileInfo);
            }
            return fileInfoList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
    private static String getContainingBranches(Repository repo, RevCommit commit) throws Exception {
        return Git.wrap(repo).branchList()
                .setContains(commit.name())
                .call().stream()
                .findFirst()
                .map(ref -> ref.getName().replace("refs/heads/", ""))
                .orElse("detached");
    }
    public static List<CommitMessage> getAllCommit(String gitDir) throws Exception{
            Repository repo = Git.open(new File(gitDir)).getRepository();
            List<CommitMessage> commitMessageList=new ArrayList<>();
            Iterable<RevCommit> commits = new Git(repo).log().call();
            for (RevCommit commit : commits) {
                String shortHash = commit.name();
                String author = commit.getAuthorIdent().getName();
                String date = new SimpleDateFormat("yyyy-MM-dd")
                        .format(commit.getAuthorIdent().getWhen());
                String message = commit.getShortMessage();
                List<String> parents=new ArrayList<>();
                for(RevCommit commit1:commit.getParents()){
                    parents.add(commit1.getName());
                }

                // 获取分支信息（需要额外实现）
                String branch = getContainingBranches(repo, commit);
                CommitMessage commitMessage=new CommitMessage().setMessage(message).setAuthor(author).setDate(date)
                                .setHash(shortHash).setBranch(branch).setParents(parents);
                commitMessageList.add(commitMessage);
            }
            return commitMessageList;

    }

    public static String catFile(String gitDir,String fileHash){
        try {
            ProcessBuilder processBuilder=new ProcessBuilder("git","cat-file","-p",fileHash);
            processBuilder.directory(new File(gitDir));
            // 启动进程
            Process process = processBuilder.start();

            // 获取命令输出
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            StringBuilder result= new StringBuilder();
            while ((line = reader.readLine()) != null) {

                result.append(line);
                result.append("\n");
            }
            int exitCode = process.waitFor();
            return result.toString();
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }
    public static byte[] catFileByByte(String gitDir,String fileHash){
        try {
            // 打开仓库
            Repository repository = new FileRepositoryBuilder()
                    .setGitDir(new File(gitDir))
                    .build();

            // 将字符串形式的hash转换为ObjectId
            ObjectId objectId = ObjectId.fromString(fileHash);

            // 从仓库中打开该对象
            ObjectLoader objectLoader = repository.open(objectId);

            // 读取为字节数组
            byte[] bytes = objectLoader.getBytes();
            if(bytes.length>MAX_IMAGE_SIZE){
                return new byte[0];
            }
            // 这里可以对bytes进行处理，比如保存到文件或进一步解析等
           return bytes;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new byte[0];
    }
    public static List<CommitInfo> findFileLatestCommit(String gitDir,String fileHash){
        try {
            List<CommitInfo> commitInfoList=new ArrayList<>();
            // 构建 git log 命令
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "git",
                    "log",
                    "-1",
                    "--pretty=format:%h|%an|%ad|%s",
                    "--date=format:%Y-%m-%d %H:%M:%S",
                    "--all",
                    "--find-object=" + fileHash
            );

            // 设置工作目录为 Git 仓库路径
            processBuilder.directory(new File(gitDir));

            // 启动进程
            Process process = processBuilder.start();

            // 捕获命令输出
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] greps=line.split("\\|");
                CommitInfo commitInfo=new CommitInfo(greps[0],greps[1],greps[2],greps[3]);
                commitInfoList.add(commitInfo);
            }

            // 等待进程完成并获取退出状态
            int exitCode = process.waitFor();

            return commitInfoList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  new ArrayList<>();
    }
    public static void initGitArea(String dirGit){
        File repoDir = new File(dirGit);

        try {
            if(repoDir.exists()){
                System.err.println("directory has existed");
                return;
            }

            // 使用 Git.init() 方法初始化一个新的裸仓库
            Git git = Git.init()
                    .setDirectory(repoDir)
                    .setBare(true)  // 设置为裸仓库
                    .call();

            // 关闭 Git 对象
            git.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static void deleteDirectory(Path path) throws IOException {
        if (Files.exists(path)) {
            Files.walk(path) // 遍历目录树
                    .sorted((p1, p2) -> -p1.compareTo(p2)) // 按逆序排序（先删除子文件/目录）
                    .forEach(p -> {
                        try {
                            System.out.println("Deleting: " + p);
                            Files.delete(p); // 删除文件或空目录
                        } catch (IOException e) {
                            throw new RuntimeException("Failed to delete: " + p, e);
                        }
                    });
        }
    }
    public static void deleteRep(String path){
        if(Files.exists(Path.of(path))){
            Thread t=new Thread(()->{
                try {
                    deleteDirectory(Path.of(path));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            t.start();
        }
    }
    public static void deleteBranch(String path,String branchName){
        try {
            // 打开本地 Git 仓库
            Git git = Git.open(new File(path));
            git.branchDelete()
                    .setBranchNames(branchName)
                    .setForce(true) // 强制删除（如果分支未完全合并）
                    .call().wait();
            git.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) throws Exception {
     //  System.out.println( getAllBranches("C:\\Users\\solosw\\Desktop\\CodeLab\\test\\test.git"));
      //System.out.println( getAllTags("C:\\Users\\solosw\\Desktop\\CodeLab\\test\\test.git"));
      // System.out.println( lsTree("C:\\Users\\solosw\\Desktop\\CodeLab\\test\\test.git","dev",false));

       //System.out.println( findFileLatestCommit("C:\\Users\\solosw\\Desktop\\CodeLab\\test\\test.git","c9ae1a982371cfd60082a5ae831b4febc01bee73"));
        // System.out.println(catFile("C:\\Users\\solosw\\Desktop\\CodeLab\\test\\test.git","c9ae1a982371cfd60082a5ae831b4febc01bee73"));

        //initGitArea("C:\\Users\\solosw\\Desktop\\CodeLab\\test\\test1.git");
      //System.out.println(  diffByHash("C:\\Users\\solosw\\Desktop\\CodeLab\\test\\test.git","216b5e1","3a47fa3"));
        //System.out.println(getFatherCommitHash("C:\\Users\\solosw\\Desktop\\CodeLab\\test\\test.git","248543e"));
        System.out.println(Base64.getEncoder().encodeToString(catFileByByte(GitoliteUtil.getRepositoryPath("siki/happy"), "7a3cb65c5fd0c049485d9a90fd215895b543080c")));

    }





    @Data
    @NoArgsConstructor
    @Accessors(chain = true)
    public static class DiffInfo{
        String fileName;
        String status;
        String oldHash;
        String newHash;
        String oldContent;
        String newContent;
        @Data
        @NoArgsConstructor
        @Accessors(chain = true)
        public  static class RowInfo{
            Integer oldStart;
            Integer oldEnd;
            Integer newStart;
            Integer newEnd;

            public RowInfo(Integer oldStart, Integer oldEnd, Integer newStart, Integer newEnd) {
                this.oldStart = oldStart;
                this.oldEnd = oldEnd;
                this.newStart = newStart;
                this.newEnd = newEnd;
            }
        }

    }
@Data
@Accessors(chain = true)
    public static class CommitMessage{
        String  hash;
        String author;
        String date;
        List<String>  parents;
        String message;
        String branch;
    }
    @Data
    @NoArgsConstructor

    public static class TagInfo{
        String tagMessage;
        String type;
        String tagger;

        public String getDateTime() {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return formatter.format(new Date(dateTime));
        }


        String dateTime;
        String tagHash;
        String commitHash;
        String tagName;
        public TagInfo(String tagMessage, String type, String tagger, String dateTime, String tagHash, String commitHash,String tagName) {
            this.tagMessage = tagMessage;
            this.type = type;
            this.tagger = tagger;
            this.dateTime = dateTime;
            this.tagHash = tagHash;
            this.commitHash = commitHash;
            this.tagName=tagName;
        }

    }




    @Data
    @NoArgsConstructor
    public static class FileInfo{
        String mode,name,hash,type;

        public FileInfo(String mode, String name, String hash, String type) {
            this.mode = mode;
            this.name = name;
            this.hash = hash;
            this.type = type;
        }
    }
    @Data
    @NoArgsConstructor
    public static class CommitInfo{
        String hash,author,time,message;

        public CommitInfo(String hash, String author, String time, String message) {
            this.hash = hash;
            this.author = author;
            this.time = time;
            this.message = message;
        }
    }
}
