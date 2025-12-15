package com.wellsoft.pt.jpa.support;

import org.hibernate.dialect.MySQL5InnoDBDialect;
import org.hibernate.type.StandardBasicTypes;

import java.sql.Types;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/3/8
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/3/8    chenq		2019/3/8		Create
 * </pre>
 */
public class CustomMySQL5InnoDBDialect extends MySQL5InnoDBDialect {

    public CustomMySQL5InnoDBDialect() {
        super();
        registerColumnType(Types.TIMESTAMP, "datetime(3)");

        registerHibernateType(Types.BIGINT, StandardBasicTypes.BIG_DECIMAL.getName());
    }


    @Override
    public boolean supportsCommentOn() {
        return true;
    }
}
