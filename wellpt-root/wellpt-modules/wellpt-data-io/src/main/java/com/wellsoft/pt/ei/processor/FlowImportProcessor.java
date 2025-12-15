package com.wellsoft.pt.ei.processor;

import com.wellsoft.pt.ei.entity.DataImportRecord;
import com.wellsoft.pt.ei.processor.utils.ExpImpServiceBeanUtils;
import com.wellsoft.pt.ei.service.ExpImpService;
import com.wellsoft.pt.ei.service.impl.FlowExpImpService;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Auther: shenhb
 * @Date: 2021/9/29 10:53
 * @Description:
 */
@Component
public class FlowImportProcessor extends AbstractImportProcessor {


    @Override
    public List<ExpImpService> getExportServiceList(DataImportRecord record) {
        FlowExpImpService.dataImportRecordThreadLocal.set(record);
        return ExpImpServiceBeanUtils.flowExpImpServices(record.getDataTypeJson());
    }

}
