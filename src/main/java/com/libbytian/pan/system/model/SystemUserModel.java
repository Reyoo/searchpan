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
    @TableId("user_id")
    private String userId;

    /**
     * 用户名
     */
    @TableField("user_name")
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
    private boolean status;

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
     * 虚拟字段 查询开始时间
     */
    @TableField(exist = false)
    private LocalDate starttime;



    /**
     * 虚拟字段 查询结束时间
     */
    @TableField(exist = false)
    private LocalDate endtime;




    @TableField(exist = false)
    private String token;


    @Override
    protected Serializable pkVal() {
        return this.userId;
    }

}
