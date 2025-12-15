/*
 * @(#)2013-3-13 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.bean;

import java.io.Serializable;
import java.util.Date;

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
public class SynLogBean implements Serializable {

    private static final long serialVersionUID = -5960384622494058598L;

    private String category;// 所属分类

    private Date createTime;// 创建时间

    private Long stats;// 所花时间 | 计数

    private String data;// 数据

    public SynLogBean() {
    }

    public SynLogBean(String category, Long stats, String data) {
        this.data = data;
        this.stats = stats;
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
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
