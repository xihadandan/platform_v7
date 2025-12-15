/*
 * @(#)2019-07-23 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.di.facade.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.component.select2.Select2DataBean;
import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.pt.di.anotation.EndpointParameter;
import com.wellsoft.pt.di.anotation.ProcessorParameter;
import com.wellsoft.pt.di.callback.AbstractDiCallback;
import com.wellsoft.pt.di.component.AbstractEndpoint;
import com.wellsoft.pt.di.component.WithoutConsumer;
import com.wellsoft.pt.di.component.WithoutProducer;
import com.wellsoft.pt.di.configuration.DataIntegrationRoutInitializer;
import com.wellsoft.pt.di.constant.DiConstant;
import com.wellsoft.pt.di.dto.DiConfigDto;
import com.wellsoft.pt.di.entity.DiConfigEntity;
import com.wellsoft.pt.di.enums.EdpParameterType;
import com.wellsoft.pt.di.enums.EmbedEndpointTypeEnum;
import com.wellsoft.pt.di.facade.service.DiConfigFacadeService;
import com.wellsoft.pt.di.processor.AbstractDIProcessor;
import com.wellsoft.pt.di.service.DiConfigService;
import com.wellsoft.pt.di.transform.AbstractDataTransform;
import com.wellsoft.pt.task.entity.JobDetails;
import com.wellsoft.pt.task.service.JobDetailsService;
import com.wellsoft.pt.xxljob.service.JobHandlerName;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Description: 数据库表DI_CONFIG的门面服务实现类
 *
 * @author chenq
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019-07-23.1	chenq		2019-07-23		Create
 * </pre>
 * @date 2019-07-23
 */
@Service
public class DiConfigFacadeServiceImpl extends AbstractApiFacade implements DiConfigFacadeService {

    @Autowired
    DataIntegrationRoutInitializer integrationRoutInitializer;
    @Autowired
    private DiConfigService diConfigService;
    @Autowired
    private DataIntegrationRoutInitializer initializer;
    @Autowired
    private JobDetailsService jobDetailsService;

    @Override
    public String saveConfig(DiConfigDto diConfigDto) {
        diConfigService.saveConfig(diConfigDto);
        return diConfigDto.getUuid();
    }

    @Override
    public boolean startOrStopRouteByConfigUuid(String uuid) {
        try {
            DiConfigEntity configEntity = diConfigService.getOne(uuid);
            //添加路由
            if (configEntity.getIsEnable()) {
                integrationRoutInitializer.addRouteDeleteWhenExist(configEntity);
            }
            //停止路由
            if (!configEntity.getIsEnable()) {
                integrationRoutInitializer.stopRoute(configEntity);
            }
        } catch (Exception e) {
            logger.error("操作数据交换配置路由异常：", e);
            return false;
        }
        return true;
    }


    @Override
    public Select2QueryData endpointSelections(Select2QueryInfo queryInfo) {
        Select2QueryData queryData = new Select2QueryData();
        String type = queryInfo.getOtherParams("type");
        boolean isConsumer = "consumer".equalsIgnoreCase(type);

        queryData.addResultData(new Select2DataBean(EmbedEndpointTypeEnum.FILE.getType(),
                EmbedEndpointTypeEnum.FILE.getName()));
        queryData.addResultData(new Select2DataBean(EmbedEndpointTypeEnum.FTP.getType(),
                EmbedEndpointTypeEnum.FTP.getName()));
        if (isConsumer) {
            queryData.addResultData(new Select2DataBean(EmbedEndpointTypeEnum.DIRECT.getType(),
                    EmbedEndpointTypeEnum.DIRECT.getName()));
        }

        Map<String, Class<? extends AbstractEndpoint>> classMap = initializer.getCustomEndpointClassMap();
        Set<String> keys = classMap.keySet();
        for (String k : keys) {
            Class clazz = classMap.get(k);
            try {
                AbstractEndpoint endpoint = (AbstractEndpoint) clazz.newInstance();
                if (endpoint.isExpose()) {
                    if (isConsumer && WithoutConsumer.class.isAssignableFrom(
                            endpoint.getConsumerClass())) {
                        continue;
                    }
                    if (!isConsumer && WithoutProducer.class.isAssignableFrom(
                            endpoint.getProducerClass())) {
                        continue;
                    }
                    queryData.addResultData(new Select2DataBean(k, endpoint.endpointName()));
                }
            } catch (Exception e) {
            }
        }

        return queryData;
    }


    @Override
    public Select2QueryData processorSelections(Select2QueryInfo queryInfo) {
        Select2QueryData queryData = new Select2QueryData();
        String type = queryInfo.getRequest() != null ? queryInfo.getOtherParams("type") : null;
        Map classMap = Maps.newHashMap();
        if (StringUtils.isBlank(type)) {
            classMap.putAll(initializer.getCustomProcessorClassMap());
            classMap.putAll(initializer.getCustomTransformerClassMap());
        } else {
            classMap.putAll(DiConstant.PROCESSOR_TYPE_PROCESSOR.toString().equals(
                    type) ? initializer.getCustomProcessorClassMap() : initializer.getCustomTransformerClassMap());
        }
        Set<String> keys = classMap.keySet();
        for (String k : keys) {
            Class clazz = (Class) classMap.get(k);
            try {
                String name = "";
                String id = clazz.getCanonicalName();
                if (AbstractDataTransform.class.isAssignableFrom(clazz)) {
                    AbstractDataTransform transform = (AbstractDataTransform) clazz.newInstance();
                    if (!transform.isExpose()) {
                        continue;
                    }
                    name = transform.name();
                } else if (AbstractDIProcessor.class.isAssignableFrom(clazz)) {
                    AbstractDIProcessor processor = (AbstractDIProcessor) clazz.newInstance();
                    if (!processor.isExpose()) {
                        continue;
                    }
                    name = processor.name();
                }
                queryData.addResultData(new Select2DataBean(id, name));
            } catch (Exception e) {
            }
        }
        return queryData;
    }

    @Override
    public Select2QueryData diJobSelections(Select2QueryInfo queryInfo) {
        List<JobDetails> jobDetailsList = new ArrayList<>();
        JobDetails jobDetails = new JobDetails();
        jobDetails.setUuid(JobHandlerName.Timed.DiExecuteJob);
        jobDetails.setName("数据交换路由执行任务");
        Select2QueryData queryData = new Select2QueryData(jobDetailsList, "uuid", "name");
        return queryData;
    }

    @Override
    public Select2QueryData diCallbackSelections(Select2QueryInfo queryInfo) {
        Select2QueryData queryData = new Select2QueryData();
        Map classMap = initializer.getCustomCallbackClassMap();
        Set<String> keys = classMap.keySet();
        for (String k : keys) {
            Class clazz = (Class) classMap.get(k);
            try {
                String name = "";
                String id = clazz.getCanonicalName();
                if (AbstractDiCallback.class.isAssignableFrom(clazz)) {
                    AbstractDiCallback callback = (AbstractDiCallback) clazz.newInstance();
                    if (!callback.isExpose()) {
                        continue;
                    }
                    name = callback.name();
                    queryData.addResultData(new Select2DataBean(id, name));
                }

            } catch (Exception e) {
            }
        }
        return queryData;
    }

    @Override
    public DiConfigDto getDetails(String uuid) {
        /*try {
            Request request1 = new Request();
            RequestDataItem dataItem = new RequestDataItem();
            Map<String, Object> dataItemMap = Maps.newHashMap();
            dataItemMap.put("code", 1);
            dataItemMap.put("name", "测试");
            dataItemMap.put("number", new BigDecimal(11111));
            dataItemMap.put("boolean", true);
            dataItem.setItemList(Lists.newArrayList(dataItemMap));
            RequestStream requestStream = new RequestStream();
            DataHandler dataHandler = new DataHandler(new FileDataSource("D:/test/bing/bing.html"));
            requestStream.setDataHandler(dataHandler);
            requestStream.setFileName(dataHandler.getName());
            dataItem.getStreamingDatas().add(requestStream);
            request1.setBody(dataItem);
            DIUtils.execute(new RequestWraper("DIC_20190814135208", request1));
        } catch (Exception e) {

        }*/

        /*JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();

        // WSDL文档url配置()
        String wsdlUrl = "http://localhost:8080/webservices/diwebservice?wsdl";

        Object[] objects = null;
        try {
            // 获取CXF客户端
            Client client = dcf.createClient(wsdlUrl);
            // 调用Web Service方法
            objects = client.invoke("callback", "ID-CQ-59238-1565864199803-0-32", "<name>陈琼</name>");
            System.out.println(objects);
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        return diConfigService.getDetails(uuid);
    }

    @Override
    public void deleteDiConfigs(List<String> uuids) {
        diConfigService.deleteDiConfigs(uuids);
    }

    @Override
    public List<Map<String, String>> getProcessorSupportParameters(String processorClazz) {
        try {
            List<Map<String, String>> params = Lists.newArrayList();
            Class clazz = Class.forName(processorClazz);
            Field[] fields = clazz.getDeclaredFields();
            for (Field f : fields) {
                ProcessorParameter processorParameter = f.getAnnotation(ProcessorParameter.class);
                if (processorParameter == null) {
                    continue;
                }
                Map<String, String> p = Maps.newHashMap();
                p.put("name", processorParameter.name());
                p.put("domType", processorParameter.domType().name());
                p.put("id", f.getName());
                p.put("dataJSON", processorParameter.dataJSON());
                p.put("remark", processorParameter.remark());
                params.add(p);
            }
            return params;
        } catch (Exception e) {
            logger.error("获取处理类{}的支持参数异常：", processorClazz, e);
        }

        return null;
    }

    @Override
    public List<Map<String, String>> getEndpointSupportParameters(String endpointType,
                                                                  EdpParameterType type) {

        try {
            List<Map<String, String>> params = Lists.newArrayList();
            Class clazz = integrationRoutInitializer.getCustomEndpointClassMap().get(endpointType);
            Field[] fields = clazz.getDeclaredFields();
            Class superclazz = clazz.getSuperclass();
            Field[] superfields = superclazz.getDeclaredFields();
            List<Field> fieldList = Lists.newArrayList(fields);
            fieldList.addAll(Lists.newArrayList(superfields));
            for (Field f : fieldList) {
                EndpointParameter parameter = f.getAnnotation(EndpointParameter.class);
                if (parameter == null) {
                    continue;
                }
                if ((EdpParameterType.BOTH.equals(parameter.type())
                        || (type != null && type.equals(parameter.type()))) && parameter.show()) {
                    Map<String, String> p = Maps.newHashMap();
                    p.put("name", parameter.name());
                    p.put("domType", parameter.domType().name());
                    p.put("id", f.getName());
                    p.put("dataJSON", parameter.dataJSON());
                    params.add(p);
                }
            }
            return params;
        } catch (Exception e) {
            logger.error("获取端点类{}的支持参数异常：", endpointType, e);
        }

        return null;
    }
}
