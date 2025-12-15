package com.wellsoft.pt.jpa.hibernate;

import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLJoinTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlDeleteStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlInsertStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlUpdateStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitorAdapter;
import org.apache.commons.lang.StringUtils;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2024年01月23日   chenq	 Create
 * </pre>
 */
public class TenantMysqlASTVisitorAdapter extends MySqlASTVisitorAdapter {

    private String schema;

    private String tenant;

    private MathASTTableSource mathASTTableSource;

    public TenantMysqlASTVisitorAdapter(String schema, String tenant, MathASTTableSource mathASTTableSource) {
        this.schema = schema;
        this.tenant = tenant;
        this.mathASTTableSource = mathASTTableSource;
    }

    @Override
    public boolean visit(SQLExprTableSource tableSource) {
        if (this.mathASTTableSource.match(tableSource.getTableName())) {
            tableSource.setSchema(this.schema);
        }
        return true;
    }


    @Override
    public boolean visit(SQLSelectQueryBlock queryBlock) {
        this.equalTenantCondition(queryBlock, queryBlock.getFrom());
        return true;
    }


    @Override
    public boolean visit(MySqlInsertStatement statement) {
        if (this.mathASTTableSource.match(statement.getTableSource().getTableName())) {
            statement.getTableSource().setSchema(this.schema);
        }
        return true;
    }

    @Override
    public boolean visit(MySqlUpdateStatement statement) {
        if (this.mathASTTableSource.match(statement.getTableName().getSimpleName())) {
            SQLExprTableSource source = (SQLExprTableSource) statement.getTableSource();
            source.setSchema(this.schema);
            statement.addCondition("tenant = '" + this.tenant + "'");
        }
        return true;
    }


    @Override
    public boolean visit(MySqlDeleteStatement statement) {
        if (this.mathASTTableSource.match(statement.getExprTableSource().getTableName())) {
            statement.getExprTableSource().setSchema(this.schema);
            statement.addCondition("tenant = '" + this.tenant + "'");
        }
        return true;
    }


    private void equalTenantCondition(SQLSelectQueryBlock queryBlock, SQLTableSource source) {
        if (source != null) {
            if (source instanceof SQLExprTableSource) {
                if (this.mathASTTableSource.match(((SQLExprTableSource) source).getTableName())) {
                    if (StringUtils.isNotBlank(source.getAlias())) {
                        queryBlock.addCondition(source.getAlias() + "." + "tenant = '" + this.tenant + "'");
                    } else {
                        queryBlock.addCondition("tenant = '" + this.tenant + "'");
                    }
                }
            } else if (source instanceof SQLJoinTableSource) {
                SQLTableSource leftSource = ((SQLJoinTableSource) source).getLeft();
                this.equalTenantCondition(queryBlock, leftSource);
                this.equalTenantCondition(queryBlock, ((SQLJoinTableSource) source).getRight());
            }
        }
    }
}
