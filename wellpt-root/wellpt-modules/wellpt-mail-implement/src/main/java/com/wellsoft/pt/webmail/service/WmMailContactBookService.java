package com.wellsoft.pt.webmail.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.webmail.dao.impl.WmMailContactBookDaoImpl;
import com.wellsoft.pt.webmail.entity.WmMailContactBookEntity;

import java.util.List;

/**
 * Description: 邮件个人通讯录服务
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
public interface WmMailContactBookService extends
        JpaService<WmMailContactBookEntity, WmMailContactBookDaoImpl, String> {

    /**
     * 根据userId查询邮件个人通讯录列表
     *
     * @param userId
     * @return
     */
    List<WmMailContactBookEntity> listByUserId(String userId);

    /**
     * 根据 分组Id 查询邮件个人通讯录列表
     *
     * @param grpId
     * @return
     */
    List<WmMailContactBookEntity> listByGrpId(String grpId);

    /**
     * 更新分组管理的联系人上的分组信息为空
     *
     * @param groupUuid
     */
    void updateGroupToNullByGroupUuids(List<String> groupUuids);

    /**
     * 根据联系人ID获取联系人实体
     *
     * @param id
     * @return
     */
    WmMailContactBookEntity getByContactId(String id);

    /**
     * 根据分组ID获取联系人实体
     *
     * @param igrpIdd
     * @return
     */
    List<String> listContactIdByGrpId(String grpId);

}
