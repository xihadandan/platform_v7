package org.hibernate.cfg;

import org.apache.commons.lang.StringUtils;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.mapping.Column;

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
public class OracleCustomTable extends AbstractTable {
    @Override
    public Iterator<String> sqlCommentStrings(Dialect dialect, Mapping p, String defaultCatalog,
                                              String defaultSchema) {

        List comments = new ArrayList();
        if (dialect.supportsCommentOn()) {
            String tableName = getQualifiedName(dialect, defaultCatalog, defaultSchema);
            String comment = explainCommentOut();
            if (StringUtils.isNotBlank(comment)) {
                StringBuilder buf = new StringBuilder()
                        .append("comment on table ")
                        .append(tableName)
                        .append(" is '")
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
                            .append("comment on column ")
                            .append(tableName)
                            .append('.')
                            .append(column.getQuotedName(dialect))
                            .append(" is '")
                            .append(columnComment)
                            .append("'");
                    comments.add(buf.toString());
                }
            }
        }
        return comments.iterator();
    }
}
