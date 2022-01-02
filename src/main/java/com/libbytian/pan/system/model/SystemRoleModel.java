package com.libbytian.pan.system.model;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * <p>
 * 用户角色关系表
 * </p>
 *
 * @author liugh123
 * @since 2018-06-25
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@TableName("sys_role")
public class SystemRoleModel extends Model<SystemRoleModel> {

    private static final long serialVersionUID = 1L;

    /**
     * 角色id主键
     */
    @TableId(value = "role_id")
    String roleId;
    /**
     * 角色字符
     */
    @TableField("role_name")
    String roleName;


    /**
     * 角色名称
     */
    @TableField("show_name")
    String showName;
    /**
     * 角色状态
     */
    @TableField("role_status")
    Boolean roleStatus;

    /**
     * 可修改状态（0：不允许，1：允许）
     */
    @TableField("allowremove")
    Boolean allowremove;


    @TableField("createtime")
    LocalDateTime createTime;

    @TableField(exist = false)
    @Value("false")
    Boolean checked;

    @TableField(exist = false)
    @Value("false")
    LocalDateTime starttime;

    @TableField(exist = false)
    @Value("false")
    LocalDateTime endtime;


    @TableField(exist = false)
    Long page;


    @TableField(exist = false)
    Long limits;

}
