package com.wellsoft.pt.basicdata.datadict.datanode;

import com.wellsoft.pt.basicdata.datadict.service.DataDictionaryService;
import com.wellsoft.pt.basicdata.datanode.DataNode;
import com.wellsoft.pt.basicdata.datanode.service.AbstractDataNodeCrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;

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
@Component
public class DataDicDataNodeCrudService extends AbstractDataNodeCrudService {

    @Autowired
    DataDictionaryService dataDictionaryService;

    @Override
    public void moveDNodeAfterThatNode(String uuid, String thatUuid) {
        dataDictionaryService.moveDataDicAfterOther(uuid, thatUuid);
    }

    @Override
    public Serializable addDNodeAfterThatNode(DataNode dataNode, String thatUuid, String parentUuid) {
        return dataDictionaryService.quickAddDataDic(dataNode.getNodeName(), thatUuid, parentUuid);
    }

    @Override
    public String name() {
        return "数据字典节点服务";
    }
}
