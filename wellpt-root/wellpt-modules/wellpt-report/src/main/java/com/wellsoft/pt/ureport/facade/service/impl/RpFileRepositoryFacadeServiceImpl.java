package com.wellsoft.pt.ureport.facade.service.impl;

import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.pt.ureport.dto.RpFileRepositoryDto;
import com.wellsoft.pt.ureport.entity.RpFileRepositoryEntity;
import com.wellsoft.pt.ureport.facade.service.RpFileRepositoryFacadeService;
import com.wellsoft.pt.ureport.service.RpFileRepositoryService;
import com.wellsoft.pt.ureport.support.MongoDbFileReportProvider;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/5/10
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/5/10    chenq		2019/5/10		Create
 * </pre>
 */
@Service
public class RpFileRepositoryFacadeServiceImpl extends AbstractApiFacade implements RpFileRepositoryFacadeService {

    @Resource
    RpFileRepositoryService rpFileRepositoryService;

    @Resource
    MongoDbFileReportProvider mongoDbFileReportProvider;

    @Override
    public Select2QueryData loadSelectData(Select2QueryInfo queryInfo) {
        String fileName = queryInfo.getOtherParams("fileName");
        List<RpFileRepositoryEntity> rpFileRepositoryEntityList = rpFileRepositoryService.listLikeFileName(fileName);
        return new Select2QueryData(rpFileRepositoryEntityList, "uuid", "fileName");
    }

    @Override
    @Transactional(readOnly = true)
    public RpFileRepositoryDto getRpFileDetail(String uuid) {
        RpFileRepositoryEntity entity = rpFileRepositoryService.getOne(uuid);
        RpFileRepositoryDto dto = new RpFileRepositoryDto();
        BeanUtils.copyProperties(entity, dto, "content");
        if (StringUtils.isNotBlank(dto.getFileId())) {
            //从MongoDB读取配置详情
            try {
                dto.setContent(IOUtils.toString(mongoDbFileReportProvider.loadReport(dto.getFileName()), Charsets.UTF_8));
            } catch (Exception e) {
                logger.error("从MongoDB服务读取ureport的报表xml配置详情异常：", e);
            }
        } else {
            try {
                dto.setContent(IOUtils.toString(entity.getContent().getCharacterStream()));
            } catch (Exception e) {
                logger.error("读取ureport的报表xml大字段配置详情异常：", e);
            }

        }
        return dto;
    }
}
