package com.wellsoft.pt.dm.sql;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.annotations.SerializedName;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.jpa.template.TemplateEngineFactory;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.io.Serializable;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2024年01月04日   chenq	 Create
 * </pre>
 */
public class SqlQueryObj implements Serializable {
    private String from;
    private String alias;
    private String joinType;
    private String unionType;
    private Boolean subView;
    private String joinOnCondition;
    private String subViewSqlObjJson;
    private List<QueryColumn> columns = Lists.newArrayList();
    private List<SqlQueryObj> children = Lists.newArrayList();
    private List<Where> where = Lists.newArrayList();

    private static Pattern NAMED_PARAM_PATTERN = Pattern.compile("(:(\\w+))");

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getSubViewSqlObjJson() {
        return subViewSqlObjJson;
    }

    public void setSubViewSqlObjJson(String subViewSqlObjJson) {
        this.subViewSqlObjJson = subViewSqlObjJson;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getJoinType() {
        return joinType;
    }

    public void setJoinType(String joinType) {
        this.joinType = joinType;
    }

    public String getUnionType() {
        return unionType;
    }

    public void setUnionType(String unionType) {
        this.unionType = unionType;
    }

    public Boolean getSubView() {
        return subView;
    }

    public void setSubView(Boolean subView) {
        this.subView = subView;
    }

    public String getJoinOnCondition() {
        return joinOnCondition;
    }

    public void setJoinOnCondition(String joinOnCondition) {
        this.joinOnCondition = joinOnCondition;
    }

    public List<QueryColumn> getColumns() {
        return columns;
    }

    public void setColumns(List<QueryColumn> columns) {
        this.columns = columns;
    }

    public List<SqlQueryObj> getChildren() {
        return children;
    }

    public void setChildren(List<SqlQueryObj> children) {
        this.children = children;
    }

    public List<Where> getWhere() {
        return where;
    }

    public void setWhere(List<Where> where) {
        this.where = where;
    }

    public static class QueryColumn implements Serializable {
        private String column;
        private String dataType;
        private String alias;
        @SerializedName("return")
        private Boolean isReturn;
        private String location;
        private Boolean hidden;

        public String getColumn() {
            return column;
        }

        public void setColumn(String column) {
            this.column = column;
        }

        public String getDataType() {
            return dataType;
        }

        public void setDataType(String dataType) {
            this.dataType = dataType;
        }

        public String getAlias() {
            return alias;
        }

        public void setAlias(String alias) {
            this.alias = alias;
        }

        public Boolean getIsReturn() {
            return isReturn;
        }

        public void setIsReturn(Boolean aReturn) {
            isReturn = aReturn;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public Boolean getHidden() {
            return hidden;
        }

        public void setHidden(Boolean hidden) {
            this.hidden = hidden;
        }
    }

    public static class Where implements Serializable {
        private String prop;
        private String sign;
        private String valueType;
        private String value;
        private String dataType;
        // ----- 仅 between 情况下才存在
        private String valueType2;
        private String value2;
        // -----
        private String sql;
        private Boolean sqlWord;
        private List<String> leftBracket = Lists.newArrayList();
        private List<String> rightBracket = Lists.newArrayList();

        public String getProp() {
            return prop;
        }

        public void setProp(String prop) {
            this.prop = prop;
        }

        public String getSign() {
            return sign;
        }

        public void setSign(String sign) {
            this.sign = sign;
        }

        public String getValueType() {
            return valueType;
        }

        public void setValueType(String valueType) {
            this.valueType = valueType;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public List<String> getLeftBracket() {
            return leftBracket;
        }

        public void setLeftBracket(List<String> leftBracket) {
            this.leftBracket = leftBracket;
        }

        public List<String> getRightBracket() {
            return rightBracket;
        }

        public void setRightBracket(List<String> rightBracket) {
            this.rightBracket = rightBracket;
        }

        public String getSql() {
            return sql;
        }

        public void setSql(String sql) {
            this.sql = sql;
        }

        public String getValueType2() {
            return valueType2;
        }

        public void setValueType2(String valueType2) {
            this.valueType2 = valueType2;
        }

        public String getValue2() {
            return value2;
        }

        public void setValue2(String value2) {
            this.value2 = value2;
        }

        public String getDataType() {
            return dataType;
        }

        public void setDataType(String dataType) {
            this.dataType = dataType;
        }

        public Boolean getSqlWord() {
            return sqlWord;
        }

        public void setSqlWord(Boolean sqlWord) {
            this.sqlWord = sqlWord;
        }
    }

    public static SqlQueryObj fromJsonString(String str) {
        return StringUtils.isNotBlank(str) ? JsonUtils.gson2Object(str, SqlQueryObj.class) : null;
    }


    private void sqlQueryObj2Sql(SqlQueryObj obj, StringBuilder sql, List<String> where, List<QueryColumn> backCols, Map<String, Object> namedParamValues) {
        if (StringUtils.isBlank(obj.getFrom()) || StringUtils.isNotBlank(obj.getUnionType())) {
            sql.append("( ");
            StringBuilder viewSql = new StringBuilder();
            List<String> viewWhere = Lists.newArrayList();
            if (StringUtils.isNotBlank(obj.getUnionType())) {
                // 合并情况
                if (backCols != null) {
                    backCols.addAll(obj.getColumns());
                }
                List<SqlQueryObj> joinObj = Lists.newArrayList();
                if (CollectionUtils.isNotEmpty(obj.getChildren())) {
                    List<String> unions = Lists.newArrayList();
                    for (SqlQueryObj subDto : obj.getChildren()) {
                        if (StringUtils.isBlank(subDto.getJoinType())) {
                            List<String> subWhere = Lists.newArrayList();
                            List<String> selectCols = Lists.newArrayList();
                            for (QueryColumn col : subDto.getColumns()) {
                                selectCols.add(col.getAlias());
                            }
                            StringBuilder subSql = new StringBuilder("");
                            sqlQueryObj2Sql(subDto, subSql, subWhere, null, namedParamValues);
                            StringBuilder subViewSql = new StringBuilder(" SELECT ");
                            subViewSql.append(StringUtils.join(selectCols, ",")).append(" FROM ").append(subSql);
                            unions.add(subViewSql.toString());
                        } else {
                            joinObj.add(subDto);
                        }
                    }
                    viewSql.append(StringUtils.join(unions, " " + obj.getUnionType() + " "));
                    sql.append(viewSql.toString()).append(" ) ").append(obj.getAlias());
                }
                if (!joinObj.isEmpty()) {
                    for (SqlQueryObj subDto : joinObj) {
                        sqlQueryObj2Sql(subDto, sql, viewWhere, backCols, namedParamValues);
                    }
                }


            } else {
                // 数据视图
                if (CollectionUtils.isNotEmpty(obj.getColumns())) {
                    if (backCols != null) {
                        backCols.addAll(obj.getColumns());
                    }
                }
                List<QueryColumn> subBackCols = Lists.newArrayList();
                sqlQueryObj2Sql(obj.getChildren().get(0), viewSql, viewWhere, subBackCols, namedParamValues);
                List<String> selectCols = Lists.newArrayList();
                Set<String> alias = Sets.newHashSet();
                for (QueryColumn col : subBackCols) {
                    if (alias.contains(col.getAlias())) {
                        selectCols.add(col.getLocation() + " AS " + col.getLocation().replace(".", "_"));
                    } else {
                        selectCols.add(col.getLocation());
                        alias.add(col.getAlias());
                    }

                }
                StringBuilder subViewSql = new StringBuilder();
                subViewSql.append(StringUtils.join(selectCols, ",")).append(" FROM ").append(viewSql);
                if (!viewWhere.isEmpty()) {
                    subViewSql.append(" WHERE ").append(StringUtils.join(viewWhere, " AND "));
                }
                sql.append(" SELECT ").append(subViewSql).append(" ) ").append(obj.getAlias());
                String y = doWhere(obj.getWhere(), namedParamValues);
                if (StringUtils.isNotBlank(y)) {
                    where.add(y);
                }
            }
        } else {
            if (StringUtils.isNotBlank(obj.getJoinType())) {
                sql.append(" ").append(obj.getJoinType()).append(" ");
            }
            String from = obj.getFrom();
            if (BooleanUtils.isTrue(obj.getSubView()) && StringUtils.isNotBlank(obj.getSubViewSqlObjJson())) {
                // 连接的是其他视图对象，动态解析视图对象sql涉及到的命名参数
                SqlQueryObj subSqlQueryObj = SqlQueryObj.fromJsonString(obj.getSubViewSqlObjJson());
                from = "( " + subSqlQueryObj.toSqlString(namedParamValues, true) + " ) ";
            }

            sql.append(from).append(" ").append(obj.getAlias());
            if (StringUtils.isNotBlank(obj.getJoinOnCondition())) {
                sql.append(" ").append(obj.getJoinOnCondition()).append(" ");
            }
            if (backCols != null) {
                backCols.addAll(obj.getColumns());
            }

            String y = doWhere(obj.getWhere(), namedParamValues);
            if (StringUtils.isNotBlank(y)) {
                where.add(y);
            }

            if (CollectionUtils.isNotEmpty(obj.getChildren())) {
                for (SqlQueryObj child : obj.getChildren()) {
                    sqlQueryObj2Sql(child, sql, where, backCols, namedParamValues);
                }
            }

        }

    }

    private String doWhere(List<Where> where, Map<String, Object> namedParamValues) {
        // 判断 where 中的命名参数是否存在
        if (CollectionUtils.isNotEmpty(where)) {
            List<Where> source = Lists.newArrayList(where);
            for (int i = 0; i < where.size(); i++) {
                Where w = where.get(i);
                boolean remove = false;
                if (BooleanUtils.isTrue(w.getSqlWord())) {
                    String sqlWord = null;
                    try {
                        sqlWord = TemplateEngineFactory.getDefaultTemplateEngine().process(
                                w.getSql(), namedParamValues);

                    } catch (Exception e) {
                        throw new RuntimeException("sql 处理异常: ", e);
                    }
                    if (StringUtils.isNotBlank(sqlWord)) {
                        source.get(i).setSql(sqlWord);
                        Matcher m = NAMED_PARAM_PATTERN.matcher(sqlWord);
                        Set<String> sqlWordParams = Sets.newHashSet();
                        while (m.find()) {
                            sqlWordParams.add(m.group(2));
                        }
                        if (!sqlWordParams.isEmpty()) {
                            if (MapUtils.isNotEmpty(namedParamValues)) {
                                Iterator<String> params = sqlWordParams.iterator();
                                while (params.hasNext()) {
                                    if (namedParamValues.containsKey(params.next())) {
                                        params.remove();
                                    }
                                }
                                if (!sqlWordParams.isEmpty()) { // 存在未设置的命名参数
                                    remove = true;
                                }
                            } else {
                                remove = true;
                            }

                        }
                    }
                } else if ("AND OR".indexOf(w.getSign()) == -1 &&
                        (("var".equalsIgnoreCase(w.getValueType()) && (namedParamValues == null || !namedParamValues.containsKey(w.getValue()))) ||
                                (w.getSign().equalsIgnoreCase("between") && "var".equalsIgnoreCase(w.getValueType2()) && (namedParamValues == null || !namedParamValues.containsKey(w.getValue2()))))) {
                    remove = true;
                }
                if (remove) {
                    // 移除条件的同时，需要移除与之配对的括号
                    List<String> leftBracket = w.getLeftBracket();
                    List<String> rightBracket = w.getRightBracket();
                    if (CollectionUtils.isNotEmpty(leftBracket)) {
                        Iterator<Where> iterator = source.iterator();
                        int lefts = leftBracket.size() - rightBracket.size();
                        if (lefts > 0) {
                            // 往后删除右括号
                            for (int j = i + 1, jlen = source.size(); j < jlen; j++) {
                                Where it = source.get(j);
                                if ("AND OR".indexOf(it.getSign()) == -1) {
                                    // 删除右括号
                                    List<String> rights = it.getRightBracket();
                                    int rlen = rights.size();
                                    if (rlen > 0) {
                                        if (rlen <= lefts) {
                                            it.getRightBracket().clear();
                                            if (rlen == lefts) {
                                                break;
                                            } else {
                                                lefts = lefts - rlen;
                                            }
                                        } else if (rlen > lefts) {
                                            it.setRightBracket(Collections.nCopies(rlen - lefts, ")"));
                                            break;
                                        }
                                    }
                                }
                            }

                        }
                    }

                    if (CollectionUtils.isNotEmpty(rightBracket)) {
                        int rights = rightBracket.size() - leftBracket.size();
                        if (rights > 0) {
                            // 往前删除左括号
                            for (int j = i - 1; j >= 0; j--) {
                                Where it = source.get(j);
                                if ("AND OR".indexOf(it.getSign()) == -1) {
                                    List<String> lefts = it.getLeftBracket();
                                    int llen = lefts.size();
                                    if (llen > 0) {
                                        if (llen <= rights) {
                                            it.getLeftBracket().clear();
                                            if (llen == rights) {
                                                break;
                                            } else {
                                                rights = rights - llen;
                                            }
                                        } else if (llen > rights) {
                                            it.setLeftBracket(Collections.nCopies(llen - rights, "("));
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }

                    source.remove(i);
                    where.remove(i);
                    if (i > 0) {
                        // 移除前面的连接符号
                        source.remove(i - 1);
                        where.remove(i - 1);
                        i = i - 2;
                    } else if (i == 0 && where.size() > 0 && "AND OR".indexOf(source.get(i).getSign()) != -1) {
                        // 移除后面的连接符号
                        source.remove(i);
                        where.remove(i);
                        i--;
                    }
                }
            }


            if (source.isEmpty()) {
                return null;
            }

            StringBuilder builder = new StringBuilder();
            for (Where w : source) {
                if ("AND OR".indexOf(w.getSign()) != -1) {
                    builder.append(" ").append(w.getSign()).append(" ");
                } else {
                    if (BooleanUtils.isTrue(w.getSqlWord())) {
                        builder.append(" ").append(CollectionUtils.isNotEmpty(w.getLeftBracket()) ? StringUtils.join(w.getLeftBracket(), " ") : "")
                                .append(w.getSql()).append(" ")
                                .append(CollectionUtils.isNotEmpty(w.getRightBracket()) ? StringUtils.join(w.getRightBracket(), " ") : "");
                    } else {
                        if ("is null".equalsIgnoreCase(w.getSign()) || "is not null".equalsIgnoreCase(w.getSign())) {
                            builder.append(" ").append(CollectionUtils.isNotEmpty(w.getLeftBracket()) ? StringUtils.join(w.getLeftBracket(), " ") : "")
                                    .append(w.getProp()).append(" ").append(w.getSign())
                                    .append(CollectionUtils.isNotEmpty(w.getRightBracket()) ? StringUtils.join(w.getRightBracket(), " ") : "");
                        } else if (w.getProp() != null) {
                            builder.append(" ").append(CollectionUtils.isNotEmpty(w.getLeftBracket()) ? StringUtils.join(w.getLeftBracket(), " ") : "")
                                    .append(w.getProp()).append(" ").append(w.getSign());
                            if ("var".equalsIgnoreCase(w.getValueType())) {
                                builder.append(" :").append(w.getValue());
                            } else if ("prop".equalsIgnoreCase(w.getValueType())) {
                                builder.append(" ").append(w.getValue());
                            } else if ("constant".equalsIgnoreCase(w.getValueType())) {
                                builder.append(" ").append(this.propConstantValueFormat(w.getValue(), w.getDataType(), w.getProp(), namedParamValues));
                            }
                            if ("between".equalsIgnoreCase(w.getSign())) {
                                builder.append(" AND ");
                                if ("var".equalsIgnoreCase(w.getValueType2())) {
                                    builder.append(" :").append(w.getValue2());
                                } else if ("prop".equalsIgnoreCase(w.getValueType2())) {
                                    builder.append(" ").append(w.getValue2());
                                } else if ("constant".equalsIgnoreCase(w.getValueType2())) {
                                    builder.append(" ").append(this.propConstantValueFormat(w.getValue2(), w.getDataType(), w.getProp(), namedParamValues));
                                }
                            }
                            builder.append(CollectionUtils.isNotEmpty(w.getRightBracket()) ? StringUtils.join(w.getRightBracket(), " ") : "");
                        }
                    }

                }

            }
            return builder.toString();

        }
        return null;

    }

    private Object propConstantValueFormat(String value, String dataType, String prop, Map<String, Object> namedParamValues) {
        if ("number".equalsIgnoreCase(dataType)) {
            return value;
        } else if ("timestamp".equalsIgnoreCase(dataType)) {
            // 日期常量值要替换为命名参数进行传递
            String dateNamedParam = prop.replace(".", "_");
            int i = 0;
            while (namedParamValues.containsKey(dateNamedParam)) {
                dateNamedParam = dateNamedParam + "_" + (i++);
            }
            try {
                namedParamValues.put(dateNamedParam, DateUtils.parseDate(value, "yyyyMMddHHmmss", "yyyy-MM-dd HH:mm:ss",
                        "yyyy年MM月dd日HH时mm分ss秒", "yyyy年MM月dd日 HH时mm分ss秒", "yyyy", "yyyyMM", "yyyy-MM", "yyyyMMdd", "yyyy-MM-dd", "yyyy年", "yyyy年MM月", "yyyy年MM月dd日"));
            } catch (Exception e) {
                throw new RuntimeException("不支持的日期字符格式");
            }
            return ":" + dateNamedParam;
        }
        return "'" + value + "'";
    }

    public String toSqlString(Map<String, Object> namedParamValues, boolean format) {

        StringBuilder sql = new StringBuilder();
        List<String> where = Lists.newArrayList();
        List<QueryColumn> backCols = Lists.newArrayList();

        sqlQueryObj2Sql(this, sql, where, backCols, namedParamValues);
        StringBuilder finalSql = new StringBuilder();
        finalSql.append("SELECT ");
        if (CollectionUtils.isNotEmpty(backCols)) {
            List<String> selectCols = Lists.newArrayList();
            Set<String> alias = Sets.newHashSet();
            for (QueryColumn col : backCols) {
                if (col.getIsReturn()) {
                    if (alias.contains(col.getAlias())) {
                        selectCols.add(col.getLocation() + " AS " + col.getLocation().replace(".", "_"));
                    } else {
                        selectCols.add(col.getLocation());
                        alias.add(col.getAlias());
                    }
                }
            }
            finalSql.append(StringUtils.join(selectCols, " , ")).append(" FROM ").append(sql);
            if (where.size() != 0) {
                finalSql.append(" WHERE ").append(StringUtils.join(where, " AND "));
            }
        }
        return format ? SQLUtils.format(finalSql.toString(), DbType.oracle) : finalSql.toString();
    }


}
