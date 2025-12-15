package com.wellsoft.pt.di.transform;

import com.wellsoft.context.jdbc.entity.BaseEntity;
import com.wellsoft.pt.di.anotation.ProcessorParameter;
import com.wellsoft.pt.di.constant.DiConstant;
import com.wellsoft.pt.di.entity.DiDataProcessorLogEntity;
import com.wellsoft.pt.di.enums.DIParameterDomType;

import java.util.Date;
import java.util.HashMap;

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
public class ExampleOutBaseEntityTransform extends AbstractDataTransform<HashMap, BaseEntity> {

    @ProcessorParameter(name = "参数1")
    private String param1;

    @ProcessorParameter(name = "参数2", domType = DIParameterDomType.TEXTAREA)
    private String param2;

    @Override
    public String name() {
        return "测试样例输出实体类转换器";
    }

    @Override
    public BaseEntity transform(HashMap hashMap) throws Exception {
        DiDataProcessorLogEntity entity = new DiDataProcessorLogEntity();
        entity.setDiConfigUuid(super.getProperty(DiConstant.DI_UUID_PROPERTY_NAME, String.class));
        entity.setTimeConsuming(1000);
        entity.setDiProcessorUuid("xx");
        entity.setCreateTime(new Date());
        return entity;
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
