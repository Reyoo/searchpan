package com.libbytian.pan.system.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author: QiSun
 * @date: 2020-09-10
 * @Description:
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@TableName("permission")
public class SystemPermissionModel {


    /**
     * 权限id主键
     */
    @TableId(value = "permission_id")
    private String permissionId;
    /**
     * 权限url
     */
    @TableField("permission_url")
    private String permissionUrl;


    /**
     * 权限说明
     */
    @TableField("permission_comment")
    private String permissionComment;


    /**
     * 权限状态
     */
    @TableField("permission_status")
    private String permissionstatus;

    /**
     * 创建时间
     */
    @TableField("createtime")
    private LocalDateTime createtime;
}
