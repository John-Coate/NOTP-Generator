package com.notp.service;

import com.notp.vo.OtpVerifyResult;
import com.notp.vo.OtpResponse;
import com.notp.vo.OtpInfoVo;
import java.util.Map;

/**
 * OTP双因素认证服务接口
 *
 * @author sign
 */
public interface OtpService {

    /**
     * 启用OTP验证
     *
     * @param userId 用户ID
     * @return 启用结果
     */
    OtpResponse enableOtp(Long userId);

    /**
     * 启用OTP验证（带验证码验证）
     *
     * @param userId 用户ID
     * @param secret OTP密钥
     * @param code 6位验证码
     * @return 是否启用成功
     */
    boolean enableOtp(Long userId, String secret, String code);

    /**
     * 禁用OTP验证
     *
     * @param userId 用户ID
     * @param code 6位验证码（可选验证）
     * @return 是否禁用成功
     */
    boolean disableOtp(Long userId, String code);

    /**
     * 验证OTP验证码
     *
     * @param userId 用户ID
     * @param otpCode OTP验证码
     * @return 验证结果
     */
    OtpVerifyResult verifyOtp(Long userId, String otpCode);

    /**
     * 生成OTP二维码
     *
     * @param userId 用户ID
     * @return 二维码字节数组
     */
    byte[] generateQrCode(Long userId);

    /**
     * 生成OTP二维码数据
     *
     * @param userId 用户ID
     * @return 二维码相关数据
     */
    Map<String, Object> generateQRCodeData(Long userId);

    /**
     * 检查OTP是否启用
     *
     * @param userId 用户ID
     * @return 是否启用
     */
    boolean isOtpEnabled(Long userId);

    /**
     * 获取OTP信息
     *
     * @param userId 用户ID
     * @return OTP信息
     */
    OtpInfoVo getOtpInfo(Long userId);
}
