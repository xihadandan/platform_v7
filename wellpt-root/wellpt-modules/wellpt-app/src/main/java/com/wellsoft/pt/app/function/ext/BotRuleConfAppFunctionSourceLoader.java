package com.wellsoft.pt.app.function.ext;

import com.google.common.collect.Lists;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.pt.app.function.AbstractAppFunctionSourceLoader;
import com.wellsoft.pt.app.function.AppFunctionSource;
import com.wellsoft.pt.app.function.SimpleAppFunctionSource;
import com.wellsoft.pt.app.support.AppFunctionType;
import com.wellsoft.pt.bot.entity.BotRuleConfEntity;
import com.wellsoft.pt.bot.facade.service.BotRuleConfFacadeService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 单据转换功能的定义资源
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
public class BotRuleConfAppFunctionSourceLoader extends AbstractAppFunctionSourceLoader {

    @Autowired
    BotRuleConfFacadeService botRuleConfFacadeService;

    @Override
    public String getAppFunctionType() {
        return AppFunctionType.BotRuleConf;
    }

    @Override
    public List<AppFunctionSource> getAppFunctionSources() {
        List<AppFunctionSource> appFunctionSources = Lists.newArrayList();
        List<BotRuleConfEntity> entities = botRuleConfFacadeService.listAll();
        if (CollectionUtils.isNotEmpty(entities)) {
            for (BotRuleConfEntity entity : entities) {
                appFunctionSources.add(this.convert2AppFunctionSource(entity));
            }
        }

        return appFunctionSources;
    }

    @Override
    public <ITEM extends Serializable> AppFunctionSource convert2AppFunctionSource(ITEM item) {
        BotRuleConfEntity entity = (BotRuleConfEntity) item;
        String uuid = entity.getUuid();
        String fullName = entity.getRuleName();
        String name = "单据转换规则定义_" + fullName;
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
