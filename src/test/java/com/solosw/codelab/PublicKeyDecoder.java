package com.solosw.codelab;

import org.apache.sshd.common.config.keys.PublicKeyEntryDataResolver;
import org.apache.sshd.common.config.keys.impl.RSAPublicKeyDecoder;
import org.apache.sshd.common.keyprovider.FileKeyPairProvider;
import org.apache.sshd.common.util.security.SecurityUtils;
import org.apache.sshd.common.config.keys.PublicKeyEntry;
import org.apache.sshd.common.config.keys.KeyUtils;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;

import java.security.PublicKey;
import java.io.IOException;
import java.util.Base64;

public class PublicKeyDecoder {

        public static PublicKey decodeSSHPublicKey(String sshPublicKey) throws Exception {
            // SSH格式的公钥通常以 "ssh-rsa AAAAB3... your_email@example.com" 开始
            String[] parts = sshPublicKey.split(" ");

            if (parts.length < 2) {
                throw new IllegalArgumentException("Invalid SSH public key format");
            }

            // 提取Base64编码的公钥部分
            String base64Key = parts[1];

            // 解码Base64编码的公钥
            byte[] encodedKey = Base64.getDecoder().decode(base64Key);

            // 去掉开头的 "ssh-rsa" 标识符
            if (!parts[0].equals("ssh-rsa")) {
                throw new IllegalArgumentException("Unsupported key type: " + parts[0]);
            }

            // 使用Bouncy Castle库将SSH格式的公钥转换为SubjectPublicKeyInfo
            SubjectPublicKeyInfo spki = SubjectPublicKeyInfo.getInstance(encodedKey);

            // 使用JcaPEMKeyConverter将SubjectPublicKeyInfo转换为Java的PublicKey对象
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider("BC");
            PublicKey publicKey = converter.getPublicKey(spki);

            return publicKey;
        }

        public static void main(String[] args) {
            String sshPublicKey = "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABgQDM/8PvG+u29CaMioWvYz+ziQcft6YSZ7m2qJCaEZKTACiRpdRNbeEiwPeosbtHmU6Wg6rBcuotGGwQlQh9oUwvOrdzpPI6QO9H7yGxM4Lnk1GEw6Uci9AcSg0DJGBL+X3X82xHlCQm72Jbv557T/nTBfgRQuxUVZQnhV78hNunICPoTXJCfyk+TVU68L+Eqr6p7HJMuBOwxq3XPb1zfSam6z0HFK3Em2EgNP+kQPP+8J+7zsuaKskWr17f5Pl5wsXSsB0TSkJNYoUMKXcJvsDCwdu3HhWtW14VcUd0p7nlDwwL0CkqIpAZ/lujVzl4cmZv01QQR1Awa3xHITEgaePnDFt3CYEAFx3XqRy29EA9DWfEC4+4+EQnBfieYsOws2Pn30Q/Gi4Pt7Uwf26PKq9z/pbp+fkXtWBS/MhvZjiLOuvXP0Y6Cu1d+qsdtzGlddnxgrC3qjCtErlIGAlZWwCDRoEuLx6dCXpFrdCAy/bGCjWUeSjjjYNE+QMpyxzjHrk= solosw@LAPTOP-4O5L4UP6"; // 示例公钥

            try {
                PublicKey publicKey = decodeSSHPublicKey(sshPublicKey);
                System.out.println("Public Key Algorithm: " + publicKey.getAlgorithm());
                System.out.println("Public Key Format: " + publicKey.getFormat());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
}

