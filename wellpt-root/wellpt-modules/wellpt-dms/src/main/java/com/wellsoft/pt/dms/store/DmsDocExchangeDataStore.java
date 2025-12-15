package com.wellsoft.pt.dms.store;

import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.basicdata.datastore.support.AbstractDataStoreQueryInterface;
import com.wellsoft.pt.dms.service.DmsDocExchangeRecordService;
import com.wellsoft.pt.jpa.criteria.Criteria;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.criteria.QueryContext;
import com.wellsoft.pt.jpa.criterion.Order;
import com.wellsoft.pt.jpa.dao.NativeDao;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Auther: yt
 * @Date: 2021/7/22 14:01
 * @Description:
 */
@Component
public class DmsDocExchangeDataStore extends AbstractDataStoreQueryInterface {


    @Autowired
    private DmsDocExchangeRecordService dmsDocExchangeRecordService;

    @Override
    public String getQueryName() {
        return "平台_公文交换_收件箱";
    }


    @Override
    public CriteriaMetadata initCriteriaMetadata(QueryContext context) {
        CriteriaMetadata metadata = CriteriaMetadata.createMetadata();
        if (StringUtils.isNotBlank(context.getInterfaceParam())) {
            Criteria criteria = ApplicationContextHolder.getBean(
                    NativeDao.class).createTableCriteria(StringUtils.trim(context.getInterfaceParam()).toLowerCase());
            metadata = criteria.getCriteriaMetadata();
        }

        metadata.add("rUuid", "r_uuid", "公文交换UUID", String.class);
        metadata.add("rCreateTime", "r_create_time", "创建时间", Date.class);
        metadata.add("rCreator", "r_creator", "创建人", String.class);
        metadata.add("rModifier", "r_modifier", "修改人", String.class);
        metadata.add("rModifyTime", "r_modify_time", "修改时间", Date.class);

        metadata.add("rIsMailNotify", "r_is_mail_notify", "是否邮件通知", Integer.class);
        metadata.add("rIsSmsNotify", "r_is_sms_notify", "是否短信通知", Integer.class);
        metadata.add("rDataUuid", "r_data_uuid", "表单保存数据的UUID", String.class);
        metadata.add("rFileUuids", "r_file_uuids", "文档交换类型为文件时候，存储的是文件上传后的fileId", String.class);
        metadata.add("rFileNames", "r_file_names", "文档交换类型为文件时候，存储的是文件上传后的fileName", String.class);
        metadata.add("rUserId", "r_user_id", "用户Id", String.class);
        metadata.add("rRecordStatus", "r_record_status", "档交换记录状态位", Integer.class);
        metadata.add("rExchangeType", "r_exchange_type", "文档交换类型", Integer.class);
        metadata.add("rToUserNames", "r_to_user_names", "接收用户名称", String.class);
        metadata.add("rFromUserId", "r_from_userId", "来源用户Id", String.class);
        metadata.add("rSendTime", "r_send_time", "发件时间", Date.class);
        metadata.add("rDocUrgeLevel", "r_doc_urge_level", "文档缓急程度", Integer.class);
        metadata.add("rFlowUuid", "r_flow_uuid", "流程定义UUID", String.class);
        metadata.add("rToUserIds", "r_to_user_ids", "接收用户ID", String.class);
        metadata.add("rDocTitle", "r_doc_title", "文档标题", String.class);
        metadata.add("rDyformUuid", "r_dyform_uuid", "表单定义UUID", String.class);
        metadata.add("rFromRecordDetailUuid", "r_from_record_detail_uuid", "来源文档交换明细UUID", String.class);
        metadata.add("rSignTimeLimit", "r_sign_time_limit", "签收时限", Date.class);
        metadata.add("rIsNeedSign", "r_is_need_sign", "是否需要签收", Integer.class);
        metadata.add("rIsNeedFeedSback", "r_is_need_feedback", "是否需要反馈", Integer.class);
        metadata.add("rOvertimeLevel", "r_overtime_level", "紧要性，根据反馈/签收时限与工作日的差距判断，0 无 1 一般 2 重要 3 紧急", Integer.class);
        metadata.add("rDocEncryptionLevel", "r_doc_encryption_level", "文档密级", Integer.class);
        metadata.add("rIsImNotify", "r_is_im_notify", "是否消息通知", Integer.class);
        metadata.add("rFeedbackTimeLimit", "r_feedback_time_limit", "反馈时限", Date.class);
        metadata.add("rFromUnitId", "r_from_unit_id", "发件单位ID", String.class);
        metadata.add("rConfigUuid", "r_config_uuid", "文档交换-配置uuid", String.class);
        return metadata;
    }

    @Override
    public List<QueryItem> query(QueryContext context) {
        Map<String, Object> params = context.getQueryParams();
        params.put("tableName", context.getInterfaceParam());
        params.put("likeCurrentUserId", "%" + SpringSecurityUtils.getCurrentUserId() + "%");
        params.putAll(context.getQueryParams());
        List<Order> orders = context.getOrders();
        if (CollectionUtils.isNotEmpty(orders)) {
            for (int i = 0; i < orders.size(); i++) {
                Order order = orders.get(i);
                if (!order.getColumnIndex().startsWith("r")) {
                    if (order.isAscending()) {
                        orders.set(i, Order.asc("o." + order.getColumnIndex()));
                    } else {
                        orders.set(i, Order.desc("o." + order.getColumnIndex()));
                    }
                }
            }
        }
        params.put("orderString", context.getOrderString());
        String whereSql = context.getWhereSqlString();
        whereSql = whereSql.replaceAll("r_", "r.");
        for (String columnIndex : context.getCriteriaMetadata().getColumnIndexs()) {
            if (!columnIndex.startsWith("r")) {
                whereSql = whereSql.replace(columnIndex, "o." + columnIndex);
                params.put("o." + columnIndex, params.get(columnIndex));
            }
        }
        params.put("whereSqlString", whereSql);
        context.getPagingInfo().setAutoCount(false);
        context.getPagingInfo().setTotalCount(this.count(context));
        List<QueryItem> queryItemList = dmsDocExchangeRecordService.listQueryItemByNameSQLQuery("queryDmsDocExchangeDataStore", params, context.getPagingInfo());
        return queryItemList;
    }


    @Override
    public long count(QueryContext context) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("tableName", context.getInterfaceParam());
        params.put("likeCurrentUserId", "%" + SpringSecurityUtils.getCurrentUserId() + "%");
        params.putAll(context.getQueryParams());
        List<QueryItem> queryItemList = dmsDocExchangeRecordService.listQueryItemByNameSQLQuery("countDmsDocExchangeDataStore", params, null);
        return queryItemList.get(0).getLong("total");
    }
}
