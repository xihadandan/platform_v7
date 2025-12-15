package com.wellsoft.pt.log;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.junit.Test;
import sun.misc.BASE64Encoder;

import java.io.File;
import java.io.FileInputStream;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2020年09月05日   chenq	 Create
 * </pre>
 */
public class AttachmentTest {

    @Test
    public void test() throws Exception {
        HttpClient client = new HttpClient();
        String base64 = new BASE64Encoder().encode(IOUtils.toByteArray(new FileInputStream(new File("D:\\Download\\测试文档.txt"))));
        PostMethod method = new PostMethod("http://localhost:9200/welldoc/attachment");
        method.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");
        JSONObject json = new JSONObject();
        json.put("attachment", base64);
        json.put("filename", "测试文档.txt");
        method.setRequestBody(json.toString());

        client.executeMethod(method);
        System.out.println(method.getResponseBodyAsString());
    }
}
