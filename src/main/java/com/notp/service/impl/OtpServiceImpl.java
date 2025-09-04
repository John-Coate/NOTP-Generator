package com.notp.service.impl;

import com.notp.constant.OtpErrorCode;
import com.notp.entity.SysUserOtp;
import com.notp.mapper.SysUserOtpMapper;
import com.notp.service.OtpService;
import com.notp.util.TotpUtil;
import com.notp.util.QrCodeUtil;
import com.notp.vo.OtpResponse;
import com.notp.vo.OtpInfoVo;
import com.notp.vo.OtpVerifyResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * OTP双因素认证服务实现类
 *
 * @author sign
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

    private final SysUserOtpMapper userOtpMapper;

    @Override
    @Transactional
    public OtpResponse enableOtp(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }

        // 检查是否已启用
        SysUserOtp existing = userOtpMapper.selectByUserId(userId);
        if (existing != null && Boolean.TRUE.equals(existing.getEnabled())) {
            throw new RuntimeException("用户已启用OTP验证");
        }

        // 生成密钥
        String secret = TotpUtil.generateSecret();

        // 保存到数据库
        SysUserOtp userOtp = new SysUserOtp();
        userOtp.setUserId(userId);
        userOtp.setSecret(secret);
        userOtp.setEnabled(true);
        userOtp.setCreateTime(new Date());
        userOtp.setUpdateTime(new Date());

        if (existing == null) {
            userOtpMapper.insert(userOtp);
        } else {
            userOtp.setId(existing.getId());
            userOtpMapper.updateById(userOtp);
        }

        // 生成响应
        OtpResponse response = new OtpResponse();
        response.setSecret(secret);
        response.setQrCodeUrl(generateOtpUrl(secret, String.valueOf(userId), "NOTP-System"));
        response.setEnabled(true);
        response.setErrorCode(OtpErrorCode.SUCCESS.getCode());
        response.setErrorMessage(OtpErrorCode.SUCCESS.getMessage());

        return response;
    }

    @Override
    @Transactional
    public boolean enableOtp(Long userId, String secret, String code) {
        if (userId == null || secret == null || code == null) {
            throw new IllegalArgumentException("参数不能为空");
        }

        // 验证验证码
        if (!TotpUtil.verifyCode(code, secret)) {
            return false;
        }

        // 检查是否已启用
        SysUserOtp existing = userOtpMapper.selectByUserId(userId);
        if (existing != null && Boolean.TRUE.equals(existing.getEnabled())) {
            return true; // 已经启用
        }

        // 保存到数据库
        SysUserOtp userOtp = new SysUserOtp();
        userOtp.setUserId(userId);
        userOtp.setSecret(secret);
        userOtp.setEnabled(true);
        userOtp.setCreateTime(new Date());
        userOtp.setUpdateTime(new Date());

        if (existing == null) {
            return userOtpMapper.insert(userOtp) > 0;
        } else {
            userOtp.setId(existing.getId());
            return userOtpMapper.updateById(userOtp) > 0;
        }
    }

    @Override
    @Transactional
    public boolean disableOtp(Long userId, String code) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }

        SysUserOtp userOtp = userOtpMapper.selectByUserId(userId);
        if (userOtp == null) {
            throw new RuntimeException("用户未配置OTP");
        }

        // 如果提供了验证码，则验证验证码
        if (code != null && !code.trim().isEmpty()) {
            if (!TotpUtil.verifyCode(code, userOtp.getSecret())) {
                throw new RuntimeException("OTP验证码错误");
            }
        }

        userOtp.setEnabled(false);
        userOtp.setUpdateTime(new Date());
        int result = userOtpMapper.updateById(userOtp);

        return result > 0;
    }

    @Override
    public OtpVerifyResult verifyOtp(Long userId, String otpCode) {
        if (userId == null || otpCode == null) {
            return OtpVerifyResult.fail(OtpErrorCode.PARAMETER_INVALID);
        }

        SysUserOtp userOtp = userOtpMapper.selectByUserId(userId);
        if (userOtp == null) {
            return OtpVerifyResult.fail(OtpErrorCode.NOT_CONFIGURED);
        }

        if (!Boolean.TRUE.equals(userOtp.getEnabled())) {
            return OtpVerifyResult.fail(OtpErrorCode.DISABLED);
        }

        if (TotpUtil.verifyCode(otpCode, userOtp.getSecret())) {
            return OtpVerifyResult.success("OTP验证成功");
        } else {
            return OtpVerifyResult.fail(OtpErrorCode.INVALID_CODE);
        }
    }

    @Override
    public byte[] generateQrCode(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }

        SysUserOtp userOtp = userOtpMapper.selectByUserId(userId);
        if (userOtp == null || !Boolean.TRUE.equals(userOtp.getEnabled())) {
            throw new RuntimeException("用户未启用OTP验证");
        }

        String qrCodeUrl = generateOtpUrl(userOtp.getSecret(), String.valueOf(userId), "NOTP-System");

        try {
            return QrCodeUtil.generateQRCodeImage(qrCodeUrl, 400, 400);
        } catch (Exception e) {
            throw new RuntimeException("生成二维码失败", e);
        }
    }

    @Override
    public Map<String, Object> generateQRCodeData(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }

        // 检查是否已启用OTP
        SysUserOtp userOtp = userOtpMapper.selectByUserId(userId);
        if (userOtp == null || !Boolean.TRUE.equals(userOtp.getEnabled())) {
            // 如果未启用，先生成并启用OTP
            OtpResponse response = enableOtp(userId);

            Map<String, Object> result = new HashMap<>();
            result.put("userId", userId);
            result.put("username", "user" + userId);
            result.put("secret", response.getSecret());
            result.put("qrCodeUrl", response.getQrCodeUrl());
            result.put("enabled", true);
            result.put("qrCodeData", response.getQrCodeUrl());

            return result;
        }

        // 已启用，直接返回二维码数据
        String qrCodeUrl = generateOtpUrl(userOtp.getSecret(), String.valueOf(userId), "NOTP-System");

        Map<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("username", "user" + userId);
        result.put("secret", userOtp.getSecret());
        result.put("qrCodeUrl", qrCodeUrl);
        result.put("enabled", true);
        result.put("qrCodeData", qrCodeUrl);

        return result;
    }

    @Override
    public boolean isOtpEnabled(Long userId) {
        if (userId == null) {
            return false;
        }

        SysUserOtp userOtp = userOtpMapper.selectByUserId(userId);
        return userOtp != null && Boolean.TRUE.equals(userOtp.getEnabled());
    }

    @Override
    public OtpInfoVo getOtpInfo(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }

        SysUserOtp userOtp = userOtpMapper.selectByUserId(userId);
        if (userOtp == null) {
            return null;
        }

        OtpInfoVo info = new OtpInfoVo();
        info.setUserId(userId);
        info.setEnabled(userOtp.getEnabled());
        info.setCreateTime(userOtp.getCreateTime());
        info.setUpdateTime(userOtp.getUpdateTime());

        if (Boolean.TRUE.equals(userOtp.getEnabled())) {
            info.setQrCodeUrl(generateOtpUrl(userOtp.getSecret(), String.valueOf(userId), "NOTP-System"));
        }

        return info;
    }

    /**
     * 生成OTP URL
     * @param secret 密钥
     * @param account 账户名
     * @param issuer 颁发者
     * @return OTP URL
     */
    private String generateOtpUrl(String secret, String account, String issuer) {
        try {
            String encodedAccount = URLEncoder.encode(account, "UTF-8");
            String encodedIssuer = URLEncoder.encode(issuer, "UTF-8");

            return String.format("otpauth://totp/%s:%s?secret=%s&issuer=%s&algorithm=SHA512&digits=6&period=30",
                    encodedIssuer, encodedAccount, secret, encodedIssuer);
        } catch (Exception e) {
            throw new RuntimeException("生成OTP URL失败", e);
        }
    }
}
