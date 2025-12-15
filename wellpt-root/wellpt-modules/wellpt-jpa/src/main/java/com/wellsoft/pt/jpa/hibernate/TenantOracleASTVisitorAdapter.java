package com.wellsoft.pt.jpa.hibernate;

import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.*;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitorAdapter;
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
public class TenantOracleASTVisitorAdapter extends OracleASTVisitorAdapter {

    private String schema;

    private String tenant;

    private MathASTTableSource mathASTTableSource;


    public TenantOracleASTVisitorAdapter(String schema, String tenant, MathASTTableSource mathASTTableSource) {
        this.schema = schema;
        this.tenant = tenant;
        this.mathASTTableSource = mathASTTableSource;
    }

    @Override
    public boolean visit(OracleSelectTableReference source) {
        if (this.mathASTTableSource.match(source.getTableName())) {
            source.setSchema(this.schema);
        }
        return true;
    }


    @Override
    public boolean visit(OracleSelectQueryBlock queryBlock) {
        this.equalTenantCondition(queryBlock, queryBlock.getFrom());
        return true;
    }

    private void equalTenantCondition(OracleSelectQueryBlock queryBlock, SQLTableSource source) {
        if (source != null) {
            if (source instanceof OracleSelectTableReference) {
                if(this.mathASTTableSource.match(((OracleSelectTableReference) source).getTableName())){
                    if (StringUtils.isNotBlank(source.getAlias())) {
                        queryBlock.addCondition(source.getAlias() + "." + "tenant = '" + this.tenant + "'");
                    } else {
                        queryBlock.addCondition("tenant = '" + this.tenant + "'");
                    }
                }
            } else if (source instanceof OracleSelectJoin) {
                SQLTableSource leftSource = ((OracleSelectJoin) source).getLeft();
                this.equalTenantCondition(queryBlock, leftSource);
                this.equalTenantCondition(queryBlock, ((OracleSelectJoin) source).getRight());
            }
        }
    }

    @Override
    public boolean visit(OracleInsertStatement statement) {
        if (this.mathASTTableSource.match(statement.getTableSource().getTableName())) {
            statement.getTableSource().setSchema(this.schema);
        }
        return true;
    }

    @Override
    public boolean visit(OracleUpdateStatement statement) {
        if (this.mathASTTableSource.match(statement.getTableName().getSimpleName())) {
            OracleSelectTableReference reference = (OracleSelectTableReference) statement.getTableSource();
            reference.setSchema(this.schema);
            statement.addCondition("tenant = '" + this.tenant + "'");
        }
        return true;
    }

    @Override
    public boolean visit(OracleDeleteStatement statement) {
        if (this.mathASTTableSource.match(statement.getExprTableSource().getTableName())) {
            statement.getExprTableSource().setSchema(this.schema);
            statement.addCondition("tenant = '" + this.tenant + "'");
        }
        return true;
    }


}
