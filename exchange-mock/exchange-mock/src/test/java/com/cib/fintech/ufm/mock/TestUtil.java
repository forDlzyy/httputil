/*
 * Copyright (C), 2015-2016, CIBFINTECH
 * FileName: TestHelper.java
 * Author:   郑尚书
 * Date:     2016年5月17日 下午4:11:13
 * Description: //模块目的、功能描述      
 * History: //修改记录
 * <author>      <time>      <version>    <desc>
 * 修改人姓名             修改时间            版本号                  描述
 */
package com.cib.fintech.ufm.mock;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 测试工具类<br>
 * 
 *
 * @author 郑尚书
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class TestUtil {
	private static final Logger logger = LoggerFactory.getLogger(TestUtil.class);

	/**
	 * 
	 * 功能描述: <br>
	 * 生成一对新的RSA公私钥匙文件。并保存到/keys/{timestamp}目录下
	 *
	 * @see [相关类/方法](可选)
	 * @since [产品/模块版本](可选)
	 */
	@Test
	public void generateNewRSAKeys() {
		Date now = Calendar.getInstance().getTime();
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddhhmmss");
		String timestamp = df.format(now);

		File f = new File("/tomcat/" + timestamp);
		if (!f.exists()) {
			f.mkdirs();// 新建目录
		}

		RSAUtil.genKeyPair(f.getAbsolutePath());

		logger.info("成功生成新的RSA公私钥@{}", f.getAbsolutePath());
	}
}
