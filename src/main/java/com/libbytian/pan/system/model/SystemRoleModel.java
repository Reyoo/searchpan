package com.libbytian.pan.system.model;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.bind.DefaultValue;

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
    private String roleId;
    /**
     * 角色字符
     */
    @TableField("role_name")
    private String roleName;


    /**
     * 角色名称
     */
    @TableField("show_name")
    private String showName;
    /**
     * 角色状态
     */
    @TableField("role_status")
    private boolean roleStatus;



    @TableField("createtime")
    private LocalDateTime createTime;

    @TableField(exist = false)
    @Value("false")
    private boolean isChecked;

    @TableField(exist = false)
    @Value("false")
    private LocalDateTime starttime;

    @TableField(exist = false)
    @Value("false")
    private LocalDateTime endtime;

    @Override
    protected Serializable pkVal() {
        return this.roleId;
    }
}
