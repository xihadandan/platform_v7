package com.wellsoft.pt.di.dao.impl;

import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.pt.di.dao.DiTableDataChangeDao;
import com.wellsoft.pt.di.entity.DiTableDataChangeEntity;
import com.wellsoft.pt.jpa.dao.impl.EntityDaoImpl;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

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
@Repository
public class DiTableDataChangeDaoImpl extends EntityDaoImpl<DiTableDataChangeEntity> implements
        DiTableDataChangeDao {
    @Override
    public List<DiTableDataChangeEntity> queryNoSyncTableDataChanges(List<String> tableName,
                                                                     PagingInfo pagingInfo) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("tableName", tableName);
        //未同步、同步中
        return this.listByHQLAndPage(
                "from DiTableDataChangeEntity where status =0 and tableName in (:tableName) order by createTime asc ",
                param,
                pagingInfo);
    }

    @Override
    public int batchUpdateStatus(int i, List<String> uuids) {
        Map<String, Object> data = Maps.newHashMap();
        data.put("status", i);
        data.put("uuids", uuids);
        return updateByHQL(
                "update DiTableDataChangeEntity set status=:status where uuid in (:uuids)", data);
    }

    @Override
    public void updateStatus2Status(int from, int to, List<String> tableDataUuids) {
        Map<String, Object> data = Maps.newHashMap();
        data.put("fromStatus", from);
        data.put("toStatus", to);
        data.put("uuids", tableDataUuids);
        updateByHQL(
                "update DiTableDataChangeEntity set status=:toStatus where status=:fromStatus and uuid in (:uuids)",
                data);
    }
}
