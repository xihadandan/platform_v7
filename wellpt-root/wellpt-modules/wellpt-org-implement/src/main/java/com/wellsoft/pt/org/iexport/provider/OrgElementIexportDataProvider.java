package com.wellsoft.pt.org.iexport.provider;

import com.google.common.collect.Lists;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.basicdata.iexport.service.AbstractIexportDataProvider;
import com.wellsoft.pt.basicdata.iexport.suport.ExportTreeContextHolder;
import com.wellsoft.pt.basicdata.iexport.suport.IExportTable;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import com.wellsoft.pt.org.entity.OrgElementEntity;
import com.wellsoft.pt.org.entity.OrgElementModelEntity;
import com.wellsoft.pt.org.service.OrgElementModelService;
import com.wellsoft.pt.org.service.OrgElementService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2025年04月19日   chenq	 Create
 * </pre>
 */
@Service
@Transactional(readOnly = true)
public class OrgElementIexportDataProvider extends AbstractIexportDataProvider<OrgElementEntity, Long> {
    @Autowired
    OrgElementService orgElementService;

    @Autowired
    OrgElementModelService elementModelService;

    @Override
    public String getType() {
        return IexportType.OrgElement;
    }

    @Override
    public String getTreeName(OrgElementEntity entity) {
        OrgElementModelEntity modelEntity = elementModelService.getOrgElementModelByIdAndSystem(entity.getType(), entity.getSystem());
        if (modelEntity != null) {
            return modelEntity.getName() + Separator.COLON.getValue() + entity.getName();
        }
        return entity.getName();
    }

    @Override
    public TreeNode treeNode(Long uuid) {
        TreeNode node = super.treeNode(uuid);
        if (ExportTreeContextHolder.exportDependency()) {
            Set<String> roleUuids = orgElementService.getRelaRoleUuids(uuid);
            if (CollectionUtils.isNotEmpty(roleUuids)) {
                for (String roleUuid : roleUuids) {
                    node.appendChild(this.exportTreeNodeByDataProvider(roleUuid, IexportType.Role));
                }
            }
        }
        return node;
    }


    @Override
    protected List<IExportTable> childTableStream() {
        return Lists.newArrayList(new IExportTable("SELECT * FROM ORG_ELEMENT_PATH P WHERE P.ORG_ELEMENT_UUID=:uuid"),
                new IExportTable("SELECT * FORM ORG_ELEMENT_PATH_CHAIN C WHERE C.ORG_ELEMENT_ID =:id or c.SUB_ORG_ELEMENT_ID=:id"),
                new IExportTable("SELECT * FROM ORG_ELEMENT_ROLE_RELA R WHERE R.ORG_ELEMENT_UUID=:uuid "));
    }
}
