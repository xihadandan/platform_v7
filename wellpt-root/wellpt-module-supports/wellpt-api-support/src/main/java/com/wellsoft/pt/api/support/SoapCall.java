package com.wellsoft.pt.api.support;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;

import javax.xml.namespace.QName;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Description:
 *
 * @author chenq
 * @date 2018/11/7
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/11/7    chenq		2018/11/7		Create
 * </pre>
 */
public class SoapCall {

    private SoapCall() {
    }

    public static HttpBuilder buildHttp() {
        return new HttpBuilder();
    }

    public static AxisBuilder buildAxis() {
        return new AxisBuilder();
    }


    public static class AxisBuilder {

        private ServiceClient sender = null;

        private Options options = new Options();

        private OMFactory omFactory = null;

        private String endpoint;

        private boolean enableMTOM = true;

        private OMElement responseElement;

        private OMElement body;

        private OMElement operationElement;

        private int readTimeout = 60000;


        protected AxisBuilder() {
            try {
                this.sender = new ServiceClient();
                this.omFactory = OMAbstractFactory.getOMFactory();
            } catch (Exception e) {
            }
        }

        /**
         * 设置axis选项
         *
         * @param options
         * @return
         */
        public AxisBuilder options(Options options) {
            this.options = options;
            return this;
        }

        /**
         * 端点地址
         *
         * @param endpoint
         * @return
         */
        public AxisBuilder endpoint(String endpoint) {
            this.endpoint = endpoint;
            return this;
        }

        /**
         * 是否开启mtom传输
         *
         * @param mtom
         * @return
         */
        public AxisBuilder mtom(boolean mtom) {
            this.enableMTOM = mtom;
            return this;
        }

        /**
         * 设置超时时间
         *
         * @param readTimeout
         * @return
         */
        public AxisBuilder readTimeout(int readTimeout) {
            this.readTimeout = readTimeout;
            return this;
        }

        /**
         * 请求操作方法
         *
         * @param operationName
         * @param namespace
         * @return
         */
        public AxisBuilder operation(String operationName, String namespace) {
            OMNamespace omNs = omFactory.createOMNamespace(namespace, "");
            operationElement = omFactory.createOMElement(operationName, omNs);
            return this;
        }

        /**
         * 输入值
         *
         * @param qName
         * @param value
         * @return
         */
        public AxisBuilder input(QName qName, String value) {
            if (this.operationElement != null) {
                OMElement element = omFactory.createOMElement(qName);
                element.setText(value);
                this.operationElement.addChild(element);
            }
            return this;
        }

        /**
         * 输入值
         *
         * @param element
         * @return
         */
        public AxisBuilder input(OMElement element) {
            if (this.operationElement != null) {
                this.operationElement.addChild(element);
            }
            return this;
        }


        /**
         * 设置请求体
         *
         * @param body
         * @return
         */
        public AxisBuilder body(OMElement body) {
            this.operationElement = body;
            return this;
        }


        /**
         * 请求webservice
         *
         * @return
         * @throws Exception
         */
        public OMElement post() throws Exception {
            options.setTo(
                    new EndpointReference(this.endpoint));
            if (this.enableMTOM) {
                options.setProperty(Constants.Configuration.ENABLE_MTOM, Constants.VALUE_TRUE);
            }
            options.setProperty(HTTPConstants.SO_TIMEOUT, this.readTimeout);
            sender.setOptions(options);
            if (this.operationElement != null) {
                this.operationElement.build();
            }
            responseElement = sender.sendReceive(
                    this.operationElement);
            return responseElement;
        }


    }


    public static class HttpBuilder {

        private String endpoint; //端点地址

        private String contentType = "text/xml;charset=utf-8";

        private boolean doInput = true; //是否有输入参数

        private boolean doOutput = true;//是否有输出参数

        private String requestMethod = "POST";

        private int readTimeout = 60000;//连接超时时间，默认60秒

        private String body; //soapbody

        protected HttpBuilder() {

        }

        public HttpBuilder body(String body) {
            this.body = body;
            return this;
        }

        public HttpBuilder endpoint(String endpoint) {
            this.endpoint = endpoint;
            return this;
        }


        public HttpBuilder doInput(boolean doInput) {
            this.doInput = doInput;
            return this;
        }

        public HttpBuilder doOutput(boolean doOutput) {
            this.doOutput = doOutput;
            return this;
        }

        public HttpBuilder contentType(String contentType) {
            this.contentType = contentType;
            return this;
        }

        public HttpBuilder readTimeout(int readTimeout) {
            this.readTimeout = readTimeout;
            return this;
        }

        private String soapWrapper(String body) {
            return "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
                    + "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
                    + "<soap:Body>"
                    + body
                    + "</soap:Body>"
                    + "</soap:Envelope>";
        }

        private InputStream invoke() throws Exception {
            HttpURLConnection connection = this.connection();
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                return connection.getInputStream();
            } else {
                //异常处理
                throw new RuntimeException(
                        IOUtils.toString(connection.getErrorStream(), Charsets.UTF_8));
            }
        }

        private HttpURLConnection connection() throws Exception {
            URL url = new URL(this.endpoint);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(this.requestMethod);
            connection.setRequestProperty("content-type", this.contentType);
            connection.setDoInput(this.doInput);
            connection.setDoOutput(this.doOutput);
            connection.setReadTimeout(this.readTimeout);

            OutputStream os = connection.getOutputStream();

            os.write(soapWrapper(this.body).getBytes());
            return connection;
        }


        public String post() throws Exception {
            return IOUtils.toString(invoke(), Charsets.UTF_8);
        }

        public InputStream inputStream() throws Exception {
            return invoke();
        }

        public CloseableHttpResponse response() throws Exception {
            // 创建http POST请求
            HttpPost httpPost = new HttpPost(this.endpoint);


            if (this.body != null) {
                // 构造一个请求实体
                StringEntity stringEntity = new StringEntity(this.soapWrapper(this.body),
                        ContentType.TEXT_XML);
                // 将请求实体设置到httpPost对象中
                httpPost.setEntity(stringEntity);
            }
            RequestConfig requestConf = RequestConfig.custom().setConnectTimeout(
                    this.readTimeout).setSocketTimeout(
                    this.readTimeout).build();

            CloseableHttpResponse response = null;
            CloseableHttpClient closeableHttpClient = HttpClientBuilder.create().setRetryHandler(
                    new DefaultHttpRequestRetryHandler()).setDefaultRequestConfig(
                    requestConf).build();
            try {
                // 执行请求
                return closeableHttpClient.execute(httpPost);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                HttpClientUtils.closeQuietly(response);
                HttpClientUtils.closeQuietly(closeableHttpClient);
            }
            return null;
        }

        public String get() throws Exception {
            this.requestMethod = "GET";
            return IOUtils.toString(invoke(), Charsets.UTF_8);
        }

    }


}
