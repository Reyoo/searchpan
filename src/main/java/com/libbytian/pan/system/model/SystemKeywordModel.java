package com.libbytian.pan.system.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 项目名: pan
 * 文件名: SystemKeywordModel
 * 创建者: HS
 * 创建时间:2020/12/14 15:24
 * 描述: TODO
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@TableName("sys_keyword")
public class SystemKeywordModel extends Model<SystemKeywordModel> {

    private static final long serialVersionUID = 1L;




    /**
     * 关键词id主键
     */
    @TableId(value = "keyword_id")
    private String keywordId;

    /**
     * 秘钥
     */
    @TableField("secret_key")
    private String secretKey;



    /**
     * 用户base64 认证
     */
    @TableField("user_safe_key")
    private String userSafeKey;




    /**
     * 用户接口认证token
     */
    @TableField("user_token")
    private String userToken;



    /**
     * 维护开始时间
     */
    @TableField("start_time")
    private String startTime;

    /**
     * 维护结束时间
     */
    @TableField("end_time")
    private String endTime;


    @TableField("fans_key")
    private String fansKey;


    @TableField("app_id")
    private String appId;

    /**
     * 搜索框开关
     */
    @TableField("search_flag")
    private int searchFlag;



    @Override
    protected Serializable pkVal() {
        return this.keywordId;
    }



}
