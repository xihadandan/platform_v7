package com.wellsoft.pt.dms.service;

import com.wellsoft.pt.dms.dao.impl.DmsDocExcUrgeDetailDaoImpl;
import com.wellsoft.pt.dms.entity.DmsDocExcUrgeDetailEntity;
import com.wellsoft.pt.jpa.service.JpaService;

import java.util.List;

/**
 * Description: 催办情况服务接口
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
public interface DmsDocExcUrgeDetailService extends JpaService<DmsDocExcUrgeDetailEntity, DmsDocExcUrgeDetailDaoImpl, String> {
    /**
     * 获取文档交换的催办详情
     *
     * @param docExchageRecordUuid
     * @return
     */
    List<DmsDocExcUrgeDetailEntity> listDocExcUrgeDetail(String docExchageRecordUuid);
}
