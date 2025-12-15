package com.wellsoft.pt.message.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.jasson.im.api.APIClient;
import com.jasson.im.api.MOItem;
import com.jasson.im.api.RPTItem;
import com.wellsoft.context.config.service.SystemParamsFacadeService;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.date.DateUtil;
import com.wellsoft.context.util.date.DateUtils;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.message.dao.ShortMessageDao;
import com.wellsoft.pt.message.entity.MasConfig;
import com.wellsoft.pt.message.entity.ShortMessage;
import com.wellsoft.pt.message.facade.SmsClientApiFacade;
import com.wellsoft.pt.message.processor.MessageProcessor;
import com.wellsoft.pt.message.processor.impl.SmsMessageProcessor;
import com.wellsoft.pt.message.service.MasConfigService;
import com.wellsoft.pt.message.service.ShortMessageService;
import com.wellsoft.pt.message.sms.*;
import com.wellsoft.pt.message.support.Message;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Description: 如何描述该类
 *
 * @author wbx
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-10-16.1	wbx		2013-10-16		Create
 * </pre>
 * @date 2013-10-16
 */
@Service
public class ShortMessageServiceImpl extends AbstractJpaServiceImpl<ShortMessage, ShortMessageDao, String> implements
        ShortMessageService {

    @Autowired
    private MasConfigService masConfigService;

    @Autowired
    private SmsClientApiFacade smsClientApiFacade;

    /**
     * 更新发送短信   历史记录
     */
    @Override
    @Transactional
    public void saveShortMessage(List<ShortMessage> shortsms) {
        // dao.saveBatch(shortsms);
        dao.saveAll(shortsms);
    }

    /**
     * 获取smid最大值
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.message.service.ShortMessageService#getMaxSmid()
     */
    @Override
    public Long getMaxSmid() {
        return dao.getMaxSmid();
    }

    /**
     * 获取随机两天内唯一短信ID
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.message.service.ShortMessageService#getOnlySmid()
     */
    @Override
    public String getOnlySmid() {
        int smid = 0;
        while (true) {
            smid = (int) (Math.random() * 980 + 1);
            List<ShortMessage> smlist = dao.findBySmid(smid);
            if (smlist != null && smlist.size() == 1) {
                continue;
            } else
                break;
        }
        String str = smid + "";
        if (str.length() == 1) {
            str = "00" + str;
        } else if (str.length() == 2) {
            str = "0" + str;
        }
        return str;
    }

    @Override
    public List<ShortMessage> findBySmid(String smid) {
        return dao.findBySmid(Integer.parseInt(smid));
    }

    /**
     * 定时任务（据说只适用于mas机。。。春喜说的。。。）
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.message.service.ShortMessageService#doMasJob()
     */
    @Override
    @Transactional
    public void doMasJob() {
        APIClient apiClient = new APIClient();
        MasConfig config = this.getBean();
        if (config != null && StringUtils.isNotBlank(config.getUuid())) {
            String dbIP = config.getImIp();
            String dbUser = config.getLoginName();
            String dbPwd = config.getLoginPassword();
            String apiCode = config.getApiCode();
            String dbName = config.getDbName();
            if (!config.getIsOpen()) {
                //
                SystemParamsFacadeService systemParamsFacadeService = ApplicationContextHolder.getBean(SystemParamsFacadeService.class);
                String name = systemParamsFacadeService.getValue("sms.client.api.name");
                if (StringUtils.isNotBlank(name)) {
                    SmsClientApi smsClientApi = ApplicationContextHolder.getBean(name, SmsClientApi.class);
                    if (null == smsClientApi) {
                        return;
                    }
                }
            }
            // 1.建立到移动代理服务器的连接
            if (apiClient.init(dbIP, dbUser, dbPwd, apiCode, dbName) == APIClient.IMAPI_SUCC) {
                // 2.取出之前需要重发的短信，重发
                List<ShortMessage> msList;
                if (config.getSendLimit() != null) {
                    msList = dao.getListByIsReSendAndType1(true, smsClientApiFacade.getType(), config.getSendLimit());
                } else {
                    msList = dao.getListByIsReSendAndType(true, smsClientApiFacade.getType());
                }
                if (msList != null) {
                    for (ShortMessage sm : msList) {
                        int sendResult = apiClient.sendSM(sm.getRecipientMobilePhone(), sm.getBody(), sm.getSmid());
                        if (sendResult == APIClient.IMAPI_SUCC) {
                            sm.setIsReSend(false);
                            sm.setSendStatus(3); // 重发成功
                            dao.save(sm);
                        }
                    }
                }

                List<ShortMessage> shortsms = new ArrayList<ShortMessage>();
                // 3.接收回执信息
                RPTItem[] rtps = apiClient.receiveRPT();
                for (RPTItem item : rtps) {
                    ShortMessage sm = dao.getObjBySmidAndRecPhone(item.getSmID(), item.getMobile(), 1, 0); // 确认
                    // mobile是源发送方还是接收方的
                    if (sm != null) {
                        sm.setIsReSend(false);
                        if (item.getCode() == 0) {
                            sm.setSendStatus(1); // 对方成功收到
                        } else if (item.getCode() == 8 || item.getCode() == 101) {
                            sm.setIsReSend(true); // 需要重发
                            sm.setSendStatus(2);
                        } else {
                            sm.setSendStatus(2);
                        }
                        sm.setRecId(item.getCode());
                        sm.setRecMsg(item.getDesc());
                        try {
                            sm.setReceivedTime(DateUtils.parseDateTime(item.getRptTime()));
                        } catch (ParseException e) {
                            logger.error(ExceptionUtils.getStackTrace(e));
                        }
                        shortsms.add(sm);
                    }
                }
                if (shortsms.size() > 0) {
                    dao.saveAll(shortsms);
                }

                shortsms = new ArrayList<ShortMessage>();
                // 4.接收MO回复短信
                MOItem[] mos = apiClient.receiveSM();
                for (MOItem mo : mos) {
                    ShortMessage sourceSM = dao.getObjBySmidAndRecPhone(mo.getSmID(), mo.getMobile(), 1, 1);
                    if (sourceSM != null) {
                        ShortMessage sm = new ShortMessage();
                        sm.setSendStatus(1);
                        sm.setBody(mo.getContent());
                        sm.setSend(sourceSM.getReceived());
                        sm.setSendMobilePhone(mo.getMobile());

                        try {
                            sm.setSendTime(DateUtils.parseDateTime(mo.getMoTime()));
                        } catch (ParseException e) {
                            logger.error(ExceptionUtils.getStackTrace(e));
                        }

                        sm.setSenderName(sourceSM.getRecipientName());
                        sm.setReceived(sourceSM.getSend());
                        sm.setRecipientMobilePhone(sourceSM.getSendMobilePhone());
                        sm.setRecipientName(sourceSM.getSenderName());
                        sm.setSmid(mo.getSmID());
                        sm.setType(smsClientApiFacade.getType()); // mas机
                        sm.setIsReSend(false);

                        // 如果源发送方是用户使用平台发送，需要发送短信通知其有回复
                        if (!sourceSM.getSenderName().equals("system")) {
                            if (StringUtils.isNotBlank(sm.getRecipientMobilePhone())) {
                                int result = apiClient.sendSM(sm.getRecipientMobilePhone(), sm.getBody(), sm.getSmid());
                                if (result != APIClient.IMAPI_SUCC) {
                                    sm.setIsReSend(true);
                                    sm.setSendStatus(0);
                                }
                            }
                        }
                        shortsms.add(sm);
                    }
                }
                if (shortsms.size() > 0) {
                    dao.saveAll(shortsms);
                }
            }
        }
        // 5.断开到移动代理服务器的连接，同时释放相关资源
        apiClient.release();
    }

    /**
     * 定时任务
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.message.service.ShortMessageService#doMasJob()
     */
    @Override
    @Transactional
    public void doMasJob2() {
        MasConfig config = this.getBean();
        if (config != null && StringUtils.isNotBlank(config.getUuid())) {
            // 1.建立到移动代理服务器的连接
            // 2.取出之前需要重发的短信，重发
            List<ShortMessage> msList;
            if (config.getSendLimit() != null) {
                msList = dao.getListByIsReSendAndType1(true, smsClientApiFacade.getType(), config.getSendLimit());
            } else {
                msList = dao.getListByIsReSendAndType(true, smsClientApiFacade.getType());
            }
            if (msList != null) {
                for (ShortMessage sm : msList) {
                    int sendResult = SmsClientApiFactory.createSmsClientApi(config).sendSM(
                            sm.getRecipientMobilePhone(), sm.getBody(), sm.getSmid());
                    if (sendResult == SmsClientApi.IM_SEND_SUCC) {
                        sm.setIsReSend(false);
                        sm.setSendStatus(3); // 重发成功
                        dao.save(sm);
                    }
                }
            }

            List<ShortMessage> shortsms = new ArrayList<ShortMessage>();
            // 3.接收回执信息
            RptType[] rtps = SmsClientApiFactory.createSmsClientApi(config).receiveRPT();
            if (null != rtps) {
                for (RptType item : rtps) {
                    ShortMessage sm = dao.getObjBySmidAndRecPhone(Long.parseLong(item.getSmId()), item.getMobile(), 1,
                            0); // 确认
                    // mobile是源发送方还是接收方的
                    if (sm != null) {
                        sm.setIsReSend(false);
                        if (item.isRtpSUCC()) {
                            sm.setSendStatus(1); // 对方成功收到
                        } else {
                            sm.setSendStatus(2);
                        }
                        sm.setRecId(Integer.parseInt(item.getCode()));
                        sm.setRecMsg(item.getDesc());
                        try {
                            sm.setReceivedTime(DateUtils.parseDateTime(item.getMoTime()));
                        } catch (ParseException e) {
                            logger.error(ExceptionUtils.getStackTrace(e));
                        }
                        shortsms.add(sm);
                    }
                }
                if (shortsms.size() > 0) {
                    dao.saveAll(shortsms);
                }
            }

            shortsms = new ArrayList<ShortMessage>();
            // 4.接收MO回复短信
            MoType[] mos = SmsClientApiFactory.createSmsClientApi(config).receiveSM();
            if (null != mos) {
                for (MoType mo : mos) {
                    ShortMessage sourceSM = dao.getObjBySmidAndRecPhone(Long.parseLong(mo.getSmId()), mo.getMobile(),
                            1, 1);
                    if (sourceSM != null) {
                        ShortMessage sm = new ShortMessage();
                        sm.setSendStatus(1);
                        sm.setBody(mo.getMoContent());
                        sm.setSend(sourceSM.getReceived());
                        sm.setSendMobilePhone(mo.getMobile());
                        try {
                            sm.setSendTime(DateUtils.parseDateTime(mo.getMoTime()));
                        } catch (ParseException e) {
                            logger.error(ExceptionUtils.getStackTrace(e));
                        }

                        sm.setSenderName(sourceSM.getRecipientName());
                        sm.setReceived(sourceSM.getSend());
                        sm.setRecipientMobilePhone(sourceSM.getSendMobilePhone());
                        sm.setRecipientName(sourceSM.getSenderName());
                        sm.setSmid(Long.parseLong(mo.getSmId()));
                        sm.setType(smsClientApiFacade.getType()); // mas机
                        sm.setIsReSend(false);

                        // 如果源发送方是用户使用平台发送，需要发送短信通知其有回复
                        if (!sourceSM.getSenderName().equals("system")) {
                            if (StringUtils.isNotBlank(sm.getRecipientMobilePhone())) {
                                int result = SmsClientApiFactory.createSmsClientApi(config).sendSM(
                                        sm.getRecipientMobilePhone(), sm.getBody(), sm.getSmid());
                                if (result == SmsClientApi.IM_SEND_SUCC) {
                                    sm.setIsReSend(true);
                                    sm.setSendStatus(0);
                                }
                            }
                        }
                        shortsms.add(sm);
                    }
                }
            }
            if (shortsms.size() > 0) {
                dao.saveAll(shortsms);
            }
        }
    }

    @Override
    public MasConfig getBean() {
        List<MasConfig> masList = masConfigService.listAll();
        if (masList != null && masList.size() > 0) {
            return masList.get(0);
        } else {
            return new MasConfig();
        }
    }

    @Override
    @Transactional
    public String saveMas(MasConfig config) {
        MasConfig obj;
        if (StringUtils.isBlank(config.getUuid())) {
            obj = new MasConfig();
            BeanUtils.copyProperties(config, obj);
        } else {
            obj = masConfigService.getOne(config.getUuid());
            org.springframework.beans.BeanUtils.copyProperties(config, obj, "uuid");
        }
        masConfigService.save(obj);
        return "success";
    }

    @Override
    @Transactional
    public void sendCommonMessages(String recipients, String mbphones, String body, String reservedText1,
                                   String reservedText2, String reservedText3) {
        this.sendCommonMessages(Sets.newHashSet(recipients.split(";")), Sets.<String>newHashSet(mbphones.split(";")),
                body, reservedText1, reservedText2, reservedText3, false);
    }

    private <COLLECTION extends Collection<String>> COLLECTION arr2Collction(String[] str, COLLECTION sources) {
        COLLECTION targets = null;
        try {
            if (Set.class.isAssignableFrom(sources.getClass())) {
                targets = (COLLECTION) new HashSet<String>();
            } else {
                targets = (COLLECTION) new ArrayList<String>();
            }
            for (String s : str) {
                targets.add(s);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return targets;
    }

    private void doProcess(Class<? extends MessageProcessor> processor, Message message) {
        try {
            ApplicationContextHolder.getBean(processor).doProcessor(message);
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
    }

    @Override
    @Transactional
    public void sendCommonMessages(String recipients, String mbphones, String body, String reservedText1,
                                   String reservedText2, String reservedText3, Boolean async) {
        this.sendCommonMessages(Sets.newHashSet(recipients.split(";")), Sets.<String>newHashSet(mbphones.split(";")),
                body, reservedText1, reservedText2, reservedText3, async);
    }

    @Override
    @Transactional
    public void sendCommonMessages(Set<String> recipients, Set<String> mbphones, String body, String reservedText1,
                                   String reservedText2, String reservedText3, Boolean async) {
        Message msg = new Message();
        msg.setBody(body);
        msg.setAsync(async);
        msg.setIsread(false);
        msg.setRecipients(Lists.newArrayList(recipients));
        msg.setSender(SpringSecurityUtils.getCurrentUserId());
        msg.setSentTime(Calendar.getInstance().getTime());
        msg.setRecipientMbPhones(mbphones);
        msg.setReservedText1(reservedText1);
        msg.setReservedText2(reservedText2);
        msg.setReservedText3(reservedText3);
        doProcess(SmsMessageProcessor.class, msg);
    }

    @Override
    @Transactional
    public boolean applyConfig(MasConfig config) {
        MasConfig obj;
        if (StringUtils.isBlank(config.getUuid())) {
            obj = new MasConfig();
            BeanUtils.copyProperties(config, obj);
        } else {
            obj = masConfigService.getOne(config.getUuid());
            obj.setImIp(config.getImIp());
            obj.setApiCode(config.getApiCode());
            obj.setDbName(config.getDbName());
            obj.setIsOpen(config.getIsOpen());
            obj.setLoginName(config.getLoginName());
            obj.setLoginPassword(config.getLoginPassword());
            obj.setSendLimit(config.getSendLimit());
            // ADD
            obj.setsWebService(config.getsWebService());
            obj.setsLoginName(config.getsLoginName());
            obj.setsLoginPassword(config.getsLoginPassword());
            obj.setsSendLimit(config.getsSendLimit());
            obj.setsIsOpen(config.getsIsOpen());
            obj.setApiAsync(config.getApiAsync());
            // CLOUD
            obj.setCloudMasHttp(config.getCloudMasHttp());
            obj.setEcName(config.getEcName());
            obj.setApId(config.getApId());
            obj.setSecretKey(config.getSecretKey());
            obj.setCloudSign(config.getCloudSign());
            obj.setAddSerial(config.getAddSerial());
            obj.setCloudSendLimit(config.getCloudSendLimit());
            obj.setCloudIsOpen(config.getCloudIsOpen());
        }
        masConfigService.save(obj);
        SmsClientApiFactory.resetCountor();
        return true;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.app.xzsp.service.BanJianService#findByExample(com.wellsoft.app.xzsp.entity.BanJian)
     */
    @Override
    public List<ShortMessage> findByExampleDesc(ShortMessage example) {
        return dao.listByEntityAndPage(example, null, "createTime desc");
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.message.service.ShortMessageService#getShortMessageByUuid(java.lang.String)
     */
    @Override
    public Map<String, String> getShortMessageByUuid(String uuid) {
        Map<String, String> shortMessageMap = new HashMap<String, String>();
        ShortMessage shortMessage = dao.getOne(uuid);
        shortMessageMap.put("senderName", shortMessage.getSenderName());
        shortMessageMap.put("body", shortMessage.getBody());
        shortMessageMap.put("sendTime", DateUtil.getFormatDate(shortMessage.getSendTime(), "yyyy-MM-dd HH:mm"));
        shortMessageMap.put("recipientName", shortMessage.getRecipientName());
        shortMessageMap.put("recipientMobilePhone", shortMessage.getRecipientMobilePhone());
        Boolean isReSend = shortMessage.getIsReSend();
        if (isReSend) {
            shortMessageMap.put("sendStatus", "发送失败");
        } else {
            shortMessageMap.put("sendStatus", "发送成功");
        }
        if (StringUtils.isNotBlank(shortMessage.getRecMsg())) {
            shortMessageMap.put("recMsg", shortMessage.getRecMsg());
        } else {
            shortMessageMap.put("recMsg", "");
        }
        return shortMessageMap;
    }

    /**
     * 更新smid 沈阳短信接口发送时返回smid
     *
     * @param newSmId
     * @param oldSmId
     * @return
     */
    @Override
    public Boolean addWsIdBySmId(long SmId, String wsId) {
        try {
            ShortMessage sm = dao.getBySmid(SmId);
            sm.setReservedText6(wsId);
            sm.setIsReSend(false);
            dao.save(sm);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public List<ShortMessage> getListOfNotRPT(int type, int sendStatus) {
        return dao.getListOfNotRPT(type, sendStatus);
    }

    @Override
    public void cloudMasReport(String json) {
        CloudMasReport rep = (CloudMasReport) JSONObject.toBean(JSONObject.fromObject(json), CloudMasReport.class);
        if (rep != null) {
            ShortMessage sm = dao.getObjByMsgGroupAndRecPhone(rep.getMsgGroup(), rep.getMobile());
            if (sm != null) {
                Date receivedTime;
                try {
                    receivedTime = new SimpleDateFormat("yyyyMMddHHmmss").parse(rep.getReceiveDate());
                    sm.setReceivedTime(receivedTime);
                } catch (ParseException e) {
                    logger.error(ExceptionUtils.getFullStackTrace(e));
                }
                sm.setRecMsg(rep.getReportStatus() + ";" + rep.getErrorCode());
            }
        }

    }

    @Override
    public List<ShortMessage> listByMessageOutboxUuid(String messageOutboxUuid) {
        String hql = "from ShortMessage where messageOutboxUuid = :messageOutboxUuid";
        Map<String, Object> params = Maps.newHashMap();
        params.put("messageOutboxUuid", messageOutboxUuid);
        return dao.listByHQL(hql, params);
    }

}
