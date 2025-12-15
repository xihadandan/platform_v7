package com.wellsoft.pt.app.support.renderer;

import com.wellsoft.pt.app.entity.AppModule;
import com.wellsoft.pt.app.service.AppModuleService;
import com.wellsoft.pt.basicdata.datastore.support.RendererParam;
import com.wellsoft.pt.basicdata.datastore.support.renderer.AbstractDataStoreRenderer;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author yt
 * @title: AppModuleDataStoreRenderer
 * @date 2020/5/22 4:52 下午
 */
@Component
public class AppModuleNameDataStoreRenderer extends AbstractDataStoreRenderer {

    @Autowired
    private AppModuleService appModuleService;

    @Override
    public Object doRenderData(String columnIndex, Object value, Map<String, Object> rowData, RendererParam param) {
        if (value != null && StringUtils.isNotBlank(value.toString())) {
            AppModule appModule = appModuleService.getById(value.toString());
            if (appModule != null) {
                return appModule.getName();
            }
        }
        return "";
    }

    @Override
    public String getType() {
        return "appModuleName";
    }

    @Override
    public String getName() {
        return "模块名称渲染器";
    }
}
