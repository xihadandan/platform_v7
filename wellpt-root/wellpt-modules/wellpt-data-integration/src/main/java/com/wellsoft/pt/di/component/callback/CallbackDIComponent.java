package com.wellsoft.pt.di.component.callback;

import com.wellsoft.pt.di.component.AbstractDIComponent;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/7/23
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/7/23    chenq		2019/7/23		Create
 * </pre>
 */
public class CallbackDIComponent extends AbstractDIComponent<CallbackEndpoint> {
    @Override
    protected String name() {
        return "回调类组件";
    }
}
