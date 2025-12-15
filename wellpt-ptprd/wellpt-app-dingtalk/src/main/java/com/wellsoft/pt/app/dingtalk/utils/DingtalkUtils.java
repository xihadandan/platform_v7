package com.wellsoft.pt.app.dingtalk.utils;

import net.sf.json.JSONObject;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.Formatter;

/**
 * Description: 钉钉基本方法工具类
 *
 * @author Well
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年5月29日.1	Well		2020年5月29日		Create
 * </pre>
 * @date 2020年5月29日
 */
@Deprecated
public class DingtalkUtils {

    private static Logger logger = LoggerFactory.getLogger(DingtalkUtils.class);

    /**
     * 个人免密登录场景的签名计算
     *
     * @param timestamp
     * @param appSecret
     * @return
     */
    public static String sign(String timestamp, String appSecret) {
        // 根据timestamp, appSecret计算签名值
        String stringToSign = timestamp;
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(appSecret.getBytes("UTF-8"), "HmacSHA256"));
            byte[] signatureBytes = mac.doFinal(stringToSign.getBytes("UTF-8"));
            String signature = new String(Base64.encodeBase64(signatureBytes));
            String urlEncodeSignature = urlEncode(signature);
            return urlEncodeSignature;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        return "";
    }

    public static String sign(String ticket, String nonceStr, long timeStamp, String url) {
        String plain = "jsapi_ticket=" + ticket + "&noncestr=" + nonceStr + "&timestamp=" + String.valueOf(timeStamp)
                + "&url=" + url;
        try {
            MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
            sha1.reset();
            sha1.update(plain.getBytes("UTF-8"));
            return byteToHex(sha1.digest());
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        return "";
    }

    // 字节数组转化成十六进制字符串
    private static String byteToHex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }

    /**
     * url 编码
     *
     * @param value
     * @return
     */
    public static String urlEncode(String value) {
        if (value == null) {
            return "";
        }
        try {
            String encoded = URLEncoder.encode(value, "utf-8"); // encoding参数使用utf-8
            return encoded.replace("+", "%20").replace("*", "%2A").replace("~", "%7E").replace("/", "%2F");
        } catch (UnsupportedEncodingException ex) {
            throw new IllegalArgumentException(ex.getMessage(), ex);
        }
    }

    /**
     * url 解码
     *
     * @param value
     * @return
     */
    public static String urlDecode(String value) {
        if (value == null) {
            return "";
        }
        try {
            value = value.replace("%20", "+").replace("%2A", "*").replace("%7E", "~").replace("%2F", "/");
            return URLDecoder.decode(value, "utf-8"); // encoding参数使用utf-8
        } catch (UnsupportedEncodingException ex) {
            throw new IllegalArgumentException(ex.getMessage(), ex);
        }
    }

    /**
     * uri 地址构建，参数替换
     *
     * @param uri
     * @param args
     * @return
     */
    public static String uriFormat(String uri, Object... args) {
        if (StringUtils.isEmpty(uri)) {
            return "";
        }
        return String.format(uri, args);
    }

    /**
     * 转化json字符串
     *
     * @param json 接口返回json
     * @return
     */
    public static JSONObject getJsonResult(String json) {
        if (StringUtils.isBlank(json)) {
            return null;
        }
        return JSONObject.fromObject(json);
    }

    public static JSONObject checkResult(JSONObject jb) {
        int errcode = jb.getInt("errcode");
        if (errcode == 88) {
            throw new RuntimeException(String.format("sub_code:%d,sub_msg:%s。", jb.getInt("sub_code"),
                    jb.getString("sub_msg")));
        } else if (errcode != 0) {
            throw new RuntimeException(String.format("errcode:%d,errmsg:%s。", jb.getInt("errcode"),
                    jb.getString("errmsg")));
        }
        return jb;
    }

    /**
     * 数据加密密钥。用于回调数据的加密
     *
     * @param length
     * @return
     */
    public static String getAesKey(int length) {
        String aesKey = RandomStringUtils.randomAlphanumeric(length);
        return aesKey;
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println(DingtalkUtils.getAesKey(8));

        System.out.println("access_token:" + DingtalkApiUtils.getAccessToken());
    }
}
