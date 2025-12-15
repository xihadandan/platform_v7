package com.wellsoft.pt.dms.web;

import com.wellsoft.context.util.excel.ExcelImportDataReport;
import com.wellsoft.context.util.excel.ExcelImportRule;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.pt.dms.facade.service.DmsDataExportService;
import com.wellsoft.pt.dms.facade.service.DmsDataImportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2024年06月14日   chenq	 Create
 * </pre>
 */
@RestController
@RequestMapping({"/api/dms/excel"})
public class DmsExcelImportController {

    private Logger logger = LoggerFactory.getLogger(DmsExcelImportController.class);

    @Autowired
    DmsDataImportService dmsDataImportService;

    @Autowired
    DmsDataExportService dmsDataExportService;

    @PostMapping("/importData")
    public ApiResult<ExcelImportDataReport> importData(@RequestPart("file") MultipartFile multifile,
                                                       @RequestParam(value = "importService") String importService,
                                                       @RequestParam(value = "rule") String rule) {
        ExcelImportRule excelImportRule = JsonUtils.gson2Object(rule, ExcelImportRule.class);
        try {
            ExcelImportDataReport report = dmsDataImportService.importByListener(multifile.getInputStream(), importService, excelImportRule);
            return ApiResult.success(report);
        } catch (Exception e) {
            logger.error("导入失败", e);
            return ApiResult.fail("导入失败");
        }
    }

    @PostMapping("/exportSubformData")
    public ApiResult<ExcelImportDataReport> exportSubformData(
            @RequestBody Map<String, Object> params
    ) {
        return ApiResult.success();
    }
}
