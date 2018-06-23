/*
 * Copyright (C), 2015-2016, CIBFINTECH
 * FileName: ExchangeVO.java
 * Author:   ShangshuZHENG
 * Date:     2016年5月6日 下午2:46:48
 * Description: //模块目的、功能描述      
 * History: //修改记录
 * <author>      <time>      <version>    <desc>
 * 修改人姓名             修改时间            版本号                  描述
 */
package com.cib.fintech.ufm.mock.model.vo;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 平台相关信息<br>
 * 〈功能详细描述〉
 *
 * @author ShangshuZHENG
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class ExchangeVO {

	/**
	 * 新交易所签名公钥
	 */
	@JSONField(name = "new_sign_key")
	private String newSignKey;

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

	/**
	 * @return the newSignKey
	 */
	public String getNewSignKey() {
		return newSignKey;
	}

	/**
	 * @param newSignKey
	 *            the newSignKey to set
	 */
	public void setNewSignKey(String newSignKey) {
		this.newSignKey = newSignKey;
	}

}
