package com.notp.common.constant;

/**
 * OTP错误码枚举
 */
public enum OtpErrorCode {
    NOT_CONFIGURED("OTP_NOT_CONFIGURED", "双重认证未配置，请先初始化OTP安全设置"),
    DISABLED("OTP_DISABLED", "双重认证功能已停用，请前往账户安全中心重新启用"),
    INVALID_CODE("OTP_INVALID", "验证失败：无效的安全验证码"),
    TOO_MANY_ATTEMPTS("OTP_ATTEMPTS_LIMIT", "验证尝试次数过多，请15分钟后再试"),
    SYSTEM_ERROR("OTP_SYSTEM_ERROR", "系统安全验证服务暂时不可用，请稍后重试");

    private final String code;
    private final String message;

    OtpErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
