package com.solosw.codelab.config.server;

import org.apache.sshd.client.ClientBuilder;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.config.hosts.HostConfigEntryResolver;
import org.apache.sshd.client.keyverifier.AcceptAllServerKeyVerifier;
import org.apache.sshd.common.NamedFactory;
import org.apache.sshd.common.cipher.BuiltinCiphers;
import org.apache.sshd.common.cipher.Cipher;
import org.apache.sshd.common.kex.BuiltinDHFactories;
import org.apache.sshd.common.keyprovider.KeyIdentityProvider;
import org.apache.sshd.common.signature.BuiltinSignatures;
import org.apache.sshd.common.signature.Signature;
import org.apache.sshd.common.util.GenericUtils;
import org.apache.sshd.core.CoreModuleProperties;
import org.apache.sshd.git.GitLocationResolver;
import org.apache.sshd.server.ServerBuilder;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.session.ServerSession;
import org.apache.sshd.sftp.server.SftpSubsystemFactory;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class CoreTestSupportUtils {
    public static final Duration READ_TIMEOUT = getTimeout("read.nio2", Duration.ofSeconds(60*10));


    private CoreTestSupportUtils() {
        throw new UnsupportedOperationException("No instance");
    }

    public static int getFreePort() throws Exception {
        try (ServerSocket s = new ServerSocket()) {
            s.setReuseAddress(true);
            s.bind(new InetSocketAddress((InetAddress) null, 0));
            return s.getLocalPort();
        }
    }

    public static class MyGitLocationResolver implements GitLocationResolver {

        String rootPath;

        public MyGitLocationResolver(String rootPath) {
            this.rootPath = rootPath;
        }

        @Override
        public Path resolveRootDirectory(String s, String[] strings, ServerSession serverSession, FileSystem fileSystem) throws IOException {
            if(strings.length!=2){
                throw new RuntimeException("命令错误");
            }
            String p=rootPath+strings[1]+".git";
            Path path = Path.of(p);
            if(!Files.exists(path))throw new RuntimeException("目录不存在");
            return path;
        }
    }


    public static <S extends SshServer> S setupTestServer(S sshd, Integer maxThread,String rootPath,GitPersmionHelper gitPersmionHelper) {

        sshd.setPublickeyAuthenticator(new MyPublickeyAuthenticator(gitPersmionHelper));
        sshd.setSubsystemFactories(Collections.singletonList(new SftpSubsystemFactory()));
        sshd.setCommandFactory(new GitCommandFactory(maxThread,rootPath,gitPersmionHelper));
        sshd.setShellFactory(null);
        sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider(Paths.get("hostkey.pem")));
        CoreModuleProperties.NIO2_READ_TIMEOUT.set(sshd, READ_TIMEOUT);
        return sshd;
    }

    // Adds deprecated / insecure settings




    @SuppressWarnings("deprecation")
    public static SshServer setupFullSignaturesSupport(SshServer server) {
        List<NamedFactory<Signature>> signatures = Stream.of( //
                        BuiltinSignatures.nistp256_cert, //
                        BuiltinSignatures.nistp384_cert, //
                        BuiltinSignatures.nistp521_cert, //
                        BuiltinSignatures.ed25519_cert, //
                        BuiltinSignatures.rsaSHA512_cert, //
                        BuiltinSignatures.rsaSHA256_cert, //
                        BuiltinSignatures.rsa_cert, //
                        BuiltinSignatures.nistp256, //
                        BuiltinSignatures.nistp384, //
                        BuiltinSignatures.nistp521, //
                        BuiltinSignatures.ed25519, //
                        BuiltinSignatures.rsaSHA512, //
                        BuiltinSignatures.rsaSHA256, //
                        BuiltinSignatures.rsa, //
                        BuiltinSignatures.dsa_cert, //
                        BuiltinSignatures.dsa) //
                .filter(BuiltinSignatures::isSupported) //
                .collect(Collectors.toList());
        server.setSignatureFactories(signatures);
        return server;
    }

    @SuppressWarnings("deprecation")
    public static SshClient setupFullSignaturesSupport(SshClient client) {
        List<NamedFactory<Signature>> signatures = Stream.of( //
                        BuiltinSignatures.nistp256_cert, //
                        BuiltinSignatures.nistp384_cert, //
                        BuiltinSignatures.nistp521_cert, //
                        BuiltinSignatures.ed25519_cert, //
                        BuiltinSignatures.rsaSHA512_cert, //
                        BuiltinSignatures.rsaSHA256_cert, //
                        BuiltinSignatures.rsa_cert, //
                        BuiltinSignatures.nistp256, //
                        BuiltinSignatures.nistp384, //
                        BuiltinSignatures.nistp521, //
                        BuiltinSignatures.ed25519, //
                        BuiltinSignatures.sk_ecdsa_sha2_nistp256, //
                        BuiltinSignatures.sk_ssh_ed25519, //
                        BuiltinSignatures.rsaSHA512, //
                        BuiltinSignatures.rsaSHA256, //
                        BuiltinSignatures.rsa, //
                        BuiltinSignatures.dsa_cert, //
                        BuiltinSignatures.dsa) //
                .filter(BuiltinSignatures::isSupported) //
                .collect(Collectors.toList());
        client.setSignatureFactories(signatures);
        return client;
    }

    public static Duration getTimeout(String property, Duration defaultValue) {
        // Do we have a specific timeout value ?
        String str = System.getProperty("org.apache.sshd.test.timeout." + property);
        if (GenericUtils.isNotEmpty(str)) {
            return Duration.ofMillis(Long.parseLong(str));
        }

        // Do we have a specific factor ?
        str = System.getProperty("org.apache.sshd.test.timeout.factor." + property);
        if (GenericUtils.isEmpty(str)) {
            // Do we have a global factor ?
            str = System.getProperty("org.apache.sshd.test.timeout.factor");
        }

        if (GenericUtils.isNotEmpty(str)) {
            double factor = Double.parseDouble(str);
            long dur = Math.round(defaultValue.toMillis() * factor);
            return Duration.ofMillis(dur);
        }

        return defaultValue;
    }
}
