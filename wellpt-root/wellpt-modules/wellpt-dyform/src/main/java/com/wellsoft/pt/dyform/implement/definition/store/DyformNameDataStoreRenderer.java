package com.wellsoft.pt.dyform.implement.definition.store;

import com.google.common.collect.Lists;
import com.wellsoft.pt.basicdata.datastore.support.RendererParam;
import com.wellsoft.pt.basicdata.datastore.support.renderer.AbstractDataStoreRenderer;
import com.wellsoft.pt.dyform.implement.definition.entity.FormDefinition;
import com.wellsoft.pt.dyform.implement.definition.service.FormDefinitionService;
import org.apache.commons.lang.StringUtils;
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
 * 2021年10月14日   chenq	 Create
 * </pre>
 */
@Component
public class DyformNameDataStoreRenderer extends AbstractDataStoreRenderer {
    @Autowired
    FormDefinitionService formDefinitionService;

    @Override
    public Object doRenderData(String columnIndex, Object value, Map<String, Object> rowData, RendererParam param) {
        if (value != null && StringUtils.isNotBlank(value.toString())) {
            String[] formUuids = value.toString().split(";|,");
            List<String> names = Lists.newArrayListWithCapacity(formUuids.length);
            for (String uuid : formUuids) {
                FormDefinition definition = formDefinitionService.getOne(uuid);
                if (definition != null) {
                    names.add(definition.getName() + "(" + definition.getVersion() + ")");
                }
            }
            return StringUtils.join(names, ", ");
        }
        return "";
    }

    @Override
    public String getType() {
        return "DyformNameDataStoreRender";
    }

    @Override
    public String getName() {
        return "表单名称渲染器";
    }
}
