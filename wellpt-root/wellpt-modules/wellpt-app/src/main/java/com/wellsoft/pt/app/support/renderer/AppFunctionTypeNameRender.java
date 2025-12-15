package com.wellsoft.pt.app.support.renderer;

import com.wellsoft.pt.basicdata.datastore.support.RendererParam;
import com.wellsoft.pt.basicdata.datastore.support.renderer.AbstractDataStoreRenderer;
import com.wellsoft.pt.basicdata.selective.facade.SelectiveDatas;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/12/25
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/12/25    chenq		2019/12/25		Create
 * </pre>
 */
@Component
public class AppFunctionTypeNameRender extends AbstractDataStoreRenderer {


    @Override
    public Object doRenderData(String columnIndex, Object value, Map<String, Object> rowData,
                               RendererParam param) {
        return value != null && StringUtils.isNotBlank(value.toString()) ? SelectiveDatas.getLabel(
                "PT_APP_FUNCTION_TYPE", value.toString()) : "";
    }

    @Override
    public String getType() {
        return "APP_FUNCTION_TYPE_NAME_RENDER";
    }

    @Override
    public String getName() {
        return "平台功能类型名称渲染器";
    }
}
