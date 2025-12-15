package com.wellsoft.pt.dms.dao.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.dms.entity.DmsDataLabelRelaEntity;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Description: 标签与数据的关系DAO
 *
 * @author chenq
 * @date 2018/6/12
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/6/12    chenq		2018/6/12		Create
 * </pre>
 */
@Repository
public class DmsDataLabelRelaDaoImpl extends AbstractJpaDaoImpl<DmsDataLabelRelaEntity, String> {
    public void deleteByDataUuids(List<String> dataUuid) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("dataUuids", dataUuid);
        this.deleteByHQL("delete from DmsDataLabelRelaEntity where dataUuid in (:dataUuids)",
                param);
    }

    public void deleteByDataUuidAndLabelUuid(String dataUuid, String labelUuid) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("dataUuid", dataUuid);
        param.put("labelUuid", labelUuid);
        this.deleteByHQL(
                "delete from DmsDataLabelRelaEntity where dataUuid=:dataUuid and labelUuid=:labelUuid",
                param);
    }

    public List<DmsDataLabelRelaEntity> listByDataUuid(String dataUuid) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("dataUuid", dataUuid);
        return this.listByHQL(
                " from DmsDataLabelRelaEntity where dataUuid=:dataUuid order by createTime asc",
                param);
    }

    public void deleteByLabelUuids(ArrayList<String> labelUuids) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("labelUuids", labelUuids);
        this.deleteByHQL(
                "delete from DmsDataLabelRelaEntity where  labelUuid in(:labelUuids)",
                param);
    }

    public List<DmsDataLabelRelaEntity> listDataRelaEntities(String dataUuid,
                                                             String entityClassName) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("dataUuid", dataUuid);
        return this.listByHQL(
                " from " + entityClassName + " where dataUuid=:dataUuid order by createTime asc",
                param);
    }
}
