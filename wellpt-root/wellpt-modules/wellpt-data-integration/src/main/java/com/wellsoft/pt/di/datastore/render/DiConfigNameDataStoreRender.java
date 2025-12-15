package com.wellsoft.pt.di.datastore.render;

import com.wellsoft.pt.basicdata.datastore.support.RendererParam;
import com.wellsoft.pt.basicdata.datastore.support.renderer.AbstractDataStoreRenderer;
import com.wellsoft.pt.di.entity.DiConfigEntity;
import com.wellsoft.pt.di.service.DiConfigService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/7/19
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/7/19    chenq		2019/7/19		Create
 * </pre>
 */
@Component
public class DiConfigNameDataStoreRender extends AbstractDataStoreRenderer {


    @Autowired
    DiConfigService diConfigService;

    @Override
    public String getType() {
        return "DiConfigNameDataStoreRender";
    }

    @Override
    public String getName() {
        return "数据交换_配置名称渲染";
    }

    @Override
    public Object doRenderData(String columnIndex, Object value, Map<String, Object> rowData,
                               RendererParam param) {
        if (value != null && StringUtils.isNotBlank(value.toString())) {
            DiConfigEntity configEntity = diConfigService.getOne(value.toString());
            return configEntity != null ? configEntity.getName() : "";
        }
        return "";
    }


}
