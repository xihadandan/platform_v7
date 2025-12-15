package com.wellsoft.pt.integration.service.impl;

import com.sunsharing.collagen.filex.*;
import com.wellsoft.context.config.Config;
import com.wellsoft.context.http.HttpUtil;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.RandomUtil;
import com.wellsoft.context.util.SerializableUtil;
import com.wellsoft.pt.integration.entity.*;
import com.wellsoft.pt.integration.facade.ExchangedataApiFacade;
import com.wellsoft.pt.integration.request.DXRequest;
import com.wellsoft.pt.integration.security.ExchangeConfig;
import com.wellsoft.pt.integration.service.ExchangeDataFlowService;
import com.wellsoft.pt.integration.service.ExchangeDataSynSQLDService;
import com.wellsoft.pt.integration.service.ZongXianExchangeService;
import com.wellsoft.pt.integration.support.*;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.mt.entity.Tenant;
import com.wellsoft.pt.mt.facade.service.TenantFacadeService;
import com.wellsoft.pt.multi.org.bean.OrgUserVo;
import com.wellsoft.pt.security.core.userdetails.DefaultUserDetails;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.IgnoreLoginUtils;
import com.wellsoft.pt.security.util.TenantContextHolder;
import com.wellsoft.pt.unit.entity.CommonUnit;
import com.wellsoft.pt.unit.facade.service.UnitApiFacade;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.cxf.ws.security.wss4j.WSS4JInInterceptor;
import org.apache.wss4j.dom.WSSecurityEngineResult;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.activation.DataHandler;
import javax.annotation.Resource;
import javax.xml.ws.WebServiceContext;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.X509Certificate;
import java.sql.Blob;
import java.util.*;

/**
 * Description: 路由规则业务类
 *
 * @author Administrator
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-11-18.1	wbx		2013-11-18		Create
 * </pre>
 * @date 2013-11-18
 */
@Service
@Transactional
public class ZongXianExchangeServiceImpl extends BaseServiceImpl implements
        ZongXianExchangeService {

    public static String GHJ_UNITID = "K19051188";
    // 需要重发的间隔时间（分钟）
    public static double REPEAT_TIME = 2;
    public String SAVE_DATA_ID = "";
    // 通道（正式/测试）
    String sunsharingType = Config.getValue("sunsharing.gd.type");
    String sunsharingTypeTest = "test";
    String sunsharingTypeFormal = "formal";
    @Resource
    private WebServiceContext context;
    @Autowired
    private ExchangedataApiFacade exchangedataApiFacade;
    @Autowired
    private ExchangeDataFlowService exchangeDataFlowService;
    @Autowired
    private ExchangeDataSynSQLDService exchangeDataSynSQLDService;

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.service.ZongXianExchangeService#submit(com.wellsoft.pt.integration.request.DXRequest)
     */
    @Override
    public synchronized boolean submit(DXRequest request) {
        try {
            List<Attachment> attachments = new ArrayList<Attachment>();
            List<DXDataItem> dataList = request.getDataList();
            for (DXDataItem dxDataItem : dataList) {
                List<StreamingData> streamingDataList = dxDataItem.getStreamingDatas();
                for (StreamingData streamingData : streamingDataList) {
                    Attachment attachment = new Attachment();
                    attachment.id = RandomUtil.getRandomNumber(5);
                    attachment.name = streamingData.getFileName();
                    attachment.description = "";
                    byte[] appliance_data = null;
                    try {
                        appliance_data = SerializableUtil.input2byte(
                                streamingData.getDataHandler().getInputStream());
                    } catch (IOException e) {
                        logger.info(
                                "************附件[" + streamingData.getFileName() + "]转byte数组异常！************");
                        logger.error(e.getMessage(), e);
                    }
                    attachment.data = appliance_data;
                    attachment.checkValue = "";
                    attachments.add(attachment);
                    // 对dxRequest进行序列化,去除附件字段DataHandler
                    streamingData.setDataHandler(null);
                }
            }
            byte[] baseInfo = SerializableUtil.ObjectToByte(request);
            boolean flag = true;
            if (GHJ_UNITID.equals(request.getTo())) {// 如果目标id为规划局时
                flag = submit(request.getTo(), baseInfo, attachments, "", false);
            } else {
                // 保存发送的光盾数据
                flag = saveGuangDunSend(request.getTo(), baseInfo, attachments,
                        request.getTypeId());
            }
            return flag;
        } catch (Exception e) {
            logger.info(
                    "************组织准备发送到目标方id[" + request.getTo() + "],业务数据类型[" + request.getTypeId()
                            + "]************");
            logger.error(e.getMessage(), e);
            return false;
        }

    }

    /**
     * 保存发送的光盾数据
     *
     * @param toId        目标
     * @param baseInfo    光盾数据主体信息
     * @param attachments 光盾附件信息
     * @param typeId      数据类型
     * @return
     */
    @Override
    public synchronized boolean saveGuangDunSend(String toId, byte[] baseInfo,
                                                 List<Attachment> attachments,
                                                 String typeId) {
        boolean flag = true;
        try {
            String preDataId = "";
            List<GuangDunPoint> points = this.dao.findByExample(new GuangDunPoint());
            if (StringUtils.isBlank(this.SAVE_DATA_ID) && points != null && !points.isEmpty()
                    && StringUtils.isNotBlank(points.get(0).getSaveDataId())) {
                preDataId = points.get(0).getSaveDataId();
            } else {
                preDataId = this.SAVE_DATA_ID;
            }
            String dataId = UUID.randomUUID().toString();
            GuangDunSend gds = new GuangDunSend();
            gds.setToId(toId);
            gds.setDataId(dataId);
            gds.setPreDataId(preDataId);
            gds.setSubject(Hibernate.getLobCreator(this.dao.getSession()).createBlob(baseInfo));
            gds.setIsBack(0);
            this.dao.save(gds);
            this.saveGuangDunFile(attachments, dataId);
            if (points != null && !points.isEmpty()) {
                // 更新指针表
                this.updatePointSaveDataId(dataId);
            } else {
                GuangDunPoint point = new GuangDunPoint();
                point.setSaveDataId(dataId);
                this.dao.save(point);
            }
            this.SAVE_DATA_ID = dataId;
        } catch (Exception e) {
            logger.info(
                    "************保存发送光盾数据表[is_guangdun_send]异常,目标方id[" + toId + "],业务数据类型[" + typeId
                            + "]************");
            logger.error(e.getMessage(), e);
            flag = false;
            this.SAVE_DATA_ID = "";
            throw new RuntimeException(e);
        }

        return flag;
    }

    @Override
    public void updatePointSaveDataId(String saveDataId) {
        StringBuffer hql = new StringBuffer();
        hql.append(" update  GuangDunPoint  p  set p.saveDataId=:saveDataId ");
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("saveDataId", saveDataId);
        this.nativeDao.update(hql.toString(), paramMap);
    }

    @Override
    public void updatePointSendDataId(String sendDataId) {
        StringBuffer hql = new StringBuffer();
        hql.append(" update  GuangDunPoint  p  set p.sendDataId=:sendDataId ");
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("sendDataId", sendDataId);
        this.nativeDao.update(hql.toString(), paramMap);
    }

    @Override
    public void updatePointReceiveDataId(String receiveDataId) {
        StringBuffer hql = new StringBuffer();
        hql.append(" update  GuangDunPoint  p  set p.receiveDataId=:receiveDataId ");
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("receiveDataId", receiveDataId);
        this.nativeDao.update(hql.toString(), paramMap);
    }

    @Override
    public void updatePointErrDataIdAndStatus(String errDataId, Integer status) {
        StringBuffer hql = new StringBuffer();
        hql.append(" update  GuangDunPoint  p  set p.errDataId=:errDataId,p.status=:status");
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("errDataId", errDataId);
        paramMap.put("status", status);
        this.nativeDao.update(hql.toString(), paramMap);
    }

    @Override
    public void updateReceiveIsBack(String dataId, Integer isBack) {
        StringBuffer hql = new StringBuffer();
        hql.append(" update  GuangDunReceive  r set r.isBack=:isBack where r.dataId=:dataId");
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("dataId", dataId);
        paramMap.put("isBack", isBack);
        this.nativeDao.update(hql.toString(), paramMap);
    }

    @Override
    public void updateReceiveStatus(String dataId, Integer status) {
        StringBuffer hql = new StringBuffer();
        hql.append(" update  GuangDunReceive  r set r.status=:status where r.dataId=:dataId");
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("dataId", dataId);
        paramMap.put("status", status);
        this.nativeDao.update(hql.toString(), paramMap);
    }

    @Override
    public void updateSendIsBack(String dataId, Integer isBack) {
        try {
            StringBuffer hql = new StringBuffer();
            hql.append(" update  GuangDunSend  s set s.isBack=:isBack where s.dataId=:dataId");
            Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("dataId", dataId);
            paramMap.put("isBack", isBack);
            this.nativeDao.update(hql.toString(), paramMap);
        } catch (Exception e) {
            logger.info(
                    "************接受反馈机制,更新发送光盾数据表[is_guangdun_send]异常,数据id[" + dataId + "]************");
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.service.ZongXianExchangeService#sendDataAsynchronousJob()
     */
    @Override
    public void sendDataAsynchronousJob() {
        GuangDunSend gds = new GuangDunSend();
        List<GuangDunPoint> points = this.dao.findByExample(new GuangDunPoint());
        if (points != null && !points.isEmpty() && StringUtils.isNotBlank(
                points.get(0).getSendDataId())) {
            GuangDunPoint point = points.get(0);
            GuangDunSend guangDunSend = new GuangDunSend();
            guangDunSend.setPreDataId(point.getSendDataId());
            List<GuangDunSend> gdsList = this.dao.findByExample(guangDunSend);
            if (gdsList != null && !gdsList.isEmpty()) {
                gds = gdsList.get(0);
            }
        } else {
            PagingInfo pagingInfo = new PagingInfo();
            pagingInfo.setPageSize(1);
            pagingInfo.setCurrentPage(1);
            List<GuangDunSend> list = this.dao.findByExample(new GuangDunSend(), "createTime asc",
                    pagingInfo);
            if (list != null && !list.isEmpty()) {
                gds = list.get(0);
            }
        }
        if (gds != null && StringUtils.isNotBlank(gds.getDataId())) {
            String dataId = gds.getDataId();
            try {
                byte[] subjectByte = {};
                Map<String, Object> subject = new HashMap<String, Object>();
                byte[] baseInfo = blobToBytes(gds.getSubject());
                subject.put("data", SerializableUtil.ByteToObject(baseInfo));
                subject.put("dataId", dataId);
                subject.put("preDataId", gds.getPreDataId());
                subject.put("fromId", XZSPBIZ.UNIT_ID_TYDJ);
                subjectByte = SerializableUtil.ObjectToByte(subject);
                // 获取发送附件信息
                List<Attachment> attachments = getGuangDunFiles(dataId);
                // 调用光盾发送数据
                submit(gds.getToId(), subjectByte, attachments, dataId, false);
            } catch (Exception e) {
                logger.info(
                        "************调用光盾发送数据异常,表[is_guangdun_send]数据dataId[" + dataId + "]目标方id[" + gds.getToId()
                                + "]************");
                logger.error(e.getMessage(), e);
            }
        }

    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.service.ZongXianExchangeService#submit(java.util.List,
     * byte[], java.util.List)
     */
    // GHJ SPXT 规划-审批系统
    // GHJ DGPT 规划-多规合一平台
    // JSJ SJG 建设局-建管
    // JM QJG 集美区-建管
    // XZFWZX JSXMSP 行政服务中心-建设项目审批

    // JM QJG2 集美区-建管测试
    // XZFWZX JSXMSP_CS 行政服务中心-建设项目审批-测试
    @Override
    public boolean submit(String toId, byte[] baseInfo, List<Attachment> attachments, String dataId,
                          boolean isRepeatEnd) {
        try {
            Boolean isSqldPass = true;
            // 初始化通道
            DataTransfer transfer = new DataTransfer();
            if (!initZongxianPath(transfer)) {
                return false;
            }
            List<SubmitTarget> targets = new ArrayList<SubmitTarget>();
            if (XZSPBIZ.UNIT_ID_TYDJ.equals(XZSPBIZ.UNIT_ID_CENTER)) {
                if (sunsharingType.equals(sunsharingTypeFormal)) {
                    targets.add(new SubmitTarget("JM", "QJG"));
                } else {
                    targets.add(new SubmitTarget("JM", "QJG2"));
                }
            } else {// 集美
                if (toId.equals(GHJ_UNITID)) {// 规划局
                    targets.add(new SubmitTarget("GHJ", "SPXT"));
                    isSqldPass = false;
                } else if (toId.contains(XZSPBIZ.UNIT_ID_CENTER)) {
                    if (sunsharingType.equals(sunsharingTypeFormal)) {
                        targets.add(new SubmitTarget("XZFWZX", "JSXMSP"));
                    } else {
                        targets.add(new SubmitTarget("XZFWZX", "JSXMSP_CS"));
                    }
                } else {
                    if (sunsharingType.equals(sunsharingTypeFormal)) {
                        targets.add(new SubmitTarget("XZFWZX", "JSXMSP"));
                    } else {
                        targets.add(new SubmitTarget("XZFWZX", "JSXMSP_CS"));
                    }
                }
            }
            SubmitResult result = transfer.submit(targets, baseInfo, attachments);
            if (!result.submitted) {
                // 提交到平台失败，出错处理
                // 出错时一般是再次发送
                logger.info("第一次调用畅享transfer.submit方法出错，错误信息：》》》》》》》》》》》》》》》》》" + result.msg
                        + ";出错的taskId为：》》》》》》》》》》》》" + result.taskId);
                int submitSize = 10;
                for (int i = 0; i < submitSize; i++) {
                    SubmitResult secondSubmitResult = transfer.submit(targets, baseInfo,
                            attachments);
                    if (!secondSubmitResult.submitted) {
                        logger.info(
                                "第一次调用出错后再次发送，第" + (i + 2) + "次调用畅享transfer.submit方法出错，错误信息：》》》》》》》》》》》》》》》》》"
                                        + secondSubmitResult.msg + ";出错的taskId为：》》》》》》》》》》》》" + secondSubmitResult.taskId);
                    } else {
                        if (isSqldPass) {
                            // 更新发送数据及指针表
                            updateGuangDunSendAndPoint(dataId, secondSubmitResult.taskId,
                                    isRepeatEnd);
                        }
                        return true;
                    }
                }
                return false;
            } else {
                if (isSqldPass) {
                    // 更新发送数据及指针表
                    updateGuangDunSendAndPoint(dataId, result.taskId, isRepeatEnd);
                }
                System.out.println("数据成功提交到业务协同平台，任务号：" + result.taskId);
                return true;
            }
        } catch (Exception e) {
            logger.error("******************写入总线异常*************");
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * 更新发送数据及指针表
     *
     * @param dataId      发送数据id
     * @param taskId      光盾发送任务id
     * @param isRepeatEnd 是否重发(true/false:是/否 )
     */
    @Override
    public void updateGuangDunSendAndPoint(String dataId, String taskId, boolean isRepeatEnd) {
        try {
            GuangDunSend gdsExample = new GuangDunSend();
            gdsExample.setDataId(dataId);
            List<GuangDunSend> gdsList = this.dao.findByExample(gdsExample);
            if (gdsList != null && !gdsList.isEmpty()) {
                GuangDunSend gds = gdsList.get(0);
                gds.setTaskId(taskId);
                gds.setPreSendDate(new Date());
                this.dao.save(gds);
            }
            if (!isRepeatEnd && StringUtils.isNotBlank(dataId)) {//
                updatePointSendDataId(dataId);
            }

        } catch (Exception e) {
            String msg = "发送：";
            if (isRepeatEnd) {
                msg = "重新发送：";
            }
            logger.info("************数据dataId[" + dataId + "]" + msg
                    + "更新光盾表[is_guangdun_send或者is_guangdun_point]数据异常!************");
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }

    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.app.xzsp.webservice.service.PickNumberWebService#dataArrived(java.lang.String,
     * java.lang.String, java.lang.String)
     */
    @Override
    public boolean dataArrived(String taskId, String dataPath, String msg) {
        try {
            DataTransfer transfer = new DataTransfer();
            if (initZongxianPath(transfer)) {
                ReadResult res = transfer.read(taskId, dataPath);
                if (!res.isSuccess) {
                    // …出错处理
                    logger.error(
                            "******************光盾任务id[" + taskId + "]调用总线dataArrived方法出错:" + res.msg
                                    + "******************");
                    System.out.println(res.msg);
                    return false;
                }
                if (taskId.startsWith("GHJ-SPXT")) {// 规划局
                    dataArrivedBuss(res.baseInfo, res.attachments, taskId);
                } else {
                    this.saveGuangDunReceive(taskId, res.baseInfo, res.attachments);
                }
            } else {
                return false;
            }
            return true;
        } catch (Exception e) {
            logger.error("******************光盾任务id[" + taskId + "]接收总线通知异常*************");
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * 如何描述该方法
     */
    @Override
    public void dataArrivedAsynchronous() {
        GuangDunReceive gdr = getGuangDunReceiveData();
        Map<String, SysProperties> sysPropertiess = exchangedataApiFacade.getAllSysProperties(
                "SYSPROPERTIES");
        String sysContextPath = sysPropertiess.get("sys_context_path").getProValue();
        try {
            if (StringUtils.isBlank(gdr.getUuid())) {
                return;
            }
            byte[] baseInfo = blobToBytes(gdr.getSubject());
            // 获取附件信息
            List<Attachment> attachments = this.getGuangDunFiles(gdr.getDataId());
            dataArrivedBuss(baseInfo, attachments, gdr.getTaskId());
            this.updateGuangDunReceiveAndPoint(gdr.getDataId());
        } catch (Exception e) {
            logger.error("******************还原光盾数据异常,数据Id[" + gdr.getDataId() + "]*************");
            logger.error(e.getMessage(), e);
            // 如果还原光盾数据出现业务异常，更新该数据为已还原状态“1”并更新指针
            String path = sysContextPath + "/exchangedata/client/synUpdate?className=guangDunSynUpdateClass&json="
                    + gdr.getDataId();
            HttpUtil.getUrlRetrunString(path);
        }
    }

    @Override
    public void updateGuangDunReceiveAndPoint(String dataId) {
        try {
            List<GuangDunPoint> points = this.dao.findByExample(new GuangDunPoint());
            if (points != null && !points.isEmpty()) {
                this.updatePointReceiveDataId(dataId);
            } else {
                GuangDunPoint guangDunPoint = new GuangDunPoint();
                guangDunPoint.setReceiveDataId(dataId);
                this.dao.save(guangDunPoint);
            }
            this.updateReceiveStatus(dataId, 1);// 设置为还原状态
        } catch (Exception e) {
            logger.info(
                    "************更新光盾表[is_guangdun_receive或者is_guangdun_point]数据异常!数据dataId[" + dataId
                            + "]************");
            logger.error(e.getMessage(), e);
            logger.error(e.getMessage(), e);
            throw new RuntimeException();
        }
    }

    @Override
    public void dataArrivedBuss(byte[] baseInfo, List<Attachment> attachments, String taskId) {
        try {
            Object obj = SerializableUtil.ByteToObject(baseInfo);
            if (obj instanceof DXRequest) {
                DXRequest request = (DXRequest) obj;
                List<DXDataItem> dataList = request.getDataList();
                for (DXDataItem dxDataItem : dataList) {
                    List<StreamingData> streamingDatas = dxDataItem.getStreamingDatas();
                    for (StreamingData streamingData : streamingDatas) {
                        String fileName = streamingData.getFileName();
                        // 获取附件对象
                        for (Attachment attachment : attachments) {
                            String attachmentFileName = attachment.name;
                            if (attachmentFileName.equals(fileName)) {
                                byte[] data = attachment.data;
                                InputStream dataHandlerInputStream = SerializableUtil.byte2Input(
                                        data);
                                streamingData.setDataHandler(
                                        new DataHandler(new InputStreamDataSource(
                                                dataHandlerInputStream, "octet-stream")));
                            }
                        }
                    }
                }
                // 模拟单位用户登录
                // 获取单位业务负责人
                UnitApiFacade unitApiFacade = ApplicationContextHolder.getBean(UnitApiFacade.class);
                List<OrgUserVo> users = unitApiFacade.getBusinessUnitManagerById("XZSP",
                        request.getFrom());
                OrgUserVo user = users.get(0);
                TenantFacadeService tenantService = ApplicationContextHolder.getBean(
                        TenantFacadeService.class);
                String tenantId = getTanentId(request.getFrom());
                Tenant tenant = null;
                tenant = tenantService.getByAccount(tenantId);
                UserDetails userDetails = new DefaultUserDetails(tenant, user,
                        AuthorityUtils.createAuthorityList());
                CommonUnit commonUnit = unitApiFacade.getCommonUnitById(request.getFrom());
                CommonUnit unit = new CommonUnit();
                BeanUtils.copyProperties(commonUnit, unit);
                // userDetails.setCommonUnit("XZSP", commonUnit);
                UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        userDetails.getPassword());
                SecurityContextHolder.getContext().setAuthentication(authRequest);
                // 屏蔽业务代码 --zyguo
                // XzspZxsjRecord sjRecord = new XzspZxsjRecord();
                // sjRecord.setTaskId(taskId);
                // sjRecord.setTriggerTime(new Date());
                // xzspZxsjRecordDao.save(sjRecord);
                exchangeDataFlowService.dxSend(request, ExchangeConfig.SOURCE_EXTERNAL);
            } else if (obj instanceof Map) {
                try {
                    IgnoreLoginUtils.login("T001", "T001");
                    // 屏蔽业务代码 --zyguo
                    // XzspZxsjRecord sjRecord = new XzspZxsjRecord();
                    // sjRecord.setTaskId(taskId);
                    // sjRecord.setTriggerTime(new Date());
                    // xzspZxsjRecordDao.save(sjRecord);
                    if (((Map) obj).containsKey("triggerCount")) {
                        @SuppressWarnings("unchecked")
                        Map<String, Long> retMap = (Map<String, Long>) obj;
                        exchangeDataSynSQLDService.triggerCountIn(retMap);
                    } else {
                        @SuppressWarnings("unchecked")
                        Map<String, List<Map<String, Object>>> retMap = (Map<String, List<Map<String, Object>>>) obj;
                        exchangeDataSynSQLDService.trunkSynInData(retMap);
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                } finally {
                    IgnoreLoginUtils.logout();
                }
            }
        } catch (Exception e) {
            logger.error("******************还原光盾数据异常,任务id[" + taskId + "]*************");
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }

    }

    @Override
    public String getTanentId(String unitId) {
        try {
            String tenantId = TenantContextHolder.getTenantId();
            String tenantId2 = Config.COMMON_TENANT;
            if (tenantId.equals(tenantId2) && !"".equals(
                    unitId)) {// tenantId为公共库租户Id时，请求的为公共WebService
                UnitApiFacade unitApiFacade = ApplicationContextHolder.getBean(UnitApiFacade.class);
                tenantId = unitApiFacade.getTenantIdByCommonUnitId(unitId);
            }
            return tenantId;
        } catch (Exception e) {
            logger.error("******************获取租户ID异常*************");
            logger.error(e.getMessage(), e);
            return Config.COMMON_TENANT;
        }

    }

    /**
     * 初始化总线路径,成功返回true
     *
     * @return
     */
    public boolean initZongxianPath(DataTransfer transfer) {
        try {
            Map<String, SysProperties> sysPropertiess = exchangedataApiFacade.getAllSysProperties(
                    "SYSPROPERTIES");
            if (!sysPropertiess.containsKey("sqld_zongxian_init_path")) {
                logger.error("管理后台未配置畅享总线初始化路径！");
                return false;
            }
            String zongxianInitPath = sysPropertiess.get("sqld_zongxian_init_path").getProValue();
            if (!transfer.init(zongxianInitPath)) {
                System.out.println("初始化畅享SDK失败！");
                return false;
            }
            return true;
        } catch (Exception e) {
            logger.error("******************初始化总线异常*************");
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    public void saveJmRecord(String taskId) {
        // 屏蔽业务代码 --zyguo
        // XzspZxjmRecord jmRecord = new XzspZxjmRecord();
        // jmRecord.setTaskId(taskId);
        // jmRecord.setTriggerTime(new Date());
        // xzspZxjmRecordDao.save(jmRecord);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void saveGuangDunReceive(String taskId, byte[] baseInfo, List<Attachment> attachments) {
        try {
            IgnoreLoginUtils.login("T001", "T001");
            Map<String, Object> subjectByte = (Map<String, Object>) SerializableUtil.ByteToObject(
                    baseInfo);
            if (subjectByte.containsKey("back_data")) {// 接收反馈机制,更新为已反馈
                String dataId = subjectByte.get("back_data").toString();
                this.updateSendIsBack(dataId, 1);
            } else {
                String dataId = subjectByte.get("dataId").toString();
                GuangDunReceive gdr = new GuangDunReceive();
                GuangDunReceive gdrExample = new GuangDunReceive();
                gdrExample.setDataId(dataId);
                List<GuangDunReceive> gdrs = this.dao.findByExample(gdrExample);
                if (gdrs != null && !gdrs.isEmpty()) {
                    gdr = gdrs.get(0);
                    this.updateReceiveIsBack(gdr.getDataId(), 1);
                } else {
                    gdr.setTaskId(taskId);
                    gdr.setDataId(dataId);
                    gdr.setPreDataId(
                            subjectByte.get("preDataId") == null ? "" : subjectByte.get("preDataId")
                                    .toString());
                    gdr.setStatus(0);
                    gdr.setIsBack(0);
                    gdr.setToId(subjectByte.get("fromId").toString());
                    gdr.setSubject(Hibernate.getLobCreator(this.dao.getSession()).createBlob(
                            SerializableUtil.ObjectToByte(subjectByte.get("data"))));
                    this.dao.save(gdr);
                    this.saveGuangDunFile(attachments, dataId);
                }
            }

        } catch (Exception e) {
            logger.error("******************保存接收的光盾数据异常!光盾任务Id[" + taskId + "]*************");
            logger.error(e.getMessage(), e);
        } finally {
            IgnoreLoginUtils.logout();
        }
    }

    /**
     * 保存光盾附件信息
     *
     * @param attachments
     */
    @Override
    public void saveGuangDunFile(List<Attachment> attachments, String dataId) {
        try {
            if (attachments != null && !attachments.isEmpty()) {
                List<GuangDunFile> files = new ArrayList<GuangDunFile>();
                for (Attachment atta : attachments) {
                    GuangDunFile file = new GuangDunFile();
                    file.setId(atta.id);
                    file.setCheckValue(atta.checkValue);
                    file.setName(atta.name);
                    file.setDescription(atta.description);
                    file.setDataId(dataId);
                    file.setData(
                            Hibernate.getLobCreator(this.dao.getSession()).createBlob(atta.data));
                    files.add(file);
                }
                this.dao.saveAll(files);
            }
        } catch (Exception e) {
            logger.error("******************保存光盾附件数据异常！数据id[" + dataId + "]*************");
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取光盾附件信息
     *
     * @param dataId
     * @return
     */
    @Override
    public List<Attachment> getGuangDunFiles(String dataId) {
        List<Attachment> attachments = new ArrayList<Attachment>();
        if (StringUtils.isNotBlank(dataId)) {
            GuangDunFile fileExample = new GuangDunFile();
            fileExample.setDataId(dataId);
            List<GuangDunFile> files = this.dao.findByExample(fileExample);
            if (files != null && !files.isEmpty()) {
                for (GuangDunFile file : files) {
                    Attachment atta = new Attachment();
                    atta.checkValue = file.getCheckValue();
                    atta.id = file.getId();
                    atta.name = file.getName();
                    atta.description = file.getDescription();
                    atta.data = blobToBytes(file.getData());
                    attachments.add(atta);
                }
            }

        }

        return attachments;
    }

    @Override
    public GuangDunReceive getGuangDunReceiveData() {
        GuangDunReceive guangDunReceive = new GuangDunReceive();
        try {
            List<GuangDunPoint> points = this.dao.findByExample(new GuangDunPoint());
            if (points.size() > 0 && StringUtils.isNotBlank(points.get(0).getReceiveDataId())) {
                GuangDunPoint point = points.get(0);
                String preDataId = point.getReceiveDataId();
                GuangDunReceive gdr = new GuangDunReceive();
                gdr.setPreDataId(preDataId);
                List<GuangDunReceive> gdrs = this.dao.findByExample(gdr);
                if (gdrs.size() == 0) {
                    GuangDunReceive guangDunReceiveExample = new GuangDunReceive();
                    guangDunReceiveExample.setStatus(0);
                    long count = this.dao.countByExample(guangDunReceiveExample);
                    if (count > 0) {
                        logger.info(
                                "******************未找到下一条数据，数据阻塞，当前的dataId为[" + preDataId + "]******************");
                        updatePointErrDataIdAndStatus(preDataId, 1);
                    } else {
                        if (point.getStatus() != null && point.getStatus() == 1) {// 解除阻塞状态
                            updatePointErrDataIdAndStatus("", 0);
                        }
                    }
                } else {
                    guangDunReceive = gdrs.get(0);
                }
            } else {// 取创建时间最早的一条
                PagingInfo pagingInfo = new PagingInfo();
                pagingInfo.setPageSize(1);
                pagingInfo.setCurrentPage(1);
                List<GuangDunReceive> list = this.dao
                        .findByExample(new GuangDunReceive(), "createTime asc", pagingInfo);
                if (list != null && !list.isEmpty()) {
                    guangDunReceive = list.get(0);
                }
            }
        } catch (Exception e) {
            logger.info("******************还原光盾数据任务获取需要的还原数据失败！******************");
            logger.error(e.getMessage(), e);
        }
        return guangDunReceive;
    }

    @Override
    public Boolean repeatSend(String dataId) {
        GuangDunSend gds = new GuangDunSend();
        try {
            GuangDunSend gdsExample = new GuangDunSend();
            gdsExample.setDataId(dataId);
            List<GuangDunSend> gdss = this.dao.findByExample(gdsExample);
            if (gdss.size() == 0) {
                return false;
            }
            gds = gdss.get(0);
            byte[] baseInfo = blobToBytes(gds.getSubject());
            // 获取发送附件信息
            List<Attachment> attachments = getGuangDunFiles(dataId);
            // 调用光盾发送数据
            return submit(gds.getToId(), baseInfo, attachments, dataId, true);
        } catch (Exception e) {
            logger.info(
                    "************重发数据：调用光盾发送数据异常,表[is_guangdun_send]数据dataId[" + dataId + "],目标方id["
                            + gds.getToId() + "]************");
            logger.error(e.getMessage(), e);
            return false;
        }

    }

    /**
     * 附件数据解密等处理
     *
     * @param request
     * @throws Exception
     */
    @Override
    public void dxDecryptedAndPrepareStreamingData(DXRequest request) throws Exception {
        try {
            WSSecurityEngineResult wser = (WSSecurityEngineResult) context.getMessageContext().get(
                    WSS4JInInterceptor.SIGNATURE_RESULT);
            X509Certificate cert = (X509Certificate) wser.get(
                    WSSecurityEngineResult.TAG_X509_CERTIFICATE);
            // 附件验签解密
            Map<String, Object> props = MerlinCrypto.getInstace().getWSS4JAttachmentPropsForX509Cert(
                    cert);
            for (DXDataItem dataItem : request.getDataList()) {
                List<StreamingData> streamingDatas = dataItem.getStreamingDatas();
                List<StreamingData> decryptedStreamingDatas = new ArrayList<StreamingData>();
                for (StreamingData streamingData : streamingDatas) {
                    WSS4JIntAttachment wss4jIntAttachment = null;
                    try {
                        wss4jIntAttachment = new WSS4JIntAttachment(streamingData, props);
                        wss4jIntAttachment.verifyAndDecrypt();
                        if (!wss4jIntAttachment.getVerifyValue()) {
                            throw new RuntimeException(
                                    "附件[" + streamingData.getFileName() + "]签名验证失败!");
                        }
                        decryptedStreamingDatas.add(wss4jIntAttachment.getDecryptStreamingData());
                    } catch (Exception e) {
                        logger.info(e.getMessage());
                        throw e;
                    }
                }
                dataItem.setStreamingDatas(decryptedStreamingDatas);
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
            throw e;
        }
    }

    public void testJM(byte[] baseInfo, List<Attachment> attachments) {
        /************** 测试发送代码开始 *************/
        // byte[] baseInfo = res.baseInfo;
        Object dxRequest = SerializableUtil.ByteToObject(baseInfo);
        if (dxRequest instanceof DXRequest) {
            DXRequest requst = (DXRequest) dxRequest;
            List<DXDataItem> dataList1 = requst.getDataList();
            for (DXDataItem dxDataItem : dataList1) {
                List<StreamingData> streamingDatas = dxDataItem.getStreamingDatas();
                for (StreamingData streamingData : streamingDatas) {
                    String fileName = streamingData.getFileName();
                    // 获取附件对象
                    List<Attachment> attachments1 = attachments;
                    for (Attachment attachment : attachments1) {
                        String attachmentFileName = attachment.name;
                        if (attachmentFileName.equals(fileName)) {
                            byte[] data = attachment.data;
                            InputStream dataHandlerInputStream = SerializableUtil.byte2Input(data);
                            streamingData.setDataHandler(new DataHandler(new InputStreamDataSource(
                                    dataHandlerInputStream, "octet-stream")));
                        }
                    }
                }
            }
            requst.setDataList(dataList1);
            // 附件解密处理
            try {
                dxDecryptedAndPrepareStreamingData(requst);
                List<DXDataItem> list = requst.getDataList();
                for (DXDataItem dxDataItem : list) {
                    List<StreamingData> streamList = dxDataItem.getStreamingDatas();
                    for (StreamingData streamingData : streamList) {
                        InputStream inputStream2 = streamingData.getDataHandler().getInputStream();
                        FileUtils.copyInputStreamToFile(inputStream2,
                                new File("./", streamingData.getFileName()));
                    }
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                logger.info(e.getMessage());
                logger.error("集美总线 dataArrived 方法附件解密异常：》》》》》》》》》》》》》》》》》》" + e.getMessage());
            }
            System.out.println("");
        }

        /************* 测试发送代码结束 **************/
    }

    /**
     * 将blob转化为byte[],可以转化二进制流的
     *
     * @param blob
     * @return
     */
    private byte[] blobToBytes(Blob blob) {
        byte[] b = null;
        if (blob == null) {
            return b;
        }
        InputStream is = null;
        try {
            is = blob.getBinaryStream();
            b = new byte[(int) blob.length()];
            is.read(b);
            return b;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
                is = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return b;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.service.ZongXianExchangeService#feedBackJob()
     */
    @Override
    public void feedBackJob() {
        GuangDunReceive gdrExample = new GuangDunReceive();
        gdrExample.setIsBack(0);
        List<GuangDunReceive> gdrLsit = this.dao.findByExample(gdrExample);
        if (gdrLsit != null && !gdrLsit.isEmpty()) {
            for (GuangDunReceive gdr : gdrLsit) {
                String toId = gdr.getToId();
                String dataId = gdr.getDataId();
                try {
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("back_data", dataId);
                    byte[] subjectByte = SerializableUtil.ObjectToByte(map);
                    boolean flag = feedBackSubmit(toId, subjectByte, new ArrayList<Attachment>(),
                            dataId);
                    if (flag) {
                        // 更新未已反馈
                        this.updateReceiveIsBack(gdr.getDataId(), 1);
                    }
                } catch (Exception e) {
                    logger.info(
                            "************反馈机制异常,反馈数据dataId[" + dataId + "],目标方id[" + toId + "]************");
                    logger.error(e.getMessage(), e);
                }
            }
        }

    }

    @Override
    public boolean feedBackSubmit(String toId, byte[] baseInfo, List<Attachment> attachments,
                                  String dataId) {
        try {
            // 初始化通道
            DataTransfer transfer = new DataTransfer();
            if (!initZongxianPath(transfer)) {
                return false;
            }
            List<SubmitTarget> targets = new ArrayList<SubmitTarget>();
            if (XZSPBIZ.UNIT_ID_TYDJ.equals(XZSPBIZ.UNIT_ID_CENTER)) {
                if (sunsharingType.equals(sunsharingTypeFormal)) {
                    targets.add(new SubmitTarget("JM", "QJG"));
                } else {
                    targets.add(new SubmitTarget("JM", "QJG2"));
                }
            } else {// 集美
                if (toId.equals(GHJ_UNITID)) {// 规划局
                    targets.add(new SubmitTarget("GHJ", "SPXT"));
                } else if (toId.contains(XZSPBIZ.UNIT_ID_CENTER)) {
                    if (sunsharingType.equals(sunsharingTypeFormal)) {
                        targets.add(new SubmitTarget("XZFWZX", "JSXMSP"));
                    } else {
                        targets.add(new SubmitTarget("XZFWZX", "JSXMSP_CS"));
                    }
                } else {
                    if (sunsharingType.equals(sunsharingTypeFormal)) {
                        targets.add(new SubmitTarget("XZFWZX", "JSXMSP"));
                    } else {
                        targets.add(new SubmitTarget("XZFWZX", "JSXMSP_CS"));
                    }
                }
            }
            SubmitResult result = transfer.submit(targets, baseInfo, attachments);
            if (!result.submitted) {
                // 提交到平台失败，出错处理
                // 出错时一般是再次发送
                logger.info("第一次调用畅享transfer.submit方法出错，错误信息：》》》》》》》》》》》》》》》》》" + result.msg
                        + ";出错的taskId为：》》》》》》》》》》》》" + result.taskId);
                int submitSize = 10;
                for (int i = 0; i < submitSize; i++) {
                    SubmitResult secondSubmitResult = transfer.submit(targets, baseInfo,
                            attachments);
                    if (!secondSubmitResult.submitted) {
                        logger.info(
                                "第一次调用出错后再次发送，第" + (i + 2) + "次调用畅享transfer.submit方法出错，错误信息：》》》》》》》》》》》》》》》》》"
                                        + secondSubmitResult.msg + ";出错的taskId为：》》》》》》》》》》》》" + secondSubmitResult.taskId);
                    } else {
                        System.out.println("数据成功提交到业务协同平台，任务号：" + secondSubmitResult.taskId);
                        return true;
                    }
                }
                return false;
            } else {
                System.out.println("数据成功提交到业务协同平台，任务号：" + result.taskId);
                return true;
            }
        } catch (Exception e) {
            logger.info(
                    "************反馈机制写入光盾发送数据异常,数据dataId[" + dataId + "],目标方id[" + toId + "]************");
            logger.error(e.getMessage(), e);
            return false;
        }

    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.service.ZongXianExchangeService#repeatDateJob()
     */
    @Override
    public void repeatDateJob() {
        StringBuffer hql = new StringBuffer();
        hql.append(" from GuangDunSend s where s.isBack=0 ");
        hql.append(
                " and (to_date(to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'),'yyyy-mm-dd hh24:mi:ss') -to_date(to_char(decode(s.preSendDate,null,sysdate,s.preSendDate),'yyyy-mm-dd hh24:mi:ss'),'yyyy-mm-dd hh24:mi:ss')) * 24 * 60  >= :repeatTime");
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("repeatTime", REPEAT_TIME);
        List<GuangDunSend> guangDunSends = this.dao.query(hql.toString(), paramMap,
                GuangDunSend.class);
        if (guangDunSends != null && !guangDunSends.isEmpty()) {
            for (GuangDunSend gds : guangDunSends) {
                String dataId = gds.getDataId();
                try {
                    byte[] subjectByte = {};
                    Map<String, Object> subject = new HashMap<String, Object>();
                    byte[] baseInfo = blobToBytes(gds.getSubject());
                    subject.put("data", SerializableUtil.ByteToObject(baseInfo));
                    subject.put("dataId", dataId);
                    subject.put("preDataId", gds.getPreDataId());
                    subject.put("fromId", XZSPBIZ.UNIT_ID_TYDJ);
                    subjectByte = SerializableUtil.ObjectToByte(subject);

                    // 获取发送附件信息
                    List<Attachment> attachments = getGuangDunFiles(dataId);
                    // 调用光盾发送数据
                    submit(gds.getToId(), subjectByte, attachments, dataId, true);
                } catch (Exception e) {
                    logger.info(
                            "************重发数据：调用光盾发送数据异常,表[is_guangdun_send]数据dataId[" + dataId + "],目标方id["
                                    + gds.getToId() + "]************");
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }

}
