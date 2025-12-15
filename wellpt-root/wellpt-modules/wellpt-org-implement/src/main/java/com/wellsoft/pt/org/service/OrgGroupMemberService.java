package com.wellsoft.pt.org.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.org.dao.impl.OrgGroupMemberDaoImpl;
import com.wellsoft.pt.org.entity.OrgGroupMemberEntity;

import java.util.List;
import java.util.Set;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2023年02月10日   chenq	 Create
 * </pre>
 */
public interface OrgGroupMemberService extends JpaService<OrgGroupMemberEntity, OrgGroupMemberDaoImpl, Long> {

    Set<String> queryGroupUuidsByMemberIds(List<String> memberIds);

    List<String> getMemberIdsByGroupUuid(Long groupUuid);

    List<String> getMemberIdsByGroupId(String groupId);

}
