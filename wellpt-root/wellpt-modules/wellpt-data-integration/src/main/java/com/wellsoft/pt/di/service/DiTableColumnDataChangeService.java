package com.wellsoft.pt.di.service;

import com.wellsoft.pt.di.dao.DiTableColumnDataChangeDao;
import com.wellsoft.pt.di.entity.DiTableColumnDataChangeEntity;
import com.wellsoft.pt.jpa.service.EntityService;

import java.util.List;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/8/26
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/8/26    chenq		2019/8/26		Create
 * </pre>
 */
public interface DiTableColumnDataChangeService extends
        EntityService<DiTableColumnDataChangeEntity, DiTableColumnDataChangeDao> {


    boolean existLobDataChange(String uuid);

    List<DiTableColumnDataChangeEntity> getAllColumnDataByUuid(String uuid);
}
