package com.wellsoft.pt.basicdata.datastore.support.renderer;

import com.wellsoft.pt.basicdata.datastore.support.RendererParam;
import org.apache.commons.lang.StringUtils;

import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017年5月17日.1	zhulh		2017年5月17日		Create
 * </pre>
 * @date 2017年5月17日
 */
public abstract class AbstractCustomDataStoreRenderer implements DataStoreRenderer {
    private static final String PARAMS_KEY = "params";

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.support.renderer.DataStoreRenderer#renderData(java.lang.String, java.lang.Object, java.util.Map, com.wellsoft.pt.basicdata.datastore.support.RendererParam)
     */
    @Override
    public Object renderData(String columnIndex, Object value, Map<String, Object> rowData, RendererParam param) {
        if (!param.containsKey(CUSTOM_TYPE_KEY)) {
            return null;
        }
        if (param.getString(CUSTOM_TYPE_KEY).equals(getType())) {
            return doRenderData(columnIndex, value, rowData, param);
        }
        return null;
    }

    /**
     * @param columnIndex
     * @param value
     * @param rowData
     * @param param
     * @return
     */
    public Object doRenderData(String columnIndex, Object value, Map<String, Object> rowData, RendererParam param) {
        return doRenderData(columnIndex, value, rowData, param.getString(PARAMS_KEY, StringUtils.EMPTY));
    }

    /**
     * @param columnIndex
     * @param value
     * @param rowData
     * @param exParams
     * @return
     */
    public abstract Object doRenderData(String columnIndex, Object value, Map<String, Object> rowData, String exParams);
}
