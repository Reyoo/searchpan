package com.libbytian.pan.system.model;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(fluent = true)
@TableName("sys_notify")
public class SystemNotifyModel implements Serializable {


    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */

    @TableId("id")
    private String id;


    /**
     * 通知内容
     */
    @TableField(value = "notify_text")
    private String notifyText;


    /**
     * 新增、编辑时间
     */
    @TableField(value = "modify_date")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    LocalDateTime modifyDate;

    /**
     * 0、禁用；1、启用
     */
    @TableField(value = "open_notify")
    Boolean openNotify;



    @TableField(exist = false )
    private Long  page;


    @TableField(exist = false)
    private  Long limits;


}
