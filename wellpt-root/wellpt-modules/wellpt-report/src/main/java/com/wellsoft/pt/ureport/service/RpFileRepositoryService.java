package com.wellsoft.pt.ureport.service;

import com.bstek.ureport.provider.report.ReportFile;
import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.ureport.dao.RpFileRepositoryDaoImpl;
import com.wellsoft.pt.ureport.entity.RpFileRepositoryEntity;

import java.io.InputStream;
import java.util.List;

/**
 * Description:
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
public interface RpFileRepositoryService extends
        JpaService<RpFileRepositoryEntity, RpFileRepositoryDaoImpl, String> {

    RpFileRepositoryEntity getByFileName(String fileName);

    InputStream getRpFileInputStream(String fileName);

    void deleteRpFile(String realFileName);

    List<ReportFile> getAllReportFiles(String purpose);

    /**
     * 保存报表文件内容
     *
     * @param file    报表文件名
     * @param content 报表内容字符串
     * @param purpose 目的地，可保存到MongoDB
     */
    void saveRpFile(String file, String content, String purpose);

    void deleteRps(List<String> uuids);

    RpFileRepositoryEntity getDetail(String uuid);

    List<RpFileRepositoryEntity> listLikeFileName(String fileName);
}
