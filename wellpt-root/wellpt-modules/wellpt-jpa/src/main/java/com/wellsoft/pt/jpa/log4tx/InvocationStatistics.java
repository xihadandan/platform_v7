/*
 * @(#)2019年11月11日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.log4tx;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import java.io.Serializable;

/**
 * Description: 调用统计
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年11月11日.1	zhongzh		2019年11月11日		Create
 * </pre>
 * @date 2019年11月11日
 */
@JsonSerialize(include = Inclusion.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class InvocationStatistics implements Serializable {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    //
    private int count;// invoke count
    private long total;// time sum
    private long max = Long.MIN_VALUE;
    private long min = Long.MAX_VALUE;
    @JsonIgnore
    private InvocationStatistics parent;

    public InvocationStatistics() {
    }

    public InvocationStatistics(InvocationStatistics parent) {
        this.parent = parent;
    }

    /**
     * @param milliseconds
     */
    public InvocationStatistics(long milliseconds) {
        this.costTime(milliseconds);
    }

    /**
     * @param milliseconds
     */
    public void costTime(long milliseconds) {
        count += 1;
        total += milliseconds;
        if (max < milliseconds) {
            max = milliseconds;
        }
        if (milliseconds < min) {
            min = milliseconds;
        }
    }

    public int getCount() {
        return count;
    }

    public long getTotal() {
        return total;
    }

    public long getMax() {
        return max;
    }

    public long getMin() {
        return min;
    }

    public long getAvg() {
        return count == 0 ? 0 : total / count;
    }

    public String getPct() {
        if (parent == null || parent.total == 0) {
            return null;
        }
        return String.format("%.2f", (total * 100.0) / parent.total);
    }

    @Override
    public String toString() {
        ToStringHelper helper = MoreObjects.toStringHelper("statistic");
        String pct = getPct();
        if (pct != null) {
            helper.add("pct", pct);
        }
        helper.add("total", getTotal()).add("count", getCount());
        helper.add("max", getMax()).add("min", getMin()).add("avg", getAvg());
        return helper.toString();
    }
}
