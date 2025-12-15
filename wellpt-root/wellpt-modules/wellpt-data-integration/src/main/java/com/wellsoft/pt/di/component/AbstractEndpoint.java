package com.wellsoft.pt.di.component;

import com.google.gson.Gson;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.di.anotation.EndpointParameter;
import com.wellsoft.pt.di.entity.DiConfigEntity;
import com.wellsoft.pt.di.enums.EdpParameterType;
import com.wellsoft.pt.di.service.DiConfigService;
import org.apache.camel.Component;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Set;

/**
 * Description:数据交换-端口定义
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
public abstract class AbstractEndpoint<COMPONENT extends AbstractDIComponent,
        PRODUCER extends Producer,
        CONSUMER extends Consumer> extends
        DefaultEndpoint {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    protected String diUuid;//数据交换配置UUID
    protected long delay = 1000L;//默认间隔1秒
    Class<PRODUCER> producerClass;
    Class<CONSUMER> consumerClass;
    Class<COMPONENT> componentClass;
    private Component component;


    public AbstractEndpoint() {
        types();
    }

    public AbstractEndpoint(String uri, COMPONENT component) {
        super(uri, component);
        types();
    }


    public AbstractEndpoint(String endpointUri) {
        super(endpointUri);
        types();
    }

    private void types() {
        try {
            Type type = this.getClass().getGenericSuperclass();
            if (type instanceof ParameterizedType) {
                Type[] parameterTypes = ((ParameterizedType) type).getActualTypeArguments();
                componentClass = (Class<COMPONENT>) parameterTypes[0];
                producerClass = (Class<PRODUCER>) parameterTypes[1];
                consumerClass = (Class<CONSUMER>) parameterTypes[2];
            }

        } catch (Exception e) {
        }
    }

    public PRODUCER createProducer() throws Exception {
        Constructor constructor = producerClass.getConstructor(this.getClass());
        return (PRODUCER) constructor.newInstance(this);
    }

    public CONSUMER createConsumer(Processor processor) throws Exception {
        Constructor constructor = consumerClass.getConstructor(this.getClass(), Processor.class);
        CONSUMER consumer = (CONSUMER) constructor.newInstance(this, processor);
        return consumer;
    }

    public boolean isSingleton() {
        return true;
    }

    public Class<PRODUCER> getProducerClass() {
        return producerClass;
    }

    public Class<CONSUMER> getConsumerClass() {
        return consumerClass;
    }

    public Class<COMPONENT> getComponentClass() {
        return componentClass;
    }


    public void init(String endpointUri, COMPONENT component) {
        super.setCamelContext(component == null ? null : component.getCamelContext());
        super.setEndpointUri(endpointUri);
        this.component = component;
        try {
            //解析数据交换配置ID
            this.diUuid = endpointUri.substring(endpointUri.indexOf("://") + 3,
                    endpointUri.indexOf("?"));
            if (StringUtils.isNotBlank(diUuid)) {
                //间隔时间设置
                DiConfigEntity diConfigEntity = ApplicationContextHolder.getBean(
                        DiConfigService.class).getOne(this.diUuid);
                if (diConfigEntity.getTimeInterval() != null) {
                    this.delay = diConfigEntity.getTimeInterval() * 1000;
                }
            }

        } catch (Exception e) {
        }

    }

    @Override
    public Component getComponent() {
        return component;
    }


    protected String toUri(String diUuid, String parameterJson, EdpParameterType type, Long delay) {
        HashMap<String, Object> map = new Gson().fromJson(parameterJson, HashMap.class);
        Set<String> keys = map.keySet();
        StringBuilder paramBuilder = new StringBuilder();
        for (String k : keys) {
            Object obj = map.get(k);
            if (obj == null) {
                continue;
            }
            Field field = null;
            Object defaultValue = null;
            try {
                field = this.getClass().getDeclaredField(k);
                field.setAccessible(true);
                defaultValue = field.get(this.getClass().newInstance());
            } catch (Exception e) {
                try {
                    if (field == null) {
                        field = this.getClass().getSuperclass().getDeclaredField(k);
                        defaultValue = field.get(this.getClass().newInstance());
                    }
                } catch (Exception e1) {
                    continue;
                }
            }

            if (StringUtils.isBlank(obj.toString())) {
                //使用默认值
                obj = defaultValue;
            }

            EndpointParameter endpointParameter = field.getAnnotation(EndpointParameter.class);
            if (endpointParameter == null || (!endpointParameter.type().equals(
                    type) && !endpointParameter.type().equals(EdpParameterType.BOTH))) {
                continue;
            }


            if (obj != null) {
                paramBuilder.append(k).append("=").append(obj.toString()).append("&");
            }
        }
        /*if (delay != null) {
            paramBuilder.append("delay=").append(
                    delay != null ? delay * 1000 : 1000);//设置间隔时间，默认：1秒间隔
        }*/
        return endpointPrefix() + "://" + diUuid + (paramBuilder.length() > 0 ?
                ("?" + paramBuilder.toString().substring(0, paramBuilder.length() - 1)) : "");
    }

    public String toConsumerUri(String diUuid, String parameterJson, Long delay) {
        return toUri(diUuid, parameterJson, EdpParameterType.CONSUMER, delay);
    }


    public String toProducerUri(String diUuid, String parameterJson) {
        return toUri(diUuid, parameterJson, EdpParameterType.PRODUCER, null);
    }

    public String getDiUuid() {
        return diUuid;
    }

    public void setDiUuid(String diUuid) {
        this.diUuid = diUuid;
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    /**
     * 是否暴露到前端使用
     *
     * @return
     */
    public boolean isExpose() {
        return true;
    }

    /**
     * 定义端点的前缀
     *
     * @return
     */
    public abstract String endpointPrefix();


    /**
     * 定义端点的名称
     *
     * @return
     */
    public abstract String endpointName();

}
