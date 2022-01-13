package com.libbytian.pan.system.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.libbytian.pan.system.enums.SensitiveWordsType;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;


/**
 * 敏感词库
 * @author hoojo
 * @createDate 2018-02-02 14:54:58
 * @file SensitiveWordModel.java
 * @blog http://hoojo.cnblogs.com
 * @email hoojo_@126.com
 * @version 1.0
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data

@TableName("sys_sensitive")
public class SensitiveWordModel implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	/** 主键id */
	@TableId(value = "id")
	private Long id;
	/** 敏感词 */
	@TableField("word")
	private String word;
	/** 敏感词类型，1：色情，2：政治，3：暴恐，4：民生，5：反动，6：贪腐，7：其他 */
	@TableField(value = "type")
	private SensitiveWordsType type;
	/** 创建人 */
	@TableField("creator")
	private String creator;
	/** 创建时间 */
	@TableField("createTime")
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	private LocalDateTime createTime;
	/** 更新人 */
	@TableField("updater")
	private String updater;
	/** 更新时间 */
	@TableField("updateTime")
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	private LocalDate updateTime;



}
