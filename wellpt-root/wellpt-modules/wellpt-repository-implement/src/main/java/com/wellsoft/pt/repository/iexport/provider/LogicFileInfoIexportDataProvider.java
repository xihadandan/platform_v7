package com.wellsoft.pt.repository.iexport.provider;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.service.AbstractIexportDataProvider;
import com.wellsoft.pt.basicdata.iexport.suport.IExportEntityStream;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import com.wellsoft.pt.repository.entity.LogicFileInfo;
import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;
import com.wellsoft.pt.repository.service.MongoFileService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
 * 2023年11月09日   chenq	 Create
 * </pre>
 */
@Service
@Transactional(readOnly = true)
public class LogicFileInfoIexportDataProvider extends AbstractIexportDataProvider<LogicFileInfo, String> {

    @Autowired
    MongoFileService mongoFileService;

    @Override
    public String getType() {
        return IexportType.LogicFileInfo;
    }

    @Override
    public IexportData getData(String s) {
        return null;
    }

    @Override
    public String getTreeName(LogicFileInfo logicFileInfo) {
        return "附件: " + logicFileInfo.getFileName();
    }

    @Override
    public TreeNode treeNode(String uuid) {
        TreeNode node = new TreeNode();
        LogicFileInfo logicFileInfo = mongoFileService.getLogicFileInfo(uuid);
        if (logicFileInfo == null) {
            return null;
        }
        node.setId(logicFileInfo.getUuid());
        node.setName("附件: " + logicFileInfo.getFileName());
        node.setType(IexportType.LogicFileInfo);
        return node;
    }

    @Override
    public IExportEntityStream exportStream(String uuid) {
        IExportEntityStream stream = super.exportStream(uuid);
        LogicFileInfo fileInfo = dao.get(LogicFileInfo.class, uuid);
        stream.setName("附件信息:" + fileInfo.getFileName().replaceAll("\\.", "_"));
        if (stream != null) {
            MongoFileEntity fileEntity = mongoFileService.getFile(uuid);
            stream.getFiles().add(new IExportEntityStream.File(fileEntity.getFileName(), fileEntity.getInputstream()));
        }
        return stream;
    }

    @Override
    @Transactional
    public LogicFileInfo saveEntityStream(IExportEntityStream stream) {
        LogicFileInfo fileInfo = super.saveEntityStream(stream);
        // 上传文件
        List<IExportEntityStream.File> files = stream.getFiles();
        if (CollectionUtils.isNotEmpty(files) && files.get(0).getInput() != null) {
            mongoFileService.saveFile(fileInfo.getFileID(), files.get(0).getName(), files.get(0).getInput());
            IOUtils.closeQuietly(files.get(0).getInput());
        }
        return fileInfo;
    }


}
