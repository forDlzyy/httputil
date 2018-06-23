/*
 * Copyright (C), 2015-2016, CIBFINTECH
 * FileName: HttpDeleteWithBody.java
 * Author:   ShaominShen
 * Date:     2016年06月12日 14:43:52
 * Description: //模块目的、功能描述      
 * History: //修改记录
 * <author>      <time>      <version>    <desc>
 * 修改人姓名             修改时间            版本号                  描述
 */
package com.cib.fintech.ufm.mock;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

import java.net.URI;

/**
 * http delete method<br>
 * 〈功能详细描述〉
 *
 * @author ShaominShen
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class HttpDeleteWithBody extends HttpEntityEnclosingRequestBase {
    public static final String METHOD_NAME = "DELETE";

    public String getMethod() {
        return METHOD_NAME;
    }

    public HttpDeleteWithBody(final String uri) {
        super();
        setURI(URI.create(uri));
    }

    public HttpDeleteWithBody(final URI uri) {
        super();
        setURI(uri);
    }

    public HttpDeleteWithBody() {
        super();
    }
}
