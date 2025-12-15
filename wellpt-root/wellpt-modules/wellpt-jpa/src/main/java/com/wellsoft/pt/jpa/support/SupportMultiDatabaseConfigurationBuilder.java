package com.wellsoft.pt.jpa.support;

import com.wellsoft.pt.jpa.datasource.DatabaseType;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.Node;
import org.dom4j.tree.DefaultElement;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.MySQL5InnoDBDialect;
import org.hibernate.internal.util.xml.XmlDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.orm.hibernate4.LocalSessionFactoryBuilder;

import javax.sql.DataSource;
import java.util.List;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/3/7
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/3/7    chenq		2019/3/7		Create
 * </pre>
 */
public class SupportMultiDatabaseConfigurationBuilder extends
        LocalSessionFactoryBuilder {

    private final static Logger LOGGER = LoggerFactory.getLogger(
            SupportMultiDatabaseConfigurationBuilder.class);


    private ThreadLocal<Resource> currentResource = new ThreadLocal<>();


    public SupportMultiDatabaseConfigurationBuilder(DataSource dataSource) {
        super(dataSource);
    }

    public SupportMultiDatabaseConfigurationBuilder(DataSource dataSource,
                                                    ClassLoader classLoader) {
        super(dataSource, (ResourceLoader) (new PathMatchingResourcePatternResolver(classLoader)));
    }

    public SupportMultiDatabaseConfigurationBuilder(DataSource dataSource,
                                                    ResourceLoader resourceLoader) {
        super(dataSource, resourceLoader);
    }

    public static String resolveDatabaseType(Resource resource) {
        try {
            String uri = resource.getURI().toASCIIString();
            String[] parts = uri.split("\\.");
            if (parts.length <= 2) {
                throw new RuntimeException("hibernate mapping文件格式不正确");
            }
            if (DatabaseType.SUPPORTS.contains(parts[parts.length - 3])) {
                return parts[parts.length - 3] + "#";
            }
        } catch (Exception e) {
            LOGGER.error("解析命名查询xml资源的数据库类型异常：", e);
        }
        return "";
    }

    public static String resolveDatabaseType(Dialect dialect) {
        String dataBaseTypeSuffix = "";
        try {
            //数据库类型判断
            if (dialect instanceof CustomOracle10gDialect) {
                dataBaseTypeSuffix = "oracle#";
            } else if (dialect instanceof MySQL5InnoDBDialect) {
                dataBaseTypeSuffix = "mysql#";
            } else if (dialect instanceof CustomDm7DBDialect) {
                dataBaseTypeSuffix = "dm#";
            } else if (dialect instanceof CustomKingbase8Dialect) {
                dataBaseTypeSuffix = "kb#";
            }
        } catch (Exception e) {
            LOGGER.error("解析命名查询xml资源的数据库类型异常：", e);
        }
        return dataBaseTypeSuffix;
    }

    public void currentResource(Resource resource) {
        this.currentResource.set(resource);
    }

    public void add(XmlDocument metadataXml) {
        Resource resource = this.currentResource.get();
        String uri = null;
        try {
            String databaseType = resolveDatabaseType(resource);
            if (StringUtils.isBlank(databaseType)) {
                //默认的hibernate mapping文件
                super.add(metadataXml);
                return;
            }

            //解析mapping文件尾部
            Document document = metadataXml.getDocumentTree();
            //修改各个命名查询的名称
            List<Node> nodeList = document.selectNodes(
                    "hibernate-mapping/sql-query");

            for (Node node : nodeList) {
                DefaultElement element = (DefaultElement) node;
                String name = element.attribute("name").getValue();
                element.attribute("name").setValue(databaseType + name);
            }

            super.add(metadataXml);


        } catch (Exception e) {
            throw new RuntimeException("加载hibernate命名查询文件异常，文件：" + uri, e);
        }

    }


}
