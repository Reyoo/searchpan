package com.libbytian.pan.system.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 过滤敏感词类型
 * @author hoojo
 * @createDate 2018年9月24日 下午9:16:04
 * @file SensitiveWordsType.java
 * @package com.cnblogs.hoojo.sensitivewords.business.enums
 * @project sensitive-words-filter
 * @blog http://hoojo.cnblogs.com
 * @email hoojo_@126.com
 * @version 1.0
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum SensitiveWordsType implements IEnum<Integer> {
	/** 色情 */
	PORNO(1, "色情"), 
	/** 政治 */
	POLITICS(2, "政治"), 
	/** 暴恐 */
	TERROR(3, "暴恐"), 
	/** 民生 */
	LIVELIHOOD(4, "民生"), 
	/** 反动 */
	REACTION(5, "反动"), 
	/** 贪腐 */
	CORRUPTION(6, "贪腐"), 
	/** 其他 */
	OTHERS(7, "其他"); 

	private int code;
	private String name;


	SensitiveWordsType(int code, String name) {
		this.code = code;
		this.name = name;
	}

	@Override
	public Integer getValue() {
		return this.code;
	}
}
