package com.wellsoft.pt.di.dao.impl;

import com.wellsoft.pt.di.dao.DiTableColumnDataChangeDao;
import com.wellsoft.pt.di.entity.DiTableColumnDataChangeEntity;
import com.wellsoft.pt.jpa.dao.impl.EntityDaoImpl;
import org.springframework.stereotype.Repository;

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
@Repository
public class DiTableColumnDataChangeDaoImpl extends
        EntityDaoImpl<DiTableColumnDataChangeEntity> implements DiTableColumnDataChangeDao {
}
