package com.wellsoft.pt.app.facade.service.impl;

import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.pt.app.bean.AppDefCodeHisDto;
import com.wellsoft.pt.app.entity.AppDefCodeHisEntity;
import com.wellsoft.pt.app.facade.service.AppDefCodeHisFacadeService;
import com.wellsoft.pt.app.service.AppDefCodeHisService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.rowset.serial.SerialClob;
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
public class AppDefCodeHisFacadeServiceImpl extends AbstractApiFacade implements
        AppDefCodeHisFacadeService {

    @Autowired
    AppDefCodeHisService appDefCodeHisService;


    @Override
    public void addCodeHis(AppDefCodeHisDto codeDto) {
        AppDefCodeHisEntity entity = new AppDefCodeHisEntity();
        BeanUtils.copyProperties(codeDto, entity, "script");
        entity.setAuthor(SpringSecurityUtils.getCurrentUserName());
        try {
            if (StringUtils.isNotBlank(codeDto.getScript()))
                entity.setScript(new SerialClob(
                        IOUtils.toCharArray(IOUtils.toInputStream(codeDto.getScript()))));

        } catch (Exception e) {
            throw new RuntimeException("保存代码内容异常：", e);
        }

        appDefCodeHisService.save(entity);

    }

    @Override
    public String getScriptContent(String uuid) {
        return appDefCodeHisService.getScriptContent(uuid);
    }

    @Override
    public List<AppDefCodeHisDto> listByPage(String relaBusizUuid, String scriptType,
                                             PagingInfo page) {

        return appDefCodeHisService.listByPage(relaBusizUuid, scriptType, page);
    }
}
