/*
 * Copyright (C), 2015-2016, CIBFINTECH
 * FileName: ExchangeApiTest.java
 * Author:   郑尚书
 * Date:     2016年5月17日 下午4:32:14
 * Description: //模块目的、功能描述      
 * History: //修改记录
 * <author>      <time>      <version>    <desc>
 * 修改人姓名             修改时间            版本号                  描述
 */
package com.cib.fintech.ufm.api;

import com.cib.fintech.ufm.mock.HttpClient;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;

/**
 * 平台接口测试<br>
 *
 * @author 郑尚书
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class ExchangeApiTest {
	private static final Logger logger = LoggerFactory.getLogger(ExchangeApiTest.class);

	private static final HttpClient CLIENT = new HttpClient();

	private static final String UFM_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCNL+iNuvg1CBcW9BI3Fo3q4UVBO5XURUy8tIfCkextFi5wbvgGZBTTU0xLPQnp/alq5gbEwfX6lwS+zIg/WnUF2hsYRAaCJYRvlGYpDC0lHZOf4gwCZPtRnwkOQvF9NcJPrOYWp6t85VlIUWn/IZUYEZ31DBFPqln9eAcAuO02BwIDAQAB";
	private static final String EXCH_PRIVATE_KEY = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAKSo5fktzm9DXT34ZaiyKRueN5H2Tj+qPivavqY/x4LX7/zPR1X877Ebmvi19nNydwdsQWwR8TSJfi/MDNwzOKDHdjAkkCbK1yAOEvnBLuCax/OhlT6NF9a9o5h7SHkZ4nsliqbpLek3XYWPPvIX5PbJpVEkiCggf4qD6if7+vpTAgMBAAECgYAeG1/JWu3G/Es9PIDiAolvqlNA1gdirq8ld56qaTkCnJcd44yIlXICMSj51tOUMla/PbUMnI886vLurGGhlaABwS7Ji9J66rKT2XZGC/lpl6bORSRlJ1ByxSn5gOgrP5+PCKhINQAR1BVyOv8zFjKk6nfOvlBkh7JEvLb6Qwi2cQJBAPvloDVFSd/NkqPJ1cKA/0HAq45laiCRMkT/LOFrBsFtcOKl19AmJHhmmx5aE3AVupbavh++hAI7NpjEwmLMwlsCQQCnV4GAVE+t+Db/otBqRyFv6/WMaTo6SYpv8+ZmyqosTYDeDQSmeQrATfc6c0yQDxapAINK4PO9C6QTOvQHezlpAkBej1LW7I3Q4AD+T1RZUceAzW0ZZWSzmQ3/7LLSZDUDA6xuyMb9MnRaZlowyKunVeDXpIHetMwlckkKjEJUiH0lAkAdAlVnyrXZYbsfC7l3gwcv4Ma7ZY57hj4idDSPwzhG39SkKbuRpFAR0DI4hr5SBtuVxon8FA0My5TQ5JpjJaBpAkEAtIrlf6bXC4rRCsaPij1UXhWMVm9wYZDs03Hhs3Pge7pXGcJgtPJsFj8W8b5fO38ivW3ofNKPf6T+POEJSjXZZw==";
	private static final String EXCH_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCkqOX5Lc5vQ109+GWosikbnjeR9k4/qj4r2r6mP8eC1+/8z0dV/O+xG5r4tfZzcncHbEFsEfE0iX4vzAzcMzigx3YwJJAmytcgDhL5wS7gmsfzoZU+jRfWvaOYe0h5GeJ7JYqm6S3pN12Fjz7yF+T2yaVRJIgoIH+Kg+on+/r6UwIDAQAB";

	@Test
	public void testRefreshPublicKey() throws Exception {
		String appid = "3a36ee394f024427a1b0b08997a8fd98";
		String url = "http://10.21.10.29:8080/biz/exch/" + appid + "/signatureKey";

		String timestamp = DateFormatUtils.format(Calendar.getInstance(), "yyyy-MM-dd HH:mm:ss");
		String exSerialNo = DateFormatUtils.format(Calendar.getInstance(), "yyyyMMddHHmmss");

		String body = "{\"timestamp\":'" + timestamp + "', \"nonce\":'123456',\"ex_serial_no\":'" + exSerialNo
				+ "', \"exch\":{\"new_sign_key\":'" + EXCH_PUBLIC_KEY + "'}}";

		// base64解码
		String jsonMsg = CLIENT.send(url, appid, EXCH_PRIVATE_KEY, UFM_PUBLIC_KEY, body, HttpClient.METHOD.PUT);

		logger.info("返回消息体:{}", jsonMsg);
	}
}
