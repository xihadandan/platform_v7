package com.wellsoft.pt.dms.store;

import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.basicdata.datastore.support.AbstractDataStoreQueryInterface;
import com.wellsoft.pt.dms.service.DmsDataVersionService;
import com.wellsoft.pt.dyform.facade.dto.DyFormFormDefinition;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.jpa.criteria.Criteria;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.criteria.QueryContext;
import com.wellsoft.pt.jpa.dao.NativeDao;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
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
public class LatestDmsFormVersionDataSotre extends AbstractDataStoreQueryInterface {

    @Resource
    DmsDataVersionService dmsDataVersionService;

    @Override
    public List<QueryItem> query(QueryContext context) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("whereSql", context.getWhereSqlString());
        params.put("formId", context.getInterfaceParam());
        params.putAll(context.getQueryParams());
        return dmsDataVersionService.listLatestVersionFormDataByParams(params,
                context.getPagingInfo());
    }

    @Override
    public String getQueryName() {
        return "查询最新版本的表单业务数据";
    }

    @Override
    public CriteriaMetadata initCriteriaMetadata(QueryContext context) {
        //获取表单的元数据
        DyFormFormDefinition definition = ApplicationContextHolder.getBean(
                DyFormFacade.class).getFormDefinitionOfMaxVersionById(
                StringUtils.trim(context.getInterfaceParam()));
        CriteriaMetadata metadata = CriteriaMetadata.createMetadata();
        if (definition != null) {
            Criteria criteria = ApplicationContextHolder.getBean(
                    NativeDao.class).createTableCriteria(
                    definition.getTableName());
            metadata = criteria.getCriteriaMetadata();
        }
        metadata.add("max_version", "max_version", "最大版本号", Double.class);
        return metadata;
    }

    @Override
    public long count(QueryContext context) {
        return context.getPagingInfo().getTotalCount();
    }

    @Override
    public String getInterfaceDesc() {
        return "请在数据接口参数填写表单ID ";
    }


}
