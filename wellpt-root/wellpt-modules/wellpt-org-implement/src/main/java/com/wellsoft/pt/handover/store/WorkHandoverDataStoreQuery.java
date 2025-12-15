package com.wellsoft.pt.handover.store;

import cn.hutool.core.date.DateUtil;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.HandoverUtils;
import com.wellsoft.pt.basicdata.datastore.support.AbstractDataStoreQueryInterface;
import com.wellsoft.pt.handover.entity.WhWorkSettingsEntity;
import com.wellsoft.pt.handover.service.WhWorkSettingsService;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.criteria.QueryContext;
import com.wellsoft.pt.jpa.dao.NativeDao;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	        修改人		修改日期			修改内容
 * 2022/3/22.1	    zenghw		2022/3/22		    Create
 * </pre>
 * @date 2022/3/22
 */
@Component
public class WorkHandoverDataStoreQuery extends AbstractDataStoreQueryInterface {
    @Autowired
    private NativeDao nativeDao;
    private String nameQuery = "queryWorkHandoverDatas";
    @Autowired
    private WhWorkSettingsService whWorkSettingsService;

    @Override
    public CriteriaMetadata initCriteriaMetadata(QueryContext queryContext) {
        CriteriaMetadata metadata = CriteriaMetadata.createMetadata();
        metadata.add("uuid", "uuid", "UUID", String.class);
        metadata.add("handoverPersonId", "HANDOVER_PERSON_ID", "交接人ID", String.class);
        metadata.add("handoverPersonName", "HANDOVER_PERSON_NAME", "交接人名称", String.class);
        metadata.add("handoverWorkTime", "HANDOVER_WORK_TIME", "任务实际开始执行的时间", Date.class);
        metadata.add("noticeHandoverPersonFlag", "NOTICE_HANDOVER_PERSON_FLAG", "是否通知接收人", String.class);
        metadata.add("handoverContentsName", "HANDOVER_CONTENTS_NAME", "交接内容：流程定义内容显示值", String.class);
        metadata.add("handoverContentsId", "HANDOVER_CONTENTS_ID", "交接内容：流程定义内容", String.class);
        metadata.add("receiverId", "RECEIVER_ID", "接收人ID", String.class);
        metadata.add("receiverName", "RECEIVER_NAME", "接收人名称", String.class);
        metadata.add("workHandoverStatus", "WORK_HANDOVER_STATUS", "工作交接状态", String.class);
        metadata.add("handoverWorkTimeSetting", "HANDOVER_WORK_TIME_SETTING", "交接执行时间", String.class);
        metadata.add("handoverWorkType", "HANDOVER_WORK_TYPE", "工作类型", String.class);
        metadata.add("handoverWorkTypeName", "HANDOVER_WORK_TYPE_NAME", "工作类型显示值", String.class);
        metadata.add("createTime", "create_time", "创建时间/操作时间", Date.class);
        return metadata;
    }

    @Override
    public List<QueryItem> query(QueryContext queryContext) {
        Map<String, Object> params = convertQueryParams(queryContext);
        List<QueryItem> items = queryContext.getNativeDao().namedQuery(nameQuery, params, QueryItem.class,
                queryContext.getPagingInfo());
        int year = DateUtil.year(new Date());
        for (QueryItem item : items) {
            // 操作时间
            String createTimeFormat = DateUtil.format(item.getDate("createTime"), "yyyy-MM-dd HH:mm");
            if (StringUtils.isNotBlank(createTimeFormat)) {
                createTimeFormat = createTimeFormat.replace(String.valueOf(year) + "-", "");
                item.put("createTime", createTimeFormat);
            }

            // 执行时间
            String handoverWorkTimeFormat = DateUtil.format(item.getDate("handoverWorkTime"), "yyyy-MM-dd HH:mm");
            if (StringUtils.isNotBlank(handoverWorkTimeFormat)) {
                handoverWorkTimeFormat = handoverWorkTimeFormat.replace(String.valueOf(year) + "-", "");
                item.put("handoverWorkTime", handoverWorkTimeFormat);
            } else {
                // 未执行
                String dateStr = "";
                WhWorkSettingsEntity whWorkSettingsEntity = whWorkSettingsService
                        .getDetailByCurrentUnitId(SpringSecurityUtils.getCurrentUserUnitId());
                if (whWorkSettingsEntity == null) {
                    dateStr = DateUtil.formatDateTime(HandoverUtils.getWorkDateTime("01:00"));
                } else {
                    dateStr = DateUtil
                            .formatDateTime(HandoverUtils.getWorkDateTime(whWorkSettingsEntity.getWorkTime()));
                }
                dateStr = dateStr.substring(0, dateStr.length() - 3);
                dateStr = dateStr.replace(String.valueOf(year) + "-", "");
                item.put("handoverWorkTime", dateStr);
            }
        }
        return items;
    }

    @Override
    public String getQueryName() {
        return "组织管理_工作交接数据仓库";
    }

    @Override
    public long count(QueryContext queryContext) {
        Map<String, Object> params = convertQueryParams(queryContext);
        return this.nativeDao.countByNamedQuery(this.nameQuery, params);
    }

    /**
     * 将queryContext转化查询查询参数
     *
     * @param queryContext
     * @return java.util.HashMap<java.lang.String, java.lang.Object>
     **/
    private Map<String, Object> convertQueryParams(QueryContext queryContext) {
        Map<String, Object> params = queryContext.getQueryParams();

        String wheresql = queryContext.getWhereSqlString();
        String orderStr = queryContext.getOrderString();
        params.put("orderStr", orderStr);
        params.put("whereSql", wheresql);
        params.put("systemUnitId", SpringSecurityUtils.getCurrentUserUnitId());
        return params;
    }

}
