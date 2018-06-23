package com.cib.fintech.ufm.mock;

import java.io.File;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ConnectException;
import java.net.UnknownHostException;

import javax.net.ssl.SSLException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * 
 * p2p资金托管平台，模拟p2p网贷公司客户端
 * 
 * 
 * @author yangyiming
 * @version 0.0.1
 * @date 2015年9月10日
 */
public class HttpClient {
	public enum METHOD {
		PUT, POST, GET, DELETE
	};

	private static final String REQUEST_TEMPLATE = "{\"message\":\"$message$\",\"signature\":\"$signature$\"}";
	private static final Logger logger = LoggerFactory.getLogger(HttpClient.class);
	private static final String CHARSET_UTF8 = "UTF-8";

	/**
	 * max total connection
	 */
	public final static int maxTotal = 200;

	/**
	 * default max connection per route
	 */
	public final static int maxPerRoute = 50;

	public final static int timeout = 30 * 1000;

	private CloseableHttpClient getHttpClient() {
		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();

		cm.setMaxTotal(maxTotal);
		cm.setDefaultMaxPerRoute(maxPerRoute);

		CloseableHttpClient httpClient = null;
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(timeout).setConnectTimeout(timeout)
				.setConnectionRequestTimeout(timeout).build();

		String proxyHost = System.getProperty("http.proxyHost");
		String proxyPort = System.getProperty("http.proxyPort");

		if (StringUtils.isNotBlank(proxyHost)) {
			// 使用代理链接
			int port = 80;
			if (StringUtils.isNotBlank(proxyPort)) {
				port = Integer.parseInt(proxyPort);
			}
			httpClient = HttpClients.custom().setConnectionManager(cm)
					.setRetryHandler(new DefaultHttpRequestRetryHandler(1, true)).setDefaultRequestConfig(requestConfig)
					.setProxy(new HttpHost(proxyHost, port)).build();

		} else {
			httpClient = HttpClients.custom().setConnectionManager(cm)
					.setRetryHandler(new DefaultHttpRequestRetryHandler(1, true)).setDefaultRequestConfig(requestConfig)
					.build();
		}

		return httpClient;
	}

	/**
	 * 
	 * 功能描述: <br>
	 * 发送报文并解析结果
	 *
	 * @param url
	 *            目标URL
	 * @param appid
	 *            平台appid
	 * @param exchPrivateKey
	 *            平台加签私钥
	 * @param ufmPublicKey
	 *            兴资管验签公钥
	 * @param requestMsg
	 *            请求消息
	 * @param method
	 *            HTTP方法
	 * @return 返回json
	 * @throws Exception
	 */
	public String send(String url, String appid, String exchPrivateKey, String ufmPublicKey, String requestMsg,
			METHOD method) throws Exception {
		// 1.base64编码
		String encoded = Base64.encodeBase64String(requestMsg.getBytes("UTF-8"));
		// 2.左边加上appid后生成待签字符串
		String forSign = StringUtils.join(appid, encoded);

		String signature = RSASignature.sign(forSign, exchPrivateKey, "UTF-8");

		String content = StringUtils.replace(REQUEST_TEMPLATE, "$message$", encoded);
		content = StringUtils.replace(content, "$signature$", signature);

		String responseTxt = "";
		switch (method) {
		case PUT:
			responseTxt = put(url, content);
			break;
		case POST:
			responseTxt = post(url, content);
			break;
		case DELETE:
			responseTxt = delete(url, content);
			break;
		default:
			break;
		}

		logger.info("Response txt: {}", responseTxt);

		JSONObject responseMsg = (JSONObject) JSON.parse(responseTxt);
		// 验签
		String respSignature = responseMsg.getString("signature");
		String respEncoded = responseMsg.getString("message");
		String respForSign = StringUtils.join(appid, respEncoded);
		// 待签名数据
		boolean isPassed = RSASignature.doCheck(respForSign, respSignature, ufmPublicKey, "UTF-8");

		if (!isPassed) {
			throw new Exception("返回消息验签失败");
		}

		// base64解码
		String jsonMsg = new String(Base64.decodeBase64(respEncoded), "UTF-8");

		logger.info("返回消息体:{}", jsonMsg);

		return jsonMsg;
	}

	private String delete(String url, String content) throws Exception {
		CloseableHttpClient client = getHttpClient();

		CloseableHttpResponse response = null;
		try {
			HttpDeleteWithBody delete = new HttpDeleteWithBody(url);
			StringEntity entity = new StringEntity(content, CHARSET_UTF8);

			delete.setEntity(entity);
			delete.addHeader("Content-Type", "application/json;charset=UTF-8");

			response = client.execute(delete);
			int statusCode = response.getStatusLine().getStatusCode();

			if (statusCode == 200) {
				logger.info("*********发送请求成功**********");
			} else {
				logger.error("*********发送请求失败[" + statusCode + "]**********");
				throw new Exception("http返回码：" + statusCode);
			}

			String respText = EntityUtils.toString(response.getEntity(), CHARSET_UTF8);
			logger.info("服务器返回respText:" + respText);

			return respText;
		} catch (Exception e) {
			if (e instanceof InterruptedIOException) {
				logger.error("连接服务器超时");
			} else if (e instanceof UnknownHostException) {
				logger.error("UnknownHost");
			} else if (e instanceof ConnectException) {
				logger.error("服务器拒绝连接");
			} else if (e instanceof SSLException) {
				logger.error("SSL协议错误");
			} else {
				logger.error("系统错误");
			}
			logger.error(e.getMessage(), e);
			throw e;
		} finally {
			if (response != null) {
				response.close();
			}
		}
	}

	/**
	 * 
	 * @param url
	 *            服务地址
	 * @param message
	 *            报文
	 * @param privateKey
	 *            签名私钥
	 * @return
	 * @throws Exception
	 */
	public String put(String url, String content) throws Exception {
		CloseableHttpClient client = getHttpClient();

		CloseableHttpResponse response = null;
		try {
			HttpPut put = new HttpPut(url);

			StringEntity entity = new StringEntity(content, CHARSET_UTF8);

			put.setEntity(entity);

			put.addHeader("Content-Type", "application/json;charset=UTF-8");

			response = client.execute(put);
			int statusCode = response.getStatusLine().getStatusCode();

			if (statusCode == 200) {
				logger.info("*********发送请求成功**********");
			} else {
				logger.error("*********发送请求失败[" + statusCode + "]**********");
				throw new Exception("http返回码：" + statusCode);
			}

			String respText = EntityUtils.toString(response.getEntity(), CHARSET_UTF8);
			logger.info("服务器返回respText:" + respText);

			return respText;
		} catch (Exception e) {
			if (e instanceof InterruptedIOException) {
				logger.error("连接服务器超时");
			} else if (e instanceof UnknownHostException) {
				logger.error("UnknownHost");
			} else if (e instanceof ConnectException) {
				logger.error("服务器拒绝连接");
			} else if (e instanceof SSLException) {
				logger.error("SSL协议错误");
			} else {
				logger.error("系统错误");
			}
			logger.error(e.getMessage(), e);
			throw e;
		} finally {
			if (response != null) {
				response.close();
			}
		}
	}
	/**
	 * 
	 * @param url
	 *            服务地址
	 * @param message
	 *            报文
	 * @param privateKey
	 *            签名私钥
	 * @return
	 * @throws Exception
	 */

	public String post(String url, String content) throws Exception {
		CloseableHttpClient client = getHttpClient();

		CloseableHttpResponse response = null;
		try {
			HttpPost post = new HttpPost(url);
			StringEntity entity = new StringEntity(content, CHARSET_UTF8);

			post.setEntity(entity);
			post.addHeader("Content-Type", "application/json;charset=UTF-8");

			response = client.execute(post);
			int statusCode = response.getStatusLine().getStatusCode();

			if (statusCode == 200) {
				logger.info("*********发送请求成功**********");
			} else {
				logger.error("*********发送请求失败[" + statusCode + "]**********");
				throw new Exception("http返回码：" + statusCode);
			}

			String respText = EntityUtils.toString(response.getEntity(), CHARSET_UTF8);
			logger.info("服务器返回respText:" + respText);

			return respText;
		} catch (Exception e) {
			if (e instanceof InterruptedIOException) {
				logger.error("连接服务器超时");
			} else if (e instanceof UnknownHostException) {
				logger.error("UnknownHost");
			} else if (e instanceof ConnectException) {
				logger.error("服务器拒绝连接");
			} else if (e instanceof SSLException) {
				logger.error("SSL协议错误");
			} else {
				logger.error("系统错误");
			}
			logger.error(e.getMessage(), e);
			throw e;
		} finally {
			if (response != null) {
				response.close();
			}
		}
	}
	    /**
     * 文件上传
     *
     * @param url 服务地址
     * @param filePath 要上传的文件
     * @param appid 平台id
     * @param exchPrivateKey 平台私钥
     * @param ufmPublicKey 兴存管公钥
     * @param requestMsg 请求消息
     * @return
     * @throws Exception
     */
    public String upload(String url, String filePath, String appid, String exchPrivateKey, String ufmPublicKey, String requestMsg)
            throws Exception {
        // 1.base64编码
        String encoded = Base64.encodeBase64String(requestMsg.getBytes("UTF-8"));
        // 2.左边加上appid后生成待签字符串
        String forSign = StringUtils.join(appid, encoded);

        String signature = RSASignature.sign(forSign, exchPrivateKey, "UTF-8");

        CloseableHttpClient client = getHttpClient();

        CloseableHttpResponse response = null;
        String responseTxt = null;
        try {
            HttpPost post = new HttpPost(url);
            MultipartEntityBuilder mEntityBuilder = MultipartEntityBuilder.create();
            mEntityBuilder.addBinaryBody("file", new File(filePath));
            mEntityBuilder.addTextBody("message", encoded);
            mEntityBuilder.addTextBody("signature", signature);
            post.setEntity(mEntityBuilder.build());

            // post.addHeader("Content-Type", "application/octet-stream");
            System.out.print(post.getEntity().toString());
            response = client.execute(post);
            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode == 200) {
                logger.info("*********发送请求成功**********");
            } else {
                logger.error("*********发送请求失败[" + statusCode + "]**********");
                throw new Exception("http返回码：" + statusCode);
            }
            responseTxt = EntityUtils.toString(response.getEntity(), CHARSET_UTF8);

            logger.info("服务器返回respText:" + responseTxt);

        } catch (Exception e) {
            if (e instanceof InterruptedIOException) {
                logger.error("连接服务器超时");
            } else if (e instanceof UnknownHostException) {
                logger.error("UnknownHost");
            } else if (e instanceof ConnectException) {
                logger.error("服务器拒绝连接");
            } else if (e instanceof SSLException) {
                logger.error("SSL协议错误");
            } else if (e instanceof IOException) {
                logger.error("文件解析错误");
            } else {
                logger.error("系统错误");
            }
            logger.error(e.getMessage(), e);
            throw e;
        } finally {
            if (response != null) {
                response.close();
            }
        }

        logger.debug("Response txt: {}", responseTxt);

        JSONObject responseMsg = (JSONObject) JSON.parse(responseTxt);
        // 验签
        String respSignature = responseMsg.getString("signature");
        String respEncoded = responseMsg.getString("message");
        String respForSign = StringUtils.join(appid, respEncoded);
        // 待签名数据
        boolean isPassed = RSASignature.doCheck(respForSign, respSignature, ufmPublicKey, "UTF-8");

        if (!isPassed) {
            throw new Exception("返回消息验签失败");
        }

        // base64解码
        String jsonMsg = new String(Base64.decodeBase64(respEncoded), "UTF-8");

        return jsonMsg;
    }

}
