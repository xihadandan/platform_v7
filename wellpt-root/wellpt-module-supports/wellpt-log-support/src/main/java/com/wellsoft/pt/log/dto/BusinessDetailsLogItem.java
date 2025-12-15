package com.wellsoft.pt.log.dto;

import com.wellsoft.context.jdbc.support.BaseQueryItem;

/**
 * Description:
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	        修改人		修改日期			修改内容
 * 2021/10/10.1	    zenghw		2021/10/10		    Create
 * </pre>
 * @date 2021/10/10
 */
public class BusinessDetailsLogItem implements BaseQueryItem {

    private String logId;

    private String num;

    public String getLogId() {
        return this.logId;
    }

    public void setLogId(final String logId) {
        this.logId = logId;
    }

    public String getNum() {
        return this.num;
    }

    public void setNum(final String num) {
        this.num = num;
    }
}
