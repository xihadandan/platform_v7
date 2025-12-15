package com.wellsoft.pt.api.export;

import com.google.common.collect.Lists;
import com.wellsoft.pt.api.entity.ApiOperationEntity;
import com.wellsoft.pt.basicdata.iexport.service.AbstractIexportDataProvider;
import com.wellsoft.pt.basicdata.iexport.suport.IExportTable;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2025年06月25日   chenq	 Create
 * </pre>
 */
@Service
@Transactional(readOnly = true)
public class ApiOperationIexportDataProvider extends AbstractIexportDataProvider<ApiOperationEntity, Long> {

    @Override
    public String getType() {
        return IexportType.ApiOperation;
    }

    @Override
    public String getTreeName(ApiOperationEntity apiOperationEntity) {
        return apiOperationEntity.getName();
    }

    @Override
    protected List<IExportTable> childTableStream() {
        return Lists.newArrayList(new IExportTable("select * from api_operation_param where operation_uuid=:uuid"),
                new IExportTable("select * from api_body_schema where operation_uuid=:uuid "));
    }
}
