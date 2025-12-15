package com.wellsoft.pt.di.processor;

import com.wellsoft.pt.di.anotation.ProcessorParameter;
import com.wellsoft.pt.di.enums.DIParameterDomType;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/8/7
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/8/7    chenq		2019/8/7		Create
 * </pre>
 */
public class ExampleProcessor extends AbstractDIProcessor<Object> {

    @ProcessorParameter(name = "参数1")
    private String param1;

    @ProcessorParameter(name = "参数2", domType = DIParameterDomType.TEXTAREA)
    private String param2;


    @Override
    void action(Object idEntity) throws Exception {
//        throw new RuntimeException("测试异常");
        Object[] objects = new Object[2];
        objects[0] = "ID-CQ-52019-1566206526724-0-8";
        objects[1] = "leo";
        EXCHANGE.get().getIn().setBody(objects);

    }

    @Override
    public String name() {
        return "测试样例处理器1";
    }

    public String getParam1() {
        return param1;
    }

    public void setParam1(String param1) {
        this.param1 = param1;
    }

    public String getParam2() {
        return param2;
    }

    public void setParam2(String param2) {
        this.param2 = param2;
    }
}
