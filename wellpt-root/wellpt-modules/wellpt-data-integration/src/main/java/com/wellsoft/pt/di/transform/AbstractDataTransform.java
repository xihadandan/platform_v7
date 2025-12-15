package com.wellsoft.pt.di.transform;

import com.google.common.base.Stopwatch;
import com.google.common.base.Throwables;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.di.constant.DiConstant;
import com.wellsoft.pt.di.service.DiDataProcessorLogService;
import com.wellsoft.pt.security.util.IgnoreLoginUtils;
import org.apache.camel.Exchange;
import org.apache.commons.lang.StringUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;

/**
 * Description:数据交换转换器抽象类
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
public abstract class AbstractDataTransform<IN, OUT> implements
        DataTransform {


    final ThreadLocal<Exchange> EXCHANGE = new ThreadLocal<>();
    public Class<IN> inClass;
    public Class<OUT> outClass;


    public AbstractDataTransform() {
        super();
        try {
            Type genType = this.getClass().getGenericSuperclass();
            if (genType instanceof ParameterizedType) {
                Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
                if (ParameterizedType.class.isAssignableFrom(params[0].getClass())) {
                    inClass = (Class<IN>) ((ParameterizedType) params[0]).getRawType();
                } else {
                    inClass = (Class<IN>) params[0];
                }
                if (ParameterizedType.class.isAssignableFrom(params[1].getClass())) {
                    outClass = (Class<OUT>) ((ParameterizedType) params[1]).getRawType();
                } else {
                    outClass = (Class<OUT>) params[1];
                }
            }

        } catch (Exception e) {
            logger.error("数据转换实例化异常：", e);
            throw new RuntimeException(e);
        }
    }

    public String getExchangeId() {
        return EXCHANGE.get().getExchangeId();
    }


    public <P> P getProperty(String key, Class<P> clazz) {
        return EXCHANGE.get().getProperty(key, clazz);
    }

    public <H> H getHeader(String key, Class<H> clazz) {
        return EXCHANGE.get().getIn().getHeader(key, clazz);
    }

    public abstract OUT transform(IN in) throws Exception;

    @Override
    public <OUT> OUT evaluate(Exchange exchange, Class<OUT> type) {
        logger.info("执行数据转换器[{}]", name());
        EXCHANGE.set(exchange);
        Stopwatch timer = Stopwatch.createStarted();
        String logUuid = null;
        IN inBody = null;
        OUT outBody = null;
        DiDataProcessorLogService logService = ApplicationContextHolder.getBean(
                DiDataProcessorLogService.class);
        try {
            IgnoreLoginUtils.loginSuperadmin();
            inBody = exchange.getIn() != null ? exchange.getIn().getBody(inClass) : null;
            String inMessageType = exchange.getIn().getHeader(Exchange.FILE_CONTENT_TYPE,
                    String.class);

            if (exchange.getProperty(DiConstant.DI_RETRY_PROPERTY_NAME,
                    String.class) == null) {//非重试的情况，记录日志
                logUuid = logService.saveInMessage(inBody,
                        exchange.getProperty(DiConstant.DI_UUID_PROPERTY_NAME, String.class),
                        exchange.getProperty(DiConstant.DI_PROCESSOR_UUID_PROPERTY_NAME,
                                String.class),
                        name(),
                        StringUtils.isNotBlank(
                                inMessageType) ? inMessageType : inClass.getCanonicalName(),
                        exchange.getExchangeId());
            }

            outBody = (OUT) transform(inBody);
        } catch (Exception e) {
            //数据转换错误，不继续传播、或者重试
            exchange.getOut().setFault(true);
            exchange.getOut().setBody("数据转换异常：" + Throwables.getStackTraceAsString(e));
        } finally {
            logger.info("执行数据转换器[{}]结束，耗时:{}", name(), timer.stop());
            if (StringUtils.isNotBlank(logUuid)) {
                logService.saveOutMessage(outBody, outClass.getCanonicalName(),
                        (int) timer.elapsed(TimeUnit.MILLISECONDS), logUuid);
            }
            EXCHANGE.remove();
            IgnoreLoginUtils.logout();
        }
        return outBody;

    }


    public boolean isExpose() {
        return true;
    }


}
