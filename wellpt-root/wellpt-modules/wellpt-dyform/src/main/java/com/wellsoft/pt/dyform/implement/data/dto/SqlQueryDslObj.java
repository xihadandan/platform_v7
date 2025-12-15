package com.wellsoft.pt.dyform.implement.data.dto;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
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
 * 2024年03月18日   chenq	 Create
 * </pre>
 */
public class SqlQueryDslObj implements Serializable {

    private static Pattern NAMED_PARAM_PATTERN = Pattern.compile("(:(\\w+))");


    private List<Where> where = Lists.newArrayList();

    private String order;

    private Map<String, Object> namedParams = Maps.newHashMap();

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
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

    public String getWhere(Map<String, Object> namedParams) {
        Map<String, Object> namedParamValues = Maps.newHashMap();
        namedParamValues.putAll(this.namedParams);
        if (MapUtils.isNotEmpty(namedParams)) {
            namedParamValues.putAll(namedParams);
        }
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

    public List<Where> getWhere() {
        return where;
    }

    public void setWhere(List<Where> where) {
        this.where = where;
    }

    public Map<String, Object> getNamedParams() {
        return namedParams;
    }

    public void setNamedParams(Map<String, Object> namedParams) {
        this.namedParams = namedParams;
    }
}
