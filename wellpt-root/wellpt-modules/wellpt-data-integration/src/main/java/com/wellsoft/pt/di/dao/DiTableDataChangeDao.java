package com.wellsoft.pt.di.dao;

import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.pt.di.entity.DiTableDataChangeEntity;
import com.wellsoft.pt.jpa.dao.EntityDao;

import java.util.List;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/8/23
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/8/23    chenq		2019/8/23		Create
 * </pre>
 */
public interface DiTableDataChangeDao extends EntityDao<DiTableDataChangeEntity> {
    List<DiTableDataChangeEntity> queryNoSyncTableDataChanges(List<String> tableName,
                                                              PagingInfo pagingInfo);

    int batchUpdateStatus(int i, List<String> uuids);

    void updateStatus2Status(int from, int to, List<String> tableDataUuids);
}
