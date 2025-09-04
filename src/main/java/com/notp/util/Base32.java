package com.notp.util;

/**
 * Base32编码解码工具类
 * 基于RFC4648标准实现
 *
 * @author sign
 */
public class Base32 {

    private static final String BASE32_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";
    private static final int[] BASE32_LOOKUP = {
        0xFF, 0xFF, 0x1A, 0x1B, 0x1C, 0x1D, 0x1E, 0x1F, // '0', '1', '2', '3', '4', '5', '6', '7'
        0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, // '8', '9', ':', ';', '<', '=', '>', '?'
        0xFF, 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, // '@', 'A', 'B', 'C', 'D', 'E', 'F', 'G'
        0x07, 0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, // 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O'
        0x0F, 0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16, // 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W'
        0x17, 0x18, 0x19, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, // 'X', 'Y', 'Z', '[', '\\', ']', '^', '_'
        0xFF, 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, // '`', 'a', 'b', 'c', 'd', 'e', 'f', 'g'
        0x07, 0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, // 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o'
        0x0F, 0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16, // 'p', 'q', 'r', 's', 't', 'u', 'v', 'w'
        0x17, 0x18, 0x19  // 'x', 'y', 'z'
    };

    /**
     * 编码字节数组为Base32字符串
     *
     * @param bytes 要编码的字节数组
     * @return Base32编码的字符串
     */
    public static String encode(byte[] bytes) {
        if (bytes == null) {
            return "";
        }

        StringBuilder result = new StringBuilder();
        int buffer = 0;
        int bitsLeft = 0;

        for (byte b : bytes) {
            buffer <<= 8;
            buffer |= (b & 0xFF);
            bitsLeft += 8;

            while (bitsLeft >= 5) {
                result.append(BASE32_CHARS.charAt((buffer >> (bitsLeft - 5)) & 0x1F));
                bitsLeft -= 5;
            }
        }

        if (bitsLeft > 0) {
            result.append(BASE32_CHARS.charAt((buffer << (5 - bitsLeft)) & 0x1F));
        }

        // 添加填充
        while (result.length() % 8 != 0) {
            result.append('=');
        }

        return result.toString();
    }

    /**
     * 解码Base32字符串为字节数组
     *
     * @param base32 Base32编码的字符串
     * @return 解码后的字节数组
     */
    public static byte[] decode(String base32) {
        if (base32 == null || base32.isEmpty()) {
            return new byte[0];
        }

        base32 = base32.trim().replace("=", "").toUpperCase();
        if (base32.isEmpty()) {
            return new byte[0];
        }

        byte[] bytes = new byte[base32.length() * 5 / 8];
        int buffer = 0;
        int bitsLeft = 0;
        int count = 0;

        for (char c : base32.toCharArray()) {
            int value = c < 128 ? BASE32_LOOKUP[c] : 0xFF;
            if (value == 0xFF) {
                continue;
            }

            buffer <<= 5;
            buffer |= value;
            bitsLeft += 5;

            if (bitsLeft >= 8) {
                bytes[count++] = (byte) ((buffer >> (bitsLeft - 8)) & 0xFF);
                bitsLeft -= 8;
            }
        }

        return bytes;
    }
}
