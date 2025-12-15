package com.wellsoft.pt.message.sms.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.message.sms.MoType;
import com.wellsoft.pt.message.sms.RptType;
import com.wellsoft.pt.message.sms.SmsClientApi;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Description: 腾讯云服务短信
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2024年08月14日   chenq	 Create
 * </pre>
 */
@Component
public class TencentCloudSmsClientApi extends SmsClientApi {

    @Override
    public int sendSM(String[] mobiles, String content, long smID, long srcID, String url, String sendTime) {

        HttpPost post = new HttpPost();
        CloseableHttpClient client = HttpClients.createDefault();
        try {

            post.setHeader("Content-type", ContentType.APPLICATION_JSON.toString());
            post.setHeader("X-TC-Action", "SendSms");
            post.setHeader("X-TC-Version", "2021-01-11");
            post.setHeader("X-TC-Region", "ap-guangzhou");


//
//            {
//                "PhoneNumberSet": [
//                "+8618511122233",
//                        "+8618511122266"
//    ],
//                "SmsSdkAppId": "1400006666",
//                    "SignName": "腾讯云",
//                    "TemplateId": "1110",
//                    "TemplateParamSet": [
//                "1234"
//    ],
//                "SessionContext": "test"
//            }
//
//
            Map<String, Object> body = Maps.newHashMap();
            List<String> formatMobiles = Lists.newArrayList();
            for (String m : mobiles) {
                formatMobiles.add(m.startsWith("+86") ? m : "+86" + m);
            }
            body.put("PhoneNumberSet", formatMobiles);
            body.put("SmsSdkAppId", "1400930924");
            body.put("SignName", "威尔信息技术有限公司");
            body.put("TemplateId", "2240350");
            body.put("TemplateParamSet", Lists.newArrayList("123456"));
            post.setEntity(new StringEntity(JsonUtils.object2Gson(body), ContentType.APPLICATION_JSON));
            CloseableHttpResponse response = client.execute(new HttpHost("sms.tencentcloudapi.com"), post);

            HttpEntity responseEntity = response.getEntity();
            if (responseEntity != null) {
                String responseStr = EntityUtils.toString(responseEntity, "UTF-8");
                logger.info("腾讯云发送短信反馈: {}", responseStr);
            }
            EntityUtils.consume(responseEntity);
        } catch (Exception e) {
            logger.error("腾讯云发送短信异常: ", e);
        } finally {
            HttpClientUtils.closeQuietly(client);
        }
        return 0;
    }

    @Override
    public MoType[] receiveSM(long srcID, int amount) {
        return new MoType[0];
    }

    @Override
    public RptType[] receiveRPT(long smID, int amount) {
        return new RptType[0];
    }

    @Override
    public int getType() {
        return 0;
    }
}
