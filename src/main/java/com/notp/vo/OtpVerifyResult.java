package com.notp.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.notp.constant.OtpErrorCode;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 统一API响应格式 - 符合大公司设计标准
 *
 * @author sign
 */
@Data
public class OtpVerifyResult<T> {

    /**
     * 响应状态码
     * 0000: 成功
     * 其他: 错误码
     */
    private String code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 时间戳
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    /**
     * 请求ID，用于链路追踪
     */
    private String requestId;

    /**
     * 成功响应
     */
    public static <T> OtpVerifyResult<T> success() {
        return success(null);
    }

    public static <T> OtpVerifyResult<T> success(T data) {
        return success("操作成功", data);
    }

    public static <T> OtpVerifyResult<T> success(String message, T data) {
        OtpVerifyResult<T> result = new OtpVerifyResult<>();
        result.setCode("0000");
        result.setMessage(message);
        result.setData(data);
        result.setTimestamp(LocalDateTime.now());
        return result;
    }

    /**
     * 错误响应
     */
    public static <T> OtpVerifyResult<T> fail(String code, String message) {
        return fail(code, message, null);
    }

    public static <T> OtpVerifyResult<T> fail(String code, String message, T data) {
        OtpVerifyResult<T> result = new OtpVerifyResult<>();
        result.setCode(code);
        result.setMessage(message);
        result.setData(data);
        result.setTimestamp(LocalDateTime.now());
        return result;
    }

    /**
     * 基于错误码的响应
     */
    public static <T> OtpVerifyResult<T> fail(OtpErrorCode errorCode) {
        return fail(errorCode.getCode(), errorCode.getMessage());
    }

    public static <T> OtpVerifyResult<T> fail(OtpErrorCode errorCode, T data) {
        return fail(errorCode.getCode(), errorCode.getMessage(), data);
    }

    /**
     * 是否成功
     */
    public boolean isSuccess() {
        return "0000".equals(this.code);
    }
}
