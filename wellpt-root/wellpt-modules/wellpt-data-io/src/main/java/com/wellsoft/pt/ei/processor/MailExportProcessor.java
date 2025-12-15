package com.wellsoft.pt.ei.processor;

import com.wellsoft.pt.ei.entity.DataExportRecord;
import com.wellsoft.pt.ei.processor.utils.ExpImpServiceBeanUtils;
import com.wellsoft.pt.ei.service.ExpImpService;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Auther: yt
 * @Date: 2021/9/24 10:18
 * @Description: 导出处理器
 */
@Component
public class MailExportProcessor extends AbstractExportProcessor {

    /**
     * 获取ExportService集合
     *
     * @return
     */
    @Override
    public List<ExpImpService> getExportServiceList(DataExportRecord record) {
        return ExpImpServiceBeanUtils.mailExpImpServices(record.getDataTypeJson());
    }


}
