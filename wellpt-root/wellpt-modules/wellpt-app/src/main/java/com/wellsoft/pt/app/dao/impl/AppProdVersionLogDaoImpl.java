package com.wellsoft.pt.app.dao.impl;

import com.wellsoft.pt.app.entity.AppProductVersionLogEntity;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import org.springframework.stereotype.Repository;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2023年07月31日   chenq	 Create
 * </pre>
 */
@Repository
public class AppProdVersionLogDaoImpl extends AbstractJpaDaoImpl<AppProductVersionLogEntity, Long> {

    public AppProductVersionLogEntity getVersionLogByVersionUuid(Long prodVersionUuid) {
        return this.getOneByFieldEq("prodVersionUuid", prodVersionUuid);
    }
}
