package com.wellsoft.pt.dms.service;

import com.wellsoft.pt.dms.bean.DmsDocExcContactBookDto;
import com.wellsoft.pt.dms.dao.impl.DmsDocExcContactBookDaoImpl;
import com.wellsoft.pt.dms.entity.DmsDocExcContactBookEntity;
import com.wellsoft.pt.jpa.service.JpaService;

import java.util.List;
import java.util.Set;

/**
 * Description:文档交换-通讯录服务
 *
 * @author chenq
 * @date 2018/5/31
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/5/31    chenq		2018/5/31		Create
 * </pre>
 */
public interface DmsDocExcContactBookService extends
        JpaService<DmsDocExcContactBookEntity, DmsDocExcContactBookDaoImpl, String> {
    void saveContactBook(DmsDocExcContactBookDto contactBookDto);

    List<DmsDocExcContactBookEntity> listByUnitUuid(String uuid);

    List<DmsDocExcContactBookEntity> listByUserId(String currentUserId);


    List<DmsDocExcContactBookEntity> listByUserIdAndModule(String currentUserId, String moduleId);

    List<DmsDocExcContactBookEntity> listByUnitId(String unitId);

    DmsDocExcContactBookEntity getByContactId(String contactId);

    /**
     * 根据通讯录ID解析返回用户ID
     *
     * @param ui
     * @return
     */
    List<String> explainUserIdsByContactBookId(String ui);


    /**
     * 解析组织树选择的组织节点ID字符串，返回用户ID集合
     *
     * @param ids
     * @return
     */
    Set<String> explainUserIdsBySelectIds(String ids);

    List<DmsDocExcContactBookEntity> listBySysUnitIdAndModule(String unitId, String moduleId);
}
