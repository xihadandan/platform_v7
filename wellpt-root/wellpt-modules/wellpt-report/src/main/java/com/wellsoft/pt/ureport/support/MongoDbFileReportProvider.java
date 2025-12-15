package com.wellsoft.pt.ureport.support;

import com.bstek.ureport.provider.report.ReportFile;
import com.bstek.ureport.provider.report.ReportProvider;
import com.wellsoft.pt.ureport.service.RpFileRepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;

/**
 * Description: 报表文件内容存储在MongoDB数据库提供器
 *
 * @author chenq
 * @date 2018/9/25
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/9/25    chenq		2018/9/25		Create
 * </pre>
 */
@Component
public class MongoDbFileReportProvider implements ReportProvider {

    private final static String FILE_PREFIX = "mongo:";

    @Autowired
    RpFileRepositoryService rpFileRepositoryService;


    @Override
    public InputStream loadReport(String file) {
        return rpFileRepositoryService.getRpFileInputStream(getRealFileName(file));

    }

    @Override
    public void deleteReport(String file) {
        rpFileRepositoryService.deleteRpFile(getRealFileName(file));


    }

    @Override
    public List<ReportFile> getReportFiles() {
        return rpFileRepositoryService.getAllReportFiles("MongoDB");

    }

    private String getRealFileName(String file) {
        if (file.startsWith(FILE_PREFIX)) {
            return file.substring(FILE_PREFIX.length(), file.length());
        }
        return file;
    }

    @Override
    public void saveReport(String file, String content) {
        rpFileRepositoryService.saveRpFile(getRealFileName(file), content, "MongoDB");
    }

    @Override
    public String getName() {
        return "MongoDB存储库";
    }

    @Override
    public boolean disabled() {
        return false;
    }

    @Override
    public String getPrefix() {
        return FILE_PREFIX;
    }
}
