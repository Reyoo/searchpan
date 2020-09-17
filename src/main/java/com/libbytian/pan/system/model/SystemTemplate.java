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
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName("sys_template")
public class SystemTemplate extends Model<SystemTemplate> {

    private static final long serialVersionUID = 1L;


    @TableId(value = "template_id",type = IdType.AUTO)
    private Integer templateid;

    @TableField(value = "template_name")
    private String templatename;

    @TableField(value = "template_createtime")
    private LocalDateTime templatecreatetime;

    @TableField(value = "template_lastupdate")
    private LocalDateTime templatelastupdate;

    @TableField(value = "temolate_status")
    private String temolatestatus;


    @Override
    protected Serializable pkVal() {
        return this.templateid;
    }

}
