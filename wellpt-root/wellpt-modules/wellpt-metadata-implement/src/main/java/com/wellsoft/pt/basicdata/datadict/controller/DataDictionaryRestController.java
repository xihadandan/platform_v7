package com.wellsoft.pt.basicdata.datadict.controller;


import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.pt.basicdata.datadict.entity.DataDictionary;
import com.wellsoft.pt.basicdata.datadict.service.DataDictionaryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Api(tags = "字典模块")
@RestController
@RequestMapping("/api/dict")
public class DataDictionaryRestController {

    @Resource
    private DataDictionaryService dataDictionaryService;

    @DeleteMapping("/{uuid}")
    @ApiOperation(value = "根据uuid删除字典项", notes = "根据uuid删除字典项")
    public ApiResult quickDeleteDataDic(@PathVariable("uuid") String uuid) {
        dataDictionaryService.quickDeleteDataDic(uuid);
        return ApiResult.success();
    }

    @ApiOperation("根据字典类型查询数据字典")
    @GetMapping("/getDataDictionariesByType/{type}")
    public ApiResult getDataDictionariesByType(@PathVariable String type) {
        List<DataDictionary> dictionariesByType = dataDictionaryService.getDataDictionariesByType(type);
        return ApiResult.success(dictionariesByType);
    }


}
