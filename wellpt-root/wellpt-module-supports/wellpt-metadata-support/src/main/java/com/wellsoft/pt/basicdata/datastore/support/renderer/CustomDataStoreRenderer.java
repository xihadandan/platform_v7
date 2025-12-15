package com.wellsoft.pt.basicdata.datastore.support.renderer;

import com.wellsoft.pt.basicdata.datastore.support.RendererParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CustomDataStoreRenderer extends AbstractDataStoreRenderer {
    @Autowired(required = false)
    private List<AbstractCustomDataStoreRenderer> dataStoreRenderers;
    private Map<String, DataStoreRenderer> dataStoreRendererMap;

    @Override
    public String getType() {
        return "customRenderer";
    }

    @Override
    public String getName() {
        return "自定义渲染器";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.support.renderer.AbstractDataStoreRenderer#doRenderData(java.lang.String, java.lang.Object, java.util.Map, com.wellsoft.pt.basicdata.datastore.support.RendererParam)
     */
    @Override
    public Object doRenderData(String columnIndex, Object value, Map<String, Object> rowData, RendererParam param) {
        DataStoreRenderer dataStoreRenderer = getDataStoreRendererMap().get(param.getString(CUSTOM_TYPE_KEY));
        if (dataStoreRenderer != null) {
            return dataStoreRenderer.renderData(columnIndex, value, rowData, param);
        }
        return null;
    }

    /**
     * @return
     */
    public Map<String, DataStoreRenderer> getDataStoreRendererMap() {
        if (dataStoreRendererMap == null) {
            dataStoreRendererMap = new HashMap<String, DataStoreRenderer>();
            for (DataStoreRenderer dataStoreRenderer : dataStoreRenderers) {
                dataStoreRendererMap.put(dataStoreRenderer.getType(), dataStoreRenderer);
            }
        }
        return dataStoreRendererMap;
    }

}
