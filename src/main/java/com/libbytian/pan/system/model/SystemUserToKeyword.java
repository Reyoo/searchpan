package com.libbytian.pan.system.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: QiSun
 * @date: 2020-12-16
 * @Description: 用户关联关键字
 */


@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@TableName("sys_user_keyword")
public class SystemUserToKeyword  extends Model<SystemUserToKeyword> {

    private static final long serialVersionUID = 1L;


    /**
     * 用户关键字表ID
     */
    @TableId("user_keyword_id")
    private String userKeyId;


    /**
     * 用户ID
     */
    @TableField("user_id")
    private String userId;


    /**
     * 关键字ID
     */
    @TableField("keyword_id")
    private String keywordId;




}
