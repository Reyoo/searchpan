package com.libbytian.pan.system.model;


import cn.hutool.core.util.StrUtil;
import com.alibaba.druid.util.StringUtils;
import com.alibaba.druid.util.Utils;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.bind.DefaultValue;


import java.io.ByteArrayOutputStream;
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
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@TableName("sys_user")
public class SystemUserModel extends Model<SystemUserModel>   {

    private static final long serialVersionUID = 1L;
    /**
     * 用户主键
     */
    @TableField("user_id")
    private String userId;

    /**
     * 用户名
     */
    @TableId("user_name")
    private String username;

    /**
     * 用户手机号
     */
    @TableField("user_mobile")
    private String mobile;
    /**
     * 密码
     */
    @TableField("user_password")
    private String password;

    /**
     * 最后修改时间
     */
    @TableField("user_last_login_time")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime lastLoginTime;

    /**
     * 创建时间
     */
    @TableField("createtime")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createTime;




    /**
     * 状态值（1：启用，2：禁用，3：删除）
     */
    @TableField("status")
    private Boolean status;

    /**
     * 激活到期时间
     */
    @TableField("act_time")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime actTime;

    /**
     * 激活时长
     */
    @TableField("act_range")
    private Integer actrange;

    /**
     * 用户付费状态（0：白嫖用户 ，1：付费用户）
     */
    @TableField("user_flag")
    private Boolean userFlag;


    /**
     * 可修改状态（0：不允许，1：允许）
     */
    @TableField("allowremove")
    private Boolean allowremove;



    /**
     * 虚拟字段 查询开始时间
     */
    @TableField(exist = false)
    private LocalDate starttime;



    /**
     * 虚拟字段 查询结束时间
     */
    @TableField(exist = false)
    private LocalDate endtime;


    @TableField(exist = false )
    private Long  page;


    @TableField(exist = false)
    private  Long limits;




    @TableField(exist = false)
    private String token;

    @TableField(exist = false)
    private Integer rememberMe;


    @TableField(exist = false)
    private String wxToken;






    @Override
    protected Serializable pkVal() {
        return this.userId;
    }





}
