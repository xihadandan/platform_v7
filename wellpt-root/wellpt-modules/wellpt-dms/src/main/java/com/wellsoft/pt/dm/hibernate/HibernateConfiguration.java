package com.wellsoft.pt.dm.hibernate;

import org.hibernate.cfg.*;
import org.hibernate.mapping.Table;

import java.io.Serializable;
import java.util.Properties;

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
public class HibernateConfiguration extends Configuration {
    final ObjectNameNormalizer normalizer = new HibernateConfiguration.ObjectNameNormalizerImpl();

    private Properties properties;

    public ObjectNameNormalizer getObjectNameNormalizer() {
        return normalizer;
    }

    public Mappings createMappings() {
        return new MappingsImpl();
    }


    class MappingsImpl extends Configuration.MappingsImpl {

        public Table addTable(
                String schema,
                String catalog,
                String name,
                String subselect,
                boolean isAbstract) {
            name = getObjectNameNormalizer().normalizeIdentifierQuoting(name);
            schema = getObjectNameNormalizer().normalizeIdentifierQuoting(schema);
            catalog = getObjectNameNormalizer().normalizeIdentifierQuoting(catalog);

            String key = subselect == null ? Table.qualify(catalog, schema, name) : subselect;
            Table table = tables.get(key);

            if (table == null) {
                table = new com.wellsoft.pt.dm.hibernate.Table();
                table.setAbstract(isAbstract);
                table.setName(name);
                table.setSchema(schema);
                table.setCatalog(catalog);
                table.setSubselect(subselect);
                tables.put(key, table);
            } else {
                if (!isAbstract) {
                    table.setAbstract(false);
                }
            }

            return table;
        }
    }

    final class ObjectNameNormalizerImpl extends ObjectNameNormalizer implements Serializable {
        public boolean isUseQuotedIdentifiersGlobally() {
            //Do not cache this value as we lazily set it in Hibernate Annotation (AnnotationConfiguration)
            //TODO use a dedicated protected useQuotedIdentifier flag in Configuration (overriden by AnnotationConfiguration)
            String setting = (String) properties.get(Environment.GLOBALLY_QUOTED_IDENTIFIERS);
            return setting != null && Boolean.valueOf(setting);
        }

        public NamingStrategy getNamingStrategy() {
            return namingStrategy;
        }
    }
}
