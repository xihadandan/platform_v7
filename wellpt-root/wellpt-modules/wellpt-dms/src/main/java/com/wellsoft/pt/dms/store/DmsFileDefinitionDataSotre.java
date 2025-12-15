package com.wellsoft.pt.dms.store;

import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.datastore.support.AbstractDataStoreQueryInterface;
import com.wellsoft.pt.dms.service.DmsFolderService;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.criteria.QueryContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/5/30
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/5/30    chenq		2019/5/30		Create
 * </pre>
 */
@Component
public class DmsFileDefinitionDataSotre extends AbstractDataStoreQueryInterface {

    @Autowired
    private DmsFolderService dmsFolderService;

    @Override
    public List<QueryItem> query(QueryContext context) {
        List<QueryItem> queryItemList = dmsFolderService.listByParams(
                getQueryParams(context), context.getPagingInfo());
        context.getPagingInfo().setTotalCount(queryItemList.size());
        return queryItemList;
    }

    @Override
    public String getQueryName() {
        return "文件夹定义";
    }

    @Override
    public CriteriaMetadata initCriteriaMetadata(QueryContext context) {
        CriteriaMetadata metadata = CriteriaMetadata.createMetadata();
        metadata.add("uuid", "uuid", "UUID", String.class);
        metadata.add("name", "name", "名称", String.class);
        metadata.add("code", "code", "编号", String.class);
        metadata.add("contentType", "content_type", "夹类型", String.class);
        metadata.add("uuidPath", "uuid_path", "夹UUID路径", String.class);
        metadata.add("absolutePath", "absolute_path", "夹名称路径", String.class);
        metadata.add("parentUuid", "parent_uuid", "父级UUID", String.class);
        metadata.add("status", "status", "状态", String.class);
        metadata.add("createTime", "create_time", "创建时间", String.class);
        metadata.add("creator", "creator", "创建人", String.class);
        metadata.add("modifier", "modifier", "修改人", String.class);
        metadata.add("modifyTime", "modify_time", "修改时间", Date.class);
        return metadata;
    }

    @Override
    public long count(QueryContext context) {
        return context.getPagingInfo().getTotalCount();
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
        queryParams.put("systemUnitId", queryContext.getQueryParams().get("systemUnitId"));
        return queryParams;
    }
}
