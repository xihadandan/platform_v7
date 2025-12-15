package com.wellsoft.pt.basicdata.datanode.facade.service.impl;

import com.wellsoft.context.component.select2.Select2DataBean;
import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.basicdata.datanode.DataNode;
import com.wellsoft.pt.basicdata.datanode.facade.service.DataNodeFacadeService;
import com.wellsoft.pt.basicdata.datanode.service.AbstractDataNodeCrudService;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
@Service
public class DataNodeFacadeServiceImpl implements DataNodeFacadeService {

    @Autowired
    private List<AbstractDataNodeCrudService> dataNodeCrudServices;

    @Override
    public Select2QueryData dataNodeServiceSelections(Select2QueryInfo queryInfo) {
        Select2QueryData select2QueryData = new Select2QueryData();
        for (AbstractDataNodeCrudService crudService : dataNodeCrudServices) {
            select2QueryData.addResultData(
                    new Select2DataBean(AopUtils.getTargetClass(crudService).getCanonicalName(),
                            crudService.name()));
        }
        return select2QueryData;
    }

    @Override
    public void moveDataNode(String className, String uuid, String thatUuid) {
        try {
            AbstractDataNodeCrudService crudService = (AbstractDataNodeCrudService) ApplicationContextHolder.getBean(
                    Class.forName(className));
            if (crudService != null) {
                crudService.moveDNodeAfterThatNode(uuid, thatUuid);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void quickAddDataNode(String className, DataNode dataNode, String thatUuid, String parentUuid) {
        try {
            AbstractDataNodeCrudService crudService = (AbstractDataNodeCrudService) ApplicationContextHolder.getBean(
                    Class.forName(className));
            if (crudService != null) {
                crudService.addDNodeAfterThatNode(dataNode, thatUuid, parentUuid);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
