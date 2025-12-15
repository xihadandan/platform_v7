package com.wellsoft.pt.dm.hibernate;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.cfg.Configuration;
import org.hibernate.metamodel.Metadata;
import org.hibernate.metamodel.MetadataSources;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManagerFactory;
import java.io.ByteArrayInputStream;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2023年05月19日   chenq	 Create
 * </pre>
 */
public class HibernateTest {
    public static final String XML_MAPPING = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<!DOCTYPE hibernate-mapping PUBLIC\n" +
            "        \"-//Hibernate/Hibernate Mapping DTD 3.0//EN\"\n" +
            "        \"http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd\">\n" +
            "<hibernate-mapping>\n" +
            "    <class entity-name=\"Student\" table=\"t_student\">\n" +
            "        <id name=\"id\" type=\"java.lang.Long\" length=\"64\" unsaved-value=\"null\">\n" +
            "            <generator class=\"identity\" />\n" +
            "        </id>" +
            "        <property type=\"java.lang.String\" name=\"username\" column=\"username\"/>\n" +
            "        <property name=\"password\" type=\"java.lang.String\" column=\"password\"/>\n" +
            "        <property name=\"sex\" type=\"java.lang.String\" column=\"sex\"/>\n" +
            "        <property name=\"age\" type=\"java.lang.Integer\" column=\"age\"/>\n" +
            "        <property name=\"birthday\" type=\"java.util.Date\" column=\"birthday\"/>\n" +
            "    </class>" +
            "</hibernate-mapping>";
    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Test
    public void test() {
        SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
        StandardServiceRegistry serviceRegistry = sessionFactory.getSessionFactoryOptions().getServiceRegistry();
        MetadataSources metadataSources = new MetadataSources(serviceRegistry);
        sessionFactory.getSessionFactoryOptions();
        //读取映射文件
        metadataSources.addInputStream(new ByteArrayInputStream(XML_MAPPING.getBytes()));
        Metadata metadata = metadataSources.buildMetadata();
        //创建数据库Schema,如果不存在就创建表,存在就更新字段,不会影响已有数据
        SchemaExport schemaExport = new SchemaExport(new Configuration());
        schemaExport.create(true, true);


    }
}
