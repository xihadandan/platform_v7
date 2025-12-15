/*
 * @(#)2014-7-27 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.service.impl;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.integration.entity.ExchangeSystem;
import com.wellsoft.pt.integration.facade.DataExchangeClientWebService;
import com.wellsoft.pt.integration.request.FilesRequest;
import com.wellsoft.pt.integration.response.Response;
import com.wellsoft.pt.integration.security.ServerPasswordCallback;
import com.wellsoft.pt.integration.service.ExchangeSystemService;
import com.wellsoft.pt.integration.service.SerializeExchangeService;
import com.wellsoft.pt.integration.support.MerlinCrypto;
import com.wellsoft.pt.integration.support.SerializeUtils;
import com.wellsoft.pt.integration.support.XZSPBIZ;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.mt.entity.Tenant;
import com.wellsoft.pt.mt.facade.service.TenantFacadeService;
import com.wellsoft.pt.multi.org.bean.OrgUserVo;
import com.wellsoft.pt.multi.org.facade.service.OrgApiFacade;
import com.wellsoft.pt.security.core.userdetails.DefaultUserDetails;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.apache.cxf.ws.security.wss4j.WSS4JInInterceptor;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author Administrator
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016-9-21.1	Administrator		2016-9-21		Create
 * </pre>
 * @date 2016-9-21
 */
@Service
@Transactional
public class SerializeExchangeServiceImpl extends BaseServiceImpl implements SerializeExchangeService {

    @Autowired
    private ExchangeSystemService exchangeSystemDao;

    /**
     * unitId 目标系统
     * typeId 业务
     * serializeData
     * <p>
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.service.SerializeExchangeService#onSend(java.util.Map)
     */
    @Override
    public Response onSend(String typeId, String unitId, String serializeData, String para, FilesRequest streamingDatas) throws Exception {
        // TODO Auto-generated method stub
        try {
            return dataExchangeClientWebServicegetClientWebService(unitId).SendSerializeData(typeId, XZSPBIZ.UNIT_ID_TYDJ, serializeData, para, streamingDatas);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.service.SerializeExchangeService#onReceive(java.util.Map)
     */
    @Override
    public Response onReceive(String typeId, String unitId, String serializeData, String para, FilesRequest streamingDatas) throws Exception {
        // TODO Auto-generated method stub
        try {
            // 用户登录
            TenantFacadeService tenantService = ApplicationContextHolder.getBean(TenantFacadeService.class);
            Tenant tenant = null;
            tenant = tenantService.getByAccount("T001");
            OrgApiFacade orgApiFacade = ApplicationContextHolder.getBean(OrgApiFacade.class);
            OrgUserVo user = orgApiFacade.getUnitAdmin(unitId);
            UserDetails userDetails = new DefaultUserDetails(tenant, user, AuthorityUtils.createAuthorityList());
            UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword());
            SecurityContextHolder.getContext().setAuthentication(authRequest);

            Response map = new Response();
            Object data = SerializeUtils.unSerialize(serializeData);
            // 具体业务，直接屏蔽， --zyguo
            // if (typeId.equals("ysj_forward")) {
            // PreTrialService preTrialService =
            // ApplicationContextHolder.getBean(PreTrialService.class);
            // return preTrialService.receiveForward(data, streamingDatas);
            // } else if (typeId.equals("ysj_back")) {
            // PreTrialService preTrialService =
            // ApplicationContextHolder.getBean(PreTrialService.class);
            // return preTrialService.receiveBack(data, para);
            // } else if (typeId.equals("ysj_cancel")) {
            // PreTrialService preTrialService =
            // ApplicationContextHolder.getBean(PreTrialService.class);
            // return preTrialService.receiveCancel(data, para);
            // } else if (typeId.equals("ysj_bmys")) {
            // PreTrialService preTrialService =
            // ApplicationContextHolder.getBean(PreTrialService.class);
            // return preTrialService.receiveBMYS(data);
            // } else if (typeId.equals("ysj_bz")) {
            // PreTrialService preTrialService =
            // ApplicationContextHolder.getBean(PreTrialService.class);
            // return preTrialService.receiveBZ(data);
            // } else if (typeId.equals("ysj_bzfk")) {
            // PreTrialService preTrialService =
            // ApplicationContextHolder.getBean(PreTrialService.class);
            // return preTrialService.receiveBZFK(data, streamingDatas);
            // } else if (typeId.equals("yy_send")) {
            // WSYYBizService WSYYBizService =
            // ApplicationContextHolder.getBean(WSYYBizService.class);
            // return WSYYBizService.receiveYyData(data);
            // } else if (typeId.equals("yy_qrap")) {
            // WSYYBizService WSYYBizService =
            // ApplicationContextHolder.getBean(WSYYBizService.class);
            // return WSYYBizService.receiveYyQRAP(data);
            // } else if (typeId.equals("yy_fkxx")) {
            // WSYYBizService WSYYBizService =
            // ApplicationContextHolder.getBean(WSYYBizService.class);
            // return WSYYBizService.receiveYyFK(data, para);
            // }

            map.setMsg("typeId不能为空");
            map.setCode(-1);
            return map;
        } catch (Exception e) {
            throw e;
        }
    }

    public DataExchangeClientWebService dataExchangeClientWebServicegetClientWebService(String unitId) {
        // if (SqldConfig.SJ_UNIT_ID.equals(XZSPBIZ.UNIT_ID_TYDJ)) {
        // return
        // getClientWebService(exchangeSystemDao.getExchangeSystemsByUnit(unitId).get(0),
        // "webServiceUrl");
        // } else {
        // return
        // getClientWebService(exchangeSystemDao.getExchangeSystemsByUnit(SqldConfig.SJ_UNIT_ID).get(0),
        // "webServiceUrl");
        // }

        // 具体业务，直接屏蔽， --zyguo
        return null;
    }

    /**
     * 辅助方法
     * 获得客户端webservice对象
     *
     * @param exchangeSystem
     * @return
     */
    public DataExchangeClientWebService getClientWebService(ExchangeSystem exchangeSystem, String method) {
        try {
            // 调用WebService
            JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
            factory.setServiceClass(DataExchangeClientWebService.class);
            // 数据交换目标结点(工商局)自建系统WebService地址
            String address = "";
            if (method.equals("sendCallbackUrl")) {
                address = exchangeSystem.getSendCallbackUrl();
            } else if (method.equals("receiveUrl")) {
                address = exchangeSystem.getReceiveUrl();
            } else if (method.equals("replyMsgUrl")) {
                address = exchangeSystem.getReplyMsgUrl();
            } else if (method.equals("routeCallbackUrl")) {
                address = exchangeSystem.getRouteCallbackUrl();
            } else if (method.equals("cancelUrl")) {
                address = exchangeSystem.getCancelUrl();
            } else if (method.equals("cancelCallbackUrl")) {
                address = exchangeSystem.getCancelCallbackUrl();
            } else if (method.equals("webServiceUrl")) {
                address = exchangeSystem.getWebServiceUrl();
            }

            factory.setAddress(address);

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
                alias = crypto.getAliasesForDN(exchangeSystem.getSubjectDN());
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            String keyStorePropFile = MerlinCrypto.getKeyStorePropFile();
            outProps.put("encryptionUser", alias[0]);
            outProps.put("encryptionPropFile", keyStorePropFile);
            // 服务器证书别名
            outProps.put("signatureUser", server);
            outProps.put("signaturePropFile", keyStorePropFile);
            WSS4JOutInterceptor outInterceptor = new WSS4JOutInterceptor(outProps);
            // 启用附件流数据上传
            outInterceptor.setAllowMTOM(true);
            factory.getOutInterceptors().add(outInterceptor);
            // org.apache.ws.security.components.crypto.Merlin
            // 接收数据用户名密码、签名解密
            Map<String, Object> inProps = new HashMap<String, Object>();
            inProps.put("action", "Signature Encrypt");
            inProps.put("passwordCallbackClass", ServerPasswordCallback.class.getName());
            inProps.put("decryptionPropFile", keyStorePropFile);
            inProps.put("signaturePropFile", keyStorePropFile);
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
