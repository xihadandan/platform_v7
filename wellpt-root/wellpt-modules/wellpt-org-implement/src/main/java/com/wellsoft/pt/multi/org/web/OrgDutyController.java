/*
 * @(#)2015-10-10 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.web;

import com.google.common.collect.Lists;
import com.wellsoft.context.util.date.DateUtil;
import com.wellsoft.context.util.excel.ExcelUtils;
import com.wellsoft.context.util.file.FileDownloadUtils;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.multi.org.entity.MultiOrgDuty;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgService;
import com.wellsoft.pt.multi.org.service.MultiOrgDutyService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zyguo
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-1-14.1	zyguo		2015-10-10		Create
 * </pre>
 * @date 2015-10-10
 */
@Controller
@RequestMapping(value = "/multi/org/duty")
public class OrgDutyController extends BaseController {

    @Autowired
    private MultiOrgService multiOrgService;
    @Autowired
    private MultiOrgDutyService multiOrgDutyService;

    /**
     * 打开列表界面
     *
     * @return
     */
    @RequestMapping(value = "/list")
    public String list(Model model) {
        return "/multi/org/org_duty_list";
    }

    @RequestMapping(value = "/import", method = RequestMethod.POST)
    @ResponseBody
    public String importDuty(@RequestParam(value = "upload") MultipartFile excelFile,
                             @RequestParam(value = "fun", required = false) String fun,
                             HttpServletResponse response)
            throws IOException {
        String msg = this.getReturnMsg(excelFile);
        if (StringUtils.isNotBlank(fun)) {
            fun = HtmlUtils.htmlUnescape(fun);
            fun = fun.replace("{data}", msg);
            return fun;
        }
        return msg;
    }

    private String getReturnMsg(MultipartFile excelFile) throws IOException {
        String msg;
        Sheet sheet = ExcelUtils.getSheetFromInputStream(excelFile.getInputStream(), "职务");
        if (sheet != null) {
            int maxRow = sheet.getLastRowNum();
            int totalNum = 0;
            int newNum = 0;
            int modifyNum = 0;
            String systemUnitId = SpringSecurityUtils.getCurrentUserUnitId();
            // 第一行是标题，所以是从第二行开始读取
            for (int rowNum = 1; rowNum <= maxRow; rowNum++) {
                Row row = sheet.getRow(rowNum);
                if (row != null) {
                    totalNum++;
                    DecimalFormat df = new DecimalFormat("0");
                    String spaCode = null;
                    try {
                        spaCode = row.getCell(0).getStringCellValue();
                    } catch (Exception e) {
                        spaCode = row.getCell(0) == null ? null : df.format(row.getCell(0).getNumericCellValue());
                    }
                    String name = row.getCell(1).getStringCellValue();
                    // sapCode
                    MultiOrgDuty duty = multiOrgDutyService.getByName(name, systemUnitId);
                    if (duty == null) {
                        newNum++;
                        duty = new MultiOrgDuty();
                        duty.setName(name);
                        duty.setCode(spaCode);
                        duty.setSapCode(spaCode);
                        duty.setSystemUnitId(systemUnitId);
                        this.multiOrgService.addDuty(duty);
                    } else {
                        modifyNum++;
                        duty.setSapCode(spaCode);
                        duty.setCode(spaCode);
                        this.multiOrgService.modifyDuty(duty);
                    }
                }
            }
            msg = "选择的职务数据共【" + totalNum + "】行，成功更新【" + modifyNum + "】行，成功导入【" + newNum + "】行";
        } else {
            msg = "上传的excel解析失败，请检查文件。";
        }
        return msg;
    }

    @RequestMapping(value = "/export", method = RequestMethod.POST)
    public void export(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String fileName = "职务_" + DateUtil.getFormatDate(new Date(), "yyyy-MM-dd") + ".xls";
        String systemUnitId = SpringSecurityUtils.getCurrentUserUnitId();
        String[] titles = new String[]{"职务编号", "职务名称"};
        List<MultiOrgDuty> dutys = this.multiOrgDutyService.queryAllDutyBySystemUnitId(systemUnitId);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            List<String[]> dataList = Lists.newArrayList();
            if (dutys != null) {
                for (MultiOrgDuty d : dutys) {
                    String[] data = new String[]{d.getCode(), d.getName()};
                    dataList.add(data);
                }
            }
            HSSFWorkbook excel = ExcelUtils.generateExcelBook(dataList, titles);
            excel.write(os);
            FileDownloadUtils.download(request, response, new ByteArrayInputStream(os.toByteArray()), fileName);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        } finally {
            os.close();
        }
    }
}
