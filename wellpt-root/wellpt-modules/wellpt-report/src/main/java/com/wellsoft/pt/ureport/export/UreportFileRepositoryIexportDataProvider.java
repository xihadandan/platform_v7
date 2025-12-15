package com.wellsoft.pt.ureport.export;

import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.acceptor.PrintMongoFileSerializable;
import com.wellsoft.pt.basicdata.iexport.service.AbstractIexportDataProvider;
import com.wellsoft.pt.basicdata.iexport.suport.IexportDataRecordSet;
import com.wellsoft.pt.basicdata.iexport.suport.IexportDataResultSetUtils;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import com.wellsoft.pt.basicdata.iexport.suport.TableMetaData;
import com.wellsoft.pt.repository.service.MongoFileService;
import com.wellsoft.pt.ureport.entity.RpFileRepositoryEntity;
import com.wellsoft.pt.ureport.service.RpFileRepositoryService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
@Service
public class UreportFileRepositoryIexportDataProvider extends AbstractIexportDataProvider<RpFileRepositoryEntity, String> {

    static {
        TableMetaData.register(IexportType.UreportFileRepository, "ureport报表",
                RpFileRepositoryEntity.class);
    }

    @Autowired
    RpFileRepositoryService rpFileRepositoryService;

    @Autowired
    MongoFileService mongoFileService;

    @Override
    public String getType() {
        return IexportType.UreportFileRepository;
    }

    @Override
    @Transactional
    public void storeData(IexportData iexportData, boolean newVer) throws Exception {
        Object object = IexportDataResultSetUtils.inputStream2Object(iexportData.getInputStream());
        Map<String, Object> dataMap = (Map<String, Object>) object;
        IexportDataRecordSet printIexportData = (IexportDataRecordSet) dataMap
                .get(IexportDataResultSetUtils.ENTITY_BEAN);
        iexportDataMetaDataService.save(printIexportData);
        List<PrintMongoFileSerializable> printMongoFileSerializables = (List<PrintMongoFileSerializable>) dataMap
                .get(IexportDataResultSetUtils.MONGO_FILES);
        if (printMongoFileSerializables == null || printMongoFileSerializables.isEmpty()) {
            return;
        }
        for (PrintMongoFileSerializable printMongoFileSerializable : printMongoFileSerializables) {
            byte fileArray[] = printMongoFileSerializable.getFileArray();
            RpFileRepositoryEntity entity = rpFileRepositoryService.getDetail(
                    iexportData.getUuid());
            mongoFileService.saveFile(entity.getFileId(),
                    printMongoFileSerializable.getFileName(),
                    new ByteArrayInputStream(fileArray));
        }
    }

    @Override
    public IexportData getData(String uuid) {
        RpFileRepositoryEntity entity = rpFileRepositoryService.getDetail(uuid);
        return new RpFileRepositoryIexportData(entity);
    }


    @Override
    public String getTreeName(RpFileRepositoryEntity rpFileRepositoryEntity) {
        return new RpFileRepositoryIexportData(rpFileRepositoryEntity).getName();
    }

    @Override
    public Set<String> getFileIds(RpFileRepositoryEntity rpFileRepositoryEntity) {
        if (StringUtils.isNotBlank(rpFileRepositoryEntity.getFileId())) {
            Set<String> fileIds = new HashSet<>();
            fileIds.add(rpFileRepositoryEntity.getFileId());
            return fileIds;
        }
        return null;
    }

}
