package org.hibernate.cfg;

import com.google.common.collect.Lists;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.i18n.MsgUtils;
import com.wellsoft.pt.dyform.implement.data.service.FormDataService;
import com.wellsoft.pt.dyform.implement.definition.enums.EnumSystemField;
import org.apache.commons.lang.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Constraint;
import org.hibernate.mapping.Table;
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
public abstract class AbstractTable extends Table {
    public final static String MAPPING_TYPE_BIG_DECIMAL = "java.math.BigDecimal";
    protected static final Logger logger = LoggerFactory.getLogger(AbstractTable.class);
    protected final static String COMMENT_KEY = "comment";
    protected final static String OLD_COLUMN_NAME_KEY = "oldName";
    protected JSONArray delFieldNamesJson;

    protected String defaultCatalog;

    protected String defaultSchema;


    public Iterator sqlAlterStrings(Dialect dialect, Mapping p, TableMetadata tableInfo,
                                    String defaultCatalog,
                                    String defaultSchema) throws HibernateException {
        this.defaultCatalog = defaultCatalog;
        this.defaultSchema = defaultSchema;
        List<String> results = new ArrayList();
        if (this.getComment() != null) {//删除字段、与表单的注释
            try {
                JSONObject commentJSON = new JSONObject(this.getComment());
                this.delFieldNamesJson = commentJSON.getJSONArray("delFieldNames");
                this.setComment(commentJSON.getString("comment"));
            } catch (Exception e) {
                logger.error("获取表删除字段与备注json异常：", e);
            }
        }

        results.addAll(modifyColumSqlScripts(dialect, p, tableInfo, defaultCatalog, defaultSchema));
        results.addAll(
                dropColumnSqlScripts(delFieldNamesJson, dialect, p, tableInfo, defaultCatalog,
                        defaultSchema));
        return results.iterator();
    }

    protected List<String> modifyColumSqlScripts(Dialect dialect, Mapping p,
                                                 TableMetadata tableInfo,
                                                 String defaultCatalog,
                                                 String defaultSchema) {
        List<String> scripts = Lists.newArrayList();
        String root = new StringBuilder("alter table ").append(
                getQualifiedName(dialect, defaultCatalog, defaultSchema)).append(' ').toString();
        String tblName = tableInfo.getName();
        Iterator iter = getColumnIterator();
        while (iter.hasNext()) {
            Column column = (Column) iter.next();
            String name = column.getQuotedName(dialect);
            String oldName = getOldName(column);
            String comment = getComment(column);
            column.setSqlType(column.getSqlType(dialect, p));
            ColumnMetadata columnInfo = tableInfo.getColumnMetadata(
                    column.getName().trim());// 数据库字段不存在空格,BUG:当表单字段有空格时
            ColumnMetadata oldColumnInfo = null;
            if (oldName != null && oldName.trim().length() > 0
                    && name.trim().equalsIgnoreCase(oldName.trim()) == false) {
                oldColumnInfo = tableInfo.getColumnMetadata(oldName.trim());// this
            }
            // the column doesnt exist at all.
            if (columnInfo == null && (oldColumnInfo == null)) {
                StringBuilder alter = new StringBuilder(root).append(
                        dialect.getAddColumnString())
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
                    alter.append(
                            dialect.getUniqueDelegate().getColumnDefinitionUniquenessFragment(
                                    column));
                }

                if (column.hasCheckConstraint() && dialect.supportsColumnCheck()) {
                    alter.append(" check(").append(column.getCheckConstraint()).append(")");
                }

                String columnComment = column.getComment();
                if (columnComment != null) {
                    alter.append(dialect.getColumnComment(columnComment));
                }

                alter.append(dialect.getAddColumnSuffixString());
                scripts.add(alter.toString());

            } else {

                // 修改字段名
                if (oldName == null || oldName.trim().length() == 0 || name.trim().equalsIgnoreCase(
                        oldName.trim())) {
                } else if (oldColumnInfo != null) {// 更新字段名//
                    // 目标数据库表不存在,在源数据库做字段名修改,此时不应该修改字段名.
                    StringBuilder alter = new StringBuilder(root).append(
                            " rename column  ").append(' ')
                            .append(oldName).append(' ').append(" to ").append(
                                    column.getQuotedName(dialect));

                    scripts.add(alter.toString());

                    columnInfo = oldColumnInfo;// this column is loaded from
                    // db
                }

                ColumnMetadata originalColumn = tableInfo.getColumnMetadata(column.getName());
                FormDataService dyFormDataService = (FormDataService) ApplicationContextHolder
                        .getBean(FormDataService.class);
                boolean clobChange2varchar = originalColumn != null && originalColumn.getTypeName().equalsIgnoreCase("clob") && column.getSqlType().toLowerCase().indexOf("varchar") != -1;
                if (originalColumn != null && (("clob".equalsIgnoreCase(column.getSqlType()) && !originalColumn.getTypeName().equalsIgnoreCase("clob"))
                        || clobChange2varchar)) {
                    if (clobChange2varchar) {
                        // clob 修改为 varchar, 要确认列数据不存在才允许
                        long count = dyFormDataService.queryTotalCountOfFormDataOfMainform(tblName, Restrictions.isNotNull(name));// 表示有数据
                        if (count > 0) {
                            throw new HibernateException(
                                    MsgUtils.getMessage("dyform.exception.dataExistColumn",
                                            new Object[]{name, count, tblName}));
                        }
                    }

                    // 修改为大字段类型
                    String backupColumnName = column.isQuoted() ? dialect.openQuote() + column.getName() + "__" + dialect.closeQuote() : column.getName() + "__";
                    // 1. 添加一个备份字段
                    scripts.add(new StringBuilder(root).append(
                            dialect.getAddColumnString())
                            .append(" (").append(backupColumnName).append(" ")
                            .append(!clobChange2varchar ? "clob" : column.getSqlType()).append(" )").toString());
                    if (!clobChange2varchar) {
                        // 2. 更新值到备份字段
                        scripts.add(new StringBuilder("UPDATE ").append(getQualifiedName(dialect, defaultCatalog, defaultSchema)
                        ).append(" SET ").append(backupColumnName).append("=").append(column.getQuotedName(dialect)).toString());
                    }
                    // 3. 删除原字段
                    scripts.add(new StringBuilder(root).append(
                            " drop column ").append(' ')
                            .append(column.getQuotedName(dialect)).toString());
                    // 4. 重命名备份字段名为原字段
                    scripts.add(new StringBuilder(root).append(
                            " rename column  ").append(' ')
                            .append(backupColumnName).append(' ').append(" to ").append(
                                    column.getQuotedName(dialect)).toString());
                    continue;
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
                        || (destType.indexOf(
                        "char") > 0 && columnInfo.getColumnSize() != column.getLength())) {// type

                    if (!typesMatch) {// 数据类型要做改变，列必须没有数据

                        long count = dyFormDataService.queryTotalCountOfFormDataOfMainform(
                                tblName,
                                Restrictions.isNotNull(name));// 表示有数据
                        if (count > 0) {
                            throw new HibernateException(
                                    MsgUtils.getMessage("dyform.exception.dataExistColumn",
                                            new Object[]{name, count, tblName}));
                        }

                    }

                    if ((column.getSqlType(dialect, p).toLowerCase().indexOf(
                            "char") > 0 && columnInfo
                            .getColumnSize() != column.getLength()) && /*非内置系统字段*/ EnumSystemField.value2EnumObj(column.getName()) == null) {// 发生了变化

                        if (reduceLength(columnInfo, column, destType)) {
                            long count = dyFormDataService.queryTotalCountOfFormDataOfMainform(
                                    tblName,
                                    Restrictions.isNotNull(name)
//                                    Restrictions.sqlRestriction(
//                                            " length(" + name + ") > " + column.getLength())
                            );
                            if (count > 0) {
                                throw new HibernateException(
                                        MsgUtils.getMessage("dyform.exception.dataExistColumn",
                                                new Object[]{name, count, tblName}));
//                                throw new HibernateException(
//                                        "field[" + name + "] has " + count + " data in table["
//                                                + tblName + "], data length is much longer than " + column.getLength());
                            }
                        }
                    }

                    StringBuilder alter = new StringBuilder(root).append(
                            " modify ").append(' ')
                            .append(column.getQuotedName(dialect)).append(' ')
                            .append(column.getSqlType(dialect, p));

                    scripts.add(alter.toString());

                }

                // number类型
                if ((!typesMatch || destType.indexOf(
                        "num") > -1) && /*非内置系统字段*/ EnumSystemField.value2EnumObj(column.getName()) == null) {
                    if (reduceLength(columnInfo, column, destType)) {
                        long count = dyFormDataService.queryTotalCountOfFormDataOfMainform(tblName,
                                Restrictions.isNotNull(name));// 表示有数据
                        if (count > 0) {
                            throw new HibernateException(
                                    MsgUtils.getMessage("dyform.exception.dataExistColumn",
                                            new Object[]{name, count, tblName}));
                        }
                    }

                    StringBuilder alter = new StringBuilder(root).append(
                            " modify ").append(' ')
                            .append(column.getQuotedName(dialect)).append(' ')
                            .append(column.getSqlType(dialect, p));

                    scripts.add(alter.toString());

                }

            }
        }

        scripts.addAll(Lists.<String>newArrayList(sqlCommentStrings(dialect, p, defaultCatalog, defaultSchema)));

        return scripts;
    }


    protected List<String> dropColumnSqlScripts(JSONArray delFieldNamesJson,
                                                Dialect dialect, Mapping p,
                                                TableMetadata tableInfo,
                                                String defaultCatalog,
                                                String defaultSchema) {
        List<String> scripts = Lists.newArrayList();
        String root = new StringBuilder("alter table ").append(
                getQualifiedName(dialect, defaultCatalog, defaultSchema)).append(' ').toString();
        if (delFieldNamesJson != null) {
            try {
                String tblName = tableInfo.getName();
                Map<String, Object> map = new HashMap<String, Object>();
                for (int i = 0; i < delFieldNamesJson.length(); i++) {
                    String fieldName = delFieldNamesJson.getString(i);
                    if (map.containsKey(fieldName.toLowerCase())) {// 排除重复
                        continue;
                    }
                    map.put(fieldName.toLowerCase(), null);
                    ColumnMetadata columnInfo = tableInfo.getColumnMetadata(fieldName);// this
                    if (columnInfo == null) {
                        System.out.println(
                                "fieldName[" + fieldName + "] is not exist in tbl " + tableInfo.getName());
                        continue;
                    }
                    FormDataService dyFormDataService = (FormDataService) ApplicationContextHolder
                            .getBean(FormDataService.class);
                    long count = dyFormDataService.queryTotalCountOfFormDataOfMainform(tblName,
                            Restrictions.isNotNull(fieldName));// 表示有数据
                    if (count > 0) {
                        throw new HibernateException(
                                MsgUtils.getMessage("dyform.exception.dataExistColumnForDel",
                                        new Object[]{fieldName, count, tblName}));
                    }
                    StringBuilder alter = new StringBuilder(root).append(
                            " drop column ").append(' ')
                            .append(fieldName);
                    scripts.add(alter.toString());
                }
            } catch (JSONException e1) {
                logger.error("解析表{}的删除字段json数据异常：", tableInfo.getName());
                throw new RuntimeException(e1);
            }
        }

        return scripts;
    }

    /**
     * 输出表、字段的相关注释
     *
     * @param dialect
     * @param defaultCatalog
     * @param defaultSchema
     * @return
     */
    public Iterator<String> sqlCommentStrings(Dialect dialect, String defaultCatalog,
                                              String defaultSchema) {
        return super.sqlCommentStrings(dialect, defaultCatalog, defaultSchema);
    }

    public abstract Iterator<String> sqlCommentStrings(Dialect dialect, Mapping mapping,
                                                       String defaultCatalog,
                                                       String defaultSchema);

    /**
     * 从注释json数据结构内解析出注释内容（表单创建时候的mapping映射xml会生成json的注释内容）
     *
     * @return
     */
    protected String explainCommentOut() {
        String comment = this.getComment();
        if (StringUtils.isNotBlank(comment)) {
            try {
                JSONObject commentJSON = new JSONObject(comment);
                return commentJSON.getString(COMMENT_KEY);
            } catch (Exception e) {
                return comment;
            }
        }
        return "";
    }

    /**
     * @param column
     * @return
     */
    protected String getComment(Column column) {
        return getPropertyFromComment(COMMENT_KEY, column);
    }

    private String getPropertyFromComment(String propertyName, Column column) {
        String comment = column.getComment();
        String propertyValue = null;
        if (comment != null && comment.trim().length() > 0) {
            try {
                JSONObject commentJSON = new JSONObject(comment);
                if (commentJSON.has(propertyName)) {
                    propertyValue = commentJSON.getString(propertyName);// 字段最原来的字段名
                }
            } catch (JSONException e) {
                logger.error("从注释的json数据内解析属性[{}]值异常：", propertyName, e);
            }
        }

        return propertyValue == null ? null : propertyValue;
    }


    private String getOldName(Column column) {
        String oldName = getPropertyFromComment(OLD_COLUMN_NAME_KEY, column);
        return oldName == null ? column.getName() : oldName;
    }

    private boolean reduceLength(ColumnMetadata columnInfo, Column column, String destType) {
        // 数字类型要判断是否缩小长度精度
        if (columnInfo.getTypeName().toLowerCase().indexOf("num") != -1 && destType.indexOf("num") != -1) {
            return columnInfo.getColumnSize() > column.getPrecision() || columnInfo.getDecimalDigits() > column.getScale();
        } else {
            return columnInfo.getColumnSize() > column.getLength();
        }
    }

    private boolean diffNum(ColumnMetadata columnInfo, Column column, String destType) {
        destType = destType.replaceAll(" ", "");
        String numType = "(" + columnInfo.getColumnSize() + "," + columnInfo.getDecimalDigits() + ")";
        return !destType.contains(numType);
    }
}
