package com.wellsoft.pt.bpm.engine.dao.impl;

import com.wellsoft.pt.bpm.engine.dao.WorkFrequencyRecordDao;
import com.wellsoft.pt.bpm.engine.entity.WorkFrequencyRecord;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import org.springframework.stereotype.Repository;

/**
 * Description: 流程使用记录dao
 *
 * @author xujm
 * @version 1.0
 * @date @2015-6-4
 */
@Repository
public class WorkFrequencyRecordDaoImpl extends AbstractJpaDaoImpl<WorkFrequencyRecord, String> implements
        WorkFrequencyRecordDao {

}
