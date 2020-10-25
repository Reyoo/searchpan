package com.libbytian.pan.system.model;

import com.baomidou.mybatisplus.annotation.IdType;
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
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName("sys_template")
public class SystemTemplateModel extends Model<SystemTemplateModel> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "template_id",type = IdType.INPUT)
    private String templateid;

    /**
     * 模板名称
     */
    @TableField(value = "template_name")
    private String templatename;

    /**
     * 创建时间
     */
    @TableField(value = "template_createtime")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime templatecreatetime;

    /**
     * 最后修改时间
     */
    @TableField(value = "template_lastupdate")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime templatelastupdate;

    /**
     * 状态值 0正常，-1删除
     */
    @TableField(value = "template_status")
    private Boolean templatestatus;



    /**
     * 条数
     */
    @TableField(exist = false)
    private Integer detialsize;


    @Override
    protected Serializable pkVal() {
        return this.templateid;
    }

}
