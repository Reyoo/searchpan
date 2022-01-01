package com.libbytian.pan.system.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.*;

import java.io.Serializable;

/**
 * 模板，模板详情表
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName("tem_temdetails")
public class SystemTemToTemdetail extends Model<SystemTemToTemdetail> {

    /**
     * 关联表主键
     */
    @TableId(value = "tem_temdetails_id",type = IdType.AUTO)
    private Integer temtemdetailsid ;

    /**
     * 模板主键
     */
    @TableField(value = "template_id")
    private String templateid;

    /**
     * 模板详情主键
     */
    @TableField(value = "templatedetails_id")
    private String templatedetailsid;


}
