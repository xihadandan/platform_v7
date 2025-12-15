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
public class ExampleIdempoetentProcessor extends AbstractIdempotentProcessor {
    @Override
    public boolean isIdempotent(Object o) {
        return false;
    }

    @Override
    public String name() {
        return "样例-幂等性判断";
    }
}
