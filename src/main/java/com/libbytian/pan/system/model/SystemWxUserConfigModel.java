package com.libbytian.pan.system.model;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;


/**
 *
 */

@NoArgsConstructor
@AllArgsConstructor
@Data
@TableName("sys_wx_user_config")
@Builder
public class SystemWxUserConfigModel extends Model<SystemWxUserConfigModel> implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 用户Id 主键
     */
    @TableId("user_id")
    String userId;
    /**
     * 用户名
     */
    @TableField("user_name")
    String username;
    /**
     *  微信AppId
     */
    @TableField("wx_appId")
    String wxAppId;
    /**
     * 微信自生成密码
     */
    @TableField("wx_secret")
    String wxSecret;

    /**
     * 用户定义token
     */
    @TableField("wx_token")
    String wxToken;

    /**
     * 消息加解密密钥(EncodingAESKey)
     */
    @TableField("wx_aesKey")
    String wxAesKey;

    /**
     * 微信ID
     */
    @TableField("wx_id")
    String wxId;

    @TableField("user_safe_key")
    String userSafeKey;
    /**
     * 最后修改时间
     */
    @TableField("record_time")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    LocalDateTime recordTime;



}
