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


/**
 * @author yingzi
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@TableName("sys_temdetails")
public class SystemTemDetailsModel extends Model<SystemTemDetailsModel> {


    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "templatedetails_id",type = IdType.INPUT)
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
    private LocalDateTime createtime;

    /**
     * 排序状态 默认0 ，1 置顶
     */
    @TableField("templatedetails_status")
    private Integer temdetailsstatus;


    @Override
    protected Serializable pkVal() {
        return this.temdetailsId;
    }
}
