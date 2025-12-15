package com.wellsoft.pt.message.sms.impl;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.message.dao.ShortMessageDao;
import com.wellsoft.pt.message.entity.ShortMessage;
import com.wellsoft.pt.message.service.ShortMessageService;
import com.wellsoft.pt.message.sms.*;
import net.sf.json.JSONObject;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@Component
public class CloudMasSmsClientApiImpl extends SmsClientApi {

    public CloudMasSmsClientApiImpl() {
        super();
    }

    public CloudMasSmsClientApiImpl(String cloudMasHttp, String ecName, String apId,
                                    String secretKey, String sign,
                                    String addSerial) {
        super(cloudMasHttp, ecName, apId, secretKey, sign, addSerial);
    }

    /**
     * md5(32位小写)
     */
    private static String md5(String str) {
        MessageDigest md5;
        StringBuffer buf = new StringBuffer("");
        try {
            md5 = MessageDigest.getInstance("MD5");
            md5.update((str).getBytes("UTF-8"));
            byte b[] = md5.digest();
            int i;
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0) {
                    i += 256;
                }
                if (i < 16) {
                    buf.append("0");
                }
                buf.append(Integer.toHexString(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return buf.toString();
    }

    public static String sendPost(String url, String encode) {
        DefaultHttpClient httpClient = new DefaultHttpClient();

        HttpPost method = new HttpPost(url);
        StringEntity entity = new StringEntity(encode, "utf-8");//解决中文乱码问题
        entity.setContentEncoding("UTF-8");
        entity.setContentType("application/json");
        method.setEntity(entity);
        String resData = "";
        try {
            HttpResponse result = httpClient.execute(method);
            resData = EntityUtils.toString(result.getEntity());
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resData;
    }

    /**
     * 使用URLConnection发送post
     */
    @SuppressWarnings("null")
    public static String sendPost2(String urlParam, Map<String, Object> params) {
        StringBuffer resultBuffer = null;
        // 构建请求参数
        StringBuffer sbParams = new StringBuffer();
        if (params != null && params.size() > 0) {
            for (Entry<String, Object> e : params.entrySet()) {
                sbParams.append(e.getKey());
                sbParams.append("=");
                sbParams.append(e.getValue());
                sbParams.append("&");
            }
        }
        URLConnection con = null;
        OutputStreamWriter osw = null;
        BufferedReader br = null;
        try {
            URL realUrl = new URL(urlParam);
            // 打开和URL之间的连接
            con = realUrl.openConnection();
            // 设置通用的请求属性
            con.setRequestProperty("accept", "*/*");
            con.setRequestProperty("connection", "Keep-Alive");
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            con.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 发送POST请求必须设置如下两行
            con.setDoOutput(true);
            con.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            osw = new OutputStreamWriter(con.getOutputStream());
            if (sbParams != null && sbParams.length() > 0) {
                // 发送请求参数
                osw.write(sbParams.substring(0, sbParams.length() - 1));
                // flush输出流的缓冲
                osw.flush();
            }
            // 定义BufferedReader输入流来读取URL的响应
            resultBuffer = new StringBuffer();
            int contentLength = Integer.parseInt(con.getHeaderField("Content-Length"));
            if (contentLength > 0) {
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String temp;
                while ((temp = br.readLine()) != null) {
                    resultBuffer.append(temp);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (osw != null) {
                try {
                    osw.close();
                } catch (IOException e) {
                    osw = null;
                    throw new RuntimeException(e);
                }
            }
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    br = null;
                    throw new RuntimeException(e);
                }
            }
        }
        return resultBuffer.toString();
    }

    @Override
    public int sendSM(String[] mobiles, String content, long smID, long srcID, String url,
                      String sendTime) {
        logger.info(
                "sendSM(String[] mobiles={}, String content={}, long smID={}, long srcID={}, String url={}, String sendTime={}) - start",
                new Object[]{mobiles, content, smID, srcID, url, sendTime});

        CloudMasReq req = new CloudMasReq();
        req.setEcName(apiCode);
        req.setApId(username);
        req.setSecretKey(password);
        String mobiless = StringUtils.join(mobiles, ",");
        req.setMobiles(mobiless);
        req.setContent(content);
        req.setSign(dbname);
        StringBuffer sb = new StringBuffer();
        sb.append(apiCode).append(username).append(password).append(mobiless).append(
                content).append(dbname);
        if (StringUtils.isNotBlank(clientBean)) {
            req.setAddSerial(clientBean);
            sb.append(clientBean);
        }
        //md5
        req.setMac(md5(sb.toString()));
        String reqText = JSONObject.fromObject(req).toString();
        //base64
        String encode = Base64.encodeBase64String(reqText.getBytes());

        //Map<String, Object> params = new HashMap<String, Object>();
        //params.put("TYPE", encode);
        //String rspStr = sendPost(service, params);
        String rspStr = sendPost(service, encode);
        CloudMasRsp rsp = (CloudMasRsp) JSONObject.toBean(JSONObject.fromObject(rspStr),
                CloudMasRsp.class);

        int sendResult = IM_SEND_FAIL;
        if (rsp.isSuccess()) {
            sendResult = IM_SEND_SUCC;
            List<ShortMessage> shortsms = new ArrayList<ShortMessage>();
            ShortMessageDao shortMessageDao = ApplicationContextHolder.getBean(
                    ShortMessageDao.class);
            ShortMessageService shortMessageService = ApplicationContextHolder.getBean(
                    ShortMessageService.class);
            for (String mobile : mobiles) {
                ShortMessage sm = shortMessageDao.getObjBySmidAndRecPhone(smID, mobile, 3, 0);
                sm.setMsgGroup(rsp.getMsgGroup());
                shortsms.add(sm);
            }
            shortMessageService.saveShortMessage(shortsms);
        }

        logger.info("sendSM(String[], String, long, long, String, String) - end - return value={}",
                sendResult);
        return sendResult;
    }

    @Override
    public MoType[] receiveSM(long srcID, int amount) {
        return null;
    }

    @Override
    public RptType[] receiveRPT(long smID, int amount) {
        List<RptType> rptResult = new ArrayList<RptType>();
        RptType[] rptType = new RptType[0];
        RptType[] returnRptTypeArray = rptResult.toArray(rptType);
        return returnRptTypeArray;
    }

    @Override
    public int getType() {
        return MSG_TYPE_CLOUDMAS;
    }
}
