package com.wellsoft.pt.jpa.hibernate4;

import org.hibernate.cfg.ImprovedNamingStrategy;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/4/10
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/4/10    chenq		2019/4/10		Create
 * </pre>
 */
public class CustomeImprovedNamingStrategy extends ImprovedNamingStrategy {

    @Override
    public String columnName(String columnName) {
        //为了解决部分数据库的保留字可以用引号引用，而又区分标识符大小写敏感的情况
        return columnName.startsWith("`") && columnName.endsWith(
                "`") ? columnName : super.columnName(columnName);
    }

}
