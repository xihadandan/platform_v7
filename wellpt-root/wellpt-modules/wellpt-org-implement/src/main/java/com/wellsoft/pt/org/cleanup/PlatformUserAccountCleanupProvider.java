package com.wellsoft.pt.org.cleanup;

import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.cleanup.AbstractDataCleanupDataCleanupProvider;
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
 * 2024年06月04日   chenq	 Create
 * </pre>
 */
@Service
public class PlatformUserAccountCleanupProvider extends AbstractDataCleanupDataCleanupProvider {
    @Override
    public String getType() {
        return "platformUserAccount";
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ExpectCleanupResult cleanup(Params params) {
        int count = 0;
        List<String> clearTypes = (List<String>) params.get("clearTypes");
        Map<String, Object> sqlParams = Maps.newHashMap();
        sqlParams.put("type", new Integer[]{1, 2, 3});
        if (clearTypes.contains("userPreference")) {
            count += this.nativeDao.batchExecute("delete  from cd_user_preferences c" +
                    " where exists ( select 1 from user_account u, user_info i  where u.type in (:type) and u.login_name = i.login_name and i.user_id=c.user_id ) ", sqlParams);

        }

        if (clearTypes.contains("userMailbox")) {
            count += this.nativeDao.batchExecute("delete  from wm_mailbox c" +
                    " where exists ( select 1 from user_account u, user_info i  where u.type in (:type) and u.login_name = i.login_name and i.user_id=c.user_id ) ", sqlParams);


            count += this.nativeDao.batchExecute("delete  from wm_mail_folder c" +
                    " where exists ( select 1 from user_account u, user_info i  where u.type in (:type) and u.login_name = i.login_name and i.user_id=c.user_id ) ", sqlParams);


        }

        if (clearTypes.contains("userMessage")) {
            count += this.nativeDao.batchExecute("delete  from msg_message_outbox c" +
                    " where exists ( select 1 from user_account u, user_info i  where u.type in (:type) and u.login_name = i.login_name and i.user_id=c.sender ) ", sqlParams);


            count += this.nativeDao.batchExecute("delete  from msg_message_inbox c" +
                    " where exists ( select 1 from user_account u, user_info i  where u.type in (:type) and u.login_name = i.login_name and i.user_id=c.recipient ) ", sqlParams);

        }

        if (clearTypes.contains("userFile")) {
            count += this.nativeDao.batchExecute("delete  from dms_file f" +
                    " where exists ( select 1 from  dms_folder c , user_account u, user_info i  where f.folder_uuid = c.uuid  and  u.type in (:type) and u.login_name = i.login_name and i.user_id=c.creator ) ", sqlParams);


            count += this.nativeDao.batchExecute("delete  from dms_folder c" +
                    " where exists ( select 1 from user_account u, user_info i  where u.type in (:type) and u.login_name = i.login_name and i.user_id=c.creator ) ", sqlParams);

        }
        this.nativeDao.batchExecute("delete from user_info i where exists ( select 1 from user_account a where  a.type in (:type) and a.login_name =i.login_name) ", sqlParams);
        count += this.nativeDao.batchExecute("delete from user_account a where a.type in (:type) ", sqlParams);

        // 额外删除不存在用户信息的账号
        this.nativeDao.batchExecute("delete from user_account a where not exists ( select 1 from  user_info i " +
                "where i.login_name = a.login_name   ) ", sqlParams);
        return ExpectCleanupResult.total(count);
    }

    @Override
    public ExpectCleanupResult expectCleanupRows(Params params) {
        int count = 0;
        List<String> clearTypes = (List<String>) params.get("clearTypes");
        Map<String, Object> sqlParams = Maps.newHashMap();
        sqlParams.put("type", new Integer[]{1, 2, 3});
        List<QueryItem> items = nativeDao.query("select count(1) as total from user_account a where a.type in (:type) ", sqlParams, QueryItem.class);
        count += items.get(0).getLong("total");

        if (clearTypes.contains("userPreference")) {
            items = nativeDao.query("select count(1) as total from cd_user_preferences c" +
                    " where exists ( select 1 from user_account u, user_info i  where u.type in (:type) and u.login_name = i.login_name and i.user_id=c.user_id ) ", sqlParams, QueryItem.class);
            count += items.get(0).getLong("total");
        }

        if (clearTypes.contains("userMailbox")) {
            items = nativeDao.query("select count(1) as total from wm_mailbox c" +
                    " where exists ( select 1 from user_account u, user_info i  where u.type in (:type) and u.login_name = i.login_name and i.user_id=c.user_id ) ", sqlParams, QueryItem.class);
            count += items.get(0).getLong("total");

            items = nativeDao.query("select count(1) as total from wm_mail_folder c" +
                    " where exists ( select 1 from user_account u, user_info i  where u.type in (:type) and u.login_name = i.login_name and i.user_id=c.user_id ) ", sqlParams, QueryItem.class);
            count += items.get(0).getLong("total");


        }

        if (clearTypes.contains("userMessage")) {
            items = nativeDao.query("select count(1) as total from msg_message_outbox c" +
                    " where exists ( select 1 from user_account u, user_info i  where u.type in (:type) and u.login_name = i.login_name and i.user_id=c.sender ) ", sqlParams, QueryItem.class);
            count += items.get(0).getLong("total");

            items = nativeDao.query("select count(1) as total from msg_message_inbox c" +
                    " where exists ( select 1 from user_account u, user_info i  where u.type in (:type) and u.login_name = i.login_name and i.user_id=c.recipient ) ", sqlParams, QueryItem.class);
            count += items.get(0).getLong("total");
        }

        if (clearTypes.contains("userFile")) {
            items = nativeDao.query("select count(1) as total from dms_file f" +
                    " where exists ( select 1 from  dms_folder c , user_account u, user_info i  where f.folder_uuid = c.uuid  and  u.type in (:type) and u.login_name = i.login_name and i.user_id=c.creator ) ", sqlParams, QueryItem.class);
            count += items.get(0).getLong("total");

            items = nativeDao.query("select count(1) as total from dms_folder c" +
                    " where exists ( select 1 from user_account u, user_info i  where u.type in (:type) and u.login_name = i.login_name and i.user_id=c.creator ) ", sqlParams, QueryItem.class);
            count += items.get(0).getLong("total");
        }


        return ExpectCleanupResult.total(count);
    }
}
