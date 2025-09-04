package com.notp.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 用户OTP配置表
 *
 * @author sign
 */
@Data
@TableName("sys_user_otp")
public class SysUserOtp {

    @TableId
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * OTP密钥 (Base32编码)
     */
    private String secret;

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
