package com.wellsoft.pt.di.processor;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/8/15
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/8/15    chenq		2019/8/15		Create
 * </pre>
 */
public abstract class AbstractIdempotentProcessor extends AbstractDIProcessor {

    /**
     * 判断幂等性
     *
     * @param o
     * @return
     */
    public abstract boolean isIdempotent(Object o);


    @Override
    void action(Object o) {
        if (!isIdempotent(o)) {//幂等性判断为重复
            faultStop("幂等性判断为重复数据，中止执行...");
        }
    }
}
