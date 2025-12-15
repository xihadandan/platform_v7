package com.wellsoft.pt.timer.iexport.acceptor;

import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.suport.IexportDataResultSetUtils;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import com.wellsoft.pt.timer.entity.TsTimerConfigEntity;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Description: 流程计时器配置
 *
 * @author liuyz
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2023-05-20	liuyz		2023-05-20	    Create
 * </pre>
 * @date 2023-05-20
 */
public class TsTimerConfigIexportData extends IexportData {

    private TsTimerConfigEntity tsTimerConfigEntity;

    public TsTimerConfigIexportData(TsTimerConfigEntity tsTimerConfigEntity) {
        this.tsTimerConfigEntity = tsTimerConfigEntity;
    }

    @Override
    public String getUuid() {
        return tsTimerConfigEntity.getUuid();
    }

    @Override
    public String getName() {
        String name = tsTimerConfigEntity.getName();
        if (name.contains(Separator.UNDERLINE.getValue())) {
            return "流程计时器配置：" + tsTimerConfigEntity.getName().split(Separator.UNDERLINE.getValue())[1];
        }
        return "流程计时器配置：" + tsTimerConfigEntity.getName();
    }

    @Override
    public String getType() {
        return IexportType.TsTimerConfig;
    }

    @Override
    public Integer getRecVer() {
        return tsTimerConfigEntity.getRecVer();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return IexportDataResultSetUtils.iexportDataResultSet2InputStream(this, tsTimerConfigEntity);
    }

    @Override
    public List<IexportData> getDependencies() {
        List<IexportData> dependencies = new ArrayList<IexportData>();
        return dependencies;
    }
}
