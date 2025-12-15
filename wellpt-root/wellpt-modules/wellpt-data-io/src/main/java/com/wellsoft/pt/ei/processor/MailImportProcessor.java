package com.wellsoft.pt.ei.processor;

import com.wellsoft.pt.ei.entity.DataImportRecord;
import com.wellsoft.pt.ei.processor.utils.ExpImpServiceBeanUtils;
import com.wellsoft.pt.ei.service.ExpImpService;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Auther: yt
 * @Date: 2021/9/29 10:53
 * @Description:
 */
@Component
public class MailImportProcessor extends AbstractImportProcessor {


    @Override
    public List<ExpImpService> getExportServiceList(DataImportRecord record) {
        return ExpImpServiceBeanUtils.mailExpImpServices(record.getDataTypeJson());
    }
}
