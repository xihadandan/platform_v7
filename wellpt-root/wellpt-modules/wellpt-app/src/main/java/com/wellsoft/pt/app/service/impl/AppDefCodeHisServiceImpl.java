package com.wellsoft.pt.app.service.impl;

import com.google.common.collect.Lists;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.pt.app.bean.AppDefCodeHisDto;
import com.wellsoft.pt.app.dao.impl.AppDefCodeHisDaoImpl;
import com.wellsoft.pt.app.entity.AppDefCodeHisEntity;
import com.wellsoft.pt.app.service.AppDefCodeHisService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Description:
 *
 * @author chenq
 * @date 2018/12/7
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/12/7    chenq		2018/12/7		Create
 * </pre>
 */
@Service
public class AppDefCodeHisServiceImpl extends
        AbstractJpaServiceImpl<AppDefCodeHisEntity, AppDefCodeHisDaoImpl, String> implements
        AppDefCodeHisService {

    @Override
    public String getScriptContent(String uuid) {
        AppDefCodeHisEntity entity = this.getOne(uuid);
        if (entity != null) {
            try {
                return IOUtils.toString(entity.getScript().getCharacterStream());
            } catch (Exception e) {
                logger.error("获取自定义代码内容异常：", e);
            }
        }
        return null;
    }

    @Override
    public List<AppDefCodeHisDto> listByPage(String relaBusizUuid, String scriptType,
                                             PagingInfo page) {
        List<AppDefCodeHisEntity> entities = this.dao.listByPage(relaBusizUuid, scriptType, page);
        List<AppDefCodeHisDto> dtos = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(entities)) {
            for (AppDefCodeHisEntity entity : entities) {
                AppDefCodeHisDto dto = new AppDefCodeHisDto();
                BeanUtils.copyProperties(entity, dto, "script");
                dtos.add(dto);
            }
        }
        return dtos;
    }
}
