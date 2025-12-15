/*
 * @(#)2020年6月12日 V1.0
 *
 * Copyright 2020 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.context.web.process;

import java.io.Serializable;

/**
 * Description: 进度消息
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年6月12日.1	zhongzh		2020年6月12日		Create
 * </pre>
 * @date 2020年6月12日
 */
public class ProcessMessage implements Serializable {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    private long total;
    private long current;
    private String taskName;
    private String subTaskName;

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getCurrent() {
        return current;
    }

    public void setCurrent(long current) {
        this.current = current;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getSubTaskName() {
        return subTaskName;
    }

    public void setSubTaskName(String subTaskName) {
        this.subTaskName = subTaskName;
    }

    /**
     * 重置
     */
    public void reset() {
        setTotal(0);
        setCurrent(0);
        setTaskName(null);
        setSubTaskName(null);
    }

    /**
     * 递增
     *
     * @param increase
     * @return
     */
    public long increase(long increase) {
        return current += increase;
    }

}
