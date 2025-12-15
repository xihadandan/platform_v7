package com.wellsoft.pt.api.export;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.pt.api.entity.ApiLinkEntity;
import com.wellsoft.pt.api.entity.ApiOperationEntity;
import com.wellsoft.pt.api.service.ApiLinkService;
import com.wellsoft.pt.basicdata.iexport.service.AbstractIexportDataProvider;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2025年06月25日   chenq	 Create
 * </pre>
 */
@Service
@Transactional(readOnly = true)
public class ApiLinkIexportDataProvider extends AbstractIexportDataProvider<ApiLinkEntity, Long> {
    @Autowired
    ApiLinkService apiLinkService;

    @Override
    public String getType() {
        return IexportType.ApiLink;
    }

    @Override
    public String getTreeName(ApiLinkEntity apiLinkEntity) {
        return apiLinkEntity.getName();
    }

    @Override
    public TreeNode treeNode(Long uuid) {
        ApiLinkEntity entity = getEntity(uuid);
        TreeNode node = new TreeNode();
        node.setId(uuid.toString());
        node.setType(getType());
        node.setName(getTreeName(entity));
        List<ApiOperationEntity> apiOperationEntityList = apiLinkService.listApiOperationsByApiLinkUuid(uuid);
        if (CollectionUtils.isNotEmpty(apiOperationEntityList)) {
            for (ApiOperationEntity apiOperationEntity : apiOperationEntityList) {
                node.appendChild(this.exportTreeNodeByDataProvider(apiOperationEntity.getUuid(), IexportType.ApiOperation));
            }
        }
        return node;
    }
}
