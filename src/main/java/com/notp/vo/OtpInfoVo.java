package com.notp.vo;

import lombok.Data;

import java.util.Date;

/**
 * OTP信息VO
 *
 * @author sign
 */
@Data
public class OtpInfoVo {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 二维码URL
     */
    private String qrCodeUrl;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
