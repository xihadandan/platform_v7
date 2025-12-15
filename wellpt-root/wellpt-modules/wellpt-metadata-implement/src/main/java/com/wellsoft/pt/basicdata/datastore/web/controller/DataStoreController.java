package com.wellsoft.pt.basicdata.datastore.web.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.basicdata.datastore.bean.CdDataStoreColumnBean;
import com.wellsoft.pt.basicdata.datastore.bean.CdDataStoreDefinitionBean;
import com.wellsoft.pt.basicdata.datastore.bean.DataStoreData;
import com.wellsoft.pt.basicdata.datastore.bean.DataStoreParams;
import com.wellsoft.pt.basicdata.datastore.dto.InterfaceParamDto;
import com.wellsoft.pt.basicdata.datastore.facade.service.CdDataStoreService;
import com.wellsoft.pt.basicdata.datastore.service.CdDataStoreDefinitionService;
import com.wellsoft.pt.basicdata.datastore.support.export.DataStoreExport;
import com.wellsoft.pt.basicdata.datastore.support.export.DataStoreExportFactory;
import com.wellsoft.pt.jpa.datasource.SelectDatasource;
import io.swagger.annotations.*;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Description:
 * 数据仓库restful控制类
 * <pre>
 * 修改记录:
 * 修改后版本    修改人     修改日期    修改内容
 * 1.0         baozh    2021/11/1   Create
 * </pre>
 */
@Api(tags = "数据仓库")
@RestController
@RequestMapping("/api/datastore")
public class DataStoreController extends BaseController {

    @Autowired
    CdDataStoreDefinitionService dataStoreDefinitionService;

    @Autowired
    CdDataStoreService dataStoreService;

    @Autowired
    DataStoreExportFactory dataStoreExportFactory;


    @ApiOperation(value = "根据数据仓库Id获取数据仓库实体", notes = "根据数据仓库Id获取数据仓库实体")
    @GetMapping("/getBeanById/{id}")
    @ApiImplicitParam(name = "id", value = "数据仓库Id", paramType = "query", dataType = "String", required = true)
    @ApiOperationSupport(order = 10)
    public ApiResult<CdDataStoreDefinitionBean> getBeanById(@PathVariable String id) {
        return ApiResult.success(dataStoreDefinitionService.getBeanById(id));
    }

    @ApiOperation(value = "保存数据仓库", notes = "保存数据仓库")
    @ApiOperationSupport(order = 20)
    @PostMapping("/saveBean")
    public ApiResult saveBean(@RequestBody CdDataStoreDefinitionBean bean) {
        return ApiResult.success(dataStoreDefinitionService.saveBean(bean));
    }

    @ApiOperation(value = "根据表名称重载列数据", notes = "根据表名称重载列数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tableName", value = "数据仓库表名称", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "camelColumnIndex", value = "是否使用驼峰风格列索引获取所有列", paramType = "query", dataType = "Boolean", required = false)
    })
    @ApiOperationSupport(order = 30)
    @GetMapping("/loadTableColumns/{tableName}")
    public ApiResult<List<CdDataStoreColumnBean>> loadTableColumns(@PathVariable String tableName, @RequestParam(required = false) Boolean camelColumnIndex
            , @RequestParam(required = false) Long dbLinkConfUuid) {
        if (camelColumnIndex == null) {
            return ApiResult.success(dataStoreDefinitionService.loadTableColumns(tableName));
        }
        return ApiResult.success(dataStoreDefinitionService.loadTableColumns(tableName, camelColumnIndex, dbLinkConfUuid));
    }

    @ApiOperation(value = "根据视图名称重载列数据", notes = "根据视图名称重载列数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "viewName", value = "数据视图名称", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "camelColumnIndex", value = "是否使用驼峰风格列索引获取所有列", paramType = "query", dataType = "Boolean", required = false)
    })
    @ApiOperationSupport(order = 40)
    @GetMapping("/loadViewColumns/{viewName}")
    public ApiResult<List<CdDataStoreColumnBean>> loadViewColumns(@PathVariable String viewName, @RequestParam(required = false) Boolean camelColumnIndex, @RequestParam(required = false) Long dbLinkConfUuid) {
        if (camelColumnIndex == null) {
            return ApiResult.success(dataStoreDefinitionService.loadViewColumns(viewName));
        }
        return ApiResult.success(dataStoreDefinitionService.loadViewColumns(viewName, camelColumnIndex, dbLinkConfUuid));
    }

    @ApiOperation(value = "根据实体名称重载列数据", notes = "根据实体名称重载列数据")
    @ApiImplicitParam(name = "entityName", value = "实体名称", paramType = "query", dataType = "String", required = true)
    @ApiOperationSupport(order = 50)
    @GetMapping("/loadEntityColumns")
    public ApiResult<List<CdDataStoreColumnBean>> loadEntityColumns(@RequestParam String entityName) {
        return ApiResult.success(dataStoreDefinitionService.loadEntityColumns(entityName));
    }

    @ApiOperation(value = "根据SQL重载列数据", notes = "根据SQL重载列数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sqlStatement", value = "SQL语句", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "camelColumnIndex", value = "是否使用驼峰风格列索引获取所有列", paramType = "query", dataType = "Boolean", required = false)
    })
    @ApiOperationSupport(order = 60)
    @RequestMapping(value = "/loadSqlColumns", method = {RequestMethod.GET, RequestMethod.POST})
    public ApiResult<List<CdDataStoreColumnBean>> loadSqlColumns(@RequestParam String sqlStatement,
                                                                 @RequestParam(required = false) Boolean camelColumnIndex,
                                                                 @RequestParam(required = false) Long dbLinkConfUuid,
                                                                 @RequestBody Map<String, Object> requestMap) {
        String sql = sqlStatement;
        if (MapUtils.isNotEmpty(requestMap) && requestMap.containsKey("sqlStatement")) {
            sql = Objects.toString(requestMap.get("sqlStatement"));
        }
        if (camelColumnIndex == null) {
            return ApiResult.success(dataStoreDefinitionService.loadSqlColumns(sql));
        }
        return ApiResult.success(dataStoreDefinitionService.loadSqlColumns(sql, camelColumnIndex, dbLinkConfUuid));
    }


    @ApiOperation(value = "根据SQL名称重载列数据", notes = "根据SQL名称重载列数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sqlName", value = "SQL名称", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "camelColumnIndex", value = "是否使用驼峰风格列索引获取所有列", paramType = "query", dataType = "Boolean", required = false)
    })
    @ApiOperationSupport(order = 70)
    @GetMapping("/loadSqlNameColumns/{sqlName}")
    public ApiResult<List<CdDataStoreColumnBean>> loadSqlNameColumns(@PathVariable String sqlName, @RequestParam(required = false) Boolean camelColumnIndex, @RequestParam(required = false) Long dbLinkConfUuid) {
        if (camelColumnIndex == null) {
            return ApiResult.success(dataStoreDefinitionService.loadSqlNameColumns(sqlName));
        }
        return ApiResult.success(dataStoreDefinitionService.loadSqlNameColumns(sqlName, camelColumnIndex, dbLinkConfUuid));
    }

    @ApiOperation(value = "根据接口名称重载列数据", notes = "根据接口名称重载列数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "interfaceName", value = "接口名称", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "interfaceParam", value = "接口参数", paramType = "query", dataType = "String", required = true)
    })
    @ApiOperationSupport(order = 80)
    @GetMapping("/loadDataInterfaceColumns")
    public ApiResult<List<CdDataStoreColumnBean>> loadDataInterfaceColumns(@RequestParam String interfaceName, @RequestParam String interfaceParam) {
        return ApiResult.success(dataStoreDefinitionService.loadDataInterfaceColumns(interfaceName, interfaceParam));
    }

    @ApiOperation(value = "根据接口名称重载列数据", notes = "根据接口名称重载列数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "interfaceName", value = "接口名称", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "interfaceParam", value = "接口参数", paramType = "query", dataType = "String", required = true)
    })
    @ApiOperationSupport(order = 80)
    @PostMapping("/postLoadDataInterfaceColumns")
    public ApiResult<List<CdDataStoreColumnBean>> postLoadDataInterfaceColumns(@RequestBody CdDataStoreDefinitionBean bean) {
        return ApiResult.success(dataStoreDefinitionService.loadDataInterfaceColumns(bean.getDataInterfaceName(), bean.getDataInterfaceParam()));
    }

    @ApiOperation(value = "根据接口名称查询接口信息", notes = "根据接口名称查询接口信息")
    @ApiImplicitParam(name = "interfaceName", value = "接口名称", paramType = "query", dataType = "String", required = false)
    @ApiOperationSupport(order = 90)
    @GetMapping("/getQueryInterfaceParams")
    public ApiResult<List<InterfaceParamDto>> getQueryInterfaceParams(@RequestParam String interfaceName) {
        List<Map<String, String>> params = dataStoreService.getQueryInterfaceParams(interfaceName);
        List<InterfaceParamDto> collect = null;
        if (params != null) {
            collect = params.stream().map(param -> {
                InterfaceParamDto dto = JSONObject.toJavaObject((JSON) JSONObject.toJSON(param), InterfaceParamDto.class);
                return dto;
            }).collect(Collectors.toList());
        }
        return ApiResult.success(collect);
    }


    @ApiOperation(value = "根据接口名称查询接口描述", notes = "根据接口名称查询接口描述")
    @ApiImplicitParam(name = "interfaceName", value = "接口名称", paramType = "query", dataType = "String", required = false)
    @ApiOperationSupport(order = 100)
    @GetMapping("/getInterfaceDesc")
    public ApiResult<String> getInterfaceDesc(@RequestParam String interfaceName) {
        return ApiResult.success(dataStoreDefinitionService.getInterfaceDesc(interfaceName));
    }

    @ApiOperation(value = "根据uuid删除", notes = "根据uuid删除")
    @ApiImplicitParam(name = "uuids", value = "仓库uuid", paramType = "query", dataType = "String[]", required = true)
    @ApiOperationSupport(order = 110)
    @PostMapping("/deleteByUuids")
    public ApiResult deleteByUuids(@RequestBody String[] uuids) {
        dataStoreDefinitionService.deleteByUuids(uuids);
        return ApiResult.success();
    }

    @ApiOperation(value = "根据条件加载表数据", notes = "根据条件加载表数据")
    @ApiOperationSupport(order = 120)
    @PostMapping("/loadData")
    @SelectDatasource
    public ApiResult<DataStoreData> loadData(@RequestBody DataStoreParams params) {
        return ApiResult.success(dataStoreService.loadData(params));
    }

    @ApiOperation(value = "根据条件加载表数量", notes = "根据条件加载表数量")
    @ApiOperationSupport(order = 120)
    @PostMapping("/loadCount")
    @SelectDatasource
    public ApiResult<Long> loadCount(@RequestBody DataStoreParams params) {
        return ApiResult.success(dataStoreService.loadCount(params));
    }

    @GetMapping("/getDataSourceExportServiceSelection")
    public ApiResult<Select2QueryData> getDataSourceExportServiceSelection() {
        List<DataStoreExport> exports = dataStoreExportFactory.getAllDataStoreExport();
        return ApiResult.success(dataStoreExportFactory.getSelectData(null));
    }


}
