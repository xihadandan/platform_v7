package com.wellsoft.pt.ureport.export;

import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.suport.IexportDataResultSetUtils;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import com.wellsoft.pt.ureport.entity.RpFileRepositoryEntity;

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
public class RpFileRepositoryIexportData extends IexportData {

    RpFileRepositoryEntity rpFileRepositoryEntity;

    public RpFileRepositoryIexportData(
            RpFileRepositoryEntity rpFileRepositoryEntity) {
        this.rpFileRepositoryEntity = rpFileRepositoryEntity;
    }

    @Override
    public String getUuid() {
        return rpFileRepositoryEntity.getUuid();
    }

    @Override
    public String getName() {
        return rpFileRepositoryEntity.getFileName();
    }

    @Override
    public String getType() {
        return IexportType.UreportFileRepository;
    }

    @Override
    public Integer getRecVer() {
        return rpFileRepositoryEntity.getRecVer();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        List<String> fileIds = new ArrayList<String>();
        if (this.rpFileRepositoryEntity.getFileId() != null) {
            fileIds.add(this.rpFileRepositoryEntity.getFileId());
        }
        return IexportDataResultSetUtils.mongoFileResultInputStream(this,
                this.rpFileRepositoryEntity,
                fileIds);
    }

    @Override
    public List<IexportData> getDependencies() {
        return Collections.emptyList();
    }
}
