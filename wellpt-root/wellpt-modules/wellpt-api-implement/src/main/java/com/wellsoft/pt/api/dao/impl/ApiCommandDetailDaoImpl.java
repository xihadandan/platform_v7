package com.wellsoft.pt.api.dao.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.api.entity.ApiCommandDetailEntity;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import org.springframework.stereotype.Repository;

import java.util.Map;

/**
 * Description:
 *
 * @author chenq
 * @date 2018/8/13
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/8/13    chenq		2018/8/13		Create
 * </pre>
 */
@Repository
public class ApiCommandDetailDaoImpl extends AbstractJpaDaoImpl<ApiCommandDetailEntity, String> {
    public ApiCommandDetailEntity getByCommandUuid(String commandUuid) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("commandUuid", commandUuid);
        return this.getOneByHQL("from ApiCommandDetailEntity where commandUuid=:commandUuid", map);

    }
}
