package com.libbytian.pan.system.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
@Data
@TableName("sys_keyword")
public class SystemKeywordModel {

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
     * 维护开始时间
     */
    @TableField("start_time")
    private LocalDateTime startTime;

    /**
     * 维护结束时间
     */
    @TableField("end_time")
    private LocalDateTime endTime;

}
