package com.notp.constant;

/**
 * OTP错误码枚举
 *
 * @author sign
 */
public enum OtpErrorCode {

    SUCCESS("0000", "操作成功"),

    INVALID_CODE("1001", "验证码无效"),
    CODE_EXPIRED("1002", "验证码已过期"),
    TOO_MANY_ATTEMPTS("1003", "尝试次数过多，请稍后再试"),
    NOT_CONFIGURED("1004", "用户未配置OTP验证"),
    DISABLED("1005", "OTP验证已禁用"),
    SECRET_INVALID("1006", "密钥格式无效"),
    QR_GENERATION_FAILED("1007", "二维码生成失败"),

    SYSTEM_ERROR("2001", "系统内部错误"),
    DATABASE_ERROR("2002", "数据库操作失败"),
    NETWORK_ERROR("2003", "网络连接异常"),

    PARAMETER_INVALID("3001", "参数格式错误"),
    USER_NOT_FOUND("3002", "用户不存在"),
    USER_DISABLED("3003", "用户已禁用"),

    UNKNOWN_ERROR("9999", "未知错误");

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

    public static OtpErrorCode fromCode(String code) {
        for (OtpErrorCode errorCode : values()) {
            if (errorCode.code.equals(code)) {
                return errorCode;
            }
        }
        return UNKNOWN_ERROR;
    }

    @Override
    public String toString() {
        return "{" +
                "\"code\":\"" + code + "\"" +
                ", \"message\":\"" + message + "\"" +
                '}';
    }
}
