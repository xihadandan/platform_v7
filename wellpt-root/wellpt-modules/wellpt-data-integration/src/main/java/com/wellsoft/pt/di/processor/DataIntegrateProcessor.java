package com.wellsoft.pt.di.processor;

import org.apache.camel.Processor;

/**
 * Description: 数据交换-处理器接口
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
public interface DataIntegrateProcessor<BODY> extends Processor {


    /**
     * 名称
     *
     * @return
     */
    String name();


}
