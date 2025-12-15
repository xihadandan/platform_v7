package com.wellsoft.pt.app.iexport;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.pt.app.entity.AppSystemLoginPageDefinitionEntity;
import com.wellsoft.pt.basicdata.iexport.service.AbstractIexportDataProvider;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2024年02月27日   chenq	 Create
 * </pre>
 */
@Service
@Transactional(readOnly = true)
public class AppSystemLoginPageDefIexportDataProvider extends AbstractIexportDataProvider<AppSystemLoginPageDefinitionEntity, Long> {
    Pattern pattern = Pattern.compile("/proxy-repository/repository/file/mongo/download\\?fileID=[0-9]+");

    @Override
    public String getType() {
        return IexportType.AppSystemLoginPageDef;
    }

    @Override
    public String getTreeName(AppSystemLoginPageDefinitionEntity appSystemLoginPageDefinitionEntity) {
        return appSystemLoginPageDefinitionEntity.getName();
    }

    @Override
    public TreeNode treeNode(Long uuid) {
        TreeNode node = super.treeNode(uuid);
        /**
         * 匹配出使用到附件，一并导出下载
         */
        AppSystemLoginPageDefinitionEntity entity = this.getEntity(uuid);
        Matcher matcher = pattern.matcher(entity.getDefJson());
        while (matcher.find()) {
            node.appendChild(this.exportTreeNodeByDataProvider(matcher.group()
                    .replace("/proxy-repository/repository/file/mongo/download?fileID=", ""), IexportType.LogicFileInfo));
        }
        return node;
    }
}
