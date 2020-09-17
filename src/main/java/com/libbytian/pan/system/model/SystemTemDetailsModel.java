package com.libbytian.pan.system.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


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
    @TableId("templatedetails_id")
    private String temdetailsId;

    /**
     * 关键字
     */
    @TableField("templatedetails_keyword")
    private String temdetailsKeyword;

    /**
     * 关键字对应回复
     */
    @TableField("templatedetails_value")
    private String temdetailsValue;

    @Override
    protected Serializable pkVal() {
        return this.temdetailsId;
    }
}
