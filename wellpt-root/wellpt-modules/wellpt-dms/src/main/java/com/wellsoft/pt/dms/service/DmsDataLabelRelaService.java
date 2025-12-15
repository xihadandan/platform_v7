package com.wellsoft.pt.dms.service;

import com.wellsoft.pt.dms.dao.impl.DmsDataLabelRelaDaoImpl;
import com.wellsoft.pt.dms.entity.DataLabelRelaEntity;
import com.wellsoft.pt.dms.entity.DmsDataLabelRelaEntity;
import com.wellsoft.pt.jpa.service.JpaService;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:标签与数据的关系服务接口
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
public interface DmsDataLabelRelaService extends
        JpaService<DmsDataLabelRelaEntity, DmsDataLabelRelaDaoImpl, String> {

    void saveRelaEntity(DataLabelRelaEntity labelRelaEntity);

    void deleteByDataUuids(List<String> dataUuid);

    void deleteByDataUuidAndLabelUuid(String dataUuid, String labelUuid);

    List<DmsDataLabelRelaEntity> listByDataUuid(String dataUuid);

    void deleteByLabelUuids(ArrayList<String> labelUuids);

    List<DmsDataLabelRelaEntity> listDataRelaEntities(String dataUuid, String entityClassName);

    void deleteByUuidsAndEntityClass(List<String> relaUuid, String entityClassName);
}
