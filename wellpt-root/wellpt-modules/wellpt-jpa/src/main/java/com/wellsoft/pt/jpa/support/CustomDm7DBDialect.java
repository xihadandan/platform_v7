package com.wellsoft.pt.jpa.support;

import org.hibernate.dialect.DmDialect;
import org.hibernate.type.StandardBasicTypes;

import java.sql.Types;

/**
 * Description: 达梦数据库
 *
 * @author chenq
 * @date 2019/4/8
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/4/8    chenq		2019/4/8		Create
 * </pre>
 */
public class CustomDm7DBDialect extends DmDialect {

    public CustomDm7DBDialect() {
        super();
        registerHibernateType(Types.BIGINT, StandardBasicTypes.BIG_DECIMAL.getName());
        registerColumnType(Types.INTEGER, "number(10,0)");
    }

    @Override
    public boolean supportsCommentOn() {
        return true;
    }


    @Override
    public String getCurrentTimestampSelectString() {
        return "select GETDATE() ";
    }


}
