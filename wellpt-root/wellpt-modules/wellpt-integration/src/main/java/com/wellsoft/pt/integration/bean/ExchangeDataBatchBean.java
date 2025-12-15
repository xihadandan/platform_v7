package com.wellsoft.pt.integration.bean;

import com.wellsoft.pt.integration.entity.ExchangeDataBatch;

/**
 * Description: 如何描述该类
 *
 * @author Administrator
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-1-7.1	Administrator		2014-1-7		Create
 * </pre>
 * @date 2014-1-7
 */
public class ExchangeDataBatchBean extends ExchangeDataBatch {

    private String fromUnitName;

    public String getFromUnitName() {
        return fromUnitName;
    }

    public void setFromUnitName(String fromUnitName) {
        this.fromUnitName = fromUnitName;
    }

}
