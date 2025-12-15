package com.wellsoft.pt.cg.core.source;

import com.google.common.collect.Sets;

import java.util.*;

/**
 * 数据表数据
 *
 * @author lmw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-7-8.1	lmw		2015-7-8		Create
 * </pre>
 * @date 2015-7-8
 */
public class TableSource implements Source {
    private Map<String, List<TableSource.Column>> rMap = new HashMap<String, List<Column>>();
    private Set<String> tableSet = Sets.newHashSet();

    public Map<String, List<TableSource.Column>> getTables() {
        return rMap;
    }

    public Set<String> getTableSet() {
        return tableSet;
    }


    public void addColumn(String table, TableSource.Column column) {
        List<TableSource.Column> list = rMap.get(table);
        if (list == null) {
            list = new LinkedList<TableSource.Column>();
        }
        list.add(column);
        rMap.put(table, list);
        tableSet.add(table);
    }

    public List<TableSource.Column> getColumn(String table) {
        return rMap.get(table);
    }

    public static class Column {
        private String columnName;// 列明
        private String type;// 列类型
        private boolean isParmaryKey;// 是否为主键
        private boolean isForeignKey;// 是否为外键
        private String foreignTable;// 外键表
        private String remark;// 备注

        public String getColumnName() {
            return columnName;
        }

        public void setColumnName(String columnName) {
            this.columnName = columnName;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public boolean isParmaryKey() {
            return isParmaryKey;
        }

        public void setParmaryKey(boolean isParmaryKey) {
            this.isParmaryKey = isParmaryKey;
        }

        public boolean isForeignKey() {
            return isForeignKey;
        }

        public void setForeignKey(boolean isForeignKey) {
            this.isForeignKey = isForeignKey;
        }

        public String getForeignTable() {
            return foreignTable;
        }

        public void setForeignTable(String foreignTable) {
            this.foreignTable = foreignTable;
        }

        /**
         * @return the remark
         */
        public String getRemark() {
            return remark;
        }

        /**
         * @param remark 要设置的remark
         */
        public void setRemark(String remark) {
            this.remark = remark;
        }

        @Override
        public int hashCode() {
            return this.getColumnName() != null ? this.getColumnName().toUpperCase().hashCode() : super.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Column) {
                return this.getColumnName() != null ? this.getColumnName().equalsIgnoreCase(
                        ((Column) obj).getColumnName()) : false;
            }
            return false;

        }
    }
}
