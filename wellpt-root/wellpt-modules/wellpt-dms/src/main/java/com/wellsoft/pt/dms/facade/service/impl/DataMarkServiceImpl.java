package com.wellsoft.pt.dms.facade.service.impl;

import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.dms.dao.impl.DataMarkDaoImpl;
import com.wellsoft.pt.dms.entity.DataMarkEntity;
import com.wellsoft.pt.dms.facade.service.DataMarkService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Description:
 *
 * @author chenq
 * @date 2018/6/19
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/6/19    chenq		2018/6/19		Create
 * </pre>
 */
@Service
public class DataMarkServiceImpl extends
        AbstractJpaServiceImpl<DataMarkEntity, DataMarkDaoImpl, String> implements
        DataMarkService {
    @Override
    @Transactional
    public void saveOrDeleteDataMarkRela(List<? extends DataMarkEntity> dataMarkEntityList,
                                         boolean isDeleteMark) {
        this.dao.saveOrDeleteDataMarkRela(dataMarkEntityList, isDeleteMark);

    }

    @Override
    @Transactional
    public void updateDataTableMarked(List<? extends DataMarkEntity> dataList, String tableName,
                                      String statusColumn, String updateTimeColumn,
                                      boolean isDeleteMark) {

        this.dao.updateDataTableMarked(dataList, tableName, statusColumn, updateTimeColumn,
                isDeleteMark);

    }

    @Override
    public List<String> existMarkRelasByRelaEntities(List<DataMarkEntity> dataMarkEntityList) {
        return this.dao.existMarkRelasByRelaEntities(dataMarkEntityList);

    }

    @Override
    public List<QueryItem> listMarkStatusByTable(List<String> requestUuids, String tableName,
                                                 String statusColumn) {
        return this.dao.listMarkStatusByTable(requestUuids, tableName, statusColumn);
    }

    @Override
    @Transactional
    public void saveOrDeleteMarkRela(List<String> dataUuids,
                                     Class<? extends DataMarkEntity> markClass,
                                     boolean isDeleteMark) {
        try {
            this.dao.saveOrDeleteDataMarkRela(dataUuids, markClass, isDeleteMark);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public List<? extends DataMarkEntity> listByDataUuidAndClass(String dataUuid,
                                                                 Class<? extends DataMarkEntity> markClass,
                                                                 PagingInfo page) {


        return this.dao.listByDataUuidAndClass(dataUuid, markClass, page);
    }
}
