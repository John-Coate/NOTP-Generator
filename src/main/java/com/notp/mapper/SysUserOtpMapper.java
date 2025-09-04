package com.notp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.notp.entity.SysUserOtp;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 用户OTP配置Mapper
 *
 * @author sign
 */
@Mapper
public interface SysUserOtpMapper extends BaseMapper<SysUserOtp> {

    /**
     * 根据用户ID查询OTP配置
     */
    @Select("SELECT * FROM sys_user_otp WHERE user_id = #{userId}")
    SysUserOtp selectByUserId(Long userId);
}
