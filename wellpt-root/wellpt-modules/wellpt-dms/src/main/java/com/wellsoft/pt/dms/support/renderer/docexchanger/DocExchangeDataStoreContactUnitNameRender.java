package com.wellsoft.pt.dms.support.renderer.docexchanger;

import com.wellsoft.pt.dms.entity.DmsDocExcContactBookUnitEntity;
import com.wellsoft.pt.dms.service.DmsDocExcContactBookUnitService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Description: 文档交换-通讯录单位名称渲染器
 *
 * @author chenq
 * @date 2018/5/26
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/5/26    chenq		2018/5/26		Create
 * </pre>
 */
@Component
public class DocExchangeDataStoreContactUnitNameRender extends DocExchangeDataStoreRender {

    @Autowired
    DmsDocExcContactBookUnitService dmsDocExcContactBookUnitService;

    @Override
    public String doRenderData(String columnIndex, Object value, Map<String, Object> rowData,
                               String param) {
        if (value != null && StringUtils.isNotBlank(value.toString())) {
            DmsDocExcContactBookUnitEntity unitEntity = dmsDocExcContactBookUnitService.getOne(
                    value.toString());
            return unitEntity != null ? unitEntity.getUnitName() : "";
        }
        return "";

    }

    @Override
    public String getType() {
        return "docExchangeDataStoreContactUnitNameRender";
    }

    @Override
    public String getName() {
        return "数据管理_文档交换_文档通讯录单位名称渲染器";
    }
}
