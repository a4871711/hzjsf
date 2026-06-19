package com.dlc.common.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dlc.common.exception.RRException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 严格对齐：
 * php/extend/fast/Douyin.php
 * php/application/wxsite/controller/UserController.php :: couponQuery / couponConfirm
 */
@Component
public class DouyinOpenService {

    private static final Logger log = LoggerFactory.getLogger(DouyinOpenService.class);

    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.98 Safari/537.36";

    private static final String TOKEN_URL = "https://open.douyin.com/oauth/client_token/";
    private static final String PREPARE_URL = "https://open.douyin.com/goodlife/v1/fulfilment/certificate/prepare/";
    private static final String VERIFY_URL = "https://open.douyin.com/goodlife/v1/fulfilment/certificate/verify/";
    private static final String CANCEL_URL = "https://open.douyin.com/goodlife/v1/fulfilment/certificate/cancel/";

    private static final long TOKEN_CACHE_MS = 7000L * 1000L;

    private static final int ERR_CODE = -99;

    @Value("${douyin.clientKey}")
    private String clientKey;

    @Value("${douyin.clientSecret}")
    private String clientSecret;

    private volatile String accessToken;
    private volatile long accessTokenExpireAt;

    /** Douyin::getAccessToken */
    public synchronized String getAccessToken() {
        if (StringUtils.isNotBlank(accessToken) && System.currentTimeMillis() < accessTokenExpireAt) {
            log.info("抖音 access_token 使用缓存, clientKey={}", maskClientKey(clientKey));
            return accessToken;
        }
        Map<String, String> form = new LinkedHashMap<String, String>();
        form.put("client_key", clientKey);
        form.put("client_secret", clientSecret);
        form.put("grant_type", "client_credential");

        log.info("抖音 client_token 请求: POST {}", TOKEN_URL);
        String result = httpPostForm(TOKEN_URL, httpBuildQuery(form));
        log.info("抖音 client_token 响应: {}", result);

        if (StringUtils.isBlank(result)) {
            throw new RRException("网络请求失败", ERR_CODE);
        }
        JSONObject json = JSONObject.parseObject(result);
        if (json == null) {
            throw new RRException("网络请求失败", ERR_CODE);
        }
        JSONObject data = json.getJSONObject("data");
        if (data == null || StringUtils.isBlank(data.getString("access_token"))) {
            String desc = data != null ? data.getString("description") : null;
            throw new RRException(StringUtils.isNotBlank(desc) ? desc : "网络请求失败", ERR_CODE);
        }
        accessToken = data.getString("access_token");
        accessTokenExpireAt = System.currentTimeMillis() + TOKEN_CACHE_MS;
        return accessToken;
    }

    /**
     * UserController::couponQuery 里的 prepare 逻辑
     * 短链：HEAD 跳转取 object_id；其它：明文 code
     */
    public DouyinPrepareResult prepare(String couponCode) {
        if (StringUtils.isBlank(couponCode)) {
            throw new RRException("参数错误", ERR_CODE);
        }
        String code = couponCode.trim();
        log.info("抖音验券准备开始 couponCode={}", code);

        String plainCode = "";
        String encryptedData = "";
        if (code.toLowerCase().contains("https://v.douyin.com/")) {
            encryptedData = resolveObjectIdByShortUrl(code);
            log.info("抖音短链 object_id 长度={}", encryptedData.length());
        } else {
            plainCode = code;
        }

        JSONObject data = douyinPrepare(plainCode, encryptedData);

        DouyinPrepareResult result = new DouyinPrepareResult();
        String verifyToken = data.getString("verify_token");
        if (StringUtils.isNotBlank(verifyToken)) {
            result.setVerifyToken(verifyToken);
        }

        JSONObject cert = pickFirstCertificate(data);
        if (cert == null) {
            throw new RRException("抖音验券准备失败：无可用券码", ERR_CODE);
        }
        String encryptedCode = cert.getString("encrypted_code");
        if (StringUtils.isBlank(encryptedCode)) {
            throw new RRException("抖音验券准备失败：无可用券码", ERR_CODE);
        }
        result.setEncryptedCode(encryptedCode);

        JSONObject sku = cert.getJSONObject("sku");
        if (sku != null) {
            String skuId = sku.getString("sku_id");
            if (StringUtils.isNotBlank(skuId)) {
                result.setSkuId(skuId);
            }
            String productId = sku.getString("product_id");
            if (StringUtils.isNotBlank(productId)) {
                result.setProductId(productId);
            }
        }
        log.info("抖音验券准备成功 skuId={}, productId={}", result.getSkuId(), result.getProductId());
        return result;
    }

    /** 对齐 PHP：certificates[0]；无则取 certificates_v2[0] */
    private JSONObject pickFirstCertificate(JSONObject data) {
        JSONArray certificates = data.getJSONArray("certificates");
        if (certificates != null && !certificates.isEmpty()) {
            return certificates.getJSONObject(0);
        }
        JSONArray certificatesV2 = data.getJSONArray("certificates_v2");
        if (certificatesV2 != null && !certificatesV2.isEmpty()) {
            return certificatesV2.getJSONObject(0);
        }
        return null;
    }

    /**
     * 验券核销（须与 prepare 返回的 verify_token 配对）
     */
    public DouyinVerifyResult verify(DouyinPrepareResult prepare, String poiId) {
        if (prepare == null || StringUtils.isAnyBlank(prepare.getEncryptedCode(), poiId)) {
            throw new RRException("参数错误", ERR_CODE);
        }
        JSONObject body = new JSONObject();
        String verifyToken = prepare.getVerifyToken();
        body.put("verify_token", StringUtils.isNotBlank(verifyToken) ? verifyToken : guid());
        body.put("poi_id", poiId);
        JSONArray encryptedCodes = new JSONArray();
        encryptedCodes.add(prepare.getEncryptedCode());
        body.put("encrypted_codes", encryptedCodes);

        log.info("抖音验券核销开始 poiId={}", poiId);
        JSONObject data = douyinVerify(body.toJSONString());
        JSONArray verifyResults = data.getJSONArray("verify_results");
        if (verifyResults == null || verifyResults.isEmpty()) {
            throw new RRException("抖音验券失败", ERR_CODE);
        }
        JSONObject item = verifyResults.getJSONObject(0);
        if (item == null || item.getIntValue("result") != 0) {
            String msg = item != null ? item.getString("msg") : null;
            throw new RRException(StringUtils.isNotBlank(msg) ? msg : "抖音验券失败", ERR_CODE);
        }
        DouyinVerifyResult result = new DouyinVerifyResult();
        result.setVerifyId(item.getString("verify_id"));
        result.setCertificateId(item.getString("certificate_id"));
        log.info("抖音验券核销成功 poiId={}, verifyId={}", poiId, result.getVerifyId());
        return result;
    }

    /** 撤销核销（仅发起请求并记录日志，不校验平台返回结果） */
    public void cancelVerify(String certificateId, String verifyId) {
        if (StringUtils.isAnyBlank(certificateId, verifyId)) {
            log.warn("抖音撤销核销跳过：certificate_id 或 verify_id 为空");
            return;
        }
        try {
            JSONObject body = new JSONObject();
            body.put("certificate_id", certificateId);
            body.put("verify_id", verifyId);
            log.info("抖音撤销核销请求 certificateId={}, verifyId={}", certificateId, verifyId);
            String result = httpPostJsonWithToken(CANCEL_URL, body.toJSONString());
            log.info("抖音撤销核销响应: {}", result);
        } catch (Exception e) {
            log.warn("抖音撤销核销请求异常 certificateId={}, verifyId={}", certificateId, verifyId, e);
        }
    }

    /** Douyin::prepare */
    private JSONObject douyinPrepare(String code, String encryptedData) {
        Map<String, String> params = new LinkedHashMap<String, String>();
        if (StringUtils.isNotBlank(code)) {
            params.put("code", code);
        }
        if (StringUtils.isNotBlank(encryptedData)) {
            params.put("encrypted_data", encryptedData);
        }
        String url = PREPARE_URL + "?" + httpBuildQuery(params);
        log.info("抖音验券准备请求: {}", url.replaceAll("encrypted_data=[^&]+", "encrypted_data=***"));
        String result = httpGetWithToken(url);
        log.info("抖音验券准备响应: {}", result);
        return parseDataOrThrow(result);
    }

    /** Douyin::verify POST */
    private JSONObject douyinVerify(String jsonBody) {
        log.info("抖音验券核销请求: POST {}", VERIFY_URL);
        String result = httpPostJsonWithToken(VERIFY_URL, jsonBody);
        log.info("抖音验券核销响应: {}", result);
        return parseDataOrThrow(result);
    }

    private JSONObject parseDataOrThrow(String result) {
        if (StringUtils.isBlank(result)) {
            throw new RRException("网络请求失败", ERR_CODE);
        }
        JSONObject json = JSONObject.parseObject(result);
        if (json == null) {
            throw new RRException("网络请求失败", ERR_CODE);
        }
        JSONObject data = json.getJSONObject("data");
        if (data == null) {
            throw new RRException("网络请求失败", ERR_CODE);
        }
        if (data.getIntValue("error_code") != 0) {
            String desc = data.getString("description");
            throw new RRException(StringUtils.isNotBlank(desc) ? desc : "请求失败", ERR_CODE);
        }
        return data;
    }

    /**
     * UserController::couponQuery 短链段
     * CURLOPT_NOBODY + FOLLOWLOCATION -> CURLINFO_EFFECTIVE_URL -> parse_str object_id
     */
    private String resolveObjectIdByShortUrl(String shortUrl) {
        try {
            log.info("抖音短链解析开始: {}", shortUrl);
            HttpURLConnection conn = openConnection(shortUrl);
            conn.setInstanceFollowRedirects(true);
            conn.setRequestProperty("User-Agent", USER_AGENT);
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(15000);
            conn.setRequestMethod("HEAD");
            conn.getResponseCode();
            String effectiveUrl = conn.getURL().toString();
            conn.disconnect();
            log.info("抖音短链跳转最终 URL: {}", effectiveUrl);

            String objectId = parseQueryParam(effectiveUrl, "object_id");
            if (StringUtils.isBlank(objectId)) {
                throw new RRException("无法解析抖音券码", ERR_CODE);
            }
            return objectId;
        } catch (RRException e) {
            throw e;
        } catch (Exception e) {
            log.warn("抖音短链解析失败: {}", shortUrl, e);
            throw new RRException("无法解析抖音券码", ERR_CODE);
        }
    }

    private static String parseQueryParam(String url, String name) {
        try {
            int q = url.indexOf('?');
            if (q < 0) {
                return null;
            }
            for (String part : url.substring(q + 1).split("&")) {
                int eq = part.indexOf('=');
                if (eq > 0 && name.equals(part.substring(0, eq))) {
                    return URLDecoder.decode(part.substring(eq + 1), StandardCharsets.UTF_8.name());
                }
            }
            return null;
        } catch (Exception e) {
            throw new RRException("无法解析抖音券码", ERR_CODE);
        }
    }

    private static String guid() {
        String charid = UUID.randomUUID().toString().replace("-", "");
        return charid.substring(0, 8) + "-"
                + charid.substring(8, 12) + "-"
                + charid.substring(12, 16) + "-"
                + charid.substring(16, 20) + "-"
                + charid.substring(20, 32);
    }

    private static String httpBuildQuery(Map<String, String> params) {
        try {
            StringBuilder sb = new StringBuilder();
            boolean first = true;
            for (Map.Entry<String, String> e : params.entrySet()) {
                if (StringUtils.isBlank(e.getValue())) {
                    continue;
                }
                if (!first) {
                    sb.append("&");
                }
                first = false;
                sb.append(URLEncoder.encode(e.getKey(), "UTF-8"));
                sb.append("=");
                sb.append(URLEncoder.encode(e.getValue(), "UTF-8"));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RRException("网络请求失败", ERR_CODE);
        }
    }

    private String httpPostForm(String url, String formBody) {
        try {
            HttpURLConnection conn = openConnection(url);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("User-Agent", USER_AGENT);
            conn.setDoOutput(true);
            conn.getOutputStream().write(formBody.getBytes(StandardCharsets.UTF_8));
            return readResponse(conn);
        } catch (RRException e) {
            throw e;
        } catch (Exception e) {
            log.warn("抖音 POST 表单失败: {}", url, e);
            throw new RRException("网络请求失败", ERR_CODE);
        }
    }

    private String httpGetWithToken(String url) {
        try {
            HttpURLConnection conn = openConnection(url);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", USER_AGENT);
            conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            conn.setRequestProperty("access-token", getAccessToken());
            return readResponse(conn);
        } catch (RRException e) {
            throw e;
        } catch (Exception e) {
            log.warn("抖音 GET 失败: {}", url, e);
            throw new RRException("网络请求失败", ERR_CODE);
        }
    }

    private String httpPostJsonWithToken(String url, String jsonBody) {
        try {
            HttpURLConnection conn = openConnection(url);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("User-Agent", USER_AGENT);
            conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            conn.setRequestProperty("access-token", getAccessToken());
            conn.setDoOutput(true);
            conn.getOutputStream().write(jsonBody.getBytes(StandardCharsets.UTF_8));
            return readResponse(conn);
        } catch (RRException e) {
            throw e;
        } catch (Exception e) {
            log.warn("抖音 POST JSON 失败: {}", url, e);
            throw new RRException("网络请求失败", ERR_CODE);
        }
    }

    private String readResponse(HttpURLConnection conn) throws Exception {
        int code = conn.getResponseCode();
        InputStream is = code >= 400 ? conn.getErrorStream() : conn.getInputStream();
        if (is == null) {
            conn.disconnect();
            throw new RRException("网络请求失败", ERR_CODE);
        }
        byte[] buf = new byte[4096];
        StringBuilder sb = new StringBuilder();
        int n;
        while ((n = is.read(buf)) != -1) {
            sb.append(new String(buf, 0, n, StandardCharsets.UTF_8));
        }
        is.close();
        conn.disconnect();
        return sb.toString();
    }

    private HttpURLConnection openConnection(String urlStr) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection conn;
        if (urlStr.toLowerCase().startsWith("https://")) {
            HttpsURLConnection https = (HttpsURLConnection) url.openConnection();
            SSLContext ssl = SSLContext.getInstance("TLS");
            ssl.init(null, new TrustManager[]{new MyX509TrustManager()}, new SecureRandom());
            https.setSSLSocketFactory(ssl.getSocketFactory());
            https.setHostnameVerifier((h, s) -> true);
            conn = https;
        } else {
            conn = (HttpURLConnection) url.openConnection();
        }
        return conn;
    }

    private String maskClientKey(String key) {
        if (StringUtils.isBlank(key) || key.length() <= 4) {
            return "****";
        }
        return "****" + key.substring(key.length() - 4);
    }

    /** 抖音专用字段 douyin_sku_id，sku_id / product_id 有值才参与，null 或空忽略 */
    public static boolean douyinSkuMatches(String douyinSkuId, String skuId, String productId) {
        if (StringUtils.isBlank(douyinSkuId)) {
            return false;
        }
        String configured = douyinSkuId.trim();
        if (StringUtils.isNotBlank(skuId) && configured.equals(skuId.trim())) {
            return true;
        }
        if (StringUtils.isNotBlank(productId) && configured.equals(productId.trim())) {
            return true;
        }
        return false;
    }

    /** 美团 goodsId，sku_id / product_id 有值才参与匹配，null 或空忽略 */
    public static boolean goodsIdMatches(Long goodsId, String skuId, String productId) {
        if (goodsId == null) {
            return false;
        }
        if (StringUtils.isNotBlank(skuId) && goodsIdEquals(goodsId, skuId.trim())) {
            return true;
        }
        if (StringUtils.isNotBlank(productId) && goodsIdEquals(goodsId, productId.trim())) {
            return true;
        }
        return false;
    }

    private static boolean goodsIdEquals(Long goodsId, String idStr) {
        return goodsId != null && idStr != null && String.valueOf(goodsId).equals(idStr);
    }
}
