package com.notp.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * 二维码工具类
 *
 * @author sign
 */
public class QrCodeUtil {

    private static final int QR_CODE_SIZE = 300; // 二维码尺寸
    private static final String QR_CODE_FORMAT = "PNG"; // 二维码格式

    /**
     * 生成二维码图片
     */
    public static byte[] generateQrCode(String text) {
        return generateQRCodeImage(text, QR_CODE_SIZE, QR_CODE_SIZE);
    }

    /**
     * 生成Base64格式的二维码
     */
    public static String generateQrCodeBase64(String text) {
        byte[] qrCodeBytes = generateQrCode(text);
        return "data:image/png;base64," + Base64.getEncoder().encodeToString(qrCodeBytes);
    }

    /**
     * 生成OTP绑定URL
     */
    public static String generateOtpUrl(String username, String issuer, String secret) {
        return String.format("otpauth://totp/%s:%s?secret=%s&issuer=%s",
                issuer, username, secret, issuer);
    }

    /**
     * 生成OTP二维码
     */
    public static String generateOtpQrCode(String username, String issuer, String secret) {
        String otpUrl = generateOtpUrl(username, issuer, secret);
        return generateQrCodeBase64(otpUrl);
    }

    /**
     * 生成指定尺寸的二维码图片
     * @param text 二维码内容
     * @param width 宽度
     * @param height 高度
     * @return 二维码字节数组
     */
    public static byte[] generateQRCodeImage(String text, int width, int height) {
        try {
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
            hints.put(EncodeHintType.MARGIN, 2); // 增加边距
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height, hints);

            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
            return pngOutputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("生成二维码失败", e);
        }
    }

    /**
     * 生成指定尺寸的Base64二维码
     *
     * @param text 二维码内容
     * @param width 二维码宽度
     * @param height 二维码高度
     * @return Base64编码的二维码图片
     */
    public static String generateQRCodeImageBase64(String text, int width, int height) {
        try {
            byte[] qrCodeBytes = generateQRCodeImage(text, width, height);
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(qrCodeBytes);
        } catch (Exception e) {
            throw new RuntimeException("生成Base64二维码失败", e);
        }
    }
}
