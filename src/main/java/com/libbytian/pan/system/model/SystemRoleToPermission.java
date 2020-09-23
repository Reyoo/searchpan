package com.libbytian.pan.system.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@TableName("role_permission")
public class SystemRoleToPermission extends Model<SystemRoleToPermission> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "role_permission_id" , type = IdType.AUTO)
    private Integer rolepermissionid;

    /**
     * 橘色编号
     */
    @TableField("role_id")
    private String roleid;

    /**
     * 权限编号
     */
    @TableField("permission_id")
    private String permissionid;


    /**
     * 角色权限状态
     */
    @TableField("role_permission_status")
    private Integer rolepermissionstatus;


    @Override
    protected Serializable pkVal() {
        return this.rolepermissionid;
    }
}
