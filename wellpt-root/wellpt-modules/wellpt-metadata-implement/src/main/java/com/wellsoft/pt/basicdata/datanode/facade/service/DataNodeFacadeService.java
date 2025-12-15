package com.wellsoft.pt.basicdata.datanode.facade.service;

import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.context.service.Facade;
import com.wellsoft.pt.basicdata.datanode.DataNode;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/10/21
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/10/21    chenq		2019/10/21		Create
 * </pre>
 */
public interface DataNodeFacadeService extends Facade {


    Select2QueryData dataNodeServiceSelections(Select2QueryInfo queryInfo);


    void moveDataNode(String className, String uuid, String thatUuid);

    void quickAddDataNode(String className, DataNode dataNode, String thatUuid, String parentUuid);

}
