package com.wellsoft.pt.dm.service;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.pt.basicdata.datastore.bean.DataStoreData;
import com.wellsoft.pt.basicdata.datastore.bean.DataStoreParams;
import com.wellsoft.pt.basicdata.datastore.support.export.ExportParams;
import com.wellsoft.pt.dm.controller.request.AclDataRequest;
import com.wellsoft.pt.dm.controller.request.ModelFormDataRequest;
import com.wellsoft.pt.dm.dao.impl.DataModelDaoImpl;
import com.wellsoft.pt.dm.dto.DataModelDto;
import com.wellsoft.pt.dm.dto.DmQueryInfo;
import com.wellsoft.pt.dm.dto.TreeDataModelDataRequestParam;
import com.wellsoft.pt.dm.entity.DataModelDetailEntity;
import com.wellsoft.pt.dm.entity.DataModelEntity;
import com.wellsoft.pt.dm.enums.MarkType;
import com.wellsoft.pt.dm.factory.ddl.Table;
import com.wellsoft.pt.jpa.service.JpaService;

import javax.activation.DataSource;
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
public interface DataModelService extends JpaService<DataModelEntity, DataModelDaoImpl, Long> {

    Long saveDataModel(DataModelDto dto);

    DataModelDto getDataModelDto(Long uuid);

    DataModelDto getDataModelDto(String id);

    DataModelEntity getById(String id);


    List<DataModelEntity> getByType(DataModelEntity.Type type, String module);

    List<Map<String, Object>> queryViewDataById(String id, DmQueryInfo queryInfo);

    List<Table> getDmExposedTableEntities();

    List<Table.Column> getTableColumns(String tableName);


    DataStoreData loadDataStoreData(Long uuid, DataStoreParams params);

    DataSource exportData(Long uuid, String type, ExportParams exportParams);

    public List<TreeNode> loadTreeNodes(final TreeDataModelDataRequestParam param);

    long loadDataStoreDataCount(Long uuid, DataStoreParams params);

    void updateDataRelaMarker(Long uuid, List<Long> dataUuids, MarkType type);

    void deleteDataRelaMarker(Long uuid, List<Long> dataUuids, List<MarkType> type);

    List<Map<String, Object>> getDataRelaMarker(Long uuid, List<Long> dataUuids, List<MarkType> type);

    void updateDataRelaData(Long uuid, List<Long> dataUuids, List<Long> relaDataUuids, MarkType type, String relaId, Boolean override);

    void deleteDataRelaData(Long uuid, List<Long> dataUuids, MarkType type, List<Long> relaDataUuids, String relaId);

    List<Map<String, Object>> getDataRelaData(Long uuid, List<Long> dataUuids, MarkType type, String relaId);

    Long saveDataModelDataAsNewVersion(Long uuid, Long dataUuid);

    Long saveOrUpdateByFormData(ModelFormDataRequest dataRequest);

    void saveAcl(AclDataRequest dataRequest);


    List<Map<String, Object>> getDataAcls(String dataModelId, List<Long> dataUuids, boolean filterExpire);

    void deleteDataModel(Long uuid);

    void dropDataModel(String id, Boolean force);

    DataModelDetailEntity getDetailByDataModelUuid(Long uuid);


    void updateDataModelTableConstruct(String oldColumnJson, String columnJson, String oldRemark,
                                       Long uuid, Boolean createMainTable, Boolean creatFormRl, Boolean createDmRl);

    List<Map<String, Object>> queryViewDataBySql(String sql, Map<String, Object> sqlParameter, PagingInfo pagingInfo);

    Boolean canDropColumn(Long uuid, String column);

    List<Table> getTableComments(List<String> tables);


    List<DataModelEntity> getByModuleAndTypes(List<DataModelEntity.Type> type, List<String> module);
}
