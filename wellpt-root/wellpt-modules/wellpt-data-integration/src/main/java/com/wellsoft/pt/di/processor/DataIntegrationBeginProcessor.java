package com.wellsoft.pt.di.processor;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.di.constant.DiConstant;
import com.wellsoft.pt.di.service.DiDataInterationLogService;

/**
 * Description:数据交换开始的处理器：记录交换日志
 *
 * @author chenq
 * @date 2019/8/8
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/8/8    chenq		2019/8/8		Create
 * </pre>
 */
public class DataIntegrationBeginProcessor extends AbstractDIProcessor<Object> {


    @Override
    public String name() {
        return "开始数据交换处理器";
    }

    @Override
    void action(Object o) throws Exception {
        DiDataInterationLogService dataInterationLogService = ApplicationContextHolder.getBean(
                DiDataInterationLogService.class);
        dataInterationLogService.saveBeginLog(
                getProperty(DiConstant.DI_UUID_PROPERTY_NAME, String.class),
                getExchangeId());
    }

    @Override
    public boolean isExpose() {
        return false;
    }
}
