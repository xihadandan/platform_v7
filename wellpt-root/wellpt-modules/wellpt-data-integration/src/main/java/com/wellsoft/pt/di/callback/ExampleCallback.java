package com.wellsoft.pt.di.callback;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/8/12
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/8/12    chenq		2019/8/12		Create
 * </pre>
 */
public class ExampleCallback extends AbstractDiCallback {


    @Override
    public void callback(Object responseObj, Object request) {
        //TODO:解析原始的请求报文requestObj，处理responseObj
        System.out.println();

    }

    @Override
    public String name() {
        return "回调函数样例1";
    }
}
