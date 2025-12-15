package com.wellsoft.pt.basicdata.datastore.provider;

import com.wellsoft.pt.basicdata.datastore.support.renderer.AbstractCustomDataStoreRenderer;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TestCustomDataStoreRenderer extends AbstractCustomDataStoreRenderer {

    @Override
    public String getType() {
        return "TestCustomDataStoreRenderer";
    }

    @Override
    public String getName() {
        return "测试自定义渲染器";
    }

    @Override
    public Object doRenderData(String columnIndex, Object value, Map<String, Object> rowData, String exParams) {
        return exParams + "(" + value + ")";
    }

}
