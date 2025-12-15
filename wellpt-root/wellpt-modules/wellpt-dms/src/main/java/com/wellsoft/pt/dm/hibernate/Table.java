package com.wellsoft.pt.dm.hibernate;

import com.google.common.collect.Sets;
import com.wellsoft.context.util.json.JsonUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tools.ant.util.ReflectUtil;
import org.hibernate.HibernateException;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Constraint;
import org.hibernate.mapping.UniqueKey;
import org.hibernate.tool.hbm2ddl.ColumnMetadata;
import org.hibernate.tool.hbm2ddl.IndexMetadata;
import org.hibernate.tool.hbm2ddl.TableMetadata;

import java.util.*;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2023年05月19日   chenq	 Create
 * </pre>
 */
public class Table extends org.hibernate.mapping.Table {


    public Iterator sqlAlterStrings(Dialect dialect, Mapping p, TableMetadata tableInfo, String defaultCatalog,
                                    String defaultSchema)
            throws HibernateException {

        StringBuilder root = new StringBuilder("alter table ")
                .append(getQualifiedName(dialect, defaultCatalog, defaultSchema))
                .append(' ')
                .append(dialect.getAddColumnString());


        Iterator iter = getColumnIterator();
        List results = new ArrayList();

        Map dbColsMap = (Map) ReflectUtil.getField(tableInfo, "columns");
        Set<String> columnNames = Sets.newHashSet(dbColsMap.keySet());
        Map<String, IndexMetadata> indexes = (Map) ReflectUtil.getField(tableInfo, "indexes");

        while (iter.hasNext()) {
            Column column = (Column) iter.next();

            ColumnMetadata columnInfo = tableInfo.getColumnMetadata(column.getName());

            String comment = column.getComment();
            if (StringUtils.isNotBlank(comment) && comment.startsWith("{") && comment.endsWith("}")) {
                // 通过注解传递json修改说明实现字段重命名
                Map<String, String> json = JsonUtils.json2Object(comment, HashMap.class);
                String rename = json.get("rename");
                String rollback = json.get("rollback");
                if (StringUtils.isNotBlank(rename)) {
                    if (BooleanUtils.toBoolean(rollback) && !columnNames.contains(rename.toLowerCase())) {
                        // 如果重命名标记的是回滚，则本次重命名操作需要判断上一次发生异常时候的重命名ddl是否执行成功（判断数据库中的字段名是否包含本次回滚的重命名设值）
                        // 如果数据库中的字段集不包含该回滚重命名字段，则说明上次重命名是执行失败的，则只需要修改备注即可，跳过下面的重命名
                        column.setComment(json.get("comment"));
                        columnNames.remove(column.getName().toLowerCase());
                        continue;
                    }
                    results.add(new StringBuilder("alter table ")
                            .append(getQualifiedName(dialect, defaultCatalog, defaultSchema))
                            .append(' ')
                            .append("rename column ").append(rename.toLowerCase()).append(" to ").append(column.getQuotedName(dialect)).toString());

                    // 类型变更
                    String modify = sqlModifyColumnString(column, tableInfo.getColumnMetadata(rename.toLowerCase()), dialect, p, defaultCatalog,
                            defaultSchema);
                    if (StringUtils.isNotBlank(modify)) {
                        results.add(modify);
                    }

                    columnNames.remove(rename.toLowerCase());
                    column.setComment(json.get("comment"));
                    continue;
                }
            }

            columnNames.remove(column.getName().toLowerCase());

            if (columnInfo == null) { // 新增字段
                // the column doesnt exist at all.
                StringBuilder alter = new StringBuilder(root.toString())
                        .append(' ')
                        .append(column.getQuotedName(dialect))
                        .append(' ')
                        .append(column.getSqlType(dialect, p));

                String defaultValue = column.getDefaultValue();
                if (defaultValue != null) {
                    alter.append(" default ").append(defaultValue);
                }

                if (column.isNullable()) {
                    alter.append(dialect.getNullColumnString());
                } else {
                    alter.append(" not null");
                }

                if (column.isUnique()) { // 添加唯一性
                    String keyName = Constraint.generateName("UK_", this, column);
                    UniqueKey uk = getOrCreateUniqueKey(keyName);
                    uk.addColumn(column);
                    alter.append(dialect.getUniqueDelegate()
                            .getColumnDefinitionUniquenessFragment(column));
                }

                if (column.hasCheckConstraint() && dialect.supportsColumnCheck()) {
                    alter.append(" check(")
                            .append(column.getCheckConstraint())
                            .append(")");
                }

                String columnComment = column.getComment();
                if (columnComment != null) {
                    alter.append(dialect.getColumnComment(columnComment));
                }

                alter.append(dialect.getAddColumnSuffixString());
                results.add(alter.toString());

            } else {
                // 修改字段:
                String modify = sqlModifyColumnString(column, columnInfo, dialect, p, defaultCatalog,
                        defaultSchema);
                if (StringUtils.isNotBlank(modify)) {
                    results.add(modify);
                }
                // 非唯一型字段，要删除唯一索引限制
                if (!column.getName().equalsIgnoreCase("UUID")) {
                    List<String> dropUniqueConstraints = dropUniqueConstraintString(dialect, defaultCatalog, defaultSchema, column, indexes);
                    if (CollectionUtils.isNotEmpty(dropUniqueConstraints)) {
                        results.addAll(dropUniqueConstraints);
                    }
                }

            }

        }

        // 删除字段(已经排除了重命名的情况），删除字段导致数据丢失（考虑为重命名字段XXX_DROPED ？）
        StringBuilder drop = new StringBuilder("alter table ")
                .append(getQualifiedName(dialect, defaultCatalog, defaultSchema))
                .append(' ').append(this.getDropColumnString()).append(' ');
        for (String cn : columnNames) {
            StringBuilder dropCol = new StringBuilder(drop.toString())
                    .append(cn).append(" cascade constraints");
            results.add(dropCol.toString());
        }

        return results.iterator();
    }

    protected List<String> dropUniqueConstraintString(Dialect dialect, String defaultCatalog, String defaultSchema, Column column, Map<String, IndexMetadata> indexes) {
        Set<Map.Entry<String, IndexMetadata>> indexEntry = indexes.entrySet();
        List<String> dropConstraints = Lists.newArrayList();
        for (Map.Entry<String, IndexMetadata> entry : indexEntry) {
            IndexMetadata metadata = entry.getValue();
            boolean drop = false;
            if (metadata.getColumns().length == 1 && metadata.getColumns()[0].getName().equalsIgnoreCase(column.getName()) && metadata.getName().toUpperCase().startsWith("UK_")) {// 判断不存在唯一性约束的情况下
                drop = true;
            } else if (metadata.getColumns().length == 2 && entry.getKey().toUpperCase().startsWith("UK_")
                    && ((metadata.getColumns()[0].getName().equalsIgnoreCase("TENANT") && metadata.getColumns()[1].getName().equalsIgnoreCase(column.getName()))
                    || (metadata.getColumns()[1].getName().equalsIgnoreCase("TENANT") && metadata.getColumns()[0].getName().equalsIgnoreCase(column.getName())))) {
                // 与租户字段做联合唯一性的约束，需要判断字段与租户字段是否有组合唯一性存在，如果有则删除
                String keyName = Constraint.generateName("UK_", this, new Column[]{column, new Column("TENANT")});
                // mapping 不存在租户字段组合唯一的情况下，数据库中存在，则删除该组合唯一性
                if (this.getUniqueKey(keyName) == null) {
                    drop = true;
                }

            }
            if (drop) {
                // 删除字段唯一索引
                dropConstraints.add(new StringBuilder("alter table ")
                        .append(getQualifiedName(dialect, defaultCatalog, defaultSchema))
                        .append(' ').append("drop constraint ").append(entry.getKey().toUpperCase()).toString());
            }

        }


        return dropConstraints;

    }

    protected String dropUniqueConstraintString(Dialect dialect, String defaultCatalog, String defaultSchema, String uniqueConstraint) {
        return new StringBuilder("alter table ")
                .append(getQualifiedName(dialect, defaultCatalog, defaultSchema))
                .append(' ').append("drop constraint ").append(uniqueConstraint).toString();

    }


    private String sqlModifyColumnString(Column column, ColumnMetadata columnInfo, Dialect dialect, Mapping p, String defaultCatalog, String defaultSchema) {
        StringBuilder modifyCol = new StringBuilder("alter table ")
                .append(getQualifiedName(dialect, defaultCatalog, defaultSchema))
                .append(' ').append("modify").append(' ');

        String sqlType = column.getSqlType(dialect, p);// 返回示例: varchar2(12)
        // 存在列，则判断是否类型、长度、非空性、唯一性发生变更
        String typeName = columnInfo.getTypeName(); // 返回示例: VARCHAR
        boolean sqlTypeChanged = (sqlType.indexOf("varchar") != -1 && !(typeName + "(" + columnInfo.getColumnSize() + ")").equalsIgnoreCase(sqlType.replace(" char", "")))
                || (sqlType.indexOf("number") != -1 && !(typeName + "(" + columnInfo.getColumnSize() + "," + columnInfo.getDecimalDigits() + ")").equalsIgnoreCase(sqlType.replace(" char", "")));

        StringBuilder modify = new StringBuilder(modifyCol.toString()).append(column.getQuotedName(dialect))
                .append(' ');
        // 类型变更可能会失败：需要保证列无值的情况下
        if (sqlTypeChanged) {
            modify.append(sqlType); //修改类型/长度
        }

        // 更新默认值
        //FIXME: 查询列默认值判断是否需要进行变更
        String defaultValue = column.getDefaultValue();
        modify.append(" default ").append(StringUtils.defaultString(defaultValue, "null"));

        boolean nullChanged = !BooleanUtils.toStringYesNo(column.isNullable()).equalsIgnoreCase(columnInfo.getNullable());
        // 非空性变更可能会失败：需要保证列都要有值
        if (nullChanged) {
            modify.append(" ").append(column.isNullable() ? " null " : " not null ");
        }

        if (column.isUnique()) {
            String keyName = Constraint.generateName("UK_", this, column);
            UniqueKey uk = getOrCreateUniqueKey(keyName);
            uk.addColumn(column);
            modify.append(dialect.getUniqueDelegate()
                    .getColumnDefinitionUniquenessFragment(column));
        }

//                if (sqlTypeChanged || nullChanged) {
        return modify.toString();
//                }
    }

    protected String getDropColumnString() {
        return "drop column";
    }
}
