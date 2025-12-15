package org.hibernate.cfg;

import com.wellsoft.pt.jpa.support.CustomDm7DBDialect;
import com.wellsoft.pt.jpa.support.CustomKingbase8Dialect;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.MySQLDialect;
import org.hibernate.dialect.Oracle10gDialect;
import org.hibernate.mapping.Table;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/3/12
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/3/12    chenq		2019/3/12		Create
 * </pre>
 */
public class TableFactory {

    public static Table build(Dialect dialect) {
        if (dialect instanceof MySQLDialect) {
            return new MySQLCustomTable();
        }
        if (dialect instanceof Oracle10gDialect) {
            return new OracleCustomTable();
        }
        if (dialect instanceof CustomDm7DBDialect) {
            return new DamengCustomTable();
        }
        if (dialect instanceof CustomKingbase8Dialect) {
            return new OracleCustomTable();
        }


        throw new RuntimeException("不支持的数据库类型");
    }

}
