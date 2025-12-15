package com.wellsoft.pt.di.component.webservice;

import com.wellsoft.pt.di.component.AbstractProducer;
import com.wellsoft.pt.di.constant.DiConstant;
import com.wellsoft.pt.integration.facade.DataExchangeClientWebService;
import com.wellsoft.pt.integration.security.ServerPasswordCallback;
import com.wellsoft.pt.integration.support.MerlinCrypto;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.apache.cxf.ws.security.wss4j.WSS4JInInterceptor;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;
import org.springframework.util.ReflectionUtils;

import javax.activation.DataHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/8/15
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/8/15    chenq		2019/8/15		Create
 * </pre>
 */
public class DynamicCxfWebserviceProducer extends AbstractProducer<DynamicCxfWebserviceEndpoint> {


    public DynamicCxfWebserviceProducer(DynamicCxfWebserviceEndpoint endpoint) {
        super(endpoint);
    }

    @Override
    protected void action(Object body, Map<String, Object> headers, Map<String, Object> properties,
                          Map<String, DataHandler> attachments) throws Exception {
        try {
            String method = super.endpoint.getMethod();
            String overrideMethod = (String) properties.get(
                    DiConstant.DI_DYNAMIC_CXF_MEHTOD_OVERRIDE);//通过属性重置的方法名
            if (StringUtils.isNotBlank(overrideMethod)) {
                method = overrideMethod;
            }

            if (BooleanUtils.isTrue(super.endpoint.getIsEnableCA())) {//启用ca
                DataExchangeClientWebService webService = getClientWebService(method, properties);
                if (webService == null) {
                    throw new RuntimeException("未获取到动态调用服务DataExchangeClientWebService");
                }
                Method[] methods = DataExchangeClientWebService.class.getMethods();
                for (Method m : methods) {
                    if (m.getName().equals(method)) {
                        Object result = ReflectionUtils.invokeMethod(m, webService,
                                body.getClass().isArray() ? (Object[]) body : new Object[]{body});
                        propagateActionMessage(result);
                    }
                }
            } else {
                JaxWsDynamicClientFactory clientFactory = JaxWsDynamicClientFactory.newInstance();
                Client client = clientFactory.createClient(super.endpoint.getAddress());
                Object[] result = client.invoke(method,
                        body.getClass().isArray() ? (Object[]) body : new Object[]{body});
                propagateActionMessage(result[0]);

            }
        } catch (Exception e) {
            logger.error("cxf-webservice客户端调用异常：", e);
            throw new RuntimeException("cxf-webservice客户端调用异常");
        }

    }


    private Object[] invoke(Client client, String method, Object... obj) throws Exception {
        return client.invoke(method, obj);
    }

    private DataExchangeClientWebService getClientWebService(String method,
                                                             Map<String, Object> properties) {
        try {
            // 调用WebService
            JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
            factory.setServiceClass(DataExchangeClientWebService.class);
            factory.setAddress(super.endpoint.getAddress());

            // 发送数据用户名密码、签名加密
            Map<String, Object> outProps = new HashMap<String, Object>();
            outProps.put("action", "Signature Encrypt");
            // 服务器证书别名
            MerlinCrypto crypto = MerlinCrypto.getInstace();
            String server = crypto.getDefaultX509Alias();
            outProps.put("user", server);
            outProps.put("passwordCallbackClass", ServerPasswordCallback.class.getName());
            // 从接入系统的证书主题调用MerlinCrypto的getAliasesForDN获取另名
            String[] alias = new String[0];
            try {
                alias = crypto.getAliasesForDN(super.endpoint.getSubjectDN());
            } catch (Exception e) {
                // TODO Auto-generated catch block
                logger.info(e.getMessage());
            }
            String keyStorePropFile = MerlinCrypto.getKeyStorePropFile();
            outProps.put("mtom-enabled", Boolean.TRUE);
            outProps.put("encryptionUser", alias[0]);
            outProps.put("encryptionPropFile", keyStorePropFile);
            // 服务器证书别名
            outProps.put("signatureUser", server);
            outProps.put("signaturePropFile", keyStorePropFile);

            Map<String, Object> wSS4JOutInterceptorProperties = (Map) properties.get(
                    "WSS4JOutInterceptorProperties");
            if (MapUtils.isNotEmpty(wSS4JOutInterceptorProperties)) {
                outProps = wSS4JOutInterceptorProperties;
            }

            WSS4JOutInterceptor outInterceptor = new WSS4JOutInterceptor(outProps);


            // 启用附件流数据上传
            outInterceptor.setAllowMTOM(true);
            factory.setProperties(outProps);
            factory.getOutInterceptors().add(outInterceptor);

            // org.apache.ws.security.components.crypto.Merlin
            // 接收数据用户名密码、签名解密
            Map<String, Object> inProps = new HashMap<String, Object>();
            inProps.put("action", "Signature Encrypt");
            inProps.put("passwordCallbackClass", ServerPasswordCallback.class.getName());
            inProps.put("decryptionPropFile", keyStorePropFile);
            inProps.put("signaturePropFile", keyStorePropFile);

            Map<String, Object> wSS4JInInterceptorProperties = (Map) properties.get(
                    "WSS4JInInterceptorProperties");
            if (MapUtils.isNotEmpty(wSS4JInInterceptorProperties)) {
                inProps = wSS4JInInterceptorProperties;
            }


            factory.getInInterceptors().add(new WSS4JInInterceptor(inProps));

            DataExchangeClientWebService sourceClientWebService = (DataExchangeClientWebService) factory.create();

            Client proxy = ClientProxy.getClient(sourceClientWebService);

            HTTPConduit conduit = (HTTPConduit) proxy.getConduit();
            HTTPClientPolicy policy = new HTTPClientPolicy();
            policy.setConnectionTimeout(10000); // 连接超时时间
            policy.setReceiveTimeout(120000);// 请求超时时间.
            conduit.setClient(policy);

            return sourceClientWebService;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }

    }

}
