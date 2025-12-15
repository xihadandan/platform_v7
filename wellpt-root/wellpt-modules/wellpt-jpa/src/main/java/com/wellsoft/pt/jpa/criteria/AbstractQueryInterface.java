package com.wellsoft.pt.jpa.criteria;

import org.springframework.core.Ordered;

/**
 * Description: 接口查询的抽象
 *
 * @author Xiem
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 26 Dec 2016.1	Xiem		26 Dec 2016		Create
 * </pre>
 * @date 26 Dec 2016
 */
public abstract class AbstractQueryInterface implements QueryInterface, Ordered {

    /**
     * (non-Javadoc)
     *
     * @see org.springframework.core.Ordered#getOrder()
     */
    @Override
    public int getOrder() {
        return 0;
    }

}
