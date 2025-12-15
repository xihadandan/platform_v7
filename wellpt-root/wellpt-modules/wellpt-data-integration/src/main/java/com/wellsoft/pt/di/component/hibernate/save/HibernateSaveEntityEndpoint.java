package com.wellsoft.pt.di.component.hibernate.save;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.di.anotation.EndpointParameter;
import com.wellsoft.pt.di.component.AbstractEndpoint;
import com.wellsoft.pt.di.component.WithoutConsumer;
import com.wellsoft.pt.di.enums.EdpParameterType;
import com.wellsoft.pt.jpa.hibernate4.DynamicHibernateSessionFactoryRegistry;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/7/16
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/7/16    chenq		2019/7/16		Create
 * </pre>
 */
public class HibernateSaveEntityEndpoint extends
        AbstractEndpoint<HibernateSaveEntityDIComponent, HibernateSaveEntityProducer, WithoutConsumer> {

    @EndpointParameter(name = "动态sessionFactoryId", type = EdpParameterType.PRODUCER)
    private String sessionFactoryId;
    @EndpointParameter(name = "动态实体类名称", type = EdpParameterType.PRODUCER)
    private String entityName;


    @Override
    public String endpointPrefix() {
        return "hb-save";
    }

    @Override
    public String endpointName() {
        return "数据交换-Hibernate实体类保存端点";
    }

    public String getSessionFactoryId() {
        return sessionFactoryId;
    }

    public void setSessionFactoryId(String sessionFactoryId) {
        this.sessionFactoryId = sessionFactoryId;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }


    @Override
    public String toProducerUri(String diUuid, String parameterJson) {
        try {

            JSONObject jsonObject = new JSONObject();
            Map<String, Object> sessionFactoryProperties =
                    new Gson().fromJson(parameterJson,
                            Map.class);
            if (CollectionUtils.isEmpty(
                    (Collection) sessionFactoryProperties.get("mappingEntityProperties"))) {
                return null;
            }
            //构建实体映射xml
            String entityName = "DynamicEntity" + System.currentTimeMillis();
            StringBuilder xmlBuilder = new StringBuilder("<?xml version=\"1.0\"?>\n" +
                    "<!DOCTYPE hibernate-mapping PUBLIC\n" +
                    "        \"-//Hibernate/Hibernate Mapping DTD 3.0//EN\"\n" +
                    "        \"http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd\">\n" +
                    "\n" +
                    "<hibernate-mapping>");
            xmlBuilder.append(
                    "<class entity-name=\"" + entityName + "\" table=\"" + sessionFactoryProperties.get(
                            "tableName") + "\">");
            int offset = xmlBuilder.toString().length();
            if (sessionFactoryProperties.get("mappingEntityProperties") != null) {
                List<Map<String, Object>> entityProperties = (List<Map<String, Object>>) sessionFactoryProperties.get(
                        "mappingEntityProperties");
                for (Map<String, Object> ep : entityProperties) {
                    if (StringUtils.isNotBlank((String) ep.get("column"))
                            && StringUtils.isNotBlank((String) ep.get("type"))
                            && StringUtils.isNotBlank((String) ep.get("name"))) {
                        if (ep.get("primary").toString().equals("1")) {
                            xmlBuilder.insert(offset, String.format(
                                    String.format("<id name=\"%s\" column=\"%s\" type=\"%s\" >\n" +
                                                    // "            <generator class=\"native\"/>\n" +
                                                    "        </id>",
                                            ep.get("name"), ep.get("column"), ep.get("type"))));
                            continue;
                        }
                        xmlBuilder.append(String.format(
                                "<property name=\"%s\" column=\"%s\" type=\"%s\"/>",
                                ep.get("name"), ep.get("column"), ep.get("type")));
                    }
                }

            }
            xmlBuilder.append("</class></hibernate-mapping>");
            sessionFactoryProperties.put("mappingXML", xmlBuilder.toString());

            //解析hibernate属性
            if (sessionFactoryProperties.get("hibernateProperties") != null) {
                List<Map<String, Object>> hibernateProperties = (List<Map<String, Object>>) sessionFactoryProperties.get(
                        "hibernateProperties");
                Map<String, Object> outHibernateProperties = Maps.newHashMap();
                for (Map<String, Object> hp : hibernateProperties) {
                    if (StringUtils.isNotBlank((String) hp.get("key"))
                            & StringUtils.isNotBlank((String) hp.get("value"))) {
                        outHibernateProperties.put(hp.get("key").toString(), hp.get("value"));
                    }
                }
                sessionFactoryProperties.put("hibernateProperties", outHibernateProperties);
            }


            String sessionFactoryId = DynamicHibernateSessionFactoryRegistry.registry(
                    ApplicationContextHolder.defaultListableBeanFactory(),
                    sessionFactoryProperties);
            jsonObject.put("sessionFactoryId", sessionFactoryId);
            jsonObject.put("entityName", entityName);
            parameterJson = jsonObject.toString();
            return super.toProducerUri(diUuid, parameterJson);
        } catch (Exception e) {
            logger.error("构建hibernate-save的消费uri异常：", e);
        }

        return "";

    }

}
