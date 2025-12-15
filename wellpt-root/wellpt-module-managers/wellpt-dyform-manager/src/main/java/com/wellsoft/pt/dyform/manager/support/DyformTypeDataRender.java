package com.wellsoft.pt.dyform.manager.support;

import com.wellsoft.pt.basicdata.datastore.support.RendererParam;
import com.wellsoft.pt.basicdata.datastore.support.renderer.AbstractDataStoreRenderer;
import com.wellsoft.pt.dyform.implement.definition.enums.DyformTypeEnum;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Description:表单类型渲染器
 *
 * @author chenq
 * @date 2019/6/4
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/6/4    chenq		2019/6/4		Create
 * </pre>
 */
@Component
public class DyformTypeDataRender extends AbstractDataStoreRenderer {
    @Override
    public Object doRenderData(String columnIndex, Object value, Map<String, Object> rowData,
                               RendererParam param) {
        String type = (String) rowData.get("formType");
        DyformTypeEnum typeEnum = DyformTypeEnum.value2EnumObj(type);
        return typeEnum != null ? typeEnum.getRemark() : "";
    }

    @Override
    public String getType() {
        return "DyformDataRender";
    }

    @Override
    public String getName() {
        return "表单类型渲染器";
    }
}
