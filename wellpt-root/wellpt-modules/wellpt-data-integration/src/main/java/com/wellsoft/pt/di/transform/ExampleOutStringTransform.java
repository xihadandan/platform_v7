package com.wellsoft.pt.di.transform;

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
public class ExampleOutStringTransform extends AbstractDataTransform<Object, String> {

    @ProcessorParameter(name = "参数1")
    private String param1;

    @ProcessorParameter(name = "参数2", domType = DIParameterDomType.TEXTAREA)
    private String param2;

    @Override
    public String name() {
        return "测试样例输出字符串转换器";
    }

    @Override
    public String transform(Object object) throws Exception {
        return "{\n" +
                "    \"timeConsuming\": 1000,\n" +
                "    \"diProcessorUuid\": \"xx\",\n" +
                "    \"diConfigUuid\": \"f8a60b0d-37ed-4af7-bd75-a9a864ff9fa3\",\n" +
                "    \"createTime\":1566358354554\n" +
                "}";
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
