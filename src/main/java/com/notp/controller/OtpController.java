package com.notp.controller;

import com.notp.common.core.domain.AjaxResult;
import com.notp.service.OtpService;
import com.notp.vo.OtpVerifyResult;
import com.notp.vo.OtpInfoVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

/**
 * OTP双因素认证控制器
 *
 * @author sign
 * @version 2.0.0
 */
@Tag(name = "OTP管理", description = "双因素认证相关接口")
@RestController
@RequestMapping("/otp")
@RequiredArgsConstructor
@Validated
public class OtpController {

    private final OtpService otpService;

    /**
     * 生成OTP二维码
     */
    @Operation(summary = "生成OTP二维码", description = "为用户生成OTP双因素认证的二维码和密钥")
    @GetMapping("/qr-code")
    public AjaxResult generateQRCode(@Parameter(description = "用户ID", required = true)
                                     @RequestParam @NotNull Long userId) {
        try {
            if (userId == null || userId <= 0) {
                return AjaxResult.error("用户ID不能为空且必须大于0");
            }

            Map<String, Object> qrData = otpService.generateQRCodeData(userId);

            return AjaxResult.success(qrData);

        } catch (IllegalArgumentException e) {
            return AjaxResult.error("参数错误: " + e.getMessage());
        } catch (Exception e) {
            return AjaxResult.error("生成二维码失败: " + e.getMessage());
        }
    }

    /**
     * 启用OTP验证
     */
    @Operation(summary = "启用OTP", description = "为用户启用OTP双因素认证")
    @PostMapping("/enable")
    public AjaxResult enableOtp(@Parameter(description = "用户ID", required = true)
                               @RequestParam @NotNull Long userId,
                               @Parameter(description = "OTP密钥", required = true)
                               @RequestParam String secret,
                               @Parameter(description = "6位验证码", required = true)
                               @RequestParam String code) {
        try {
            if (userId == null || userId <= 0) {
                return AjaxResult.error("用户ID不能为空且必须大于0");
            }

            boolean result = otpService.enableOtp(userId, secret, code);

            if (result) {
                Map<String, Object> data = new HashMap<>();
                data.put("success", true);
                data.put("enabled", true);
                data.put("message", "OTP启用成功");
                data.put("userId", userId);
                data.put("timestamp", System.currentTimeMillis());

                return AjaxResult.success(data);
            } else {
                return AjaxResult.error("验证码错误: 请输入正确的6位验证码");
            }

        } catch (IllegalArgumentException e) {
            return AjaxResult.error("参数错误: " + e.getMessage());
        } catch (Exception e) {
            return AjaxResult.error("启用OTP失败: " + e.getMessage());
        }
    }

    /**
     * 禁用OTP验证
     */
    @Operation(summary = "禁用OTP", description = "为用户禁用OTP双因素认证")
    @PostMapping("/disable")
    public AjaxResult disableOtp(@Parameter(description = "用户ID", required = true)
                                @RequestParam @NotNull Long userId,
                                @Parameter(description = "6位验证码（可选）")
                                @RequestParam(required = false) String code) {
        try {
            if (userId == null || userId <= 0) {
                return AjaxResult.error("用户ID不能为空且必须大于0");
            }

            boolean disabled = otpService.disableOtp(userId, code);

            Map<String, Object> result = new HashMap<>();
            result.put("disabled", disabled);
            result.put("userId", userId);
            result.put("username", "user" + userId); // 这里应该从用户服务获取真实用户名

            return AjaxResult.success("OTP验证已禁用", result);

        } catch (IllegalArgumentException e) {
            return AjaxResult.error("参数错误: " + e.getMessage());
        } catch (Exception e) {
            return AjaxResult.error("禁用OTP失败: " + e.getMessage());
        }
    }

    /**
     * 验证OTP验证码
     */
    @Operation(summary = "验证OTP", description = "验证用户输入的OTP验证码")
    @PostMapping("/verify")
    public AjaxResult verifyOtp(@Parameter(description = "用户ID", required = true)
                             @RequestParam @NotNull Long userId,
                             @Parameter(description = "6位验证码", required = true)
                             @RequestParam String code) {
        try {
            if (userId == null || userId <= 0) {
                return AjaxResult.error("用户ID不能为空且必须大于0");
            }

            OtpVerifyResult result = otpService.verifyOtp(userId, code);

            Map<String, Object> data = new HashMap<>();
            data.put("valid", result.isSuccess());
            data.put("userId", userId);
            data.put("timestamp", System.currentTimeMillis());
            data.put("code", result.getCode());
            data.put("message", result.getMessage());

            return AjaxResult.success(data);

        } catch (IllegalArgumentException e) {
            return AjaxResult.error("参数错误: " + e.getMessage());
        } catch (Exception e) {
            return AjaxResult.error("验证OTP失败: " + e.getMessage());
        }
    }

    /**
     * 获取OTP状态
     */
    @Operation(summary = "获取OTP状态", description = "获取用户当前的OTP启用状态")
    @GetMapping("/status")
    public AjaxResult getOtpStatus(@Parameter(description = "用户ID", required = true)
                                  @RequestParam @NotNull Long userId) {
        try {
            if (userId == null || userId <= 0) {
                return AjaxResult.error("用户ID不能为空且必须大于0");
            }

            boolean enabled = otpService.isOtpEnabled(userId);

            Map<String, Object> data = new HashMap<>();
            data.put("enabled", enabled);
            data.put("userId", userId);
            data.put("timestamp", System.currentTimeMillis());

            return AjaxResult.success(data);

        } catch (IllegalArgumentException e) {
            return AjaxResult.error("参数错误: " + e.getMessage());
        } catch (Exception e) {
            return AjaxResult.error("获取OTP状态失败: " + e.getMessage());
        }
    }

    /**
     * 获取OTP信息
     */
    @Operation(summary = "获取OTP信息", description = "获取用户的OTP详细信息")
    @GetMapping("/info")
    public AjaxResult getOtpInfo(@Parameter(description = "用户ID", required = true)
                                @RequestParam @NotNull Long userId) {
        try {
            if (userId == null || userId <= 0) {
                return AjaxResult.error("用户ID不能为空且必须大于0");
            }

            OtpInfoVo info = otpService.getOtpInfo(userId);

            return AjaxResult.success(info);

        } catch (IllegalArgumentException e) {
            return AjaxResult.error("参数错误: " + e.getMessage());
        } catch (Exception e) {
            return AjaxResult.error("获取OTP信息失败: " + e.getMessage());
        }
    }
}
