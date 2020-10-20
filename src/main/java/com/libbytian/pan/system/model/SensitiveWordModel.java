package com.libbytian.pan.system.model;

import com.libbytian.pan.system.enums.SensitiveWordsType;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;


/**
 * 敏感词库
 * @author hoojo
 * @createDate 2018-02-02 14:54:58
 * @file SensitiveWordModel.java
 * @blog http://hoojo.cnblogs.com
 * @email hoojo_@126.com
 * @version 1.0
 */
@Data
public class SensitiveWordModel implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	/** 主键id */
	private Long id;
	/** 敏感词 */
	private String word;
	/** 敏感词类型，1：色情，2：政治，3：暴恐，4：民生，5：反动，6：贪腐，7：其他 */
	private SensitiveWordsType type;
	/** 创建人 */
	private String creator;
	/** 创建时间 */
	private LocalDate createTime;
	/** 更新人 */
	private String updater;
	/** 更新时间 */
	private LocalDate updateTime;


	public SensitiveWordModel() {
	}
	
	public SensitiveWordModel(String word, String creator, String updater) {
		super();
		this.word = word;
		this.creator = creator;
		this.updater = updater;
	}

	public SensitiveWordModel(String word) {
		super();
		this.word = word;
	}

}
