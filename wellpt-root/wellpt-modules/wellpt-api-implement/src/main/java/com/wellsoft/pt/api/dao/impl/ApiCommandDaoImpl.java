package com.wellsoft.pt.api.dao.impl;

import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.pt.api.entity.ApiCommandEntity;
import com.wellsoft.pt.api.enums.CommandStatusEnum;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
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
public class ApiCommandDaoImpl extends AbstractJpaDaoImpl<ApiCommandEntity, String> {

    public List<ApiCommandEntity> listByGreaterThanRetryTime(Date date) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("time", date);
        param.put("status", CommandStatusEnum.FAIL);
        PagingInfo page = new PagingInfo(0, 10, false);
        return this.listByHQLAndPage(
                "from ApiCommandEntity where status=:status and nextRetryTime <=:time and nextRetryTime is not null order by nextRetryTime desc",
                param, page);

    }
}
