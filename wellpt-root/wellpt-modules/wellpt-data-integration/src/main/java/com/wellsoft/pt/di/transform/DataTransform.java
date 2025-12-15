package com.wellsoft.pt.di.transform;

import org.apache.camel.Expression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * Description: 数据交换转换器接口
 *
 * @author chenq
 * @date 2019/7/9
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/7/9    chenq		2019/7/9		Create
 * </pre>
 */
public interface DataTransform<IN extends Serializable, OUT extends Serializable> extends
        Expression {


    Logger logger = LoggerFactory.getLogger(DataTransform.class);

    /**
     * 转换器名称
     *
     * @return
     */
    String name();

}
