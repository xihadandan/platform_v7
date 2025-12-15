package com.wellsoft.pt.basicdata.datastore.support.renderer;

import com.wellsoft.pt.basicdata.datastore.support.RendererParam;
import com.wellsoft.pt.multi.org.entity.MultiOrgSystemUnit;
import com.wellsoft.pt.multi.org.facade.service.OrgApiFacade;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/12/21
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/12/21    chenq		2019/12/21		Create
 * </pre>
 */
@Component
public class SystemUnitNameDataStoreRender extends AbstractDataStoreRenderer {

    @Autowired
    OrgApiFacade orgApiFacade;

    @Override
    public Object doRenderData(String columnIndex, Object value, Map<String, Object> rowData,
                               RendererParam param) {
        if (value != null && StringUtils.isNotBlank(value.toString())) {
            if (MultiOrgSystemUnit.PT_ID.equalsIgnoreCase(value.toString())) {
                return MultiOrgSystemUnit.PT_NAME;
            }
            MultiOrgSystemUnit unit = orgApiFacade.getSystemUnitById(value.toString());
            if (unit != null) {
                return unit.getName();
            }
        }
        return "";
    }

    @Override
    public String getType() {
        return "systemUnitName";
    }

    @Override
    public String getName() {
        return "系统单位名称渲染器";
    }
}
