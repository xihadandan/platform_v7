package com.wellsoft.pt.app.service.impl;

import com.wellsoft.pt.app.dao.impl.AppTipNoLongerRemindDaoImpl;
import com.wellsoft.pt.app.entity.AppTipNoLongerRemindEntity;
import com.wellsoft.pt.app.service.AppTipNoLongerRemindService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/10/16
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/10/16    chenq		2019/10/16		Create
 * </pre>
 */
@Service
public class AppTipNoLongerRemindServiceImpl extends
        AbstractJpaServiceImpl<AppTipNoLongerRemindEntity, AppTipNoLongerRemindDaoImpl, String> implements
        AppTipNoLongerRemindService {
    @Override
    public boolean existAppTipNoLongerRemind(String tipCode, String userId) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("userId", userId);
        values.put("tipCode", tipCode);
        return dao.countByHQL("from AppTipNoLongerRemindEntity t where t.tipCode = :tipCode and t.userId = :userId", values) > 0;
        // return CollectionUtils.isNotEmpty(this.dao.listByEntity(new AppTipNoLongerRemindEntity(tipCode, userId)));
    }
}
