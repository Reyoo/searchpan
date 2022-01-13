package com.libbytian.pan.system.model;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author liugh123
 * @since 2018-06-25
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@TableName("sys_user")
public class SystemUserModel extends Model<SystemUserModel> {

    static final long serialVersionUID = -6832900779447088497L;

    public SystemUserModel(String username) {
        this.username = username;
    }

    /**
     * 用户主键
     */
    @TableField("user_id")
    String userId;

    /**
     * 用户名
     */
    @TableId("user_name")
    String username;

    /**
     * 用户手机号
     */
    @TableField("user_mobile")
    String mobile;
    /**
     * 密码
     */
    @TableField("user_password")
    String password;

    /**
     * 最后修改时间
     */
    @TableField("user_lastlogin_time")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    LocalDateTime lastLoginTime;

    /**
     * 创建时间
     */
    @TableField("createtime")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    LocalDateTime createTime;

    /**
     * 接口调用最后时间
     */
    @TableField("call_time")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    LocalDateTime callTime;


    /**
     * 状态值（1：启用，2：禁用，3：删除）
     */
    @TableField("status")
    Boolean status;

    /**
     * 激活到期时间
     */
    @TableField("act_time")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    LocalDateTime actTime;


    /**
     * 用户付费状态（0：白嫖用户 ，1：付费用户）
     */
    @TableField("user_flag")
    Boolean userFlag;


    /**
     * 可修改状态（0：不允许，1：允许）
     */
    @TableField("allowremove")
    Boolean allowremove;

    /**
     * 虚拟字段 查询开始时间
     */
    @TableField(exist = false)
    LocalDate starttime;

    /**
     * 虚拟字段 查询结束时间
     */
    @TableField(exist = false)
    LocalDate endtime;


    @TableField(exist = false)
    Long page;


    @TableField(exist = false)
    Long limits;


    @TableField(exist = false)
    String token;

    @TableField(exist = false)
    Integer rememberMe;


    @TableField(exist = false)
    String wxToken;


    @TableField(exist = false)
    String appId;

}
