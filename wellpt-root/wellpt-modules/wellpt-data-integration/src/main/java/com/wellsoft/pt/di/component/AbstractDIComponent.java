package com.wellsoft.pt.di.component;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;
import org.apache.commons.lang.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;

/**
 * Description: 数据交换-组件定义
 *
 * @author chenq
 * @date 2019/7/11
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/7/11    chenq		2019/7/11		Create
 * </pre>
 */
public abstract class AbstractDIComponent<ENDPOINT extends AbstractEndpoint> extends
        DefaultComponent {

    public Class<ENDPOINT> endpointClass;

    public AbstractDIComponent() {
        super();
        try {
            Type type = this.getClass().getGenericSuperclass();
            if (type instanceof ParameterizedType) {
                Type[] parameterTypes = ((ParameterizedType) type).getActualTypeArguments();
                endpointClass = (Class<ENDPOINT>) parameterTypes[0];
            }
        } catch (Exception e) {

        }

    }

    public AbstractDIComponent(CamelContext context) {
        super(context);
        try {
            Type type = this.getClass().getGenericSuperclass();
            if (type instanceof ParameterizedType) {
                Type[] parameterTypes = ((ParameterizedType) type).getActualTypeArguments();
                endpointClass = (Class<ENDPOINT>) parameterTypes[0];
            }
        } catch (Exception e) {

        }
    }

    /**
     * 组件名称
     *
     * @return
     */
    protected abstract String name();

    protected Endpoint createEndpoint(String uri, String remaining,
                                      Map<String, Object> parameters) throws Exception {
        ENDPOINT endpoint = endpointClass.newInstance();
        endpoint.init(uri, this);
        setProperties(endpoint, parameters);
        return endpoint;
    }


    @Override
    protected void setProperties(Object bean, Map<String, Object> parameters) throws Exception {
        Set<String> keys = parameters.keySet();
        for (String k : keys) {
            if (parameters.get(k) == null || StringUtils.isBlank(parameters.get(k).toString())) {
                //设置默认值
                Field field = null;
                try {
                    field = endpointClass.getDeclaredField(k);
                } catch (Exception e) {
                    try {
                        if (field == null) {
                            field = endpointClass.getSuperclass().getDeclaredField(k);
                        }
                    } catch (Exception e1) {
                        continue;
                    }
                }
                if (field != null) {
                    field.setAccessible(true);
                    parameters.put(k, field.get(endpointClass.newInstance()));
                }
            }
        }

        super.setProperties(bean, parameters);
    }
}
