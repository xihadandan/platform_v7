package com.wellsoft.pt.dms.dao.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.dms.entity.DmsDataLabelEntity;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author chenq
 * @date 2018/6/11
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/6/11    chenq		2018/6/11		Create
 * </pre>
 */
@Repository
public class DmsDataLabelDaoImpl extends AbstractJpaDaoImpl<DmsDataLabelEntity, String> {

    public List<DmsDataLabelEntity> queryByUserIdAndModuleId(String currentUserId,
                                                             String moduleId) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("userId", currentUserId);
        param.put("moduleId", moduleId);
        return this.listByHQL(
                "from DmsDataLabelEntity where userId=:userId and moduleId=:moduleId order by createTime asc",
                param);
    }
}
