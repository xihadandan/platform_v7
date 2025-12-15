package com.wellsoft.pt.dms.service;

import com.wellsoft.pt.dms.bean.DmsDocExcContactBookUnitDto;
import com.wellsoft.pt.dms.dao.impl.DmsDocExcContactBookUnitDaoImpl;
import com.wellsoft.pt.dms.entity.DmsDocExcContactBookUnitEntity;
import com.wellsoft.pt.jpa.service.JpaService;

import java.util.List;

/**
 * Description: 文档交换-通讯录单位服务
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
public interface DmsDocExcContactBookUnitService extends
        JpaService<DmsDocExcContactBookUnitEntity, DmsDocExcContactBookUnitDaoImpl, String> {
    void saveContactBookUnit(DmsDocExcContactBookUnitDto contactBookUnitDto);

    List<DmsDocExcContactBookUnitEntity> listByUserId(String currentUserId);

    List<DmsDocExcContactBookUnitEntity> listByUserIdAndModule(String currentUserId,
                                                               String moduleId);

    DmsDocExcContactBookUnitEntity getByUnitId(String unitId);

    List<DmsDocExcContactBookUnitEntity> listBySysUnitIdAndModule(String unitId, String moduleId);
}
