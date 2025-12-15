package com.wellsoft.pt.dyform.implement.definition.cache;

/**
 * 被缓存的对象
 *
 * @author hunt
 */
public class CachedObject {
    Object value;
    long createTime;//创建时间
    long timeout;//超时时长


    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }


    public boolean doIsTimeout() {
        if ((System.currentTimeMillis() - this.getCreateTime()) > this.getTimeout()) {//超时了
            return true;
        }
        return false;
    }

    /**
     * 创建时间设置为当前时间
     */
    public void doBindCreateTimeAsNow() {
        this.setCreateTime(System.currentTimeMillis());

    }


}
