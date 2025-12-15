package com.wellsoft.pt.dms.dao.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.dms.entity.DmsDocExcContactBookEntity;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Description: 文档交换-通讯录dao服务
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
public class DmsDocExcContactBookDaoImpl extends
        AbstractJpaDaoImpl<DmsDocExcContactBookEntity, String> {
    public List<DmsDocExcContactBookEntity> listByUnitUuid(String unitUuid) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("unitUuid", unitUuid);
        return this.listByHQL(" from DmsDocExcContactBookEntity where contactUnitUuid=:unitUuid",
                param);


    }

    public List<DmsDocExcContactBookEntity> listByUserId(String currentUserId) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("userId", currentUserId);
        return this.listByHQL(" from DmsDocExcContactBookEntity where creator=:userId",
                param);
    }

    public List<DmsDocExcContactBookEntity> listByUserIdAndModule(String currentUserId,
                                                                  String moduleId) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("userId", currentUserId);
        param.put("moduleId", moduleId);
        return this.listByHQL(
                " from DmsDocExcContactBookEntity where creator=:userId and moduleId=:moduleId",
                param);
    }

    public List<DmsDocExcContactBookEntity> listBySysUnitIdAndModule(String unitId,
                                                                     String moduleId) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("unitId", unitId);
        param.put("moduleId", moduleId);
        return this.listByHQL(
                " from DmsDocExcContactBookEntity where systemUnitId=:unitId and moduleId=:moduleId",
                param);
    }
}
