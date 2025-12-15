package com.wellsoft.pt.jpa.support;

import org.hibernate.dialect.Kingbase8Dialect;

import java.sql.Types;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2021年07月09日   chenq	 Create
 * </pre>
 */
public class CustomKingbase8Dialect extends Kingbase8Dialect {

    public CustomKingbase8Dialect() {
        super();
        registerColumnType(Types.INTEGER, "numeric(10,0)");
    }

    @Override
    public String toBooleanValueString(boolean bool) {
        return bool ? "1" : "0";
    }
}
