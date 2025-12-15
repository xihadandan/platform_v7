package com.wellsoft.pt.integration.service.impl;

import com.wellsoft.context.config.Config;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.datadict.dto.CdDataDictionaryItemDto;
import com.wellsoft.pt.basicdata.facade.service.BasicDataApiFacade;
import com.wellsoft.pt.basicdata.workhour.enums.WorkUnit;
import com.wellsoft.pt.basicdata.workhour.support.WorkPeriod;
import com.wellsoft.pt.bm.service.AdvisoryComplaintsService;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.integration.bean.ExchangeDataBean;
import com.wellsoft.pt.integration.bean.ExchangeDataDetailBean;
import com.wellsoft.pt.integration.bean.ExchangeDataMonitorBean;
import com.wellsoft.pt.integration.entity.*;
import com.wellsoft.pt.integration.request.CancelRequest;
import com.wellsoft.pt.integration.request.ReplyRequest;
import com.wellsoft.pt.integration.response.ReplyResponse;
import com.wellsoft.pt.integration.security.ExchangeConfig;
import com.wellsoft.pt.integration.service.*;
import com.wellsoft.pt.integration.support.ExchangeDataResultTransformer;
import com.wellsoft.pt.integration.support.zipfile.ZipFileConvertUtils;
import com.wellsoft.pt.integration.support.zipfile.ZipFileHandler;
import com.wellsoft.pt.integration.support.zipfile.ZipFileHandlerFactory;
import com.wellsoft.pt.jpa.dao.UniversalDao;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.multi.org.facade.service.OrgApiFacade;
import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;
import com.wellsoft.pt.repository.service.MongoFileService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.unit.entity.CommonUnit;
import com.wellsoft.pt.unit.facade.service.UnitApiFacade;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.tools.zip.ZipFile;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Description: 用户收发件界面业务类
 *
 * @author Administrator
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-11-17.1	wbx		2013-11-17		Create
 * </pre>
 * @date 2013-11-17
 */
@Service
public class ExchangeDataClientServiceImpl implements ExchangeDataClientService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UnitApiFacade unitApiFacade;

    @Autowired
    private ExchangeDataService exchangeDataService;

    @Autowired
    private ExchangeDataFlowService exchangeDataFlowService;
    @Autowired
    private MongoFileService mongoFileService;
    @Autowired
    private ExchangeDataTypeService exchangeDataTypeService;
    @Autowired
    private BasicDataApiFacade basicDataApiFacade;
    @Autowired
    private ExchangeDataFileUploadService exchangeDataFileUploadService;
    @Autowired
    private OrgApiFacade orgApiFacade;
    @Autowired
    private DyFormFacade dyFormApiFacade;
    @Autowired
    private ExchangeDataSendMonitorService exchangeDataSendMonitorService;
    @Autowired
    private ExchangeDataMonitorService exchangeDataMonitorService;
    @Autowired
    private AdvisoryComplaintsService advisoryComplaintsService;
    @Autowired
    private DelDataLogService delDataLogService;

    @Autowired
    private UniversalDao dao;

    /**
     * 保存并发送数据
     *
     * @see com.wellsoft.pt.integration.service.ExchangeDataClientService#saveAndSendData(java.lang.Object,
     * <p>
     * com.wellsoft.pt.integration.entity.ExchangeData)
     */
    @Override
    @Transactional
    public String saveAndSendData(ExchangeDataBean bean) {
        DyFormData formData = bean.getDyFormData();
        if (bean.getRel() == 27) {// 重发

            String reservedText2Value = formData.getFieldValueByMappingName("exchangeData_reservedText2") == null ? ""
                    : formData.getFieldValueByMappingName("exchangeData_reservedText2").toString();
            if (reservedText2Value.indexOf(":") > -1) {
                reservedText2Value = dyFormApiFacade.getDisplayValue(reservedText2Value);
            }
            String reservedText1Value = formData.getFieldValueByMappingName("exchangeData_reservedText1") == null ? ""
                    : formData.getFieldValueByMappingName("exchangeData_reservedText1").toString();
            ExchangeDataSendMonitor edsm = exchangeDataSendMonitorService.getOne(bean.getSendMonitorUuid());
            ExchangeData exchangeData = edsm.getExchangeData();
            exchangeData.getExchangeDataBatch().setToId(bean.getToId());
            exchangeData.getExchangeDataBatch().setCc(bean.getCc());
            exchangeData.getExchangeDataBatch().setBcc(bean.getBcc());
            if (!StringUtils.isBlank(reservedText1Value)) {
                exchangeData.setReservedText1(reservedText1Value);
            }
            if (!StringUtils.isBlank(reservedText2Value)) {
                exchangeData.setReservedText2(reservedText2Value);
            }
            edsm.setSendNode("examineIng");
            exchangeDataService.save(exchangeData);
            exchangeDataSendMonitorService.save(edsm);
            dyFormApiFacade.saveFormData(formData);
            return "success";
        } else {
            return null;
        }
    }

    public Map getSendDataValue(String dataId, Integer recVer) {
        Map map = new HashMap();
        ExchangeData exchangeData = exchangeDataService.getExchangeDataByDataId(dataId, recVer);
        ExchangeDataType dataType = exchangeDataTypeService
                .getByTypeId(exchangeData.getExchangeDataBatch().getTypeId());
        map.put("formId", dataType.getFormId());
        map.put("typeName", dataType.getName());
        map.put("typeId", dataType.getId());
        map.put("dataUuid", exchangeData.getFormDataId());
        map.put("from", exchangeData.getExchangeDataBatch().getFromId());
        map.put("fromName", getUnitName(exchangeData.getExchangeDataBatch().getFromId()));

        List<CommonUnit> unitList = unitApiFacade.getCommonUnitsByBusinessTypeIdAndUserId(
                ExchangeConfig.EXCHANGE_BUSINESS_TYPE, SpringSecurityUtils.getCurrentUserId());
        if (unitList != null && unitList.size() > 0) {
            ExchangeDataMonitor monitor = exchangeDataMonitorService.getObjByUnitAndDataId(unitList.get(0).getId(),
                    dataId, recVer);
            if (monitor != null) {
                String formUuid = monitor.getFormId();
                String dataUuid = monitor.getFormDataUuid();
                DyFormData formData = dyFormApiFacade.getDyFormData(formUuid, dataUuid);
                map.put("ZCH", formData.getFieldValue(ExchangeConfig.EXCHANGE_ZCH));
                map.put("ZTMC", formData.getFieldValue(ExchangeConfig.EXCHANGE_ZTMC));
                map.put("FDDBR", formData.getFieldValue(ExchangeConfig.EXCHANGE_FDDBR));
                map.put("ZTLX", formData.getFieldValue(ExchangeConfig.EXCHANGE_ZTLX));
                // map.put("JYCS",
                // dytableApiFacade.getSubFormFieldValue(formUuid, dataUuid,
                // ExchangeConfig.EXCHANGE_JYCS));

            }
        }
        return map;
    }

    @Override
    public Map getSendDataValueByUuid(String uuid) {
        ExchangeDataSendMonitor edsm = exchangeDataSendMonitorService.getOne(uuid);
        ExchangeData exchangeData = edsm.getExchangeData();
        ExchangeDataType dataType = exchangeDataTypeService
                .getByTypeId(exchangeData.getExchangeDataBatch().getTypeId());
        Map map = new HashMap();
        map.put("dataUuid", exchangeData.getFormDataId());
        map.put("toId", exchangeData.getExchangeDataBatch().getToId());
        map.put("toNames", this.getUnitNames(exchangeData.getExchangeDataBatch().getToId()));
        map.put("cc", exchangeData.getExchangeDataBatch().getCc());
        map.put("ccNames", this.getUnitNames(exchangeData.getExchangeDataBatch().getCc()));
        map.put("bcc", exchangeData.getExchangeDataBatch().getBcc());
        map.put("bccNames", this.getUnitNames(exchangeData.getExchangeDataBatch().getBcc()));
        map.put("formId", dataType.getFormId());
        map.put("typeName", dataType.getName());
        map.put("typeId", dataType.getId());
        return map;
    }

    /**
     * 获取ExchangeData
     *
     * @see com.wellsoft.pt.integration.service.ExchangeDataClientService#getExchangeDataByUUid(java.lang.String)
     */
    @Override
    public ExchangeData getExchangeDataByUUid(String uuid) {
        return exchangeDataService.getOne(uuid);
    }

    @Override
    public ExchangeDataMonitor getExchangeDataMonitorByCorrelationandUnitId(String correlationId,
                                                                            String correlationRecVer, String unitId) {
        return exchangeDataService.getExchangeDataMonitorByCorrelationandUnitId(correlationId,
                Integer.parseInt(correlationRecVer), unitId);
    }

    @Override
    public ExchangeDataMonitor getExchangeDataMonitor(String EchangeDataUuid, String unitId) {

        String hql = "from ExchangeDataMonitor edm where edm.exchangeData.uuid='" + EchangeDataUuid
                + "' and edm.unitId='" + unitId + "'";
        List<ExchangeDataMonitor> exchangeDataMonitors = exchangeDataMonitorService.listByHQL(hql, null);
        if (exchangeDataMonitors.size() == 0) {
            return null;
        } else {
            return exchangeDataMonitors.get(0);
        }
    }

    @Override
    public String signData(String uuid) {
        ExchangeDataMonitor edm = exchangeDataMonitorService.getOne(uuid);
        ExchangeDataSendMonitor edsm = edm.getExchangeDataSendMonitor();
        ExchangeData exchangeData = edsm.getExchangeData();
        List<CommonUnit> units = unitApiFacade.getCommonUnitsByBusinessTypeIdAndUserId(
                ExchangeConfig.EXCHANGE_BUSINESS_TYPE, SpringSecurityUtils.getCurrentUserId());
        if (units.isEmpty()) {
            return "fail";
        }

        ReplyRequest message = new ReplyRequest();
        message.setCode(1);// 1代表签收
        message.setDataId(exchangeData.getDataId());
        message.setMatterId(edm.getMatterId());
        message.setMsg("已签收");
        message.setRecVer(exchangeData.getDataRecVer());
        message.setReplyTime(new Date());
        message.setUnitId(units.get(0).getId());
        ReplyResponse response = exchangeDataFlowService.reply(message);
        if (response.getCode() == 1) {
            return "success";
        }
        return "fail";
    }

    @Override
    public String refuseData(String uuid, String msg, String type) {
        if ("send".equals(type)) {
            ExchangeDataSendMonitor edsm = exchangeDataSendMonitorService.getOne(uuid);
            String matterId = "";
            for (ExchangeDataMonitor edm : edsm.getExchangeDataMonitors()) {
                matterId = edm.getMatterId();
                if (!"".equals(matterId)) {
                    break;
                }
            }
            ExchangeData exchangeData = edsm.getExchangeData();
            List<CommonUnit> units = unitApiFacade.getCommonUnitsByBusinessTypeIdAndUserId(
                    ExchangeConfig.EXCHANGE_BUSINESS_TYPE, SpringSecurityUtils.getCurrentUserId());
            if (units.isEmpty()) {
                return "fail";
            }
            ReplyRequest message = new ReplyRequest();
            message.setCode(-1);// 0代表拒收
            message.setDataId(exchangeData.getDataId());
            message.setMsg("已拒收");
            message.setMatterId(matterId);
            message.setRecVer(exchangeData.getDataRecVer());
            message.setReplyTime(new Date());
            message.setUnitId(units.get(0).getId());
            message.setMsg(msg);
            ReplyResponse response = exchangeDataFlowService.reply(message);
            if (response != null && response.getMsg() != null) {
                return "success";
            }
            return "fail";
        } else if ("receive".equals(type)) {
            ExchangeDataMonitor edm = exchangeDataMonitorService.getOne(uuid);
            ExchangeData exchangeData = edm.getExchangeDataSendMonitor().getExchangeData();
            List<CommonUnit> units = unitApiFacade.getCommonUnitsByBusinessTypeIdAndUserId(
                    ExchangeConfig.EXCHANGE_BUSINESS_TYPE, SpringSecurityUtils.getCurrentUserId());
            if (units.isEmpty()) {
                return "fail";
            }
            ReplyRequest message = new ReplyRequest();
            message.setCode(-1);// 0代表拒收
            message.setDataId(exchangeData.getDataId());
            message.setMsg("已拒收");
            message.setMatterId(edm.getMatterId());
            message.setRecVer(exchangeData.getDataRecVer());
            message.setReplyTime(new Date());
            message.setUnitId(units.get(0).getId());
            message.setMsg(msg);
            ReplyResponse response = exchangeDataFlowService.reply(message);
            if (response != null && response.getMsg() != null) {
                return "success";
            }
            return "fail";
        }
        return "fail";
    }

    /**
     * 撤回
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.service.ExchangeDataClientService#cancel(com.wellsoft.pt.integration.bean.ExchangeDataBean)
     */
    @Override
    public String cancel(Integer rel, String dataId, Integer recVer, String unitIds, String msg, String fromId,
                         String matterIds) {
        // TODO Auto-generated method stub
        String[] unitId = unitIds.split(";");
        String[] matterId = matterIds.split(";");
        for (int i = 0; i < unitId.length; i++) {
            CancelRequest request = new CancelRequest();
            request.setDataId(dataId);
            request.setRecVer(recVer);
            request.setMsg(msg);
            request.setUnitId(unitId[i]);
            request.setFromId(fromId);
            if ("null".equals(matterId[i])) {
                request.setMatterId("");
            } else {
                request.setMatterId(matterId[i]);
            }
            exchangeDataFlowService.cancel(request);
        }
        return "注销成功";
    }

    /**
     * 补发
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.service.ExchangeDataClientService#reapply(java.lang.Integer, java.lang.String, java.lang.Integer)
     */
    @Override
    public String reapply(Integer rel, String sendMonitorUuid, String unitIds) {
        // TODO Auto-generated method stub
        String str = exchangeDataFlowService.reapply(24, sendMonitorUuid, unitIds);
        return str;
    }

    /**
     * 获取到详细页所需数据
     *
     * @see com.wellsoft.pt.integration.service.ExchangeDataClientService#getDetailDataByExDataUuid(java.lang.String)
     */
    @Override
    public ExchangeDataDetailBean getSendDetailByExDataUuid(String uuid) {

        ExchangeDataDetailBean bean = new ExchangeDataDetailBean();
        ExchangeDataSendMonitor edsm = exchangeDataSendMonitorService.getOne(uuid);
        ExchangeData exchangeData = edsm.getExchangeData();
        ExchangeDataBatch batch = exchangeData.getExchangeDataBatch();
        // 动态表单form_id 通过类型id获取
        ExchangeDataType dataType = exchangeDataTypeService.getByTypeId(batch.getTypeId());
        Set<ExchangeDataMonitor> monitors = edsm.getExchangeDataMonitors();
        Iterator i = monitors.iterator();
        // 1许可过程 2 待收 3 已收 4退回 5发件 7上传监察 8接收监察
        String allUnits = this.getMonitorsAllUnit(uuid);
        if (StringUtils.isNotBlank(allUnits)) {
            List<CommonUnit> allUnitList = unitApiFacade.getCommonUnitListByIds(allUnits);
            bean.setAllUnit(this.idList2names(allUnitList));
        }
        if (StringUtils.isNotBlank(batch.getToId())) {
            List<CommonUnit> toList = unitApiFacade.getCommonUnitListByIds(batch.getToId());
            bean.setTo(this.idList2names(toList));
        }
        if (StringUtils.isNotBlank(batch.getCc())) {
            List<CommonUnit> ccList = unitApiFacade.getCommonUnitListByIds(batch.getCc());
            bean.setCc(this.idList2names(ccList));
        }
        if (StringUtils.isNotBlank(batch.getBcc())) {
            List<CommonUnit> bccList = unitApiFacade.getCommonUnitListByIds(batch.getBcc());
            bean.setBcc(this.idList2names(bccList));
        }
        if (monitors != null) {
            List<ExchangeDataMonitorBean> mointorList = new ArrayList<ExchangeDataMonitorBean>();
            while (i.hasNext()) {
                ExchangeDataMonitor e = (ExchangeDataMonitor) i.next();
                ExchangeDataMonitorBean mBean = new ExchangeDataMonitorBean();
                BeanUtils.copyProperties(e, mBean);
                mBean.setCreateTime(e.getCreateTime());
                mBean.setUnitName(getUnitName(mBean.getUnitId()));
                mBean.setFromUnitName(getUnitName(edsm.getFromId()));
                if (StringUtils.isNotBlank(mBean.getReplyUser())) {
                    mBean.setReplyUser(orgApiFacade.getUserNameById(mBean.getReplyUser()));
                } else {
                    mBean.setReplyUser("");
                }
                mointorList.add(mBean);
            }
            bean.setExchangeDataMonitors(mointorList);
        }

        bean.setDataUuid(exchangeData.getFormDataId());
        bean.setFormId(dataType.getFormId());

        bean.setFromId(edsm.getFromId());
        bean.setDrafter(orgApiFacade.getUserNameById(exchangeData.getDrafter()));
        bean.setDraftTime(exchangeData.getDraftTime());
        if (!StringUtils.isBlank(exchangeData.getReleaser())) {
            bean.setReleaser(orgApiFacade.getUserNameById(exchangeData.getReleaser()));
            bean.setReleaseTime(exchangeData.getReleaseTime());
        }
        bean.setFromUnitName(getUnitName(edsm.getFromId()));
        bean.setShowToUnit(dataType.getShowToUnit());
        bean.setTypeId(dataType.getId());
        bean.setTypeName(dataType.getName());
        bean.setDataId(exchangeData.getDataId());
        bean.setRecVer(exchangeData.getDataRecVer());
        bean.setSendNode(edsm.getSendNode());
        bean.setSendLimitNum(edsm.getSendLimitNum());// 逾期天数（首次上报还是发送过程的逾期）
        String userName = orgApiFacade.getUserNameById(edsm.getSendUser());
        if (!StringUtils.isBlank(userName)) {
            userName = "(" + userName + ")";
        } else {
            userName = "";
        }
        bean.setFromUserName(userName);
        String title = "";
        String ztmc = exchangeData.getReservedText1(); // 主体
        ExchangeDataType type = exchangeDataTypeService.getByTypeId(batch.getTypeId());
        title = type.getName();
        if (StringUtils.isNotBlank(ztmc)) {
            title = ztmc + "(" + title + ")";
        }
        bean.setTitle(title);
        // 获取可撤销的用户
        String cancelUnits = "";
        List<Map<String, Object>> cancelMsg = exchangeDataFlowService.cancelMsg(uuid);
        for (Map<String, Object> c : cancelMsg) {
            if ((Integer) c.get("status") == 1) {
                cancelUnits += ";" + c.get("unitId") + ":" + c.get("unitName") + ":" + c.get("matterId");
            }
        }
        bean.setCancelUnits(cancelUnits.replaceFirst(";", ""));
        bean.setExamineFailMsg(exchangeData.getExamineFailMsg());
        bean.setSendType(edsm.getSendType());
        return bean;
    }

    private String idList2names(List<CommonUnit> list) {
        String all = "";
        for (CommonUnit c : list) {
            if (c != null) {
                all += ";" + c.getName();
            }
        }
        return all.replaceFirst(";", "");
    }

    @Override
    public ExchangeDataType getExchangeTypeByDataUuid(String uuid) {
        ExchangeData exchangeData = exchangeDataService.getOne(uuid);
        return exchangeDataTypeService.getByTypeId(exchangeData.getExchangeDataBatch().getTypeId());
    }

    @Override
    public String getSendMonitorByZch(String zch) {
        String hql = "select s.uuid from ExchangeDataSendMonitor s left join s.exchangeData d left join d.exchangeDataBatch b "
                + "where d. reservedNumber2= :zch and b.typeId=:typeId and b.fromId = s.fromId and s.sendType='first'";
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("zch", zch);
        values.put("typeId", ExchangeConfig.TYPE_ID_SSXX_ZTDJ);
        List<String> smList = exchangeDataSendMonitorService.getUuidsByHQL(hql, values);
        if (smList != null && smList.size() > 0) {
            return smList.get(0);
        } else {
            return "";
        }
    }

    @Override
    public ExchangeDataType getExchangeDataTypeById(String id) {
        return exchangeDataTypeService.getByTypeId(id);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.service.ExchangeDataClientService#uploadFile(java.util.List)
     */
    @Override
    public void uploadFile(List<Map<String, Object>> files) {
        if (files.isEmpty()) {
            throw new RuntimeException("请先上传rar/zip文件!");
        }

        Date uploadTime = Calendar.getInstance().getTime();
        for (int i = 0; i < files.size(); i++) {
            ExchangeDataFileUpload fileUpload = getFileUpload(files.get(i));
            String fileId = fileUpload.getFileId();
            String fileName = fileUpload.getFileName();
            String contentType = fileUpload.getContentType();
            MongoFileEntity mf = mongoFileService.getFile(fileId);
            mongoFileService.pushFileToFolder("exchangeDataUploadByBatch", fileId, "");
            InputStream is = mf.getInputstream();
            File zipFolder = new File(Config.APP_DATA_DIR + "/zip");
            if (!zipFolder.exists()) {
                zipFolder.mkdirs();
            }
            try {
                File zipTempFile = new File(zipFolder, fileId + "_" + fileName);
                FileOutputStream fos = new FileOutputStream(zipTempFile);
                IOUtils.copy(is, fos);
                IOUtils.closeQuietly(fos);
                if (!"application/zip".equals(contentType) && !"application/x-zip-compressed".equals(contentType)) {
                    zipTempFile = ZipFileConvertUtils.convertRar(zipTempFile);
                }
                ZipFile zipFile = new ZipFile(zipTempFile);
                ZipFileHandler handler = ZipFileHandlerFactory.getZipFileHandler(zipFile);
                Map<String, String> result = handler.handler(zipFile);

                // 保存上传日志
                fileUpload.setUploadTime(uploadTime);
                fileUpload.setTypeId(result.get("typeId"));
                fileUpload.setTypeName(result.get("typeName"));
                fileUpload.setBatchId(result.get("batchId"));
                fileUpload.setUnitId(result.get("unitId"));
                fileUpload.setUnitName(result.get("unitName"));
                fileUpload.setBusinessTypeId(ExchangeConfig.EXCHANGE_BUSINESS_TYPE);
                fileUpload.setBusinessTypeName("商事管理");
                exchangeDataFileUploadService.save(fileUpload);
            } catch (Exception e) {
                logger.error(e.getMessage());
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    /**
     * @param map
     * @return
     */
    private ExchangeDataFileUpload getFileUpload(Map<String, Object> map) {
        String userId = map.get("userId") == null ? "" : map.get("userId").toString();
        String userName = map.get("userName") == null ? "" : map.get("userName").toString();
        String departmentId = map.get("departmentId") == null ? "" : map.get("departmentId").toString();
        String departmentName = map.get("departmentName") == null ? "" : map.get("departmentName").toString();
        String businessTypeId = map.get("businessTypeId") == null ? "" : map.get("businessTypeId").toString();
        String businessTypeName = map.get("businessTypeName") == null ? "" : map.get("businessTypeName").toString();
        String fileId = map.get("fileID") == null ? "" : map.get("fileID").toString();
        String fileName = map.get("fileName") == null ? "" : map.get("fileName").toString();
        String contentType = map.get("contentType") == null ? "" : map.get("contentType").toString();
        long fileSize = map.get("fileSize") == null ? 0l : Long.valueOf(map.get("fileSize").toString());
        boolean signUploadFile = map.get("signUploadFile") == null ? false : (Boolean) map.get("signUploadFile");
        String digestValue = map.get("digestValue") == null ? "" : map.get("digestValue").toString();
        String digestAlgorithm = map.get("digestAlgorithm") == null ? "" : map.get("digestAlgorithm").toString();
        String certificate = map.get("certificate") == null ? "" : map.get("certificate").toString();
        String signatureValue = map.get("signatureValue") == null ? "" : map.get("signatureValue").toString();

        ExchangeDataFileUpload fileUpload = new ExchangeDataFileUpload();
        fileUpload.setUserId(userId);
        fileUpload.setUserName(userName);
        fileUpload.setDepartmentId(departmentId);
        fileUpload.setDepartmentName(departmentName);
        fileUpload.setBusinessTypeId(businessTypeId);
        fileUpload.setBusinessTypeName(businessTypeName);
        fileUpload.setFileId(fileId);
        fileUpload.setFileName(fileName);
        fileUpload.setContentType(contentType);
        fileUpload.setFileSize(fileSize);
        fileUpload.setSignUploadFile(signUploadFile);
        fileUpload.setDigestValue(digestValue);
        fileUpload.setDigestAlgorithm(digestAlgorithm);
        fileUpload.setCertificate(certificate);
        fileUpload.setSignatureValue(signatureValue);

        return fileUpload;
    }

    public Map getExchangeObjByDataUUid(String uuid) {
        ExchangeData ed = exchangeDataService.getOne(uuid);
        Map map = new HashMap();
        map.put("dataId", ed.getDataId());
        map.put("batchId", ed.getExchangeDataBatch().getId());
        return map;
    }

    /**
     * (non-Javadoc)
     *
     * @throws Exception
     * @see com.wellsoft.pt.integration.service.ExchangeDataClientService#generateZipFile(java.util.Collection)
     */
    @Override
    public File generateZipFile(Collection<String> uuids) throws Exception {
        return null;
    }

    /**
     * @param file
     * @throws FileNotFoundException
     */
    private void sendExcelRequest(File file) throws Exception {
        return;
    }

    /**
     * @param file
     * @return
     * @throws Exception
     */
    private List<Map<String, Object>> readExcelFile(File file) throws Exception {
        return null;
    }

    @Override
    public String reapeat(Integer rel, String uuid, String unitIds) {
        return exchangeDataFlowService.reapeat(rel, uuid, unitIds);
    }

    @Override
    @Transactional(readOnly = true)
    public List getSSDJBDetailDate(String zch) {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        // 数据集合
        List<Map> resoult = new ArrayList();
        // 商事主体上报集合
        String firsthql = "from ExchangeData e where e.reservedNumber2 = :zch and e.exchangeDataBatch.typeId = :typeId order by e.createTime";
        Map<String, Object> firsthqlMap = new HashMap<String, Object>();
        firsthqlMap.put("zch", zch);
        firsthqlMap.put("typeId", ExchangeConfig.TYPE_ID_SSXX_ZTDJ);
        List<ExchangeData> ssExchangeDatas = exchangeDataService.listByHQL(firsthql, firsthqlMap);
        if (ssExchangeDatas.size() == 0) {
            return new ArrayList();
        }
        ExchangeData firstExchangeData = ssExchangeDatas.get(0);
        String firstUuid = firstExchangeData.getUuid();

        String czUuids = "";// 记录出证的数据uuid
        /*******************第一条商事登记********************/
        Map<String, Object> firstmap = new HashMap<String, Object>();
        // 节点信息
        List<List<Map<String, Object>>> firstlist = new ArrayList<List<Map<String, Object>>>();

        ExchangeDataType exchangeDataType = exchangeDataTypeService.getByTypeId(firstExchangeData
                .getExchangeDataBatch().getTypeId());

        int maxDept = 0;
        // 第一次商事主体上报
        List<Map<String, Object>> list1 = new ArrayList<Map<String, Object>>();
        Map<String, Object> map1 = new HashMap<String, Object>();// 企业登记
        map1.put("time", firstExchangeData.getReservedText3());
        map1.put("task", "主体登记");
        map1.put("code", 1);
        map1.put("index", 1);
        map1.put("isFirst", true);

        List<Map<String, Object>> list2 = new ArrayList<Map<String, Object>>();
        Map<String, Object> map2 = new HashMap<String, Object>();// 工商上传
        map2.put("time", format.format(firstExchangeData.getCreateTime()));
        map2.put("task", "工商上传");
        map2.put("code", 2);
        map2.put("url", request.getContextPath() + "/exchangedata/client/toExchangeDetailPage?UUID="
                + firstExchangeData.getUuid());
        map2.put("limitNum", firstExchangeData.getUploadLimitNum());
        map2.put("recVer", "版本:" + firstExchangeData.getDataRecVer());
        map2.put("index", 1);
        map2.put("isFirst", false);

        List<Map<String, Object>> list3 = new ArrayList<Map<String, Object>>();// 送达
        List<Map<String, Object>> list4 = new ArrayList<Map<String, Object>>();// 签收
        List<Map<String, Object>> list5 = new ArrayList<Map<String, Object>>();// 出证
        List<Map<String, Object>> list6 = new ArrayList<Map<String, Object>>();// 出证上传登记
        List<Map<String, Object>> list7 = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> list8 = new ArrayList<Map<String, Object>>();
        // 找出所有的接收单位
        String hql1 = "select m.uuid from ExchangeDataMonitor m "
                + "where m.exchangeDataSendMonitor.exchangeData.reservedNumber2 = :zch "
                + "and m.exchangeDataSendMonitor.exchangeData.exchangeDataBatch.typeId = :typeId "
                + "and m.exchangeDataSendMonitor.exchangeData.exchangeDataBatch.fromId = m.exchangeDataSendMonitor.fromId "
                + "order by m.createTime";
        Map<String, Object> hqlMap1 = new HashMap<String, Object>();
        hqlMap1.put("zch", zch);
        hqlMap1.put("typeId", ExchangeConfig.TYPE_ID_SSXX_ZTDJ);
        List<String> monitorUuids = exchangeDataMonitorService.getUuidsByHQL(hql1, hqlMap1);

        map1.put("childNum", monitorUuids.size());
        map2.put("childNum", monitorUuids.size());
        list1.add(map1);
        list2.add(map2);
        for (String monitorUuid : monitorUuids) {
            ExchangeDataMonitor em = exchangeDataMonitorService.getOne(monitorUuid);
            String unitShortName = getUnitShortName(em.getUnitId());
            Map<String, Object> map3 = new HashMap<String, Object>();// 送达局
            if (em.getReceiveTime() != null) {
                map3.put("time", format.format(em.getReceiveTime()));
            }
            if (em.getCancelStatus() != null && em.getCancelStatus().equals("success")) {
                map3.put("cancel", format.format(em.getCancelTime()));
            }
            if (em.getReceiveStatus() != null && em.getReceiveStatus().equals("success")) {
                map3.put("task", "送达" + unitShortName);
            } else if (em.getReceiveStatus() != null && em.getReceiveStatus().equals("fail")) {
                map3.put("task", "送达" + unitShortName);
            } else {
                map3.put("task", "未送达");
            }
            map3.put("matter", em.getMatter());
            map3.put("task", "送达" + unitShortName);// 未送达单位系统，却能先签收的处理，已发出去即是送达
            map3.put("code", 3);
            map3.put("index", 2);
            map3.put("isFirst", true);

            Map<String, Object> map4 = new HashMap<String, Object>();
            if (em.getReplyTime() != null) {
                map4.put("time", format.format(em.getReplyTime()));
            }
            if (em.getReplyStatus() != null && em.getReplyStatus().equals("success")) {
                map4.put("task", "签收");
                map4.put("limitNum", em.getReplyLimitNum());
            } else if (em.getReplyStatus() != null && em.getReplyStatus().equals("fail")) {
                map4.put("task", "退回");
                map4.put("limitNum", em.getReplyLimitNum());
            } else {
                map4.put("task", "待签收");
            }
            if (StringUtils.isNotBlank(em.getReplyUser())) {
                map4.put("userName", orgApiFacade.getUserNameById(em.getReplyUser()));
            }
            map4.put("code", 4);
            map4.put("index", 2);
            map4.put("isFirst", false);

            int distribution = 0;
            Set<ExchangeDataSendMonitor> exchangeDataSendMonitors = em.getExchangeDataSendMonitor().getExchangeData()
                    .getExchangeDataSendMonitors();
            for (ExchangeDataSendMonitor exchangeDataSendMonitor : exchangeDataSendMonitors) {
                if (exchangeDataSendMonitor.getFromId().equals(em.getUnitId())
                        && exchangeDataSendMonitor.getSendType().equals("distribution")) {
                    // 转发的数据
                    distribution = 1;

                    Set<ExchangeDataMonitor> zfExchangeDataMonitors = exchangeDataSendMonitor.getExchangeDataMonitors();
                    ExchangeData zfExchangeData = exchangeDataSendMonitor.getExchangeData();
                    map3.put("childNum", zfExchangeDataMonitors.size());
                    map4.put("childNum", zfExchangeDataMonitors.size());
                    list3.add(map3);
                    list4.add(map4);

                    Map<String, Object> map5 = new HashMap<String, Object>();
                    map5.put("index", 2);
                    map5.put("isFirst", false);
                    map5.put("childNum", zfExchangeDataMonitors.size());
                    Map<String, Object> map6 = new HashMap<String, Object>();
                    map6.put("index", 2);
                    map6.put("isFirst", false);
                    map6.put("childNum", zfExchangeDataMonitors.size());
                    Map<String, Object> map7 = new HashMap<String, Object>();
                    map7.put("index", 2);
                    map7.put("isFirst", false);
                    map7.put("childNum", zfExchangeDataMonitors.size());
                    Map<String, Object> map8 = new HashMap<String, Object>();
                    map8.put("index", 2);
                    map8.put("isFirst", false);
                    map8.put("childNum", zfExchangeDataMonitors.size());

                    List<Map<String, Object>> listChild5 = new ArrayList<Map<String, Object>>();
                    List<Map<String, Object>> listChild6 = new ArrayList<Map<String, Object>>();
                    List<Map<String, Object>> listChild7 = new ArrayList<Map<String, Object>>();
                    List<Map<String, Object>> listChild8 = new ArrayList<Map<String, Object>>();
                    maxDept = maxDept + zfExchangeDataMonitors.size() - 1;
                    for (ExchangeDataMonitor zfEm : zfExchangeDataMonitors) {

                        String unitShortName2 = getUnitShortName(zfEm.getUnitId());

                        Map<String, Object> mapChild5 = new HashMap<String, Object>();
                        Map<String, Object> mapChild6 = new HashMap<String, Object>();
                        Map<String, Object> mapChild7 = new HashMap<String, Object>();
                        Map<String, Object> mapChild8 = new HashMap<String, Object>();

                        mapChild5.put("time", format.format(exchangeDataSendMonitor.getCreateTime()));
                        if (zfEm.getCancelStatus() != null && zfEm.getCancelStatus().equals("success")) {
                            mapChild5.put("cancel", format.format(zfEm.getCancelTime()));
                        }
                        if (zfEm.getReceiveStatus() != null && zfEm.getReceiveStatus().equals("success")) {
                            mapChild5.put("task", "转发到" + unitShortName2 + "(送达)");
                        } else if (zfEm.getReceiveStatus() != null && zfEm.getReceiveStatus().equals("fail")) {
                            mapChild5.put("task", "转发到" + unitShortName2 + "(送达)");
                        } else {
                            mapChild5.put("task", "转发到" + unitShortName2 + "(未送达)");
                        }
                        mapChild5.put("code", 5);
                        mapChild5.put("isFirst", true);
                        listChild5.add(mapChild5);

                        if (zfEm.getReplyTime() != null) {
                            mapChild6.put("time", format.format(zfEm.getReplyTime()));
                        }
                        if (zfEm.getReplyStatus() != null && zfEm.getReplyStatus().equals("success")) {
                            mapChild6.put("task", "签收");
                            mapChild6.put("limitNum", zfEm.getReplyLimitNum());
                        } else if (zfEm.getReplyStatus() != null && zfEm.getReplyStatus().equals("fail")) {
                            mapChild6.put("task", "退回");
                            mapChild6.put("limitNum", zfEm.getReplyLimitNum());
                        } else {
                            mapChild6.put("task", "待签收");
                        }
                        if (StringUtils.isNotBlank(zfEm.getReplyUser())) {
                            mapChild6.put("userName", orgApiFacade.getUserNameById(zfEm.getReplyUser()));
                        }
                        mapChild6.put("code", 6);
                        listChild6.add(mapChild6);

                        String hqlChild = "from ExchangeData e where e.reservedNumber2 = :ZCH and e.exchangeDataBatch.fromId = :unitId and e.exchangeDataBatch.typeId = :typeId order by e.createTime desc";
                        Map<String, Object> hqlChildMap = new HashMap<String, Object>();
                        hqlChildMap.put("ZCH", firstExchangeData.getReservedNumber2());
                        hqlChildMap.put("unitId", zfEm.getUnitId());
                        hqlChildMap.put("typeId", ExchangeConfig.TYPE_ID_SSXX_XZXK);
                        List<ExchangeData> hfChildExchangeDatas = exchangeDataService.listByHQL(hqlChild, hqlChildMap);

                        if (hfChildExchangeDatas != null && hfChildExchangeDatas.size() > 0
                                && zfEm.getReplyStatus() != null && zfEm.getReplyStatus().equals("success")
                                && zfEm.getReceiveNode().equals("reply")) {

                            ExchangeData eChild = hfChildExchangeDatas.get(0);
                            czUuids += "," + eChild.getUuid();

                            mapChild7.put("time", eChild.getReservedText3());
                            mapChild7.put("task", "出证");
                            mapChild7.put("code", 7);
                            mapChild7.put("childNum", 1);
                            listChild7.add(mapChild7);

                            mapChild8.put("time", format.format(eChild.getCreateTime()));
                            mapChild8.put("task", "上传");
                            mapChild8.put("code", 8);
                            mapChild8.put("url", request.getContextPath()
                                    + "/exchangedata/client/toExchangeDetailPage?UUID=" + eChild.getUuid() + "&rel=7");
                            mapChild8.put("limitNum", eChild.getUploadLimitNum());
                            mapChild8.put("recVer", "版本:" + eChild.getDataRecVer());
                            listChild8.add(mapChild8);
                        } else {
                            mapChild7.put("task", "出证");
                            mapChild7.put("code", 7);
                            listChild7.add(mapChild7);

                            mapChild8.put("task", "上传");
                            mapChild8.put("code", 8);
                            listChild8.add(mapChild8);
                        }
                    }
                    map5.put("data", listChild5);
                    map6.put("data", listChild6);
                    map7.put("data", listChild7);
                    map8.put("data", listChild8);
                    list5.add(map5);
                    list6.add(map6);
                    list7.add(map7);
                    list8.add(map8);
                    break;
                }
            }

            if (distribution == 0) {// 回复
                map3.put("childNum", 1);
                map4.put("childNum", 1);
                list3.add(map3);
                list4.add(map4);
                String hql2 = "from ExchangeData e where e.reservedNumber2 = :ZCH and e.exchangeDataBatch.fromId = :unitId and e.exchangeDataBatch.typeId = :typeId order by e.createTime desc";
                Map<String, Object> hql2Map = new HashMap<String, Object>();
                hql2Map.put("ZCH", firstExchangeData.getReservedNumber2());
                hql2Map.put("unitId", em.getUnitId());
                hql2Map.put("typeId", ExchangeConfig.TYPE_ID_SSXX_XZXK);
                List<ExchangeData> hfExchangeDatas = exchangeDataService.listByHQL(hql2, hql2Map);
                if (hfExchangeDatas != null && hfExchangeDatas.size() > 0 && em.getReplyStatus() != null
                        && em.getReplyStatus().equals("success") && em.getReceiveNode().equals("reply")) {

                    ExchangeData e2 = hfExchangeDatas.get(0);
                    czUuids += "," + e2.getUuid();

                    Map<String, Object> map5 = new HashMap<String, Object>();
                    map5.put("time", e2.getReservedText3());
                    map5.put("task", "出证");
                    map5.put("code", 5);
                    map5.put("index", 2);
                    map5.put("isFirst", false);
                    map5.put("childNum", 1);
                    list5.add(map5);

                    Map<String, Object> map6 = new HashMap<String, Object>();
                    map6.put("time", format.format(e2.getCreateTime()));
                    map6.put("task", "上传");
                    map6.put("code", 6);
                    map6.put("url",
                            request.getContextPath() + "/exchangedata/client/toExchangeDetailPage?UUID=" + e2.getUuid()
                                    + "&rel=7");
                    map6.put("limitNum", e2.getUploadLimitNum());
                    map6.put("recVer", "版本:" + e2.getDataRecVer());
                    map6.put("index", 2);
                    map6.put("isFirst", false);
                    map6.put("childNum", 1);
                    list6.add(map6);
                } else {
                    Map<String, Object> map5 = new HashMap<String, Object>();
                    map5.put("task", "出证");
                    map5.put("code", 5);
                    map5.put("index", 2);
                    map5.put("isFirst", false);
                    map5.put("childNum", 1);
                    list5.add(map5);

                    Map<String, Object> map6 = new HashMap<String, Object>();
                    map6.put("task", "上传");
                    map6.put("code", 6);
                    map6.put("index", 2);
                    map6.put("isFirst", false);
                    map6.put("childNum", 1);
                    list6.add(map6);
                }
                Map<String, Object> map7 = new HashMap<String, Object>();
                map7.put("index", 2);
                map7.put("isFirst", false);
                map7.put("childNum", 1);
                list7.add(map7);
                Map<String, Object> map8 = new HashMap<String, Object>();
                map8.put("index", 2);
                map8.put("isFirst", false);
                map8.put("childNum", 1);
                list8.add(map8);
            }
        }
        firstlist.add(list1);
        firstlist.add(list2);
        firstlist.add(list3);
        firstlist.add(list4);
        firstlist.add(list5);
        firstlist.add(list6);
        firstlist.add(list7);
        firstlist.add(list8);

        maxDept += monitorUuids.size();
        firstmap.put("maxDept", maxDept);
        firstmap.put("type", exchangeDataType.getName());
        firstmap.put("data", firstlist);
        resoult.add(firstmap);
        /*******************其余商事登记********************/
        for (ExchangeData exchangeData : ssExchangeDatas) {
            Map<String, Object> map = new HashMap<String, Object>();
            // 节点信息
            List<List<Map<String, Object>>> list = new ArrayList<List<Map<String, Object>>>();

            ExchangeDataType exchangeDataType1 = exchangeDataTypeService.getByTypeId(exchangeData
                    .getExchangeDataBatch().getTypeId());
            map.put("type", exchangeDataType1.getName());
            map.put("recVer", "版本:" + exchangeData.getDataRecVer());
            if (!exchangeData.getUuid().equals(firstUuid)) {
                map.put("maxDept", 1);
                // 节点信息
                List<Map<String, Object>> list1s = new ArrayList<Map<String, Object>>();
                Map<String, Object> map1s = new HashMap<String, Object>();// 企业登记
                map1s.put("time", exchangeData.getReservedText3());
                map1s.put("task", "登记");
                map1s.put("code", 1);
                map1s.put("index", 1);
                map1s.put("isFirst", true);
                map1s.put("childNum", 1);
                list1s.add(map1s);

                List<Map<String, Object>> list2s = new ArrayList<Map<String, Object>>();
                Map<String, Object> map2s = new HashMap<String, Object>();// 工商上传
                map2s.put("time", format.format(exchangeData.getCreateTime()));
                map2s.put("task", "上传");
                map2s.put("limitNum", exchangeData.getUploadLimitNum());
                map2s.put("code", 2);
                map2s.put("index", 1);
                map2s.put("isFirst", false);
                map2s.put("childNum", 1);
                map2s.put("url", request.getContextPath() + "/exchangedata/client/toExchangeDetailPage?UUID="
                        + exchangeData.getUuid());
                map2s.put("recVer", "版本:" + exchangeData.getDataRecVer());
                list2s.add(map2s);

                list.add(list1s);
                list.add(list2s);

                map.put("data", list);
                resoult.add(map);
            }
        }
        /******************非商事主体上报集合********************/
        String hql = "from ExchangeData e where e.reservedNumber2 = :zch and e.exchangeDataBatch.typeId != :typeId order by e.exchangeDataBatch.typeId";
        Map<String, Object> hqlMap = new HashMap<String, Object>();
        hqlMap.put("zch", zch);
        hqlMap.put("typeId", ExchangeConfig.TYPE_ID_SSXX_ZTDJ);
        List<ExchangeData> exchangeDatas = exchangeDataService.listByHQL(hql, hqlMap);
        for (ExchangeData exchangeData : exchangeDatas) {
            // 一条数据
            Map<String, Object> map = new HashMap<String, Object>();
            // 节点信息
            List<List<Map<String, Object>>> list = new ArrayList<List<Map<String, Object>>>();
            ExchangeDataType exchangeDataType1 = exchangeDataTypeService.getByTypeId(exchangeData
                    .getExchangeDataBatch().getTypeId());
            map.put("type", exchangeDataType1.getName());
            map.put("recVer", "版本:" + exchangeData.getDataRecVer());
            if (czUuids.indexOf(exchangeData.getUuid()) < 0) {// 不是出证
                map.put("maxDept", 1);
                // 节点信息
                List<Map<String, Object>> list1s = new ArrayList<Map<String, Object>>();
                Map<String, Object> map1s = new HashMap<String, Object>();// 企业登记
                map1s.put("time", exchangeData.getReservedText3());
                map1s.put("task", "登记");
                map1s.put("code", 1);
                map1s.put("index", 1);
                map1s.put("isFirst", true);
                map1s.put("childNum", 1);
                list1s.add(map1s);

                List<Map<String, Object>> list2s = new ArrayList<Map<String, Object>>();
                Map<String, Object> map2s = new HashMap<String, Object>();// 工商上传
                map2s.put("time", format.format(exchangeData.getCreateTime()));
                map2s.put("task", "上传");
                map2s.put("limitNum", exchangeData.getUploadLimitNum());
                map2s.put("code", 2);
                map2s.put("index", 1);
                map2s.put("isFirst", false);
                map2s.put("childNum", 1);
                map2s.put("url", request.getContextPath() + "/exchangedata/client/toExchangeDetailPage?UUID="
                        + exchangeData.getUuid());
                map2s.put("recVer", "版本:" + exchangeData.getDataRecVer());
                list2s.add(map2s);

                list.add(list1s);
                list.add(list2s);

                map.put("data", list);
                resoult.add(map);
            }
        }
        return resoult;
    }

    /**
     * 通过数据Uuid获得上报记录
     *
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public ExchangeDataSendMonitor getFromEdsmByEdUuid(String exchangeDataUuid) {
        ExchangeData ed = exchangeDataService.getOne(exchangeDataUuid);
        ExchangeDataBatch edb = ed.getExchangeDataBatch();
        Set<ExchangeDataSendMonitor> edsms = ed.getExchangeDataSendMonitors();
        ExchangeDataSendMonitor exchangeDataSendMonitor = new ExchangeDataSendMonitor();
        for (ExchangeDataSendMonitor edsm : edsms) {
            if (edsm.getFromId().equals(edb.getFromId())) {
                exchangeDataSendMonitor = edsm;
            }
        }
        return exchangeDataSendMonitor;
    }

    @Override
    @Transactional(readOnly = true)
    public List getXZXKGCDetail(String ywlsh, String uuid) {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();

        // 数据集合
        List<Map> result = new ArrayList();
        // 一条数据
        Map<String, Object> map = new HashMap<String, Object>();

        Map<String, String> sourceTimeMap = new HashMap<String, String>();// 排序用
        // key/value
        // 时间/节点名
        Map<String, String> sourceUrlMap = new HashMap<String, String>(); // 存url
        List<String> dateStr = new ArrayList<String>(); // 排序用 存时间

        List<List<Map<String, Object>>> list = new ArrayList<List<Map<String, Object>>>();
        List<Map<String, Object>> list1 = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> list2 = new ArrayList<Map<String, Object>>();

        // 收件
        ExchangeData sj = exchangeDataService.getOne(uuid);
        if (sj != null) {
            Map<String, Object> map1 = new HashMap<String, Object>();
            map1.put("code", 1);
            map1.put("task", "收件");
            map1.put("url", request.getContextPath() + "/exchangedata/client/toSendDetailPage?UUID=" + uuid);
            map1.put("time", sj.getReservedText3());
            list1.add(map1);
        }

        boolean isSL = false;
        List<ExchangeData> slList2 = exchangeDataService.findNewestDataByYwlshAndTypeId(ywlsh,
                ExchangeConfig.SPGC_SL_TYPEID, null);
        if (slList2 != null && slList2.size() > 0) {
            Map<String, Object> map2 = new HashMap<String, Object>();
            map2.put("code", 2);
            // 受理
            if (slList2.get(0).getReservedNumber1().equals("0")) {
                map2.put("task", "受理");
                isSL = true;
            } else {
                map2.put("task", "不受理");
            }
            map2.put("url", request.getContextPath() + "/exchangedata/client/toSendDetailPage?UUID="
                    + slList2.get(0).getUuid());
            map2.put("time", slList2.get(0).getReservedText3());
            list2.add(map2);
        } else {
            Map<String, Object> map2 = new HashMap<String, Object>();
            map2.put("code", 2);
            map2.put("task", "受理");
            list2.add(map2);
        }

        List<String> codeMsg = new ArrayList<String>(); // 对节点未办理排序用

        List<CdDataDictionaryItemDto> dataDictionaries = basicDataApiFacade.getDataDictionariesByType("SPHJMC");
        Map<String, String> spMap = new HashMap<String, String>();
        for (CdDataDictionaryItemDto d : dataDictionaries) {
            spMap.put(d.getValue(), d.getValue());
        }

        // 承办
        List<ExchangeData> sjList3 = exchangeDataService.findNewestDataByYwlshAndTypeId(ywlsh,
                ExchangeConfig.SPGC_SH_TYPEID, spMap.get(ExchangeConfig.SH_SPHJDM_CB));
        if (sjList3 != null && sjList3.size() > 0) {
            String s = sjList3.get(0).getReservedText3();
            sourceTimeMap.put(s, "承办");
            dateStr.add(s);
            sourceUrlMap
                    .put(s, request.getContextPath() + "/exchangedata/client/toSendDetailPage?UUID="
                            + sjList3.get(0).getUuid());
        } else {
            codeMsg.add("承办");
        }

        // 审核
        List<ExchangeData> sjList4 = exchangeDataService.findNewestDataByYwlshAndTypeId(ywlsh,
                ExchangeConfig.SPGC_SH_TYPEID, spMap.get(ExchangeConfig.SH_SPHJDM_SH));
        if (sjList4 != null && sjList4.size() > 0) {
            String s = sjList4.get(0).getReservedText3();
            sourceTimeMap.put(s, "审核");
            dateStr.add(s);
            sourceUrlMap
                    .put(s, request.getContextPath() + "/exchangedata/client/toSendDetailPage?UUID="
                            + sjList4.get(0).getUuid());
        } else {
            codeMsg.add("审核");
        }

        // 批准
        List<ExchangeData> sjList5 = exchangeDataService.findNewestDataByYwlshAndTypeId(ywlsh,
                ExchangeConfig.SPGC_SH_TYPEID, spMap.get(ExchangeConfig.SH_SPHJDM_PZ));
        if (sjList5 != null && sjList5.size() > 0) {
            String s = sjList5.get(0).getReservedText3();
            sourceTimeMap.put(s, "批准");
            dateStr.add(s);
            sourceUrlMap
                    .put(s, request.getContextPath() + "/exchangedata/client/toSendDetailPage?UUID="
                            + sjList5.get(0).getUuid());
        } else {
            codeMsg.add("批准");
        }

        // 办结
        boolean isBJ = false;
        List<ExchangeData> sjList6 = exchangeDataService.findNewestDataByYwlshAndTypeId(ywlsh,
                ExchangeConfig.SPGC_BJ_TYPEID, null);
        if (sjList6 != null && sjList6.size() > 0) {
            isBJ = true;
            String s = sjList6.get(0).getReservedText3();
            sourceTimeMap.put(s, "办结");
            dateStr.add(s);
            sourceUrlMap
                    .put(s, request.getContextPath() + "/exchangedata/client/toSendDetailPage?UUID="
                            + sjList6.get(0).getUuid());
        } else {
            codeMsg.add("办结");
        }

        // 补交告知
        boolean isBJGZ = false;
        List<ExchangeData> sjList7 = exchangeDataService.findNewestDataByYwlshAndTypeId(ywlsh,
                ExchangeConfig.SPGC_BJGZ_TYPEID, null);
        if (sjList7 != null && sjList7.size() > 0) {
            isBJGZ = true;
            String s = sjList7.get(0).getReservedText3();
            sourceTimeMap.put(s, "补交告知");
            dateStr.add(s);
            sourceUrlMap
                    .put(s, request.getContextPath() + "/exchangedata/client/toSendDetailPage?UUID="
                            + sjList7.get(0).getUuid());
        }

        // 补交受理
        boolean isBJSL = false;
        List<ExchangeData> sjList8 = exchangeDataService.findNewestDataByYwlshAndTypeId(ywlsh,
                ExchangeConfig.SPGC_BJSL_TYPEID, null);
        if (sjList8 != null && sjList8.size() > 0) {
            isBJSL = true;
            String s = sjList8.get(0).getReservedText3();
            sourceTimeMap.put(s, "补交受理");
            dateStr.add(s);
            sourceUrlMap
                    .put(s, request.getContextPath() + "/exchangedata/client/toSendDetailPage?UUID="
                            + sjList8.get(0).getUuid());
        }

        // 特别程序开始
        boolean isCXKS = false;
        List<ExchangeData> sjList9 = exchangeDataService.findNewestDataByYwlshAndTypeId(ywlsh,
                ExchangeConfig.SPGC_CXKS_TYPEID, null);
        if (sjList9 != null && sjList9.size() > 0) {
            isCXKS = true;
            String s = sjList9.get(0).getReservedText3();
            sourceTimeMap.put(s, "特别程序开始");
            dateStr.add(s);
            sourceUrlMap
                    .put(s, request.getContextPath() + "/exchangedata/client/toSendDetailPage?UUID="
                            + sjList9.get(0).getUuid());
        }

        // 特别程序结束
        boolean isCXJS = false;
        List<ExchangeData> sjList10 = exchangeDataService.findNewestDataByYwlshAndTypeId(ywlsh,
                ExchangeConfig.SPGC_CXJS_TYPEID, null);
        if (sjList10 != null && sjList10.size() > 0) {
            isCXJS = true;
            String s = sjList10.get(0).getReservedText3();
            sourceTimeMap.put(s, "特别程序结束");
            dateStr.add(s.toString());
            sourceUrlMap.put(s, request.getContextPath() + "/exchangedata/client/toSendDetailPage?UUID="
                    + sjList10.get(0).getUuid());
        }

        list.add(list1);
        list.add(list2);

        List<String> resultList = null;
        try {
            resultList = sortDateStr(dateStr);
        } catch (ParseException e) {
            logger.info(e.getMessage());
        }

        // 按时间先后添加节点信息
        if (resultList != null && resultList.size() > 0) {
            int len = resultList.size();
            for (int i = 0; i < len; i++) {
                List<Map<String, Object>> list3 = new ArrayList<Map<String, Object>>();
                Map<String, Object> map3 = new HashMap<String, Object>();
                map3.put("code", i + 3);
                map3.put("task", sourceTimeMap.get(resultList.get(i)));
                map3.put("url", sourceUrlMap.get(resultList.get(i)));
                map3.put("time", resultList.get(i));
                list3.add(map3);
                list.add(list3);
            }
        }

        // 至少显示前6个节点（不包括补交和特别程序）
        int length = codeMsg.size();
        if (length > 0) {
            for (int i = 0; i < length; i++) {
                List<Map<String, Object>> list4 = new ArrayList<Map<String, Object>>();
                Map<String, Object> map4 = new HashMap<String, Object>();
                map4.put("code", list.size() + 1);
                map4.put("task", codeMsg.get(i));
                list4.add(map4);
                list.add(list4);
            }
        }

        // 事项定义
        List<Map> listmap = this.getFormDataBySql("uf_matters_definition",
                "approval_number='" + sj.getReservedNumber1() + "'");
        if (listmap != null && listmap.size() > 0) {
            map.put("type", listmap.get(0).get("matters_name").toString()); // 获取事项名称
        }
        // 事项若被受理
        try {
            if (isSL) {
                int limitDays = 0;
                String promiseDays = "";
                if (listmap != null && listmap.size() > 0) {
                    promiseDays = listmap.get(0).get("promise_transact_deadline").toString(); // 获取承诺办结时间
                }
                DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                Date startDate = format.parse(slList2.get(0).getReservedText3());// 受理时间
                Date endDate = new Date(); // 办结时间(对于未办结的 取当前时间)
                // 对于已办结的
                if (isBJ) {
                    endDate = format.parse(sjList6.get(0).getReservedText3());
                }
                WorkPeriod WorkPeriod = basicDataApiFacade.getWorkPeriod(startDate, endDate);
                int betweenDays = WorkPeriod.getDays();
                limitDays = betweenDays - Integer.parseInt(promiseDays);
                // 事项若有补交告知，而没有补交受理，不用算逾期天数(特别程序亦同)
                boolean flag = false;
                if ((isBJGZ && !isBJSL) || (isCXKS && !isCXJS)) {
                    flag = true;
                }

                if (isBJGZ && isBJSL) {
                    Date bjgzDate = format.parse(sjList7.get(0).getReservedText3()); // 办结告知
                    Date bjslDate = format.parse(sjList8.get(0).getReservedText3()); // 办结受理
                    WorkPeriod bjPeriod = basicDataApiFacade.getWorkPeriod(bjgzDate, bjslDate);
                    int bjDays = bjPeriod.getDays();
                    if (!"".equals(promiseDays)) {
                        limitDays = limitDays - bjDays;
                    }
                }

                if (isCXKS && isCXJS) {
                    Date cxksDate = format.parse(sjList9.get(0).getReservedText3()); // 特别程序开始
                    Date ckjsDate = format.parse(sjList10.get(0).getReservedText3()); // 特别程序结束
                    WorkPeriod cxPeriod = basicDataApiFacade.getWorkPeriod(cxksDate, ckjsDate);
                    int cxDays = cxPeriod.getDays();
                    if (!"".equals(promiseDays)) {
                        limitDays = limitDays - cxDays;
                    }
                }
                if (limitDays > 0 && !flag) {
                    map.put("limitNum", limitDays); // 逾期天数
                }
            }
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            logger.info(e.getMessage());
        }
        map.put("branchNumber", 1); // 1代表没有其他分支，只有一条主线信息
        map.put("data", list);
        result.add(map);
        return result;
    }

    /**
     * 通过sql和参数获取动态表单
     *
     * @param tableName 动态表单表名
     * @param whereHql  查询语句
     * @param sphjdm    审批环节代码
     * @return
     */
    @Override
    public List<Map> getFormDataBySql(String tableName, String whereHql) {
        List<Map> sjList = new ArrayList<Map>();
        // sjList = formDataDao.getFormDataBySql(tableName, whereHql);
        return sjList;
    }

    private List<String> sortDateStr(List<String> dateStr) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ArrayList<Date> dateList = new ArrayList<Date>();

        // 字符串转时间
        for (String str : dateStr) {
            dateList.add(format.parse(str));
        }

        // 冒泡排序
        Date tempDate = null;
        for (int i = dateList.size() - 1; i > 0; --i) {
            for (int j = 0; j < i; ++j) {
                // 从小到大
                if (dateList.get(j + 1).before(dateList.get(j))) {
                    tempDate = dateList.get(j);
                    dateList.set(j, dateList.get(j + 1));
                    dateList.set(j + 1, tempDate);
                }
            }
        }
        List<String> resultStr = new ArrayList<String>();
        for (Date d : dateList) {
            resultStr.add(format.format(d));
        }

        return resultStr;
    }

    /**
     * 收件详情
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.service.ExchangeDataClientService#getReceiveDetailByExDataUuid(java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public ExchangeDataDetailBean getReceiveDetailByExDataUuid(String uuid) {

        ExchangeDataDetailBean bean = new ExchangeDataDetailBean();
        ExchangeDataMonitor exchangeDataMonitor = exchangeDataMonitorService.getOne(uuid);
        ExchangeDataSendMonitor exchangeDataSendMonitor = exchangeDataMonitor.getExchangeDataSendMonitor();
        ExchangeData exchangeData = exchangeDataSendMonitor.getExchangeData();
        ExchangeDataBatch batch = exchangeData.getExchangeDataBatch();
        ExchangeDataType dataType = exchangeDataTypeService.getByTypeId(batch.getTypeId());
        List<CommonUnit> commonUnits = unitApiFacade.getCommonUnitsByBusinessTypeIdAndUserId(
                ExchangeConfig.EXCHANGE_BUSINESS_TYPE, SpringSecurityUtils.getCurrentUserId());
        String unitId = "";
        if (commonUnits.size() > 0) {
            unitId = commonUnits.get(0).getId();
        }
        bean.setBcc(batch.getBcc());
        // 已收，要获取 该单位下属 各个区的单位
        List<CommonUnit> unitList = unitApiFacade.getCommonUnitsByParentIdAndBusinessTypeId(unitId,
                ExchangeConfig.EXCHANGE_BUSINESS_TYPE);
        if (unitList != null && unitList.size() > 0) {
            bean.setHasUnderling(1);
        } else {
            bean.setHasUnderling(0);
        }
        bean.setUnitList(unitList);

        bean.setDrafter(orgApiFacade.getUserNameById(exchangeData.getDrafter()));
        bean.setDraftTime(exchangeData.getDraftTime());
        bean.setReleaser(orgApiFacade.getUserNameById(exchangeData.getReleaser()));
        bean.setReleaseTime(exchangeData.getReleaseTime());

        bean.setDataUuid(exchangeDataMonitor.getFormDataUuid());
        bean.setFormId(exchangeDataMonitor.getFormId());
        bean.setFromUnitName(getUnitName(exchangeDataSendMonitor.getFromId()));
        bean.setTypeId(dataType.getId());
        bean.setTypeName(dataType.getName());
        bean.setDataId(exchangeData.getDataId());
        bean.setRecVer(exchangeData.getDataRecVer());
        bean.setReceiveNode(exchangeDataMonitor.getReceiveNode());
        String userName = orgApiFacade.getUserNameById(exchangeDataSendMonitor.getSendUser());
        if (!StringUtils.isBlank(userName)) {
            userName = "(" + userName + ")";
        } else {
            userName = "";
        }
        bean.setFromUserName(userName);
        if (StringUtils.isNotBlank(exchangeDataMonitor.getReplyStatus())
                && exchangeDataMonitor.getReplyStatus().equals("default")) {
            Date beginDate = exchangeDataSendMonitor.getLimitTime();
            Date endDate = new Date();
            if (beginDate.before(endDate)) {
                WorkPeriod workPeriod = basicDataApiFacade.getWorkPeriod(beginDate, endDate);
                bean.setReplyLimitNum(workPeriod.getDays());
            } else {
                bean.setReplyLimitNum(exchangeDataMonitor.getReplyLimitNum());
            }
        } else {
            bean.setReplyLimitNum(exchangeDataMonitor.getReplyLimitNum());
            // 已签收的退回
            Date replyTime = exchangeDataMonitor.getReplyTime();
            Date refuseLimitTime = basicDataApiFacade.getWorkDate(replyTime, 1 * 1.00, WorkUnit.WorkingDay);
            Date nowDate = new Date();
            if (refuseLimitTime.before(nowDate)) {
                bean.setShowRefuse(0);// 不能退回
            } else {
                bean.setShowRefuse(1);// 能退回
            }
        }
        String title = "";
        String ztmc = exchangeData.getReservedText1(); // 主体
        ExchangeDataType type = exchangeDataTypeService.getByTypeId(batch.getTypeId());
        title = type.getName();
        if (StringUtils.isNotBlank(exchangeDataMonitor.getMatter())) {
            title = title + "-" + exchangeDataMonitor.getMatter();
        }
        if (StringUtils.isNotBlank(ztmc)) {
            title = ztmc + "(" + title + ")";
        }
        bean.setTitle(title);
        if (StringUtils.isNotBlank(exchangeDataMonitor.getUnitId())) {
            CommonUnit unit = unitApiFacade.getCommonUnitById(exchangeDataMonitor.getUnitId());
            if (unit != null) {
                bean.setTo(unit.getName());
            }
        }
        Set<ExchangeDataMonitor> monitors = exchangeDataSendMonitor.getExchangeDataMonitors();
        Iterator i = monitors.iterator();
        if (monitors != null) {
            List<ExchangeDataMonitorBean> mointorList = new ArrayList<ExchangeDataMonitorBean>();
            while (i.hasNext()) {
                ExchangeDataMonitor e = (ExchangeDataMonitor) i.next();
                ExchangeDataMonitorBean mBean = new ExchangeDataMonitorBean();
                BeanUtils.copyProperties(e, mBean);
                if (mBean.getUnitId() == null || "".equals(mBean.getUnitId())) {
                    continue;
                }
                mBean.setUnitName(getUnitName(mBean.getUnitId()));
                mBean.setFromUnitName(getUnitName(exchangeDataSendMonitor.getFromId()));
                mBean.setCreateTime(e.getCreateTime());
                if (StringUtils.isNotBlank(mBean.getReplyUser())) {
                    mBean.setReplyUser(orgApiFacade.getUserNameById(mBean.getReplyUser()));
                } else {
                    mBean.setReplyUser("");
                }
                mointorList.add(mBean);
            }
            bean.setExchangeDataMonitors(mointorList);
        }
        return bean;
    }

    /**
     * 模块查询数据
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.service.ExchangeDataClientService#queryExchangeModuleData(java.lang.String, java.util.Map, com.wellsoft.pt.core.support.PagingInfo)
     */
    @Override
    public List<QueryItem> queryExchangeQueryItemData(String hql, Map<String, Object> values, PagingInfo pagingInfo) {
        List<QueryItem> queryItems = advisoryComplaintsService.listQueryItemByHQL(hql, values, pagingInfo);
        return queryItems;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> selectQueryItemDataBySql(String sql) {
        // TODO Auto-generated method stub
        Session session = this.dao.getSession();
        List<Map<String, Object>> queryItems = session.createSQLQuery(sql)
                .setResultTransformer(ExchangeDataResultTransformer.INSTANCE).list();
        return queryItems;
    }

    /**
     * 模块查询条数
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.service.ExchangeDataClientService#queryExchangeModuleCount(java.lang.String, java.util.Map)
     */
    @Override
    @Transactional(readOnly = true)
    public Long queryExchangeQueryItemCount(String hql, Map<String, Object> values) {
        return dao.findUnique(hql, values);
    }

    @Transactional(readOnly = true)
    public String getUnitNames(String unitIds) {
        if (StringUtils.isBlank(unitIds)) {
            return "";
        } else {
            String[] unitsTemp = unitIds.split(";");
            String unitNames = "";
            for (int i = 0; i < unitsTemp.length; i++) {
                String unitName = "";
                CommonUnit cu = unitApiFacade.getCommonUnitById(unitsTemp[i]);
                if (cu != null) {
                    unitNames += ";" + cu.getName();
                } else {
                    unitNames += ";" + unitsTemp[i];
                }
            }
            unitNames = unitNames.replaceFirst(";", "");
            return unitNames;
        }

    }

    @Transactional(readOnly = true)
    public String getUnitName(String unitId) {
        String unitName = "";
        CommonUnit cu = unitApiFacade.getCommonUnitById(unitId);
        if (cu != null) {
            unitName = cu.getName();
        } else {
            unitName = unitId;
        }
        return unitName;
    }

    @Transactional(readOnly = true)
    public String getUnitShortName(String unitId) {
        String unitName = "";
        CommonUnit cu = unitApiFacade.getCommonUnitById(unitId);
        if (cu != null) {
            unitName = cu.getShortName();
            if (StringUtils.isBlank(unitName)) {
                unitName = cu.getName();
            }
        } else {
            unitName = unitId;
        }
        return unitName;
    }

    /**
     * 获得发件下的所有收件人
     *
     * @param sendMonitorUuid
     * @return
     */
    @Transactional(readOnly = true)
    public String getMonitorsAllUnit(String sendMonitorUuid) {
        ExchangeDataSendMonitor edsm = exchangeDataSendMonitorService.getOne(sendMonitorUuid);
        String unitIds = "";
        for (ExchangeDataMonitor edm : edsm.getExchangeDataMonitors()) {
            unitIds += ";" + edm.getUnitId();
        }
        unitIds = unitIds.replaceFirst(";", "");
        return unitIds;
    }

    @Override
    public ExchangeData toZZDJXX(String zch) {
        // 商事主体上报集合
        String firsthql = "from ExchangeData e where e.reservedNumber2 = :zch and e.exchangeDataBatch.typeId = :typeId and e.newestData=:newestData order by e.createTime";
        Map<String, Object> firsthqlMap = new HashMap<String, Object>();
        firsthqlMap.put("zch", zch);
        firsthqlMap.put("typeId", ExchangeConfig.TYPE_ID_SSXX_ZTDJ);
        firsthqlMap.put("newestData", "yes");
        List<ExchangeData> ssExchangeDatas = exchangeDataService.listByHQL(firsthql, firsthqlMap);
        if (ssExchangeDatas.size() == 0) {
            return new ExchangeData();
        }
        return ssExchangeDatas.get(0);

    }

    /************************视图查询******************************/
    // 获得发件需要的内容（业务类型，收件单位）
    @Override
    @Transactional(readOnly = true)
    public Map<String, String> getFJSpecial(String sendMonitorUuid) {
        Map<String, String> map = new HashMap<String, String>();
        ExchangeDataSendMonitor exchangeDataSendMonitor = exchangeDataSendMonitorService.getOne(sendMonitorUuid);
        String typeId = exchangeDataSendMonitor.getExchangeData().getExchangeDataBatch().getTypeId();
        ExchangeDataType exchangeDataType = exchangeDataTypeService.getByTypeId(typeId);
        map.put("typeName", exchangeDataType.getName());
        // Set<ExchangeDataMonitor> exchangeDataMonitors =
        // exchangeDataSendMonitor.getExchangeDataMonitors();
        // String unitIds = "";
        // for (ExchangeDataMonitor exchangeDataMonitor : exchangeDataMonitors)
        // {
        // unitIds += ";" + exchangeDataMonitor.getUnitId();
        // }
        // unitIds = unitIds.replaceFirst(";", "");
        // map.put("unitIds", unitIds);
        return map;
    }

    @Override
    public Boolean saveQueryItem(String tableName, Map<String, Object> params) {
        // TODO Auto-generated method stub
        try {
            return true;
        } catch (Exception e) {
            return false;
        }

    }

    @Override
    public Boolean delDataLog(String name, String dataUuid) {
        // TODO Auto-generated method stub
        DelDataLog delDataLog = new DelDataLog();
        delDataLog.setDataUuid(dataUuid);
        delDataLog.setTableName(name);
        delDataLogService.save(delDataLog);
        return true;
    }

    @Override
    public String getSSZTName(String zch) {
        // TODO Auto-generated method stub
        return null;
    }
}
