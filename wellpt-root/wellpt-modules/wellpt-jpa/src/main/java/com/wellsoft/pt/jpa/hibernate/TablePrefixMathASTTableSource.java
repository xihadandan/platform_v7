package com.wellsoft.pt.jpa.hibernate;

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
public class TablePrefixMathASTTableSource implements MathASTTableSource {

    private String prefix;

    public TablePrefixMathASTTableSource(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public boolean match(String table) {
        return table != null && table.toLowerCase().startsWith(this.prefix.toLowerCase());
    }
}
