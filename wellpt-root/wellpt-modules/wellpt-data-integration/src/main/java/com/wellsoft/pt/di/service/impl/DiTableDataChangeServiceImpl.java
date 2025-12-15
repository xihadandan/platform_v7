package com.wellsoft.pt.di.service.impl;

import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.pt.di.dao.DiTableDataChangeDao;
import com.wellsoft.pt.di.entity.DiTableDataChangeEntity;
import com.wellsoft.pt.di.service.DiTableDataChangeService;
import com.wellsoft.pt.jpa.service.impl.AbstractEntityServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
@Service
public class DiTableDataChangeServiceImpl extends
        AbstractEntityServiceImpl<DiTableDataChangeEntity, DiTableDataChangeDao> implements
        DiTableDataChangeService {


    @Override
    public List<DiTableDataChangeEntity> queryNoSyncTableDataChanges(List<String> tableName,
                                                                     PagingInfo pagingInfo) {
        return this.dao.queryNoSyncTableDataChanges(tableName, pagingInfo);

    }

    @Override
    @Transactional
    public int batchUpdateStatus(int i, List<String> uuids) {
        return this.dao.batchUpdateStatus(i, uuids);
    }

    @Override
    @Transactional
    public void updateStatus2Status(int from, int to, List<String> tableDataUuids) {
        this.dao.updateStatus2Status(from, to, tableDataUuids);
    }
}
