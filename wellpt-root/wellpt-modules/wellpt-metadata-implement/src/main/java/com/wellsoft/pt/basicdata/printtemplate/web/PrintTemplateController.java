package com.wellsoft.pt.basicdata.printtemplate.web;

import com.wellsoft.context.component.jqgrid.JqGridQueryController;
import com.wellsoft.context.component.jqgrid.JqGridQueryData;
import com.wellsoft.context.component.jqgrid.JqGridQueryInfo;
import com.wellsoft.context.enums.Encoding;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.jdbc.support.QueryData;
import com.wellsoft.context.jdbc.support.QueryInfo;
import com.wellsoft.context.util.PrinttemplateDateUtil;
import com.wellsoft.context.util.date.DateUtil;
import com.wellsoft.context.util.file.FileDownloadUtils;
import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.ResultMessage;
import com.wellsoft.pt.basicdata.printtemplate.entity.PrintContents;
import com.wellsoft.pt.basicdata.printtemplate.entity.PrintTemplate;
import com.wellsoft.pt.basicdata.printtemplate.service.PrintTemplateService;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;
import com.wellsoft.pt.repository.service.MongoFileService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.beans.IntrospectionException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Description:  打印模板控制层
 *
 * @author zhouyq
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-3-20.1	zhouyq		2013-3-20		Create
 * </pre>
 * @date 2013-3-20
 */
@Controller
@RequestMapping("/basicdata/printtemplate")
public class PrintTemplateController extends JqGridQueryController {

    private Logger logger = LoggerFactory.getLogger(PrintTemplateController.class);

    @Autowired
    private PrintTemplateService printTemplateService;

    @Autowired
    private MongoFileService mongoFileService;

    @Autowired
    private DyFormFacade dyFormApiFacade;

    @RequestMapping(value = "/list/tree")
    public @ResponseBody
    JqGridQueryData listAsTreeJson(HttpServletRequest request, JqGridQueryInfo jqGridQueryInfo) {
        QueryInfo queryInfo = buildQueryInfo(jqGridQueryInfo, request);
        QueryData queryData = printTemplateService.getForPageAsTree(jqGridQueryInfo, queryInfo);
        return convertToJqGridQueryData(queryData);
    }

    /**
     * 上传模板文件
     *
     * @param uploadFile
     * @param uuid
     * @throws IOException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws IntrospectionException
     */
    @RequestMapping(value = "/uploadFile")
    @ResponseBody
    public void uploadFile(@RequestParam(value = "uploadFile") MultipartFile uploadFile,
                           @RequestParam("uuid") String uuid) throws IOException, IntrospectionException, IllegalAccessException,
            InvocationTargetException {
        // System.out.println("uuid:" + uuid);
        PrintTemplate printTemplate = printTemplateService.getByUuid(uuid);
        if (uploadFile.isEmpty()) {
            // System.out.println("上传文件为空！");
        } else {
            if (StringUtils.isNotBlank(uuid)) {
                mongoFileService.popAllFilesFromFolder(uuid);
                MongoFileEntity file = mongoFileService.saveFile(uuid, uploadFile.getOriginalFilename(),
                        uploadFile.getInputStream());
                mongoFileService.pushFileToFolder(uuid, file.getId(), "attach");
                // System.out.println("删除旧模板，开始上传新模板！");
                // System.out.println("上传文件不为空！开始上传");
            }
            printTemplate.setFileUuid(uuid);
            printTemplateService.save(printTemplate);
            // System.out.println("上传成功！");
        }
    }

    /**
     * 下载打印模板定义
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.printtemplate.service.PrintTemplateService#download(java.lang.String)
     */
    @RequestMapping(value = "/download")
    public void download(@RequestParam("downloaduuid") String downloaduuid, HttpServletRequest request,
                         HttpServletResponse response) {
        logger.debug("开始下载打印模板的模板文件:" + downloaduuid);
        PrintTemplate printTemplate = printTemplateService.getByUuid(downloaduuid);
        if (printTemplate == null) {
            logger.error("打印模板不存在:" + downloaduuid);
            return;
        }
        InputStream inputStream = null;
        MongoFileEntity mongoFileEntity = mongoFileService.getFile(printTemplate.getFileUuid());
        if (mongoFileEntity != null) {
            logger.debug("开始输出打印模板(" + printTemplate.getName() + ")文件流" + mongoFileEntity.getFileName());
            try {
                inputStream = mongoFileEntity.getInputstream();
                String fileName = printTemplate.getName();
                if (printTemplate.doIsTemplateFileTypeAsWord()) {
                    fileName += ".doc";
                } else if (printTemplate.doIsTemplateFileTypeAsHtml()) {
                    fileName += ".html";
                } else if (printTemplate.doIsTemplateFileTypeAsWordXml()
                        || printTemplate.doIsTemplateFileTypeAsWordXmlByComment()) {
                    fileName += ".xml";
                }
                FileDownloadUtils.download(request, response, inputStream, fileName);
            } catch (Exception e) {
                logger.error("打印模板(" + printTemplate.getName() + ")下载异常: " + e.getMessage(), e);
            } finally {
                if (inputStream != null) {
                    IOUtils.closeQuietly(inputStream);
                }
            }

        } else {
            logger.error("获取打印模板文件流为空，打印模板uuid为：》》》》》》》》》》》》》》" + downloaduuid);
        }
    }

    /**
     * 跳转到打印模板界面
     *
     * @return
     */
    @RequestMapping(value = "")
    public String printTemplate() {
        return forward("/basicdata/printtemplate/printtemplate");
    }

    /**
     * 获取打印模板列表
     */
    @RequestMapping(value = "/listByIds")
    public @ResponseBody
    ApiResult<List<PrintTemplate>> listByIds(@RequestParam(value = "printTemplateIds", required = false) String printTemplateIds) {
        List<PrintTemplate> printTemplateList = printTemplateService.getPrintTemplateByIds(printTemplateIds);
        return ApiResult.success(printTemplateList);
    }

    /**
     * 显示打印模板列表
     *
     * @param queryInfo
     * @return
     */

    @RequestMapping(value = "/list")
    public @ResponseBody
    JqGridQueryData listAsJson(JqGridQueryInfo queryInfo) {
        JqGridQueryData queryData = printTemplateService.query(queryInfo);
        return queryData;
    }

    @RequestMapping(value = "/getPrintTempalteLangs")
    public @ResponseBody
    ResultMessage getPrintTempalteLangs(@RequestParam("printtemplateId") String printtemplateId,
                                        @RequestParam(value = "printtemplateUuid", required = false) String printtemplateUuid,
                                        @RequestParam(value = "printtemplateLang", required = false) String printtemplateLang,
                                        HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<PrintContents> datas = printTemplateService.getPrintTempalteLangs(printtemplateId, printtemplateUuid,
                printtemplateLang);
        return new ResultMessage("success", true, datas);
    }

    /**
     * 获取打印文件流
     *
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/print")
    public void getPrintTemplateInputStream(@RequestParam("printtemplateId") String printtemplateId,
                                            @RequestParam("formUuid") String formUuid, @RequestParam("dataUuid") String dataUuid,
                                            @RequestParam(value = "printtemplateUuid", required = false) String printtemplateUuid,
                                            @RequestParam(value = "printtemplateLang", required = false) String printtemplateLang,
                                            HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintTemplate printTemplate = printTemplateService.getPrintTemplateById(printtemplateId);
        Map<String, Object> dytableMap = new HashMap<String, Object>();
        List<IdEntity> allEntities = new ArrayList<IdEntity>();

        DyFormData dyFormData = dyFormApiFacade.getDyFormData(formUuid, dataUuid);
        Map<String, Object> mainDyFormDataMap = dyFormData.getFormDataOfMainform();// 主表数据
        Map<String, List<Map<String, Object>>> dyFormDataShowMap = dyFormData.getDisplayValuesKeyAsFormId();// 表单对应的所有字段显示值（包含主表和从表）

        dytableMap.putAll(mainDyFormDataMap);
        if (printTemplate.doIsTemplateFileTypeAsWord() || printTemplate.doIsTemplateFileTypeAsWordXml()
                || printTemplate.doIsTemplateFileTypeAsWordXmlByComment() || printTemplate.doIsTemplateFileTypeAsWordPoi()) {
            dytableMap.putAll(PrinttemplateDateUtil.getPrinttemplateMap(dyFormData.getFormId(), dyFormDataShowMap));
        } else {
            dytableMap.putAll(PrinttemplateDateUtil.getPrinttemplateMap(dyFormData.getFormId(), dyFormDataShowMap,
                    false));
        }
        InputStream localInputStream = null;
        try {
            localInputStream = printTemplateService.getPrintTemplateInputStream(printtemplateId, printtemplateUuid,
                    printtemplateLang, allEntities, dytableMap, new File(""));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        String templateType = printTemplate.getTemplateType();
        if (PrintTemplate.TEMPLATE_TYPE_HTML.equals(templateType)) {
            response.setContentType(MediaType.TEXT_HTML_VALUE);
            response.setCharacterEncoding(Encoding.UTF8.getValue());
            Writer writer = response.getWriter();
            writer.write(IOUtils.toString(localInputStream));
            writer.flush();
            writer.close();
        } else {
            FileDownloadUtils.download(request, response, localInputStream,
                    DateUtil.getFormatDate(new Date(), "yyyyMMddHHmmss") + ".doc");
        }

    }
}
