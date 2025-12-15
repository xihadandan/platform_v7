package com.wellsoft.pt.basicdata.exceltemplate.web;

import com.wellsoft.context.component.jqgrid.JqGridQueryData;
import com.wellsoft.context.component.jqgrid.JqGridQueryInfo;
import com.wellsoft.context.util.file.FileDownloadUtils;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.basicdata.exceltemplate.dao.ExcelImportRuleDao;
import com.wellsoft.pt.basicdata.exceltemplate.entity.ExcelImportRule;
import com.wellsoft.pt.basicdata.exceltemplate.service.ExcelImportRuleService;
import com.wellsoft.pt.basicdata.facade.service.BasicDataApiFacade;
import com.wellsoft.pt.basicdata.systemtable.entity.SystemTable;
import com.wellsoft.pt.basicdata.systemtable.entity.SystemTableAttribute;
import com.wellsoft.pt.dyform.facade.dto.DyFormFormDefinition;
import com.wellsoft.pt.dyform.facade.dto.DyformFieldDefinition;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;
import com.wellsoft.pt.repository.service.MongoFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Description: Excel导入规则控制层
 *
 * @author zhouyq
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-4-22.1	zhouyq		2013-4-22		Create
 * </pre>
 * @date 2013-4-22
 */
@Controller
@RequestMapping("/basicdata/excelimportrule")
public class ExcelImportRuleController extends BaseController {

    private Logger logger = LoggerFactory.getLogger(ExcelImportRuleController.class);

    @Autowired
    private ExcelImportRuleService excelImportRuleService;
    @Autowired
    private BasicDataApiFacade basicDataApiFacade;
    @Autowired
    private DyFormFacade dyFormApiFacade;

    @Autowired
    private MongoFileService mongoFileService;

    /**
     * 跳转到Excel导入规则界面
     *
     * @return
     */
    @RequestMapping(value = "")
    public String excelImportRule() {
        return forward("/basicdata/excelimportrule/excelimportrule");
    }

    /**
     * 显示Excel导入规则列表
     *
     * @param queryInfo
     * @return
     */

    @RequestMapping(value = "/list")
    public @ResponseBody
    JqGridQueryData listAsJson(JqGridQueryInfo queryInfo) {
        JqGridQueryData queryData = excelImportRuleService.query(queryInfo);
        return queryData;
    }

    /**
     * 列对应列表
     *
     * @param queryInfo
     * @return
     */
    @RequestMapping(value = "/excelList")
    public @ResponseBody
    JqGridQueryData excelListAsJson(JqGridQueryInfo queryInfo) {
        JqGridQueryData queryData = excelImportRuleService.queryExcelList(queryInfo);
        return queryData;
    }

    /**
     * 解析excel测试 返回List<Map<String,Object>>
     *
     * @param uploadFile
     * @param id
     * @throws IOException
     */
    @RequestMapping(value = "/submitExcel3")
    public @ResponseBody
    JqGridQueryData uploadFile3(@RequestParam(value = "upload") MultipartFile uploadFile,
                                @RequestParam("id") String id) throws IOException {
        if (uploadFile.isEmpty()) {
            logger.info("上传文件为空！");
        } else {
            try {
                JqGridQueryData queryData = new JqGridQueryData();
                queryData.setRepeatitems(false);
                List<Map<String, Object>> list = excelImportRuleService.getListFromExcel(id,
                        uploadFile.getInputStream());
                queryData.setDataList(list);
                return queryData;
            } catch (Exception e) {
                // TODO Auto-generated catch block
                logger.error(e.getMessage(), e);
            }
        }
        return null;
    }

    /**
     * 获得所有的系统实体
     *
     * @return
     */
    @RequestMapping(value = "/getSelectEntity")
    public @ResponseBody
    Map<String, List<Map<String, Object>>> getSelectEntity() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        List<SystemTable> systemTableList = basicDataApiFacade.getAllSystemTables();
        for (SystemTable s : systemTableList) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("id", s.getUuid());
            map.put("name", s.getFullEntityName());
            map.put("cName", s.getChineseName());
            list.add(map);
        }
        Map<String, List<Map<String, Object>>> m = new HashMap<String, List<Map<String, Object>>>();
        m.put("data", list);
        return m;
    }

    /**
     * 获得实体的所有字段
     *
     * @return
     */
    @RequestMapping(value = "/getSelectEntityColumn", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Map<String, List<Map<String, Object>>> getSelectEntityColumn(@RequestParam("uuid") String uuid) {
        if (uuid != null && !uuid.equals("")) {
            List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
            List<SystemTableAttribute> systemTableAttributeList = basicDataApiFacade.getSystemTableColumns(uuid);
            for (SystemTableAttribute s : systemTableAttributeList) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("columnName", s.getAttributeName());
                list.add(map);
            }
            Map<String, List<Map<String, Object>>> m = new HashMap<String, List<Map<String, Object>>>();
            m.put("data", list);
            return m;
        } else {
            return null;
        }
    }

    /**
     * 获得所有的动态表单
     *
     * @return
     */
    @RequestMapping(value = "/getSelectForm")
    public @ResponseBody
    Map getSelectForm() {
        List list = new ArrayList();
        List<DyFormFormDefinition> formDefinitionList = dyFormApiFacade.getAllFormDefinitions(true);
        for (DyFormFormDefinition s : formDefinitionList) {/*
         * Map map = new
         * HashMap();
         * map.put("id",
         * s.getUuid());
         * map.put("name",
         * s.getUuid());
         * map.put("cName",
         * s
         * .getDisplayName()
         * ); list.add(map);
         */
        }
        Map m = new HashMap();
        m.put("data", list);
        return m;
    }

    /**
     * 获得动态表单的所有字段
     *
     * @return
     */
    @RequestMapping(value = "/getSelectFormColumn", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Map getSelectFormColumn(@RequestParam("uuid") String uuid) {
        if (uuid != null && !uuid.equals("")) {
            List list = new ArrayList();
            List<DyformFieldDefinition> systemTableAttributeList = dyFormApiFacade.getFormDefinition(uuid)
                    .doGetFieldDefintions();
            for (DyformFieldDefinition f : systemTableAttributeList) {
                Map map = new HashMap();
                map.put("columnName", f.getName());
                list.add(map);
            }
            Map m = new HashMap();
            m.put("data", list);
            return m;
        } else {
            return null;
        }
    }

    @RequestMapping(value = "/uploadExcel", method = RequestMethod.POST)
    @ResponseBody
    public void uploadExcel(@RequestParam(value = "upload") MultipartFile upload, HttpServletResponse response)
            throws IOException {
        String uuid = UUID.randomUUID().toString();
        // 上传处理
        mongoFileService.popAllFilesFromFolder(uuid);
        MongoFileEntity file = mongoFileService.saveFile(uuid, upload.getInputStream());
        mongoFileService.pushFileToFolder(uuid, file.getId(), "attach");

        response.setContentType(MediaType.TEXT_HTML_VALUE);
        response.getWriter().write(uuid);
        response.getWriter().flush();
        response.getWriter().close();
    }

    // 读取处理
    @RequestMapping(value = "/downloadImage")
    @ResponseBody
    public void downloadImage(@RequestParam("uuid") String uuid, HttpServletRequest request,
                              HttpServletResponse response) {
        ExcelImportRule excelImportRule = excelImportRuleService.getExcelImportRule(uuid);
        if (excelImportRule == null) {
            String msg = "<script type='text/javascript'>alert('【uuid为" + uuid + "的导入规则】不存在');</script>";
            logger.error(msg);
            outprint(msg, response);
            return;
        }
        if (excelImportRule.getFileUuid() != null && !"".equals(excelImportRule.getFileUuid())) {
            List<MongoFileEntity> files = ExcelImportRuleDao.getMongoFileEntityByFileUuid(mongoFileService,
                    excelImportRule.getFileUuid());//mongoFileService.getFilesFromFolder(excelImportRule.getFileUuid(), "attach");
            if (files == null || files.size() == 0) {
                String msg = "<script type='text/javascript'>alert('该规则未上传模版');</script>";
                outprint(msg, response);
                return;
            } else {
                MongoFileEntity mongoFileEntity = files.get(0);
                InputStream inputStream = mongoFileEntity.getInputstream();
                String fileName = excelImportRule.getName();
                FileDownloadUtils.download(request, response, inputStream, fileName + ".xls");
            }
        } else {
            String msg = "<script type='text/javascript'>alert('该规则未上传模版');</script>";
            outprint(msg, response);
        }
    }

    // 读取处理

    /**
     * 下载导入规则的模板附件
     *
     * @param id       导入规则的id
     * @param request
     * @param response
     */
    @RequestMapping(value = "/downloadImageById")
    @ResponseBody
    public void downloadImageById(@RequestParam("id") String id, HttpServletRequest request,
                                  HttpServletResponse response) {
        String uuid = null;
        ExcelImportRule rule = basicDataApiFacade.getExcelImportRuleById(id);
        if (rule == null) {
            String msg = "<script type='text/javascript'>alert('【ID为" + id + "的导入规则】不存在');</script>";
            logger.error(msg);
            outprint(msg, response);
            return;
        } else {
            uuid = rule.getUuid();
        }

        downloadImage(uuid, request, response);

    }

    private void outprint(String msg, HttpServletResponse response) {
        ServletOutputStream os = null;
        try {
            os = response.getOutputStream();
            response.setContentType(MediaType.TEXT_PLAIN_VALUE);
            if (null == msg) {
                os.write("<script type='text/javascript'>alert('该规则未上传模版');</script>".getBytes("utf-8"));
            } else {
                os.write(msg.getBytes("utf-8"));
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }

    /**
     * 获得excel导入规则模版选项
     *
     * @return
     */
    @RequestMapping(value = "/excelTempOption")
    @ResponseBody
    public String getExcelTempleteOption() {
        List<ExcelImportRule> excelImportRules = excelImportRuleService.getExcelImportRule();
        StringBuffer sb = new StringBuffer();
        for (ExcelImportRule excelImportRule : excelImportRules) {
            sb.append("<option value='" + excelImportRule.getCode() + "'>");
            sb.append(excelImportRule.getName());
            sb.append("</option>");
        }
        return sb.toString();
    }
}
