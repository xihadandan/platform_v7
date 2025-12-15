package com.wellsoft.pt.dms.ext.excel.controller;

import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.dms.ext.excel.entity.ImpExcelDataBatchEntity;
import com.wellsoft.pt.dms.ext.excel.service.ImpExcelDataBatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2024年05月27日   chenq	 Create
 * </pre>
 */

@Controller
@RequestMapping("/api/imp/excel")
public class ImpExcelDataController extends BaseController {

    @Autowired
    ImpExcelDataBatchService impExcelDataBatchService;


    @GetMapping("/getBatchDetail")
    public ApiResult<ImpExcelDataBatchEntity> getImpBatch(@RequestParam Long uuid) {
        return ApiResult.success(impExcelDataBatchService.getBatchDetails(uuid));
    }

}
