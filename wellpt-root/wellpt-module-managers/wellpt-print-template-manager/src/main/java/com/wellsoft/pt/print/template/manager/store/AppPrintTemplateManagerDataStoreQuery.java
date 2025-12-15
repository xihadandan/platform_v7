package com.wellsoft.pt.print.template.manager.store;

import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.datastore.support.AbstractDataStoreQueryInterface;
import com.wellsoft.pt.basicdata.printtemplate.dao.PrintTemplateCategoryDao;
import com.wellsoft.pt.basicdata.printtemplate.entity.PrintTemplateCategory;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.criteria.QueryContext;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/6/3
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/6/3    chenq		2019/6/3		Create
 * </pre>
 */
@Component
public class AppPrintTemplateManagerDataStoreQuery extends AbstractDataStoreQueryInterface {

    @Resource
    private PrintTemplateCategoryDao printTemplateCategoryDao;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.criteria.QueryInterface#getQueryName()
     */
    @Override
    public String getQueryName() {
        return "平台管理_产品集成_打印模板";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.criteria.QueryInterface#initCriteriaMetadata(QueryContext)
     */
    @Override
    public CriteriaMetadata initCriteriaMetadata(QueryContext queryContext) {
        CriteriaMetadata criteriaMetadata = CriteriaMetadata.createMetadata();
        criteriaMetadata.add("uuid", "t1.uuid", "UUID", String.class);
        criteriaMetadata.add("createTime", "t1.create_time", "创建时间", String.class);
        criteriaMetadata.add("creator", "t1.creator", "创建人", String.class);
        criteriaMetadata.add("modifier", "t1.modifier", "修改人", String.class);
        criteriaMetadata.add("modifyTime", "t1.modify_time", "修改时间", Date.class);
        criteriaMetadata.add("recVer", "t1.rec_ver", "recVer", Integer.class);
        criteriaMetadata.add("name", "t1.name", "名称", String.class);
        criteriaMetadata.add("id", "t1.id", "ID", String.class);
        criteriaMetadata.add("code", "t1.code", "编号", String.class);
        criteriaMetadata.add("category", "t1.category", "打印模版分类编码", String.class);
        criteriaMetadata.add("categoryName", "category_name", "打印模版分类名称", String.class);
        criteriaMetadata.add("isRef", "is_ref", "是否引用", Boolean.class);
        criteriaMetadata.add("moduleId", "t1.module_id", "模块ID", String.class);
        criteriaMetadata.add("systemUnitId", "t1.system_unit_id", "归属系统单位ID", String.class);
        criteriaMetadata.add("version", "t1.version", "版本号", String.class);
        criteriaMetadata.add("maxVersionPtptUuid", "t7.max_version_ptpt_uuid", "最新版本的打印模板UUID", String.class);
        return criteriaMetadata;
    }

    /**
     * (non-Javadoc)
     *
     * @see AbstractDataStoreQueryInterface#query(QueryContext)
     */
    @Override
    public List<QueryItem> query(QueryContext queryContext) {
        List<QueryItem> queryItems = queryContext.getNativeDao().namedQuery("appPrintTemplateManagerQuery",
                getQueryParams(queryContext), QueryItem.class, queryContext.getPagingInfo());
        return queryItems;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.criteria.QueryInterface#count(QueryContext)
     */
    @Override
    public long count(QueryContext queryContext) {
        Long total = queryContext.getPagingInfo().getTotalCount();
        return total != -1 ? total : queryContext.getNativeDao().countByNamedQuery(
                "appPrintTemplateManagerQuery", getQueryParams(queryContext));
    }

    /**
     * @param queryContext
     * @return
     */
    private Map<String, Object> getQueryParams(QueryContext queryContext) {
        Map<String, Object> queryParams = queryContext.getQueryParams();
        queryParams.put("keyword", queryContext.getKeyword());
        queryParams.put("whereSql", queryContext.getWhereSqlString());
        queryParams.put("orderBy", queryContext.getOrderString());

        String categoryUuid = (String) queryContext.getQueryParams().get("categoryUuid");
        List<String> allChildCategoryUuidList = this.getAllChildCategoryUuid(categoryUuid);
//        queryParams.put("categoryUuid", categoryUuid);
        if (CollectionUtils.isNotEmpty(allChildCategoryUuidList)) {
            queryParams.put("categoryUuidList", allChildCategoryUuidList);
        }
        queryParams.put("moduleId", queryContext.getQueryParams().get("moduleId"));
        return queryParams;
    }

    /**
     * 考虑mysql、金仓、oracle兼容性，临时使用循环查询方式，解决获取树所有子节点问题
     *
     * @param categoryUuid categoryUuid
     * @return
     */
    private List<String> getAllChildCategoryUuid(String categoryUuid) {
        List<String> list = new ArrayList<>();
        if (StringUtils.isNotBlank(categoryUuid)) {
            PrintTemplateCategory printTemplateCategory = printTemplateCategoryDao.getOne(categoryUuid);
            List<PrintTemplateCategory> printTemplateCategoryList = new ArrayList<>();
            printTemplateCategoryList.add(printTemplateCategory);

            while (CollectionUtils.isNotEmpty(printTemplateCategoryList)) {
                List<PrintTemplateCategory> reCategoryList = new ArrayList<>();
                for (PrintTemplateCategory templateCategory : printTemplateCategoryList) {
                    list.add(templateCategory.getUuid());

                    PrintTemplateCategory queryPrintTemplateCategory = new PrintTemplateCategory();
                    queryPrintTemplateCategory.setParentUuid(templateCategory.getUuid());
                    reCategoryList.addAll(printTemplateCategoryDao.listByEntity(queryPrintTemplateCategory));
                }

                printTemplateCategoryList = reCategoryList;
            }
        }
        return list;

    }

}
