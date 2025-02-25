package com.solosw.codelab.task;

import com.solosw.codelab.utils.GitoliteUtil;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.antlr.v4.runtime.misc.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


@Component

public class GitoliteTask {

    @Getter
    public static enum TaskType{
        create_rep(0),
        delete_rep(1),
        add_user(2),
        change_user(3),
        delete_user(4),

        change_rep(5),

        add_ssh(6),
        change_ssh(7),

        delete_ssh(8);
        private final int value;

        TaskType(int value) {
            this.value = value;
        }

    }

    @PostConstruct
    void init(){
        startProcessing();
    }

    @Data
    @Accessors(chain = true)
    @NoArgsConstructor
    public static class Task{

        TaskType taskType;
        GitoliteUtil.UserRule userRule;
        List<GitoliteUtil.UserRule> userRuleList;
        String repo;

    }
    private final BlockingQueue<Task> queue = new LinkedBlockingQueue<>();

     void startProcessing() {
        Thread processorThread = new Thread(() -> {
            while (true) {


                Task data = null;
                try {
                    data = queue.take();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                try {
                    processData(data);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                System.out.println("task::"+queue.size());
                    if(queue.isEmpty()){


                        try {
                            GitoliteUtil.commitAndPushChanges();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }


            }
        });
        processorThread.start();
    }

    public void addToQueue(Task data) {
        try {
            queue.put(data);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Adding to queue was interrupted");
        }
    }

    private void processData(Task task) throws Exception {
        switch (task.getTaskType()) {
            case create_rep:
               createRep(task);
                break;
            case delete_rep:
               deleteRep(task);
                break;
            case add_user:
                addUser(task);
                break;
            case change_user:
                changeUser(task);
                break;
            case delete_user:
                deleteUser(task);
                break;
            case change_rep:
                changeRep(task);
                break;
            case add_ssh:
               addSsHkey(task);
                break;
            case change_ssh:
               addSsHkey(task);
                break;

            case delete_ssh:
                deleteSsHkey(task);
                break;
            default:
                System.out.println("Unknown task type: ");
        }

    }

     void  createRep(Task task) throws Exception {
            GitoliteUtil.createRepository(task.repo, task.userRule.getUserName());

     }
    void  deleteRep(Task task) throws Exception {

           GitoliteUtil.deleteRepository(task.getRepo());

    }

    void addUser(Task task) throws Exception{
        if(task.userRule!=null){
            GitoliteUtil.addUsersToRepository(task.repo, Collections.singletonList(task.userRule));
        }else {
            if(task.userRuleList!=null){
                GitoliteUtil.addUsersToRepository(task.repo,task.userRuleList );
            }
        }
    }


    void changeUser(Task task) throws IOException {
        GitoliteUtil.modifyUserAccessInRepository(task.getRepo(),task.getUserRule());
    }


    void deleteUser(Task task) throws IOException {
        GitoliteUtil.removeUserFromRepository(task.getRepo(),task.getUserRule().getUserName());
    }

    void changeRep(Task task){

    }
    void addSsHkey(Task task) throws IOException, InterruptedException {
        GitoliteUtil.addOrModifyUserSSHKeyToGitolite(task.getUserRule().getUserName(),task.userRule.getSshKey());
    }
    void deleteSsHkey(Task task) throws IOException, InterruptedException {
        GitoliteUtil.deleteUserSSHKeyToGitolite(task.getUserRule().getUserName());
    }




}
