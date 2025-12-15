package com.wellsoft.pt.common.observer;

import org.springframework.util.Assert;

/**
 * Description: 观察者实现类描述器,用于描述一个观察者的所属类及用途
 *
 * @author hongjz
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017-4-26.1	hongjz		2017-4-26		Create
 * </pre>
 * @date 2017-4-26
 */
@SuppressWarnings("rawtypes")
public class ObserverImplClassDescriptor {

    private Class observerImplClazz;//观察者实现类
    private Object observerImplInstance;//观察者实现类实例

    private String purposeRemark;//该观察者实现类用途

    public ObserverImplClassDescriptor(Class observerImplClazz, String purposeRemark) {
        Assert.notNull(observerImplClazz, "观察者实现类不得为空");
        Assert.notNull(purposeRemark, "观察者实现类的作用描述不得为空");
        this.observerImplClazz = observerImplClazz;
        this.purposeRemark = purposeRemark;
    }

    public Class getObserverImplClazz() {
        return observerImplClazz;
    }

    public String getPurposeRemark() {
        return purposeRemark;
    }

    public Object getObserverImplInstance() {
        return observerImplInstance;
    }

    protected void setObserverImplInstance(Object observerImplInstance) {
        this.observerImplInstance = observerImplInstance;
    }


}
