package com.wellsoft.pt.webmail.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.webmail.dao.impl.WmMailContactBookGrpDaoImpl;
import com.wellsoft.pt.webmail.entity.WmMailContactBookGroupEntity;

import java.util.List;

/**
 * Description: 邮件个人通讯录分组
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
public interface WmMailContactBookGrpService extends
        JpaService<WmMailContactBookGroupEntity, WmMailContactBookGrpDaoImpl, String> {

    /**
     * 根据用户id查询邮件个人通讯录分组
     *
     * @param userId
     * @return
     */
    List<WmMailContactBookGroupEntity> listByUserId(String userId);

    /**
     * 根据uuids批量删除邮件个人通讯录分组
     *
     * @param uuids
     */
    void deleteByGroupUuids(List<String> uuids);

}
