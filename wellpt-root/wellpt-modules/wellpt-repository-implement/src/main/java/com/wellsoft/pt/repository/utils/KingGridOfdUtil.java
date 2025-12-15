package com.wellsoft.pt.repository.utils;

import com.alibaba.fastjson.JSON;
import com.wellsoft.pt.repository.vo.OfdParam;
import com.wellsoft.pt.repository.vo.OfdToken;
import com.wellsoft.pt.repository.vo.PreviewParam;
import com.wellsoft.pt.repository.vo.PreviewResult;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author linzc
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期        修改内容
 * 2021/9/18.1    linzc       2021/9/18     Create
 * </pre>
 */
public class KingGridOfdUtil {

    public static OfdToken getToken(String appKey, String appSecret, String url) {
        String bodyJson = "{\"app_key\": \"" + appKey + "\",\"app_secret\": \"" + appSecret + "\"}";
        String result = doPost(url + "/api/v1/token", null, bodyJson);
        OfdToken ofdToken = JSON.parseObject(result, OfdToken.class);
        if (ofdToken != null && ofdToken.getCode() != null && ofdToken.getCode() == 0) {
            return ofdToken;
        }
        throw new RuntimeException(result);
    }

    public static InputStream pdf2ofd(OfdParam param) throws Exception {
        return getCloseableHttpResponse(param, "/api/v1/pdf2ofd");
    }

    public static InputStream ofd2pdf(OfdParam param) throws Exception {
        return getCloseableHttpResponse(param, "/api/v1/ofd2pdf");
    }

    public static InputStream office2pdf(OfdParam param) throws Exception {
        return getCloseableHttpResponse(param, "/api/office/pdf");
    }

    public static InputStream office2ofd(OfdParam param) throws Exception {
        return getCloseableHttpResponse(param, "/api/office/ofd");
    }

    public static PreviewResult preview(PreviewParam previewParam) throws Exception {
        CloseableHttpClient httpClient = HttpClients.createDefault();//1、创建实例
        HttpPost uploadFile = new HttpPost(previewParam.getUrl() + "/api/preview/getUrl");//2、创建请求
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addTextBody("type", previewParam.getType().toUpperCase(), ContentType.TEXT_PLAIN);//传参
        setIsNotNull(builder, previewParam);
        // 把文件加到HTTP的post请求中
        builder.addBinaryBody("file", previewParam.getFileInputStream(), ContentType.APPLICATION_OCTET_STREAM, previewParam.getFileName());
        HttpEntity multipart = builder.build();
        uploadFile.setEntity(multipart);//对于HttpPost对象而言，可调用setEntity(HttpEntity entity)方法来设置请求参数。
        CloseableHttpResponse response = httpClient.execute(uploadFile);
        int code = response.getStatusLine().getStatusCode();
        String result = readResultString(response);
        if (code == 200) {    //请求成功
            PreviewResult previewResult = JSON.parseObject(result, PreviewResult.class);
            if ("0".equals(previewResult.getCode())) {
                return previewResult;
            }
        }
        throw new RuntimeException(result);
    }

    private static void setIsNotNull(MultipartEntityBuilder builder, PreviewParam param) {
        builder.addTextBody("officeDocConvertType", param.getOfficeDocConvertType() == null ? "pdf" : param.getOfficeDocConvertType(), ContentType.TEXT_PLAIN);//传参
        if (param.getMarker() != null) {
            builder.addTextBody("marker", param.getMarker(), ContentType.TEXT_PLAIN);//文档添加的水印文字
        }
        if (param.getShowSignatureBtn() != null) {
            builder.addTextBody("showSignatureBtn", param.getShowSignatureBtn(), ContentType.TEXT_PLAIN);//true/false 是否显示签章功能 (如果不传，则使用配置文件里面的默认配置)
        }
        if (param.getShowDownloadBtn() != null) {
            builder.addTextBody("showDownloadBtn", param.getShowDownloadBtn(), ContentType.TEXT_PLAIN);//true/false 是否显示下载按钮 (如果不传，则使用配置文件里面的默认配置)
        }
        if (param.getShowPrintBtn() != null) {
            builder.addTextBody("showPrintBtn", param.getShowPrintBtn(), ContentType.TEXT_PLAIN);//true/false 是否显示打印按钮 (如果不传，则使用配置文件里面的默认配置)
        }

        if (param.getUserId() != null) {
            builder.addTextBody("userId", param.getUserId(), ContentType.TEXT_PLAIN);//sealOrigin = 0 时才需要配置用户标识、keySN，CA0版或者手机版才需要用到
        }

        if (param.getUserCode() != null) {
            builder.addTextBody("userCode", param.getUserCode(), ContentType.TEXT_PLAIN);//用户编码
        }

        if (param.getSealOrigin() != null) {
            builder.addTextBody("sealOrigin", param.getSealOrigin(), ContentType.TEXT_PLAIN);//印章来源 签章服务器：0、 硬件密钥盘(客户端)：1  默认：0
        }

        if (param.getCertOrigin() != null) {
            builder.addTextBody("certOrigin", param.getCertOrigin(), ContentType.TEXT_PLAIN);//证书来源 不做签名：-1（电子签章）、签章服务器：0、硬件密钥盘：1、密码机等：2 默认：0
        }
        builder.setCharset(StandardCharsets.UTF_8);//设置请求的编码格式
    }

    private static InputStream getCloseableHttpResponse(OfdParam param, String uri) throws IOException {
        OfdToken token = getToken(param.getAppKey(), param.getAppSecret(), param.getUrl());
        CloseableHttpClient httpClient = HttpClients.createDefault();//1、创建实例
        HttpPost uploadFile = new HttpPost(param.getUrl() + uri);//2、创建请求
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addTextBody("token", token.getToken(), ContentType.TEXT_PLAIN);//传参
        builder.addTextBody("business_id", param.getBusinessId(), ContentType.TEXT_PLAIN);//传参
        builder.addTextBody("business_name", param.getBusinessName(), ContentType.TEXT_PLAIN);//传参
        if (param.getStampConvertMethod() != null) {
            builder.addTextBody("stampConvertMethod", param.getStampConvertMethod(), ContentType.TEXT_PLAIN);//传参
        }
        if (param.getFontMethod() != null) {
            builder.addTextBody("fontMethod", param.getFontMethod(), ContentType.TEXT_PLAIN);//传参
        }
        builder.setCharset(StandardCharsets.UTF_8);//设置请求的编码格式
        // 把文件加到HTTP的post请求中
        builder.addBinaryBody("file", param.getFileInputStream(), ContentType.APPLICATION_OCTET_STREAM, param.getFileName());
        HttpEntity multipart = builder.build();
        uploadFile.setEntity(multipart);//对于HttpPost对象而言，可调用setEntity(HttpEntity entity)方法来设置请求参数。
        CloseableHttpResponse response = httpClient.execute(uploadFile);
        int code = response.getStatusLine().getStatusCode();
        if (code == 200) {    //请求成功
            return response.getEntity().getContent();
        } else {
            String result = readResultString(response);
            throw new RuntimeException(result);
        }
    }


    /**
     * post请求(用于key-value格式的参数)
     *
     * @param url
     * @param params
     */
    private static String doPost(String url, Map<String, String> params, String bodyJson) {

        try {
            // 定义HttpClient
            HttpClient client = HttpClients.createDefault();
            // 实例化HTTP方法
            HttpPost request = new HttpPost();
            request.setHeader("content-type", "application/json");
            request.setURI(new URI(url));
            if (params != null && !params.isEmpty()) {
                //设置参数
                List<NameValuePair> nvps = new ArrayList<>();
                for (String name : params.keySet()) {
                    String value = String.valueOf(params.get(name));
                    nvps.add(new BasicNameValuePair(name, value));
                }
                request.setEntity(new UrlEncodedFormEntity(nvps, StandardCharsets.UTF_8));
            }

            if (bodyJson != null) {
                request.setEntity(new StringEntity(bodyJson, StandardCharsets.UTF_8));
            }

            HttpResponse response = client.execute(request);
            int code = response.getStatusLine().getStatusCode();
            if (code == 200) {    //请求成功
                return readResultString(response);
            } else {    //
                System.out.println(readResultString(response));
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    private static String readResultString(HttpResponse response) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        String line;
        String NL = System.getProperty("line.separator");
        while ((line = in.readLine()) != null) {
            sb.append(line).append(NL);
        }
        in.close();
        return sb.toString();
    }

    /**
     * @param is
     * @param out
     */
    public static void output(InputStream is, OutputStream out) {
        try {
            int len = -1;
            byte[] b = new byte[102400];
            while ((len = is.read(b)) != -1) {
                out.write(b, 0, len);
            }
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {

                }
            }
        }
    }
}
