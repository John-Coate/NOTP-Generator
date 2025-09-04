package com.notp.util;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * TOTP工具类
 * 基于RFC6238标准实现
 *
 * @author sign
 */
@Slf4j
public class TotpUtil {

    private static final int TIME_STEP = 30; // 30秒时间步长
    private static final int CODE_DIGITS = 6; // 6位验证码
    private static final String HMAC_ALGORITHM = "HmacSHA512";

    /**
     * 生成随机密钥
     * @return Base32编码的密钥（64字节，104字符Base32编码）
     */
    public static String generateSecret() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[64]; // SHA-512推荐使用64字节密钥
        random.nextBytes(bytes);
        return Base32.encode(bytes).replace("=", "");
    }

    /**
     * 生成当前时间戳对应的验证码
     */
    public static String generateCode(String secret) {
        if (secret == null || secret.trim().isEmpty()) {
            throw new IllegalArgumentException("密钥不能为空");
        }
        long timeCounter = System.currentTimeMillis() / 1000 / TIME_STEP;
        return generateCode(secret, timeCounter);
    }

    /**
     * 根据时间戳生成验证码
     */
    public static String generateCode(String secret, long timeCounter) {
        try {
            // 解码Base32密钥
            String base32Secret = secret.replaceAll("[^A-Z2-7]", "");
            byte[] decodedKey = base32Decode(base32Secret);

            byte[] timeBytes = ByteBuffer.allocate(8).putLong(timeCounter).array();

            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(decodedKey, HMAC_ALGORITHM));
            byte[] hash = mac.doFinal(timeBytes);

            int offset = hash[hash.length - 1] & 0xf;
            int binary = ((hash[offset] & 0x7f) << 24) |
                        ((hash[offset + 1] & 0xff) << 16) |
                        ((hash[offset + 2] & 0xff) << 8) |
                        (hash[offset + 3] & 0xff);

            int otp = binary % (int) Math.pow(10, CODE_DIGITS);
            return String.format("%0" + CODE_DIGITS + "d", otp);

        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("生成TOTP验证码失败", e);
        }
    }

    /**
     * Base32解码
     */
    private static byte[] base32Decode(String input) {
        String base32Chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";
        input = input.toUpperCase().replace("=", "");

        byte[] result = new byte[input.length() * 5 / 8];
        int buffer = 0;
        int bitsLeft = 0;
        int count = 0;

        for (char c : input.toCharArray()) {
            int value = base32Chars.indexOf(c);
            if (value < 0) continue;

            buffer <<= 5;
            buffer |= value;
            bitsLeft += 5;

            if (bitsLeft >= 8) {
                result[count++] = (byte) ((buffer >> (bitsLeft - 8)) & 0xFF);
                bitsLeft -= 8;
            }
        }

        return result;
    }

    /**
     * 获取当前验证码剩余有效时间（秒）
     */
    public static int getRemainingSeconds() {
        long currentTime = System.currentTimeMillis() / 1000;
        return TIME_STEP - (int) (currentTime % TIME_STEP);
    }

    /**
     * 验证OTP代码（带时间窗口验证）
     */
    public static boolean verifyCode(String code, String secret) {
        if (code == null || code.length() != CODE_DIGITS || secret == null || secret.trim().isEmpty()) {
            return false;
        }

        if (!code.matches("\\d{6}")) {
            return false;
        }

        try {
            long timeCounter = System.currentTimeMillis() / 1000 / TIME_STEP;

            // 检查当前及前后两个时间窗口（共3个窗口）
            for (int i = -1; i <= 1; i++) {
                String candidate = generateCode(secret, timeCounter + i);
                if (code.equals(candidate)) {
                    return true;
                }
            }

            log.debug("OTP验证失败: code={}, secret={}", code, secret);
            return false;
        } catch (Exception e) {
            log.error("OTP验证异常", e);
            return false;
        }
    }
}
