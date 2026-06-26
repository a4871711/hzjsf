package com.dlc.common.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class HttpRequest {
	
	public static String addUrl(String head, String tail) {
		if (head.endsWith("/")) {
			if (tail.startsWith("/")) {
				return head.substring(0, head.length() - 1) + tail;
			} else {
				return head + tail;
			}
		} else {
			if (tail.startsWith("/")) {
				return head + tail;
			} else {
				return head + "/" + tail;
			}
		}
	}

	public synchronized static String postData(String url, Map<String, String> params, String codePage) throws Exception {

		HttpClient httpClient = new HttpClient();
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(10 * 1000);
		httpClient.getHttpConnectionManager().getParams().setSoTimeout(10 * 1000);

		final PostMethod method = new PostMethod(url);
		if (params != null) {
			method.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, codePage);
			method.setRequestBody(assembleRequestParams(params));
		}
		String result = "";
		try {
			httpClient.executeMethod(method);
			result = new String(method.getResponseBody(), codePage);
		} catch (final Exception e) {
			throw e;
		} finally {
			method.releaseConnection();
		}
		return result;
	}

	public synchronized static String postData(String url, String codePage) throws Exception {
		final HttpClient httpClient = new HttpClient();
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(10 * 1000);
		httpClient.getHttpConnectionManager().getParams().setSoTimeout(10 * 1000);

		final GetMethod method = new GetMethod(url);
		String result = "";
		try {
			httpClient.executeMethod(method);
			result = new String(method.getResponseBody(), codePage);
		} catch (final Exception e) {
			throw e;
		} finally {
			method.releaseConnection();
		}
		return result;
	}

	/**
	 * 组装http请求参数
	 * 
	 * @param : params
	 * @param : menthod
	 * @return
	 */
	private synchronized static NameValuePair[] assembleRequestParams(Map<String, String> data) {
		final List<NameValuePair> nameValueList = new ArrayList<NameValuePair>();

		Iterator<Map.Entry<String, String>> it = data.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, String> entry = (Map.Entry<String, String>) it.next();
			nameValueList.add(new NameValuePair((String) entry.getKey(), (String) entry.getValue()));
		}

		return nameValueList.toArray(new NameValuePair[nameValueList.size()]);
	}
	
	public static final int DEF_CONN_TIMEOUT = 30000;
    public static final int DEF_READ_TIMEOUT = 30000;

	private final static String DEFAULT_ENCODING = "UTF-8";

	public static String postDataSSL(String urlStr, String data) {
		return postDataSSL(urlStr, data, null);
	}

	public static String postDataSSL(String urlStr, String data, String contentType) {
		BufferedReader reader = null;
		try {
            URLConnection conn = initHttps(urlStr);
			if (contentType != null) {
                conn.setRequestProperty("content-type", contentType);
            }
			OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream(), DEFAULT_ENCODING);
			if (data == null) {
                data = "";
            }
			writer.write(data);
			writer.flush();
			writer.close();

			reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), DEFAULT_ENCODING));
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
				sb.append("\r\n");
			}
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (reader != null) {
                    reader.close();
                }
			} catch (IOException e) {
			}
		}
		return null;
	}
	
	private static class TrustAnyHostnameVerifier implements HostnameVerifier {
        public boolean verify(String hostname, SSLSession session)
        {
            return true;
        }
    }

    /**
     * 初始化http请求参数
     */
    private static HttpsURLConnection initHttps(String url) throws Exception {
        TrustManager[] tm = {new MyX509TrustManager()};
        //System.setProperty("https.protocols", "TLSv1");
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, tm, new java.security.SecureRandom());
        // 从上述SSLContext对象中得到SSLSocketFactory对象
        SSLSocketFactory ssf = sslContext.getSocketFactory();
        URL _url = new URL(url);
        HttpsURLConnection http = (HttpsURLConnection) _url.openConnection();
        // 设置域名校验
        http.setHostnameVerifier(new TrustAnyHostnameVerifier());
        // 连接超时
        http.setConnectTimeout(DEF_CONN_TIMEOUT);
        // 读取超时 --服务器响应比较慢，增大时间
        http.setReadTimeout(DEF_READ_TIMEOUT);
        http.setUseCaches(false);
        http.setRequestProperty("Content-Type",
                "application/x-www-form-urlencoded");
        http.setRequestProperty(
                "User-Agent",
                "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.146 Safari/537.36");
        http.setSSLSocketFactory(ssf);
        http.setDoOutput(true);
        http.setDoInput(true);
        http.connect();
        return http;
    }

    private static class MyX509TrustManager implements X509TrustManager {
        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }
    }

}
