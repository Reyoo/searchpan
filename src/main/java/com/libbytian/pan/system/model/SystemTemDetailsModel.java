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
import java.time.LocalDateTime;


/**
 * @author yingzi
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@TableName("sys_temdetails")

public class  SystemTemDetailsModel extends Model<SystemTemDetailsModel> {


    private static final long serialVersionUID = 1L;


    public SystemTemDetailsModel(String temdetailsId, String keyword, String keywordToValue, LocalDateTime createtime, Boolean temdetailsstatus, Integer showOrder , Boolean enableFlag) {
        this.temdetailsId = temdetailsId;
        this.keyword = keyword;
        this.keywordToValue = keywordToValue;
        this.createtime = createtime;
        this.temdetailsstatus = temdetailsstatus;
        this.showOrder = showOrder;
        this.enableFlag = enableFlag;
    }

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



    @Override
    protected Serializable pkVal() {
        return this.temdetailsId;
    }
}
