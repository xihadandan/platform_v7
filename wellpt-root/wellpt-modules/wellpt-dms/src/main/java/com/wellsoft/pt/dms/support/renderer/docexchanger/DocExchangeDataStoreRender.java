package com.wellsoft.pt.dms.support.renderer.docexchanger;

import com.wellsoft.pt.basicdata.datastore.support.renderer.AbstractCustomDataStoreRenderer;
import com.wellsoft.pt.dms.service.DmsDocExchangeRecordDetailService;
import com.wellsoft.pt.dms.service.DmsDocExchangeRecordService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Description: 文档交换器数据源渲染器
 *
 * @author chenq
 * @date 2018/5/26
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/5/26    chenq		2018/5/26		Create
 * </pre>
 */
public abstract class DocExchangeDataStoreRender extends AbstractCustomDataStoreRenderer {

    @Autowired
    protected DmsDocExchangeRecordService dmsDocExchangeRecordService;

    @Autowired
    protected DmsDocExchangeRecordDetailService dmsDocExchangeRecordDetailService;
}
