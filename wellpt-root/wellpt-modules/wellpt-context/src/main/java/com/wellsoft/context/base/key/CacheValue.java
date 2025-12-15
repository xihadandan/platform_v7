package com.wellsoft.context.base.key;

/**
 * @author lilin
 * @ClassName: CacheValue
 * @Description: TODO(这里用一句话描述这个类的作用)
 */
public class CacheValue {
    private long minVal;
    private long maxVal;

    public CacheValue() {
        this.minVal = 0L;
        this.maxVal = 0L;
    }

    public long getMinVal() {
        return minVal;
    }

    public void setMinVal(long minVal) {
        this.minVal = minVal;
    }

    public long getMaxVal() {
        return maxVal;
    }

    public void setMaxVal(long maxVal) {
        this.maxVal = maxVal;
    }

    public String toString() {
        return "{ minVal = " + this.minVal + " || maxVal = " + this.maxVal + " }";
    }

}
