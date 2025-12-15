package com.wellsoft.pt.repository.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.repository.dao.KingGridFileDao;
import com.wellsoft.pt.repository.entity.KingGridFileEntity;
import com.wellsoft.pt.repository.vo.KingGridParamVo;

import java.io.File;
import java.io.InputStream;

public interface KingGridFileService extends JpaService<KingGridFileEntity, KingGridFileDao, String> {

    KingGridFileEntity saveFile(String name, File srcFile, String convertType, File targetDirectory, KingGridParamVo paramVo);

    /**
     * 文件转换
     *
     * @param srcFile            :转换文件
     * @param businessId：金格文件ID,
     * @param businessName:      金格文件名称,
     * @return
     * @author baozh
     * @date 2021/12/2 11:11
     * @Param convertType :转换类型
     */
    InputStream convertFile(File srcFile, InputStream out, String convertType, String businessId, String businessName, KingGridParamVo paramVo);

    /**
     * 文件预览
     *
     * @param
     * @return
     * @author baozh
     * @date 2021/12/2 11:11
     */
    String previewFile(File srcFile, KingGridParamVo paramVo);
}
