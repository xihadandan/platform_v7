package com.wellsoft.pt.dms.facade.service;

import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.dms.entity.DataMarkEntity;

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
public interface DataMarkService {

    void saveOrDeleteDataMarkRela(List<? extends DataMarkEntity> dataMarkEntityList,
                                  boolean isDeleteMark);

    void updateDataTableMarked(List<? extends DataMarkEntity> dataList, String tableName,
                               String statusColumn, String updateTimeColumn,
                               boolean isDeleteMark);

    List<String> existMarkRelasByRelaEntities(List<DataMarkEntity> dataMarkEntityList);

    List<QueryItem> listMarkStatusByTable(List<String> requestUuids, String tableName,
                                          String columnName);


    void saveOrDeleteMarkRela(List<String> dataUuids,
                              Class<? extends DataMarkEntity> markClass,
                              boolean isDeleteMark);

    List<? extends DataMarkEntity> listByDataUuidAndClass(String dataUuid,
                                                          Class<? extends DataMarkEntity> markClass,
                                                          PagingInfo page);
}
