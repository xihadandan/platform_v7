package com.wellsoft.pt.dms.service.impl;

import com.wellsoft.pt.dms.dao.impl.DmsDocExcUrgeDetailDaoImpl;
import com.wellsoft.pt.dms.entity.DmsDocExcUrgeDetailEntity;
import com.wellsoft.pt.dms.service.DmsDocExcUrgeDetailService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Description:
 *
 * @author chenq
 * @date 2018/5/18
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/5/18    chenq		2018/5/18		Create
 * </pre>
 */
@Service
public class DmsDocExcUrgeDetailServiceImpl extends AbstractJpaServiceImpl<DmsDocExcUrgeDetailEntity, DmsDocExcUrgeDetailDaoImpl, String> implements DmsDocExcUrgeDetailService {

    @Override
    public List<DmsDocExcUrgeDetailEntity> listDocExcUrgeDetail(String docExchageRecordUuid) {
        return this.dao.listDocExcUrgeDetail(docExchageRecordUuid);
    }
}
