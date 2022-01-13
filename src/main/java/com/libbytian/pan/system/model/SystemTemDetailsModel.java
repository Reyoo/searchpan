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
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * @author yingzi
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@TableName("sys_temdetails")
public class  SystemTemDetailsModel extends Model<SystemTemDetailsModel> {


    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "templatedetails_id")
    private String temdetailsId;

    /**
     * 关键字
     */
    @TableField("templatedetails_keyword")
    private String keyword;

    /**
     * 关键字对应回复
     */
    @TableField("templatedetails_value")
    private String keywordToValue;

    /**
     * 创建时间
     */
    @TableField("createtime")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createtime;

    /**
     * 排序状态 默认flase ，true 置顶
     */
    @TableField("templatedetails_status")
    private Boolean temdetailsstatus;


    /**
     * 排序位置
     */
    @TableField("show_order")
    private Integer showOrder;

    /**
     * 启用禁用
     * false 禁用
     * true 启用
     */
    @TableField("enable_flag")
    private Boolean enableFlag;



    /**
     * 模板Id
     */
    @TableField(exist = false)
    private String templateId;


}
