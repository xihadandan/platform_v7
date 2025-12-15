package com.wellsoft.pt.app.function.ext;

import com.google.common.collect.Lists;
import com.wellsoft.pt.app.entity.AppPageDefinition;
import com.wellsoft.pt.app.function.AbstractAppFunctionSourceLoader;
import com.wellsoft.pt.app.function.AppFunctionSource;
import com.wellsoft.pt.app.function.SimpleAppFunctionSource;
import com.wellsoft.pt.app.service.AppPageDefinitionService;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2023年11月30日   chenq	 Create
 * </pre>
 */

@Service
public class AppPageDefinitionAppFunctionSourceLoader extends AbstractAppFunctionSourceLoader {
    @Autowired
    AppPageDefinitionService appPageDefinitionService;

    @Override
    public String getAppFunctionType() {
        return IexportType.AppPageDefinition;
    }

    @Override
    public List<AppFunctionSource> getAppFunctionSources() {
        return null;
    }

    @Override
    public List<AppFunctionSource> getAppFunctionSourceById(String id) {
        AppPageDefinition appPageDefinition = appPageDefinitionService.getLatestPageDefinition(id);
        if (appPageDefinition != null) {
            return convert2AppFunctionSource(appPageDefinition);
        }
        return null;
    }


    private List<AppFunctionSource> convert2AppFunctionSource(AppPageDefinition appPageDefinition) {
        return Lists.newArrayList(new SimpleAppFunctionSource(appPageDefinition.getUuid(),
                appPageDefinition.getName(), appPageDefinition.getName(), appPageDefinition.getId(),
                appPageDefinition.getCode(), null, null, null, true,
                this.getAppFunctionType(), false, null));
    }

    @Override
    public List<AppFunctionSource> getAppFunctionSourceByUuid(String uuid) {
        AppPageDefinition appPageDefinition = appPageDefinitionService.getOne(uuid);
        if (appPageDefinition != null) {
            return convert2AppFunctionSource(appPageDefinition);
        }
        return null;
    }
}
