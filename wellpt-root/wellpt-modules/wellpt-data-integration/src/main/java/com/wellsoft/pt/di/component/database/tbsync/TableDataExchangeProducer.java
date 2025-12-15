package com.wellsoft.pt.di.component.database.tbsync;

import com.wellsoft.pt.di.component.AbstractProducer;

import java.util.Map;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/8/23
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/8/23    chenq		2019/8/23		Create
 * </pre>
 */
public class TableDataExchangeProducer extends AbstractProducer<TableDataExchangeEndpoint> {


    public TableDataExchangeProducer(
            TableDataExchangeEndpoint endpoint) {
        super(endpoint);

    }

    @Override
    protected void action(Object body, Map headers, Map properties,
                          Map attachments) throws Exception {

        //TODO：更新表变更数据到具体的表
    }
}
