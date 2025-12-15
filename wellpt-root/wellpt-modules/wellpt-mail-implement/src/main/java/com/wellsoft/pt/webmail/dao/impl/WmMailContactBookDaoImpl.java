package com.wellsoft.pt.webmail.dao.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import com.wellsoft.pt.webmail.entity.WmMailContactBookEntity;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Description: 邮件联系人dao实现
 *
 * @author chenq
 * @date 2018/6/6
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/6/6    chenq		2018/6/6		Create
 * </pre>
 */
@Repository
public class WmMailContactBookDaoImpl extends AbstractJpaDaoImpl<WmMailContactBookEntity, String> {
    public List<WmMailContactBookEntity> listByUserId(String userId) {
        return listByFieldEqValue("creator", userId);
    }

    public List<WmMailContactBookEntity> listByGrpId(String grpId) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("groupId", grpId);
        return this.listByHQL(
                "from WmMailContactBookEntity b where exists (select 1 from WmMailContactBookGroupEntity g where g.uuid=b.groupUuid and g.groupId=:groupId) order by b.createTime desc",
                param);
    }

    public void updateGroupToNullByGroupUuids(List<String> groupUuids) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("groupUuids", groupUuids);
        this.updateBySQL(
                "update UF_PT_MAIL_CONTACT_BOOK set group_uuid=null,group_data=null where group_uuid in (:groupUuids)",
                param);
    }

    public WmMailContactBookEntity getByContactId(String id) {
        List<WmMailContactBookEntity> bookEntities = listByFieldEqValue("contactId", id);
        return CollectionUtils.isNotEmpty(bookEntities) ? bookEntities.get(0) : null;
    }

    public List<String> listContactIdByGrpId(String grpId) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("grpId", grpId);
        return this.listCharSequenceByHQL(
                "select contactId from WmMailContactBookEntity b where exists (select 1 from WmMailContactBookGroupEntity g where g.uuid=b.groupUuid and g.groupId=:grpId)",
                param);
    }


}
