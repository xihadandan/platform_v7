package org.hibernate.cfg;

import org.apache.commons.lang.StringUtils;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.mapping.Column;
import org.hibernate.tool.hbm2ddl.TableMetadata;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
public class MySQLCustomTable extends AbstractTable {
    private static final long serialVersionUID = 1539200494068627151L;

    @Override
    protected List<String> modifyColumSqlScripts(Dialect dialect, Mapping p,
                                                 TableMetadata tableInfo, String defaultCatalog,
                                                 String defaultSchema) {
        return super.modifyColumSqlScripts(dialect, p, tableInfo, defaultCatalog, defaultSchema);
    }

    @Override
    protected List<String> dropColumnSqlScripts(JSONArray delFieldNamesJson, Dialect dialect,
                                                Mapping p, TableMetadata tableInfo,
                                                String defaultCatalog, String defaultSchema) {
        return super.dropColumnSqlScripts(delFieldNamesJson, dialect, p, tableInfo, defaultCatalog,
                defaultSchema);
    }

    @Override
    public Iterator<String> sqlCommentStrings(Dialect dialect, Mapping mapping,
                                              String defaultCatalog,
                                              String defaultSchema) {
        List comments = new ArrayList();
        if (dialect.supportsCommentOn()) {
            String tableName = getQualifiedName(dialect, defaultCatalog, defaultSchema);
            String comment = explainCommentOut();
            if (StringUtils.isNotBlank(comment)) {
                StringBuilder buf = new StringBuilder()
                        .append("alter table ")
                        .append(tableName)
                        .append(" comment ")
                        .append("'")
                        .append(comment)
                        .append("'");
                comments.add(buf.toString());
            }

            Iterator iter = getColumnIterator();
            while (iter.hasNext()) {
                Column column = (Column) iter.next();
                String columnComment = getComment(column);
                if (StringUtils.isNotBlank(columnComment)) {
                    StringBuilder buf = new StringBuilder()
                            .append(" alter table ")
                            .append(tableName)
                            .append(" modify column ")
                            .append(column.getQuotedName(dialect))
                            .append(" ")
                            .append(column.getSqlType(dialect, mapping))
                            .append(" comment '")
                            .append(columnComment)
                            .append("'");
                    comments.add(buf.toString());
                }
            }

        }

        return comments.iterator();
    }
}
