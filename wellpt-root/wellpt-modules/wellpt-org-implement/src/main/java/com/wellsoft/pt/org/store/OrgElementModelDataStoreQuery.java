package com.wellsoft.pt.org.store;

import com.google.common.collect.Lists;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.basicdata.datastore.support.AbstractDataStoreQueryInterface;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.criteria.QueryContext;
import com.wellsoft.pt.org.entity.OrgElementModelEntity;
import com.wellsoft.pt.org.service.OrgElementModelService;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
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
public class OrgElementModelDataStoreQuery extends AbstractDataStoreQueryInterface {

    @Resource
    OrgElementModelService orgElementModelService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.criteria.QueryInterface#getQueryName()
     */
    @Override
    public String getQueryName() {
        return "平台管理_组织服务_组织单元模型";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.criteria.QueryInterface#initCriteriaMetadata(QueryContext)
     */
    @Override
    public CriteriaMetadata initCriteriaMetadata(QueryContext queryContext) {
        CriteriaMetadata criteriaMetadata = CriteriaMetadata.createMetadata();
        criteriaMetadata.add("uuid", "uuid", "UUID", Long.class);
        criteriaMetadata.add("id", "id", "ID", String.class);
        criteriaMetadata.add("name", "name", "名称", String.class);
        criteriaMetadata.add("type", "type", "组织单元类型", String.class);
        criteriaMetadata.add("icon", "icon", "图标类", String.class);
        criteriaMetadata.add("remark", "remark", "备注", String.class);
        criteriaMetadata.add("enable", "enable", "是否启用", Boolean.class);
        criteriaMetadata.add("createTime", "createTime", "创建时间", String.class);
        criteriaMetadata.add("modifyTime", "modifyTime", "修改时间", String.class);
        return criteriaMetadata;
    }

    /**
     * (non-Javadoc)
     *
     * @see AbstractDataStoreQueryInterface#query(QueryContext)
     */
    @Override
    public List<QueryItem> query(QueryContext queryContext) {
        if (StringUtils.isNotBlank(RequestSystemContextPathResolver.system())) {
            List<OrgElementModelEntity> elementModelEntities = orgElementModelService.listOrgElementModels(SpringSecurityUtils.getCurrentTenantId(),
                    RequestSystemContextPathResolver.system());

            Collections.sort(elementModelEntities, (o1, o2) -> {
                int i = ArrayUtils.indexOf(OrgElementModelEntity.DEFAULT_ID, o1.getId()), j = ArrayUtils.indexOf(OrgElementModelEntity.DEFAULT_ID, o2.getId());
                if (i == -1 && j == -1) {
                    return (int) (o1.getCreateTime().getTime() - o2.getCreateTime().getTime());
                }
                return i == -1 || j == -1 ? j - i : i - j;
            });
            return Lists.newArrayList(JsonUtils.json2Object(JsonUtils.object2Json(elementModelEntities), QueryItem[].class));
        }
        // 查询平台级的组织单元模型数据
        return Lists.newArrayList(JsonUtils.json2Object(JsonUtils.object2Json(orgElementModelService.listOrgElementModelsBySystemIsNull()), QueryItem[].class));
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.criteria.QueryInterface#count(QueryContext)
     */
    @Override
    public long count(QueryContext queryContext) {
        return 0L;
    }

    /**
     * @param queryContext
     * @return
     */
    private Map<String, Object> getQueryParams(QueryContext queryContext) {

        return null;
    }

}
