package com.solosw.codelab;

import com.solosw.codelab.utils.GitServerUtil;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.*;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.util.io.DisabledOutputStream;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GitContentComparator {

    public static class CommitContents {
        public final Map<String, String> parentFiles = new HashMap<>();
        public final Map<String, String> childFiles = new HashMap<>();
    }

    /**
     * 获取父子提交文件内容对比
     * @param repoPath Git仓库路径（需包含.git目录的路径）
     * @param commitHash 要分析的提交哈希
     * @return 包含父子提交内容的CommitContents对象
     */
    public static CommitContents compareCommitContents(String repoPath, String commitHash)
            throws IOException, GitAPIException {

        try (Repository repo = new FileRepositoryBuilder()
                .setGitDir(new File(repoPath))
                .build()) {

            ObjectId commitId = repo.resolve(commitHash);
            if (commitId == null) {
                throw new IllegalArgumentException("Invalid commit hash: " + commitHash);
            }

            RevWalk revWalk = new RevWalk(repo);
            RevCommit childCommit = revWalk.parseCommit(commitId);

            // 处理无父提交的情况（如初始提交）
            if (childCommit.getParentCount() == 0) {
                return getInitialCommitContents(repo, childCommit);
            }

            RevCommit parentCommit = revWalk.parseCommit(childCommit.getParent(0).getId());
            return getDiffContents(repo, parentCommit, childCommit);
        }
    }

    private static CommitContents getInitialCommitContents(Repository repo, RevCommit commit)
            throws IOException {

        CommitContents contents = new CommitContents();
        try (TreeWalk treeWalk = new TreeWalk(repo)) {
            treeWalk.addTree(commit.getTree());
            while (treeWalk.next()) {
                String path = treeWalk.getPathString();
                contents.childFiles.put(path, readBlobContent(repo, treeWalk.getObjectId(0)));
            }
        }
        return contents;
    }

    private static CommitContents getDiffContents(Repository repo, RevCommit oldCommit, RevCommit newCommit)
            throws IOException {

        CommitContents contents = new CommitContents();

        try (ObjectReader reader = repo.newObjectReader();
             DiffFormatter diffFormatter = new DiffFormatter(DisabledOutputStream.INSTANCE)) {

            // 设置对比器
            diffFormatter.setRepository(repo);
            diffFormatter.setDiffComparator(RawTextComparator.DEFAULT);
            diffFormatter.setDetectRenames(true);

            // 生成差异列表
            CanonicalTreeParser oldTree = new CanonicalTreeParser();
            oldTree.reset(reader, oldCommit.getTree());

            CanonicalTreeParser newTree = new CanonicalTreeParser();
            newTree.reset(reader, newCommit.getTree());

            for (DiffEntry diff : diffFormatter.scan(oldTree, newTree)) {
                processDiffEntry(repo, contents, diff);
            }
        }
        return contents;
    }

    private static void processDiffEntry(Repository repo, CommitContents contents, DiffEntry diff)
            throws IOException {

        // 处理父提交内容
        if (diff.getOldId() != null && !diff.getOldId().equals(ObjectId.zeroId())) {
            String oldContent = readBlobContent(repo, diff.getOldId().toObjectId());
            contents.parentFiles.put(diff.getOldPath(), oldContent);
        }

        // 处理子提交内容
        if (diff.getNewId() != null && !diff.getNewId().equals(ObjectId.zeroId())) {
            String newContent = readBlobContent(repo, diff.getNewId().toObjectId());
            contents.childFiles.put(diff.getNewPath(), newContent);
        }

        // 特殊处理重命名
        if (diff.getChangeType() == DiffEntry.ChangeType.RENAME) {
            contents.parentFiles.put(diff.getOldPath(), null);
            contents.childFiles.put(diff.getNewPath(),
                    contents.childFiles.remove(diff.getOldPath()));
        }
    }

    private static String readBlobContent(Repository repo, ObjectId blobId) throws IOException {
        ObjectLoader loader = repo.open(blobId);

        // 检测二进制内容（改进版）
        byte[] bytes = loader.getCachedBytes();
        if (RawText.isBinary(bytes)) {
            return "[BINARY CONTENT]";
        }

        // 处理大文件阈值（推荐设置1MB限制）
        if (loader.getSize() > 1024 * 1024*10) {
            return "[LARGE FILE TRUNCATED]";
        }

        return new String(bytes, StandardCharsets.UTF_8);
    }

    public static void main(String[] args) throws Exception {
        String repoPath = "C:\\Users\\solosw\\Desktop\\CodeLab\\repositories\\siki\\happy.git";
        String commitHash = "3e8fd7f36e345e425dc329e82e749630908c8800";
        String f= GitServerUtil.getFatherCommitHash(repoPath,commitHash);
        List<GitServerUtil.DiffInfo> diffInfoList=GitServerUtil.diffByHash(repoPath,commitHash,f);
        System.out.println(diffInfoList);
    }
}

