package com.solosw.codelab;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
import org.apache.sshd.git.pack.GitPackCommand;
import org.apache.sshd.git.pack.GitPackCommandFactory;
import org.apache.sshd.server.ServerBuilder;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.auth.pubkey.AcceptAllPublickeyAuthenticator;
import org.apache.sshd.server.session.ServerSession;
import org.apache.sshd.server.shell.ProcessShellFactory;
import org.apache.sshd.server.shell.UnknownCommandFactory;
import org.apache.sshd.sftp.server.SftpSubsystemFactory;

public final class CoreTestSupportUtils {
    public static final Duration READ_TIMEOUT = getTimeout("read.nio2", Duration.ofSeconds(60));

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

    public static SshClient setupTestClient(Class<?> anchor) {
        return setupTestClient(SshClient.setUpDefaultClient(), anchor);
    }

    public static <C extends SshClient> C setupTestClient(C client, Class<?> anchor) {
        client.setServerKeyVerifier(AcceptAllServerKeyVerifier.INSTANCE);
        client.setHostConfigEntryResolver(HostConfigEntryResolver.EMPTY);
        client.setKeyIdentityProvider(KeyIdentityProvider.EMPTY_KEYS_PROVIDER);
        CoreModuleProperties.NIO2_READ_TIMEOUT.set(client, READ_TIMEOUT);
        return client;
    }

    public static SshClient setupTestFullSupportClient(Class<?> anchor) {
        SshClient client = setupTestClient(anchor);
        return setupTestFullSupportClient(client);
    }

    public static SshClient setupTestFullSupportClient(SshClient client) {
        client.setKeyExchangeFactories(
                NamedFactory.setUpTransformedFactories(false, BuiltinDHFactories.VALUES, ClientBuilder.DH2KEX));
        setupFullSignaturesSupport(client);
        return client;
    }

    public static SshServer setupTestServer(Class<?> anchor) {
        return setupTestServer(SshServer.setUpDefaultServer(), anchor);
    }
    public static class MyGitLocationResolver implements GitLocationResolver {





        @Override
        public Path resolveRootDirectory(String s, String[] strings, ServerSession serverSession, FileSystem fileSystem) throws IOException {
            System.out.println("------------------------------------------------------------------------");
            System.out.println(s);
            System.out.println(Arrays.toString(strings));
            System.out.println(serverSession);
            System.out.println(fileSystem);
            System.out.println("------------------------------------------------------------------------");

            return Paths.get("C:\\Users\\solosw\\Desktop\\CodeLab\\repositories\\siki\\happy.git");
        }
    }


    public static <S extends SshServer> S setupTestServer(S sshd, Class<?> anchor) {
        List<NamedFactory<Cipher>> cipherFactories = new ArrayList<>(sshd.getCipherFactories());
        cipherFactories.add(BuiltinCiphers.aes128cbc);
        sshd.setCipherFactories(cipherFactories);
        sshd.setPublickeyAuthenticator(new MyPublickeyAuthenticator());
        sshd.setSubsystemFactories(Collections.singletonList(new SftpSubsystemFactory()));
        sshd.setCommandFactory(new GitCommandFactory());
        sshd.setShellFactory(null);
        CoreModuleProperties.NIO2_READ_TIMEOUT.set(sshd, READ_TIMEOUT);
        return sshd;
    }

    // Adds deprecated / insecure settings
    public static SshServer setupTestFullSupportServer(Class<?> anchor) {
        SshServer sshd = setupTestServer(anchor);
        return setupTestFullSupportServer(sshd);
    }

    public static SshServer setupTestFullSupportServer(SshServer sshd) {
        sshd.setKeyExchangeFactories(
                NamedFactory.setUpTransformedFactories(false, BuiltinDHFactories.VALUES, ServerBuilder.DH2KEX));
        setupFullSignaturesSupport(sshd);
        return sshd;
    }

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
