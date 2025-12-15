package com.wellsoft.pt.dms.support.renderer.docexchanger;

import java.util.Map;

/**
 * Description: 文档交换-接收文档的签收渲染器
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
//@Component
public class DocExchangeDataStoreSignRender extends DocExchangeDataStoreRender {
    @Override
    public String doRenderData(String columnIndex, Object value, Map<String, Object> rowData,
                               String param) {
        return null;
    }


    @Override
    public String getType() {
        return "docExchangeDataStoreSignRender";
    }

    @Override
    public String getName() {
        return "数据管理_文档交换_文档签收渲染器";
    }
}
