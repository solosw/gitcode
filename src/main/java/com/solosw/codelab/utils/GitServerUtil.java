package com.solosw.codelab.utils;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import net.sf.jsqlparser.expression.DateTimeLiteralExpression;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.revwalk.*;
import org.eclipse.jgit.storage.file.FileBasedConfig;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;

import java.io.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class GitServerUtil {




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

        // 准备两个树解析器
        AbstractTreeIterator oldTreeParser = prepareTreeParser(repository, oldHead);
        AbstractTreeIterator newTreeParser = prepareTreeParser(repository, newHead);

        // 使用 DiffFormatter 来格式化输出差异
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (DiffFormatter diffFormatter = new DiffFormatter(out)) {
            diffFormatter.setRepository(repository);

            List<DiffEntry> diffs = diffFormatter.scan(oldTreeParser, newTreeParser);
            String regex = "@@ -(\\d+),(\\d+) \\+(\\d+),(\\d+) @@";
            Pattern pattern = Pattern.compile(regex);
            for (DiffEntry diff : diffs) {
                // 格式化当前差异条目
                diffFormatter.format(diff);
                String res = out.toString("UTF-8");
                out.reset();  // 重置 StringWriter 以便下次使用

                Matcher matcher = pattern.matcher(res);
                DiffInfo diffInfo=new DiffInfo(diff.getNewPath(),new ArrayList<>());

                boolean matched = false;
                while (matcher.find()) {  // 使用 while 循环来处理可能的多行匹配
                    matched = true;

                    // 提取旧文件的起始行号和行数
                    int oldStartLine = Integer.parseInt(matcher.group(1));
                    int oldLineCount = Integer.parseInt(matcher.group(2));

                    // 提取新文件的起始行号和行数
                    int newStartLine = Integer.parseInt(matcher.group(3));
                    int newLineCount = Integer.parseInt(matcher.group(4));

                    DiffInfo.RowInfo rowInfo=new DiffInfo.RowInfo(oldStartLine,oldStartLine+oldLineCount-1,
                            newStartLine,newStartLine+newLineCount-1);
                    diffInfo.getRowInfos().add(rowInfo);
                }

                if (!matched) {
                    System.out.println("No match found.");
                }else {
                    diffInfoList.add(diffInfo);
                }
                out.reset();
            }


        }

        // 关闭仓库
        repository.close();
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
            // 如果目录不存在，则创建它
            if (!repoDir.exists()) {
                boolean created = repoDir.mkdirs();
                if (!created) {
                    System.err.println("Failed to create directory for bare repository.");
                    return;
                }
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


    public static void main(String[] args) throws Exception {
     //  System.out.println( getAllBranches("C:\\Users\\solosw\\Desktop\\CodeLab\\test\\test.git"));
      //System.out.println( getAllTags("C:\\Users\\solosw\\Desktop\\CodeLab\\test\\test.git"));
      // System.out.println( lsTree("C:\\Users\\solosw\\Desktop\\CodeLab\\test\\test.git","dev",false));

       //System.out.println( findFileLatestCommit("C:\\Users\\solosw\\Desktop\\CodeLab\\test\\test.git","c9ae1a982371cfd60082a5ae831b4febc01bee73"));
        // System.out.println(catFile("C:\\Users\\solosw\\Desktop\\CodeLab\\test\\test.git","c9ae1a982371cfd60082a5ae831b4febc01bee73"));

        //initGitArea("C:\\Users\\solosw\\Desktop\\CodeLab\\test\\test1.git");
      //System.out.println(  diffByHash("C:\\Users\\solosw\\Desktop\\CodeLab\\test\\test.git","216b5e1","3a47fa3"));
        //System.out.println(getFatherCommitHash("C:\\Users\\solosw\\Desktop\\CodeLab\\test\\test.git","248543e"));
    }





    @Data
    @NoArgsConstructor
    @Accessors(chain = true)
    public static class DiffInfo{
        String fileName;
        List<RowInfo> rowInfos;

        public DiffInfo(String fileName, List<RowInfo> rowInfos) {
            this.fileName = fileName;
            this.rowInfos = rowInfos;
        }

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
