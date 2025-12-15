package com.wellsoft.pt.dms.support.renderer.docexchanger;

import com.wellsoft.pt.dms.enums.DocExchangeUrgeLevelEnum;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Description: 文档交换-文档缓急程度渲染器
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
public class DocExchangeDataStoreUrgeLevelRender extends DocExchangeDataStoreRender {

    @Override
    public String doRenderData(String columnIndex, Object value, Map<String, Object> rowData,
                               String param) {
        if (value == null) {
            return "";
        }
        DocExchangeUrgeLevelEnum urgeLevelEnum = DocExchangeUrgeLevelEnum.get(Integer.parseInt(value.toString()));
        return urgeLevelEnum != null ? urgeLevelEnum.getName() : "";
    }

    @Override
    public String getType() {
        return "docExchangeDataStoreUrgeLevelRender";
    }

    @Override
    public String getName() {
        return "数据管理_文档交换_文档缓急渲染器";
    }
}
