package com.wellsoft.pt.org.cleanup;

import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.cleanup.AbstractDataCleanupDataCleanupProvider;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.user.service.UserInfoService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2024年04月23日   chenq	 Create
 * </pre>
 */
@Service
public class OtherUserDataCleanupProvider extends AbstractDataCleanupDataCleanupProvider {

    @Autowired
    UserInfoService userInfoService;


    @Override
    public String getType() {
        return "otherUserData";
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ExpectCleanupResult cleanup(Params params) {
        String system = StringUtils.defaultIfBlank(params.optString("system"), RequestSystemContextPathResolver.system());
        long total = 0L;
        if (StringUtils.isNotBlank(system)) {
            Map<String, Object> sqlParams = Maps.newHashMap();
            sqlParams.put("system", system);
            sqlParams.put("tenant", SpringSecurityUtils.getCurrentTenantId());
            List<String> clearTypes = (List<String>) params.get("clearTypes");
            if (clearTypes.contains("userPreference")) {
                total += userInfoService.getDao().deleteBySQL("delete from cd_user_preferences c" +
                        " where exists ( select 1 from org_user u where u.user_id=c.user_id and u.system=:system and u.tenant=:tenant ) ", sqlParams);
            }
            if (clearTypes.contains("userMailbox")) {
                total += userInfoService.getDao().deleteBySQL("delete from wm_mailbox c" +
                        " where exists ( select 1 from org_user u where u.user_id=c.user_id and u.system=:system and u.tenant=:tenant ) ", sqlParams);
                userInfoService.getDao().deleteBySQL("delete from wm_mail_user_contact c" +
                        " where exists ( select 1 from org_user u where u.user_id=c.user_id and u.system=:system and u.tenant=:tenant ) ", sqlParams);
                total += userInfoService.getDao().deleteBySQL("delete from wm_mail_folder c" +
                        " where exists ( select 1 from org_user u where u.user_id=c.user_id and u.system=:system and u.tenant=:tenant ) ", sqlParams);
                userInfoService.getDao().deleteBySQL("delete from wm_mail_paper c" +
                        " where exists ( select 1 from org_user u where u.user_id=c.user_id and u.system=:system and u.tenant=:tenant ) ", sqlParams);
                try {
//                    NativeDao jamesDao = ApplicationContextHolder.getBean(NativeDao.class);
//                    jamesDao.setSessionFactory(
//                            ApplicationContextHolder.getBean("james", SessionFactory.class),
//                            null);
//                    Map<String, Object> jamesParams = Maps.newHashMap();
//                    ListUtils.handleSubList(loginNameItems, 200, list -> {
//                        jamesParams.put("userName", list);
//                        jamesDao.batchExecute("delete from james_user where user_name in (:userName)", jamesParams);
//                    });
                } catch (Exception e) {
                    //TODO:
                }


            }


            if (clearTypes.contains("userMessage")) {
                total += userInfoService.getDao().deleteBySQL("delete from msg_message_outbox c" +
                        " where exists ( select 1 from org_user u where u.user_id=c.sender and u.system=:system and u.tenant=:tenant ) ", sqlParams);
                total += userInfoService.getDao().deleteBySQL("delete from  msg_message_inbox c" +
                        " where exists ( select 1 from org_user u where u.user_id=c.recipient and u.system=:system and u.tenant=:tenant ) ", sqlParams);
                userInfoService.getDao().deleteBySQL("delete from msg_message_recent_contact c" +
                        " where exists ( select 1 from org_user u where u.user_id=c.user_id and u.system=:system and u.tenant=:tenant ) ", sqlParams);

            }
            if (clearTypes.contains("userFile")) {
                total += userInfoService.getDao().deleteBySQL("delete from dms_file f" +
                        " where exists ( select 1 from org_user u ,dms_folder c where f.folder_uuid = c.uuid and  u.user_id=c.creator and u.system=:system and u.tenant=:tenant ) ", sqlParams);
                total += userInfoService.getDao().deleteBySQL("delete from dms_folder c" +
                        " where exists ( select 1 from org_user u where u.user_id=c.creator and u.system=:system and u.tenant=:tenant ) ", sqlParams);

            }

            if (clearTypes.contains("userOperationLog")) {
                userInfoService.getDao().deleteBySQL("delete from log_business_details d " +
                        " where exists ( select 1 from log_business_operation c , org_user u where d.log_id = c.uuid and  u.user_id=c.user_id and u.system=:system and u.tenant=:tenant ) ", sqlParams);
                total += userInfoService.getDao().deleteBySQL("delete from log_business_operation c" +
                        " where exists ( select 1 from org_user u where u.user_id=c.user_id and u.system=:system and u.tenant=:tenant ) ", sqlParams);

                total += userInfoService.getDao().deleteBySQL("delete from log_user_operation c" +
                        " where exists ( select 1 from org_user u where u.user_id=c.creator and u.system=:system and u.tenant=:tenant ) ", sqlParams);

                userInfoService.getDao().deleteBySQL("delete from log_business_details d " +
                        " where exists ( select 1 from log_business_operation c , org_user u where d.log_id = c.uuid and  u.user_id=c.user_id and u.system=:system and u.tenant=:tenant ) ", sqlParams);

                total += userInfoService.getDao().deleteBySQL("delete from log_manage_operation c" +
                        " where exists ( select 1 from org_user u where u.user_id=c.user_id and u.system=:system and u.tenant=:tenant ) ", sqlParams);

            }

        }
        return ExpectCleanupResult.total(total);
    }

    @Override
    public ExpectCleanupResult expectCleanupRows(Params params) {
        String system = StringUtils.defaultIfBlank(params.optString("system"), RequestSystemContextPathResolver.system());
        long total = 0L;
        if (StringUtils.isNotBlank(system)) {
            Map<String, Object> sqlParams = Maps.newHashMap();
            sqlParams.put("system", system);
            sqlParams.put("tenant", SpringSecurityUtils.getCurrentTenantId());
            List<String> clearTypes = (List<String>) params.get("clearTypes");
            if (clearTypes.contains("userPreference")) {
                List<QueryItem> items = userInfoService.listQueryItemBySQL("select count(1) as total from cd_user_preferences c" +
                        " where exists ( select 1 from org_user u where u.user_id=c.user_id and u.system=:system and u.tenant=:tenant ) ", sqlParams, null);
                total += items.get(0).getLong("total");
            }
            if (clearTypes.contains("userMailbox")) {
                List<QueryItem> items = userInfoService.listQueryItemBySQL("select count(1) as total from wm_mailbox c" +
                        " where exists ( select 1 from org_user u where u.user_id=c.user_id and u.system=:system and u.tenant=:tenant ) ", sqlParams, null);
                total += items.get(0).getLong("total");
//                items = userInfoService.listQueryItemBySQL("select count(1) as total from wm_mail_user_contact c" +
//                        " where exists ( select 1 from org_user u where u.user_id=c.user_id and u.system=:system and u.tenant=:tenant ) ", sqlParams, null);
//                total += items.get(0).getLong("total");
                items = userInfoService.listQueryItemBySQL("select count(1) as total from wm_mail_folder c" +
                        " where exists ( select 1 from org_user u where u.user_id=c.user_id and u.system=:system and u.tenant=:tenant ) ", sqlParams, null);
                total += items.get(0).getLong("total");
//                items = userInfoService.listQueryItemBySQL("select count(1) as total from wm_mail_paper c" +
//                        " where exists ( select 1 from org_user u where u.user_id=c.user_id and u.system=:system and u.tenant=:tenant ) ", sqlParams, null);
//                total += items.get(0).getLong("total");
            }

            if (clearTypes.contains("userMessage")) {
                List<QueryItem> items = userInfoService.listQueryItemBySQL("select count(1) as total from msg_message_outbox c" +
                        " where exists ( select 1 from org_user u where u.user_id=c.sender and u.system=:system and u.tenant=:tenant ) ", sqlParams, null);
                total += items.get(0).getLong("total");
                items = userInfoService.listQueryItemBySQL("select count(1) as total from msg_message_inbox c" +
                        " where exists ( select 1 from org_user u where u.user_id=c.recipient and u.system=:system and u.tenant=:tenant ) ", sqlParams, null);
                total += items.get(0).getLong("total");
            }

            if (clearTypes.contains("userFile")) {
                List<QueryItem> items = userInfoService.listQueryItemBySQL("select count(1) as total from dms_file f" +
                        " where exists ( select 1 from org_user u ,dms_folder c where f.folder_uuid = c.uuid and  u.user_id=c.creator and u.system=:system and u.tenant=:tenant ) ", sqlParams, null);
                total += items.get(0).getLong("total");
                items = userInfoService.listQueryItemBySQL("select count(1) as total from dms_folder c" +
                        " where exists ( select 1 from org_user u where u.user_id=c.creator and u.system=:system and u.tenant=:tenant ) ", sqlParams, null);
                total += items.get(0).getLong("total");
            }

            if (clearTypes.contains("userOperationLog")) {
                List<QueryItem> items = userInfoService.listQueryItemBySQL("select count(1) as total from log_business_operation c" +
                        " where exists ( select 1 from org_user u where u.user_id=c.user_id and u.system=:system and u.tenant=:tenant ) ", sqlParams, null);
                total += items.get(0).getLong("total");

                items = userInfoService.listQueryItemBySQL("select count(1) as total from log_user_operation c" +
                        " where exists ( select 1 from org_user u where u.user_id=c.creator and u.system=:system and u.tenant=:tenant ) ", sqlParams, null);
                total += items.get(0).getLong("total");
                items = userInfoService.listQueryItemBySQL("select count(1) as total from log_manage_operation c" +
                        " where exists ( select 1 from org_user u where u.user_id=c.user_id and u.system=:system and u.tenant=:tenant ) ", sqlParams, null);
                total += items.get(0).getLong("total");
            }

        }
        return ExpectCleanupResult.total(total);
    }
}
