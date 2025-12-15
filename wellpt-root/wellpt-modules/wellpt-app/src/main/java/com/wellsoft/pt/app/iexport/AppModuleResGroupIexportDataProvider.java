package com.wellsoft.pt.app.iexport;

import com.google.common.collect.Lists;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.pt.app.entity.AppModuleResGroupEntity;
import com.wellsoft.pt.app.entity.AppModuleResGroupMemberEntity;
import com.wellsoft.pt.app.service.AppModuleResGroupService;
import com.wellsoft.pt.basicdata.iexport.service.AbstractIexportDataProvider;
import com.wellsoft.pt.basicdata.iexport.suport.IExportTable;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
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
 * 2023年11月14日   chenq	 Create
 * </pre>
 */
@Transactional(readOnly = true)
@Service
public class AppModuleResGroupIexportDataProvider extends AbstractIexportDataProvider<AppModuleResGroupEntity, Long> {
    @Autowired
    private AppModuleResGroupService appModuleResGroupService;

    @Override
    public String getType() {
        return IexportType.AppModuleResGroup;
    }

    @Override
    public String getTreeName(AppModuleResGroupEntity appModuleResGroupEntity) {
        return "分组: " + appModuleResGroupEntity.getName();
    }

    @Override
    public TreeNode treeNode(Long uuid) {
        TreeNode node = super.treeNode(uuid);
        // 导出分组下的资源
        List<AppModuleResGroupMemberEntity> memberEntities = appModuleResGroupService.listResGroupMemberByGroup(uuid);
        if (CollectionUtils.isNotEmpty(memberEntities)) {
            for (AppModuleResGroupMemberEntity mem : memberEntities) {
                if (StringUtils.isNotBlank(mem.getType())) {
                    try {
                        this.exportTreeNodeByDataProvider(mem.getMemberUuid(),mem.getType(),node);
//                        TreeNode child = this.exportTreeNodeByDataProvider(mem.getMemberUuid(), mem.getType());
//                        if (child != null) {
//                            node.getChildren().add(child);
//                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }

            }
        }
        return node;
    }

    @Override
    protected List<IExportTable> childTableStream() {
        return Lists.newArrayList(new IExportTable("select * from APP_MODULE_RES_GROUP_MEMBER where group_uuid=:uuid"),
                new IExportTable("select * from APP_MODULE_RES_SEQ s where exists ( select 1 from APP_MODULE_RES_GROUP_MEMBER m where to_char(m.member_uuid) = s.res_uuid " +
                        " and m.group_uuid=:uuid)")
        );
    }
}
