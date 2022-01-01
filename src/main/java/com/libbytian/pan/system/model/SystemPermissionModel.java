package com.libbytian.pan.system.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Value;

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
@Accessors(fluent = true)
@TableName("sys_permission")
public class SystemPermissionModel {

    /**
     * 权限id主键
     */
    @TableField(value = "permission_id")
    private String permissionId;

    /**
     * 权限url
     */

    @TableId ("permission_url")
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
    private Boolean permissionstatus;

    /**
     * 创建时间
     */
    @TableField("createtime")
    private LocalDateTime createtime;


    /**
     * 可修改状态（0：不允许，1：允许）
     */
    @TableField("allowremove")
    private Boolean allowremove;

    /**
     * 虚拟字段， 查询开始时间
     */
    @TableField(exist = false)
    private LocalDateTime starttime;

    /**
     * 虚拟字段， 查询结束时间
     */
    @TableField(exist = false)
    private LocalDateTime endtime;


    @TableField(exist = false )
    private Long  page;


    @TableField(exist = false)
    private  Long limits;

    @TableField(exist = false)
//    @Value("false")
    private Boolean checked;



}
