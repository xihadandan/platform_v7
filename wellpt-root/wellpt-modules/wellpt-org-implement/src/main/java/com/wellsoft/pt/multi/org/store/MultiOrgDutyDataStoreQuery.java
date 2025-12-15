package com.wellsoft.pt.multi.org.store;

import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.datastore.support.AbstractDataStoreQueryInterface;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.criteria.QueryContext;
import com.wellsoft.pt.jpa.dao.NativeDao;
import com.wellsoft.pt.multi.org.entity.MultiOrgJobRank;
import com.wellsoft.pt.multi.org.service.MultiOrgDutyService;
import com.wellsoft.pt.multi.org.service.MultiOrgJobRankService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Description:
 * 职务数据仓库接口
 * <pre>
 * 修改记录:
 * 修改后版本    修改人     修改日期    修改内容
 * 1.0         baozh    2021/11/5   Create
 * </pre>
 */
@Component
public class MultiOrgDutyDataStoreQuery extends AbstractDataStoreQueryInterface {

    @Autowired
    MultiOrgDutyService multiOrgDutyService;

    @Autowired
    MultiOrgJobRankService multiOrgJobRankService;

    @Override
    public List<QueryItem> query(QueryContext context) {
        NativeDao nativeDao = context.getNativeDao();
        Map<String, Object> queryParams = context.getQueryParams();

        queryParams.put("systemUnitId", SpringSecurityUtils.getCurrentUserUnitId());
        queryParams.put("keyword", context.getKeyword());
        queryParams.put("orderBy", context.getOrderString());
        List<QueryItem> list = nativeDao.namedQuery("queryMultiOrgDuty", queryParams, QueryItem.class, context.getPagingInfo());
        Set<String> jobRankIdList = list.stream()
                .filter(queryItem -> queryItem.getString("jobRank") != null)
                .flatMap(queryItem -> Arrays.stream(queryItem.getString("jobRank").split(",")))
                .collect(Collectors.toSet());
        if (jobRankIdList.isEmpty()) {
            return list;
        }
        List<MultiOrgJobRank> multiOrgJobRanks = multiOrgJobRankService.getDao().listByFieldInValues("id", Arrays.asList(jobRankIdList.toArray()));
        Map<String, String> orgJobRankMap = multiOrgJobRanks.stream().collect(Collectors.toMap(MultiOrgJobRank::getId, MultiOrgJobRank::getJobRank));
        list.stream().filter(queryItem -> queryItem.getString("jobRank") != null)
                .forEach(queryItem -> {
                    String[] jobRankIds = queryItem.getString("jobRank").split(",");
                    List<String> jobRankNameList = new ArrayList<>();
                    for (String jobRankId : jobRankIds) {
                        if (orgJobRankMap.containsKey(jobRankId)) {
                            jobRankNameList.add(orgJobRankMap.get(jobRankId));
                        }
                    }
                    queryItem.put("jobRankName", StringUtils.join(jobRankNameList, ","));
                });
        return list;
    }


    @Override
    public String getQueryName() {
        return "组织职务体系-职务列表数据仓库";
    }

    @Override
    public CriteriaMetadata initCriteriaMetadata(QueryContext context) {
        CriteriaMetadata metadata = CriteriaMetadata.createMetadata();
        metadata.add("uuid", "uuid", "UUID", String.class);
        metadata.add("name", "name", "名称", String.class);
        metadata.add("id", "id", "ID", String.class);
        metadata.add("code", "code", "编码", String.class);
        metadata.add("dutySeqUuid", "dutySeqUuid", "职务序列UUID", String.class);
        metadata.add("dutySeqName", "dutySeqName", "职务序列", String.class);
        metadata.add("jobRank", "jobRank", "职级ID", String.class);
        metadata.add("jobRankName", "jobRankName", "职级", String.class);
        metadata.add("remark", "remark", "描述", String.class);
        return metadata;
    }

    @Override
    public long count(QueryContext context) {
        return context.getPagingInfo().getTotalCount();
    }
}
