package com.notp.vo;

import lombok.Data;

/**
 * OTP响应VO
 *
 * @author sign
 */
@Data
public class OtpResponse {

    /**
     * OTP密钥
     */
    private String secret;

    /**
     * 二维码URL
     */
    private String qrCodeUrl;

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 错误码
     */
    private String errorCode;

    /**
     * 错误消息
     */
    private String errorMessage;
}
