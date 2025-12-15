/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Inc.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.hibernate.cfg;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.i18n.MsgUtils;
import com.wellsoft.pt.dyform.implement.data.service.FormDataService;
import org.apache.commons.lang.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Constraint;
import org.hibernate.mapping.UniqueKey;
import org.hibernate.tool.hbm2ddl.ColumnMetadata;
import org.hibernate.tool.hbm2ddl.TableMetadata;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * A relational table
 * FIXBUG:1、表单字段名存在空格时,会再添加字段.
 * 2、目标数据库表不存在,在源数据库做字段名修改,此时不应该修改字段名.
 *
 * @author Gavin King
 */
public class CustomTable extends org.hibernate.mapping.Table {
    public static final String MAPPING_TYPE_BIG_DECIMAL = "java.math.BigDecimal";
    private Logger logger = LoggerFactory.getLogger(CustomTable.class);

    /**
     * @param results
     * @param alter   脚本
     * @param tblName 表名
     * @param name    字段名
     * @param comment 备注
     */
    private static void setCommentScript(List results, String tblName, String name, String comment) {

        StringBuilder commentScript = new StringBuilder();
        if (StringUtils.isBlank(comment)) {
            return;
        }
        commentScript.append(" comment on column ");
        commentScript.append(tblName).append(".").append(name);
        commentScript.append(" is '").append(comment).append("'");
        results.add(commentScript.toString());
    }

    public Iterator sqlAlterStrings(Dialect dialect, Mapping p, TableMetadata tableInfo, String defaultCatalog,
                                    String defaultSchema) throws HibernateException {

        StringBuilder root = new StringBuilder("alter table ").append(
                getQualifiedName(dialect, defaultCatalog, defaultSchema)).append(' ');

        List results = new ArrayList();
        String tblName = tableInfo.getName();

        {// 添加字段、修改字段类型，以及修改字段名，修改字段长度

            Iterator iter = getColumnIterator();
            while (iter.hasNext()) {
                Column column = (Column) iter.next();
                String name = column.getQuotedName(dialect);
                String oldName = getOldName(column);
                String comment = getComment(column);

                // ColumnMetadata columnInfo =
                // tableInfo.getColumnMetadata(column.getName());//this column
                // is loaded from db
                ColumnMetadata columnInfo = tableInfo.getColumnMetadata(column.getName().trim());// 数据库字段不存在空格,BUG:当表单字段有空格时
                ColumnMetadata oldColumnInfo = null;
                if (oldName != null && oldName.trim().length() > 0
                        && name.trim().equalsIgnoreCase(oldName.trim()) == false) {
                    oldColumnInfo = tableInfo.getColumnMetadata(oldName.trim());// this
                    // column
                    // is
                    // loaded
                    // from
                    // db
                }
                // the column doesnt exist at all.
                if (columnInfo == null && (oldColumnInfo == null)) {
                    StringBuilder alter = new StringBuilder(root.toString()).append(dialect.getAddColumnString())
                            .append(' ').append(column.getQuotedName(dialect)).append(' ')
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

                    if (column.isUnique()) {
                        String keyName = Constraint.generateName("UK_", this, column);
                        UniqueKey uk = getOrCreateUniqueKey(keyName);
                        uk.addColumn(column);
                        alter.append(dialect.getUniqueDelegate().getColumnDefinitionUniquenessFragment(column));
                    }

                    if (column.hasCheckConstraint() && dialect.supportsColumnCheck()) {
                        alter.append(" check(").append(column.getCheckConstraint()).append(")");
                    }

                    String columnComment = column.getComment();
                    if (columnComment != null) {
                        alter.append(dialect.getColumnComment(columnComment));
                    }

                    alter.append(dialect.getAddColumnSuffixString());
                    results.add(alter.toString());

                } else {

                    // 修改字段名
                    if (oldName == null || oldName.trim().length() == 0 || name.trim().equalsIgnoreCase(oldName.trim())) {
                    } else if (oldColumnInfo != null) {// 更新字段名//
                        // 目标数据库表不存在,在源数据库做字段名修改,此时不应该修改字段名.
                        StringBuilder alter = new StringBuilder(root.toString()).append(" rename column  ").append(' ')
                                .append(oldName).append(' ').append(" to ").append(column.getQuotedName(dialect));

                        results.add(alter.toString());

                        columnInfo = oldColumnInfo;// this column is loaded from
                        // db
                    }

                    // 修改字段长度及类型
                    int typeCode1 = columnInfo.getTypeCode();
                    int typeCode2 = column.getSqlTypeCode(p);

                    String srcType = columnInfo.getTypeName().toLowerCase();
                    String destType = column.getSqlType(dialect, p).toLowerCase();
                    boolean typesMatch = (destType.startsWith(srcType) || typeCode1 == typeCode2);
                    if ((destType.indexOf("double") != -1 && srcType.indexOf("float") != -1)) {
                        typesMatch = true;// oracle中double,float都是表现为float
                    }

                    if (!typesMatch
                            || (destType.indexOf("char") > 0 && columnInfo.getColumnSize() != column.getLength())) {// type
                        // of
                        // column
                        // is
                        // changed,or
                        // string
                        // length
                        // is
                        // changed
                        FormDataService dyFormDataService = (FormDataService) ApplicationContextHolder
                                .getBean(FormDataService.class);
                        if (!typesMatch) {// 数据类型要做改变，列必须没有数据

                            long count = dyFormDataService.queryTotalCountOfFormDataOfMainform(tblName,
                                    Restrictions.isNotNull(name));// 表示有数据
                            if (count > 0) {
                                throw new HibernateException(MsgUtils.getMessage("dyform.exception.dataExistColumn",
                                        new Object[]{name, count, tblName}));
                            }

                        }

                        if ((column.getSqlType(dialect, p).toLowerCase().indexOf("char") > 0 && columnInfo
                                .getColumnSize() != column.getLength())) {// 发生了变化
                            logger.error(name + " columnInfo .getColumnSize()=" + columnInfo.getColumnSize()
                                    + ",  column.getLength()=" + column.getLength());
                            long count = dyFormDataService.queryTotalCountOfFormDataOfMainform(tblName,
                                    Restrictions.sqlRestriction(" length(" + name + ") > " + column.getLength()));
                            if (count > 0) {
                                throw new HibernateException("field[" + name + "] has " + count + " data in table["
                                        + tblName + "], data length is much longer than " + column.getLength());
                            }
                        }

                        StringBuilder alter = new StringBuilder(root.toString()).append(" modify ").append(' ')
                                .append(column.getQuotedName(dialect)).append(' ')
                                .append(column.getSqlType(dialect, p));

                        results.add(alter.toString());

                    }

                    // number类型
                    if ((!typesMatch || destType.indexOf("number") > -1) && !"REC_VER".equalsIgnoreCase(name)
                            && diffNum(columnInfo, column, destType)) {
                        FormDataService dyFormDataService = (FormDataService) ApplicationContextHolder
                                .getBean(FormDataService.class);
                        long count = dyFormDataService.queryTotalCountOfFormDataOfMainform(tblName,
                                Restrictions.isNotNull(name));// 表示有数据
                        if (count > 0) {
                            throw new HibernateException(MsgUtils.getMessage("dyform.exception.dataExistColumn",
                                    new Object[]{name, count, tblName}));
                        }

                        StringBuilder alter = new StringBuilder(root.toString()).append(" modify ").append(' ')
                                .append(column.getQuotedName(dialect)).append(' ')
                                .append(column.getSqlType(dialect, p));

                        results.add(alter.toString());

                    }

                }
                setCommentScript(results, tblName, name, comment);
                // results.add(getCommentScript(tblName, name,
                // comment).toString());
            }
        }

        {// 删除字段

            String commment = this.getComment();
            if (commment == null) {
                return results.iterator();
            }
            try {
                JSONObject commentJSON = new JSONObject(commment);
                JSONArray delFieldNamesJson = commentJSON.getJSONArray("delFieldNames");
                Map<String, Object> map = new HashMap<String, Object>();
                for (int i = 0; i < delFieldNamesJson.length(); i++) {
                    String fieldName = delFieldNamesJson.getString(i);
                    if (map.containsKey(fieldName.toLowerCase())) {// 排除重复
                        continue;
                    }
                    map.put(fieldName.toLowerCase(), null);
                    ColumnMetadata columnInfo = tableInfo.getColumnMetadata(fieldName);// this
                    // column
                    // is
                    // loaded
                    // from
                    // db
                    if (columnInfo == null) {
                        System.out.println("fieldName[" + fieldName + "] is not exist in tbl " + tableInfo.getName());
                        continue;
                    }
                    FormDataService dyFormDataService = (FormDataService) ApplicationContextHolder
                            .getBean(FormDataService.class);
                    long count = dyFormDataService.queryTotalCountOfFormDataOfMainform(tblName,
                            Restrictions.isNotNull(fieldName));// 表示有数据
                    if (count > 0) {
                        throw new HibernateException(MsgUtils.getMessage("dyform.exception.dataExistColumnForDel",
                                new Object[]{fieldName, count, tblName}));
                    }
                    StringBuilder alter = new StringBuilder(root.toString()).append(" drop column ").append(' ')
                            .append(fieldName);
                    results.add(alter.toString());
                }
            } catch (JSONException e1) {
                logger.info(e1.getMessage(), e1);
                return results.iterator();
            }
        }

        return results.iterator();
    }

    private boolean diffNum(ColumnMetadata columnInfo, Column column, String destType) {
        destType = destType.replaceAll(" ", "");
        String numType = "(" + columnInfo.getColumnSize() + "," + columnInfo.getDecimalDigits() + ")";
        return !destType.contains(numType);
    }

    private String getComment(Column column) {
        return getPropertyFromComment("comment", column);
    }

    /**
     * 如果是number(x,0)，则为整形，修改默认的精度和小数点 add by wujx 20160715
     *
     * @param column
     */
    private void set2Integer(Column column) {
        String sqlType = column.getSqlType();
        int precision = Integer.parseInt(sqlType.substring(sqlType.indexOf("(") + 1, sqlType.indexOf(",")));
        int scale = Integer.parseInt(sqlType.substring(sqlType.indexOf(",") + 1, sqlType.indexOf(")")));
        if (scale == 0) {
            column.setPrecision(precision);
            column.setScale(scale);
        }
    }

    private String getOldName(Column column) {
        String oldName = getPropertyFromComment("oldName", column);
        return oldName == null ? column.getName() : oldName;

    }

    private String getPropertyFromComment(String propertyName, Column column) {
        String comment = column.getComment();
        String propertyValue = null;
        if (comment != null && comment.trim().length() > 0) {
            try {
                JSONObject commentJSON = new JSONObject(comment);
                propertyValue = commentJSON.getString(propertyName);// 字段最原来的字段名
            } catch (JSONException e) {
                logger.error(e.getMessage(), e);
            }
        }

        return propertyValue == null ? null : propertyValue;
    }

}
