package com.libbytian.pan.system.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.*;

import java.io.Serializable;

@Builder
@AllArgsConstructor
@NoArgsConstructor

@Getter
@Setter
@ToString
@TableName("user_template")
public class SystemUserToTemplate extends Model<SystemUserToTemplate> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "user_template_id", type = IdType.AUTO)
    private Integer userTotemplateId;
    /**
     * 用户编号
     */
    @TableField("user_id")
    private String userId;
    /**
     * 模板编号
     */
    @TableField("template_id")
    private String templateId;


    @TableField("user_template_status")
    private Boolean userTemplateStatus;

    @Override
    protected Serializable pkVal() {
        return this.userTotemplateId;
    }
}
