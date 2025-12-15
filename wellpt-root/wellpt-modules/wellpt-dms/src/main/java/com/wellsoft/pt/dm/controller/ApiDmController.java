package com.wellsoft.pt.dm.controller;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.pt.basicdata.datastore.bean.DataStoreData;
import com.wellsoft.pt.basicdata.datastore.bean.DataStoreOrder;
import com.wellsoft.pt.basicdata.datastore.bean.DataStoreParams;
import com.wellsoft.pt.dm.controller.request.*;
import com.wellsoft.pt.dm.dto.DataModelDto;
import com.wellsoft.pt.dm.dto.DmQueryInfo;
import com.wellsoft.pt.dm.dto.DmViewPreviewQueryInfo;
import com.wellsoft.pt.dm.entity.DataMarkTypeEntity;
import com.wellsoft.pt.dm.entity.DataModelEntity;
import com.wellsoft.pt.dm.enums.DataUniqueType;
import com.wellsoft.pt.dm.factory.ddl.Table;
import com.wellsoft.pt.dm.jdbc.Model;
import com.wellsoft.pt.dm.jdbc.service.ModelService;
import com.wellsoft.pt.dm.service.DataMarkTypeService;
import com.wellsoft.pt.dm.service.DataModelService;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2023年04月06日   chenq	 Create
 * </pre>
 */
@RestController
@RequestMapping("/api/dm")
public class ApiDmController {

    @Resource(name = "sessionFactory")
    protected SessionFactory sessionFactory;
    @Autowired
    DataModelService dataModelService;
    @Autowired
    DataMarkTypeService dataMarkTypeService;
    @Autowired
    ModelService modelService;


    @ApiOperation(value = "保存数据模型对象", notes = "保存数据模型对象")
    @PostMapping("/save")
    public ApiResult<String> save(@RequestBody DataModelDto dataModelDto) {
        Long uuid = 0L;
        try {
            uuid = dataModelService.saveDataModel(dataModelDto);
        } catch (Exception e) {
            return ApiResult.fail(e.getMessage());
        }
        return ApiResult.success(uuid.toString());
    }

    @ApiOperation(value = "删除数据模型对象", notes = "删除数据模型对象")
    @GetMapping("/delete/{uuid}")
    public ApiResult<String> delete(@PathVariable Long uuid) {

        try {
            dataModelService.deleteDataModel(uuid);
        } catch (Exception e) {
            return ApiResult.fail(e.getMessage());
        }
        return ApiResult.success(uuid.toString());
    }

    @ApiOperation(value = "是否允许删除字段", notes = "是否允许删除字段")
    @GetMapping("/canDropColumn/{uuid}")
    public ApiResult<Boolean> canDropColumn(@PathVariable Long uuid, @RequestParam String column) {
        return ApiResult.success(dataModelService.canDropColumn(uuid, column));
    }

    @ApiOperation(value = "删除数据模型对象", notes = "删除数据模型对象")
    @GetMapping("/drop/{id}")
    public ApiResult<Boolean> drop(@PathVariable String id, @RequestParam(required = false, defaultValue = "false") Boolean force) {
        try {
            dataModelService.dropDataModel(id, force);
        } catch (Exception e) {
            return ApiResult.fail(e.getMessage());
        }
        return ApiResult.success(true);
    }

    @ApiOperation(value = "ID是否存在", notes = "ID是否存在")
    @GetMapping("/exist/{id}")
    public ApiResult<Boolean> existId(@PathVariable String id) {
        return ApiResult.success(dataModelService.getById(id) != null);
    }

    @ApiOperation(value = "保存或者更新存储数据", notes = "保存或者更新存储数据")
    @PostMapping("/saveOrUpdateModelData")
    public ApiResult<String> saveOrUpdateModelData(@RequestBody Model model) {
        return ApiResult.success(modelService.saveOrUpdate(model).toString());
    }

    @PostMapping("/saveOrUpdateByFormData")
    public ApiResult<Long> saveOrUpdateByFormData(@RequestBody ModelFormDataRequest dataRequest) {
        return ApiResult.success(dataModelService.saveOrUpdateByFormData(dataRequest));
    }

    @PostMapping("/saveAcl")
    public ApiResult<Boolean> saveAcl(@RequestBody AclDataRequest dataRequest) {
        dataModelService.saveAcl(dataRequest);
        return ApiResult.success(true);
    }

    @PostMapping("/getDataAcls")
    public ApiResult<List> getDataAcls(@RequestBody AclDataRequest dataRequest) {
        List<Long> dataUuids = Lists.newArrayList();
        for (AclDataRequest.AclData data : dataRequest.getDataList()) {
            dataUuids.add(data.getDataUuid());
        }
        return ApiResult.success(dataModelService.getDataAcls(dataRequest.getDataModelId(), dataUuids, true));
    }

    @ApiOperation(value = "保存存储数据为新版本", notes = "保存存储数据为新版本")
    @GetMapping("/saveDataModelDataAsNewVersion/{uuid}/{dataUuid}")
    public ApiResult<Long> saveDataModelDataAsNewVersion(@PathVariable Long uuid, @PathVariable Long dataUuid) {
        return ApiResult.success(dataModelService.saveDataModelDataAsNewVersion(uuid, dataUuid));
    }

    @ApiOperation(value = "删除表存储数据", notes = "删除表存储数据")
    @GetMapping("/deleteByUuid/{tableName}")
    public ApiResult<Boolean> deleteByUuid(@PathVariable String tableName, @RequestParam List uuid) {
        modelService.delete(tableName, uuid);
        return ApiResult.success(true);
    }

    @ApiOperation(value = "更新数据标记", notes = "更新数据标记")
    @PostMapping("/updateDataRelaMarker/{uuid}")
    public ApiResult<Boolean> updateDataRelaMarker(@PathVariable Long uuid, @RequestBody UpdateDataMarkRequest request) {
        dataModelService.updateDataRelaMarker(uuid, request.getDataUuids(), request.getType());
        return ApiResult.success(true);
    }

    @ApiOperation(value = "获取关联数据", notes = "获取关联数据")
    @PostMapping("/getDataRelaData/{uuid}")
    public ApiResult<List> getDataRelaData(@PathVariable Long uuid, @RequestBody UpdateDataRnRequest request) {
        List<Map<String, Object>> list = dataModelService.getDataRelaData(uuid, request.getDataUuids(), request.getType(), request.getRelaId());
        return ApiResult.success(list);
    }

    @ApiOperation(value = "获取数据标记", notes = "获取数据标记")
    @PostMapping("/getDataRelaMarker/{uuid}")
    public ApiResult<List> getDataRelaMarker(@PathVariable Long uuid, @RequestBody DataMarkQueryRequest request) {
        List<Map<String, Object>> list = dataModelService.getDataRelaMarker(uuid, request.getDataUuids(), request.getTypes());
        return ApiResult.success(list);
    }

    @ApiOperation(value = "更新数据关联关系", notes = "更新数据关联关系")
    @PostMapping("/updateDataRelaData/{uuid}")
    public ApiResult<Boolean> updateDataRelaData(@PathVariable Long uuid, @RequestBody UpdateDataRnRequest request) {
        dataModelService.updateDataRelaData(uuid, request.getDataUuids(), request.getRelaDataUuids(), request.getType(), request.getRelaId(), request.getOverride());
        return ApiResult.success(true);
    }

    @ApiOperation(value = "删除数据关联关系", notes = "删除数据关联关系")
    @PostMapping("/deleteDataRelaData/{uuid}")
    public ApiResult<Boolean> deleteDataRelaData(@PathVariable Long uuid, @RequestBody UpdateDataRnRequest request) {
        dataModelService.deleteDataRelaData(uuid, request.getDataUuids(), request.getType(), request.getRelaDataUuids(), request.getRelaId());
        return ApiResult.success(true);
    }

    @ApiOperation(value = "删除数据标记", notes = "删除数据标记")
    @PostMapping("/deleteDataRelaMarker/{uuid}")
    public ApiResult<Boolean> deleteDataRelaMarker(@PathVariable Long uuid, @RequestBody UpdateDataMarkRequest request) {
        dataModelService.deleteDataRelaMarker(uuid, request.getDataUuids(), Lists.newArrayList(request.getType()));
        return ApiResult.success(true);
    }


    @ApiOperation(value = "标记存储数据类型", notes = "标记存储数据类型")
    @GetMapping("/markDataType/{tableName}")
    public ApiResult<Boolean> updateByUuid(@PathVariable String tableName, @RequestParam(name = "uuid") List<String> uuid, @RequestParam(required = false) DataMarkTypeEntity.Type type) {
        if (type == null) {
            dataMarkTypeService.deleteMarkData(uuid, null);
        } else {
            dataMarkTypeService.markData(uuid, type);
        }
        return ApiResult.success(true);
    }


    @ApiOperation(value = "获取表模型对象", notes = "获取数据模型对象详情")
    @PostMapping("/getDataModelsByType")
    public ApiResult<List<DataModelEntity>> getDataModelTables(@RequestBody DataModelQueryRequest request) {
        return ApiResult.success(dataModelService.getByModuleAndTypes(request.getType(), request.getModule()));
    }

    @ApiOperation(value = "获取实体类作为数据模型", notes = "获取实体类作为数据模型")
    @GetMapping("/getDmExposedTableEntities")
    public ApiResult<List<Table>> getDmExposedTableEntities() {
        return ApiResult.success(dataModelService.getDmExposedTableEntities());
    }

    @ApiOperation(value = "获取实体类作为数据模型的列数据", notes = "获取实体类作为数据模型的列数据")
    @GetMapping("/getExposedColumns")
    public ApiResult<List<Table.Column>> getExposedColumns(@RequestParam String table) {
        return ApiResult.success(dataModelService.getTableColumns(table));
    }

    @ApiOperation(value = "获取实体类的数据库备注", notes = "获取实体类的数据库备注")
    @PostMapping("/getExposedTableComments")
    public ApiResult<List<Table>> getExposedTableComments(@RequestBody List<String> tables) {
        return ApiResult.success(dataModelService.getTableComments(tables));
    }

    @ApiOperation(value = "获取数据模型对象详情", notes = "获取数据模型对象详情")
    @GetMapping("/getDetails")
    public ApiResult<DataModelDto> getDetails(@RequestParam(required = false) Long uuid, @RequestParam(required = false) String id) {
        return ApiResult.success(uuid != null ? dataModelService.getDataModelDto(uuid) : (id != null ? dataModelService.getDataModelDto(id) : null));
    }


    @ApiOperation(value = "查询表数据", notes = "查询表数据")
    @PostMapping("/queryTableData/{tableName}")
    public ApiResult<Map<String, Object>> listTableData(@PathVariable String tableName, @RequestBody DmQueryInfo queryInfo) {
        DataStoreData data = new DataStoreData();
        Map<String, Object> result = Maps.newHashMap();
        List<String> orders = Lists.newArrayListWithCapacity(queryInfo.getOrders().size());
        for (DataStoreOrder o : queryInfo.getOrders()) {
            orders.add(o.getSortName() + " " + o.getSortOrder());
        }
        List<Map<String, Object>> queryItems = modelService.list(tableName, queryInfo.getPagingInfo(), orders.size() == 0 ? null : StringUtils.join(orders, " , "));
        result.put("data", queryItems);
        result.put("pagination", queryInfo.getPagingInfo());
        return ApiResult.success(result);
    }

    @ApiOperation(value = "根据条件加载表数据", notes = "根据条件加载表数据")
    @PostMapping("/loadData/{uuid}")
    public ApiResult<DataStoreData> loadData(@PathVariable Long uuid, @RequestBody DataStoreParams params) {
        return ApiResult.success(dataModelService.loadDataStoreData(uuid, params));
    }


    @ApiOperation(value = "根据条件加载表数据", notes = "根据条件加载表数据")
    @PostMapping("/loadDataById/{id}")
    public ApiResult<DataStoreData> loadDataById(@PathVariable String id, @RequestBody DataStoreParams params) {
        DataModelEntity modelEntity = dataModelService.getById(id);
        if (modelEntity != null) {
            return ApiResult.success(dataModelService.loadDataStoreData(modelEntity.getUuid(), params));
        }
        return ApiResult.success();
    }

    @ApiOperation(value = "根据条件表数据量", notes = "根据条件表数据量")
    @PostMapping("/loadDataCount/{uuid}")
    public ApiResult<Long> loadDataCount(@PathVariable Long uuid, @RequestBody DataStoreParams params) {
        return ApiResult.success(dataModelService.loadDataStoreDataCount(uuid, params));
    }


    @ApiOperation(value = "查询视图数据", notes = "查询视图数据")
    @PostMapping("/queryViewData/{id}")
    public ApiResult<Map<String, Object>> listViewData(@PathVariable String id, @RequestBody DmQueryInfo queryInfo) {
        Map<String, Object> result = Maps.newHashMap();
        List<Map<String, Object>> queryItems = dataModelService.queryViewDataById(id, queryInfo);
        result.put("data", queryItems);
        result.put("pagination", queryInfo.getPagingInfo());
        return ApiResult.success(result);
    }

    @ApiOperation(value = "预览查询视图数据", notes = "预览查询视图数据")
    @PostMapping("/previewQueryViewData")
    public ApiResult<Map<String, Object>> previewQueryViewData(@RequestBody DmViewPreviewQueryInfo queryInfo) {
        DataStoreData data = new DataStoreData();
        Map<String, Object> result = Maps.newHashMap();
        Map<String, Object> sqlParamValues = Maps.newHashMap();
        String sql = queryInfo.getSql();
//        if (StringUtils.isNotBlank(queryInfo.getSqlObj())) {
//            sql = JsonUtils.gson2Object(queryInfo.getSqlObj(), SqlQueryObj.class).toSqlString(sqlParamValues, true);
//        }
        List<Map<String, Object>> queryItems = dataModelService.queryViewDataBySql(sql, sqlParamValues, queryInfo.getPagingInfo());
        result.put("data", queryItems);
        result.put("pagination", queryInfo.getPagingInfo());
        return ApiResult.success(result);
    }

    @ApiOperation(value = "获取数据类型", notes = "获取数据类型")
    @GetMapping("/getDataTypes")
    public ApiResult<Map<String, DataMarkTypeEntity.Type>> getDataTypes(@RequestParam(required = false) List<String> dataUuid) {
        return ApiResult.success(dataMarkTypeService.getDataTypes(dataUuid));
    }

    @ApiOperation(value = "数据唯一性判断", notes = "数据唯一性判断")
    @PostMapping("/tableDataUnique/{uniqueType}/{tableName}")
    public ApiResult<Boolean> tableDataUnique(@PathVariable DataUniqueType uniqueType, @RequestBody Model model) {
        boolean unique = modelService.checkUnique(model, uniqueType);
        return ApiResult.success(unique);
    }

}
