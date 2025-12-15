package com.wellsoft.pt.integration.service.impl;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Maps;
import com.wellsoft.context.config.Config;
import com.wellsoft.context.exception.BusinessException;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.basicdata.facade.service.BasicDataApiFacade;
import com.wellsoft.pt.basicdata.workhour.enums.WorkUnit;
import com.wellsoft.pt.basicdata.workhour.support.WorkPeriod;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.integration.bean.ExchangeDataMonitorBean;
import com.wellsoft.pt.integration.entity.*;
import com.wellsoft.pt.integration.facade.DataExchangeClientWebService;
import com.wellsoft.pt.integration.provider.BusinessHandleSource;
import com.wellsoft.pt.integration.provider.UnitSystemSource;
import com.wellsoft.pt.integration.request.*;
import com.wellsoft.pt.integration.response.*;
import com.wellsoft.pt.integration.security.ExchangeConfig;
import com.wellsoft.pt.integration.security.ServerPasswordCallback;
import com.wellsoft.pt.integration.security.WebServiceMessageSender;
import com.wellsoft.pt.integration.service.*;
import com.wellsoft.pt.integration.support.*;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.message.facade.service.MessageClientApiFacade;
import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;
import com.wellsoft.pt.repository.service.MongoFileService;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.IgnoreLoginUtils;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.security.util.TenantContextHolder;
import com.wellsoft.pt.unit.entity.CommonUnit;
import com.wellsoft.pt.unit.facade.service.UnitApiFacade;
import net.sf.json.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.apache.cxf.ws.security.wss4j.WSS4JInInterceptor;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;
import org.apache.tools.ant.filters.StringInputStream;
import org.apache.wss4j.common.ext.WSSecurityException;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.activation.DataHandler;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Description: 数据交换数据流向
 *
 * @author Administrator
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-11-17.1	ruanhg		2013-11-17		Create
 * </pre>
 * @date 2013-11-17
 */
@Service
public class ExchangeDataFlowServiceImpl implements ExchangeDataFlowService {

    private org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ExchangeDataBatchService exchangeDataBatchService;
    @Autowired
    private ExchangeDataService exchangeDataService;
    @Autowired
    private ExchangeDataSendMonitorService exchangeDataSendMonitorService;
    @Autowired
    private ExchangeDataCallbackService exchangeDataCallbackService;
    @Autowired
    private ExchangeDataRouteService exchangeDataRouteService;
    @Autowired
    private ExchangeDataReplyService exchangeDataReplyService;
    @Autowired
    private ExchangeDataTypeService exchangeDataTypeService;
    @Autowired
    private ExchangeSystemService exchangeSystemService;
    @Autowired
    private DyFormFacade dyFormApiFacade;
    @Autowired
    private ExchangeDataMonitorService exchangeDataMonitorService;
    @Autowired
    private MongoFileService mongoFileService;
    @Autowired
    private ExchangeDataLogService exchangeDataLogService;
    @Autowired
    private ExchangeRouteService exchangeRouteService;
    @Autowired
    private ExchangeDataTransformService exchangeDataTransformService;
    @Autowired
    private UnitApiFacade unitApiFacade;
    @Autowired
    private BasicDataApiFacade basicDataApiFacade;
    @Autowired
    private MessageClientApiFacade messageClientApiFacade;
    @Autowired
    private ExchangeDataRepestService exchangeDataRepestService;
    @Autowired
    private DXExchangeBatchService dXExchangeBatchService;
    @Autowired
    private DXExchangeDataService dXExchangeDataService;
    @Autowired
    private DXExchangeRouteDateService dXExchangeRouteDateService;
    @Autowired
    private Map<String, BusinessHandleSource> businessHandleSourceMap;
    @Autowired
    private ExchangeLogService exchangeLogService;
    @Autowired
    private Map<String, UnitSystemSource> unitSystemSourceMap;
    @Autowired
    private ZongXianExchangeService zongXianExchangeService;

    /******************************商改*************************************/

    private static String map2dom(Map<String, String> params) {
        if (params == null || params.size() <= 0) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        String upperCase;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            sb.append("<").append(upperCase = entry.getKey().toUpperCase()).append(">");
            sb.append(entry.getValue());
            sb.append("</").append(upperCase).append(">");
        }
        return sb.toString();
    }

    /**
     * 平台接口实现
     * 接收数据
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.service.ExchangeDataFlowService#send(com.wellsoft.pt.integration.request.SendRequest, java.lang.Integer)
     */
    @Override
    @Transactional
    public SendResponse send(SendRequest request, Integer operateSource) {
        return null;
    }

    @Override
    public String getTest(String getData) {
        logger.error("与工商数据包测试，接收到数据内容为：" + getData);
        return "与工商数据包测试，接收到数据内容为：" + getData;
    }

    @Override
    public String sendTest() {
        String sendData = UUID.randomUUID().toString();
        ExchangeSystem exchangeSystem = exchangeSystemService.getExchangeSystemsByUnit("").get(0);
        DataExchangeClientWebService clientWebService = getClientWebService(exchangeSystem,
                "sendCallbackUrl");
        try {
            clientWebService.getTest(sendData);
            logger.error("与工商数据包测试，发送的数据内容为：" + sendData);
            return "与工商数据包测试，发送的数据内容为：" + sendData;
        } catch (Exception e) {
            logger.error("与工商数据包测试，发送的数据异常");
            return "与工商数据包测试，发送的数据异常";
        }
    }

    /**
     * 标记已经出证
     *
     * @param unitId
     * @param matterId
     * @param ZCH
     * @return
     */
    @Transactional
    public boolean updateReceiveSendNode(String unitId, String matterId, String ZCH) {
        Stopwatch timer = Stopwatch.createStarted();
        logger.error("-------------updateReceiveSendNode begin---------------");
        try {
            String hql = "";
            if (!StringUtils.isBlank(matterId)) {
                hql = "from ExchangeDataMonitor m where m.unitId = :unitId and m.matterId = :matterId and m.exchangeDataSendMonitor.exchangeData.reservedNumber2 = :ZCH";
            } else {
                hql = "from ExchangeDataMonitor m where m.unitId = :unitId and m.exchangeDataSendMonitor.exchangeData.reservedNumber2 = :ZCH and m.matterId is null";
            }
            Map<String, Object> values = new HashMap<String, Object>();
            values.put("unitId", unitId);
            values.put("matterId", matterId);
            values.put("ZCH", ZCH);
            List<ExchangeDataMonitor> edms = exchangeDataMonitorService.listByHQL(hql, values);
            for (ExchangeDataMonitor edm : edms) {
                edm.setReceiveNode("reply");
                exchangeDataMonitorService.save(edm);
            }
            logger.error(
                    "-------------updateReceiveSendNode end success--------------:" + timer.stop());
            return true;
        } catch (Exception e) {
            logger.error(
                    "-------------updateReceiveSendNode end error--------------:" + timer.stop());
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * 是否通过审核
     *
     * @param code
     * @return
     */
    @Override
    @Transactional
    public String examineExchange(String sendDataUuid, Integer code, String failMsg) {
        Stopwatch timer = Stopwatch.createStarted();
        logger.error("-------------examineExchange begin---------------");
        try {
            UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
            ExchangeDataSendMonitor exchangeDataSendMonitor = exchangeDataSendMonitorService.getOne(
                    sendDataUuid);
            ExchangeData exchangeData = exchangeDataSendMonitor.getExchangeData();
            ExchangeDataBatch exchangeDataBatch = exchangeData.getExchangeDataBatch();
            if (code == 1) {

                // 如果上传的是行政许可，标记相关收件的出证状态
                if (ExchangeConfig.TYPE_ID_SSXX_XZXK.equals(exchangeDataBatch.getTypeId())) {
                    updateReceiveSendNode(exchangeDataBatch.getFromId(), exchangeData.getMatterId(),
                            exchangeData.getReservedNumber2());
                }

                exchangeDataSendMonitor.setSendNode("ing");
                exchangeData.setReleaser(SpringSecurityUtils.getCurrentUserId());
                exchangeData.setReleaseTime(new Date());
                exchangeDataService.save(exchangeData);
                exchangeDataSendMonitorService.save(exchangeDataSendMonitor);

                WebServiceMessageSender sender = ApplicationContextHolder.getBean(
                        WebServiceMessageSender.class);
                WebServiceMessage msg = new WebServiceMessage();
                msg.setWay("callbackClientAndreceiveClient");
                msg.setBatchId(exchangeDataBatch.getId());
                msg.setUnitId(exchangeDataBatch.getFromId());// 发件人
                msg.setTypeId(exchangeDataBatch.getTypeId());
                msg.setTenantId(userDetails.getTenantId());
                msg.setUserId(userDetails.getUserId());
                msg.setCc(exchangeDataBatch.getCc());
                msg.setBcc(exchangeDataBatch.getBcc());
                msg.setToId(exchangeDataBatch.getToId());
                sender.send(msg);// callbackClientAndreceiveClient
            } else if (code == -1) {
                exchangeDataSendMonitor.setSendNode("examineFail");
                exchangeData.setExamineFailMsg(failMsg);
                exchangeDataService.save(exchangeData);
                exchangeDataSendMonitorService.save(exchangeDataSendMonitor);
            }
            logger.error("-------------examineExchange end success--------------:" + timer.stop());
            return "success";
        } catch (Exception e) {
            logger.error("-------------examineExchange end error--------------:" + timer.stop());
            logger.error(e.getMessage(), e);
            return "fail";
        }

    }

    /**
     * 删除审核被退回
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.service.ExchangeDataFlowService#delExamineExchange(java.lang.String)
     */
    @Override
    public String delExamineExchange(String sendDataUuid) {
        try {
            ExchangeDataSendMonitor exchangeDataSendMonitor = exchangeDataSendMonitorService.getOne(
                    sendDataUuid);
            exchangeDataSendMonitor.setSendNode("examineClose");
            exchangeDataSendMonitorService.save(exchangeDataSendMonitor);
            return "success";
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return "fail";
        }

    }

    /**
     * 标记出证状态
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.service.ExchangeDataFlowService#markCertificateStatus(java.lang.String[])
     */
    @Override
    @Transactional
    public String markCertificateStatus(String[] receiveMonitorUuid) {
        try {
            for (int i = 0; i < receiveMonitorUuid.length; i++) {
                ExchangeDataMonitor edm = exchangeDataMonitorService.getOne(receiveMonitorUuid[i]);
                if (edm.getReceiveNode().equals("sign")) {
                    edm.setReceiveNode("reply");
                } else if (edm.getReceiveNode().equals("reply")) {
                    edm.setReceiveNode("sign");
                }
                exchangeDataMonitorService.save(edm);
            }
            return "success";

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return "fail";
        }

    }

    /**
     * 请求终端接口
     * 请求终端接口上传结果回调接口
     *
     * @param exchangeSystem
     * @param batchId
     * @return
     */
    @Override
    @Transactional
    public void callbackClientAndreceiveClient(WebServiceMessage webServiceMessage) {
        return;
    }

    /**
     * 请求终端接口
     * 请求终端接收数据接口分发给系统
     * <p>
     * (non-Javadoc)
     *
     * @throws IOException
     * @see com.wellsoft.pt.integration.service.ExchangeDataFlowService#recviceClient(com.wellsoft.pt.integration.support.WebServiceMessage)
     */
    @Override
    @Transactional
    public void receiveClient(WebServiceMessage webServiceMessage) throws IOException {
        Stopwatch timer = Stopwatch.createStarted();
        logger.error("-------------receiveClient begin---------------");
        try {
            ExchangeSystem clientExchangeSystem = exchangeSystemService.getOne(
                    webServiceMessage.getSystemUuid());
            // DataExchangeClientWebService clientWebService =
            // getClientWebService(clientExchangeSystem, "receiveUrl");
            // 构造发送对象
            ReceiveRequest request = new ReceiveRequest();
            request.setBatchId(webServiceMessage.getBatchId());
            request.setBcc(webServiceMessage.getBcc());
            request.setCc(webServiceMessage.getCc());
            request.setFrom(webServiceMessage.getUnitId());
            request.setTo(webServiceMessage.getToId());
            request.setTypeId(webServiceMessage.getTypeId());

            List<DataItem> dataItems = new ArrayList<DataItem>();
            DataItem di = new DataItem();
            di.setCorrelationId(webServiceMessage.getCorrelationDataId());
            di.setCorrelationRecVer(webServiceMessage.getCorrelationRecver());
            di.setDataId(webServiceMessage.getDataId());
            di.setRecVer(webServiceMessage.getRecVer());
            di.setText(webServiceMessage.getText());
            Map<String, Object> props = MerlinCrypto.getInstace().getWSS4JAttachmentPropsForDN(
                    clientExchangeSystem.getSubjectDN());
            List<StreamingData> streamingDatas = new ArrayList<StreamingData>();
            List<MongoFileEntity> mongoFileEntitys = mongoFileService.getFilesFromFolder(
                    webServiceMessage.getFormDataUuid(), null);
            for (MongoFileEntity mongoFileEntity : mongoFileEntitys) {
                StreamingData sd = new StreamingData();
                sd.setFileName(mongoFileEntity.getFileName());
                sd.setDataHandler(
                        new DataHandler(new InputStreamDataSource(mongoFileEntity.getInputstream(),
                                "octet-stream")));
                WSS4JOutAttachment wss4jOutAttachment2;
                try {
                    wss4jOutAttachment2 = new WSS4JOutAttachment(sd, props);
                    wss4jOutAttachment2.signAndEncrypt();
                    streamingDatas.add(wss4jOutAttachment2.getEncryptedStreamingData());
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    logger.info(e.getMessage());
                }
            }
            ExchangeDataMonitor exchangeDataMonitor = exchangeDataMonitorService.getOne(
                    webServiceMessage
                            .getMonitorUuid());
            di.setStreamingDatas(streamingDatas);
            di.setMatterId(exchangeDataMonitor.getMatterId());
            dataItems.add(di);
            request.setDataList(dataItems);

            request.setBatchId(
                    webServiceMessage.getBatchId() + exchangeDataMonitor.getMatterId());// 批次号，工商过来的数据，一个批次发多次给市局
            // 要求撤回
            if (exchangeDataMonitor.getCancelRequest() != null && exchangeDataMonitor.getCancelRequest().equals(
                    "yes")) {
                if (exchangeDataMonitor.getReceiveStatus().equals("default")) {
                    // 处理未到达系统的撤回
                    exchangeDataMonitor.setCancelStatus("success");
                    exchangeDataMonitor.setCancelMsg("未发送到系统被注销");
                    exchangeDataMonitor.setCancelTime(new Date());
                    if (!StringUtils.isBlank(SpringSecurityUtils.getCurrentUserId())) {
                        exchangeDataMonitor.setCancelUser(SpringSecurityUtils.getCurrentUserId());
                    }
                }
            } else {
                // try {
                // // logger.error("****receiveClient receive ****" +
                // request.toString());
                // // 调用WebService发送
                // ReceiveResponse wp = clientWebService.receive(request);
                // /*****************写入日志5****************************/
                // ExchangeDataLog exchangeDataLog5 = new ExchangeDataLog();
                // exchangeDataLog5.setBatchId(webServiceMessage.getBatchId());
                // exchangeDataLog5.setDataId(webServiceMessage.getDataId());
                // exchangeDataLog5.setRecVer(webServiceMessage.getRecVer());
                // exchangeDataLog5.setNode(5);
                // exchangeDataLog5.setCode(wp.getCode());
                // exchangeDataLog5.setStatus(1);
                // exchangeDataLog5.setMatterId(exchangeDataMonitor.getMatterId());
                // exchangeDataLog5.setFromUnitId(webServiceMessage.getUnitId());
                // exchangeDataLog5.setToUnitId(clientExchangeSystem.getUnitId());
                // exchangeDataLog5.setMsg("调用终端接收数据接口日志:成功，" + wp.getMsg());
                // addExchangeDataLog(exchangeDataLog5);
                // } catch (Exception e) {
                ExchangeRoute exchangeRoute = exchangeRouteService.getOne(
                        exchangeDataMonitor.getRouteMsg());
                if (exchangeRoute.getRetransmissionNum() == null) {// 不重发时，发件状态未送达
                    ExchangeDataSendMonitor fe = exchangeDataMonitor.getExchangeDataSendMonitor();
                    fe.setSendNode("abnormal");
                    exchangeDataSendMonitorService.save(fe);
                } else {
                    /****************加入重发任务****************/
                    ExchangeDataRepest exchangeDataRepest = new ExchangeDataRepest();
                    exchangeDataRepest.setExchangeDataMonitorUuid(exchangeDataMonitor.getUuid());
                    exchangeDataRepest.setInterval(exchangeRoute.getInterval());
                    exchangeDataRepest.setSystemUuid(clientExchangeSystem.getUuid());
                    exchangeDataRepest.setMatterId(exchangeDataMonitor.getMatterId());
                    exchangeDataRepest.setRetransmissionNum(exchangeRoute.getRetransmissionNum());
                    exchangeDataRepest.setStatus("ing");
                    exchangeDataRepest.setSendNum(1);
                    exchangeDataRepest.setSendMethod("receiveClient");
                    exchangeDataRepest.setUserId(webServiceMessage.getUserId());
                    exchangeDataRepest.setTenantId(webServiceMessage.getTenantId());
                    exchangeDataRepestService.save(exchangeDataRepest);
                    /*****************写入日志5****************************/
                    ExchangeDataLog exchangeDataLog5 = new ExchangeDataLog();
                    exchangeDataLog5.setBatchId(webServiceMessage.getBatchId());
                    exchangeDataLog5.setDataId(webServiceMessage.getDataId());
                    exchangeDataLog5.setRecVer(webServiceMessage.getRecVer());
                    exchangeDataLog5.setNode(5);
                    exchangeDataLog5.setStatus(-1);
                    exchangeDataLog5.setFromUnitId(webServiceMessage.getUnitId());
                    exchangeDataLog5.setToUnitId(clientExchangeSystem.getUnitId());
                    exchangeDataLog5.setMsg("加入重发任务receiveClient：" + exchangeDataRepest.getUuid());
                    addExchangeDataLog(exchangeDataLog5);
                }
                // logger.info(e.getMessage());
                // }
            }
            logger.error("-------------receiveClient end success--------------:" + timer.stop());
        } catch (Exception e) {
            logger.error("-------------receiveClient end error--------------:" + timer.stop());
            logger.error(e.getMessage(), e);

        }

    }

    /**
     * 平台接口实现
     * 终端调用平台抄送结果回调接口
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.service.ExchangeDataFlowService#sendCallback(com.wellsoft.pt.integration.request.PlatformSendCallbackRequest)
     */
    @Override
    @Transactional
    public PlatformSendCallbackResponse sendCallback(PlatformSendCallbackRequest message) {
        Stopwatch timer = Stopwatch.createStarted();
        logger.error("-------------sendCallback begin---------------");
        try {
            UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
            // TODO Auto-generated method stub
            // 获得交换数据对象
            ExchangeData exchangeData = exchangeDataService.getExchangeDataByDataId(
                    message.getDataId(),
                    message.getRecVer());
            if (exchangeData == null) {
                PlatformSendCallbackResponse platformSendCallbackResponse = new PlatformSendCallbackResponse();
                platformSendCallbackResponse.setCode(-1);
                platformSendCallbackResponse.setMsg("找不到dataId为" + message.getDataId() + ",recVer为"
                        + message.getRecVer() + "的数据");
                return platformSendCallbackResponse;
            }
            for (ExchangeDataSendMonitor edsm : exchangeData.getExchangeDataSendMonitors()) {
                // 写入用户监控到达收件箱
                Set<ExchangeDataMonitor> exchangeDataMonitors = edsm.getExchangeDataMonitors();
                int flag = 1;// 标识全部到达，有未到达时为0
                for (ExchangeDataMonitor edm : exchangeDataMonitors) {
                    if (edm.getUnitId().equals(message.getUnitId())
                            && ((edm.getMatterId() != null && edm.getMatterId().equals(
                            message.getMatterId())) || StringUtils
                            .isBlank(edm.getMatterId()))) {
                        if (message.getCode() == 1) {
                            edm.setReceiveStatus("success");
                        } else if (message.getCode() == -1) {
                            edm.setReceiveStatus("fail");
                        }
                        edm.setReceiveTime(new Date());
                        exchangeDataMonitorService.save(edm);
                    } else if (!edm.getReceiveStatus().equals("success")) {
                        flag = 0;
                    }
                }
                // 全部到达
                if (flag == 1 && message.getCode() == 1) {
                    edsm.setSendNode("end");
                }
                /*****************添加抄送结果回调信息到表里****************************/
                ExchangeDataCallback edcb = new ExchangeDataCallback();
                edcb.setDataId(message.getDataId());
                edcb.setMsg(message.getMsg());
                edcb.setRecVer(message.getRecVer());
                if (message.getCode() == 1) {
                    edcb.setStatus(true);
                } else {
                    edcb.setStatus(false);
                }
                edcb.setUnitId(message.getUnitId());
                exchangeDataCallbackService.save(edcb);

                /*****************写入日志6****************************/
                ExchangeDataLog exchangeDataLog = new ExchangeDataLog();
                exchangeDataLog.setBatchId(exchangeData.getExchangeDataBatch().getId());
                exchangeDataLog.setDataId(message.getDataId());
                exchangeDataLog.setRecVer(message.getRecVer());
                exchangeDataLog.setNode(6);
                exchangeDataLog.setFromUnitId(edsm.getFromId());
                exchangeDataLog.setToUnitId(message.getUnitId());
                exchangeDataLog.setCode(message.getCode());
                exchangeDataLog.setStatus(1);
                exchangeDataLog.setMsg("调用平台抄送结果回调接口日志:" + message.getMsg());
                addExchangeDataLog(exchangeDataLog);

                /**********回调终端路由情况接口************/
                WebServiceMessageSender sender = ApplicationContextHolder.getBean(
                        WebServiceMessageSender.class);
                WebServiceMessage msg = new WebServiceMessage();
                msg.setWay("RouteCallBackClient");
                msg.setBatchId(exchangeData.getExchangeDataBatch().getId());
                msg.setUnitId(message.getUnitId());
                msg.setTypeId(exchangeData.getExchangeDataBatch().getTypeId());
                msg.setDataId(message.getDataId());
                msg.setUnitId(message.getUnitId());
                msg.setRecVer(message.getRecVer());
                msg.setCode(message.getCode());
                msg.setMsg(message.getMsg());
                msg.setFromId(edsm.getFromId());
                msg.setMatterId(message.getMatterId());
                msg.setTenantId(userDetails.getTenantId());
                msg.setUserId(userDetails.getUserId());
                sender.send(msg);// RouteCallBackClient
            }
            PlatformSendCallbackResponse platformSendCallbackResponse = new PlatformSendCallbackResponse();
            platformSendCallbackResponse.setCode(1);
            platformSendCallbackResponse.setMsg("成功");
            logger.error("-------------sendCallback end success--------------:" + timer.stop());
            return platformSendCallbackResponse;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            PlatformSendCallbackResponse platformSendCallbackResponse = new PlatformSendCallbackResponse();
            platformSendCallbackResponse.setCode(-2);
            platformSendCallbackResponse.setMsg("程序异常");
            logger.error("-------------sendCallback end error--------------:" + timer.stop());
            return platformSendCallbackResponse;
        }

    }

    /**
     * 请求终端接口
     * 请求回调终端路由
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.service.ExchangeDataFlowService#reply(com.wellsoft.pt.integration.request.ReplyRequest)
     */
    @Override
    @Transactional
    public void RouteCallBackClient(WebServiceMessage webServiceMessage) {
        Stopwatch timer = Stopwatch.createStarted();
        logger.error("-------------RouteCallBackClient begin---------------");
        try {
            ExchangeSystem exchangeSystem = exchangeSystemService.getExchangeSystemByUnitAndType(
                    webServiceMessage.getFromId(), webServiceMessage.getTypeId());

            // DataExchangeClientWebService clientWebService =
            // getClientWebService(exchangeSystem, "routeCallbackUrl");

            RouteCallbackRequest routeCallbackMessage = new RouteCallbackRequest();
            // 上传时的批量ID
            routeCallbackMessage.setDataId(webServiceMessage.getDataId());
            // 数据版本号
            routeCallbackMessage.setRecVer(webServiceMessage.getRecVer());

            routeCallbackMessage.setUnitId(webServiceMessage.getUnitId());
            // 状态-成功1/失败0
            routeCallbackMessage.setCode(webServiceMessage.getCode());
            // 说明
            routeCallbackMessage.setMsg(webServiceMessage.getMsg());

            routeCallbackMessage.setMatterId(webServiceMessage.getMatterId());

            // 发送上传结果回调信息
            // try {
            // RouteCallbackResponse rp =
            // clientWebService.routeCallback(routeCallbackMessage);
            //
            // /****************添加日志7****************/
            // ExchangeDataLog exchangeDataLog = new ExchangeDataLog();
            // exchangeDataLog.setBatchId(webServiceMessage.getBatchId());
            // exchangeDataLog.setDataId(webServiceMessage.getDataId());
            // exchangeDataLog.setRecVer(webServiceMessage.getRecVer());
            // exchangeDataLog.setNode(7);
            // exchangeDataLog.setFromUnitId(webServiceMessage.getFromId());
            // exchangeDataLog.setMatterId(webServiceMessage.getMatterId());
            // exchangeDataLog.setToUnitId(webServiceMessage.getUnitId());
            // exchangeDataLog.setCode(webServiceMessage.getCode());
            // exchangeDataLog.setStatus(1);
            // exchangeDataLog.setMsg("调用终端路由结果接口日志:" +
            // webServiceMessage.getMsg());
            // addExchangeDataLog(exchangeDataLog);
            // /****************添加路由信息到表里****************/
            // ExchangeDataRoute edroute = new ExchangeDataRoute();
            // edroute.setDataId(webServiceMessage.getDataId());
            // edroute.setMsg(webServiceMessage.getMsg());
            // edroute.setRecVer(webServiceMessage.getRecVer());
            // edroute.setMatterId(webServiceMessage.getMatterId());
            // if (rp.getCode() == 1) {
            // edroute.setStatus(true);
            // } else {
            // edroute.setStatus(false);
            // }
            // edroute.setUnitId(webServiceMessage.getUnitId());
            // exchangeDataRouteDao.save(edroute);
            // } catch (Exception e) {
            /****************加入重发任务****************/
            ExchangeDataRepest exchangeDataRepest = new ExchangeDataRepest();
            exchangeDataRepest.setDataId(routeCallbackMessage.getDataId());
            exchangeDataRepest.setBatchId(webServiceMessage.getBatchId());
            exchangeDataRepest.setFromId(webServiceMessage.getFromId());
            exchangeDataRepest.setDataRecVer(routeCallbackMessage.getRecVer());
            exchangeDataRepest.setCode(routeCallbackMessage.getCode());
            exchangeDataRepest.setMsg(routeCallbackMessage.getMsg());
            exchangeDataRepest.setUnitId(routeCallbackMessage.getUnitId());
            exchangeDataRepest.setStatus("ing");
            exchangeDataRepest.setSystemUuid(exchangeSystem.getUuid());
            exchangeDataRepest.setRetransmissionNum(ExchangeConfig.EXCHANGE_RETRANSMISSIONNUM);
            exchangeDataRepest.setInterval(ExchangeConfig.EXCHANGE_INTERVER);
            exchangeDataRepest.setSendNum(1);
            exchangeDataRepest.setUserId(webServiceMessage.getUserId());
            exchangeDataRepest.setTenantId(webServiceMessage.getTenantId());
            exchangeDataRepest.setSendMethod("routeCallBackClient");
            exchangeDataRepest.setMatterId(webServiceMessage.getMatterId());
            exchangeDataRepestService.save(exchangeDataRepest);
            /****************添加日志7****************/
            ExchangeDataLog exchangeDataLog = new ExchangeDataLog();
            exchangeDataLog.setBatchId(webServiceMessage.getBatchId());
            exchangeDataLog.setDataId(webServiceMessage.getDataId());
            exchangeDataLog.setRecVer(webServiceMessage.getRecVer());
            exchangeDataLog.setNode(7);
            exchangeDataLog.setFromUnitId(webServiceMessage.getFromId());
            exchangeDataLog.setToUnitId(webServiceMessage.getUnitId());
            exchangeDataLog.setStatus(-1);
            exchangeDataLog.setMsg("加入重发任务routeCallBackClient：" + exchangeDataRepest.getUuid());
            addExchangeDataLog(exchangeDataLog);
            /****************添加路由信息到表里****************/
            ExchangeDataRoute edroute = new ExchangeDataRoute();
            edroute.setDataId(webServiceMessage.getDataId());
            edroute.setMsg(webServiceMessage.getMsg());
            edroute.setRecVer(webServiceMessage.getRecVer());
            edroute.setMatterId(webServiceMessage.getMatterId());
            edroute.setStatus(false);
            edroute.setUnitId(webServiceMessage.getUnitId());
            exchangeDataRouteService.save(edroute);
            // }
            logger.error(
                    "-------------RouteCallBackClient end success--------------:" + timer.stop());
        } catch (Exception e) {
            logger.error(
                    "-------------RouteCallBackClient end error--------------:" + timer.stop());
            logger.error(e.getMessage(), e);
        }

    }

    /****
     * 平台接口实现
     * 终端调用平台签收结果接口
     *
     * (non-Javadoc)
     * @see com.wellsoft.pt.integration.service.ExchangeDataFlowService#reply(com.wellsoft.pt.integration.request.ReplyRequest)
     */
    @Override
    @Transactional
    public ReplyResponse reply(ReplyRequest message) {
        Stopwatch timer = Stopwatch.createStarted();
        logger.error("-------------reply begin---------------");
        try {
            UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
            // TODO Auto-generated method stub
            // 获得交换数据对象
            ExchangeData exchangeData = exchangeDataService.getExchangeDataByDataId(
                    message.getDataId(),
                    message.getRecVer());
            if (exchangeData == null) {
                ReplyResponse replyResponse = new ReplyResponse();
                replyResponse.setCode(-1);
                replyResponse.setMsg(
                        "找不到dataId为" + message.getDataId() + ",recVer为" + message.getRecVer() + "的数据");
                return replyResponse;
            }
            ExchangeDataType exchangeDataType = exchangeDataTypeService.getByTypeId(
                    exchangeData.getExchangeDataBatch()
                            .getTypeId());
            String fromId = "";
            for (ExchangeDataSendMonitor edsm : exchangeData.getExchangeDataSendMonitors()) {
                fromId = edsm.getFromId();
                // 写入用户监控到达收件箱
                Set<ExchangeDataMonitor> exchangeDataMonitors = edsm.getExchangeDataMonitors();
                int flag = 1;// 标识全部签收，有未签收时为0
                int flag1 = 1;// 标识全部到达
                int flag2 = 0;// 没有退回
                for (ExchangeDataMonitor edm : exchangeDataMonitors) {
                    if (edm.getUnitId().equals(message.getUnitId())
                            && ((edm.getMatterId() != null && edm.getMatterId().equals(
                            message.getMatterId())) || StringUtils
                            .isBlank(message.getMatterId()))) {
                        if (message.getCode() == 1) {// 已签收
                            edm.setReplyStatus("success");
                            edm.setReceiveNode("sign");
                        } else if (message.getCode() == -1) {// 退回
                            edm.setReplyStatus("fail");
                            edm.setReplyMsg(message.getMsg());
                            edm.setReceiveNode("back");
                            flag2 = 1;
                        }
                        // 如果还未到达系统
                        if (edm.getReceiveStatus().equals("default")) {
                            edm.setReceiveStatus("success");
                            edm.setReceiveTime(edm.getCreateTime());
                        }
                        Date date = new Date();// 签收时间时间
                        edm.setReplyTime(date);
                        WorkPeriod WorkPeriod = basicDataApiFacade.getWorkPeriod(
                                edm.getCreateTime(), date);
                        int betweenDays = WorkPeriod.getDays() - 1;
                        if (!basicDataApiFacade.isWorkDay(edm.getCreateTime())) {
                            betweenDays++;
                        }
                        if (betweenDays > exchangeDataType.getReceiveLimit()) {// 签收超期
                            edm.setReplyLimitNum(betweenDays - exchangeDataType.getReceiveLimit());
                        }
                        if (!StringUtils.isBlank(SpringSecurityUtils.getCurrentUserId())) {
                            edm.setReplyUser(SpringSecurityUtils.getCurrentUserId());
                        }
                        exchangeDataMonitorService.save(edm);
                        ExchangeSystem exchangeSystem = exchangeSystemService.getExchangeSystemByUnitAndType(
                                exchangeData.getExchangeDataBatch().getFromId(),
                                exchangeData.getExchangeDataBatch()
                                        .getTypeId());
                        if (exchangeSystem != null) {

                            /****************调用系统签收接口**********************/
                            WebServiceMessageSender sender = ApplicationContextHolder
                                    .getBean(WebServiceMessageSender.class);
                            WebServiceMessage msg = new WebServiceMessage();
                            msg.setWay("replyClient");
                            msg.setBatchId(exchangeData.getExchangeDataBatch().getId());
                            msg.setUnitId(message.getUnitId());
                            msg.setTypeId(exchangeData.getExchangeDataBatch().getTypeId());
                            msg.setDataId(message.getDataId());
                            msg.setRecVer(message.getRecVer());
                            msg.setCode(message.getCode());
                            msg.setMsg(message.getMsg());
                            msg.setMonitorUuid(edm.getUuid());
                            msg.setDate(message.getReplyTime());
                            msg.setTenantId(userDetails.getTenantId());
                            msg.setUserId(userDetails.getUserId());
                            msg.setMatterId(message.getMatterId());
                            sender.send(msg);// replyClient

                        }
                    } else if (edm.getReplyStatus().equals(
                            "default") && edm.getCancelStatus().equals("default")) {
                        flag = 0;
                    }
                    if (!edm.getUnitId().equals(
                            message.getUnitId()) && !edm.getReceiveStatus().equals("success")) {
                        flag1 = 0;
                    }
                    if (!edm.getUnitId().equals(message.getUnitId()) && edm.getReplyStatus().equals(
                            "fail")) {
                        flag2 = 1;
                    }
                }
                // 全部到达
                if (flag1 == 1 && edsm.getSendNode().equals("ing")) {
                    edsm.setSendNode("end");
                }
                // 全部签收
                if (flag == 1 && message.getCode() == 1 && !edsm.getSendNode().equals("abnormal")) {
                    edsm.setSendNode("sign");// 发送单位已签收件
                }
                // 操作有退回时
                if (flag2 == 1 && !edsm.getSendNode().equals("abnormal")) {
                    edsm.setSendNode("back");// 发送单位退回件
                }
                exchangeDataSendMonitorService.save(edsm);
            }
            /****************添加日志8****************/
            ExchangeDataLog exchangeDataLog = new ExchangeDataLog();
            exchangeDataLog.setBatchId(exchangeData.getExchangeDataBatch().getId());
            exchangeDataLog.setDataId(message.getDataId());
            exchangeDataLog.setRecVer(message.getRecVer());
            exchangeDataLog.setNode(8);
            exchangeDataLog.setFromUnitId(fromId);
            exchangeDataLog.setToUnitId(message.getUnitId());
            exchangeDataLog.setCode(message.getCode());
            exchangeDataLog.setStatus(1);
            exchangeDataLog.setMsg("调用平台签收结果接口日志:" + message.getMsg());
            addExchangeDataLog(exchangeDataLog);

            ReplyResponse replyResponse = new ReplyResponse();
            /****************添加回复结果回调信息到表里****************/
            ExchangeDataReply edr = new ExchangeDataReply();
            edr.setDataId(message.getDataId());
            edr.setMsg(message.getMsg());
            edr.setMatterId(message.getMatterId());
            edr.setRecVer(message.getRecVer());
            edr.setMatterId(message.getMatterId());
            if (message.getCode() == 1) {
                edr.setStatus(true);
                replyResponse.setCode(1);
                replyResponse.setMsg("已收！");
            } else {
                edr.setStatus(false);
                replyResponse.setCode(1);
                replyResponse.setMsg("拒收！");
            }
            edr.setUnitId(message.getUnitId());
            edr.setReplyTime(message.getReplyTime());
            exchangeDataReplyService.save(edr);
            logger.error("-------------reply end success--------------:" + timer.stop());
            return replyResponse;
        } catch (Exception e) {
            logger.error("-------------reply end error--------------:" + timer.stop());
            logger.error(e.getMessage(), e);
            ReplyResponse replyResponse = new ReplyResponse();
            replyResponse.setCode(-2);
            replyResponse.setMsg("程序异常");
            return replyResponse;
        }
    }

    /***
     * 请求终端接口
     * 请求终端签收结果
     *
     * (non-Javadoc)
     * @see com.wellsoft.pt.integration.service.ExchangeDataFlowService#reply(com.wellsoft.pt.integration.request.ReplyRequest)
     */
    @Override
    @Transactional
    public void replyClient(WebServiceMessage webServiceMessage) {
        Stopwatch timer = Stopwatch.createStarted();
        logger.error("-------------replyClient begin---------------");
        try {
            ExchangeDataMonitor edm = exchangeDataMonitorService.getOne(
                    webServiceMessage.getMonitorUuid());
            ExchangeData exchangeData = edm.getExchangeDataSendMonitor().getExchangeData();
            ExchangeSystem exchangeSystem = exchangeSystemService.getExchangeSystemByUnitAndType(
                    exchangeData
                            .getExchangeDataBatch().getFromId(), webServiceMessage.getTypeId());
            // DataExchangeClientWebService clientWebService =
            // getClientWebService(exchangeSystem, "replyMsgUrl");

            ReplyRequest replyMessage = new ReplyRequest();
            // 发送成功的数据ID
            replyMessage.setDataId(webServiceMessage.getDataId());
            // 数据版本号
            replyMessage.setRecVer(webServiceMessage.getRecVer());
            // 接收/拒收动作标识（接收1/拒收0）
            replyMessage.setCode(webServiceMessage.getCode());

            replyMessage.setUnitId(webServiceMessage.getUnitId());

            replyMessage.setReplyTime(webServiceMessage.getDate());
            // 说明
            replyMessage.setMsg(webServiceMessage.getMsg());

            replyMessage.setMatterId(webServiceMessage.getMatterId());
            // 发送请求
            // try {
            // ReplyResponse rp = clientWebService.replyMsg(replyMessage);
            // /****************添加日志9****************/
            // ExchangeDataLog exchangeDataLog = new ExchangeDataLog();
            // exchangeDataLog.setBatchId(webServiceMessage.getBatchId());
            // exchangeDataLog.setDataId(webServiceMessage.getDataId());
            // exchangeDataLog.setRecVer(webServiceMessage.getRecVer());
            // exchangeDataLog.setNode(9);
            // exchangeDataLog.setFromUnitId(edm.getExchangeDataSendMonitor().getFromId());
            // exchangeDataLog.setMatterId(webServiceMessage.getMatterId());
            // exchangeDataLog.setToUnitId(webServiceMessage.getUnitId());
            // exchangeDataLog.setCode(rp.getCode());
            // exchangeDataLog.setStatus(1);
            // exchangeDataLog.setMsg("调用终端签收结果接口日志:" +
            // webServiceMessage.getMsg());
            // addExchangeDataLog(exchangeDataLog);
            // } catch (Exception e) {
            /****************加入重发任务****************/
            ExchangeDataRepest exchangeDataRepest = new ExchangeDataRepest();
            exchangeDataRepest.setDataId(replyMessage.getDataId());
            exchangeDataRepest.setDataRecVer(replyMessage.getRecVer());
            exchangeDataRepest.setCode(replyMessage.getCode());
            exchangeDataRepest.setMsg(replyMessage.getMsg());
            exchangeDataRepest.setOpTime(replyMessage.getReplyTime());
            exchangeDataRepest.setBatchId(webServiceMessage.getBatchId());
            exchangeDataRepest.setFromId(exchangeData.getExchangeDataBatch().getFromId());
            exchangeDataRepest.setUnitId(replyMessage.getUnitId());
            exchangeDataRepest.setExchangeDataMonitorUuid(webServiceMessage.getMonitorUuid());
            exchangeDataRepest.setStatus("ing");
            exchangeDataRepest.setRetransmissionNum(ExchangeConfig.EXCHANGE_RETRANSMISSIONNUM);
            exchangeDataRepest.setInterval(ExchangeConfig.EXCHANGE_INTERVER);
            exchangeDataRepest.setMatterId(webServiceMessage.getMatterId());
            exchangeDataRepest.setSendNum(1);
            exchangeDataRepest.setSystemUuid(exchangeSystem.getUuid());
            exchangeDataRepest.setUserId(webServiceMessage.getUserId());
            exchangeDataRepest.setTenantId(webServiceMessage.getTenantId());
            exchangeDataRepest.setSendMethod("replyClient");
            exchangeDataRepestService.save(exchangeDataRepest);
            /****************添加日志9****************/
            ExchangeDataLog exchangeDataLog = new ExchangeDataLog();
            exchangeDataLog.setBatchId(webServiceMessage.getBatchId());
            exchangeDataLog.setDataId(webServiceMessage.getDataId());
            exchangeDataLog.setRecVer(webServiceMessage.getRecVer());
            exchangeDataLog.setNode(9);
            exchangeDataLog.setFromUnitId(edm.getExchangeDataSendMonitor().getFromId());
            exchangeDataLog.setToUnitId(webServiceMessage.getUnitId());
            exchangeDataLog.setStatus(-1);
            exchangeDataLog.setMsg("加入重发任务replyClient：" + exchangeDataRepest.getUuid());
            addExchangeDataLog(exchangeDataLog);
            // }
            logger.error("-------------replyClient end success--------------:" + timer.stop());
        } catch (Exception e) {
            logger.error("-------------replyClient end error--------------:" + timer.stop());
            logger.error(e.getMessage(), e);
        }

    }

    /**
     * 平台接口实现
     * 获得撤回的信息
     * 平台撤回需要判断哪些可撤回
     *
     * @param dataId
     * @param recVer
     * @return
     */
    @Override
    @Transactional
    public List<Map<String, Object>> cancelMsg(String sendMonitorUuid) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        try {
            ExchangeDataSendMonitor edsm = exchangeDataSendMonitorService.getOne(sendMonitorUuid);

            Set<ExchangeDataMonitor> exchangeDataMonitors = edsm.getExchangeDataMonitors();
            for (ExchangeDataMonitor exchangeDataMonitor : exchangeDataMonitors) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("unitId", exchangeDataMonitor.getUnitId());
                CommonUnit commonUnit = unitApiFacade.getCommonUnitById(
                        exchangeDataMonitor.getUnitId());
                String unitName = "";
                if (commonUnit != null) {
                    unitName = commonUnit.getName();
                } else {
                    unitName = exchangeDataMonitor.getUnitId();
                }
                map.put("unitName", unitName);
                map.put("matterId", exchangeDataMonitor.getMatterId());
                if (exchangeDataMonitor.getCancelStatus() != null
                        && "success".equals(exchangeDataMonitor.getCancelStatus())) {
                    map.put("status", 0);
                    map.put("msg", "已注销");
                } else if (exchangeDataMonitor.getReplyStatus().equals("fail")) {
                    map.put("status", 0);
                    map.put("msg", "已退回");
                } else if (exchangeDataMonitor.getReplyStatus().equals("reply")) {
                    map.put("status", 0);
                    map.put("msg", "已出证");
                } else if (exchangeDataMonitor.getReplyStatus().equals("transfer")) {
                    map.put("status", 0);
                    map.put("msg", "已转发");
                }
                // else if
                // (exchangeDataMonitor.getReplyStatus().equals("success")) {
                // map.put("status", 0);
                // map.put("msg", "已签收");
                // }
                else {
                    map.put("status", 1);
                    map.put("msg", "");
                }
                list.add(map);
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return list;
    }

    /**
     * 平台接口实现
     * 客户端向平台撤回
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.service.ExchangeDataFlowService#cancel(java.lang.String, java.lang.Integer)
     */
    @Override
    @Transactional
    public CancelResponse cancel(CancelRequest request) {
        Stopwatch timer = Stopwatch.createStarted();
        logger.error("-------------cancel begin---------------");
        try {
            UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
            String dataId = request.getDataId();
            Integer recVer = request.getRecVer();
            String toId = request.getUnitId();
            String matterId = request.getMatterId();
            String msg = request.getMsg();
            String fromId = request.getFromId();
            ExchangeData exchangeData = exchangeDataService.getExchangeDataByDataId(dataId, recVer);
            if (exchangeData == null) {
                CancelResponse cancelResponse = new CancelResponse();
                cancelResponse.setCode(-1);
                cancelResponse.setMsg(
                        "找不到dataId为" + request.getDataId() + ",recVer为" + request.getRecVer() + "的数据");
                return cancelResponse;
            }
            for (ExchangeDataSendMonitor edsm : exchangeData.getExchangeDataSendMonitors()) {
                if (edsm.getFromId().equals(fromId)) {
                    if (toId != null && !toId.equals("")) {
                        List<ExchangeSystem> es = exchangeSystemService.getExchangeSystemsByUnit(
                                toId);
                        if (es.size() == 0) {// 撤回平台上的数据
                            // 写入用户监控到达收件箱
                            Set<ExchangeDataMonitor> exchangeDataMonitors = edsm.getExchangeDataMonitors();
                            int flag = 1;// 所有收件单位都已签收或被退回
                            for (ExchangeDataMonitor edm : exchangeDataMonitors) {
                                if (edm.getUnitId().equals(toId)
                                        && ((edm.getMatterId() != null && edm.getMatterId().equals(
                                        matterId)) || StringUtils
                                        .isBlank(matterId))) {
                                    edm.setCancelMsg(msg);
                                    edm.setCancelStatus("success");
                                    edm.setCancelTime(new Date());
                                    edm.setCancelRequest("yes");
                                    if (!StringUtils.isBlank(
                                            SpringSecurityUtils.getCurrentUserId())) {
                                        edm.setCancelUser(SpringSecurityUtils.getCurrentUserId());
                                    }
                                    exchangeDataMonitorService.save(edm);

                                    // 发送短信通知收件人sendMessage
                                    List<String> userIds = unitApiFacade.getBusinessManageUserIds(
                                            ExchangeConfig.EXCHANGE_BUSINESS_TYPE, toId, 2);
                                    HashSet<String> sets = new HashSet<String>();
                                    for (String userId : userIds) {
                                        sets.add(userId);
                                    }
                                    HashSet<ExchangeDataMonitorBean> entitys = new HashSet<ExchangeDataMonitorBean>();
                                    ExchangeDataMonitorBean edmb = new ExchangeDataMonitorBean();
                                    BeanUtils.copyProperties(edm, edmb);
                                    CommonUnit cu1 = unitApiFacade.getCommonUnitById(
                                            request.getFromId());
                                    if (cu1 != null) {
                                        edmb.setFromId(cu1.getName());
                                    } else {
                                        edmb.setFromUnitName(request.getFromId());
                                    }
                                    edmb.setReservedText1(exchangeData.getReservedText1());
                                    edmb.setReservedText2(exchangeData.getReservedText2());
                                    entitys.add(edmb);
                                    if (ExchangeConfig.TYPE_ID_SSXX_ZTDJ.equals(
                                            exchangeData.getExchangeDataBatch()
                                                    .getTypeId())) {
                                        messageClientApiFacade.send(
                                                ExchangeConfig.EXCHANGE_MESSAGE_CANCEL_SSZT,
                                                entitys, sets);
                                    } else if (ExchangeConfig.TYPE_ID_SSXX_XZXK.equals(exchangeData
                                            .getExchangeDataBatch().getTypeId())) {
                                        messageClientApiFacade.send(
                                                ExchangeConfig.EXCHANGE_MESSAGE_CANCEL_XZXK,
                                                entitys, sets);
                                    } else {
                                        messageClientApiFacade.send(
                                                ExchangeConfig.EXCHANGE_MESSAGE_CANCEL_OTHER,
                                                entitys, sets);
                                    }

                                    /****************添加日志10****************/
                                    ExchangeDataLog exchangeDataLog = new ExchangeDataLog();
                                    exchangeDataLog.setBatchId(
                                            exchangeData.getExchangeDataBatch().getId());
                                    exchangeDataLog.setDataId(exchangeData.getDataId());
                                    exchangeDataLog.setRecVer(exchangeData.getDataRecVer());
                                    exchangeDataLog.setNode(10);
                                    exchangeDataLog.setFromUnitId(edsm.getFromId());
                                    exchangeDataLog.setToUnitId(toId);
                                    exchangeDataLog.setStatus(1);
                                    exchangeDataLog.setMsg("成功撤回无系统单位" + toId + "的数据,撤回原因" + msg);

                                    // 回调源，撤销情况
                                    ClientCancelCallbackRequest clientCancelCallbackRequest = new ClientCancelCallbackRequest();
                                    clientCancelCallbackRequest.setDataId(request.getDataId());
                                    clientCancelCallbackRequest.setRecVer(request.getRecVer());
                                    clientCancelCallbackRequest.setUnitId(request.getUnitId());
                                    clientCancelCallbackRequest.setCode(1);
                                    clientCancelCallbackRequest.setMatterId(matterId);
                                    clientCancelCallbackRequest.setMsg("成功");
                                    ExchangeSystem exchangeSystem = exchangeSystemService
                                            .getExchangeSystemByUnitAndType(request.getFromId(),
                                                    exchangeData
                                                            .getExchangeDataBatch().getTypeId());
                                    if (exchangeSystem != null) {
                                        // try {
                                        // DataExchangeClientWebService
                                        // clientWebService =
                                        // getClientWebService(
                                        // exchangeSystem, "cancelCallbackUrl");
                                        // ClientCancelCallbackResponse rp =
                                        // clientWebService
                                        // .clientCancelCallback(clientCancelCallbackRequest);
                                        // /****************添加日志16****************/
                                        // ExchangeDataLog exchangeDataLog1 =
                                        // new ExchangeDataLog();
                                        // exchangeDataLog1.setBatchId(exchangeData.getExchangeDataBatch().getId());
                                        // exchangeDataLog1.setDataId(exchangeData.getDataId());
                                        // exchangeDataLog1.setRecVer(exchangeData.getDataRecVer());
                                        // exchangeDataLog1.setNode(16);
                                        // exchangeDataLog1.setFromUnitId(edsm.getFromId());
                                        // exchangeDataLog1.setMatterId(matterId);
                                        // exchangeDataLog1.setToUnitId(request.getUnitId());
                                        // exchangeDataLog1.setCode(rp.getCode());
                                        // exchangeDataLog1.setStatus(1);
                                        // exchangeDataLog1.setMsg("调用系统撤回回调接口："
                                        // + rp.getMsg());
                                        // addExchangeDataLog(exchangeDataLog1);
                                        //
                                        // } catch (Exception e) {
                                        /****************加入重发任务****************/
                                        ExchangeDataRepest exchangeDataRepest = new ExchangeDataRepest();
                                        exchangeDataRepest.setDataId(
                                                clientCancelCallbackRequest.getDataId());
                                        exchangeDataRepest.setDataRecVer(
                                                clientCancelCallbackRequest.getRecVer());
                                        exchangeDataRepest.setCode(
                                                clientCancelCallbackRequest.getCode());
                                        exchangeDataRepest.setBatchId(
                                                exchangeData.getExchangeDataBatch().getId());
                                        exchangeDataRepest.setFromId(edsm.getFromId());
                                        exchangeDataRepest.setMsg(
                                                clientCancelCallbackRequest.getMsg());
                                        exchangeDataRepest.setUnitId(
                                                clientCancelCallbackRequest.getUnitId());
                                        exchangeDataRepest.setStatus("ing");
                                        exchangeDataRepest.setMatterId(matterId);
                                        exchangeDataRepest
                                                .setRetransmissionNum(
                                                        ExchangeConfig.EXCHANGE_RETRANSMISSIONNUM);
                                        exchangeDataRepest.setInterval(
                                                ExchangeConfig.EXCHANGE_INTERVER);
                                        exchangeDataRepest.setSendNum(1);
                                        exchangeDataRepest.setSystemUuid(exchangeSystem.getUuid());
                                        exchangeDataRepest.setUserId(userDetails.getUserId());
                                        exchangeDataRepest.setTenantId(userDetails.getTenantId());
                                        exchangeDataRepest.setSendMethod("clientCancelCallback");
                                        exchangeDataRepestService.save(exchangeDataRepest);
                                        /****************添加日志16****************/
                                        ExchangeDataLog exchangeDataLog1 = new ExchangeDataLog();
                                        exchangeDataLog1.setBatchId(
                                                exchangeData.getExchangeDataBatch().getId());
                                        exchangeDataLog1.setDataId(exchangeData.getDataId());
                                        exchangeDataLog1.setRecVer(exchangeData.getDataRecVer());
                                        exchangeDataLog1.setNode(16);
                                        exchangeDataLog1.setFromUnitId(edsm.getFromId());
                                        exchangeDataLog1.setToUnitId(request.getUnitId());
                                        exchangeDataLog1.setStatus(-1);
                                        exchangeDataLog1.setMsg("加入重发任务clientCancelCallback："
                                                + exchangeDataRepest.getUuid());
                                        addExchangeDataLog(exchangeDataLog1);
                                        // }
                                    }
                                } else if (!edm.getReplyStatus().equals("success")
                                        && !edm.getCancelStatus().equals("success")) {// 没签收且没撤回成功
                                    flag = 0;
                                }
                            }
                            // 所有都签收或撤回，发送单位状态已签收完
                            if (flag == 1 && !edsm.getSendNode().equals("abnormal")) {
                                edsm.setSendNode("sign");
                            }
                        } else {
                            // 写入用户监控
                            Set<ExchangeDataMonitor> exchangeDataMonitors = edsm.getExchangeDataMonitors();
                            for (ExchangeDataMonitor edm : exchangeDataMonitors) {
                                if (edm.getUnitId().equals(toId)
                                        && edm.getCancelStatus().equals("default")
                                        && ((edm.getMatterId() != null && edm.getMatterId().equals(
                                        matterId)) || StringUtils
                                        .isBlank(matterId))) {
                                    edm.setCancelRequest("yes");// 请求退回
                                    exchangeDataMonitorService.save(edm);
                                    /****************发消息抄送给单位*************************/
                                    WebServiceMessageSender sender = ApplicationContextHolder
                                            .getBean(WebServiceMessageSender.class);
                                    WebServiceMessage msg1 = new WebServiceMessage();
                                    msg1.setWay("cancelClient");
                                    msg1.setTenantId(userDetails.getTenantId());
                                    msg1.setUserId(userDetails.getUserId());
                                    msg1.setDataId(exchangeData.getDataId());
                                    msg1.setRecVer(exchangeData.getDataRecVer());
                                    msg1.setMonitorUuid(edm.getUuid());
                                    msg1.setMatterId(matterId);
                                    msg1.setFromId(exchangeData.getExchangeDataBatch().getFromId());
                                    msg1.setMsg(msg);
                                    msg1.setUnitId(toId);
                                    sender.send(msg1);// cancelClient

                                    /****************添加日志11****************/
                                    ExchangeDataLog exchangeDataLog = new ExchangeDataLog();
                                    exchangeDataLog.setBatchId(
                                            exchangeData.getExchangeDataBatch().getId());
                                    exchangeDataLog.setDataId(exchangeData.getDataId());
                                    exchangeDataLog.setRecVer(exchangeData.getDataRecVer());
                                    exchangeDataLog.setNode(11);
                                    exchangeDataLog.setFromUnitId(edsm.getFromId());
                                    exchangeDataLog.setToUnitId(toId);
                                    exchangeDataLog.setStatus(1);
                                    exchangeDataLog.setMsg("请求撤回单位" + toId + "的数据");
                                    addExchangeDataLog(exchangeDataLog);
                                }
                            }
                        }
                    } else {
                        CancelResponse cancelResponse = new CancelResponse();
                        cancelResponse.setCode(-1);
                        cancelResponse.setMsg("未传入需要注销的单位");
                        return cancelResponse;
                    }
                }
            }
            CancelResponse cancelResponse = new CancelResponse();
            cancelResponse.setCode(1);
            cancelResponse.setMsg("SUCCESS");
            logger.error("-------------cancel end success--------------:" + timer.stop());
            return cancelResponse;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            CancelResponse cancelResponse = new CancelResponse();
            cancelResponse.setCode(-2);
            cancelResponse.setMsg("程序异常");
            logger.error("-------------cancel end error--------------:" + timer.stop());
            return cancelResponse;
        }

    }

    /**
     * 请求终端
     * 请求终端撤回接口
     *
     * @param webServiceMessage
     */
    @Override
    @Transactional
    public void cancelClient(WebServiceMessage webServiceMessage) {
        Stopwatch timer = Stopwatch.createStarted();
        logger.error("-------------cancelClient begin---------------");
        try {
            ExchangeDataMonitor edm = exchangeDataMonitorService.getOne(
                    webServiceMessage.getMonitorUuid());
            ExchangeDataSendMonitor edsm = edm.getExchangeDataSendMonitor();
            ExchangeData exchangeData = edsm.getExchangeData();
            String systemId = edm.getSystemId();
            ExchangeSystem exchangeSystem = exchangeSystemService.getById(systemId);
            // DataExchangeClientWebService clientWebService =
            // getClientWebService(exchangeSystem, "cancelUrl");
            CancelRequest request = new CancelRequest();
            request.setDataId(exchangeData.getDataId());
            request.setRecVer(exchangeData.getDataRecVer());
            request.setUnitId(webServiceMessage.getUnitId());
            request.setMatterId(webServiceMessage.getMatterId());
            request.setCancelTime(new Date());
            request.setMsg(webServiceMessage.getMsg());
            request.setFromId(webServiceMessage.getFromId());
            // 发送请求
            // try {
            // CancelResponse rp = clientWebService.cancel(request);
            // /****************添加日志12****************/
            // ExchangeDataLog exchangeDataLog = new ExchangeDataLog();
            // exchangeDataLog.setBatchId(exchangeData.getExchangeDataBatch().getId());
            // exchangeDataLog.setDataId(exchangeData.getDataId());
            // exchangeDataLog.setRecVer(exchangeData.getDataRecVer());
            // exchangeDataLog.setNode(12);
            // exchangeDataLog.setFromUnitId(edsm.getFromId());
            // exchangeDataLog.setMatterId(webServiceMessage.getMatterId());
            // exchangeDataLog.setToUnitId(webServiceMessage.getUnitId());
            // exchangeDataLog.setStatus(1);
            // exchangeDataLog.setCode(rp.getCode());
            // if (rp.getCode() == 1) {
            // exchangeDataLog.setMsg("调用系统撤回接口成功");
            // } else {
            // exchangeDataLog.setMsg("调用系统撤回接口失败");
            // }
            // addExchangeDataLog(exchangeDataLog);
            // } catch (Exception e) {
            /****************加入重发任务****************/
            ExchangeDataRepest exchangeDataRepest = new ExchangeDataRepest();
            exchangeDataRepest.setDataId(request.getDataId());
            exchangeDataRepest.setDataRecVer(request.getRecVer());
            exchangeDataRepest.setBatchId(exchangeData.getExchangeDataBatch().getId());
            exchangeDataRepest.setMsg(request.getMsg());
            exchangeDataRepest.setUnitId(request.getUnitId());
            exchangeDataRepest.setMatterId(webServiceMessage.getMatterId());
            exchangeDataRepest.setOpTime(request.getCancelTime());
            exchangeDataRepest.setFromId(request.getFromId());
            exchangeDataRepest.setStatus("ing");
            exchangeDataRepest.setRetransmissionNum(ExchangeConfig.EXCHANGE_RETRANSMISSIONNUM);
            exchangeDataRepest.setInterval(ExchangeConfig.EXCHANGE_INTERVER);
            exchangeDataRepest.setSendNum(1);
            exchangeDataRepest.setSystemUuid(exchangeSystem.getUuid());
            exchangeDataRepest.setSendMethod("cancelClient");
            exchangeDataRepest.setUserId(webServiceMessage.getUserId());
            exchangeDataRepest.setTenantId(webServiceMessage.getTenantId());
            exchangeDataRepest.setExchangeDataMonitorUuid(webServiceMessage.getMonitorUuid());
            exchangeDataRepestService.save(exchangeDataRepest);
            /****************添加日志12****************/
            ExchangeDataLog exchangeDataLog = new ExchangeDataLog();
            exchangeDataLog.setBatchId(exchangeData.getExchangeDataBatch().getId());
            exchangeDataLog.setDataId(exchangeData.getDataId());
            exchangeDataLog.setRecVer(exchangeData.getDataRecVer());
            exchangeDataLog.setNode(12);
            exchangeDataLog.setFromUnitId(edsm.getFromId());
            exchangeDataLog.setToUnitId(webServiceMessage.getUnitId());
            exchangeDataLog.setStatus(-1);
            exchangeDataLog.setMsg("加入重发任务cancelClient：" + exchangeDataRepest.getUuid());
            addExchangeDataLog(exchangeDataLog);
            // }
            logger.error("-------------cancelClient end success--------------:" + timer.stop());
        } catch (Exception e) {
            logger.error("-------------cancelClient end error--------------:" + timer.stop());
            logger.error(e.getMessage(), e);
        }

    }

    /**
     * 平台接口实现
     * 平台撤销回调请求类
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.service.ExchangeDataFlowService#platformCancelCallback(com.wellsoft.pt.integration.request.PlatformCancelCallbackRequest)
     */
    @Override
    @Transactional
    public PlatformCancelCallbackResponse platformCancelCallback(
            PlatformCancelCallbackRequest request) {
        Stopwatch timer = Stopwatch.createStarted();
        logger.error("-------------platformCancelCallback begin---------------");
        try {
            ExchangeData exchangeData = exchangeDataService.getExchangeDataByDataId(
                    request.getDataId(),
                    request.getRecVer());
            if (exchangeData == null) {
                PlatformCancelCallbackResponse platformCancelCallbackResponse = new PlatformCancelCallbackResponse();
                platformCancelCallbackResponse.setCode(-1);
                platformCancelCallbackResponse.setMsg(
                        "找不到dataId为" + request.getDataId() + ",recVer为"
                                + request.getRecVer() + "的数据");
                return platformCancelCallbackResponse;
            }
            for (ExchangeDataSendMonitor edsm : exchangeData.getExchangeDataSendMonitors()) {
                if (edsm.getFromId().equals(request.getUnitId())) {
                    // 写入用户监控到达收件箱
                    Set<ExchangeDataMonitor> exchangeDataMonitors = edsm.getExchangeDataMonitors();
                    int flag = 1;// 所有收件单位都已签收或被退回
                    for (ExchangeDataMonitor edm : exchangeDataMonitors) {
                        if (edm.getUnitId().equals(request.getFromId())
                                && edm.getCancelStatus().equals("default")
                                && ((edm.getMatterId() != null && edm.getMatterId().equals(
                                request.getMatterId())) || StringUtils
                                .isBlank(edm.getMatterId()))) {
                            edm.setCancelMsg(request.getMsg());
                            if (request.getCode() == 1) {
                                edm.setCancelStatus("success");
                            } else {
                                edm.setCancelStatus("fail");
                            }
                            edm.setCancelTime(new Date());
                            exchangeDataMonitorService.save(edm);

                            // 发送短信通知收件人sendMessage
                            List<String> userIds = unitApiFacade.getBusinessManageUserIds(
                                    ExchangeConfig.EXCHANGE_BUSINESS_TYPE, request.getUnitId(), 2);
                            HashSet<String> sets = new HashSet<String>();
                            for (String userId : userIds) {
                                sets.add(userId);
                            }
                            HashSet<ExchangeDataMonitorBean> entitys = new HashSet<ExchangeDataMonitorBean>();
                            ExchangeDataMonitorBean edmb = new ExchangeDataMonitorBean();
                            BeanUtils.copyProperties(edm, edmb);
                            CommonUnit cu1 = unitApiFacade.getCommonUnitById(request.getFromId());
                            if (cu1 != null) {
                                edmb.setFromId(cu1.getName());
                            } else {
                                edmb.setFromUnitName(request.getFromId());
                            }
                            edmb.setReservedText1(exchangeData.getReservedText1());
                            edmb.setReservedText2(exchangeData.getReservedText2());
                            entitys.add(edmb);
                            if (ExchangeConfig.TYPE_ID_SSXX_ZTDJ
                                    .equals(exchangeData.getExchangeDataBatch().getTypeId())) {
                                messageClientApiFacade.send(
                                        ExchangeConfig.EXCHANGE_MESSAGE_CANCEL_SSZT, entitys, sets);
                            } else if (ExchangeConfig.TYPE_ID_SSXX_XZXK.equals(
                                    exchangeData.getExchangeDataBatch()
                                            .getTypeId())) {
                                messageClientApiFacade.send(
                                        ExchangeConfig.EXCHANGE_MESSAGE_CANCEL_XZXK, entitys, sets);
                            } else {
                                messageClientApiFacade
                                        .send(ExchangeConfig.EXCHANGE_MESSAGE_CANCEL_OTHER, entitys,
                                                sets);
                            }
                        } else if (!edm.getReplyStatus().equals(
                                "success") && !edm.getCancelStatus().equals("success")) {
                            flag = 0;
                        }
                    }
                    // 全部签收
                    if (flag == 1 && request.getCode() == 1) {
                        edsm.setSendNode("sign");// 发送单位已签收件
                    }
                    // 回调源，撤销情况
                    ExchangeSystem exchangeSystem = exchangeSystemService.getExchangeSystemByUnitAndType(
                            request.getUnitId(), exchangeData.getExchangeDataBatch().getTypeId());
                    // try {
                    // DataExchangeClientWebService clientWebService =
                    // getClientWebService(exchangeSystem,
                    // "cancelCallbackUrl");
                    // ClientCancelCallbackRequest clientCancelCallbackRequest =
                    // new ClientCancelCallbackRequest();
                    // clientCancelCallbackRequest.setDataId(request.getDataId());
                    // clientCancelCallbackRequest.setRecVer(request.getRecVer());
                    // clientCancelCallbackRequest.setUnitId(request.getFromId());
                    // clientCancelCallbackRequest.setCode(request.getCode());
                    // clientCancelCallbackRequest.setMsg(request.getMsg());
                    // clientCancelCallbackRequest.setMatterId(request.getMatterId());
                    // ClientCancelCallbackResponse rp = clientWebService
                    // .clientCancelCallback(clientCancelCallbackRequest);
                    // /****************添加日志16****************/
                    // ExchangeDataLog exchangeDataLog = new ExchangeDataLog();
                    // exchangeDataLog.setBatchId(exchangeData.getExchangeDataBatch().getId());
                    // exchangeDataLog.setDataId(exchangeData.getDataId());
                    // exchangeDataLog.setRecVer(exchangeData.getDataRecVer());
                    // exchangeDataLog.setNode(16);
                    // exchangeDataLog.setFromUnitId(request.getFromId());
                    // exchangeDataLog.setMatterId(request.getMatterId());
                    // exchangeDataLog.setToUnitId(request.getUnitId());
                    // exchangeDataLog.setCode(rp.getCode());
                    // exchangeDataLog.setStatus(1);
                    // exchangeDataLog.setMsg("调用系统撤回回调接口：" + rp.getMsg());
                    // addExchangeDataLog(exchangeDataLog);
                    // } catch (Exception e) {
                    UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
                    /****************加入重发任务****************/
                    ExchangeDataRepest exchangeDataRepest = new ExchangeDataRepest();
                    exchangeDataRepest.setDataId(request.getDataId());
                    exchangeDataRepest.setDataRecVer(request.getRecVer());
                    exchangeDataRepest.setCode(request.getCode());
                    exchangeDataRepest.setMatterId(request.getMatterId());
                    exchangeDataRepest.setMsg(request.getMsg());
                    exchangeDataRepest.setUnitId(request.getUnitId());
                    exchangeDataRepest.setBatchId(exchangeData.getExchangeDataBatch().getId());
                    exchangeDataRepest.setFromId(request.getFromId());
                    exchangeDataRepest.setStatus("ing");
                    exchangeDataRepest.setRetransmissionNum(
                            ExchangeConfig.EXCHANGE_RETRANSMISSIONNUM);
                    exchangeDataRepest.setInterval(ExchangeConfig.EXCHANGE_INTERVER);
                    exchangeDataRepest.setSendNum(1);
                    exchangeDataRepest.setSystemUuid(exchangeSystem.getUuid());
                    exchangeDataRepest.setUserId(userDetails.getUserId());
                    exchangeDataRepest.setTenantId(userDetails.getTenantId());
                    exchangeDataRepest.setSendMethod("clientCancelCallback");
                    exchangeDataRepestService.save(exchangeDataRepest);
                    /****************添加日志16****************/
                    ExchangeDataLog exchangeDataLog = new ExchangeDataLog();
                    exchangeDataLog.setBatchId(exchangeData.getExchangeDataBatch().getId());
                    exchangeDataLog.setDataId(exchangeData.getDataId());
                    exchangeDataLog.setRecVer(exchangeData.getDataRecVer());
                    exchangeDataLog.setNode(16);
                    exchangeDataLog.setFromUnitId(request.getFromId());
                    exchangeDataLog.setToUnitId(request.getUnitId());
                    exchangeDataLog.setStatus(-1);
                    exchangeDataLog.setMsg(
                            "加入重发任务clientCancelCallback：" + exchangeDataRepest.getUuid());
                    addExchangeDataLog(exchangeDataLog);
                    // } finally {
                    /****************添加日志15****************/
                    ExchangeDataLog exchangeDataLog1 = new ExchangeDataLog();
                    exchangeDataLog1.setBatchId(exchangeData.getExchangeDataBatch().getId());
                    exchangeDataLog1.setDataId(exchangeData.getDataId());
                    exchangeDataLog1.setRecVer(exchangeData.getDataRecVer());
                    exchangeDataLog1.setNode(15);
                    exchangeDataLog1.setFromUnitId(request.getFromId());
                    exchangeDataLog1.setToUnitId(request.getUnitId());
                    exchangeDataLog1.setCode(request.getCode());
                    exchangeDataLog1.setStatus(1);
                    exchangeDataLog1.setMsg("终端撤销回调平台:" + request.getMsg());

                    addExchangeDataLog(exchangeDataLog1);
                    PlatformCancelCallbackResponse platformCancelCallbackResponse = new PlatformCancelCallbackResponse();
                    platformCancelCallbackResponse.setCode(1);
                    platformCancelCallbackResponse.setMsg("成功");
                    return platformCancelCallbackResponse;
                    // }
                }
            }
            PlatformCancelCallbackResponse platformCancelCallbackResponse = new PlatformCancelCallbackResponse();
            platformCancelCallbackResponse.setCode(-1);
            platformCancelCallbackResponse.setMsg("找不到要注销的数据");
            logger.error(
                    "-------------platformCancelCallback end success--------------:" + timer.stop());
            return platformCancelCallbackResponse;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            PlatformCancelCallbackResponse platformCancelCallbackResponse = new PlatformCancelCallbackResponse();
            platformCancelCallbackResponse.setCode(-2);
            platformCancelCallbackResponse.setMsg("程序异常");
            logger.error(
                    "-------------platformCancelCallback end error--------------:" + timer.stop());
            return platformCancelCallbackResponse;
        }

    }

    /**
     * 平台接口实现
     * 补发 (无事项)
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.service.ExchangeDataClientService#reapply(java.lang.Integer, java.lang.String, java.lang.Integer)
     */
    @Override
    @Transactional
    public String reapply(Integer rel, String sendMonitorUuid, String unitIds) {
        Stopwatch timer = Stopwatch.createStarted();
        logger.error("-------------reapply begin---------------");
        try {
            // TODO Auto-generated method stub
            ExchangeDataSendMonitor oldSendMonitor = exchangeDataSendMonitorService.getOne(
                    sendMonitorUuid);
            ExchangeData exchangeData = oldSendMonitor.getExchangeData();
            ExchangeDataBatch edb = exchangeData.getExchangeDataBatch();
            ExchangeDataType exchangeDataType = exchangeDataTypeService.getByTypeId(
                    exchangeData.getExchangeDataBatch()
                            .getTypeId());
            /*******************添加发送人******************/
            ExchangeDataSendMonitor edsm = new ExchangeDataSendMonitor();
            edsm.setExchangeData(exchangeData);
            edsm.setFromId(oldSendMonitor.getFromId());
            edsm.setSendUser(SpringSecurityUtils.getCurrentUserId());
            edsm.setSendTime(new Date());
            try {
                DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                Date dateTemp = format.parse("2013-12-31");
                Date date = format.parse(exchangeData.getReservedText3().toString());// 登记时间
                if (date.after(dateTemp)) {// 登记时间在2014-01-01之后，才计算逾期
                    Date date1 = new Date();// 当前时间
                    if (date.before(date1)) {// 登记时间在当前时间之前
                        WorkPeriod WorkPeriod = basicDataApiFacade.getWorkPeriod(date, date1);
                        int betweenDays = WorkPeriod.getDays() - 1;
                        if (!basicDataApiFacade.isWorkDay(date)) {
                            betweenDays++;
                        }
                        if (exchangeDataType.getReportLimit() != null && exchangeDataType.getReportLimit() != 0
                                && betweenDays > exchangeDataType.getReportLimit()) {
                            // 逾期
                            int t = betweenDays - exchangeDataType.getReportLimit();
                            edsm.setSendLimitNum(t);
                        }
                    } else {
                        // 登记时间在当前时间（上传）之后
                    }
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                logger.info(e.getMessage());
            }
            edsm.setSendType("reapply");
            if (exchangeDataType.getReceiveLimit() != null) {
                Date limitTime = basicDataApiFacade.getWorkDate(edsm.getSendTime(),
                        exchangeDataType.getReceiveLimit() * 1.00, WorkUnit.WorkingDay);
                edsm.setLimitTime(limitTime);
            }
            edsm.setSendNode("ing");
            exchangeDataSendMonitorService.save(edsm);

            Set<ExchangeDataMonitor> lists = oldSendMonitor.getExchangeDataMonitors();
            // 获取路由
            String routeUuid = "";
            String formId = "";
            String formDataUuid = "";
            String xml_ = "";
            for (ExchangeDataMonitor edm : lists) {
                routeUuid = edm.getRouteMsg();
                formId = edm.getFormId();
                formDataUuid = edm.getFormDataUuid();
                try {
                    xml_ = IOUtils.toString(
                            edm.getXml().getCharacterStream()) == null ? "" : IOUtils.toString(
                            edm.getXml().getCharacterStream()).toString();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    logger.info(e.getMessage());
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    logger.info(e.getMessage());
                }
                break;
            }
            if (lists == null || (lists != null && lists.size() == 0)) {
                try {
                    xml_ = IOUtils.toString(
                            exchangeData.getText().getCharacterStream()) == null ? "" : IOUtils
                            .toString(exchangeData.getText().getCharacterStream()).toString();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    logger.info(e.getMessage());
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    logger.info(e.getMessage());
                }
            }
            ExchangeRoute exchangeRoute = exchangeRouteService.getOne(routeUuid);
            if (exchangeRoute == null) {
                List<ExchangeRoute> exchangeRoutes = exchangeRouteService.getExRouteListBySource(
                        edb.getTypeId());
                if (exchangeRoutes == null || (exchangeRoutes != null && exchangeRoutes.size() == 0)) {
                    // 将发送者的状态改为已送达
                    edsm.setSendNode("end");
                    exchangeDataSendMonitorService.save(edsm);
                } else {
                    exchangeRoute = exchangeRoutes.get(0);
                }
            }
            int flag = 0;// 一个数据是否有分发给系统,0无,用于判断是否已到达
            String[] units = unitIds.split(";");
            for (int i = 0; i < units.length; i++) {
                ExchangeSystem esItem = exchangeSystemService.getExchangeSystemByUnitAndType1(
                        units[i],
                        exchangeDataType.getId());
                /************写入用户监控***************/
                ExchangeDataMonitor exchangeDataMonitor = new ExchangeDataMonitor();
                exchangeDataMonitor.setReplyLimitNum(0);
                exchangeDataMonitor.setUnitType("to");
                exchangeDataMonitor.setExchangeDataSendMonitor(edsm);
                exchangeDataMonitor.setUnitId(units[i]);
                exchangeDataMonitor.setReplyStatus("default");
                exchangeDataMonitor.setCancelStatus("default");
                exchangeDataMonitor.setFormId(formId);
                exchangeDataMonitor.setFormDataUuid(formDataUuid);
                exchangeDataMonitor.setXml(exchangeDataService.convertString2Clob(xml_));
                exchangeDataMonitor.setRouteMsg(exchangeRoute.getUuid());
                exchangeDataMonitor.setReceiveNode("wait");
                exchangeDataMonitor.setCancelRequest("no");
                if (!StringUtils.isBlank(exchangeData.getMatterId())) {
                    exchangeDataMonitor.setMatterId(exchangeData.getMatterId());
                }
                if (esItem != null) {// 有系统
                    flag = 1;
                    exchangeDataMonitor.setReceiveStatus("default");
                    exchangeDataMonitor.setSystemId(esItem.getId());
                    exchangeDataMonitorService.save(exchangeDataMonitor);
                    /****************发消息抄送给单位*************************/
                    UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
                    WebServiceMessageSender sender = ApplicationContextHolder.getBean(
                            WebServiceMessageSender.class);
                    WebServiceMessage msg = new WebServiceMessage();
                    msg.setWay("receiveClient");
                    msg.setSystemUuid(esItem.getUuid());
                    msg.setBatchId(edb.getId());
                    msg.setUnitId(edb.getFromId());// 发件人
                    msg.setTypeId(edb.getTypeId());
                    msg.setTenantId(userDetails.getTenantId());
                    msg.setUserId(userDetails.getUserId());
                    msg.setCc(edb.getCc());
                    msg.setBcc(edb.getBcc());
                    msg.setToId(exchangeDataMonitor.getUnitId());
                    msg.setDataId(exchangeData.getDataId());
                    msg.setRecVer(exchangeData.getDataRecVer());
                    msg.setCorrelationDataId(exchangeData.getCorrelationDataId());
                    msg.setCorrelationRecver(exchangeData.getCorrelationRecver());
                    msg.setText(xml_);
                    msg.setRestrain(exchangeRoute.getRestrain());
                    msg.setFormDataUuid(formDataUuid);
                    msg.setMonitorUuid(exchangeDataMonitor.getUuid());
                    sender.send(msg);// receiveClient
                    /*****************写入日志14****************************/
                    ExchangeDataLog exchangeDataLog4 = new ExchangeDataLog();
                    exchangeDataLog4.setBatchId(edb.getId());
                    exchangeDataLog4.setDataId(exchangeData.getDataId());
                    exchangeDataLog4.setRecVer(exchangeData.getDataRecVer());
                    exchangeDataLog4.setNode(14);
                    exchangeDataLog4.setStatus(1);
                    exchangeDataLog4.setFromUnitId(edsm.getFromId());
                    exchangeDataLog4.setToUnitId(units[i]);
                    exchangeDataLog4.setMsg("消息补发到单位系统");
                    addExchangeDataLog(exchangeDataLog4);
                } else {// 无系统
                    // 写入用户监控
                    exchangeDataMonitor.setReceiveStatus("success");
                    exchangeDataMonitor.setReceiveTime(new Date());
                    exchangeDataMonitorService.save(exchangeDataMonitor);
                    /*****************写入日志13****************************/
                    ExchangeDataLog exchangeDataLog4 = new ExchangeDataLog();
                    exchangeDataLog4.setBatchId(edb.getId());
                    exchangeDataLog4.setDataId(exchangeData.getDataId());
                    exchangeDataLog4.setRecVer(exchangeData.getDataRecVer());
                    exchangeDataLog4.setNode(13);
                    exchangeDataLog4.setStatus(1);
                    exchangeDataLog4.setFromUnitId(edsm.getFromId());
                    exchangeDataLog4.setToUnitId(units[i]);
                    exchangeDataLog4.setMsg("补发到平台单位成功");
                    addExchangeDataLog(exchangeDataLog4);
                }
                // 发送短信通知收件人sendMessage
                List<String> userIds = unitApiFacade.getBusinessManageUserIds(
                        ExchangeConfig.EXCHANGE_BUSINESS_TYPE,
                        units[i], 2);
                HashSet<String> sets = new HashSet<String>();
                for (String userId : userIds) {
                    sets.add(userId);
                }
                HashSet<ExchangeDataMonitorBean> entitys = new HashSet<ExchangeDataMonitorBean>();
                ExchangeDataMonitorBean edmb = new ExchangeDataMonitorBean();
                BeanUtils.copyProperties(exchangeDataMonitor, edmb);
                CommonUnit cu1 = unitApiFacade.getCommonUnitById(edsm.getFromId());
                if (cu1 != null) {
                    edmb.setFromId(cu1.getName());
                } else {
                    edmb.setFromUnitName(edsm.getFromId());
                }
                edmb.setReservedText1(exchangeData.getReservedText1());
                edmb.setReservedText2(exchangeData.getReservedText2());
                entitys.add(edmb);
                if (ExchangeConfig.TYPE_ID_SSXX_ZTDJ.equals(
                        exchangeData.getExchangeDataBatch().getTypeId())) {
                    messageClientApiFacade.send(ExchangeConfig.EXCHANGE_MESSAGE_SSZT, entitys,
                            sets);
                } else if (ExchangeConfig.TYPE_ID_SSXX_XZXK.equals(
                        exchangeData.getExchangeDataBatch().getTypeId())) {
                    messageClientApiFacade.send(ExchangeConfig.EXCHANGE_MESSAGE_XZXK, entitys,
                            sets);
                } else {
                    messageClientApiFacade.send(ExchangeConfig.EXCHANGE_MESSAGE_OTHER, entitys,
                            sets);
                }
            }
            // 没有接收单位及没有分发给系统时，发送者状态为已送达（发给单位是同步，实时成功）
            if (flag == 0) {
                // 将发送者的状态改为已送达
                edsm.setSendNode("end");
                exchangeDataSendMonitorService.save(edsm);
            } else if (edsm.getSendNode().equals("back") || edsm.getSendNode().equals(
                    "abnormal")) {// 有退回时不做处理

            } else {
                edsm.setSendNode("ing");
                exchangeDataSendMonitorService.save(edsm);
            }
            logger.error("-------------reapply end success--------------:" + timer.stop());
            return "Success";
        } catch (Exception e) {
            logger.error("-------------reapply end error--------------:" + timer.stop());
            logger.error(e.getMessage(), e);
            return "fail";
        }

    }

    // /**
    // * 平台接口实现
    // * 转发走新路由
    // *
    // * (non-Javadoc)
    // * @see
    // com.wellsoft.pt.integration.service.ExchangeDataFlowService#reapeat(java.lang.Integer,
    // java.lang.String, java.lang.Integer,
    //
    // java.lang.String)
    // */
    // @Override
    // public String reapeat1(Integer rel, String uuid, String unitIds) {
    // // TODO Auto-generated method stub
    // ExchangeDataMonitor monitor = exchangeDataMonitorDao.get(uuid);
    // //ExchangeData exchangeData =
    // exchangeDataDao.getExchangeDataByDataId(dataId, recVer);
    // ExchangeData exchangeData = monitor.getExchangeData();
    // ExchangeDataBatch edb = exchangeData.getExchangeDataBatch();
    // Set<ExchangeDataMonitor> lists = exchangeData.getExchangeDataMonitors();
    // //获取路由
    // String routeUuid = "";
    // String formId = "";
    // String formDataUuid = "";
    // String xml_ = "";
    // for (ExchangeDataMonitor edm : lists) {
    // routeUuid = edm.getRouteMsg();
    // formId = edm.getFormId();
    // formDataUuid = edm.getFormDataUuid();
    // try {
    // xml_ = IOUtils.toString(edm.getXml().getCharacterStream()) == null ? "" :
    // IOUtils.toString(
    // edm.getXml().getCharacterStream()).toString();
    // } catch (IOException e) {
    // // TODO Auto-generated catch block
    // logger.info(e.getMessage());
    // } catch (SQLException e) {
    // // TODO Auto-generated catch block
    // logger.info(e.getMessage());
    // }
    // }
    // ExchangeRoute exchangeRoute = exchangeRouteService.get(routeUuid);
    // ExchangeDataType et = exchangeDataTypeDao.findUniqueBy("formId", formId);
    // //批次
    // ExchangeDataBatch edbnew = new ExchangeDataBatch();
    // if (unitIds != null && !"".equals(unitIds)) {
    // edbnew.setToId(unitIds);
    // edbnew.setAllUnit(unitIds);
    // }
    // List<CommonUnit> unitList =
    // unitApiFacade.getCommonUnitsByBusinessTypeIdAndUserId(
    // ExchangeConfig.EXCHANGE_BUSINESS_TYPE,
    // SpringSecurityUtils.getCurrentUserId());
    // if (unitList != null && unitList.size() > 0) {
    // edbnew.setFromId(unitList.get(0).getId());
    // }
    // edbnew.setTypeId(et.getId());
    // edbnew.setOperateSource(22);
    // if (!StringUtils.isBlank(SpringSecurityUtils.getCurrentUserId())) {
    // edbnew.setFromUserMsg(SpringSecurityUtils.getCurrentUserId() + ":"
    // + SpringSecurityUtils.getCurrentUserName());
    // }
    // edbnew.setLimitTime(edb.getLimitTime());
    // exchangeDataBatchDao.save(edbnew);
    // //数据
    // ExchangeData exchangeDataNew = new ExchangeData();
    // exchangeDataNew.setDataId("DATAID" + UUID.randomUUID());
    // exchangeDataNew.setDataRecVer(1);
    // exchangeDataNew.setCorrelationDataId(exchangeData.getDataId());
    // exchangeDataNew.setCorrelationRecver(exchangeData.getRecVer());
    // exchangeDataNew.setExchangeDataBatch(edbnew);
    // exchangeDataNew.setFormDataId(exchangeData.getFormDataId());
    // exchangeDataNew.setReservedNumber1(exchangeData.getReservedNumber1());
    // exchangeDataNew.setReservedNumber2(exchangeData.getReservedNumber2());
    // exchangeDataNew.setReservedText1(exchangeData.getReservedText1());
    // exchangeDataNew.setReservedText2(exchangeData.getReservedText2());
    // exchangeDataNew.setReservedText3(exchangeData.getReservedText3());
    // exchangeDataNew.setSendLimitNum(exchangeData.getSendLimitNum());
    // exchangeDataNew.setSendLimitStatus(exchangeData.getSendLimitStatus());
    // exchangeDataNew.setSignDigest(exchangeData.getSignDigest());
    // exchangeDataNew.setText(exchangeData.getText());
    // exchangeDataNew.setNewestData("yes");
    // exchangeDataNew.setSendNode("ing");
    // exchangeDataDao.save(exchangeDataNew);
    // //添加发送单位发件权限
    // aclService.addPermission(ExchangeData.class, exchangeDataNew.getUuid(),
    // AclPermission.OUTBOX, "ROLE_UNTI_"
    // + unitList.get(0).getId());
    //
    // int flag = 0;//一个数据是否有分发给系统,0无,用于判断是否已到达
    // String[] units = unitIds.split(";");
    // for (int i = 0; i < units.length; i++) {
    // ExchangeSystem esItem =
    // exchangeSystemDao.getExchangeSystemByUnitAndType1(units[i], et.getId());
    // /************写入用户监控***************/
    // ExchangeDataMonitor exchangeDataMonitor = new ExchangeDataMonitor();
    // exchangeDataMonitor.setExchangeData(exchangeDataNew);
    // exchangeDataMonitor.setUnitId(units[i]);
    // exchangeDataMonitor.setReplyStatus("default");
    // exchangeDataMonitor.setCancelStatus("default");
    // exchangeDataMonitor.setFormId(formId);
    // exchangeDataMonitor.setFormDataUuid(formDataUuid);
    // exchangeDataMonitor.setXml(Hibernate.getLobCreator(exchangeDataDao.getSession()).createClob(xml_));
    // exchangeDataMonitor.setRouteMsg(exchangeRoute.getUuid());
    // exchangeDataMonitor.setReplyLimitStatus(0);
    // exchangeDataMonitor.setReceiveNode("wait");
    // exchangeDataMonitor.setCancelRequest("no");
    // if (esItem != null) {//有系统
    // flag = 1;
    // exchangeDataMonitor.setReceiveStatus("default");
    // exchangeDataMonitor.setSystemId(esItem.getId());
    // exchangeDataMonitorDao.save(exchangeDataMonitor);
    // /****************发消息抄送给单位*************************/
    // UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
    // WebServiceMessageSender sender =
    // ApplicationContextHolder.getBean(WebServiceMessageSender.class);
    // WebServiceMessage msg = new WebServiceMessage();
    // msg.setWay("receiveClient");
    // msg.setSystemUuid(esItem.getUuid());
    // msg.setBatchId(edbnew.getId());
    // msg.setUnitId(edbnew.getFromId());//发件人
    // msg.setTypeId(edbnew.getTypeId());
    // msg.setTenantId(userDetails.getTenantId());
    // msg.setUserId(userDetails.getUserId());
    // msg.setCc(edbnew.getCc());
    // msg.setBcc(edbnew.getBcc());
    // msg.setToId(edbnew.getToId());
    // msg.setDataId(exchangeDataNew.getDataId());
    // msg.setRecVer(exchangeDataNew.getDataRecVer());
    // msg.setCorrelationDataId(exchangeData.getCorrelationDataId());
    // msg.setCorrelationRecver(exchangeData.getCorrelationRecver());
    // msg.setText(xml_);
    // msg.setRestrain(exchangeRoute.getRestrain());
    // msg.setFormDataUuid(formDataUuid);
    // msg.setMonitorUuid(exchangeDataMonitor.getUuid());
    // sender.send(msg);//receiveClient
    // /*****************写入日志41****************************/
    // ExchangeDataLog exchangeDataLog4 = new ExchangeDataLog();
    // exchangeDataLog4.setBatchId(edb.getId());
    // exchangeDataLog4.setDataId(exchangeDataNew.getDataId());
    // exchangeDataLog4.setRecVer(exchangeDataNew.getDataRecVer());
    // exchangeDataLog4.setNode(41);
    // exchangeDataLog4.setStatus(1);
    // exchangeDataLog4.setFromUnitId(edb.getFromId());
    // exchangeDataLog4.setToUnitId(units[i]);
    // exchangeDataLog4.setMsg("将请求终端接收数据接口请求加入队列");
    // addExchangeDataLog(exchangeDataLog4);
    // } else {//无系统
    // //写入用户监控
    // exchangeDataMonitor.setReceiveStatus("success");
    // exchangeDataMonitor.setReceiveTime(new Date());
    // exchangeDataMonitorDao.save(exchangeDataMonitor);
    // /*****************写入日志4****************************/
    // ExchangeDataLog exchangeDataLog4 = new ExchangeDataLog();
    // exchangeDataLog4.setBatchId(edb.getId());
    // exchangeDataLog4.setDataId(exchangeDataNew.getDataId());
    // exchangeDataLog4.setRecVer(exchangeDataNew.getDataRecVer());
    // exchangeDataLog4.setNode(4);
    // exchangeDataLog4.setStatus(1);
    // exchangeDataLog4.setFromUnitId(edb.getFromId());
    // exchangeDataLog4.setToUnitId(units[i]);
    // exchangeDataLog4.setMsg("发送到无系统单位成功");
    // addExchangeDataLog(exchangeDataLog4);
    // }
    // //添加接收者的收件权限
    // aclService.addPermission(ExchangeDataMonitor.class,
    // exchangeDataMonitor.getUuid(), AclPermission.INTBOX,
    // "ROLE_UNTI_MT_" + units[i]);
    // }
    // //没有接收单位及没有分发给系统时，发送者状态为已送达（发给单位是同步，实时成功）
    // if (flag == 0) {
    // //将发送者的状态改为已送达
    // exchangeDataNew.setSendNode("end");
    // exchangeDataDao.save(exchangeDataNew);
    // }
    // return "Success";
    // }

    /**
     * 平台接口实现
     * 分发
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.service.ExchangeDataFlowService#reapeat(java.lang.Integer, java.lang.String, java.lang.Integer,
     * <p>
     * java.lang.String)
     */
    @Override
    @Transactional
    public String reapeat(Integer rel, String uuid, String unitIds) {
        Stopwatch timer = Stopwatch.createStarted();
        logger.error("-------------reapeat begin---------------");
        try {
            // TODO Auto-generated method stub
            ExchangeDataMonitor monitor = exchangeDataMonitorService.getOne(uuid);
            ExchangeDataSendMonitor oldSendMonitor = monitor.getExchangeDataSendMonitor();
            ExchangeData exchangeData = oldSendMonitor.getExchangeData();
            ExchangeDataBatch edb = exchangeData.getExchangeDataBatch();
            ExchangeDataType exchangeDataType = exchangeDataTypeService.getByTypeId(
                    edb.getTypeId());
            /*******************添加发送人******************/
            ExchangeDataSendMonitor edsm = new ExchangeDataSendMonitor();
            edsm.setExchangeData(exchangeData);
            edsm.setFromId(monitor.getUnitId());
            edsm.setSendUser(SpringSecurityUtils.getCurrentUserId());
            edsm.setSendTime(new Date());
            try {
                Date date = monitor.getReplyTime();// 登记时间
                Date date1 = new Date();// 当前时间
                if (date.before(date1)) {// 登记时间在当前时间之前
                    WorkPeriod WorkPeriod = basicDataApiFacade.getWorkPeriod(date, date1);
                    int betweenDays = WorkPeriod.getDays() - 1;
                    if (!basicDataApiFacade.isWorkDay(date)) {
                        betweenDays++;
                    }
                    if (exchangeDataType.getReportLimit() != null && exchangeDataType.getReportLimit() != 0
                            && betweenDays > exchangeDataType.getReportLimit()) {
                        // 逾期
                        int t = betweenDays - exchangeDataType.getReportLimit();
                        edsm.setSendLimitNum(t);
                    }
                } else {
                    // 登记时间在当前时间（上传）之后
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                logger.info(e.getMessage());
            }
            edsm.setSendType("distribution");
            if (exchangeDataType.getReceiveLimit() != null) {
                Date limitTime = basicDataApiFacade.getWorkDate(edsm.getSendTime(),
                        exchangeDataType.getReceiveLimit() * 1.00, WorkUnit.WorkingDay);
                edsm.setLimitTime(limitTime);
            }
            edsm.setSendNode("ing");
            exchangeDataSendMonitorService.save(edsm);

            String routeUuid = monitor.getRouteMsg();
            String formId = monitor.getFormId();
            String xml_ = "";
            String formDataUuid = monitor.getFormDataUuid();
            try {
                xml_ = IOUtils.toString(
                        monitor.getXml().getCharacterStream()) == null ? "" : IOUtils.toString(
                        monitor.getXml().getCharacterStream()).toString();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                logger.info(e.getMessage());
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                logger.info(e.getMessage());
            }

            ExchangeRoute exchangeRoute = exchangeRouteService.getOne(routeUuid);
            ExchangeDataType et = exchangeDataTypeService.getByFormId(formId);
            int flag = 0;// 一个数据是否有分发给系统,0无,用于判断是否已到达
            String[] units = unitIds.split(";");
            for (int i = 0; i < units.length; i++) {
                ExchangeSystem esItem = exchangeSystemService.getExchangeSystemByUnitAndType1(
                        units[i], et.getId());
                /************写入用户监控************/
                ExchangeDataMonitor exchangeDataMonitor = new ExchangeDataMonitor();
                exchangeDataMonitor.setReplyLimitNum(0);
                exchangeDataMonitor.setUnitType("to");
                exchangeDataMonitor.setUnitId(units[i]);
                exchangeDataMonitor.setExchangeDataSendMonitor(edsm);
                exchangeDataMonitor.setReplyStatus("default");
                exchangeDataMonitor.setCancelStatus("default");
                exchangeDataMonitor.setFormId(formId);
                exchangeDataMonitor.setFormDataUuid(formDataUuid);
                exchangeDataMonitor.setXml(exchangeDataService.convertString2Clob(xml_));
                exchangeDataMonitor.setRouteMsg(exchangeRoute.getUuid());
                exchangeDataMonitor.setReceiveNode("wait");
                exchangeDataMonitor.setCancelRequest("no");
                exchangeDataMonitor.setMatter(monitor.getMatter());
                exchangeDataMonitor.setMatterId(monitor.getMatterId());
                if (esItem != null) {// 有系统
                    flag = 1;
                    exchangeDataMonitor.setReceiveStatus("default");
                    exchangeDataMonitor.setSystemId(esItem.getId());
                    exchangeDataMonitorService.save(exchangeDataMonitor);
                    /****************发消息抄送给单位*************************/
                    UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
                    WebServiceMessageSender sender = ApplicationContextHolder.getBean(
                            WebServiceMessageSender.class);
                    WebServiceMessage msg = new WebServiceMessage();
                    msg.setWay("receiveClient");
                    msg.setSystemUuid(esItem.getUuid());
                    msg.setBatchId(edb.getId());
                    msg.setMatterId(monitor.getMatterId());
                    msg.setUnitId(monitor.getUnitId());// 发件人
                    msg.setTypeId(edb.getTypeId());
                    msg.setTenantId(userDetails.getTenantId());
                    msg.setUserId(userDetails.getUserId());
                    msg.setCc(edb.getCc());
                    msg.setBcc(edb.getBcc());
                    msg.setToId(edb.getToId());
                    msg.setDataId(exchangeData.getDataId());
                    msg.setRecVer(exchangeData.getDataRecVer());
                    msg.setCorrelationDataId(exchangeData.getCorrelationDataId());
                    msg.setCorrelationRecver(exchangeData.getCorrelationRecver());
                    msg.setText(xml_);
                    msg.setRestrain(exchangeRoute.getRestrain());
                    msg.setFormDataUuid(formDataUuid);
                    msg.setMonitorUuid(exchangeDataMonitor.getUuid());
                    sender.send(msg);// receiveClient
                    /*****************写入日志41****************************/
                    ExchangeDataLog exchangeDataLog4 = new ExchangeDataLog();
                    exchangeDataLog4.setBatchId(edb.getId());
                    exchangeDataLog4.setDataId(exchangeData.getDataId());
                    exchangeDataLog4.setRecVer(exchangeData.getDataRecVer());
                    exchangeDataLog4.setNode(41);
                    exchangeDataLog4.setStatus(1);
                    exchangeDataLog4.setFromUnitId(monitor.getUnitId());
                    exchangeDataLog4.setToUnitId(units[i]);
                    exchangeDataLog4.setMsg("将请求终端接收数据接口请求加入队列");
                    addExchangeDataLog(exchangeDataLog4);
                } else {// 无系统
                    // 写入用户监控
                    exchangeDataMonitor.setReceiveStatus("success");
                    exchangeDataMonitor.setReceiveTime(new Date());
                    exchangeDataMonitorService.save(exchangeDataMonitor);
                    /*****************写入日志4****************************/
                    ExchangeDataLog exchangeDataLog4 = new ExchangeDataLog();
                    exchangeDataLog4.setBatchId(edb.getId());
                    exchangeDataLog4.setDataId(exchangeData.getDataId());
                    exchangeDataLog4.setRecVer(exchangeData.getDataRecVer());
                    exchangeDataLog4.setNode(4);
                    exchangeDataLog4.setStatus(1);
                    exchangeDataLog4.setFromUnitId(monitor.getUnitId());
                    exchangeDataLog4.setToUnitId(units[i]);
                    exchangeDataLog4.setMsg("发送到无系统单位成功");
                    addExchangeDataLog(exchangeDataLog4);
                }
                // 发送短信通知收件人sendMessage
                List<String> userIds = unitApiFacade.getBusinessManageUserIds(
                        ExchangeConfig.EXCHANGE_BUSINESS_TYPE,
                        units[i], 2);
                HashSet<String> sets = new HashSet<String>();
                for (String userId : userIds) {
                    sets.add(userId);
                }
                HashSet<ExchangeDataMonitorBean> entitys = new HashSet<ExchangeDataMonitorBean>();
                ExchangeDataMonitorBean edmb = new ExchangeDataMonitorBean();
                BeanUtils.copyProperties(exchangeDataMonitor, edmb);
                CommonUnit cu1 = unitApiFacade.getCommonUnitById(edsm.getFromId());
                if (cu1 != null) {
                    edmb.setFromId(cu1.getName());
                } else {
                    edmb.setFromUnitName(edsm.getFromId());
                }
                edmb.setReservedText1(exchangeData.getReservedText1());
                edmb.setReservedText2(exchangeData.getReservedText2());
                entitys.add(edmb);
                if (ExchangeConfig.TYPE_ID_SSXX_ZTDJ.equals(
                        exchangeData.getExchangeDataBatch().getTypeId())) {
                    messageClientApiFacade.send(ExchangeConfig.EXCHANGE_MESSAGE_SSZT, entitys,
                            sets);
                } else if (ExchangeConfig.TYPE_ID_SSXX_XZXK.equals(
                        exchangeData.getExchangeDataBatch().getTypeId())) {
                    messageClientApiFacade.send(ExchangeConfig.EXCHANGE_MESSAGE_XZXK, entitys,
                            sets);
                } else {
                    messageClientApiFacade.send(ExchangeConfig.EXCHANGE_MESSAGE_OTHER, entitys,
                            sets);
                }
            }
            // 没有接收单位及没有分发给系统时，发送者状态为已送达（发给单位是同步，实时成功）
            if (flag == 0) {
                // 将发送者的状态改为已送达
                edsm.setSendNode("end");
                exchangeDataSendMonitorService.save(edsm);
            }
            monitor.setReceiveNode("transfer");
            exchangeDataMonitorService.save(monitor);
            logger.error("-------------reapeat end success--------------:" + timer.stop());
            return "Success";
        } catch (Exception e) {
            logger.error("-------------reapeat end error--------------:" + timer.stop());
            logger.error(e.getMessage(), e);
            return "fail";
        }

    }

    /**
     * 辅助方法
     * 添加日志
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.service.ExchangeDataFlowService#addExchangeDataLog(com.wellsoft.pt.integration.entity.ExchangeDataLog)
     */
    @Override
    public void addExchangeDataLog(ExchangeDataLog exchangeDataLog) {
        // TODO Auto-generated method stub
        exchangeDataLogService.save(exchangeDataLog);
    }

    /**
     * 辅助方法
     * 如何描述该方法
     *
     * @param document
     * @return
     */
    // 将document转化为String
    public String XmlToString(Document document) {
        return document.asXML();
    }

    /**
     * 辅助方法
     * 如何描述该方法
     *
     * @param text
     * @return
     */
    // 将String转化为document
    public Document StringToDocument(String text) {
        try {
            return DocumentHelper.parseText(text);
        } catch (DocumentException e) {
            // TODO Auto-generated catch block
            logger.info(e.getMessage());
            return null;
        }
    }

    /**
     * 辅助方法
     * 获得客户端webservice对象
     *
     * @param exchangeSystem
     * @return
     */
    public DataExchangeClientWebService getClientWebService(ExchangeSystem exchangeSystem,
                                                            String method) {
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
                logger.info(e.getMessage());
            }
            String keyStorePropFile = MerlinCrypto.getKeyStorePropFile();
            outProps.put("mtom-enabled", Boolean.TRUE);
            outProps.put("encryptionUser", alias[0]);
            outProps.put("encryptionPropFile", keyStorePropFile);
            // 服务器证书别名
            outProps.put("signatureUser", server);
            outProps.put("signaturePropFile", keyStorePropFile);
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

    /**
     * 将重发任务加入队列
     *
     * @param webServiceMessage
     */
    @Override
    @Transactional
    public void exchangeDataRepeatTask() {
        Stopwatch timer = Stopwatch.createStarted();
        logger.error("-------------exchangeDataRepeatTask begin---------------");
        try {
            List<ExchangeDataRepest> exchangeDataRepests = exchangeDataRepestService.getExchangeDataRepestIng();
            logger.error(
                    "-------------exchangeDataRepeatTask taskNum:" + exchangeDataRepests.size());
            int i = 1;
            for (ExchangeDataRepest edr : exchangeDataRepests) {
                logger.error(
                        "-------------exchangeDataRepeatTask the " + i + "st uuid:" + edr.getUuid());
                exchangeDataRepestService.save(edr);// 更新修改时间
                WebServiceMessage msg = new WebServiceMessage();
                msg.setExchangeDataRepest(edr);
                msg.setUserId(edr.getUserId());
                msg.setTenantId(edr.getTenantId());
                try {
                    runExchangeDataRepeat(msg);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
                i++;
            }
            logger.error("-------------exchangeDataRepeatTask end success:" + timer.stop());
        } catch (Exception e) {
            logger.error("-------------exchangeDataRepeatTask end error:" + timer.stop());
            logger.error(e.getMessage(), e);
        }

    }

    @Override
    @Transactional
    public void exchangeDataRepeatTaskSSDJ(/* 商事登记单位分发 */) {
        Stopwatch timer = Stopwatch.createStarted();
        logger.error("-------------exchangeDataRepeatTaskSSDJ begin---------------");
        try {
            List<ExchangeDataRepest> exchangeDataRepests = exchangeDataRepestService.getExchangeDataRepestIngSSDJ();
            logger.error(
                    "-------------exchangeDataRepeatTaskSSDJ taskNum:" + exchangeDataRepests.size());
            int i = 1;
            for (ExchangeDataRepest edr : exchangeDataRepests) {
                logger.error(
                        "-------------exchangeDataRepeatTaskSSDJ the " + i + "st uuid:" + edr.getUuid());
                exchangeDataRepestService.save(edr);// 更新修改时间
                WebServiceMessage msg = new WebServiceMessage();
                msg.setExchangeDataRepest(edr);
                msg.setUserId(edr.getUserId());
                msg.setTenantId(edr.getTenantId());
                try {
                    runExchangeDataRepeat(msg);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
                i++;
            }
            logger.error("-------------exchangeDataRepeatTaskSSDJ end success:" + timer.stop());
        } catch (Exception e) {
            logger.error("-------------exchangeDataRepeatTaskSSDJ end error:" + timer.stop());
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void exchangeDataRepeatTaskGSDJ(/* 商事登记工商重发 */) {
        Stopwatch timer = Stopwatch.createStarted();
        logger.error("-------------exchangeDataRepeatTaskGSDJ begin---------------");
        try {
            List<ExchangeDataRepest> exchangeDataRepests = exchangeDataRepestService.getExchangeDataRepestIngGSDJ();
            logger.error(
                    "-------------exchangeDataRepeatTaskGSDJ taskNum:" + exchangeDataRepests.size());
            int i = 1;
            for (ExchangeDataRepest edr : exchangeDataRepests) {
                logger.error(
                        "-------------exchangeDataRepeatTaskGSDJ the " + i + "st uuid:" + edr.getUuid());
                exchangeDataRepestService.save(edr);// 更新修改时间
                WebServiceMessage msg = new WebServiceMessage();
                msg.setExchangeDataRepest(edr);
                msg.setUserId(edr.getUserId());
                msg.setTenantId(edr.getTenantId());
                try {
                    runExchangeDataRepeat(msg);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
                i++;
            }
            logger.error("-------------exchangeDataRepeatTaskGSDJ end success:" + timer.stop());
        } catch (Exception e) {
            logger.error("-------------exchangeDataRepeatTaskGSDJ end error:" + timer.stop());
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void exchangeDataRepeatTaskYZYM(/* 企业设立&一照一码 */) {
        Stopwatch timer = Stopwatch.createStarted();
        logger.error("-------------exchangeDataRepeatTaskYZYM begin---------------");
        try {
            List<ExchangeDataRepest> exchangeDataRepests = exchangeDataRepestService.getExchangeDataRepestIngYZYM();
            logger.error(
                    "-------------exchangeDataRepeatTaskYZYM taskNum:" + exchangeDataRepests.size());
            int i = 1;
            for (ExchangeDataRepest edr : exchangeDataRepests) {
                logger.error(
                        "-------------exchangeDataRepeatTaskYZYM the " + i + "st uuid:" + edr.getUuid());
                exchangeDataRepestService.save(edr);// 更新修改时间
                WebServiceMessage msg = new WebServiceMessage();
                msg.setExchangeDataRepest(edr);
                msg.setUserId(edr.getUserId());
                msg.setTenantId(edr.getTenantId());
                try {
                    runExchangeDataRepeat(msg);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
                i++;
            }
            logger.error("-------------exchangeDataRepeatTaskYZYM end success:" + timer.stop());
        } catch (Exception e) {
            logger.error("-------------exchangeDataRepeatTaskYZYM end error:" + timer.stop());
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * 执行重发任务
     *
     * @param webServiceMessage
     */
    @Override
    @Transactional
    public void runExchangeDataRepeat(WebServiceMessage webServiceMessage) {
        Date starTime = new Date();
        logger.error("-------------runExchangeDataRepeat begin uuid:"
                + webServiceMessage.getExchangeDataRepest().getUuid());
        try {
            ExchangeDataRepest edr = webServiceMessage.getExchangeDataRepest();
            ExchangeSystem exchangeSystem = exchangeSystemService.getOne(edr.getSystemUuid());
            logger.error(
                    "-------------runExchangeDataRepeat exchangeSystem:" + exchangeSystem.getName() + " uuid: "
                            + webServiceMessage.getExchangeDataRepest().getUuid());
            if (edr.getSendMethod().equals("callbackClient")) {
                logger.error("-------------runExchangeDataRepeat callbackClient uuid:"
                        + webServiceMessage.getExchangeDataRepest().getUuid());
                DataExchangeClientWebService clientWebService = getClientWebService(exchangeSystem,
                        "sendCallbackUrl");
                ClientSendCallbackRequest message = new ClientSendCallbackRequest();
                message.setBatchId(edr.getBatchId());
                message.setCode(edr.getCode());
                message.setMsg(edr.getMsg());
                try {
                    ClientSendCallbackResponse rp = clientWebService.clientSendCallback(message);
                    edr.setStatus("success");
                    exchangeDataRepestService.save(edr);
                    /******************写入日志17***********************/
                    ExchangeDataLog exchangeDataLog = new ExchangeDataLog();
                    exchangeDataLog.setNode(17);
                    exchangeDataLog.setBatchId(message.getBatchId());
                    exchangeDataLog.setFromUnitId(edr.getFromId());
                    exchangeDataLog.setStatus(1);
                    exchangeDataLog.setCode(rp.getCode());
                    exchangeDataLog.setMsg(
                            "重发任务" + edr.getSendNum() + ",调问终端上传结果回调接口日志: " + rp.getMsg());
                    addExchangeDataLog(exchangeDataLog);
                    Date endTimer2 = new Date();
                    long difference2 = (endTimer2.getTime() - starTime.getTime());
                    logger.error(
                            "-------------runExchangeDataRepeat callbackClient end success:" + difference2
                                    + " uuid:" + webServiceMessage.getExchangeDataRepest().getUuid());
                } catch (Exception e) {
                    edr.setSendNum(edr.getSendNum() + 1);
                    if (edr.getSendNum() >= edr.getRetransmissionNum()) {
                        // edr.setStatus("fail");
                    }
                    exchangeDataRepestService.save(edr);
                    /******************写入日志17***********************/
                    ExchangeDataLog exchangeDataLog = new ExchangeDataLog();
                    exchangeDataLog.setNode(3);
                    exchangeDataLog.setBatchId(message.getBatchId());
                    exchangeDataLog.setFromUnitId(edr.getFromId());
                    exchangeDataLog.setStatus(-1);
                    exchangeDataLog.setMsg("重发任务" + edr.getSendNum() + ",调问终端上传结果回调接口日志: 请求失败");
                    addExchangeDataLog(exchangeDataLog);
                    Date endTimer2 = new Date();
                    long difference2 = (endTimer2.getTime() - starTime.getTime());
                    logger.error(
                            "-------------runExchangeDataRepeat callbackClient end error:" + difference2
                                    + " uuid:" + webServiceMessage.getExchangeDataRepest().getUuid());
                    logger.error(e.getMessage(), e);
                }
            } else if (edr.getSendMethod().equals("receiveClient")) {
                logger.error("-------------runExchangeDataRepeat receiveClient uuid:"
                        + webServiceMessage.getExchangeDataRepest().getUuid());
                DataExchangeClientWebService clientWebService = getClientWebService(exchangeSystem,
                        "receiveUrl");
                ExchangeDataMonitor exchangeDataMonitor = exchangeDataMonitorService.getOne(edr
                        .getExchangeDataMonitorUuid());
                ExchangeDataSendMonitor edsm = exchangeDataMonitor.getExchangeDataSendMonitor();
                ExchangeData exchangeData = edsm.getExchangeData();
                ExchangeDataBatch exchangeDataBatch = exchangeData.getExchangeDataBatch();
                // 构造发送对象
                ReceiveRequest request = new ReceiveRequest();
                request.setBatchId(exchangeDataBatch.getId());
                request.setBcc(exchangeDataBatch.getBcc());
                request.setCc(exchangeDataBatch.getCc());
                request.setFrom(exchangeDataBatch.getFromId());
                request.setTo(exchangeDataMonitor.getUnitId());
                request.setTypeId(exchangeDataBatch.getTypeId());
                List<DataItem> dataItems = new ArrayList<DataItem>();
                DataItem di = new DataItem();
                di.setDataId(exchangeData.getDataId());
                di.setRecVer(exchangeData.getDataRecVer());
                String xmlStr;
                try {
                    xmlStr = IOUtils.toString(
                            exchangeDataMonitor.getXml().getCharacterStream()) == null ? "" : IOUtils
                            .toString(exchangeDataMonitor.getXml().getCharacterStream()).toString();
                    xmlStr = xmlStr.replaceAll("uf_", "userform_");
                    di.setText(xmlStr);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    logger.info(e.getMessage());
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    logger.info(e.getMessage());
                }
                Map<String, Object> props = new HashMap<String, Object>();
                try {
                    props = MerlinCrypto.getInstace().getWSS4JAttachmentPropsForDN(
                            exchangeSystem.getSubjectDN());
                } catch (WSSecurityException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                List<StreamingData> streamingDatas = new ArrayList<StreamingData>();
                List<MongoFileEntity> mongoFileEntitys = mongoFileService.getFilesFromFolder(
                        exchangeDataMonitor.getFormDataUuid(), null);
                for (MongoFileEntity mongoFileEntity : mongoFileEntitys) {
                    StreamingData sd = new StreamingData();
                    sd.setFileName(mongoFileEntity.getFileName());
                    sd.setDataHandler(new DataHandler(
                            new InputStreamDataSource(mongoFileEntity.getInputstream(),
                                    "octet-stream")));
                    WSS4JOutAttachment wss4jOutAttachment2;
                    try {
                        wss4jOutAttachment2 = new WSS4JOutAttachment(sd, props);
                        wss4jOutAttachment2.signAndEncrypt();
                        streamingDatas.add(wss4jOutAttachment2.getEncryptedStreamingData());
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        logger.info(e.getMessage());
                    }
                }
                di.setStreamingDatas(streamingDatas);
                di.setMatterId(exchangeDataMonitor.getMatterId());
                dataItems.add(di);
                request.setDataList(dataItems);
                try {
                    // 调用WebService发送
                    ReceiveResponse wp = clientWebService.receive(request);
                    edr.setStatus("success");
                    exchangeDataRepestService.save(edr);
                    /*****************写入日志17****************************/
                    ExchangeDataLog exchangeDataLog5 = new ExchangeDataLog();
                    exchangeDataLog5.setBatchId(exchangeDataBatch.getId());
                    exchangeDataLog5.setDataId(exchangeData.getDataId());
                    exchangeDataLog5.setRecVer(exchangeData.getDataRecVer());
                    exchangeDataLog5.setNode(17);
                    exchangeDataLog5.setCode(wp.getCode());
                    exchangeDataLog5.setStatus(1);
                    exchangeDataLog5.setFromUnitId(edsm.getFromId());
                    exchangeDataLog5.setToUnitId(exchangeDataMonitor.getUnitId());
                    exchangeDataLog5.setMsg("重发任务,调用终端接收数据接口日志:成功，" + ShutString(wp.getMsg(), 50));
                    addExchangeDataLog(exchangeDataLog5);
                    Date endTimer2 = new Date();
                    long difference2 = (endTimer2.getTime() - starTime.getTime());
                    logger.error(
                            "-------------runExchangeDataRepeat receiveClient end success:" + difference2
                                    + " uuid:" + webServiceMessage.getExchangeDataRepest().getUuid() + " status:"
                                    + edr.getStatus());
                } catch (Exception e) {
                    edr.setSendNum(edr.getSendNum() + 1);
                    if (edr.getSendNum() >= edr.getRetransmissionNum()) {
                        // edr.setStatus("fail");
                        // edsm.setSendNode("abnormal");
                        // exchangeDataDao.save(exchangeData);
                    }
                    exchangeDataRepestService.save(edr);
                    /*****************写入日志17****************************/
                    ExchangeDataLog exchangeDataLog5 = new ExchangeDataLog();
                    exchangeDataLog5.setBatchId(exchangeDataBatch.getId());
                    exchangeDataLog5.setDataId(exchangeData.getDataId());
                    exchangeDataLog5.setRecVer(exchangeData.getDataRecVer());
                    exchangeDataLog5.setNode(17);
                    exchangeDataLog5.setStatus(-1);
                    exchangeDataLog5.setFromUnitId(edsm.getFromId());
                    exchangeDataLog5.setToUnitId(exchangeSystem.getUnitId());
                    exchangeDataLog5.setMsg("重发任务,调用终端接收数据接口日志:失败");
                    addExchangeDataLog(exchangeDataLog5);
                    Date endTimer2 = new Date();
                    long difference2 = (endTimer2.getTime() - starTime.getTime());
                    logger.error(
                            "-------------runExchangeDataRepeat receiveClient end error:" + difference2 + " uuid:"
                                    + webServiceMessage.getExchangeDataRepest().getUuid());
                    logger.error(e.getMessage(), e);
                }
            } else if (edr.getSendMethod().equals("routeCallBackClient")) {
                logger.error("-------------runExchangeDataRepeat routeCallBackClient uuid:"
                        + webServiceMessage.getExchangeDataRepest().getUuid());
                DataExchangeClientWebService clientWebService = getClientWebService(exchangeSystem,
                        "routeCallbackUrl");
                RouteCallbackRequest routeCallbackMessage = new RouteCallbackRequest();
                routeCallbackMessage.setDataId(edr.getDataId());
                routeCallbackMessage.setRecVer(edr.getDataRecVer());
                routeCallbackMessage.setUnitId(edr.getUnitId());
                routeCallbackMessage.setMatterId(edr.getMatterId());
                routeCallbackMessage.setCode(edr.getCode());
                routeCallbackMessage.setMsg(edr.getMsg());
                try {
                    RouteCallbackResponse rp = clientWebService.routeCallback(routeCallbackMessage);
                    edr.setStatus("success");
                    exchangeDataRepestService.save(edr);
                    /****************添加日志17****************/
                    ExchangeDataLog exchangeDataLog = new ExchangeDataLog();
                    exchangeDataLog.setBatchId(edr.getBatchId());
                    exchangeDataLog.setDataId(edr.getDataId());
                    exchangeDataLog.setRecVer(edr.getDataRecVer());
                    exchangeDataLog.setNode(17);
                    exchangeDataLog.setFromUnitId(edr.getFromId());
                    exchangeDataLog.setToUnitId(edr.getUnitId());
                    exchangeDataLog.setStatus(1);
                    exchangeDataLog.setCode(edr.getCode());
                    exchangeDataLog.setMsg("重发任务,调用终端路由结果接口日志:" + edr.getMsg());
                    addExchangeDataLog(exchangeDataLog);
                    /****************添加路由信息到表里****************/
                    ExchangeDataRoute edroute = new ExchangeDataRoute();
                    edroute.setDataId(webServiceMessage.getDataId());
                    edroute.setMsg(webServiceMessage.getMsg());
                    edroute.setRecVer(webServiceMessage.getRecVer());
                    edroute.setMatterId(webServiceMessage.getMatterId());
                    if (rp.getCode() == 1) {
                        edroute.setStatus(true);
                    } else {
                        edroute.setStatus(false);
                    }
                    edroute.setUnitId(webServiceMessage.getUnitId());
                    exchangeDataRouteService.save(edroute);
                    Date endTimer2 = new Date();
                    long difference2 = (endTimer2.getTime() - starTime.getTime());
                    logger.error(
                            "-------------runExchangeDataRepeat routeCallBackClient end success:" + difference2
                                    + " uuid:" + webServiceMessage.getExchangeDataRepest().getUuid());
                } catch (Exception e) {
                    edr.setSendNum(edr.getSendNum() + 1);
                    if (edr.getSendNum() >= edr.getRetransmissionNum()) {
                        // edr.setStatus("fail");
                    }
                    exchangeDataRepestService.save(edr);
                    /****************添加日志17****************/
                    ExchangeDataLog exchangeDataLog = new ExchangeDataLog();
                    exchangeDataLog.setBatchId(edr.getBatchId());
                    exchangeDataLog.setDataId(edr.getDataId());
                    exchangeDataLog.setRecVer(edr.getDataRecVer());
                    exchangeDataLog.setNode(17);
                    exchangeDataLog.setFromUnitId(edr.getFromId());
                    exchangeDataLog.setToUnitId(edr.getUnitId());
                    exchangeDataLog.setStatus(-1);
                    exchangeDataLog.setMsg("重发任务,调用终端路由结果接口日志:请求失败");
                    addExchangeDataLog(exchangeDataLog);
                    Date endTimer2 = new Date();
                    long difference2 = (endTimer2.getTime() - starTime.getTime());
                    logger.error(
                            "-------------runExchangeDataRepeat routeCallBackClient end error:" + difference2
                                    + " uuid:" + webServiceMessage.getExchangeDataRepest().getUuid());
                    logger.error(e.getMessage(), e);
                }
            } else if (edr.getSendMethod().equals("replyClient")) {
                logger.error("-------------runExchangeDataRepeat replyClient uuid:"
                        + webServiceMessage.getExchangeDataRepest().getUuid());
                DataExchangeClientWebService clientWebService = getClientWebService(exchangeSystem,
                        "replyMsgUrl");
                ReplyRequest replyMessage = new ReplyRequest();
                replyMessage.setDataId(edr.getDataId());
                replyMessage.setRecVer(edr.getDataRecVer());
                replyMessage.setCode(edr.getCode());
                replyMessage.setUnitId(edr.getUnitId());
                replyMessage.setMatterId(edr.getMatterId());
                replyMessage.setReplyTime(edr.getOpTime());
                replyMessage.setMsg(edr.getMsg());
                // 发送请求
                try {
                    ReplyResponse rp = clientWebService.replyMsg(replyMessage);
                    edr.setStatus("success");
                    exchangeDataRepestService.save(edr);
                    /****************添加日志17****************/
                    ExchangeDataLog exchangeDataLog = new ExchangeDataLog();
                    exchangeDataLog.setBatchId(edr.getBatchId());
                    exchangeDataLog.setDataId(edr.getDataId());
                    exchangeDataLog.setRecVer(edr.getDataRecVer());
                    exchangeDataLog.setNode(17);
                    exchangeDataLog.setFromUnitId(edr.getFromId());
                    exchangeDataLog.setToUnitId(edr.getUnitId());
                    exchangeDataLog.setStatus(1);
                    exchangeDataLog.setCode(rp.getCode());
                    exchangeDataLog.setMsg("重发任务,调用终端签收结果接口日志:" + edr.getMsg());
                    addExchangeDataLog(exchangeDataLog);
                    Date endTimer2 = new Date();
                    long difference2 = (endTimer2.getTime() - starTime.getTime());
                    logger.error(
                            "-------------runExchangeDataRepeat replyClient end success:" + difference2 + " uuid:"
                                    + webServiceMessage.getExchangeDataRepest().getUuid());
                } catch (Exception e) {
                    edr.setSendNum(edr.getSendNum() + 1);
                    if (edr.getSendNum() >= edr.getRetransmissionNum()) {
                        // edr.setStatus("fail");
                    }
                    exchangeDataRepestService.save(edr);
                    /****************添加日志17****************/
                    ExchangeDataLog exchangeDataLog = new ExchangeDataLog();
                    exchangeDataLog.setBatchId(edr.getBatchId());
                    exchangeDataLog.setDataId(edr.getDataId());
                    exchangeDataLog.setRecVer(edr.getDataRecVer());
                    exchangeDataLog.setNode(17);
                    exchangeDataLog.setFromUnitId(edr.getFromId());
                    exchangeDataLog.setToUnitId(edr.getUnitId());
                    exchangeDataLog.setStatus(-1);
                    exchangeDataLog.setMsg("重发任务,调用终端签收结果接口日志:请求失败");
                    addExchangeDataLog(exchangeDataLog);
                    Date endTimer2 = new Date();
                    long difference2 = (endTimer2.getTime() - starTime.getTime());
                    logger.error(
                            "-------------runExchangeDataRepeat replyClient end error:" + difference2 + " uuid:"
                                    + webServiceMessage.getExchangeDataRepest().getUuid());
                    logger.error(e.getMessage(), e);
                }

            } else if (edr.getSendMethod().equals("clientCancelCallback")) {
                logger.error("-------------runExchangeDataRepeat clientCancelCallback uuid"
                        + webServiceMessage.getExchangeDataRepest().getUuid());
                DataExchangeClientWebService clientWebService = getClientWebService(exchangeSystem,
                        "cancelCallbackUrl");
                // 回调源，撤销情况
                ClientCancelCallbackRequest clientCancelCallbackRequest = new ClientCancelCallbackRequest();
                clientCancelCallbackRequest.setDataId(edr.getDataId());
                clientCancelCallbackRequest.setRecVer(edr.getDataRecVer());
                clientCancelCallbackRequest.setUnitId(edr.getFromId());
                clientCancelCallbackRequest.setMatterId(edr.getMatterId());
                clientCancelCallbackRequest.setCode(1);
                clientCancelCallbackRequest.setMsg("成功");
                try {
                    ClientCancelCallbackResponse rp = clientWebService
                            .clientCancelCallback(clientCancelCallbackRequest);
                    edr.setStatus("success");
                    exchangeDataRepestService.save(edr);
                    /****************添加日志17****************/
                    ExchangeDataLog exchangeDataLog1 = new ExchangeDataLog();
                    exchangeDataLog1.setBatchId(edr.getBatchId());
                    exchangeDataLog1.setDataId(edr.getDataId());
                    exchangeDataLog1.setRecVer(edr.getDataRecVer());
                    exchangeDataLog1.setNode(17);
                    exchangeDataLog1.setFromUnitId(edr.getFromId());
                    exchangeDataLog1.setToUnitId(edr.getUnitId());
                    exchangeDataLog1.setStatus(1);
                    exchangeDataLog1.setCode(rp.getCode());
                    exchangeDataLog1.setMsg("重发任务,调用系统撤回回调接口：" + rp.getMsg());
                    addExchangeDataLog(exchangeDataLog1);
                    Date endTimer2 = new Date();
                    long difference2 = (endTimer2.getTime() - starTime.getTime());
                    logger.error(
                            "-------------runExchangeDataRepeat clientCancelCallback end success:" + difference2
                                    + " uuid:" + webServiceMessage.getExchangeDataRepest().getUuid());
                } catch (Exception e) {
                    edr.setSendNum(edr.getSendNum() + 1);
                    if (edr.getSendNum() >= edr.getRetransmissionNum()) {
                        // edr.setStatus("fail");
                    }
                    exchangeDataRepestService.save(edr);
                    /****************添加日志17****************/
                    ExchangeDataLog exchangeDataLog1 = new ExchangeDataLog();
                    exchangeDataLog1.setBatchId(edr.getBatchId());
                    exchangeDataLog1.setDataId(edr.getDataId());
                    exchangeDataLog1.setRecVer(edr.getDataRecVer());
                    exchangeDataLog1.setNode(17);
                    exchangeDataLog1.setFromUnitId(edr.getFromId());
                    exchangeDataLog1.setToUnitId(edr.getUnitId());
                    exchangeDataLog1.setStatus(-1);
                    exchangeDataLog1.setMsg("重发任务,调用系统撤回回调接口：请求失败");
                    addExchangeDataLog(exchangeDataLog1);
                    Date endTimer2 = new Date();
                    long difference2 = (endTimer2.getTime() - starTime.getTime());
                    logger.error(
                            "-------------runExchangeDataRepeat clientCancelCallback end error:" + difference2
                                    + " uuid:" + webServiceMessage.getExchangeDataRepest().getUuid());
                    logger.error(e.getMessage(), e);
                }

            } else if (edr.getSendMethod().equals("cancelClient")) {
                logger.error("-------------runExchangeDataRepeat cancelClient uuid:"
                        + webServiceMessage.getExchangeDataRepest().getUuid());
                DataExchangeClientWebService clientWebService = getClientWebService(exchangeSystem,
                        "cancelUrl");
                CancelRequest request = new CancelRequest();
                request.setDataId(edr.getDataId());
                request.setRecVer(edr.getDataRecVer());
                request.setUnitId(edr.getUnitId());
                request.setMatterId(edr.getMatterId());
                request.setCancelTime(edr.getOpTime());
                request.setMsg(edr.getMsg());
                request.setFromId(edr.getFromId());
                // 发送请求
                try {
                    CancelResponse rp = clientWebService.cancel(request);
                    edr.setStatus("success");
                    exchangeDataRepestService.save(edr);
                    /****************添加日志17****************/
                    ExchangeDataLog exchangeDataLog = new ExchangeDataLog();
                    exchangeDataLog.setBatchId(edr.getBatchId());
                    exchangeDataLog.setDataId(edr.getDataId());
                    exchangeDataLog.setRecVer(edr.getDataRecVer());
                    exchangeDataLog.setNode(17);
                    exchangeDataLog.setFromUnitId(edr.getFromId());
                    exchangeDataLog.setToUnitId(edr.getUnitId());
                    exchangeDataLog.setStatus(1);
                    exchangeDataLog.setCode(rp.getCode());
                    if (rp.getCode() == 1) {
                        exchangeDataLog.setMsg("重发任务,调用系统撤回接口成功");
                    } else {
                        exchangeDataLog.setMsg("重发任务,调用系统撤回接口失败");
                    }
                    addExchangeDataLog(exchangeDataLog);
                    Date endTimer2 = new Date();
                    long difference2 = (endTimer2.getTime() - starTime.getTime());
                    logger.error(
                            "-------------runExchangeDataRepeat cancelClient end success:" + difference2
                                    + " uuid:" + webServiceMessage.getExchangeDataRepest().getUuid());
                } catch (Exception e) {
                    edr.setSendNum(edr.getSendNum() + 1);
                    if (edr.getSendNum() >= edr.getRetransmissionNum()) {
                        // edr.setStatus("fail");
                    }
                    exchangeDataRepestService.save(edr);
                    /****************添加日志17****************/
                    ExchangeDataLog exchangeDataLog = new ExchangeDataLog();
                    exchangeDataLog.setBatchId(edr.getBatchId());
                    exchangeDataLog.setDataId(edr.getDataId());
                    exchangeDataLog.setRecVer(edr.getDataRecVer());
                    exchangeDataLog.setNode(17);
                    exchangeDataLog.setFromUnitId(edr.getFromId());
                    exchangeDataLog.setToUnitId(edr.getUnitId());
                    exchangeDataLog.setStatus(-1);
                    exchangeDataLog.setMsg("重发任务,调用系统撤回接口：请求失败");
                    addExchangeDataLog(exchangeDataLog);
                    Date endTimer2 = new Date();
                    long difference2 = (endTimer2.getTime() - starTime.getTime());
                    logger.error(
                            "-------------runExchangeDataRepeat cancelClient end error:" + difference2 + " uuid:"
                                    + webServiceMessage.getExchangeDataRepest().getUuid());
                    logger.error(e.getMessage(), e);
                }
            } else if (edr.getSendMethod().equals("dxSendClient")) {
                logger.error("-------------runExchangeDataRepeat dxSendClient uuid:"
                        + webServiceMessage.getExchangeDataRepest().getUuid());
                DataExchangeClientWebService clientWebService = getClientWebService(exchangeSystem,
                        "receiveUrl");

                DXExchangeRouteDate dxDataRoute = dXExchangeRouteDateService.getOne(
                        edr.getExchangeDataMonitorUuid());
                String dxBatchUuid = dxDataRoute.getdXExchangeBatch().getUuid();
                DXExchangeBatch dXExchangeBatch = this.dXExchangeBatchService.getOne(dxBatchUuid);
                ExchangeDataType exchangeDataType = exchangeDataTypeService.getByTypeId(
                        dXExchangeBatch.getTypeId());// 数据类型
                // 构建发件对象发送数据
                DXRequest dXRequest = new DXRequest();
                dXRequest.setBatchId(dXExchangeBatch.getId());
                dXRequest.setBcc(dXExchangeBatch.getBcc());
                dXRequest.setCc(dXExchangeBatch.getCc());
                dXRequest.setFrom(dXExchangeBatch.getFromId());
                if (!StringUtils.isBlank(dXExchangeBatch.getParams())) {
                    dXRequest.setParams(JsonUtils.toMap(dXExchangeBatch.getParams()));
                }
                dXRequest.setTo(dXExchangeBatch.getToId());
                dXRequest.setTypeId(dXExchangeBatch.getTypeId());

                Map<String, Object> props = new HashMap<String, Object>();
                try {
                    props = MerlinCrypto.getInstace().getWSS4JAttachmentPropsForDN(
                            exchangeSystem.getSubjectDN());
                } catch (WSSecurityException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }

                List<DXDataItem> dataList = new ArrayList<DXDataItem>();
                List<DXExchangeData> dxDatas = dXExchangeDataService.getDXExchangeDatasByBatchUuid(
                        dxBatchUuid);
                for (DXExchangeData dxData : dxDatas) {
                    // 对附件流进行加密
                    DXDataItem dXDataItem = new DXDataItem();
                    dXDataItem.setText(IOUtils.toString(
                            dxData.getText().getCharacterStream()) == null ? "" : IOUtils
                            .toString(dxData.getText().getCharacterStream()).toString());
                    dXDataItem.setRecVer(dxData.getDataRecVer());
                    dXDataItem.setDataId(dxData.getDataId());
                    if (!StringUtils.isBlank(dxData.getParams())) {
                        dXDataItem.setParams(JsonUtils.toMap(dxData.getParams()));
                    }

                    List<StreamingData> streamingDatas = new ArrayList<StreamingData>();
                    if (!StringUtils.isBlank(exchangeDataType.getFormId())) {// 类型不对应表单时
                        List<MongoFileEntity> mongoFileEntitys = mongoFileService.getFilesFromFolder(
                                dxData.getFormDataUuid(), null);
                        for (MongoFileEntity mongoFileEntity : mongoFileEntitys) {
                            StreamingData sd = new StreamingData();
                            sd.setFileName(mongoFileEntity.getFileName());
                            sd.setDataHandler(
                                    new DataHandler(new InputStreamDataSource(mongoFileEntity
                                            .getInputstream(), "octet-stream")));
                            if (isZHONGXIAN(exchangeSystem.getExchangeType())
                                    || (exchangeSystem.getIsEnableCa() != null && !exchangeSystem.getIsEnableCa())) {// 总线及非ca
                                streamingDatas.add(sd);
                            } else {
                                WSS4JOutAttachment wss4jOutAttachment2;
                                try {
                                    wss4jOutAttachment2 = new WSS4JOutAttachment(sd, props);
                                    wss4jOutAttachment2.signAndEncrypt();
                                    streamingDatas.add(
                                            wss4jOutAttachment2.getEncryptedStreamingData());
                                } catch (Exception e) {
                                    // TODO Auto-generated catch block
                                    logger.info(e.getMessage());
                                }
                            }
                        }
                    } else {
                        String fieldMsg = dxData.getFormDataUuid();
                        if (!StringUtils.isBlank(fieldMsg)) {
                            String[] fieldArray = fieldMsg.split(";");
                            for (int ii = 0; ii < fieldArray.length; ii++) {
                                StreamingData sd = new StreamingData();
                                sd.setFileName(fieldArray[ii].split(":")[0]);
                                MongoFileEntity mongoFileEntity = mongoFileService
                                        .getFile(fieldArray[ii].split(":")[1]);
                                if (mongoFileEntity != null) {
                                    sd.setDataHandler(new DataHandler(
                                            new InputStreamDataSource(mongoFileEntity
                                                    .getInputstream(), "octet-stream")));
                                    if (isZHONGXIAN(exchangeSystem.getExchangeType())
                                            || (exchangeSystem.getIsEnableCa() != null && !exchangeSystem
                                            .getIsEnableCa())) {// 总线及非ca
                                        streamingDatas.add(sd);
                                    } else {
                                        WSS4JOutAttachment wss4jOutAttachment2;
                                        try {
                                            wss4jOutAttachment2 = new WSS4JOutAttachment(sd, props);
                                            wss4jOutAttachment2.signAndEncrypt();
                                            streamingDatas.add(
                                                    wss4jOutAttachment2.getEncryptedStreamingData());
                                        } catch (Exception e1) {
                                            // TODO Auto-generated catch block
                                            e1.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }
                    }
                    dXDataItem.setStreamingDatas(streamingDatas);
                    dataList.add(dXDataItem);
                }
                dXRequest.setDataList(dataList);
                DXResponse dXResponse = new DXResponse();
                try {
                    // 调用WebService发送
                    if (isZHONGXIAN(exchangeSystem.getExchangeType())) {// 判断是否走畅享业务协同平台交换
                        try {
                            if (zongXianExchangeService.submit(dXRequest)) {
                                dXResponse.setCode(1);
                                dXResponse.setMsg("sucess");
                            } else {
                                dXResponse.setCode(-1);
                                dXResponse.setMsg("business fail");
                            }
                        } catch (Exception typeE) {
                            dXResponse.setCode(-1);
                            dXResponse.setMsg("send fail");
                        }
                    } else {
                        if (exchangeSystem.getIsEnableCa() == null || exchangeSystem.getIsEnableCa()) {
                            dXResponse = clientWebService.dxSend(dXRequest);
                        } else {
                            Client client = getClientWebServiceWithoutCA(exchangeSystem,
                                    "webServiceUrl");
                            String dxRequestXml = turnDXRequestToXml(dXRequest);
                            Object[] result = client.invoke("dxSend", dxRequestXml);
                            String dxResponseXml = result[0].toString();

                            dXResponse = turnXmlToDxResponse(dxResponseXml);
                        }
                    }

                    // DXResponse dXResponse =
                    // clientWebService.dxSend(dXRequest);
                    // DXResponse dXResponse = new DXResponse();
                    // dXResponse.setCode(1);
                    // dXResponse.setMsg("success");

                    edr.setStatus("success");
                    exchangeDataRepestService.save(edr);
                    // 回调业务实现，重发成功
                    String repeatBatchId = dXExchangeBatch.getSourceBatchId();
                    if (StringUtils.isBlank(repeatBatchId)) {
                        repeatBatchId = dXExchangeBatch.getId();
                    }
                    businessHandleSourceMap.get(
                            exchangeDataType.getBusinessId().split(":")[0]).repestCall(
                            dXExchangeBatch.getSourceBatchId(), exchangeSystem.getUnitId(),
                            "success");
                    // 收件状态成功
                    try {
                        Map<String, Object> updateParams = Maps.newHashMap();
                        updateParams.put("time", new Date());
                        updateParams.put("status", "success");
                        updateParams.put("uuid", dxDataRoute.getUuid());
                        String updateHql = "update DXExchangeRouteDate o set o.receiveStatus=:status,o.receiveTime=:time where o.uuid = :uuid";
                        dXExchangeRouteDateService.updateByHQL(updateHql, updateParams);
                    } catch (Exception e) {
                        logger.error("更新收件对象异常");
                    }
                    // dxDataRoute.setReceiveStatus("success");
                    // dxDataRoute.setReceiveTime(new Date());
                    // dXExchangeRouteDateDao.save(dxDataRoute);

                    /*****************写入日志5****************************/
                    ExchangeDataLog log = new ExchangeDataLog();
                    log.setBatchId(dXExchangeBatch.getId());
                    log.setNode(30);
                    log.setCode(dXResponse.getCode());
                    log.setStatus(1);
                    log.setFromUnitId(dXExchangeBatch.getFromId());
                    log.setToUnitId(exchangeSystem.getUnitId());
                    log.setMsg("重发任务,调用新终端接收数据接口日志:成功" + ShutString(dXResponse.getMsg(), 50));
                    addExchangeDataLog(log);

                    Date endTimer2 = new Date();
                    long difference2 = (endTimer2.getTime() - starTime.getTime());
                    logger.error(
                            "-------------runExchangeDataRepeat dxSendClient end success:" + difference2
                                    + " uuid:" + webServiceMessage.getExchangeDataRepest().getUuid() + " status:"
                                    + edr.getStatus());
                } catch (Exception e) {
                    edr.setSendNum(edr.getSendNum() + 1);
                    if (edr.getSendNum() >= edr.getRetransmissionNum()) {
                    }
                    exchangeDataRepestService.save(edr);
                    /*****************写入日志17****************************/
                    ExchangeDataLog log = new ExchangeDataLog();
                    log.setBatchId(dXExchangeBatch.getId());
                    log.setNode(30);
                    log.setStatus(-1);
                    log.setFromUnitId(dXExchangeBatch.getFromId());
                    log.setToUnitId(exchangeSystem.getUnitId());
                    log.setMsg("重发任务,调用新终端接收数据接口日志:失败");
                    addExchangeDataLog(log);
                    Date endTimer2 = new Date();
                    long difference2 = (endTimer2.getTime() - starTime.getTime());
                    logger.error(
                            "-------------runExchangeDataRepeat receiveClient end error:" + difference2 + " uuid:"
                                    + webServiceMessage.getExchangeDataRepest().getUuid());
                    logger.error(e.getMessage(), e);
                }
            }
            Date endTimer2 = new Date();
            long difference2 = (endTimer2.getTime() - starTime.getTime());
            logger.error("-------------exchangeDataRepeatTask end success:" + difference2);
        } catch (Exception e) {
            Date endTimer2 = new Date();
            long difference2 = (endTimer2.getTime() - starTime.getTime());
            logger.error("-------------exchangeDataRepeatTask end error:" + difference2);
            logger.error(e.getMessage(), e);
        }

    }

    /**
     * 时间date向后推n秒
     */
    public Date getPreTime(Date date, Integer n) {
        try {
            long Time = (date.getTime() / 1000) + n;
            date.setTime(Time * 1000);
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
        return date;
    }

    /******************************数据交换*************************************/

    public void SwitchTenant(String currentTenantId, String currentUserId) {
        logger.debug(
                "[数据交换][开始切换租户]===>[currentUserId=" + currentUserId + "][currentTenantId=" + currentTenantId + "]");
        if (StringUtils.isNotBlank(currentUserId)) {// 进来是有登陆
            if (IgnoreLoginUtils.isIgnoreLogin()) {// 当前有虚拟登陆
                String userId = SpringSecurityUtils.getCurrentUserId();
                if (!userId.equals(currentUserId)) {// 如果当前虚拟登陆与原本不一致
                    IgnoreLoginUtils.logout();
                    try {
                        IgnoreLoginUtils.login(currentTenantId, currentUserId);
                        logger.debug(
                                "[数据交换][完成切换租户1]===>[currentUserId=" + currentUserId + "][currentTenantId="
                                        + currentTenantId + "]");
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                    }// 进行虚拟登陆
                }
            } else {// 当前无虚拟登陆
                try {
                    IgnoreLoginUtils.login(currentTenantId, currentUserId);
                    logger.debug(
                            "[数据交换][完成切换租户2]===>[currentUserId=" + currentUserId + "][currentTenantId="
                                    + currentTenantId + "]");
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    logger.error(e.getMessage());
                }// 进行虚拟登陆
            }

        } else {// 进来时无虚拟登陆

            logger.debug(
                    "[数据交换][进来时无虚拟登陆]===>[currentUserId=" + currentUserId + "][currentTenantId=" + currentTenantId
                            + "]");
            if (IgnoreLoginUtils.isIgnoreLogin()) {// 当前有虚拟登陆
                logger.debug(
                        "[数据交换][开始登出]===>[currentUserId=" + currentUserId + "][currentTenantId=" + currentTenantId
                                + "]");
                IgnoreLoginUtils.logout();// 退出虚拟登陆
                logger.debug(
                        "[数据交换][完成登出]===>[currentUserId=" + currentUserId + "][currentTenantId=" + currentTenantId
                                + "]");
            }

        }
    }

    /**
     * 平台接口实现
     * 接收数据
     * <p>
     * 说明：
     * 1、先对传过来的数据进行验证是否需要数据交换重复数据，若是重复不做保存，返回告知并写下日志
     * 2、存储数据
     * <p>
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.service.ExchangeDataFlowService#send(com.wellsoft.pt.integration.request.DXRequest, java.lang.Integer)
     */
    @Override
    @Transactional
    public DXResponse dxSend(DXRequest request, String source) {
        // this.dao.getSession().getTransaction().begin();
        String currentUserId = "";
        String currentTenantId = "";
        if (IgnoreLoginUtils.isIgnoreLogin()) {
            currentUserId = SpringSecurityUtils.getCurrentUserId();
            currentTenantId = SpringSecurityUtils.getCurrentTenantId();
        }

        logger.error("-------------dxSend--------------" + request.getBatchId());

        DXResponse dXResponse = new DXResponse();// 返回对象
        try {
            boolean isCommunal = false;
            if (isCommunal()) {// 是否公共的webservice
                IgnoreLoginUtils.login(Config.COMMON_TENANT, Config.COMMON_TENANT);
                // 写入日志到公共库
                ExchangeLog exchangeLog = new ExchangeLog();
                exchangeLog.setBatchId(request.getBatchId());
                exchangeLog.setMsg("访问公共webservice发送数据接口dxSend");
                exchangeLog.setNode(1);
                exchangeLog.setFromUnitId(request.getFrom());
                exchangeLogService.save(exchangeLog);
                IgnoreLoginUtils.logout();
                // 登陆到租户库
                String tenantId = getTanentId(request.getFrom());
                try {
                    IgnoreLoginUtils.login(tenantId, tenantId);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    logger.info(e.getMessage());
                }
                isCommunal = true;
            }

            /**************验证数据是否已经存在**************/
            DXExchangeBatch batch = dXExchangeBatchService.getDXExchangeBatchById(
                    request.getBatchId());
            if (batch != null) {
                // 写入日志
                ExchangeDataLog exchangeDataLog = new ExchangeDataLog();
                exchangeDataLog.setBatchId(request.getBatchId());
                exchangeDataLog.setNode(0);
                exchangeDataLog.setFromUnitId(request.getFrom());
                exchangeDataLog.setStatus(1);
                exchangeDataLog.setMsg("批次号" + request.getBatchId() + "已经存在");
                this.addExchangeDataLog(exchangeDataLog);
                // 返回
                dXResponse.setCode(-1);
                dXResponse.setMsg("批次号" + request.getBatchId() + "已经存在");
                SwitchTenant(currentTenantId, currentUserId);
                return dXResponse;
            } else {
                List<DXDataItem> dataItems = request.getDataList();
                for (DXDataItem di : dataItems) {
                    DXExchangeData ed = dXExchangeDataService.getDXExchangeDataByDataIdAll(
                            di.getDataId(),
                            di.getRecVer());
                    if (ed != null) {
                        // 写入日志
                        ExchangeDataLog exchangeDataLog = new ExchangeDataLog();
                        exchangeDataLog.setBatchId(request.getBatchId());
                        exchangeDataLog.setNode(0);
                        exchangeDataLog.setFromUnitId(request.getFrom());
                        exchangeDataLog.setStatus(1);
                        exchangeDataLog.setMsg(
                                "dataId为" + ed.getDataId() + " 版本号为" + ed.getDataRecVer() + "已经存在");
                        this.addExchangeDataLog(exchangeDataLog);
                        // 返回
                        dXResponse.setCode(-1);
                        dXResponse.setMsg(
                                "dataId为" + ed.getDataId() + " 版本号为" + ed.getRecVer() + "已经存在");
                        SwitchTenant(currentTenantId, currentUserId);
                        return dXResponse;
                    }
                }
            }
            /*************************保存数据*******************************/
            // 写入日志
            ExchangeDataLog dxExchangeLog = new ExchangeDataLog();
            dxExchangeLog.setBatchId(request.getBatchId());
            dxExchangeLog.setNode(1);
            dxExchangeLog.setFromUnitId(request.getFrom());
            dxExchangeLog.setStatus(1);
            dxExchangeLog.setMsg("通过用户密码验证，成功访问平台webservice接收数据接口");
            this.addExchangeDataLog(dxExchangeLog);// 写入日志
            // 保存批次
            DXExchangeBatch dXExchangeBatch = new DXExchangeBatch();
            dXExchangeBatch.setId(request.getBatchId());
            dXExchangeBatch.setFromId(request.getFrom());
            if (!StringUtils.isBlank(SpringSecurityUtils.getCurrentUserId())) {
                dXExchangeBatch.setFromUser(SpringSecurityUtils.getCurrentUserId());
            }
            dXExchangeBatch.setToId(request.getTo());
            dXExchangeBatch.setCc(request.getCc());
            dXExchangeBatch.setBcc(request.getBcc());
            dXExchangeBatch.setTypeId(request.getTypeId());
            dXExchangeBatch.setArrow(0);
            dXExchangeBatch.setParams(
                    request.getParams() == null ? "" : request.getParams().toString());
            this.dXExchangeBatchService.save(dXExchangeBatch);
            // 保存数据
            ExchangeDataType exchangeDataType = exchangeDataTypeService.getByTypeId(
                    request.getTypeId());// 数据类型
            List<DXDataItem> dataList = request.getDataList();// 保存交换数据
            List<DXExchangeData> dXExchangeDatas = new ArrayList<DXExchangeData>();
            for (DXDataItem dataItem : dataList) {
                DXExchangeData dXExchangeData = new DXExchangeData();
                dXExchangeData.setdXExchangeBatch(dXExchangeBatch);
                dXExchangeData.setDataId(dataItem.getDataId());
                dXExchangeData.setDataRecVer(dataItem.getRecVer());
                dXExchangeData.setText(
                        dXExchangeDataService.convertString2Clob(dataItem.getText()));
                dXExchangeData.setSourceDataUuid("");
                // 解析xml保存表单数据
                if (!StringUtils.isBlank(exchangeDataType.getFormId())) {
                } else {
                    List<StreamingData> streamingDatas = dataItem.getStreamingDatas();
                    List<String> fileIds = new ArrayList<String>();
                    String filesStr = "";
                    String batchFolderId = UUID.randomUUID().toString();
                    for (StreamingData streamingData : streamingDatas) {
                        MongoFileEntity mongoFileEntity;
                        try {
                            mongoFileEntity = mongoFileService.saveFile(streamingData.getFileName(),
                                    streamingData
                                            .getDataHandler().getInputStream());
                            String fileId = mongoFileEntity.getFileID();
                            String fileName = mongoFileEntity.getFileName();
                            fileIds.add(fileId);
                            filesStr += ";" + fileName + ":" + fileId;
                            // mongoFileService.pushFileToFolder(dXExchangeBatch.getId(),
                            // fileId, "");
                            mongoFileService.pushFileToFolder(batchFolderId, fileId, "");
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            logger.info(e.getMessage());
                        }
                    }
                    if (fileIds.size() > 0) {
                        dXExchangeData.setFormDataUuid(filesStr.replaceFirst(";", ""));
                    }
                }

                // CA签名
                String signDigest = "";
                try {
                    signDigest = MerlinCrypto.getInstace().digestAsBase64(
                            new ByteArrayInputStream(dataItem.getText().getBytes("UTF-8")));
                    dXExchangeData.setSignDigest(signDigest);
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    logger.info(e.getMessage());
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    logger.info(e.getMessage());
                }
                dXExchangeData.setValidData("yes");
                dXExchangeData.setParams(dataItem.getParams() == null ? ""
                        : JsonUtils.object2Json(dataItem.getParams()));
                this.dXExchangeDataService.save(dXExchangeData);
                dXExchangeDatas.add(dXExchangeData);
                /******************写入日志2***********************/
                ExchangeDataLog dXExchangeLog = new ExchangeDataLog();
                dXExchangeLog.setBatchId(request.getBatchId());
                dXExchangeLog.setDataId(dataItem.getDataId());
                dXExchangeLog.setDataRecVer(dataItem.getRecVer());
                dXExchangeLog.setStatus(1);
                dXExchangeLog.setNode(2);
                dXExchangeLog.setFromUnitId(request.getFrom());
                dXExchangeLog.setMsg(
                        "保存收到的数据,DataId:" + dataItem.getDataId() + ",RecVer:" + dataItem.getRecVer());
                this.addExchangeDataLog(dXExchangeLog);
            }

            /***************表单验证、模块自定义验证*************************/
            if (!StringUtils.isBlank(exchangeDataType.getFormId())) {
                String formVerifiStr = "";// 表单验证消息
                for (DXExchangeData dxdata : dXExchangeDatas) {
                    DyFormData dyFormData = dyFormApiFacade.getDyFormData(dxdata.getFormUuid(),
                            dxdata.getFormDataUuid());
                    Map<Integer, List<Map<String, String>>> verificationMsgMap = dyFormApiFacade.validateFormdates(
                            dxdata.getFormUuid(), dyFormData.getFormDatas(dxdata.getFormUuid()));
                    if (verificationMsgMap.size() > 0) {
                        List<Map<String, String>> verificationlist = verificationMsgMap.get(1);
                        String verificationMsg = "";
                        for (int i = 0; i < verificationlist.size(); i++) {
                            Map<String, String> verificationMap = new HashMap<String, String>();
                            for (String column : verificationMap.keySet()) {
                                verificationMsg += column + verificationMap.get(column) + ";";
                            }
                        }
                        formVerifiStr += "dataId为:" + dxdata.getDataId() + "的数据,不满足的条件为:" + verificationMsg + ";";
                        dxdata.setValidMsg(verificationMsg);
                        dxdata.setValidData("no");
                        this.dXExchangeDataService.save(dxdata);

                        ExchangeDataLog dXExchangeLog = new ExchangeDataLog();
                        dXExchangeLog.setBatchId(request.getBatchId());
                        dXExchangeLog.setDataId(dxdata.getDataId());
                        dXExchangeLog.setDataRecVer(dxdata.getRecVer());
                        dXExchangeLog.setStatus(1);
                        dXExchangeLog.setCode(-1);
                        dXExchangeLog.setNode(18);
                        dXExchangeLog.setFromUnitId(request.getFrom());
                        dXExchangeLog.setMsg(formVerifiStr);
                        this.addExchangeDataLog(dXExchangeLog);
                    }
                }
                if (!formVerifiStr.equals("")) {
                    dXResponse.setCode(-1);
                    dXResponse.setMsg(formVerifiStr);
                    SwitchTenant(currentTenantId, currentUserId);
                    return dXResponse;
                }
            }
            String moduleVerifiStr = "";// 模块验证消息
            DXExchangeSupport dXExchangeSupport = this
                    .batchIdToDXExchangeSupport(request.getBatchId(), dXExchangeDatas);
            Map<String, String> verificationMap = businessHandleSourceMap.get(
                    exchangeDataType.getBusinessId().split(":")[0]).verificationData(
                    dXExchangeSupport);
            for (DXExchangeData dxdata : dXExchangeDatas) {
                if (verificationMap != null && verificationMap.get(dxdata.getDataId()) != null) {
                    String string = verificationMap.get(dxdata.getDataId());
                    if (!string.equals("success")) {
                        moduleVerifiStr += "dataId为:" + dxdata.getDataId() + "的数据,不满足的条件为:" + string + ";";
                        dxdata.setValidMsg(string);
                        dxdata.setValidData("no");
                        this.dXExchangeDataService.save(dxdata);

                        ExchangeDataLog dXExchangeLog = new ExchangeDataLog();
                        dXExchangeLog.setBatchId(request.getBatchId());
                        dXExchangeLog.setDataId(dxdata.getDataId());
                        dXExchangeLog.setDataRecVer(dxdata.getRecVer());
                        dXExchangeLog.setStatus(1);
                        dXExchangeLog.setCode(-1);
                        dXExchangeLog.setNode(18);
                        dXExchangeLog.setFromUnitId(request.getFrom());
                        dXExchangeLog.setMsg(moduleVerifiStr);
                        this.addExchangeDataLog(dXExchangeLog);
                    }
                }
            }
            if (!moduleVerifiStr.equals("")) {
                dXResponse.setCode(-1);
                dXResponse.setMsg(moduleVerifiStr);
                SwitchTenant(currentTenantId, currentUserId);
                return dXResponse;
            }
            ExchangeDataLog dXExchangeLog = new ExchangeDataLog();
            dXExchangeLog.setBatchId(request.getBatchId());
            dXExchangeLog.setStatus(1);
            dXExchangeLog.setCode(1);
            dXExchangeLog.setNode(18);
            dXExchangeLog.setFromUnitId(request.getFrom());
            dXExchangeLog.setMsg("批次数据验证通过");
            this.addExchangeDataLog(dXExchangeLog);
            /***************异步处理 分发*******************/
            WebServiceMessage msg = new WebServiceMessage();
            msg.setWay("dxSendAsynchronous");
            msg.setBatchId(request.getBatchId());
            msg.setSource(source);
            msg.setFromId(request.getFrom());
            msg.setToId(request.getTo());
            String tenantId1 = "";
            if (isCommunal) {// 是否公共的webservice
                msg.setTenantId(getTanentId(request.getFrom()));
            } else {
                // 获得当前用户信息
                UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
                msg.setTenantId(userDetails.getTenantId());
                tenantId1 = userDetails.getTenantId();
                msg.setUserId(userDetails.getUserId());
            }
            if (exchangeDataType.getSynchronous()) {// 同步
                Map<String, String> params = dxSendAsynchronous(msg, source);
                if (SpringSecurityUtils.getCurrentUser() == null) {
                    IgnoreLoginUtils.login(tenantId1, tenantId1);
                }
                dXResponse.setParams(params);
                if (params.get("dealCode").equals("fail")) {
                    dXResponse.setCode(-1);
                    dXResponse.setMsg("业务处理失败");
                    SwitchTenant(currentTenantId, currentUserId);
                    // this.dao.getSession().getTransaction().commit();
                    return dXResponse;
                }
            } else {// 异步
                WebServiceMessageSender sender = ApplicationContextHolder.getBean(
                        WebServiceMessageSender.class);
                sender.send(msg);
            }
            /***********************返回调用模块自定返回业务方法**************************/
            boolean responseResult = businessHandleSourceMap.get(
                    exchangeDataType.getBusinessId().split(":")[0])
                    .exchangeDataResponse(dXExchangeSupport);
            ExchangeDataLog responseLog = new ExchangeDataLog();
            responseLog.setBatchId(dXExchangeSupport.getBatchId());
            responseLog.setStatus(1);
            responseLog.setNode(19);
            responseLog.setFromUnitId(dXExchangeSupport.getFromId());
            if (responseResult) {
                responseLog.setCode(1);
                responseLog.setMsg("调用自定义模块方法:exchangeDataResponse,返回成功!");
            } else {
                responseLog.setCode(-1);
                responseLog.setMsg("调用自定义模块方法:exchangeDataResponse,返回失败!");
            }

            if (SpringSecurityUtils.getCurrentUser() == null) {
                String tenantId = getTanentId(request.getFrom());
                IgnoreLoginUtils.login(tenantId, tenantId);
            }

            this.addExchangeDataLog(responseLog);
            dXResponse.setCode(1);
            dXResponse.setMsg("Success");
            logger.error("-------------dxSend end success--------------------");
            SwitchTenant(currentTenantId, currentUserId);
            return dXResponse;

        } catch (BusinessException b) {
            logger.error("-------------dxSend end error--------------");
            logger.error(b.getMessage());
            dXResponse.setCode(-1);
            SwitchTenant(currentTenantId, currentUserId);
            return dXResponse;
        } catch (Exception e) {
            logger.error("-------------dxSend end error--------------");
            logger.error(e.getMessage(), e);
            dXResponse.setCode(-1);
            SwitchTenant(currentTenantId, currentUserId);
            return dXResponse;
        } finally {

        }

    }

    /**
     * 数据交换发送数据接口异步处理业务
     *
     * @param webServiceMessage
     * @return
     */
    @Override
    @Transactional
    public Map<String, String> dxSendAsynchronous(WebServiceMessage webServiceMessage,
                                                  String sourceReq) {
        logger.debug("[数据交换][异步发送数据 ][批次号:" + webServiceMessage.getBatchId() + "][formUnitId="
                + webServiceMessage.fromId + "][toUnitId=" + webServiceMessage.getToId() + "][typeId="
                + webServiceMessage.typeId);
        Map<String, String> map = new HashMap<String, String>();
        DXExchangeBatch dXExchangeBatch = dXExchangeBatchService.getDXExchangeBatchById(
                webServiceMessage.getBatchId());
        ExchangeDataType exchangeDataType = exchangeDataTypeService.getByTypeId(
                dXExchangeBatch.getTypeId());// 数据类型
        Map<String, String> sendStatusMap = new HashMap<String, String>();
        List<DXExchangeData> dxExchangeDatas = dXExchangeDataService.getDXExchangeDatasByBatchId(
                webServiceMessage
                        .getBatchId());

        /***************************服务端处理开始********************************/
        // 指定的收件单位
        String unitIds = "";
        if (dXExchangeBatch.getToId() != null && !dXExchangeBatch.getToId().equals("")) {
            unitIds += dXExchangeBatch.getToId();
        }
        if (dXExchangeBatch.getCc() != null && !dXExchangeBatch.getCc().equals("")) {
            if (unitIds.equals("")) {
                unitIds += dXExchangeBatch.getCc();
            } else {
                unitIds += ";" + dXExchangeBatch.getCc();
            }
        }
        if (dXExchangeBatch.getBcc() != null && !dXExchangeBatch.getBcc().equals("")) {
            if (unitIds.equals("")) {
                unitIds += dXExchangeBatch.getBcc();
            } else {
                unitIds += ";" + dXExchangeBatch.getBcc();
            }
        }
        // 发送到各个行政许可单位
        List<ExchangeRoute> exchangeRoutes = exchangeRouteService.getExRouteListBySource(
                dXExchangeBatch.getTypeId());
        for (ExchangeRoute e : exchangeRoutes) {
            ExchangeDataType toExchangeDataType = new ExchangeDataType();
            boolean isTrans = false;

            if (exchangeDataType.getMerge() != null && exchangeDataType.getMerge()) {// 合并发送时

                ExchangeDataTransform edt = new ExchangeDataTransform();
                if (e.getTransformId() != null && !e.getTransformId().equals("")) {
                    edt = exchangeDataTransformService.getById(e.getTransformId());
                    toExchangeDataType = exchangeDataTypeService.getByTypeId(
                            edt.getDestinationId());
                } else {
                    toExchangeDataType = exchangeDataType;
                }

                // 解析（路由）目标单位
                String toUnit = "";
                if ("type1".equals(e.getToType())) {// 直接选择收件单位
                    toUnit += e.getToId();
                } else if ("type2".equals(e.getToType())) {
                }
                if ("type3".equals(e.getToType())) {// 直接取填入的接收单位
                    toUnit = unitIds;
                } else {
                    if (!unitIds.equals("")) {// 有指定单位时
                        String[] u1 = unitIds.split(";");
                        for (int i = 0; i < u1.length; i++) {
                            if (toUnit.indexOf(u1[i]) < 0) {
                                if (!toUnit.equals("")) {
                                    toUnit += ";" + u1[i];
                                } else {
                                    toUnit += u1[i];
                                }
                            }
                        }
                    }
                }
                // 解析收件单位后回填
                dXExchangeBatch.setToId(toUnit);
                dXExchangeBatchService.save(dXExchangeBatch);

                int flag = 0;
                // 存储批次数据
                DXExchangeBatch newDXExchangeBatch = new DXExchangeBatch();
                newDXExchangeBatch.setArrow(1);
                newDXExchangeBatch.setBcc(dXExchangeBatch.getBcc());
                newDXExchangeBatch.setCc(dXExchangeBatch.getCc());
                newDXExchangeBatch.setFromId(dXExchangeBatch.getFromId());
                newDXExchangeBatch.setFromUser(dXExchangeBatch.getFromUser());
                newDXExchangeBatch.setId("DXBATCH" + UUID.randomUUID());
                newDXExchangeBatch.setParams(dXExchangeBatch.getParams());
                newDXExchangeBatch.setToId(toUnit);
                newDXExchangeBatch.setTypeId(toExchangeDataType.getId());
                newDXExchangeBatch.setSourceBatchId(dXExchangeBatch.getId());
                dXExchangeBatchService.save(newDXExchangeBatch);
                // 构建发件对象发送数据
                DXRequest dXRequest = new DXRequest();
                dXRequest.setBatchId(newDXExchangeBatch.getId());
                dXRequest.setBcc(newDXExchangeBatch.getBcc());
                dXRequest.setCc(newDXExchangeBatch.getCc());
                dXRequest.setFrom(newDXExchangeBatch.getFromId());
                if (!StringUtils.isBlank(newDXExchangeBatch.getParams())) {
                    dXRequest.setParams(JsonUtils.toMap(newDXExchangeBatch.getParams()));
                }
                dXRequest.setTo(newDXExchangeBatch.getToId());
                dXRequest.setTypeId(newDXExchangeBatch.getTypeId());
                List<DXDataItem> dataList = new ArrayList<DXDataItem>();

                for (DXExchangeData ed : dxExchangeDatas) {
                    DyFormData dyFormData = null;

                    DXDataItem dXDataItem = new DXDataItem();
                    String toFormDataUuid = "";
                    String xmlStr = "";
                    String toXml = "";
                    // 判断是否有对应表单
                    if (!StringUtils.isBlank(exchangeDataType.getFormId())) {
                    } else {
                        flag = 2;
                    }
                    // 解析开始，不对应表单或对应表单时满足约束条件
                    if (flag != 0) {
                        // 数据转换
                        try {
                            xmlStr = IOUtils.toString(
                                    ed.getText().getCharacterStream()) == null ? "" : IOUtils
                                    .toString(ed.getText().getCharacterStream()).toString();
                        } catch (IOException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        } catch (SQLException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }

                        if (edt.getXsl() != null && !edt.getXsl().equals("")) {
                            String xslStr = edt.getXsl();
                            StringInputStream xmlStream = new StringInputStream(xmlStr);
                            StringInputStream xslStream = new StringInputStream(xslStr);
                            Source source = new StreamSource(xmlStream);
                            Source xsl = new StreamSource(xslStream);
                            StringWriter writer = new StringWriter();
                            Result result = new StreamResult(writer);
                            TransformerFactory factory = TransformerFactory.newInstance();
                            try {
                                Transformer transformer = factory.newTransformer(xsl);
                                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                                try {
                                    transformer.transform(source, result);
                                } catch (TransformerException e1) {
                                    // TODO Auto-generated catch block
                                    e1.printStackTrace();
                                }
                            } catch (TransformerConfigurationException e2) {
                                // TODO Auto-generated catch block
                                e2.printStackTrace();
                            }
                            toXml = writer.toString();
                            isTrans = true;
                        }

                        // 构建发送对象
                        if (StringUtils.isBlank(toXml)) {
                            toXml = xmlStr;
                            if (flag == 1) {// 类型对应表单且路由满足筛选条件
                                toFormDataUuid = ed.getFormDataUuid();
                            }
                        }
                        dXDataItem.setText(toXml);
                        dXDataItem.setRecVer(ed.getDataRecVer());
                        dXDataItem.setDataId(ed.getDataId());
                        if (!StringUtils.isBlank(ed.getParams())) {
                            JsonUtils.toMap(ed.getParams());
                            dXDataItem.setParams(JsonUtils.toMap(ed.getParams()));
                        }
                        List<StreamingData> streamingDatas = new ArrayList<StreamingData>();
                        if (flag == 1) {
                        } else {
                            String fieldMsg = ed.getFormDataUuid();
                            if (!StringUtils.isBlank(fieldMsg)) {
                                String[] fieldArray = fieldMsg.split(";");
                                for (int ii = 0; ii < fieldArray.length; ii++) {
                                    StreamingData sd = new StreamingData();
                                    sd.setFileName(fieldArray[ii].split(":")[0]);
                                    MongoFileEntity mongoFileEntity = mongoFileService.getFile(
                                            fieldArray[ii]
                                                    .split(":")[1]);
                                    sd.setDataHandler(new DataHandler(
                                            new InputStreamDataSource(mongoFileEntity
                                                    .getInputstream(), "octet-stream")));
                                    streamingDatas.add(sd);
                                }
                            }
                        }
                        dXDataItem.setStreamingDatas(streamingDatas);
                        dataList.add(dXDataItem);
                        // 退出发送租户
                        // IgnoreLoginUtils.logout();

                        // 存储交换数据
                        DXExchangeData newDXExchangeData = new DXExchangeData();
                        newDXExchangeData.setDataId(ed.getDataId());
                        newDXExchangeData.setDataRecVer(ed.getDataRecVer());
                        newDXExchangeData.setdXExchangeBatch(newDXExchangeBatch);
                        if (flag == 1) {// 类型对应表单且路由满足筛选条件
                            newDXExchangeData.setFormDataUuid(toFormDataUuid);
                        }
                        newDXExchangeData.setFormUuid(toExchangeDataType.getFormId());
                        try {
                            String signDigest = MerlinCrypto.getInstace().digestAsBase64(
                                    new ByteArrayInputStream(toXml.getBytes("UTF-8")));
                            newDXExchangeData.setSignDigest(signDigest);
                        } catch (UnsupportedEncodingException exception) {
                            // TODO Auto-generated catch block
                            exception.printStackTrace();
                        } catch (Exception exception) {
                            // TODO Auto-generated catch block
                            exception.printStackTrace();
                        }
                        if (isTrans) {
                            newDXExchangeData.setSourceDataUuid(ed.getFormDataUuid());
                        }
                        newDXExchangeData.setFormDataUuid(ed.getFormDataUuid());
                        newDXExchangeData.setText(dXExchangeDataService.convertString2Clob(toXml));
                        newDXExchangeData.setValidData("yes");
                        newDXExchangeData.setParams(ed.getParams());
                        dXExchangeDataService.save(newDXExchangeData);
                    }
                }

                // 分发到单位
                String[] toId = (toUnit).split(";");// 目标
                for (int i = 0; i < toId.length; i++) {
                    if (toId[i] != null && !toId[i].equals("")) {
                        // 登陆收件租户
                        String tenantId = getTanentId(toId[i]);
                        try {
                            // IgnoreLoginUtils.login(tenantId, tenantId);

                            DXExchangeRouteDate dXExchangeRouteDate = new DXExchangeRouteDate();
                            dXExchangeRouteDate.setdXExchangeBatch(newDXExchangeBatch);
                            dXExchangeRouteDate.setInterval(e.getInterval());
                            if (dXExchangeBatch.getCc() != null && dXExchangeBatch.getCc().indexOf(
                                    toId[i]) > -1) {
                                dXExchangeRouteDate.setType("cc");
                            } else if (dXExchangeBatch.getBcc() != null
                                    && dXExchangeBatch.getBcc().indexOf(toId[i]) > -1) {
                                dXExchangeRouteDate.setType("bcc");
                            } else {
                                dXExchangeRouteDate.setType("to");
                            }
                            dXExchangeRouteDate.setUnitId(toId[i]);
                            dXExchangeRouteDate.setRetransmissionNum(e.getRetransmissionNum());
                            dXExchangeRouteDate.setRouteMsg(e.getId());
                            dXExchangeRouteDate.setSendNum(0);
                            ExchangeSystem clientExchangeSystem = new ExchangeSystem();
                            // 接收系统与数据无关
                            // if
                            // (!webServiceMessage.getToId().equals(webServiceMessage.getFromId()))
                            // {
                            if (isTrans) {
                                clientExchangeSystem = exchangeSystemService.getExchangeSystemByUnitAndType1(
                                        toId[i],
                                        toExchangeDataType.getId());
                            } else {
                                clientExchangeSystem = exchangeSystemService.getExchangeSystemByUnitAndType1(
                                        toId[i],
                                        exchangeDataType.getId());
                            }
                            // }
                            if (clientExchangeSystem != null && !StringUtils.isBlank(
                                    clientExchangeSystem.getUuid())) {// 有系统
                                dXExchangeRouteDate.setSystemId(clientExchangeSystem.getId());
                                dXExchangeRouteDate.setReceiveStatus("default");
                                dXExchangeRouteDate.setReceiveTime(new Date());
                            } else {// 无系统
                                dXExchangeRouteDate.setReceiveStatus("success");
                                dXExchangeRouteDate.setReceiveTime(new Date());
                            }
                            dXExchangeRouteDateService.save(dXExchangeRouteDate);

                            // 写入保存数据日志
                            ExchangeDataLog exchangeDataLog = new ExchangeDataLog();
                            exchangeDataLog.setBatchId(newDXExchangeBatch.getId());
                            exchangeDataLog.setStatus(1);
                            exchangeDataLog.setNode(2);
                            exchangeDataLog.setFromUnitId(newDXExchangeBatch.getFromId());
                            exchangeDataLog.setMsg(
                                    "保存发送的数据,DataId:" + exchangeDataLog.getDataId() + ",RecVer:"
                                            + exchangeDataLog.getDataRecVer());
                            addExchangeDataLog(exchangeDataLog);

                            if (clientExchangeSystem != null && !StringUtils.isBlank(
                                    clientExchangeSystem.getUuid())) {// 有系统
                                // 对附件流进行加密
                                Map<String, Object> props = new HashMap<String, Object>();
                                try {
                                    props = MerlinCrypto.getInstace().getWSS4JAttachmentPropsForDN(
                                            clientExchangeSystem.getSubjectDN());
                                } catch (WSSecurityException e1) {
                                    // TODO Auto-generated catch block
                                    e1.printStackTrace();
                                }

                                if (isZHONGXIAN(
                                        clientExchangeSystem.getExchangeType())) {// 判断是否走畅享业务协同平台交换，不做附件加密
                                    dXRequest.setDataList(dataList);
                                } else {
                                    // 判断附件是否进行CA加密
                                    List<DXDataItem> dataList1 = new ArrayList<DXDataItem>();
                                    if (clientExchangeSystem.getIsEnableCa() == null
                                            || clientExchangeSystem.getIsEnableCa()) {
                                        for (DXDataItem dXDataItem1 : dataList) {
                                            List<StreamingData> streamingDatas = dXDataItem1.getStreamingDatas();
                                            List<StreamingData> streamingDatas2 = new ArrayList<StreamingData>();
                                            DXDataItem ddi = new DXDataItem();
                                            BeanUtils.copyProperties(dXDataItem1, ddi);
                                            for (StreamingData sd : streamingDatas) {
                                                WSS4JOutAttachment wss4jOutAttachment2;
                                                try {
                                                    wss4jOutAttachment2 = new WSS4JOutAttachment(sd,
                                                            props);
                                                    wss4jOutAttachment2.signAndEncrypt();
                                                    streamingDatas2
                                                            .add(wss4jOutAttachment2.getEncryptedStreamingData());
                                                } catch (Exception e1) {
                                                    // TODO Auto-generated catch
                                                    // block
                                                    e1.printStackTrace();
                                                }
                                            }
                                            ddi.setStreamingDatas(streamingDatas2);
                                            dataList1.add(ddi);
                                        }
                                        dXRequest.setDataList(dataList1);
                                    } else {
                                        dXRequest.setDataList(dataList);
                                    }
                                }

                                DataExchangeClientWebService clientWebService = null;
                                if (clientExchangeSystem.getIsEnableCa() == null
                                        || clientExchangeSystem.getIsEnableCa()) {
                                    clientWebService = getClientWebService(clientExchangeSystem,
                                            "receiveUrl");
                                }
                                try {
                                    // 调用WebService发送
                                    DXResponse dXResponse = new DXResponse();
                                    if (isZHONGXIAN(
                                            clientExchangeSystem.getExchangeType())) {// 判断是否走畅享业务协同平台交换
                                        try {
                                            if (zongXianExchangeService.submit(dXRequest)) {
                                                dXResponse.setCode(1);
                                                dXResponse.setMsg("sucess");
                                            } else {
                                                dXResponse.setCode(-1);
                                                dXResponse.setMsg("business fail");
                                            }
                                        } catch (Exception typeE) {
                                            dXResponse.setCode(-1);
                                            dXResponse.setMsg("send fail");
                                        }
                                    } else {
                                        if (clientExchangeSystem.getIsEnableCa() == null
                                                || clientExchangeSystem.getIsEnableCa()) {
                                            dXResponse = clientWebService.dxSend(dXRequest);
                                        } else {
                                            Client client = getClientWebServiceWithoutCA(
                                                    clientExchangeSystem,
                                                    "webServiceUrl");
                                            String dxRequestXml = turnDXRequestToXml(dXRequest);

                                            Object[] result = client.invoke("dxSend", dxRequestXml);
                                            String dxResponseXml = result[0].toString();
                                            // 对方所返回xml
                                            logger.error("对方所返回xml:[" + dxResponseXml + "]");

                                            dXResponse = turnXmlToDxResponse(dxResponseXml);
                                        }
                                    }

                                    sendStatusMap.put(clientExchangeSystem.getUnitId(), "success");
                                    String repeatBatchId = dXExchangeBatch.getSourceBatchId();
                                    if (StringUtils.isBlank(repeatBatchId)) {
                                        repeatBatchId = dXExchangeBatch.getId();
                                    }
                                    businessHandleSourceMap.get(
                                            exchangeDataType.getBusinessId().split(":")[0])
                                            .repestCall(dXExchangeBatch.getSourceBatchId(),
                                                    clientExchangeSystem.getUnitId(), "success");

                                    /*****************写入日志5****************************/
                                    ExchangeDataLog log = new ExchangeDataLog();
                                    log.setBatchId(newDXExchangeBatch.getId());
                                    log.setNode(5);
                                    log.setCode(dXResponse.getCode());
                                    log.setStatus(1);
                                    log.setFromUnitId(newDXExchangeBatch.getFromId());
                                    log.setToUnitId(clientExchangeSystem.getUnitId());
                                    log.setMsg("调用终端接收数据接口日志:成功，" + ShutString(dXResponse.getMsg(),
                                            50));
                                    addExchangeDataLog(log);
                                    // 将结果传递回来
                                    if (dXResponse.getParams() != null) {
                                        map.putAll(dXResponse.getParams());
                                    }
                                    // 收件状态发送成功
                                    dXExchangeRouteDate.setReceiveStatus("success");
                                    dXExchangeRouteDate.setReceiveTime(new Date());
                                    dXExchangeRouteDateService.save(dXExchangeRouteDate);
                                } catch (Exception exception) {
                                    // 收件状态发送失败

                                    sendStatusMap.put(clientExchangeSystem.getUnitId(), "fail");

                                    dXExchangeRouteDate.setReceiveStatus("fail");
                                    dXExchangeRouteDate.setReceiveTime(new Date());
                                    dXExchangeRouteDateService.save(dXExchangeRouteDate);

                                    if (e.getRetransmissionNum() != null) {
                                        /****************加入重发任务****************/
                                        ExchangeDataRepest exchangeDataRepest = new ExchangeDataRepest();
                                        exchangeDataRepest.setInterval(e.getInterval());
                                        exchangeDataRepest.setSystemUuid(
                                                clientExchangeSystem.getUuid());
                                        exchangeDataRepest.setRetransmissionNum(
                                                e.getRetransmissionNum());
                                        exchangeDataRepest.setSendNum(1);
                                        exchangeDataRepest.setStatus("ing");
                                        exchangeDataRepest.setSendMethod("dxSendClient");
                                        exchangeDataRepest.setExchangeDataMonitorUuid(
                                                dXExchangeRouteDate.getUuid());
                                        exchangeDataRepest.setUserId(webServiceMessage.getUserId());
                                        exchangeDataRepest.setTenantId(
                                                webServiceMessage.getTenantId());
                                        exchangeDataRepestService.save(exchangeDataRepest);

                                    }
                                    /*****************写入日志5****************************/
                                    ExchangeDataLog log = new ExchangeDataLog();
                                    log.setBatchId(newDXExchangeBatch.getId());
                                    log.setNode(5);
                                    log.setStatus(-1);
                                    log.setFromUnitId(newDXExchangeBatch.getFromId());
                                    log.setToUnitId(clientExchangeSystem.getUnitId());
                                    log.setMsg("调用终端接收数据接口日志:失败");
                                    addExchangeDataLog(log);
                                }
                            }
                        } catch (Exception e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        } finally {
                            // IgnoreLoginUtils.logout();
                        }
                    }
                }
            } else {
                int flag = 0;
                for (DXExchangeData ed : dxExchangeDatas) {
                    DyFormData dyFormData = null;

                    DXDataItem dXDataItem = new DXDataItem();
                    String toFormDataUuid = "";
                    String xmlStr = "";
                    String toXml = "";
                    // 判断是否有对应表单
                    if (!StringUtils.isBlank(exchangeDataType.getFormId())) {
                    } else {
                        flag = 2;
                    }
                    // 解析开始，不对应表单或对应表单时满足约束条件
                    if (flag != 0) {
                        // 数据转换
                        try {
                            xmlStr = IOUtils.toString(
                                    ed.getText().getCharacterStream()) == null ? "" : IOUtils
                                    .toString(ed.getText().getCharacterStream()).toString();
                        } catch (IOException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        } catch (SQLException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                        if (e.getTransformId() != null && !e.getTransformId().equals("")) {
                            ExchangeDataTransform edt = exchangeDataTransformService.getById(
                                    e.getTransformId());
                            if (edt.getXsl() != null && !edt.getXsl().equals("")) {
                                String xslStr = edt.getXsl();
                                toExchangeDataType = exchangeDataTypeService.getByTypeId(
                                        edt.getDestinationId());
                                StringInputStream xmlStream = new StringInputStream(xmlStr);
                                StringInputStream xslStream = new StringInputStream(xslStr);
                                Source source = new StreamSource(xmlStream);
                                Source xsl = new StreamSource(xslStream);
                                StringWriter writer = new StringWriter();
                                Result result = new StreamResult(writer);
                                TransformerFactory factory = TransformerFactory.newInstance();
                                try {
                                    Transformer transformer = factory.newTransformer(xsl);
                                    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                                    try {
                                        transformer.transform(source, result);
                                    } catch (TransformerException e1) {
                                        // TODO Auto-generated catch block
                                        e1.printStackTrace();
                                    }
                                } catch (TransformerConfigurationException e2) {
                                    // TODO Auto-generated catch block
                                    e2.printStackTrace();
                                }
                                toXml = writer.toString();
                                isTrans = true;
                            }
                        }
                        // 构建发送对象
                        if (StringUtils.isBlank(toXml)) {
                            toXml = xmlStr;
                            toExchangeDataType = exchangeDataType;
                            if (flag == 1) {// 类型对应表单且路由满足筛选条件
                                toFormDataUuid = ed.getFormDataUuid();
                            }
                        }
                        dXDataItem.setText(toXml);
                        dXDataItem.setRecVer(ed.getDataRecVer());
                        dXDataItem.setDataId(ed.getDataId());
                        if (!StringUtils.isBlank(ed.getParams())) {
                            JsonUtils.toMap(ed.getParams());
                            dXDataItem.setParams(JsonUtils.toMap(ed.getParams()));
                        }
                        List<StreamingData> streamingDatas = new ArrayList<StreamingData>();
                        if (flag == 1) {
                        } else {
                            String fieldMsg = ed.getFormDataUuid();
                            if (!StringUtils.isBlank(fieldMsg)) {
                                String[] fieldArray = fieldMsg.split(";");
                                for (int ii = 0; ii < fieldArray.length; ii++) {
                                    StreamingData sd = new StreamingData();
                                    sd.setFileName(fieldArray[ii].split(":")[0]);
                                    MongoFileEntity mongoFileEntity = mongoFileService.getFile(
                                            fieldArray[ii]
                                                    .split(":")[1]);
                                    sd.setDataHandler(new DataHandler(
                                            new InputStreamDataSource(mongoFileEntity
                                                    .getInputstream(), "octet-stream")));
                                    streamingDatas.add(sd);
                                }
                            }
                        }

                        // 解析（路由）目标单位
                        String toUnit = "";
                        if ("type1".equals(e.getToType())) {// 直接选择收件单位
                            toUnit += e.getToId();
                        } else if ("type2".equals(e.getToType()) && flag == 1) {
                        }
                        if ("type3".equals(e.getToType())) {// 直接取填入的接收单位
                            toUnit = unitIds;
                        } else {
                            if (!unitIds.equals("")) {// 有指定单位时
                                String[] u1 = unitIds.split(";");
                                for (int i = 0; i < u1.length; i++) {
                                    if (toUnit.indexOf(u1[i]) < 0) {
                                        if (!toUnit.equals("")) {
                                            toUnit += ";" + u1[i];
                                        } else {
                                            toUnit += u1[i];
                                        }
                                    }
                                }
                            }
                        }
                        // 解析收件单位后回填
                        dXExchangeBatch.setToId(toUnit);
                        dXExchangeBatchService.save(dXExchangeBatch);
                        // 退出发送租户
                        IgnoreLoginUtils.logout();

                        // 分发到单位
                        String[] toId = (toUnit).split(";");// 目标
                        for (int i = 0; i < toId.length; i++) {
                            if (toId[i] != null && !toId[i].equals("")) {
                                // 登陆收件租户
                                String tenantId = getTanentId(toId[i]);
                                try {
                                    logger.debug(
                                            "[数据交换][异步发送数据,分发到单位 ][批次号:" + webServiceMessage.getBatchId()
                                                    + "][formUnitId=" + webServiceMessage.fromId + "][toUnitId=" + toId[i]
                                                    + "][typeId=" + webServiceMessage.typeId + "]===>开始虚拟登录:tenantId="
                                                    + tenantId);
                                    IgnoreLoginUtils.login(tenantId, tenantId);
                                    logger.debug(
                                            "[数据交换][异步发送数据,分发到单位 ][批次号:" + webServiceMessage.getBatchId()
                                                    + "][formUnitId=" + webServiceMessage.fromId + "][toUnitId=" + toId[i]
                                                    + "][typeId=" + webServiceMessage.typeId + "]===>完成虚拟登录:tenantId="
                                                    + tenantId);
                                    // 存储发件数据
                                    DXExchangeBatch newDXExchangeBatch = new DXExchangeBatch();
                                    newDXExchangeBatch.setArrow(1);
                                    newDXExchangeBatch.setBcc(dXExchangeBatch.getBcc());
                                    newDXExchangeBatch.setCc(dXExchangeBatch.getCc());
                                    newDXExchangeBatch.setFromId(dXExchangeBatch.getFromId());
                                    newDXExchangeBatch.setFromUser(dXExchangeBatch.getFromUser());
                                    newDXExchangeBatch.setId("DXBATCH" + UUID.randomUUID());
                                    newDXExchangeBatch.setParams(dXExchangeBatch.getParams());
                                    newDXExchangeBatch.setToId(toUnit);
                                    newDXExchangeBatch.setTypeId(toExchangeDataType.getId());
                                    newDXExchangeBatch.setSourceBatchId(dXExchangeBatch.getId());
                                    dXExchangeBatchService.save(newDXExchangeBatch);

                                    DXExchangeData newDXExchangeData = new DXExchangeData();
                                    newDXExchangeData.setDataId(ed.getDataId());
                                    newDXExchangeData.setDataRecVer(ed.getDataRecVer());
                                    newDXExchangeData.setdXExchangeBatch(newDXExchangeBatch);
                                    if (flag == 1) {// 类型对应表单且路由满足筛选条件
                                        newDXExchangeData.setFormDataUuid(toFormDataUuid);
                                    }
                                    newDXExchangeData.setFormUuid(toExchangeDataType.getFormId());
                                    try {
                                        String signDigest = MerlinCrypto.getInstace().digestAsBase64(
                                                new ByteArrayInputStream(toXml.getBytes("UTF-8")));
                                        newDXExchangeData.setSignDigest(signDigest);
                                    } catch (UnsupportedEncodingException exception) {
                                        // TODO Auto-generated catch block
                                        exception.printStackTrace();
                                    } catch (Exception exception) {
                                        // TODO Auto-generated catch block
                                        exception.printStackTrace();
                                    }
                                    if (isTrans) {
                                        newDXExchangeData.setSourceDataUuid(ed.getFormDataUuid());
                                    }
                                    newDXExchangeData.setFormDataUuid(ed.getFormDataUuid());
                                    newDXExchangeData.setText(
                                            dXExchangeDataService.convertString2Clob(toXml));
                                    newDXExchangeData.setValidData("yes");
                                    newDXExchangeData.setParams(ed.getParams());
                                    dXExchangeDataService.save(newDXExchangeData);

                                    DXExchangeRouteDate dXExchangeRouteDate = new DXExchangeRouteDate();
                                    dXExchangeRouteDate.setdXExchangeBatch(newDXExchangeBatch);
                                    dXExchangeRouteDate.setInterval(e.getInterval());
                                    if (dXExchangeBatch.getCc() != null
                                            && dXExchangeBatch.getCc().indexOf(toId[i]) > -1) {
                                        dXExchangeRouteDate.setType("cc");
                                    } else if (dXExchangeBatch.getBcc() != null
                                            && dXExchangeBatch.getBcc().indexOf(toId[i]) > -1) {
                                        dXExchangeRouteDate.setType("bcc");
                                    } else {
                                        dXExchangeRouteDate.setType("to");
                                    }
                                    dXExchangeRouteDate.setUnitId(toId[i]);
                                    dXExchangeRouteDate.setRetransmissionNum(
                                            e.getRetransmissionNum());
                                    dXExchangeRouteDate.setRouteMsg(e.getId());
                                    dXExchangeRouteDate.setSendNum(0);
                                    ExchangeSystem clientExchangeSystem = new ExchangeSystem();
                                    // if
                                    // (!webServiceMessage.getToId().equals(webServiceMessage.getFromId()))
                                    // {
                                    if (StringUtils.isBlank(
                                            toExchangeDataType.getUnitSysSourceId())) {
                                        if (isTrans) {
                                            clientExchangeSystem = exchangeSystemService
                                                    .getExchangeSystemByUnitAndType1(toId[i],
                                                            toExchangeDataType.getId());
                                        } else {
                                            clientExchangeSystem = exchangeSystemService
                                                    .getExchangeSystemByUnitAndType1(toId[i],
                                                            exchangeDataType.getId());
                                        }
                                    } else {
                                        Map<String, Object> parm = new HashMap<String, Object>();
                                        parm.put("xml", xmlStr);
                                        clientExchangeSystem = unitSystemSourceMap.get(
                                                exchangeDataType.getUnitSysSourceId()).getUnitSystem(
                                                toId[i], parm);
                                    }
                                    // }
                                    if (clientExchangeSystem != null
                                            && !StringUtils.isBlank(
                                            clientExchangeSystem.getUuid())) {// 有系统
                                        dXExchangeRouteDate.setSystemId(
                                                clientExchangeSystem.getId());
                                        dXExchangeRouteDate.setReceiveStatus("default");
                                        dXExchangeRouteDate.setReceiveTime(new Date());
                                    } else {// 无系统
                                        dXExchangeRouteDate.setReceiveStatus("success");
                                        dXExchangeRouteDate.setReceiveTime(new Date());
                                    }
                                    dXExchangeRouteDateService.save(dXExchangeRouteDate);

                                    // 写入保存数据日志
                                    ExchangeDataLog exchangeDataLog = new ExchangeDataLog();
                                    exchangeDataLog.setBatchId(newDXExchangeBatch.getId());
                                    exchangeDataLog.setDataId(newDXExchangeData.getDataId());
                                    exchangeDataLog.setDataRecVer(
                                            newDXExchangeData.getDataRecVer());
                                    exchangeDataLog.setStatus(1);
                                    exchangeDataLog.setNode(2);
                                    exchangeDataLog.setFromUnitId(newDXExchangeBatch.getFromId());
                                    exchangeDataLog.setMsg(
                                            "保存发送的数据,DataId:" + exchangeDataLog.getDataId() + ",RecVer:"
                                                    + exchangeDataLog.getDataRecVer());
                                    addExchangeDataLog(exchangeDataLog);

                                    if (clientExchangeSystem != null
                                            && !StringUtils.isBlank(
                                            clientExchangeSystem.getUuid())) {// 有系统
                                        // 构建发件对象发送数据
                                        DXRequest dXRequest = new DXRequest();
                                        dXRequest.setBatchId(newDXExchangeBatch.getId());
                                        dXRequest.setBcc(newDXExchangeBatch.getBcc());
                                        dXRequest.setCc(newDXExchangeBatch.getCc());
                                        dXRequest.setFrom(newDXExchangeBatch.getFromId());
                                        if (!StringUtils.isBlank(newDXExchangeBatch.getParams())) {
                                            dXRequest.setParams(JsonUtils.toMap(
                                                    newDXExchangeBatch.getParams()));
                                        }
                                        dXRequest.setTo(newDXExchangeBatch.getToId());
                                        dXRequest.setTypeId(newDXExchangeBatch.getTypeId());

                                        if (isZHONGXIAN(
                                                clientExchangeSystem.getExchangeType())) {// 判断是否走畅享业务协同平台交换，不做附件加密
                                            dXDataItem.setStreamingDatas(streamingDatas);
                                            List<DXDataItem> dataList = new ArrayList<DXDataItem>();
                                            dataList.add(dXDataItem);
                                            dXRequest.setDataList(dataList);
                                        } else {
                                            // 判断是否进行CA加密
                                            List<DXDataItem> dataList = new ArrayList<DXDataItem>();
                                            if (clientExchangeSystem.getIsEnableCa() == null
                                                    || clientExchangeSystem.getIsEnableCa()) {
                                                List<StreamingData> streamingDatas2 = new ArrayList<StreamingData>();
                                                // 对附件流进行加密
                                                Map<String, Object> props = new HashMap<String, Object>();
                                                try {
                                                    props = MerlinCrypto.getInstace().getWSS4JAttachmentPropsForDN(
                                                            clientExchangeSystem.getSubjectDN());
                                                } catch (WSSecurityException e1) {
                                                    // TODO Auto-generated catch
                                                    // block
                                                    e1.printStackTrace();
                                                }
                                                for (StreamingData sd : streamingDatas) {
                                                    WSS4JOutAttachment wss4jOutAttachment2;
                                                    try {
                                                        wss4jOutAttachment2 = new WSS4JOutAttachment(
                                                                sd, props);
                                                        wss4jOutAttachment2.signAndEncrypt();
                                                        streamingDatas2.add(wss4jOutAttachment2
                                                                .getEncryptedStreamingData());
                                                    } catch (Exception e1) {
                                                        // TODO Auto-generated
                                                        // catch block
                                                        e1.printStackTrace();
                                                    }
                                                }
                                                dXDataItem.setStreamingDatas(streamingDatas2);

                                                dataList.add(dXDataItem);
                                                dXRequest.setDataList(dataList);
                                            } else {
                                                dXDataItem.setStreamingDatas(streamingDatas);
                                                dataList.add(dXDataItem);
                                                dXRequest.setDataList(dataList);
                                            }
                                        }

                                        DataExchangeClientWebService clientWebService = null;
                                        if (clientExchangeSystem.getIsEnableCa() == null
                                                || clientExchangeSystem.getIsEnableCa()) {
                                            clientWebService = getClientWebService(
                                                    clientExchangeSystem, "receiveUrl");
                                        }

                                        try {
                                            logger.error(
                                                    "调用终端[" + clientExchangeSystem.getName() + "]接收数据接口【开始】,批次号["
                                                            + dXRequest.getBatchId() + "]");
                                            // 调用WebService发送
                                            DXResponse dXResponse = new DXResponse();
                                            if (isZHONGXIAN(
                                                    clientExchangeSystem.getExchangeType())) {// 判断是否走畅享业务协同平台交换
                                                try {
                                                    if (zongXianExchangeService.submit(dXRequest)) {
                                                        dXResponse.setCode(1);
                                                        dXResponse.setMsg("sucess");
                                                    } else {
                                                        dXResponse.setCode(-1);
                                                        dXResponse.setMsg("business fail");
                                                    }
                                                } catch (Exception typeE) {
                                                    dXResponse.setCode(-1);
                                                    dXResponse.setMsg("send fail");
                                                }
                                            } else {
                                                if (clientExchangeSystem.getIsEnableCa() == null
                                                        || clientExchangeSystem.getIsEnableCa()) {
                                                    dXResponse = clientWebService.dxSend(dXRequest);
                                                } else {
                                                    Client client = getClientWebServiceWithoutCA(
                                                            clientExchangeSystem,
                                                            "webServiceUrl");
                                                    String dxRequestXml = turnDXRequestToXml(
                                                            dXRequest);
                                                    Object[] result = client.invoke("dxSend",
                                                            dxRequestXml);
                                                    String dxResponseXml = result[0].toString();

                                                    dXResponse = turnXmlToDxResponse(dxResponseXml);
                                                }
                                            }
                                            sendStatusMap.put(clientExchangeSystem.getUnitId(),
                                                    "success");
                                            String repeatBatchId = dXExchangeBatch.getSourceBatchId();
                                            if (StringUtils.isBlank(repeatBatchId)) {
                                                repeatBatchId = dXExchangeBatch.getId();
                                            }
                                            businessHandleSourceMap.get(
                                                    exchangeDataType.getBusinessId().split(":")[0])
                                                    .repestCall(dXExchangeBatch.getSourceBatchId(),
                                                            clientExchangeSystem.getUnitId(),
                                                            "success");

                                            /*****************写入日志5****************************/
                                            ExchangeDataLog log = new ExchangeDataLog();
                                            log.setBatchId(newDXExchangeBatch.getId());
                                            log.setDataId(newDXExchangeData.getDataId());
                                            log.setRecVer(newDXExchangeData.getDataRecVer());
                                            log.setNode(5);
                                            log.setCode(dXResponse.getCode());
                                            log.setStatus(1);
                                            log.setFromUnitId(newDXExchangeBatch.getFromId());
                                            log.setToUnitId(clientExchangeSystem.getUnitId());
                                            log.setMsg(
                                                    "调用终端[" + clientExchangeSystem.getName() + "]接收数据接口日志:成功，"
                                                            + ShutString(dXResponse.getMsg(), 50));
                                            logger.error(
                                                    "调用终端[" + clientExchangeSystem.getName() + "]接收数据接口【成功】,批次号["
                                                            + dXRequest.getBatchId() + "]");
                                            addExchangeDataLog(log);
                                            // 将结果传递回来
                                            if (dXResponse.getParams() != null) {
                                                map.putAll(dXResponse.getParams());
                                            }
                                            // 收件状态成功
                                            dXExchangeRouteDate.setReceiveStatus("success");
                                            dXExchangeRouteDate.setReceiveTime(new Date());
                                            dXExchangeRouteDateService.save(dXExchangeRouteDate);
                                        } catch (Exception exception) {
                                            map.put("dealCode", "fail");
                                            // 收件状态失败
                                            logger.error(
                                                    "调用终端[" + clientExchangeSystem.getName() + "]接收数据接口【失败】,批次号["
                                                            + dXRequest.getBatchId() + "]" + exception.getMessage(),
                                                    exception);
                                            sendStatusMap.put(clientExchangeSystem.getUnitId(),
                                                    "fail");

                                            dXExchangeRouteDate.setReceiveStatus("fail");
                                            dXExchangeRouteDate.setReceiveTime(new Date());
                                            dXExchangeRouteDateService.save(dXExchangeRouteDate);
                                            if (e.getRetransmissionNum() != null) {
                                                /****************加入重发任务****************/
                                                ExchangeDataRepest exchangeDataRepest = new ExchangeDataRepest();
                                                exchangeDataRepest.setInterval(e.getInterval());
                                                exchangeDataRepest.setSystemUuid(
                                                        clientExchangeSystem.getUuid());
                                                exchangeDataRepest.setRetransmissionNum(
                                                        e.getRetransmissionNum());
                                                exchangeDataRepest.setSendNum(1);
                                                exchangeDataRepest.setStatus("ing");
                                                exchangeDataRepest.setSendMethod("dxSendClient");
                                                exchangeDataRepest.setExchangeDataMonitorUuid(
                                                        dXExchangeRouteDate
                                                                .getUuid());
                                                exchangeDataRepest.setUserId(
                                                        webServiceMessage.getUserId());
                                                exchangeDataRepest.setTenantId(
                                                        webServiceMessage.getTenantId());
                                                exchangeDataRepestService.save(exchangeDataRepest);
                                            }
                                            /*****************写入日志5****************************/
                                            ExchangeDataLog log = new ExchangeDataLog();
                                            log.setBatchId(newDXExchangeBatch.getId());
                                            log.setDataId(newDXExchangeData.getDataId());
                                            log.setRecVer(newDXExchangeData.getDataRecVer());
                                            log.setNode(5);
                                            log.setStatus(-1);
                                            log.setFromUnitId(newDXExchangeBatch.getFromId());
                                            log.setToUnitId(clientExchangeSystem.getUnitId());
                                            log.setMsg("调用终端接收数据接口日志:失败");
                                            addExchangeDataLog(log);
                                        }
                                    }
                                } catch (Exception e1) {
                                    // TODO Auto-generated catch block
                                    e1.printStackTrace();
                                } finally {
                                    IgnoreLoginUtils.logout();
                                }
                            }
                        }
                        String fromTenantId = getTanentId(dXExchangeBatch.getFromId());
                        try {
                            IgnoreLoginUtils.login(fromTenantId, fromTenantId);
                        } catch (Exception e3) {
                            // TODO Auto-generated catch block
                            e3.printStackTrace();
                        }
                    }
                }
            }
        }
        /************************上传过程结束，调用模块自定义上传方法**************************/
        if (sourceReq.equals(ExchangeConfig.SOURCE_EXTERNAL)) {
            ExchangeSystem sourceSys = new ExchangeSystem();
            String tenantId = getTanentId(dXExchangeBatch.getFromId());
            try {
                IgnoreLoginUtils.logout();
                IgnoreLoginUtils.login(tenantId, tenantId);
                if (StringUtils.isBlank(exchangeDataType.getUnitSysSourceId())) {
                    sourceSys = exchangeSystemService.getExchangeSystemByUnitAndType(
                            dXExchangeBatch.getFromId(),
                            dXExchangeBatch.getTypeId());
                } else {
                    Map<String, Object> parm = new HashMap<String, Object>();
                    try {
                        parm.put("xml",
                                IOUtils.toString(dxExchangeDatas.get(
                                        0).getText().getCharacterStream()) == null ? ""
                                        : IOUtils.toString(
                                        dxExchangeDatas.get(0).getText().getCharacterStream())
                                        .toString());
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        logger.info(e.getMessage());
                    }
                    sourceSys = unitSystemSourceMap.get(
                            exchangeDataType.getUnitSysSourceId()).getUnitSystem(
                            dXExchangeBatch.getFromId(), parm);
                }

                DXExchangeSupport dXExchangeSupport = this.batchIdToDXExchangeSupport(
                        dXExchangeBatch.getId(),
                        dxExchangeDatas);
                dXExchangeSupport.setSendStatusMap(sendStatusMap);
                Map<String, String> requestResult = businessHandleSourceMap.get(
                        exchangeDataType.getBusinessId().split(":")[0]).exchangeDataRequest(
                        dXExchangeSupport);
                map = requestResult;
                ExchangeDataLog requestLog = new ExchangeDataLog();
                requestLog.setBatchId(dXExchangeSupport.getBatchId());
                requestLog.setStatus(1);
                requestLog.setNode(19);
                requestLog.setFromUnitId(dXExchangeSupport.getFromId());
                requestLog.setCode(1);
                requestLog.setMsg("调用自定义模块方法:exchangeDataRequest,返回成功!");
                this.addExchangeDataLog(requestLog);

                /************************回调源单位，成功接收数据*****************************************/
                if (sourceSys != null && !sourceSys.getIsCallBack()) {
                    DXCallbackRequest dXCallbackRequest = new DXCallbackRequest();
                    dXCallbackRequest.setBatchId(dXExchangeBatch.getId());
                    dXCallbackRequest.setCode(1);
                    dXCallbackRequest.setMsg("success");
                    dXCallbackRequest.setUnitId(dXExchangeBatch.getFromId());
                    // 判断是否进行CA加密
                    if (sourceSys.getIsEnableCa() == null || sourceSys.getIsEnableCa()) {
                        DataExchangeClientWebService sourceClientWebService = getClientWebService(
                                sourceSys,
                                "webServiceUrl");
                        DXResponse rp = sourceClientWebService.dxCallback(dXCallbackRequest);
                        ExchangeDataLog backLog = new ExchangeDataLog();
                        backLog.setNode(3);
                        backLog.setBatchId(dXExchangeBatch.getId());
                        backLog.setFromUnitId(dXExchangeBatch.getFromId());
                        backLog.setCode(rp.getCode());
                        backLog.setStatus(1);
                        backLog.setMsg("调问终端上传结果回调接口日志: " + rp.getMsg());
                        addExchangeDataLog(backLog);
                    } else {
                        Client client = getClientWebServiceWithoutCA(sourceSys, "webServiceUrl");
                        String dxCallbackRequestXml = turnDXCallbackRequestToXml(dXCallbackRequest);

                        Object[] result = client.invoke("dxCallback", dxCallbackRequestXml);
                        String dxResponseXml = result[0].toString();
                        DXResponse rp = turnXmlToDxResponse(dxResponseXml);
                        ExchangeDataLog backLog = new ExchangeDataLog();
                        backLog.setNode(3);
                        backLog.setBatchId(dXExchangeBatch.getId());
                        backLog.setFromUnitId(dXExchangeBatch.getFromId());
                        backLog.setCode(rp.getCode());
                        backLog.setStatus(1);
                        backLog.setMsg("调问终端上传结果回调接口日志: " + rp.getMsg());
                        addExchangeDataLog(backLog);
                    }
                }
            } catch (BusinessException b) {
                // TODO Auto-generated catch block
                /************************回调源单位，成功接收数据*****************************************/
                // if
                // (StringUtils.isBlank(exchangeDataType.getUnitSysSourceId()))
                // {
                // sourceSys =
                // exchangeSystemDao.getExchangeSystemByUnitAndType(dXExchangeBatch.getFromId(),
                // dXExchangeBatch.getTypeId());
                // } else {
                // Map<String, Object> parm = new HashMap<String, Object>();
                // try {
                // parm.put("xml",
                // IOUtils.toString(dxExchangeDatas.get(0).getText().getCharacterStream())
                // == null ? ""
                // :
                // IOUtils.toString(dxExchangeDatas.get(0).getText().getCharacterStream())
                // .toString());
                // } catch (Exception e) {
                // // TODO Auto-generated catch block
                // logger.info(e.getMessage());
                // }
                // sourceSys =
                // unitSystemSourceMap.get(exchangeDataType.getUnitSysSourceId()).getUnitSystem(
                // dXExchangeBatch.getFromId(), parm);
                // }

                if (sourceSys != null && !sourceSys.getIsCallBack()) {
                    DataExchangeClientWebService sourceClientWebService = getClientWebService(
                            sourceSys,
                            "webServiceUrl");
                    DXCallbackRequest dXCallbackRequest = new DXCallbackRequest();
                    dXCallbackRequest.setBatchId(dXExchangeBatch.getId());
                    dXCallbackRequest.setCode(-1);
                    dXCallbackRequest.setMsg("fail");
                    dXCallbackRequest.setUnitId(dXExchangeBatch.getFromId());
                    DXResponse rp = sourceClientWebService.dxCallback(dXCallbackRequest);
                    // ExchangeDataLog backLog = new ExchangeDataLog();
                    // backLog.setNode(3);
                    // backLog.setBatchId(dXExchangeBatch.getId());
                    // backLog.setFromUnitId(dXExchangeBatch.getFromId());
                    // backLog.setCode(rp.getCode());
                    // backLog.setStatus(1);
                    // backLog.setMsg("调问终端上传结果回调接口日志: " + rp.getMsg());
                    // addExchangeDataLog(backLog);
                }

                logger.error(b.getMessage(), b);
                map.put("dealCode", "fail");
                map.put("errorMsg", b.getMessage());
                return map;

            } catch (Exception e1) {
                // TODO Auto-generated catch block
                /************************回调源单位，成功接收数据*****************************************/
                // if
                // (StringUtils.isBlank(exchangeDataType.getUnitSysSourceId()))
                // {
                // sourceSys =
                // exchangeSystemDao.getExchangeSystemByUnitAndType(dXExchangeBatch.getFromId(),
                // dXExchangeBatch.getTypeId());
                // } else {
                // Map<String, Object> parm = new HashMap<String, Object>();
                // try {
                // parm.put("xml",
                // IOUtils.toString(dxExchangeDatas.get(0).getText().getCharacterStream())
                // == null ? ""
                // :
                // IOUtils.toString(dxExchangeDatas.get(0).getText().getCharacterStream())
                // .toString());
                // } catch (Exception e) {
                // // TODO Auto-generated catch block
                // logger.info(e.getMessage());
                // }
                // sourceSys =
                // unitSystemSourceMap.get(exchangeDataType.getUnitSysSourceId()).getUnitSystem(
                // dXExchangeBatch.getFromId(), parm);
                // }

                if (sourceSys != null && !sourceSys.getIsCallBack()) {
                    DataExchangeClientWebService sourceClientWebService = getClientWebService(
                            sourceSys,
                            "webServiceUrl");
                    DXCallbackRequest dXCallbackRequest = new DXCallbackRequest();
                    dXCallbackRequest.setBatchId(dXExchangeBatch.getId());
                    dXCallbackRequest.setCode(-1);
                    dXCallbackRequest.setMsg("fail");
                    dXCallbackRequest.setUnitId(dXExchangeBatch.getFromId());
                    DXResponse rp = sourceClientWebService.dxCallback(dXCallbackRequest);
                    // ExchangeDataLog backLog = new ExchangeDataLog();
                    // backLog.setNode(3);
                    // backLog.setBatchId(dXExchangeBatch.getId());
                    // backLog.setFromUnitId(dXExchangeBatch.getFromId());
                    // backLog.setCode(rp.getCode());
                    // backLog.setStatus(1);
                    // backLog.setMsg("调问终端上传结果回调接口日志: " + rp.getMsg());
                    // addExchangeDataLog(backLog);
                }

                logger.error(e1.getMessage(), e1);
                map.put("dealCode", "fail");
                return map;
            }
        } else {
            String tenantId = getTanentId(dXExchangeBatch.getFromId());
            try {
                IgnoreLoginUtils.logout();
                IgnoreLoginUtils.login(tenantId, tenantId);
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                logger.error(e1.getMessage(), e1);
            }
        }
        if (StringUtils.isBlank(map.get("dealCode"))) {
            map.put("dealCode", "success");
        }
        return map;
    }

    /**
     * 平台接口实现
     * 终端调用平台抄送结果回调接口
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.service.ExchangeDataFlowService#sendCallback(com.wellsoft.pt.integration.request.DXCallbackRequest)
     */
    @Override
    @Transactional
    public DXResponse dxCallback(DXCallbackRequest message) {

        String currentUserId = "";
        String currentTenantId = "";
        if (IgnoreLoginUtils.isIgnoreLogin()) {
            currentUserId = SpringSecurityUtils.getCurrentUserId();
            currentTenantId = SpringSecurityUtils.getCurrentTenantId();
        }

        if (isCommunal()) {// 是否公共的webservice
            // 写入日志到公共库
            try {
                IgnoreLoginUtils.login(Config.COMMON_TENANT, Config.COMMON_TENANT);
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            ExchangeLog exchangeLog = new ExchangeLog();
            exchangeLog.setBatchId(message.getBatchId());
            exchangeLog.setMsg("访问公共webservice上传回调数据接口dxCallback");
            exchangeLog.setNode(2);
            exchangeLog.setFromUnitId(message.getUnitId());
            exchangeLogService.save(exchangeLog);
            IgnoreLoginUtils.logout();
            // 登陆到租户库
            String tenantId = getTanentId(message.getUnitId());
            try {
                IgnoreLoginUtils.login(tenantId, tenantId);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                logger.info(e.getMessage());
            }
        }
        DXResponse dXResponse = new DXResponse();
        DXExchangeBatch dXExchangeBatch = dXExchangeBatchService.getDXExchangeBatchById(
                message.getBatchId());
        if (dXExchangeBatch == null) {
            dXResponse.setCode(-1);
            dXResponse.setMsg("批次号无效");
            SwitchTenant(currentTenantId, currentUserId);
            return dXResponse;
        }
        for (DXExchangeData dXExchangeData : dXExchangeBatch.getdXExchangeDatas()) {
            for (DXExchangeRouteDate dXExchangeRouteDate : dXExchangeBatch.getdXExchangeRouteDates()) {
                if (message.getCode() == 1) {
                    dXExchangeRouteDate.setReceiveStatus("success");
                } else if (message.getCode() == -1) {
                    dXExchangeRouteDate.setReceiveStatus("fail");
                }
                dXExchangeRouteDate.setReceiveTime(new Date());
                dXExchangeRouteDateService.save(dXExchangeRouteDate);

                ExchangeDataLog log = new ExchangeDataLog();
                log.setBatchId(dXExchangeBatch.getId());
                log.setDataId(dXExchangeData.getDataId());
                log.setRecVer(dXExchangeData.getDataRecVer());
                log.setNode(6);
                log.setFromUnitId(dXExchangeBatch.getFromId());
                log.setToUnitId(dXExchangeRouteDate.getUnitId());
                log.setCode(message.getCode());
                log.setStatus(1);
                log.setMsg("调用平台抄送结果回调接口日志:" + message.getMsg());
                addExchangeDataLog(log);
            }
        }
        /***********************返回调用模块自定返回业务方法**************************/
        DXExchangeSupport dXExchangeSupport = this.batchIdToDXExchangeSupport(message.getBatchId(),
                null);
        ExchangeDataType type = exchangeDataTypeService.getByTypeId(
                dXExchangeBatch.getTypeId());// 数据类型
        DXCallbackRequest dXCallbackRequest = new DXCallbackRequest();
        dXCallbackRequest.setBatchId(message.getBatchId());
        dXCallbackRequest.setCode(message.getCode());
        dXCallbackRequest.setMsg(message.getMsg());
        dXCallbackRequest.setParams(message.getParams());
        dXCallbackRequest.setUnitId(message.getUnitId());
        boolean callBackResult = businessHandleSourceMap.get(
                type.getBusinessId().split(":")[0]).exchangeDataCallBack(
                dXCallbackRequest);

        ExchangeDataLog requestLog = new ExchangeDataLog();
        requestLog.setBatchId(dXExchangeSupport.getBatchId());
        requestLog.setStatus(1);
        requestLog.setNode(19);
        requestLog.setFromUnitId(dXExchangeSupport.getFromId());
        if (callBackResult) {
            requestLog.setCode(1);
            requestLog.setMsg("调用自定义模块方法:exchangeDataCallBack,返回成功!");
        } else {
            requestLog.setCode(-1);
            requestLog.setMsg("调用自定义模块方法:exchangeDataCallBack,返回失败!");
        }
        this.addExchangeDataLog(requestLog);
        dXResponse.setCode(1);
        dXResponse.setMsg("success");
        SwitchTenant(currentTenantId, currentUserId);
        return dXResponse;
    }

    /**
     * 查询数据实现类，当toId没有值时，查询平台数据，有值时，查询对应系统数据
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.service.ExchangeDataFlowService#query(com.wellsoft.pt.integration.request.ShareRequest, java.lang.String)
     */
    @Override
    public ShareResponse query(ShareRequest shareRequest, String toId) {
        // TODO Auto-generated method stub
        String unitId = shareRequest.getUnitId();
        String typeId = shareRequest.getTypeId();
        if (isCommunal()) {// 是否公共的webservice
            // 写入日志到公共库
            try {
                IgnoreLoginUtils.login(Config.COMMON_TENANT, Config.COMMON_TENANT);
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            ExchangeLog exchangeLog = new ExchangeLog();
            exchangeLog.setMsg("访问公共webservice查询接口queryShareData:typeId=" + typeId);
            exchangeLog.setNode(3);
            exchangeLog.setFromUnitId(unitId);
            exchangeLogService.save(exchangeLog);
            IgnoreLoginUtils.logout();
            // 登陆到租户库
            String tenantId = getTanentId(unitId);
            try {
                IgnoreLoginUtils.login(tenantId, tenantId);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                logger.info(e.getMessage());
            }
        }
        ExchangeDataType type = exchangeDataTypeService.getByTypeId(typeId);// 数据类型
        if (!StringUtils.isBlank(toId)) {
            ExchangeSystem exchangeSystem = exchangeSystemService.getExchangeSystemByUnitAndType(
                    toId, typeId);
            DataExchangeClientWebService clientWebService = getClientWebService(exchangeSystem,
                    "webServiceUrl");
            try {
                ShareResponse shareResponse = new ShareResponse();
                if (exchangeSystem.getIsEnableCa() == null || exchangeSystem.getIsEnableCa()) {
                    shareResponse = clientWebService.query(shareRequest);
                } else {
                    Client client = getClientWebServiceWithoutCA(exchangeSystem, "webServiceUrl");
                    String shareRequestXml = turnShareRequestToXml(shareRequest);
                    Object[] result = client.invoke("query", shareRequestXml);
                    String shareResponseXml = result[0].toString();
                    shareResponse = turnXmlToShareResponse(shareResponseXml);
                }

                ExchangeDataLog requestLog = new ExchangeDataLog();
                requestLog.setStatus(1);
                requestLog.setNode(21);
                requestLog.setFromUnitId(unitId);
                requestLog.setCode(shareResponse.getCode());
                requestLog.setMsg("请求调用目标的查询接口成功：" + shareResponse.getMsg());
                this.addExchangeDataLog(requestLog);
                return shareResponse;
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                ExchangeDataLog requestLog = new ExchangeDataLog();
                requestLog.setStatus(-1);
                requestLog.setNode(21);
                requestLog.setFromUnitId(unitId);
                requestLog.setMsg("请求调用目标的查询接口失败");
                this.addExchangeDataLog(requestLog);
                ShareResponse shareResponse = new ShareResponse();
                shareResponse.setCode(-1);
                shareResponse.setMsg("请求失败");
                return shareResponse;
            }
        } else {
            ShareResponse shareResponse = businessHandleSourceMap.get(
                    type.getBusinessId().split(":")[0])
                    .queryShareData(shareRequest);
            ExchangeDataLog requestLog = new ExchangeDataLog();
            requestLog.setStatus(1);
            requestLog.setNode(19);
            requestLog.setFromUnitId(unitId);
            requestLog.setCode(1);
            requestLog.setMsg("调用自定义模块方法:queryShareData,返回成功!");
            this.addExchangeDataLog(requestLog);
            return shareResponse;
        }
    }

    public DXExchangeSupport batchIdToDXExchangeSupport(String batchId,
                                                        List<DXExchangeData> dataList) {
        DXExchangeBatch dXExchangeBatch = dXExchangeBatchService.getDXExchangeBatchById(batchId);
        // 模块验证对象
        DXExchangeSupport dXExchangeSupport = new DXExchangeSupport();
        dXExchangeSupport.setBatchId(dXExchangeBatch.getId());
        dXExchangeSupport.setBcc(dXExchangeBatch.getBcc());
        dXExchangeSupport.setCc(dXExchangeBatch.getCc());
        dXExchangeSupport.setFromId(dXExchangeBatch.getFromId());
        dXExchangeSupport.setFromUser(dXExchangeBatch.getFromUser());
        dXExchangeSupport.setToId(dXExchangeBatch.getToId());
        dXExchangeSupport.setTypeId(dXExchangeBatch.getTypeId());
        List<DXDataItemSupport> dXDataItemSupports = new ArrayList<DXDataItemSupport>();
        for (DXExchangeData dXExchangeData : dataList == null ? dXExchangeBatch.getdXExchangeDatas() : dataList) {
            DXDataItemSupport dXDataItemSupport = new DXDataItemSupport();
            dXDataItemSupport.setDataId(dXExchangeData.getDataId());
            dXDataItemSupport.setDataRecVer(dXExchangeData.getDataRecVer());
            dXDataItemSupport.setFormDataUuid(dXExchangeData.getFormDataUuid());
            dXDataItemSupport.setFormUuid(dXExchangeData.getFormUuid());
            String xmlStr = "";
            try {
                xmlStr = IOUtils.toString(dXExchangeData.getText().getCharacterStream());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                logger.info(e.getMessage());
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                logger.info(e.getMessage());
            }
            if (!StringUtils.isBlank(dXExchangeData.getFormDataUuid())
                    && StringUtils.isBlank(dXExchangeData.getFormUuid())) {
                logger.error("**********fileNames:" + dXExchangeData.getFormDataUuid());
                // 所有的附件
                String[] filesStrArr = dXExchangeData.getFormDataUuid().split(";");
                Map<String, String> fileMap = new HashMap<String, String>();
                for (int i1 = 0; i1 < filesStrArr.length; i1++) {
                    String[] fileObject = filesStrArr[i1].split(":");
                    fileMap.put(fileObject[0], fileObject[1]);
                }

                Map<String, Map<String, Object>> xmlData = xmlDataToMap(xmlStr);
                for (String key : xmlData.keySet()) {
                    Map<String, Object> map_ = xmlData.get(key);
                    if (map_.get("type").equals("attachment")) {
                        String value_ = map_.get("value") == null ? "" : map_.get(
                                "value").toString();
                        List<String> fileIds = new ArrayList<String>();
                        String[] fileNames = value_.split(";");
                        for (int ii = 0; ii < fileNames.length; ii++) {
                            if (!StringUtils.isBlank(fileNames[ii])) {
                                if (fileMap.get(fileNames[ii]) != null) {
                                    fileIds.add(fileMap.get(fileNames[ii]));
                                } else {
                                    logger.error(
                                            "**********fileName" + ii + " error:" + fileNames[ii]);
                                }
                            }
                        }
                        map_.put("fileIds", fileIds);
                        xmlData.put(key, map_);
                    } else if (map_.get("type").equals("isList")) {
                        List<Map<String, Object>> subList = (List<Map<String, Object>>) map_.get(
                                "value");
                        for (Map<String, Object> subMap : subList) {// 遍历从表列表
                            for (String subFieldName : subMap.keySet()) {// 遍历数据字段
                                Map<String, Object> subFieldItem = (Map<String, Object>) subMap.get(
                                        subFieldName);
                                if (subFieldItem.get("type").equals("attachment")) {
                                    String value_ = subFieldItem.get(
                                            "value") == null ? "" : subFieldItem.get("value")
                                            .toString();
                                    List<String> fileIds = new ArrayList<String>();
                                    String[] subFileNames = value_.split(";");
                                    for (int ii = 0; ii < subFileNames.length; ii++) {
                                        if (!StringUtils.isBlank(subFileNames[ii])) {
                                            if (fileMap.get(subFileNames[ii]) != null) {
                                                fileIds.add(fileMap.get(subFileNames[ii]));
                                            } else {
                                                logger.error(
                                                        "**********subFileName" + ii + " error:"
                                                                + subFileNames[ii]);
                                            }
                                        }
                                    }
                                    subFieldItem.put("fileIds", fileIds);
                                }
                            }
                        }
                    }
                }
                dXDataItemSupport.setXmlData(xmlData);
            } else {
                Map<String, Map<String, Object>> xmlData = xmlDataToMap(xmlStr);
                dXDataItemSupport.setXmlData(xmlData);
            }
            dXDataItemSupport.setText(xmlStr);
            if (!StringUtils.isBlank(dXExchangeData.getParams())) {
                dXDataItemSupport.setParams(JsonUtils.toMap(dXExchangeData.getParams()));
            }
            dXDataItemSupports.add(dXDataItemSupport);
        }
        dXExchangeSupport.setDataList(dXDataItemSupports);
        return dXExchangeSupport;
    }

    public String getTanentId(String unitId) {
        String tenantId = TenantContextHolder.getTenantId();
        String tenantId2 = Config.COMMON_TENANT;
        if (tenantId.equals(tenantId2) && !"".equals(unitId)) {// tenantId为公共库租户Id时，请求的为公共WebService
            tenantId = unitApiFacade.getTenantIdByCommonUnitId(unitId);
        }
        return tenantId;
    }

    public boolean isCommunal() {
        String tenantId = TenantContextHolder.getTenantId();
        String tenantId2 = Config.COMMON_TENANT;
        if (tenantId.equals(tenantId2)) // tenantId为公共库租户Id时，请求的为公共WebService
            return true;
        else
            return false;
    }

    @Override
    public Boolean historyDataRequest(String zch, String unitId) {
        // TODO Auto-generated method stub
        ExchangeSystem exchangeSystem = exchangeSystemService.getExchangeSystemByUnitAndType(
                "004140203",
                ExchangeConfig.TYPE_ID_SSXX_ZTDJ);// 分发时候获得系统的问题
        DataExchangeClientWebService clientWebService = getClientWebService(exchangeSystem,
                "webServiceUrl");
        // 发送上传结果回调信息
        try {
            Boolean result = clientWebService.historyDataRequest(zch, unitId);
            /****************添加日志7****************/
            ExchangeDataLog exchangeDataLog = new ExchangeDataLog();
            exchangeDataLog.setFromUnitId(unitId);
            exchangeDataLog.setNode(20);
            exchangeDataLog.setStatus(1);
            exchangeDataLog.setMsg("请求历史数据zch返回:" + result);
            addExchangeDataLog(exchangeDataLog);
            return result;
        } catch (Exception e) {
            /****************添加日志7****************/
            ExchangeDataLog exchangeDataLog = new ExchangeDataLog();
            exchangeDataLog.setFromUnitId(unitId);
            exchangeDataLog.setNode(21);
            exchangeDataLog.setStatus(0);
            exchangeDataLog.setMsg("请求历史数据zch请求失败");
            addExchangeDataLog(exchangeDataLog);
            return false;
        }
    }

    public Map<String, Map<String, Object>> xmlDataToMap(String xml) {
        // TODO Auto-generated method stub
        try {
            // TODO Auto-generated method stub
            Document document = DocumentHelper.parseText(xml);
            Map<String, Map<String, Object>> map_ = new HashMap<String, Map<String, Object>>();
            // 获得根节点
            Element root = document.getRootElement();
            // 从XML的根结点开始遍历
            for (Iterator i = root.elementIterator(); i.hasNext(); ) {
                Element elementOneLevel = (Element) i.next();
                String isAttachment = elementOneLevel.attributeValue("isAttachment");
                String isList = elementOneLevel.attributeValue("isList");
                if (isList == null || (isList != null && !isList.equals("1"))) {// 主表
                    String key_ = elementOneLevel.getName();
                    String value_ = elementOneLevel.getTextTrim();
                    Map<String, Object> data_ = new HashMap<String, Object>();
                    // 附件
                    if (isAttachment != null && (isAttachment.equals("1") || isAttachment.equals(
                            "2"))) {
                        data_.put("type", "attachment");
                    } else {
                        data_.put("type", "text");
                    }
                    data_.put("value", value_);
                    map_.put(key_, data_);
                } else {// 从表
                    Map<String, Object> data_ = new HashMap<String, Object>();
                    data_.put("type", "isList");
                    String subFormName = elementOneLevel.getName();// 当前节点从表名
                    // 该种表单的行数据遍历
                    List subLineNodes = elementOneLevel.elements("item");
                    List<Map<String, Object>> subList = new ArrayList<Map<String, Object>>();
                    for (Iterator it = subLineNodes.iterator(); it.hasNext(); ) {
                        Map<String, Object> subFieldMap = new HashMap<String, Object>();
                        // 一行数据的列遍历
                        List subFieldNodes = ((Element) it.next()).elements();
                        for (Iterator it2 = subFieldNodes.iterator(); it2.hasNext(); ) {
                            Map<String, Object> subFieldDate = new HashMap<String, Object>();
                            Element elm2 = (Element) it2.next();
                            String subKey = elm2.getName();
                            String subValue = elm2.getTextTrim();
                            String subIsAttachment = elm2.attributeValue("isAttachment");
                            // 附件
                            if (subIsAttachment != null && (subIsAttachment.equals(
                                    "1") || subIsAttachment.equals("2"))) {
                                subFieldDate.put("type", "attachment");
                            } else {
                                subFieldDate.put("type", "text");
                            }
                            subFieldDate.put("value", subValue);
                            subFieldMap.put(subKey, subFieldDate);
                        }
                        subList.add(subFieldMap);
                    }
                    data_.put("value", subList);
                    map_.put(subFormName, data_);
                }
            }
            return map_;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public ShareResponse queryByToId(ShareRequest shareRequest, String toId) {
        // TODO Auto-generated method stub
        return this.query(shareRequest, toId);
    }

    public String ShutString(String str, int n) {
        if (str != null && str.length() > n) {
            return str.substring(0, n - 1);
        } else {
            return str;
        }
    }

    public boolean isZHONGXIAN(String type) {
        if (StringUtils.isNotBlank(type) && type.equals("CHANGXIANG")) {// 判断是否走畅享业务协同平台交换
            return true;
        } else {
            return false;
        }
    }

    /**
     * 将xml格式转为DxResponse对象
     *
     * @return
     */
    public DXResponse turnXmlToDxResponse(String dxResponseXml) {
        // 实例化DXResponse
        DXResponse dxResponse = new DXResponse();
        try {
            Document document = DocumentHelper.parseText(dxResponseXml);
            // 获得根元素
            Element rootElement = document.getRootElement();
            // 获得根元素中包含的所有子元素
            List<Element> rootChildElements = rootElement.elements();
            for (int i = 0; i < rootChildElements.size(); i++) {
                Element childElement = rootChildElements.get(i);
                if ("CODE".equals(childElement.getName())) {
                    dxResponse.setCode(Integer.valueOf(childElement.getText()));
                } else if ("MSG".equals(childElement.getName())) {
                    dxResponse.setMsg(childElement.getText());
                }
            }
        } catch (Exception e) {
            logger.error("turnXmlToDxResponse异常：" + ExceptionUtils.getFullStackTrace(e));
            throw new RuntimeException(e.getMessage());
        }
        return dxResponse;
    }

    /**
     * 将xml格式转为DxResponse对象
     *
     * @return
     */
    public ShareResponse turnXmlToShareResponse(String shareResponseXml) {
        // 实例化DXResponse
        ShareResponse shareResponse = new ShareResponse();
        try {
            Document document = DocumentHelper.parseText(shareResponseXml);
            // 获得根元素
            Element rootElement = document.getRootElement();
            // 获得根元素中包含的所有子元素
            List<Element> rootChildElements = rootElement.elements();
            for (int i = 0; i < rootChildElements.size(); i++) {
                Element childElement = rootChildElements.get(i);
                if ("TOTALROW".equals(childElement.getName())) {
                    shareResponse.setCode(Integer.valueOf(childElement.getText()));
                } else if ("CODE".equals(childElement.getName())) {
                    shareResponse.setCode(Integer.valueOf(childElement.getText()));
                } else if ("MSG".equals(childElement.getName())) {
                    shareResponse.setMsg(childElement.getText());
                } else if ("PARAMS".equals(childElement.getName())) {
                    List<Element> paraElements = childElement.elements();
                    Map<String, String> params = new HashMap<String, String>();
                    for (int k = 0; k < paraElements.size(); k++) {
                        params.put(paraElements.get(k).getName(), paraElements.get(k).getText());
                    }
                    shareResponse.setParams(params);
                } else if ("RECORDS".equals(childElement.getName())) {
                    List<String> records = new ArrayList<String>();
                    List<Element> recordElements = childElement.elements();
                    for (int k = 0; k < recordElements.size(); k++) {
                        Element itemElement = recordElements.get(k);
                        if ("item".equals(itemElement.getName())) {
                            String val = itemElement.getTextTrim();
                            if (StringUtils.isNotBlank(val)) {
                                records.add(val);
                            }
                        }
                    }
                    shareResponse.setRecords(records);
                } else if ("STREAMINGDATAS".equals(childElement.getName())) {
                    List<StreamingData> streamingDatas = new ArrayList<StreamingData>();
                    List<Element> streamingDataElements = childElement.elements();
                    for (int k = 0; k < streamingDataElements.size(); k++) {
                        Element itemElement = streamingDataElements.get(k);
                        if ("item".equals(itemElement.getName())) {
                            List<Element> childItemElements = itemElement.elements();
                            for (int m = 0; m < childItemElements.size(); m++) {
                                StreamingData streamingData = new StreamingData();
                                Element childItemElement = childItemElements.get(m);
                                if ("FILENAME".equals(childItemElement.getName())) {
                                    streamingData.setFileName(childItemElement.getText());
                                } else if ("DATAHANDLER".equals(childItemElement.getName())) {
                                    InputStream is = FileUitl.decoderBase64ToInputStream(
                                            childItemElement.getText());
                                    DataHandler dataHandler = new DataHandler(
                                            new InputStreamDataSource(is,
                                                    "application/octet-stream"));
                                    streamingData.setDataHandler(dataHandler);
                                    streamingDatas.add(streamingData);
                                }
                            }
                        }
                    }
                    shareResponse.setStreamingDatas(streamingDatas);
                }
            }
        } catch (Exception e) {
            logger.error("turnXmlToDxResponse异常：" + ExceptionUtils.getFullStackTrace(e));
            throw new RuntimeException(e.getMessage());
        }
        return shareResponse;
    }

    /**
     * 将xml格式转为DXCallbackRequest对象
     *
     * @return
     */
    public DXCallbackRequest turnXmlToDXCallbackRequest(String dXCallbackRequestXml) {
        // 实例化DXResponse
        DXCallbackRequest dxCallbackRequest = new DXCallbackRequest();
        try {
            Document document = DocumentHelper.parseText(dXCallbackRequestXml);
            // 获得根元素
            Element rootElement = document.getRootElement();
            // 获得根元素中包含的所有子元素
            List<Element> rootChildElements = rootElement.elements();
            for (int i = 0; i < rootChildElements.size(); i++) {
                Element childElement = rootChildElements.get(i);
                if ("BATCHID".equals(childElement.getName())) {
                    dxCallbackRequest.setBatchId(childElement.getText());
                } else if ("CODE".equals(childElement.getName())) {
                    dxCallbackRequest.setCode(Integer.valueOf(childElement.getText()));
                } else if ("UNITID".equals(childElement.getName())) {
                    dxCallbackRequest.setUnitId(childElement.getText());
                } else if ("MSG".equals(childElement.getName())) {
                    dxCallbackRequest.setMsg(childElement.getText());
                }
            }
        } catch (Exception e) {
            logger.error("turnXmlToDXCallbackRequest异常：" + ExceptionUtils.getFullStackTrace(e));
            throw new RuntimeException(e.getMessage());
        }
        return dxCallbackRequest;
    }

    /**
     * 将DXCallbackRequest对象转为xml格式
     *
     * @return
     */
    public String turnDXCallbackRequestToXml(DXCallbackRequest dxCallbackRequest) {
        // 创建DXCallbackRequest对象字符串
        try {
            Document document = DocumentHelper.createDocument();
            // 创建根节点
            Element rootElement = document.addElement("item");
            // 添加BATCHID节点
            Element batchidElement = rootElement.addElement("BATCHID");
            batchidElement.setText(dxCallbackRequest.getBatchId());
            // 添加CODE节点
            Element codeElement = rootElement.addElement("CODE");
            codeElement.setText(dxCallbackRequest.getCode() + "");
            // 添加UNITID节点
            Element unitIdElement = rootElement.addElement("UNITID");
            unitIdElement.setText(dxCallbackRequest.getUnitId());
            // 添加MSG节点
            Element msgElement = rootElement.addElement("MSG");
            if (StringUtils.isBlank(dxCallbackRequest.getMsg())) {
                msgElement.setText("");
            } else {
                msgElement.setText(dxCallbackRequest.getMsg());
            }
            // 添加PARAMS节点
            Element paramsElement = rootElement.addElement("PARAMS");
            paramsElement.setText("");
            // 返回字符串表示
            String dxCallbackRequestXml = document.asXML();
            // 去除头部的版本号等
            dxCallbackRequestXml = dxCallbackRequestXml.substring(
                    dxCallbackRequestXml.indexOf(">") + 1,
                    dxCallbackRequestXml.length());
            return dxCallbackRequestXml.replace("\n", "");
        } catch (Exception e) {
            logger.error("turnDXCallbackRequestToXml → ：" + ExceptionUtils.getFullStackTrace(e));
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 将DXRequest对象转为xml格式
     *
     * @return
     */
    public String turnDXRequestToXml(DXRequest dxRequest) {
        if (dxRequest != null) {
            try {
                Document document = DocumentHelper.createDocument();
                // 创建根节点
                Element rootElement = document.addElement("item");

                // 添加FROM节点
                Element fromElement = rootElement.addElement("FROM");
                fromElement.setText(dxRequest.getFrom());
                // 添加TO节点
                Element toElement = rootElement.addElement("TO");
                toElement.setText(dxRequest.getTo());
                // 添加CC节点
                Element ccElement = rootElement.addElement("CC");
                if (StringUtils.isNotBlank(dxRequest.getCc())) {
                    ccElement.setText(dxRequest.getCc());
                } else {
                    ccElement.setText("");
                }
                // 添加BCC节点
                Element bccElement = rootElement.addElement("BCC");
                if (StringUtils.isNotBlank(dxRequest.getBcc())) {
                    bccElement.setText(dxRequest.getBcc());
                } else {
                    bccElement.setText("");
                }
                // 添加TYPEID节点
                Element typeidElement = rootElement.addElement("TYPEID");
                typeidElement.setText(dxRequest.getTypeId());
                // 添加BATCHID节点
                Element batchidElement = rootElement.addElement("BATCHID");
                batchidElement.setText(dxRequest.getBatchId());

                // 循环设置DATALIST元素
                List<DXDataItem> dataItems = dxRequest.getDataList();
                if (dataItems.size() > 0) {
                    Element dataListElement = rootElement.addElement("DATALIST");
                    for (int i = 0; i < dataItems.size(); i++) {
                        DXDataItem dataItem = dataItems.get(i);
                        // 创建Item元素
                        Element itemElement = dataListElement.addElement("item");
                        // 创建DATAID元素
                        Element dataIdElement = itemElement.addElement("DATAID");
                        dataIdElement.setText(dataItem.getDataId());
                        // 创建RECVER元素
                        Element recverElement = itemElement.addElement("RECVER");
                        recverElement.setText(dataItem.getRecVer() + "");
                        // 创建TEXT元素
                        Element textElement = itemElement.addElement("TEXT");
                        // 调整Text内容为正常的Xml格式
                        String dataItemtext = dataItem.getText();
                        dataItemtext = dataItemtext.replaceAll("&lt;", "<").replaceAll("&gt;",
                                ">").replace("\n", "");
                        dataItemtext = dataItemtext.substring(dataItemtext.indexOf(">") + 1,
                                dataItemtext.length());
                        textElement.setText(dataItemtext);
                        // 获得STREAMINGDATAS集合
                        List<StreamingData> streamDatas = dataItem.getStreamingDatas();
                        // 创建STREAMINGDATAS元素
                        Element streamingDataElement = itemElement.addElement("STREAMINGDATAS");
                        if (streamDatas != null && streamDatas.size() > 0) {
                            for (int j = 0; j < streamDatas.size(); j++) {
                                StreamingData data = streamDatas.get(j);
                                // 创建STREAMINGDATAS元素的item元素
                                Element sitemElement = streamingDataElement.addElement("item");
                                // 创建FILENAME元素
                                Element fileNameElement = sitemElement.addElement("FILENAME");
                                fileNameElement.setText(data.getFileName());
                                // 处理DataHandler转换成字符串
                                String dataHandlerStr = FileUitl.encodeBase64File(
                                        data.getDataHandler()
                                                .getInputStream());
                                Element dataHandlerElement = sitemElement.addElement("DATAHANDLER");
                                dataHandlerElement.setText(dataHandlerStr);
                            }
                        }
                        // 添加PARAMS节点
                        Element paramsElement = itemElement.addElement("PARAMS");
                        paramsElement.setText("");
                    }
                }
                // 添加PARAMS节点
                Element paramsElement = rootElement.addElement("PARAMS");
                paramsElement.setText("");

                // 返回字符串表示
                String dxRequestXml = document.asXML();
                dxRequestXml = dxRequestXml.replace("\n", "").replace("&lt;", "<").replace("&gt;",
                        ">");
                return dxRequestXml;
            } catch (Exception e) {
                logger.error("turnDXRequestToXml → ：" + ExceptionUtils.getFullStackTrace(e));
                throw new RuntimeException(e.getMessage());
            }
        }
        return "所传入的dxRequest为空！";
    }

    public String turnShareRequestToXml(ShareRequest shareRequest) {
        if (shareRequest != null) {
            try {
                Document document = DocumentHelper.createDocument();
                // 创建根节点
                Element rootElement = document.addElement("item");

                // 添加TYPEID节点
                Element typeElement = rootElement.addElement("TYPEID");
                typeElement.setText(shareRequest.getTypeId());
                // 添加UNITID节点
                Element unitidElement = rootElement.addElement("UNITID");
                unitidElement.setText(shareRequest.getUnitId());
                // 添加PAGESIZE节点
                Element pagesizeElement = rootElement.addElement("PAGESIZE");
                pagesizeElement.setText(shareRequest.getPageSize() + "");
                // 添加CURRENTPAGE节点
                Element currentpageElement = rootElement.addElement("CURRENTPAGE");
                currentpageElement.setText(shareRequest.getCurrentPage() + "");

                Map<String, String> paramsMap = shareRequest.getParams();
                if (paramsMap != null && !paramsMap.isEmpty()) {
                    Element paramsElement = rootElement.addElement("PARAMS");
                    for (String key : paramsMap.keySet()) {
                        String val = paramsMap.get(key);
                        Element paraElement = paramsElement.addElement(key);
                        paraElement.setText(val);
                    }
                }

                List<Condition> conditions = shareRequest.getConditions();
                if (conditions != null && conditions.size() > 0) {
                    Element conditionElement = rootElement.addElement("CONDITION");
                    for (Condition condition : conditions) {
                        Element itemElement = conditionElement.addElement("item");
                        Element keyElement = itemElement.addElement("KEY");
                        keyElement.setText(condition.getKey());
                        Element valElement = itemElement.addElement("VALUE");
                        keyElement.setText(condition.getValue());
                        Element operatorElement = itemElement.addElement("OPERATOR");
                        keyElement.setText(condition.getOperator());
                    }
                }

                // 返回字符串表示
                String shareRequestXml = document.asXML();
                shareRequestXml = shareRequestXml.replace("\n", "").replace("&lt;", "<").replace(
                        "&gt;", ">");
                return shareRequestXml;
            } catch (Exception e) {
                logger.error("turnShareRequestToXml → ：" + ExceptionUtils.getFullStackTrace(e));
                throw new RuntimeException(e.getMessage());
            }
        }
        return "所传入的shareRequest为空！";
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.service.ExchangeDataFlowService#trunDXResponseToXml(com.wellsoft.pt.integration.response.DXResponse)
     */
    @Override
    public String turnDXResponseToXml(DXResponse dxResponse) {
        // 创建DXResponse对象字符串
        try {
            Document document = DocumentHelper.createDocument();
            // 创建根节点
            Element rootElement = document.addElement("item");
            // 添加CODE节点
            Element codeElement = rootElement.addElement("CODE");
            codeElement.setText(dxResponse.getCode() + "");
            // 添加MSG节点
            Element msgElement = rootElement.addElement("MSG");
            if (StringUtils.isBlank(dxResponse.getMsg())) {
                msgElement.setText("");
            } else {
                msgElement.setText(dxResponse.getMsg());
            }
            // 添加PARAMS节点
            Element paramsElement = rootElement.addElement("PARAMS");
            // paramsElement.setText(map2dom(dxResponse.getParams()));//
            paramsElement.setText(JSONObject.fromObject(dxResponse.getParams()).toString());
            // 返回字符串表示
            String dxResoponseXml = document.asXML();
            // 去除头部的版本号等
            dxResoponseXml = dxResoponseXml.substring(dxResoponseXml.indexOf(">") + 1,
                    dxResoponseXml.length());
            return dxResoponseXml.replace("\n", "");
        } catch (Exception e) {
            logger.error("turnDxResponseToXml → ：" + ExceptionUtils.getFullStackTrace(e));
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * shareRequestXml  →  ShareRequest
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.service.ExchangeDataFlowService#trunDXResponseToXml(com.wellsoft.pt.integration.response.DXResponse)
     */
    @Override
    public ShareRequest turnXmlToShareRequest(String shareRequestXml) {
        // 实例化ShareRequest
        ShareRequest shareRequest = new ShareRequest();
        try {
            Document document = DocumentHelper.parseText(shareRequestXml);
            // 获得根元素
            Element rootElement = document.getRootElement();
            // 获得根元素中包含的所有子元素
            List<Element> rootChildElements = rootElement.elements();
            List<Condition> conditions = new ArrayList<Condition>();
            for (int i = 0; i < rootChildElements.size(); i++) {
                Element childElement = rootChildElements.get(i);
                if ("TYPEID".equals(childElement.getName())) {
                    shareRequest.setTypeId(childElement.getText());
                } else if ("UNITID".equals(childElement.getName())) {
                    shareRequest.setUnitId(childElement.getText());
                } else if ("PAGESIZE".equals(childElement.getName())) {
                    shareRequest.setPageSize(Integer.valueOf(childElement.getText()));
                } else if ("CURRENTPAGE".equals(childElement.getName())) {
                    shareRequest.setCurrentPage(Integer.valueOf(childElement.getText()));
                } else if ("CONDITION".equals(childElement.getName())) {
                    List<Element> conditionElements = childElement.elements();
                    for (int k = 0; k < conditionElements.size(); k++) {
                        Element itemElement = conditionElements.get(k);
                        if ("item".equals(itemElement.getName())) {
                            Condition condition = new Condition();
                            List<Element> childItemElements = itemElement.elements();
                            for (int m = 0; m < childItemElements.size(); m++) {
                                Element childItemElement = childItemElements.get(m);
                                if ("KEY".equals(childItemElement.getName())) {
                                    condition.setKey(childItemElement.getText());
                                } else if ("VALUE".equals(childItemElement.getName())) {
                                    condition.setValue(childItemElement.getText());
                                } else if ("OPERATOR".equals(childItemElement.getName())) {
                                    condition.setOperator(childItemElement.getText());
                                }
                            }
                            conditions.add(condition);
                        }
                    }
                    shareRequest.setConditions(conditions);
                } else if ("PARAMS".equals(childElement.getName())) {
                    List<Element> paramsElements = childElement.elements();
                    Map<String, String> paramsMap = new HashMap<String, String>();
                    for (int k = 0; k < paramsElements.size(); k++) {
                        Element paramsElement = paramsElements.get(k);
                        String key = paramsElement.getName();
                        String value = paramsElement.getText();
                        paramsMap.put(key, value);
                        shareRequest.setParams(paramsMap);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("turnXmlToShareRequest异常：" + ExceptionUtils.getFullStackTrace(e));
            throw new RuntimeException(e.getMessage());
        }
        return shareRequest;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.service.ExchangeDataFlowService#turnShareResponseToXml(com.wellsoft.pt.integration.response.ShareResponse)
     */
    @Override
    public String turnShareResponseToXml(ShareResponse shareResponse) {
        // 创建ShareResponse对象字符串
        try {
            Document document = DocumentHelper.createDocument();
            // 创建根节点
            Element rootElement = document.addElement("item");
            // 添加CODE节点
            Element codeElement = rootElement.addElement("CODE");
            codeElement.setText(shareResponse.getCode() + "");

            // 循环设置records元素
            List<String> records = shareResponse.getRecords();
            if (records.size() > 0) {
                Element recordsElement = rootElement.addElement("RECORDS");
                for (int i = 0; i < records.size(); i++) {
                    String record = records.get(i);
                    // 获得根元素
                    Element data = (Element) DocumentHelper.parseText(record).getRootElement();
                    record = data.asXML();
                    record = record.substring(6, record.length() - 7);
                    // 创建Item元素
                    Element itemElement = recordsElement.addElement("item");
                    itemElement.setText(record);
                }
            }

            // 添加TOTALROW节点
            Element totalrowElement = rootElement.addElement("TOTALROW");
            totalrowElement.setText(shareResponse.getTotalRow() + "");

            // 获得STREAMINGDATAS集合
            List<StreamingData> streamDatas = shareResponse.getStreamingDatas();
            // 创建STREAMINGDATAS元素
            Element streamingDataElement = rootElement.addElement("STREAMINGDATAS");
            if (streamDatas != null && streamDatas.size() > 0) {
                for (int j = 0; j < streamDatas.size(); j++) {
                    StreamingData data = streamDatas.get(j);
                    // 创建STREAMINGDATAS元素的item元素
                    Element sitemElement = streamingDataElement.addElement("item");
                    // 创建FILENAME元素
                    Element fileNameElement = sitemElement.addElement("FILENAME");
                    fileNameElement.setText(data.getFileName());
                    // 处理DataHandler转换成字符串
                    String dataHandlerStr = FileUitl.encodeBase64File(
                            data.getDataHandler().getInputStream());
                    Element dataHandlerElement = sitemElement.addElement("DATAHANDLER");
                    dataHandlerElement.setText(dataHandlerStr);
                }
            }

            // 添加MSG节点
            Element msgElement = rootElement.addElement("MSG");
            if (StringUtils.isBlank(shareResponse.getMsg())) {
                msgElement.setText("");
            } else {
                msgElement.setText(shareResponse.getMsg());
            }
            // 添加PARAMS节点
            Element paramsElement = rootElement.addElement("PARAMS");
            paramsElement.setText("");
            // 返回字符串表示
            String shareResoponseXml = document.asXML();
            // 去除头部的版本号等
            // shareResoponseXml =
            // shareResoponseXml.substring(shareResoponseXml.indexOf(">") + 1,
            // shareResoponseXml.length());
            return shareResoponseXml.replace("\n", "").replace("&lt;", "<").replace("&gt;", ">");
        } catch (Exception e) {
            logger.error("turnShareResponseToXml → ：" + ExceptionUtils.getFullStackTrace(e));
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 辅助方法
     * 获得客户端webservice对象(无CA)
     *
     * @param exchangeSystem
     * @return
     */
    public Client getClientWebServiceWithoutCA(ExchangeSystem exchangeSystem, String method) {
        try {
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

            JaxWsDynamicClientFactory clientFactory = JaxWsDynamicClientFactory.newInstance();
            Client client = clientFactory.createClient(address);
            return client;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

}
