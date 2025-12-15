package com.wellsoft.pt.app.security;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.pt.app.service.AppPageDefinitionService;
import com.wellsoft.pt.app.support.AppFunctionType;
import com.wellsoft.pt.security.audit.support.AbstractResourceDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2024年04月08日   chenq	 Create
 * </pre>
 */
@Component
public class AppPageDefinitionResourceDataSource extends AbstractResourceDataSource {

    @Autowired
    AppPageDefinitionService appPageDefinitionService;

    @Override
    public List<TreeNode> getData(Map<String, Object> params) {
        return null;
    }

    @Override
    public String getId() {
        return AppFunctionType.AppPageDefinition;
    }

    @Override
    public List<String> getAnonymousResources() {
        return appPageDefinitionService.getAllAnonymousPageDefinitionIds();
    }


}
