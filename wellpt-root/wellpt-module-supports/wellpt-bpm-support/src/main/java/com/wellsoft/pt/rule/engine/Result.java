/*
 * @(#)2015-6-24 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.rule.engine;

/**
 * Description: 执行结果返回类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-6-24.1	zhulh		2015-6-24		Create
 * </pre>
 * @date 2015-6-24
 */
public class Result {
    // 返回
    public static final Result RETURN = new Result(0);
    // 正常结束
    public static final Result NORMAL = new Result(1);

    public int code;

    public Object data;

    /**
     *
     */
    public Result() {
        super();
    }

    /**
     * @param code
     */
    public Result(int code) {
        super();
        this.code = code;
    }

    /**
     * @param code
     * @param data
     */
    public Result(int code, Object data) {
        super();
        this.code = code;
        this.data = data;
    }

    /**
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + code;
        result = prime * result + ((data == null) ? 0 : data.hashCode());
        return result;
    }

    /**
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Result other = (Result) obj;
        if (code != other.code)
            return false;
        if (data == null) {
            if (other.data != null)
                return false;
        } else if (!data.equals(other.data))
            return false;
        return true;
    }

}
