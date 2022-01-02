package com.libbytian.pan.system.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.libbytian.pan.system.enums.SensitiveWordsType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 项目名: pan
 * 文件名: SystemRecordSensitiveModel
 * 创建者: HuangS
 * 创建时间:2020/10/21 1:00
 * 描述: TODO
 */

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@TableName("sys_recordsensitive")
public class SystemRecordSensitiveModel implements Serializable {

    private static final long serialVersionUID = 1L;
    /** 主键id */
    @TableId(value = "record_id")
    private Long recordId;
    /** 记录用户名 */
    @TableField("record_username")
    private String recordUsername;
    /** 记录时间 */
    @TableField("record_savetime")
    private LocalDateTime recordSaveTime;
    /** 记录敏感词 */
    @TableField("record_word")
    private String recordWord;
    /** 记录敏感词 */
    /** 敏感词类型，1：色情，2：政治，3：暴恐，4：民生，5：反动，6：贪腐，7：其他 */
    @TableField("record_type")
    private SensitiveWordsType recordType;

}
