package com.solosw.codelab.config.server;

import com.alibaba.fastjson.JSON;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AESGCMEncryptionWithCustomKey {

    private static final int TAG_LENGTH_BIT = 128; // GCM标签长度
    private static final int IV_LENGTH_BYTE = 12; // GCM IV长度

    public static String createEncodeSecret() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256); // 可以选择128, 192或256位
        SecretKey secretKey = keyGen.generateKey();
       return Base64.getUrlEncoder().withoutPadding().encodeToString(secretKey.getEncoded());
    }
    public static SecretKeySpec createKeySpec(String key) {
        byte[] decodedKey = Base64.getUrlDecoder().decode(key);
        return new SecretKeySpec(decodedKey, "AES");
    }

    public static String encrypt(String data, SecretKeySpec secretKeySpec) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        byte[] iv = new byte[IV_LENGTH_BYTE];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);

        GCMParameterSpec parameterSpec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, parameterSpec);

        byte[] encryptedData = cipher.doFinal(data.getBytes());
        byte[] encryptedDataWithIv = new byte[iv.length + encryptedData.length];
        System.arraycopy(iv, 0, encryptedDataWithIv, 0, iv.length);
        System.arraycopy(encryptedData, 0, encryptedDataWithIv, iv.length, encryptedData.length);

        // 使用URL-safe Base64编码
        return Base64.getUrlEncoder().withoutPadding().encodeToString(encryptedDataWithIv);
    }

    public static String decrypt(String encryptedData, SecretKeySpec secretKeySpec) throws Exception {
        byte[] decodedData = Base64.getUrlDecoder().decode(encryptedData);
        byte[] iv = new byte[IV_LENGTH_BYTE];
        System.arraycopy(decodedData, 0, iv, 0, iv.length);

        GCMParameterSpec parameterSpec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, parameterSpec);

        byte[] cipherText = new byte[decodedData.length - IV_LENGTH_BYTE];
        System.arraycopy(decodedData, IV_LENGTH_BYTE, cipherText, 0, cipherText.length);

        return new String(cipher.doFinal(cipherText));
    }

    public static void main(String[] args) throws Exception {
        String ss="GPxXgWCdPqGe0AUg3bKsMM7ZLp9UfGBIKpSZl_UMnAo";
        Map<String, Long> mp=new HashMap<>();
        mp.put("userId",1L);
        mp.put("houseId",3L);
        SecretKeySpec secretKeySpec = createKeySpec(ss);

        String originalText = JSON.toJSONString(mp);
       // String encryptedText = encrypt(originalText, secretKeySpec);
        String decryptedText = decrypt("ovcv99XWbZVn9r_ADUdvoXp0Ci4yVHwXz4tw5Ek3SYoZkEdk-HkOqYQQGgvzFen-2fTIhA", secretKeySpec);

        System.out.println("Original Text: " + originalText);
      //  System.out.println("Encrypted Text: " + encryptedText);
        System.out.println("Decrypted Text: " + decryptedText);
    }
}
