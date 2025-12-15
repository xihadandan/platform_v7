package com.wellsoft.pt.integration.web;

import com.wellsoft.context.config.Config;
import com.wellsoft.context.util.file.FileDownloadUtils;
import com.wellsoft.pt.integration.bean.ExchangeDataDetailBean;
import com.wellsoft.pt.integration.entity.ExchangeData;
import com.wellsoft.pt.integration.entity.ExchangeDataSendMonitor;
import com.wellsoft.pt.integration.entity.ExchangeDataType;
import com.wellsoft.pt.integration.handler.ExchangeXzxkBusinessDataProcessor;
import com.wellsoft.pt.integration.request.SendRequest;
import com.wellsoft.pt.integration.request.ShareRequest;
import com.wellsoft.pt.integration.response.ShareResponse;
import com.wellsoft.pt.integration.security.ExchangeConfig;
import com.wellsoft.pt.integration.service.ExchangeDataClientService;
import com.wellsoft.pt.integration.service.ExchangeDataFlowService;
import com.wellsoft.pt.integration.service.ExchangeDataTypeService;
import com.wellsoft.pt.integration.support.Condition;
import com.wellsoft.pt.integration.support.DataItem;
import com.wellsoft.pt.integration.support.InputStreamDataSource;
import com.wellsoft.pt.integration.support.StreamingData;
import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;
import com.wellsoft.pt.repository.service.MongoFileService;
import com.wellsoft.pt.security.audit.entity.Resource;
import com.wellsoft.pt.security.facade.service.SecurityApiFacade;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.unit.entity.CommonUnit;
import com.wellsoft.pt.unit.facade.service.UnitApiFacade;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.activation.DataHandler;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Description: 如何描述该类
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
@Controller
@RequestMapping("/exchangedata/client")
public class ExchangeDataClientController {

    protected Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private ExchangeDataClientService exchangeDataClientService;
    @Autowired
    private ExchangeDataTypeService exchangeDataTypeService;
    @Autowired
    private MongoFileService mongoFileService;
    @Autowired
    private UnitApiFacade unitApiFacade;
    @Autowired
    private ExchangeDataFlowService exchangeDataFlowService;
    @Autowired
    private SecurityApiFacade securityApiFacade;

    /**
     * 到数据类型列表
     *
     * @param request
     * @param modelMap
     * @return
     */
    @RequestMapping(value = "/toTypeList")
    public String toDataTypeList(HttpServletRequest request, ModelMap modelMap,
                                 @RequestParam(value = "dataId", required = false) String dataId,
                                 @RequestParam(value = "recVer", required = false) String recVer,
                                 @RequestParam(value = "rel", required = false) String rel) {
        // 根据单位限制可发的数据类型
        List<ExchangeDataType> typeList = exchangeDataTypeService.getExDataTypeListByUnitId();
        modelMap.put("typeList", typeList);
        modelMap.put("dataId", dataId);
        modelMap.put("recVer", recVer);
        modelMap.put("rel", rel);
        return "pt/exchangedata/sendview";
    }

    /**
     * 到发件页
     *
     * @param request
     * @param formId
     * @param typeId
     * @param model
     * @return
     */
    @RequestMapping(value = "/toSendData")
    public String toWriteMail(HttpServletRequest request, HttpServletResponse response, Model model,
                              @RequestParam(value = "typeUuid", required = false) String typeUuid,
                              @RequestParam(value = "rel") Integer rel, @RequestParam(value = "dataId", required = false) String dataId,
                              @RequestParam(value = "recVer", required = false) Integer recVer,
                              @RequestParam(value = "sendUuid", required = false) String sendUuid) {
        String typeId = "";
        if (rel == 20) {// 直接发送
            ExchangeDataType dataType = exchangeDataTypeService.getExchangeDataTypeByUuid(typeUuid);
            response.setCharacterEncoding("UTF-8");
            model.addAttribute("formId", dataType.getFormId());
            model.addAttribute("typeName", dataType.getName());
            model.addAttribute("typeId", dataType.getId());
            model.addAttribute("showToUnit", dataType.getShowToUnit());
            model.addAttribute("rel", rel);
            typeId = dataType.getId();
            // 过滤出商事行政许可的列表配置信息 6种阶段
            ExchangeXzxkBusinessDataProcessor businessDataProcessor = new ExchangeXzxkBusinessDataProcessor();
            if (businessDataProcessor.isContainDataTypeId(dataType.getId())) {
                List<CommonUnit> unitList = unitApiFacade.getCommonUnitsByBusinessTypeIdAndUserId(
                        ExchangeConfig.EXCHANGE_BUSINESS_TYPE, SpringSecurityUtils.getCurrentUserId());
                if (unitList != null && unitList.size() > 0) {
                    model.addAttribute("unitId", unitList.get(0).getId());
                    model.addAttribute("unitName", unitList.get(0).getName());
                }
                // 业务状态
                model.addAttribute("businessStatus", businessDataProcessor.getBusinessStatus(dataType.getId())
                        .getValue());

                model.addAttribute("pageType", "xzxk");
            }
        } else if (rel == 27) {// 审核被退回编辑
            Map map = exchangeDataClientService.getSendDataValueByUuid(sendUuid);
            model.addAttribute("dataUuid", map.get("dataUuid"));
            model.addAttribute("toId1", map.get("toId"));
            model.addAttribute("toNames1", map.get("toNames"));
            model.addAttribute("cc1", map.get("cc"));
            model.addAttribute("ccNames1", map.get("ccNames"));
            model.addAttribute("bcc1", map.get("bcc"));
            model.addAttribute("bccNames1", map.get("bccNames"));
            model.addAttribute("formId", map.get("formId"));
            model.addAttribute("typeName", map.get("typeName"));
            model.addAttribute("typeId", map.get("typeId"));
            model.addAttribute("rel", rel);
            model.addAttribute("sendUuid", sendUuid);
            typeId = map.get("typeId").toString();
        } else {// 回复出证
            Map map = exchangeDataClientService.getSendDataValue(dataId, recVer);
            model.addAttribute("showToUnit", true);
            model.addAttribute("rel", rel);
            model.addAttribute("correlationDataId", dataId);
            model.addAttribute("correlationRecver", recVer);
            if (rel == 23) {// 回复
                ExchangeDataType dataType = exchangeDataTypeService.getExchangeDataTypeByUuid(typeUuid);
                model.addAttribute("from", map.get("from"));
                model.addAttribute("fromName", map.get("fromName"));
                model.addAttribute("formId", dataType.getFormId());
                model.addAttribute("typeName", dataType.getName());
                model.addAttribute("typeId", dataType.getId());

                model.addAttribute("ZCH", map.get("ZCH"));
                model.addAttribute("ZTMC", map.get("ZTMC"));
                model.addAttribute("FDDBR", map.get("FDDBR"));
                // model.addAttribute("JYCS", map.get("JYCS"));
                model.addAttribute("ZTLX", map.get("ZTLX"));
                typeId = dataType.getId();
            } else if (rel == 24) {// 出证
                ExchangeDataType dataType = exchangeDataTypeService.getExchangeDataTypeByUuid(typeUuid);
                model.addAttribute("from", map.get("from"));
                model.addAttribute("fromName", map.get("fromName"));
                model.addAttribute("formId", dataType.getFormId());
                model.addAttribute("typeName", dataType.getName());
                model.addAttribute("typeId", dataType.getId());
                model.addAttribute("ZCH", map.get("ZCH"));
                model.addAttribute("ZTMC", map.get("ZTMC"));
                model.addAttribute("FDDBR", map.get("FDDBR"));
                // model.addAttribute("JYCS", map.get("JYCS"));
                model.addAttribute("ZTLX", map.get("ZTLX"));
                typeId = dataType.getId();
            }
        }
        List<Resource> btns = securityApiFacade.getDynamicButtonResourcesByCode("012");
        List<Resource> grantBtns = new ArrayList<Resource>();
        for (Resource resource : btns) {
            if (securityApiFacade.isGranted(resource.getCode())) {
                grantBtns.add(resource);
            }
        }
        model.addAttribute("btns", grantBtns);
        if (ExchangeConfig.TYPE_ID_SSXX_XZXK.equals(typeId)) {
            List<CommonUnit> unitList = unitApiFacade.getCommonUnitsByBusinessTypeIdAndUserId(
                    ExchangeConfig.EXCHANGE_BUSINESS_TYPE, SpringSecurityUtils.getCurrentUserId());
            if (unitList != null && unitList.size() > 0) {
                model.addAttribute("unitId", unitList.get(0).getId());
                model.addAttribute("unitName", unitList.get(0).getName());
            }
        }
        return "pt/exchangedata/senddata";
    }

    /**
     * 到发件详细页
     *
     * @param request
     * @param uuid    sendMonitorUuid
     * @param rel
     * @param model
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = "/toSendDetailPage")
    public String toSendDetailPage(HttpServletRequest request, @RequestParam(value = "UUID") String uuid, Model model)
            throws UnsupportedEncodingException {

        ExchangeDataDetailBean bean = exchangeDataClientService.getSendDetailByExDataUuid(uuid);
        bean.setUuid(uuid);

        List<Resource> btns = securityApiFacade.getDynamicButtonResourcesByCode("012");
        List<Resource> grantBtns = new ArrayList<Resource>();
        for (Resource resource : btns) {
            if (securityApiFacade.isGranted(resource.getCode())) {
                grantBtns.add(resource);
            }
        }
        bean.setBtns(grantBtns);
        model.addAttribute("bean", bean);
        return "pt/exchangedata/detail";
    }

    /**
     * 上报数据详情
     *
     * @param request
     * @param uuid
     * @param model
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = "/toExchangeDetailPage")
    public String toExchangeDetailPage(HttpServletRequest request, @RequestParam(value = "UUID") String uuid,
                                       Model model) throws UnsupportedEncodingException {
        ExchangeDataSendMonitor edsm = exchangeDataClientService.getFromEdsmByEdUuid(uuid);
        ExchangeDataDetailBean bean = exchangeDataClientService.getSendDetailByExDataUuid(edsm.getUuid());
        bean.setUuid(edsm.getUuid());

        List<Resource> btns = securityApiFacade.getDynamicButtonResourcesByCode("012");
        List<Resource> grantBtns = new ArrayList<Resource>();
        for (Resource resource : btns) {
            if (securityApiFacade.isGranted(resource.getCode())) {
                grantBtns.add(resource);
            }
        }
        bean.setBtns(grantBtns);
        model.addAttribute("bean", bean);
        return "pt/exchangedata/detail";
    }

    @RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
    @ResponseBody
    public void uploadFile(@RequestParam(value = "fileName", required = false) String fileName,
                           @RequestParam(value = "upload") MultipartFile upload, HttpServletResponse response) throws IOException {
        InputStream ins = upload.getInputStream();
        // 将inputStream转成file
        String path = Config.APP_DATA_DIR;
        File file = new File(path, "aaa.zip");
        OutputStream os = new FileOutputStream(file);
        int bytesRead = 0;
        byte[] buffer = new byte[52428800];
        while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
            os.write(buffer, 0, bytesRead);
        }
        os.close();
        ins.close();
        // 解析zip
        SendRequest request = new SendRequest();
        try {
            ZipFile zipFile = new ZipFile(file);
            String zipName = file.getName();
            String[] zipNameArray = zipName.split("_");
            // 一个批次
            request.setBatchId(zipNameArray[1]);
            List<CommonUnit> commonUnits = unitApiFacade.getCommonUnitsByBusinessTypeIdAndUserId(
                    ExchangeConfig.EXCHANGE_BUSINESS_TYPE, SpringSecurityUtils.getCurrentUserId());
            if (commonUnits == null || commonUnits.size() != 0) {
                request.setFrom(commonUnits.get(0).getId());
            } else {
                // 没有可发业务
            }
            request.setTypeId(zipNameArray[0]);
            List<DataItem> datdItems = new ArrayList<DataItem>();

            @SuppressWarnings("unchecked")
            Enumeration<ZipEntry> enu = (Enumeration<ZipEntry>) zipFile.entries();

            DataItem dataItem = null;
            List<StreamingData> streamingDatas = null;
            while (enu.hasMoreElements()) {
                // 一条数据
                ZipEntry zipElement = (ZipEntry) enu.nextElement();
                fileName = zipElement.getName();
                InputStream read = zipFile.getInputStream(zipElement);
                if (fileName != null && fileName.indexOf(".") != -1) {// 文件
                    if (fileName.indexOf(".xml") > -1) {// xml
                        StringBuffer out = new StringBuffer();
                        byte[] b = new byte[4096];
                        for (int n; (n = read.read(b)) != -1; ) {
                            out.append(new String(b, 0, n));
                        }
                        dataItem.setText(out.toString());
                    } else {// 附件
                        StreamingData sd = new StreamingData();
                        sd.setFileName(fileName);
                        sd.setDataHandler(new DataHandler(new InputStreamDataSource(read, "octet-stream")));
                        streamingDatas.add(sd);
                    }
                } else {// 文件夹
                    if (dataItem != null) {
                        dataItem.setStreamingDatas(streamingDatas);
                        datdItems.add(dataItem);
                    }
                    dataItem = new DataItem();
                    streamingDatas = new ArrayList<StreamingData>();
                    String[] fileNameArray = fileName.split("_");
                    dataItem.setDataId(fileNameArray[0]);
                    dataItem.setRecVer(Integer.parseInt(fileNameArray[1]));
                }
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
        // 发送数据
        exchangeDataFlowService.send(request, 25);
    }

    /**
     * 商事登记簿详情
     *
     * @param request
     * @param zch
     * @param ztName
     * @param model
     * @return
     */
    @RequestMapping(value = "/toSSDJBDetail")
    public String toSSDJBPage(HttpServletRequest request, @RequestParam(value = "zch") String zch, Model model) {
        model.addAttribute("zch", zch);
        model.addAttribute("ztName", exchangeDataClientService.getSSZTName(zch));
        return "pt/exchangedata/toSSDJBDetail";
    }

    /**
     * 到批量上传页
     *
     * @return
     */
    @RequestMapping(value = "/uploadZip")
    public String uploadZip() {
        return "pt/exchangedata/uploadZipFile";
    }

    /**
     * 行政许可过程详情
     *
     * @param request
     * @param zch
     * @param model
     * @return
     */
    @RequestMapping(value = "/toXZXKGCDetail")
    public String toXZXKGCPage(HttpServletRequest request, @RequestParam(value = "ywlsh") String ywlsh,
                               @RequestParam(value = "uuid") String uuid, Model model) {
        ExchangeData data = exchangeDataClientService.getExchangeDataByUUid(uuid);
        List<Map> mattersList = exchangeDataClientService.getFormDataBySql("userform_matters_definition",
                "approval_number='" + data.getReservedNumber1() + "'");
        model.addAttribute("ywlsh", ywlsh);
        model.addAttribute("uuid", uuid);
        if (mattersList != null && mattersList.size() > 0) {
            model.addAttribute("spsxName", mattersList.get(0).get("matters_name") == null ? "" : mattersList.get(0)
                    .get("matters_name").toString()); // 事项名
        }
        model.addAttribute("sqrhsqdw", data.getReservedText2()); // 申请人或申请单位
        return "pt/exchangedata/toXZXKGCDetail";
    }

    @RequestMapping(value = "/toReceiveDetailPage")
    public String toReceiveDetailPage(HttpServletRequest request, @RequestParam(value = "UUID") String uuid,
                                      @RequestParam(value = "status", required = false) String status, Model model)
            throws UnsupportedEncodingException {
        ExchangeDataDetailBean bean = exchangeDataClientService.getReceiveDetailByExDataUuid(uuid);
        bean.setUuid(uuid);
        if (StringUtils.isNotBlank(status)) {
            model.addAttribute("status", status);
        }

        List<Resource> btns = securityApiFacade.getDynamicButtonResourcesByCode("012");
        List<Resource> grantBtns = new ArrayList<Resource>();
        for (Resource resource : btns) {
            if (securityApiFacade.isGranted(resource.getCode())) {
                grantBtns.add(resource);
            }
        }
        bean.setBtns(grantBtns);
        List<CommonUnit> commonUnits = unitApiFacade.getCommonUnitsByBusinessTypeIdAndUserId(
                ExchangeConfig.EXCHANGE_BUSINESS_TYPE, SpringSecurityUtils.getCurrentUserId());
        if (commonUnits != null && commonUnits.size() > 0) {
            bean.setUserUnitId(commonUnits.get(0).getId());
        }
        model.addAttribute("bean", bean);
        return "pt/exchangedata/detail";
    }

    @RequestMapping(value = "/repeatTask")
    public void repeatTask() {
        exchangeDataFlowService.exchangeDataRepeatTask();
    }

    // 读取处理
    @RequestMapping(value = "/downloadFile")
    @ResponseBody
    public void downloadFile(@RequestParam("id") String id, HttpServletRequest request, HttpServletResponse response) {
        List<MongoFileEntity> files = mongoFileService.getFilesFromFolder(id, "");
        if (files != null && files.size() > 0) {
            FileDownloadUtils.download(request, response, files.get(0).getInputstream(), files.get(0).getFileName());
        } else {
            ServletOutputStream os;
            try {
                os = response.getOutputStream();
                os.print("<script type='text/javascript'>alert('批量上传');</script>");
            } catch (IOException e) {
                logger.info(e.getMessage());
            }
        }
    }

    @RequestMapping(value = "/toZTDJXX")
    public String toZTDJXXPage(@RequestParam(value = "zch") String zch, Model model) {
        String smUuid = exchangeDataClientService.getSendMonitorByZch(zch);
        if (StringUtils.isNotBlank(smUuid)) {
            ExchangeDataDetailBean bean = exchangeDataClientService.getSendDetailByExDataUuid(smUuid);
            bean.setUuid(smUuid);
            List<Resource> btns = securityApiFacade.getDynamicButtonResourcesByCode("012");
            List<Resource> grantBtns = new ArrayList<Resource>();
            for (Resource resource : btns) {
                if (securityApiFacade.isGranted(resource.getCode())) {
                    grantBtns.add(resource);
                }
            }
            bean.setBtns(grantBtns);
            model.addAttribute("bean", bean);
        }
        return "pt/exchangedata/detail";
    }

    @RequestMapping(value = "/queryHistoryDate")
    public @ResponseBody
    Map queryHistoryDate(@RequestParam(value = "unitId") String unitId,
                         @RequestParam(value = "zch", required = false) String zch,
                         @RequestParam(value = "ztmc", required = false) String ztmc) {
        ShareRequest shareRequest = new ShareRequest();
        shareRequest.setTypeId(ExchangeConfig.TYPE_ID_SSXX_ZTDJ);
        shareRequest.setCurrentPage(1);
        shareRequest.setPageSize(100);
        shareRequest.setUnitId(unitId);
        List<Condition> conditions = new ArrayList<Condition>();
        Condition condition1 = new Condition();
        condition1.setKey("ZCH");
        condition1.setValue(zch);
        condition1.setOperator("~");
        conditions.add(condition1);
        Condition condition2 = new Condition();
        condition2.setKey("ZTMC");
        condition2.setValue(ztmc);
        condition2.setOperator("~");
        conditions.add(condition2);
        shareRequest.setConditions(conditions);
        ShareResponse srp = exchangeDataFlowService.query(shareRequest, "004140203");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("code", srp.getCode());
        map.put("msg", srp.getMsg());
        map.put("totalRow", srp.getTotalRow());
        List<String> xmlList = srp.getRecords();
        List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();
        if (xmlList != null) {
            for (String xmlStr : xmlList) {
                Map<String, String> dataMap = xmlToMap(xmlStr);
                dataList.add(dataMap);
            }
        }
        map.put("datalist", dataList);
        return map;
    }

    public Map<String, String> xmlToMap(String xmlStr) {
        Map<String, String> map = new HashMap<String, String>();
        try {
            Document document = DocumentHelper.parseText(xmlStr);
            // 获得根节点
            Element root = document.getRootElement();
            // 从XML的根结点开始遍历
            for (Iterator<?> i = root.elementIterator(); i.hasNext(); ) {
                Element elementOneLevel = (Element) i.next();
                String isAttachment = elementOneLevel.attributeValue("isAttachment");
                String isList = elementOneLevel.attributeValue("isList");
                String isExtend = elementOneLevel.attributeValue("isExtend");
                if (isList != null && isList.equals("1")) {// 从表
                } else if (isExtend != null && isExtend.equals("1")) {// 扩展
                } else {// 主表
                    String value_ = elementOneLevel.getTextTrim();
                    String key_ = elementOneLevel.getName();
                    if (isAttachment != null && (isAttachment.equals("1") || isAttachment.equals("2"))) {// 附件
                    } else {
                        map.put(key_, value_);
                    }
                }
            }
        } catch (DocumentException e) {
            // TODO Auto-generated catch block
            logger.info(e.getMessage());
        }
        return map;
    }
}
