package com.wellsoft.pt.dms.dao.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.dms.entity.DmsDocExcContactBookUnitEntity;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Description: 文档交换-通讯录单位dao服务
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
@Repository
public class DmsDocExcContactBookUnitDaoImpl extends
        AbstractJpaDaoImpl<DmsDocExcContactBookUnitEntity, String> {
    public List<DmsDocExcContactBookUnitEntity> listByUserId(String currentUserId) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("userId", currentUserId);
        return this.listByHQL(" from DmsDocExcContactBookUnitEntity where creator=:userId", param);
    }

    public List<DmsDocExcContactBookUnitEntity> listByUserIdAndModule(String currentUserId,
                                                                      String moduleId) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("userId", currentUserId);
        param.put("moduleId", moduleId);
        return this.listByHQL(
                " from DmsDocExcContactBookUnitEntity where creator=:userId and moduleId=:moduleId",
                param);
    }

    public List<DmsDocExcContactBookUnitEntity> listBySysUnitIdAndModule(String unitId,
                                                                         String moduleId) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("systemUnitId", unitId);
        param.put("moduleId", moduleId);
        return this.listByHQL(
                " from DmsDocExcContactBookUnitEntity where systemUnitId=:systemUnitId and moduleId=:moduleId",
                param);
    }
}
