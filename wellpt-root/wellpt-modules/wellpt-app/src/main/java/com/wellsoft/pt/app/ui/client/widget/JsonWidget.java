package com.wellsoft.pt.app.ui.client.widget;

import com.wellsoft.pt.app.ui.AbstractWidget;
import com.wellsoft.pt.app.ui.Widget;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2022年04月08日   chenq	 Create
 * </pre>
 */
public class JsonWidget extends AbstractWidget {

    public JsonWidget(String widgetDefinition) throws Exception {
        super(widgetDefinition);
    }

    @Override
    public List<Widget> getItems() {
        throw new UnsupportedOperationException("不支持");
    }

    @Override
    public String getDefinitionJson() throws Exception {
        return this.widgetDefinition;
    }


}
