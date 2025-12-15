package com.wellsoft.pt.jpa.datasource;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/3/26
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/3/26    chenq		2019/3/26		Create
 * </pre>
 */
public class Oracle12cXADataSource extends Oracle11gXADataSource {

    @Override
    public String getType() {
        return DatabaseType.Oracle12c.getName();
    }
}
