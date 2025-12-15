package com.wellsoft.pt.di.processor;

import com.google.common.base.Stopwatch;
import com.google.common.base.Throwables;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.di.constant.DiConstant;
import com.wellsoft.pt.di.service.DiDataProcessorLogService;
import com.wellsoft.pt.security.util.IgnoreLoginUtils;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.DataHandler;
import java.io.OutputStream;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/7/9
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/7/9    chenq		2019/7/9		Create
 * </pre>
 */
public abstract class AbstractDIProcessor<BODY> implements
        DataIntegrateProcessor {
    protected ThreadLocal<Exchange> EXCHANGE = new ThreadLocal<Exchange>();
    Logger logger = LoggerFactory.getLogger(AbstractDIProcessor.class);
    Class<BODY> bodyClass;

    public AbstractDIProcessor() {
        super();
        try {
            Type type = this.getClass().getGenericSuperclass();
            if (type instanceof ParameterizedType) {
                Type[] types = ((ParameterizedType) type).getActualTypeArguments();
                bodyClass = (Class<BODY>) types[0];

                if (ParameterizedType.class.isAssignableFrom(types[0].getClass())) {
                    bodyClass = (Class<BODY>) ((ParameterizedType) types[0]).getRawType();
                } else {
                    bodyClass = (Class<BODY>) types[0];
                }
            } else if (type instanceof Class && Modifier.isAbstract(
                    ((Class) type).getModifiers())) {
                //有父类的情况下查看父类
                Type parentType = ((Class) type).getGenericSuperclass();
                if (parentType instanceof ParameterizedType) {
                    Type[] types = ((ParameterizedType) parentType).getActualTypeArguments();
                    bodyClass = (Class<BODY>) types[0];
                    if (ParameterizedType.class.isAssignableFrom(types[0].getClass())) {
                        bodyClass = (Class<BODY>) ((ParameterizedType) types[0]).getRawType();
                    } else {
                        bodyClass = (Class<BODY>) types[0];
                    }
                }
            }

            this.afterInstance();

        } catch (Exception e) {
            logger.error("实例化数据交换处理器异常：", e);
        }


    }

    /**
     * 处理过程
     *
     * @param body
     */
    abstract void action(BODY body) throws Exception;

    @Override
    public void process(Exchange exchange) throws Exception {
        logger.info("数据交换-处理器[{}]， 开始执行...", name());
        Stopwatch timer = Stopwatch.createStarted();
        String logUuid = null;
//        Exchange.REDELIVERED
//        Exchange.REDELIVERY_COUNTER
        BODY body = null;
        String processorUuid = "0";
        String diUuid = exchange.getProperty(DiConstant.DI_UUID_PROPERTY_NAME, String.class);
        DiDataProcessorLogService logService = ApplicationContextHolder.getBean(
                DiDataProcessorLogService.class);

        try {
            IgnoreLoginUtils.loginSuperadmin();
            EXCHANGE.set(exchange);
            Message message = exchange.getIn();
            body = message.getBody() != null && bodyClass != null ? message.getBody(
                    bodyClass) : null;
            if (!DataIntegrationBeginProcessor.class.isAssignableFrom(
                    this.getClass())) {//非“起始处理器”的情况下获取详情

                processorUuid = exchange.getProperty(
                        DiConstant.DI_PROCESSOR_UUID_PROPERTY_NAME, String.class);
            }

            String contentType = exchange.getIn().getHeader(Exchange.FILE_CONTENT_TYPE,
                    String.class);

            //保存处理器过程日志
            try {
                if (exchange.getProperty(DiConstant.DI_RETRY_PROPERTY_NAME,
                        String.class) == null) {//非重试的情况，记录日志
                    logUuid = logService.saveInMessage(body, diUuid, processorUuid, name(),
                            contentType,
                            exchange.getExchangeId());
                }
            } catch (Exception e) {
                logger.warn("记录处理器过程日志失败：", e);
            }


            action(body);

        } catch (Exception e) {
            logger.error("数据交换-处理器[{}]，异常：{}", name(), Throwables.getStackTraceAsString(e));
            //重试
            exchange.setException(e);
        } finally {
            logger.info("数据交换-处理器[{}]执行结束，耗时：{}", name(), timer.stop());
            if (StringUtils.isNotBlank(logUuid)) {
                Object outBody = EXCHANGE.get().getIn().getBody();
                logService.saveOutMessage(outBody,
                        outBody != null ? outBody.getClass().getCanonicalName() : null,
                        (int) timer.elapsed(TimeUnit.MILLISECONDS), logUuid);
            }
            EXCHANGE.remove();
            IgnoreLoginUtils.logout();
        }

    }

    /**
     * 业务错误，停止传播
     *
     * @param failMsg
     */
    protected void faultStop(String failMsg) {
        EXCHANGE.get().getOut().setFault(true);
        EXCHANGE.get().getOut().setBody(failMsg);
    }


    /**
     * 转发消息
     *
     * @param async
     */
    protected void forward(Boolean async, Endpoint endpoint) {
        ProducerTemplate producerTemplate = EXCHANGE.get().getContext().createProducerTemplate();
        if (async == true) {
            producerTemplate.asyncSend(
                    endpoint, EXCHANGE.get().copy(true));
        } else {
            producerTemplate.send(endpoint, EXCHANGE.get().copy(true));
        }
    }

    /**
     * 输出附件流
     *
     * @param id
     * @param out
     */
    protected void outputAttachment(String id, OutputStream out) {
        Message copy = EXCHANGE.get().getIn().copy();
        try {
            IOUtils.copy(copy.getAttachment(id).getDataSource().getInputStream(), out);
        } catch (Exception e) {
            logger.error("数据交换-处理器[{}]，输出附件{},异常：{}", new Object[]{name(), id,
                    Throwables.getStackTraceAsString(e)});
        }
    }


    protected DataHandler getAttachment(String id) {
        return EXCHANGE.get().getIn().getAttachment(id);
    }


    /**
     * 添加属性
     *
     * @param key
     * @param value
     */
    protected void addProperties(String key, Object value) {
        EXCHANGE.get().getProperties().put(key, value);
    }

    /**
     * 获取属性值
     *
     * @param key
     * @param pClass
     * @param <P>
     * @return
     */
    protected <P> P getProperty(String key, Class<P> pClass) {
        return EXCHANGE.get().getProperty(key, pClass);
    }

    /**
     * 添加属性
     *
     * @param properties
     */
    protected void addProperties(Map<String, Object> properties) {
        EXCHANGE.get().getProperties().putAll(properties);
    }

    /**
     * 添加附件数据流操作
     *
     * @param id
     * @param dataHandler
     */
    protected void addAttachment(String id, DataHandler dataHandler) {
        EXCHANGE.get().getIn().addAttachment(id, dataHandler);
    }

    /**
     * 添加头部信息
     *
     * @param key
     * @param value
     */
    protected void addHeader(String key, Object value) {
        EXCHANGE.get().getIn().setHeader(key, value);
    }

    /**
     * 添加头部信息
     *
     * @param headers
     */
    protected void addHeaders(Map<String, Object> headers) {
        EXCHANGE.get().getIn().setHeaders(headers);
    }

    /**
     * 获取本次数据交换ID
     *
     * @return
     */
    protected String getExchangeId() {
        return EXCHANGE.get().getExchangeId();
    }

    public boolean isExpose() {
        return true;
    }

    @Override
    public String toString() {
        return this.name();
    }


    protected void afterInstance() {
    }

}
