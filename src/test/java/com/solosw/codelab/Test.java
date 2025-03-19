package com.solosw.codelab;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletRequest;

import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.http.server.GitServlet;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.ReceivePack;
import org.eclipse.jgit.transport.UploadPack;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.EnumSet;

public class Test {

   public SshServer sshd;
    public Test(int port,String host){
        sshd= CoreTestSupportUtils.setupTestServer(getClass());
        sshd.setPort(port);
        sshd.setHost(host);
        SimpleGeneratorHostKeyProvider hostKeyProvider = new SimpleGeneratorHostKeyProvider();
        hostKeyProvider.setPath(new java.io.File("hostkey.pem").toPath());
        sshd.setKeyPairProvider(hostKeyProvider);

    }
    public void start() throws IOException {
        sshd.start();
    }


    public static void main(String[] args) throws Exception {
        // 定义仓库存储根目录
        File reposRootDir = new File("C:\\Users\\solosw\\Desktop\\CodeLab\\repositories");

        GitServlet gitServlet = new GitServlet();
        gitServlet.setRepositoryResolver((HttpServletRequest req, String name) -> {
            // 从请求路径中提取仓库名称（如 /repo1 → "repo1"）
            String path = req.getPathInfo();  // 如 "/repo1/git-upload-pack"
            String repoName = path.split("/")[1]; // 解析仓库名称

            // 构建仓库路径（约定仓库目录以 .git 结尾）
            File repoDir = new File(reposRootDir, repoName + ".git");

            // 检查仓库是否存在
            if (!repoDir.exists()) {
                throw new RepositoryNotFoundException("仓库不存在: " + repoName);
            }

            try {
                return new FileRepositoryBuilder()
                        .setGitDir(repoDir)
                        .build();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        gitServlet.setUploadPackFactory((req, repo) -> new UploadPack(repo));
        gitServlet.setReceivePackFactory((req, repo) -> new ReceivePack(repo));
        // 配置 Jetty 服务器
        Server server = new Server(9090);
        ServletContextHandler context = new ServletContextHandler();

        context.addServlet(new ServletHolder((Servlet) gitServlet), "/git/*"); // 限定路径前缀
        server.setHandler(context);


        server.start();
    }
}
