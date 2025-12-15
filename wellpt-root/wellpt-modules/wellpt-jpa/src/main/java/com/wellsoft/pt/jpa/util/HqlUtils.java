package com.wellsoft.pt.jpa.util;

import com.google.common.collect.Lists;
import com.wellsoft.pt.jpa.hibernate.SessionFactoryUtils;
import com.wellsoft.pt.jpa.support.CustomDm7DBDialect;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.internal.SessionFactoryImpl;

import java.io.Serializable;
import java.util.*;

/**
 * @author yt
 * @title: SqlUtils
 * @date 2020/6/17 3:51 下午
 */
public class HqlUtils {


    public static void notInSql(String inField, Map<String, Object> query, StringBuilder hqlSb, Set<Serializable> set) {
        //防止sql注入
        inField = StringEscapeUtils.escapeSql(inField);
        boolean spliceFlg = false;
        //达梦 参数最多 2048 转 sql 拼接
        if (((SessionFactoryImpl) SessionFactoryUtils.getMultiTenantSessionFactory()).getDialect() instanceof CustomDm7DBDialect) {
            Set<Serializable> newSet = new HashSet<>();
            for (Serializable s : set) {
                if (s instanceof String) {
                    newSet.add(StringEscapeUtils.escapeSql(Objects.toString(s)));
                } else {
                    newSet.add(s);
                }
            }
            set = newSet;
            spliceFlg = true;
        }
        Map<String, List<Serializable>> sqlInMap = sqlInMap(inField, set);
        Iterator<String> iterator = sqlInMap.keySet().iterator();
        hqlSb.append("(");
        while (iterator.hasNext()) {
            String key = iterator.next();
            if (spliceFlg) {
                hqlSb.append(" ").append(inField).append(" not in ('").append(StringUtils.join(sqlInMap.get(key), "','")).append("') and");
            } else {
                hqlSb.append(" " + inField + " not in (:" + key + ") and");
                query.put(key, sqlInMap.get(key));
            }
        }
        hqlSb.delete(hqlSb.length() - 3, hqlSb.length());
        hqlSb.append(")");
    }

    /**
     * 组装 in sql 添加query
     * (inField in (1,2,3...1000) or inField in (10001,...2000))
     *
     * @param inField
     * @param query
     * @param hqlSb
     * @param set
     */
    public static void appendSql(String inField, Map<String, Object> query, StringBuilder hqlSb, Set<Serializable> set) {
        //防止sql注入
        inField = StringEscapeUtils.escapeSql(inField);
        boolean spliceFlg = false;
        //达梦 参数最多 2048 转 sql 拼接
        if (((SessionFactoryImpl) SessionFactoryUtils.getMultiTenantSessionFactory()).getDialect() instanceof CustomDm7DBDialect) {
            Set<Serializable> newSet = new HashSet<>();
            for (Serializable s : set) {
                if (s instanceof String) {
                    newSet.add(StringEscapeUtils.escapeSql(Objects.toString(s)));
                } else {
                    newSet.add(s);
                }
            }
            set = newSet;
            spliceFlg = true;
        }
        Map<String, List<Serializable>> sqlInMap = sqlInMap(inField, set);
        Iterator<String> iterator = sqlInMap.keySet().iterator();
        hqlSb.append("(");
        while (iterator.hasNext()) {
            String key = iterator.next();
            if (spliceFlg) {
                hqlSb.append(" ").append(inField).append(" in ('").append(StringUtils.join(sqlInMap.get(key), "','")).append("') or");
            } else {
                hqlSb.append(" " + inField + " in (:" + key + ") or");
                query.put(key, sqlInMap.get(key));
            }
        }
        hqlSb.delete(hqlSb.length() - 2, hqlSb.length());
        hqlSb.append(")");
    }

    /**
     * 拆分in 数据量
     * oracle 对 in 查询数据量 有限制
     *
     * @param set
     * @return
     */
    private static Map<String, List<Serializable>> sqlInMap(String inField, Set<Serializable> set) {
        int maxInsize = 1000;
        Map<String, List<Serializable>> map = new HashMap<>();
        inField = inField.replace(".", "_");
        //大于1000 拆分
        if (set.size() > maxInsize) {
            int inIndex = set.size() % maxInsize == 0 ? set.size() / maxInsize : set.size() / maxInsize + 1;
            List<Serializable> list = Lists.newArrayList(set.iterator());
            for (int i = 1; i <= inIndex; i++) {
                int start = (i - 1) * maxInsize;
                int end = start + maxInsize;
                if (end > list.size()) {
                    end = list.size();
                }
                String inKey = inField + i;
                map.put(inKey, list.subList(start, end));
            }
        } else {
            map.put(inField, Lists.newArrayList(set.iterator()));
        }
        return map;
    }
}
