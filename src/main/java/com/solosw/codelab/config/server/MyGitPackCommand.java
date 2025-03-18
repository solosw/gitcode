package com.solosw.codelab.config.server;

import com.alibaba.fastjson.JSONArray;
import com.solosw.codelab.entity.po.House;
import com.solosw.codelab.entity.po.HouseRight;
import com.solosw.codelab.entity.po.Users;
import com.solosw.codelab.enums.HouseRightEnum;
import org.apache.sshd.common.util.GenericUtils;
import org.apache.sshd.common.util.MapEntryUtils;
import org.apache.sshd.common.util.threads.CloseableExecutorService;
import org.apache.sshd.git.AbstractGitCommand;
import org.apache.sshd.git.GitLocationResolver;
import org.apache.sshd.server.Environment;
import org.apache.sshd.server.session.ServerSession;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryCache;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.transport.ReceivePack;
import org.eclipse.jgit.transport.UploadPack;
import org.eclipse.jgit.util.FS;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyGitPackCommand extends AbstractGitCommand {
    String rootPath;
    GitPersmionHelper gitPersmionHelper;
    ServerSession session;
    public MyGitPackCommand(GitLocationResolver rootDirResolver, String command,
                            CloseableExecutorService executorService, String rootPath,
                            GitPersmionHelper gitPersmionHelper, ServerSession session) {
        super(rootDirResolver, command, executorService);
        this.rootPath=rootPath;
        this.gitPersmionHelper=gitPersmionHelper;
        this.session=session;
    }

    public void run() {
        String command = this.getCommand();
        try {
            List<String> strs = parseDelimitedString(command, " ", true);
            String[] args = (String[])strs.toArray(new String[strs.size()]);

            for(int i = 0; i < args.length; ++i) {
                String argVal = args[i];
                if (argVal.startsWith("'") && argVal.endsWith("'")) {
                    args[i] = argVal.substring(1, argVal.length() - 1);
                    argVal = args[i];
                }

                if (argVal.startsWith("\"") && argVal.endsWith("\"")) {
                    args[i] = argVal.substring(1, argVal.length() - 1);
                }
            }

            if (args.length != 2) {
                throw new IllegalArgumentException("Invalid git command line (no arguments): " + command);
            }

            Users users= (Users) session.getProperties().get("users");
            String gitPath=args[1];
            House house=gitPersmionHelper.getHouseByPath(gitPath);
            HouseRight houseRight=gitPersmionHelper.getHouseRightByUserAndHouse(users.getId(), house.getId());

            List<HouseRight.Right> rightList= new ArrayList<>();
            if(houseRight!=null){
                rightList.addAll( JSONArray.parseArray(houseRight.getRights(),HouseRight.Right.class));
            }
            Path rootDir = this.resolveRootDirectory(command, args);
            RepositoryCache.FileKey key = RepositoryCache.FileKey.lenient(rootDir.toFile(), FS.DETECTED);
            Repository db = key.open(true);
            String ba= db.getBranch();
            String subCommand = args[0];
            if(house.getKind()==1&&rightList.isEmpty()){
                return;
            }
            if ("git-upload-pack".equals(subCommand)) {

                UploadPack uploadPack = new UploadPack(db);
                uploadPack.setRefFilter((ref)->{
                    Map<String,Ref> saved=new HashMap<>();
                    for(String bran:ref.keySet()){
                        String[] strings=bran.split("/");
                        if(strings.length!=3) continue;
                        String branch=strings[2];
                        for(HouseRight.Right right:rightList){
                            if(right.getOwner()&& !right.getRight().equals(HouseRightEnum.NONE.getPermission())) {
                                return ref;

                            }
                            if(branch.equals(right.getBranch())){
                                if(!right.getRight().equals(HouseRightEnum.NONE.getPermission())){
                                     saved.put(bran, ref.get(bran));
                                     break;
                                }
                            }
                        }
                    }
                    for(String r:ref.keySet()) {
                        if(!saved.containsKey(r)){
                            ref.remove(r);
                        }
                    }

                    return ref;
                });

                Environment environment = this.getEnvironment();
                Map<String, String> envVars = environment.getEnv();
                String protocol = MapEntryUtils.isEmpty(envVars) ? null : (String)envVars.get("GIT_PROTOCOL");
                if (GenericUtils.isNotBlank(protocol)) {
                    //uploadPack.setExtraParameters(Collections.singleton(protocol));
                }
                uploadPack.upload(this.getInputStream(), this.getOutputStream(), this.getErrorStream());
            } else {
                if (!"git-receive-pack".equals(subCommand)) {
                    throw new IllegalArgumentException("Unknown git command: " + command);
                }
                ReceivePack receivePack=new ReceivePack(db);
                if(rightList.isEmpty()) return;
                receivePack.setPreReceiveHook(new MyPreRecieveHook(house,users,houseRight,gitPersmionHelper));
                receivePack.setRefFilter((ref)->{

                    Map<String,Ref> saved=new HashMap<>();
                    for(String bran:ref.keySet()){
                        String[] strings=bran.split("/");
                        if(strings.length!=3) continue;
                        String branch=strings[2];
                        for(HouseRight.Right right:rightList){
                            if(right.getOwner()&& !right.getRight().equals(HouseRightEnum.NONE.getPermission())
                                    && !right.getRight().equals(HouseRightEnum.READ_ONLY.getPermission())) return ref;
                            if(branch.equals(right.getBranch())){
                                if(!right.getRight().equals(HouseRightEnum.NONE.getPermission())&&!right.getRight().equals(HouseRightEnum.READ_ONLY.getPermission())){
                                    saved.put(bran,ref.get(bran));
                                    break;
                                }
                            }
                        }
                    }
                    for(String bran:ref.keySet()){
                        if(saved.containsKey(bran)) continue;
                        ref.remove(bran);
                    }
                    return ref;
                });
                receivePack.receive(this.getInputStream(), this.getOutputStream(), this.getErrorStream());
            }

            this.onExit(0);
        } catch (Throwable var12) {
            this.onExit(-1, var12.getClass().getSimpleName());
        }

    }

    protected Path resolveRootDirectory(String command, String[] args) throws IOException {
        GitLocationResolver resolver = this.getGitLocationResolver();
        Path rootDir = resolver.resolveRootDirectory(command, args, this.getServerSession(), this.getFileSystem());

        return rootDir;
    }
}
