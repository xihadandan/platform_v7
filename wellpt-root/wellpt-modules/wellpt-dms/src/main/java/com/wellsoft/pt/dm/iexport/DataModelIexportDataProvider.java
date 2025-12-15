package com.wellsoft.pt.dm.iexport;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.pt.basicdata.iexport.service.AbstractIexportDataProvider;
import com.wellsoft.pt.basicdata.iexport.suport.ExportTreeContextHolder;
import com.wellsoft.pt.basicdata.iexport.suport.IExportEntityStream;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import com.wellsoft.pt.dm.dto.DataModelDto;
import com.wellsoft.pt.dm.entity.DataModelDetailEntity;
import com.wellsoft.pt.dm.entity.DataModelEntity;
import com.wellsoft.pt.dm.service.DataModelService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Iterator;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2023年11月14日   chenq	 Create
 * </pre>
 */
@Service
@Transactional(readOnly = true)
public class DataModelIexportDataProvider extends AbstractIexportDataProvider<DataModelEntity, Long> {

    @Autowired
    private DataModelService dataModelService;

    @Override
    public String getType() {
        return IexportType.DataModel;
    }

    @Override
    public String getTreeName(DataModelEntity dataModelEntity) {
        return (dataModelEntity.getType().equals(DataModelEntity.Type.TABLE) ? "存储" : "视图") + "数据模型: " + dataModelEntity.getName();
    }

    @Override
    public TreeNode treeNode(Long uuid) {
        DataModelEntity entity = getEntity(uuid);
        if (entity != null) {
            TreeNode node = new TreeNode();
            node.setId(uuid.toString());
            node.setType(getType());
            node.setName(getTreeName(entity));
            // 如果是视图类的数据模型，要带出视图关联的数据模型
            DataModelEntity model = dataModelService.getOne(uuid);
            if (model.getType().equals(DataModelEntity.Type.VIEW)) {
                this.cascadeViewModelToNode(model.getId(), node.getChildren());

            }
            return node;
        }
        return null;
    }

    private void cascadeViewModelToNode(String id, List<TreeNode> children) {
        // 解析json
        DataModelDto dataModelDto = dataModelService.getDataModelDto(id);
        if (dataModelDto != null) {
            String json = dataModelDto.getModelJson();
            if (StringUtils.isNotBlank(json)) {
                JSONObject jsonObject = JSONObject.fromObject(json);
                if (jsonObject.has("cells")) {
                    JSONArray cells = jsonObject.getJSONArray("cells");
                    if (!cells.isEmpty()) {
                        Iterator<JSONObject> iterator = cells.iterator();
                        while (iterator.hasNext()) {
                            JSONObject obj = iterator.next();
                            if ("data-model-node".equals(obj.getString("shape"))) {
                                JSONObject data = obj.getJSONObject("data");
                                String category = data.getString("category");
                                if ("table".equals(category)) {
                                    if (data.has("type") && "VIEW".equals(data.getString("type"))) {
                                        // 视图
                                        DataModelEntity modelEntity = dataModelService.getById(data.getString("id"));
                                        if (modelEntity != null && ExportTreeContextHolder.add(modelEntity.getUuid().toString())) {
                                            TreeNode n = new TreeNode();
                                            n.setType(IexportType.DataModel);
                                            n.setId(modelEntity.getUuid().toString());
                                            n.setName("视图数据模型: " + modelEntity.getName());
                                            children.add(n);
                                            this.cascadeViewModelToNode(data.getString("id"), n.getChildren());
                                        }
                                    } else {
                                        // 表
                                        DataModelEntity modelEntity = dataModelService.getById(data.getString("id"));
                                        if (modelEntity != null && ExportTreeContextHolder.add(modelEntity.getUuid().toString())) {
                                            TreeNode n = new TreeNode();
                                            n.setType(IexportType.DataModel);
                                            n.setId(modelEntity.getUuid().toString());
                                            n.setName("存储数据模型: " + modelEntity.getName());
                                            children.add(n);
                                        }

                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    protected IExportEntityStream exportStream(Long uuid) {
        IExportEntityStream stream = super.exportStream(uuid);
        DataModelDetailEntity detail = dataModelService.getDetailByDataModelUuid(uuid);
        if (detail != null) {
            try {
                stream.getChildren().add(new IExportEntityStream("",
                        new IExportEntityStream.Metadata(objectMapper.writeValueAsString(detail), DataModelDetailEntity.class.getCanonicalName(), null, null)));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return stream;
    }

    @Override
    @Transactional
    public DataModelEntity saveEntityStream(IExportEntityStream stream) {
        DataModelDto old = dataModelService.getDataModelDto(Long.parseLong(stream.getMetadata().getUuid()));

        String oldColumnJson = old != null ? old.getColumnJson() : null;
        DataModelEntity dataModelEntity = super.saveEntityStream(stream);
        try {
            DataModelDetailEntity detailEntity = objectMapper.readValue(stream.getChildren().get(0).getMetadata().getData(), DataModelDetailEntity.class);
            // 生成表结构
            if (dataModelEntity.getType().equals(DataModelEntity.Type.TABLE)) {
                dataModelService.updateDataModelTableConstruct(oldColumnJson, detailEntity.getColumnJson(), old.getRemark(),
                        dataModelEntity.getUuid(), true,
                        true, true);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return dataModelEntity;
    }
}
