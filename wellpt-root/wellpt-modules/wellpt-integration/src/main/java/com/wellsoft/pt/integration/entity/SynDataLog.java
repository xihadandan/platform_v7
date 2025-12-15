/*
 * @(#)2013-3-13 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-4-28.1	zhongzh		2014-4-28		Create
 * </pre>
 * @date 2014-4-28
 */
@Entity
@Table(name = "is_syn_data_log")
@DynamicUpdate
@DynamicInsert
public class SynDataLog extends IdEntity {

    public static final String READ_IN = "RI";// 取数据
    public static final String WRITE_OUT = "WO";// 写关闸
    public static final String IN_STATE = "IS";// 写状态
    public static final String READ_OUT = "RO";// 读关闸
    public static final String WRITE_IN = "WI";// 写数据
    public static final String OUT_STATE = "OS";// 写状态

    public static final String FEED_CLEAN = "FC";

    public static final String OUT_READ = "OR";// 回馈读
    public static final String IN_WRITE = "IW";// 回馈写
    public static final String OUT_WRITE = "OW";// 回馈读
    public static final String IN_READ = "IR";// 回馈写

    public static final String SUB_ALL = "A"; // SUB全部

    public static final String SUB_LIST = "L"; // SUB:TIG_TABLE
    public static final String SUB_FILE = "F";
    public static final String SUB_DATA = "D";
    public static final String SUB_CLOB = "C";

    public static final String READ_PROPERTY = "RP";

    public static final String FAIL_COUNT_OUT = "FO";// 写关闸失败
    public static final String FAIL_COUNT_IN = "FI";// 写数据失败

    private static final long serialVersionUID = -5960384622494058598L;

    private String category;//所属分类

    private Long stats;//所花时间 | 计数

    private String data;//数据

    public SynDataLog(String category, Long stats, Object data) {
        if (data != null) {
            this.data = data.toString();
        }
        this.stats = stats;
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Long getStats() {
        return stats;
    }

    public void setStats(Long stats) {
        this.stats = stats;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

}
