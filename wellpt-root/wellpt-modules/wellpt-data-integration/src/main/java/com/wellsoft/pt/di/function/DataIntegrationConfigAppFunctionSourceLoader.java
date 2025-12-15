package com.wellsoft.pt.di.function;

import com.google.common.collect.Lists;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.pt.app.function.AbstractAppFunctionSourceLoader;
import com.wellsoft.pt.app.function.AppFunctionSource;
import com.wellsoft.pt.app.function.SimpleAppFunctionSource;
import com.wellsoft.pt.app.support.AppFunctionType;
import com.wellsoft.pt.di.entity.DiConfigEntity;
import com.wellsoft.pt.di.service.DiConfigService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 数据交换功能的定义资源
 *
 * @author chenq
 * @date 2018/12/27
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/12/27    chenq		2018/12/27		Create
 * </pre>
 */
@Component
public class DataIntegrationConfigAppFunctionSourceLoader extends AbstractAppFunctionSourceLoader {

    @Autowired
    DiConfigService diConfigService;

    @Override
    public String getAppFunctionType() {
        return AppFunctionType.DataIntegrationConfImpExp;
    }

    @Override
    public List<AppFunctionSource> getAppFunctionSources() {
        List<AppFunctionSource> appFunctionSources = Lists.newArrayList();
        List<DiConfigEntity> entities = diConfigService.listAll();
        if (CollectionUtils.isNotEmpty(entities)) {
            for (DiConfigEntity entity : entities) {
                appFunctionSources.add(this.convert2AppFunctionSource(entity));
            }
        }

        return appFunctionSources;
    }

    @Override
    public <ITEM extends Serializable> AppFunctionSource convert2AppFunctionSource(ITEM item) {
        DiConfigEntity entity = (DiConfigEntity) item;
        String uuid = entity.getUuid();
        String fullName = entity.getName();
        String name = "数据交换定义_" + fullName;
        String id = entity.getId();
        String code = id.hashCode() + StringUtils.EMPTY;
        String category = getAppFunctionType();
        Map<String, Object> extras = new HashMap<String, Object>();
        extras.put(IdEntity.UUID, uuid);
        return new SimpleAppFunctionSource(uuid, fullName, name, id, code, null, null, category,
                true, category, false,
                extras);


    }
}
