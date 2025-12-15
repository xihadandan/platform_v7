package com.wellsoft.pt.org.iexport.provider;

import com.google.common.collect.Lists;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.basicdata.iexport.service.AbstractIexportDataProvider;
import com.wellsoft.pt.basicdata.iexport.suport.ExportTreeContextHolder;
import com.wellsoft.pt.basicdata.iexport.suport.IExportTable;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import com.wellsoft.pt.org.dto.OrgGroupDto;
import com.wellsoft.pt.org.entity.OrgElementEntity;
import com.wellsoft.pt.org.entity.OrgGroupEntity;
import com.wellsoft.pt.org.service.OrgElementModelService;
import com.wellsoft.pt.org.service.OrgElementService;
import com.wellsoft.pt.org.service.OrgGroupService;
import com.wellsoft.pt.user.entity.UserInfoEntity;
import com.wellsoft.pt.user.service.UserInfoService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

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
public class OrgGroupIexportDataProvider extends AbstractIexportDataProvider<OrgGroupEntity, Long> {

    @Resource
    OrgGroupService orgGroupService;

    @Resource
    OrgElementModelService elementModelService;

    @Resource
    OrgElementService orgElementService;

    @Resource
    UserInfoService userInfoService;

    @Override
    public String getType() {
        return IexportType.OrgGroup;
    }

    @Override
    public String getTreeName(OrgGroupEntity entity) {
        return "群组" + Separator.COLON.getValue() + entity.getName();
    }

    @Override
    public TreeNode treeNode(Long uuid) {
        TreeNode node = super.treeNode(uuid);
        if (ExportTreeContextHolder.exportDependency()) {
            OrgGroupDto dto = orgGroupService.getOrgGroupDetails(uuid);
            if (CollectionUtils.isNotEmpty(dto.getRoleUuids())) {
                for (String role : dto.getRoleUuids()) {
                    node.appendChild(this.exportTreeNodeByDataProvider(role, IexportType.Role));
                }
            }
            appendGroupMemberAndOwnerNodes(node, dto.getMember());
            appendGroupMemberAndOwnerNodes(node, dto.getOwner());
        }
        return node;
    }

    private void appendGroupMemberAndOwnerNodes(TreeNode node, List<String> ids) {
        if (CollectionUtils.isNotEmpty(ids)) {
            for (String id : ids) {
                if (id.startsWith(IdPrefix.USER.getValue() + Separator.UNDERLINE.getValue())) {
                    // 用户导出
                    UserInfoEntity userInfoEntity = userInfoService.getByUserId(id);
                    if (userInfoEntity != null) {
                        node.appendChild(this.exportTreeNodeByDataProvider(userInfoEntity.getUuid(), IexportType.User));
                    }
                } else if (id.startsWith(IdPrefix.GROUP.getValue() + Separator.UNDERLINE.getValue())) {
                    OrgGroupEntity example = new OrgGroupEntity();
                    example.setId(id);
                    List<OrgGroupEntity> list = orgGroupService.listByEntity(example);
                    if (CollectionUtils.isNotEmpty(list)) {
                        node.appendChild(this.exportTreeNodeByDataProvider(list.get(0).getUuid(), IexportType.OrgGroup));
                    }
                } else {
                    // 组织元素导出
                    OrgElementEntity elementEntity = orgElementService.getOrgElementByIdPublished(id);
                    if (elementEntity != null) {
                        node.appendChild(this.exportTreeNodeByDataProvider(elementEntity.getUuid(), IexportType.OrgElement));
                    }
                }
            }
        }
    }

    @Override
    protected List<IExportTable> childTableStream() {
        return Lists.newArrayList(new IExportTable("SELECT * FROM ORG_GROUP_MEMBER M WHERE M.GROUP_UUID=:uuid"),
                new IExportTable("SELECT * FROM ORG_GROUP_OWNER M WHERE M.GROUP_UUID=:uuid"),
                new IExportTable("SELECT * FROM ORG_GROUP_ROLE M WHERE M.GROUP_UUID=:uuid")
                //,new IExportTable("SELECT * FROM AUDIT_ROLE R WHERE EXISTS ( SELECT 1 FROM ORG_GROUP_ROLE O WHERE O.GROUP_UUID=:uuid AND O.ROLE_UUID = R.UUID ) ")
        );
    }
}
