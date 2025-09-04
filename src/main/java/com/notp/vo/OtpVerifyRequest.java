package com.notp.vo;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * OTP验证请求VO
 *
 * @author sign
 */
@Data
public class OtpVerifyRequest {

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * OTP代码
     */
    @NotBlank(message = "验证码不能为空")
    private String code;
}
