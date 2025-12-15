package com.wellsoft.pt.app.function.ext;

import com.google.common.collect.Lists;
import com.wellsoft.pt.app.entity.AppModule;
import com.wellsoft.pt.app.function.AbstractAppFunctionSourceLoader;
import com.wellsoft.pt.app.function.AppFunctionSource;
import com.wellsoft.pt.app.function.SimpleAppFunctionSource;
import com.wellsoft.pt.app.service.AppModuleService;
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
public class AppModuleAppFunctionSourceLoader extends AbstractAppFunctionSourceLoader {
    @Autowired
    AppModuleService appModuleService;

    @Override
    public String getAppFunctionType() {
        return IexportType.AppModule;
    }

    @Override
    public List<AppFunctionSource> getAppFunctionSources() {
        return null;
    }

    @Override
    public List<AppFunctionSource> getAppFunctionSourceById(String id) {
        AppModule appModule = appModuleService.getById(id);
        if (appModule != null) {
            return convert2AppFunctionSource(appModule);
        }
        return null;
    }

    private List<AppFunctionSource> convert2AppFunctionSource(AppModule appModule) {
        return Lists.newArrayList(new SimpleAppFunctionSource(appModule.getUuid(),
                appModule.getName(), appModule.getName(), appModule.getId(),
                appModule.getCode(), null, null, null, true,
                this.getAppFunctionType(), false, null));
    }

    @Override
    public List<AppFunctionSource> getAppFunctionSourceByUuid(String uuid) {
        AppModule appModule = appModuleService.getOne(uuid);
        if (appModule != null) {
            return convert2AppFunctionSource(appModule);
        }
        return null;
    }
}
