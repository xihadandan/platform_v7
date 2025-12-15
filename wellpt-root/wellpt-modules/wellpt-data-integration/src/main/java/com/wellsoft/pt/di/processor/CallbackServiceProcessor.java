package com.wellsoft.pt.di.processor;

import com.wellsoft.pt.di.entity.DiCallbackRequestEntity;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/7/24
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/7/24    chenq		2019/7/24		Create
 * </pre>
 */
public class CallbackServiceProcessor extends
        AbstractDIProcessor<DiCallbackRequestEntity> {

    @Override
    void action(DiCallbackRequestEntity diCallbackRequestEntity) throws Exception {

    }

    @Override
    public String name() {
        return null;
    }

    @Override
    public boolean isExpose() {
        return false;
    }
}
