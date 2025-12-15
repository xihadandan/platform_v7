package com.wellsoft.pt.di.export;

import com.google.common.collect.Lists;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.suport.IexportDataResultSetUtils;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import com.wellsoft.pt.di.entity.DiConfigEntity;
import com.wellsoft.pt.task.iexport.acceptor.JobDetailsIexportData;
import com.wellsoft.pt.task.service.JobDetailsService;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Description:
 *
 * @author chenq
 * @date 2018/9/26
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/9/26    chenq		2018/9/26		Create
 * </pre>
 */
public class DataIntegrationConfigImpExpConfIexportData extends IexportData {

    DiConfigEntity diConfigEntity;

    public DataIntegrationConfigImpExpConfIexportData(
            DiConfigEntity dxDataImpExpConfEntity) {
        this.diConfigEntity = dxDataImpExpConfEntity;
    }

    @Override
    public String getUuid() {
        return diConfigEntity.getUuid();
    }

    @Override
    public String getName() {
        return "数据交换配置：" + diConfigEntity.getName();
    }

    @Override
    public String getType() {
        return IexportType.DataIntegrationConfImpExp;
    }

    @Override
    public Integer getRecVer() {
        return diConfigEntity.getRecVer();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        List<String> fileIds = new ArrayList<String>();
        this.diConfigEntity.setIsEnable(false);//导出去，默认不启动
        return IexportDataResultSetUtils.mongoFileResultInputStream(this,
                this.diConfigEntity,
                fileIds);
    }

    @Override
    public List<IexportData> getDependencies() {
        if (StringUtils.isNotBlank(this.diConfigEntity.getJobUuid())) {
            JobDetailsService jobDetailsService = ApplicationContextHolder.getBean(
                    JobDetailsService.class);
            List<IexportData> list = Lists.newArrayList();
            list.add(new JobDetailsIexportData(
                    jobDetailsService.getOne(this.diConfigEntity.getJobUuid())));
        }
        return Collections.emptyList();
    }
}
