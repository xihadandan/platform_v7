package com.wellsoft.pt.dms.service;

import com.wellsoft.pt.dms.bean.DmsDocExcExtraSendDto;
import com.wellsoft.pt.dms.core.support.DocExchangeActionData;
import com.wellsoft.pt.dms.dao.impl.DmsDocExcExtraSendDaoImpl;
import com.wellsoft.pt.dms.entity.DmsDocExcExtraSendEntity;
import com.wellsoft.pt.jpa.service.JpaService;

import java.util.List;

/**
 * Description: 文档交换的补充发送详情服务
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
public interface DmsDocExcExtraSendService extends
        JpaService<DmsDocExcExtraSendEntity, DmsDocExcExtraSendDaoImpl, String> {
    void saveExtraSendData(DocExchangeActionData actionData);

    /**
     * 查询补充发送的详情列表
     *
     * @param docExchageRecordUuid
     * @return
     */
    List<DmsDocExcExtraSendDto> listDocExcExtraSendDetail(String docExchageRecordUuid);
}
