package com.wellsoft.pt.jpa.criteria;

import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.jpa.dao.UniversalDao;
import com.wellsoft.pt.jpa.template.TemplateEngineFactory;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.type.Type;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

public class EntityCriteria extends AbstractUniversalCriteria {
    private final Class<?> clz;

    public EntityCriteria(UniversalDao dao, Class<?> clz) {
        super(dao);
        this.clz = clz;
    }

    @Override
    public <ITEM extends Serializable> List<ITEM> list() {
        return (List<ITEM>) this.list(QueryItem.class);
    }

    @Override
    public <ITEM extends Serializable> List<ITEM> list(Class<ITEM> itemClass) {
        String querySql = getQuerySql();
        try {
            querySql = TemplateEngineFactory.getDefaultTemplateEngine().process(querySql, queryParams);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return dao.query(querySql, this.queryParams, itemClass, this.pagingInfo);
    }

    @Override
    public long count() {
        return dao.count(this.getCountSql(), this.queryParams);
    }

    @Override
    protected String getSelectSql() {
        if (CollectionUtils.isEmpty(this.projection)) {
            return StringUtils.EMPTY;
        }

        StringBuilder selection = new StringBuilder(" select ");
        if (distinct) {
            selection.append(" distinct ");
        }
        Iterator<String> it = this.projection.iterator();
        while (it.hasNext()) {
            String columnIndex = it.next();
            selection.append(this.getColumnName(columnIndex) + " as " + columnIndex);
            if (it.hasNext()) {
                selection.append(", ");
            }
        }
        return selection.toString();
    }

    @Override
    protected String getFromSql() {
        return " from " + getClz().getName() + " " + getAlais();
    }

    public Class<?> getClz() {
        return clz;
    }

    @Override
    protected CriteriaMetadata initCriteriaMetadata() {
        CriteriaMetadata criteriaMetadata = CriteriaMetadata.createMetadata();
        ClassMetadata classMetadata = this.dao.getSession().getSessionFactory().getClassMetadata(getClz());
        String identifierProperty = classMetadata.getIdentifierPropertyName();
        Type identifierType = classMetadata.getIdentifierType();
        if (!identifierType.isAssociationType()) {
            criteriaMetadata.add(identifierProperty, identifierProperty, identifierType.getReturnedClass());
        }
        for (String propertyName : classMetadata.getPropertyNames()) {
            Type type = classMetadata.getPropertyType(propertyName);
            if (!type.isAssociationType()) {
                criteriaMetadata.add(propertyName, propertyName, type.getReturnedClass());
            }
        }
        return criteriaMetadata;
    }

}
