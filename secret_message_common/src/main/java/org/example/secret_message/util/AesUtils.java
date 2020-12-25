package org.example.secret_message.util;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author weicheng.zhao
 * @date 2020/12/24
 */
@Slf4j
public final class AesUtils {

    private static final byte[] DEFAULT_RESULT = new byte[0];

    private AesUtils() {
    }

    public static byte[] encrypt(byte[] origin, byte[] key) {
        try {
            return generateCipher(Cipher.ENCRYPT_MODE, key).doFinal(origin);
        } catch (Exception e) {
            log.error("对称加解密 加密异常 {} {}", origin, key, e);
            return DEFAULT_RESULT;
        }
    }

    public static byte[] decrypt(byte[] encrypted, byte[] key) {
        try {
            return generateCipher(Cipher.DECRYPT_MODE, key).doFinal(encrypted);
        } catch (Exception e) {
            log.error("对称加解密 解密异常 {} {}", encrypted, key, e);
            return DEFAULT_RESULT;
        }
    }

    private static Cipher generateCipher(int mode, byte[] key) throws Exception {
        Cipher instance = Cipher.getInstance("AES");
        instance.init(mode, new SecretKeySpec(key, "AES"));
        return instance;
    }
}
